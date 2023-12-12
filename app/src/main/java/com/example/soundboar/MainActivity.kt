package com.example.soundboar

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var addSoundButton: Button
    private lateinit var layout: LinearLayout
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val REQUEST_CODE_PICK_AUDIO = 1
        private const val MY_PERMISSIONS_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.Llayout)
        sharedPrefs = getSharedPreferences("SoundboardPrefs", MODE_PRIVATE)

        val buttonSound1 = findViewById<Button>(R.id.button1)
        val buttonSound2 = findViewById<Button>(R.id.button2)
        val buttonSound3 = findViewById<Button>(R.id.button3)
        val buttonSound4 = findViewById<Button>(R.id.button4)

        buttonSound1.setOnClickListener { playSound(R.raw.sound1) }
        buttonSound2.setOnClickListener { playSound(R.raw.sound2) }
       // buttonSound3.setOnClickListener { playSound(R.raw.sound3) }
        //buttonSound4.setOnClickListener { playSound(R.raw.sound4) }

        addSoundButton = findViewById(R.id.addSoundButton)
        addSoundButton.setOnClickListener {
            requestAudioPermissions()
        }

        loadSavedAudioUris()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                MY_PERMISSIONS_REQUEST)
        } else {
            openAudioPicker()
        }
    }

    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO)
    }

    private fun playSound(soundResourceId: Int) {
        stopAndReleaseMediaPlayer()

        mediaPlayer = MediaPlayer.create(this, soundResourceId)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            stopAndReleaseMediaPlayer()
        }
    }

    private fun createSoundButton(audioPath: String) {
        val newButton = Button(this).apply {
            text = File(audioPath).name
            setOnClickListener {
                stopAndReleaseMediaPlayer()

                mediaPlayer = MediaPlayer.create(context, Uri.parse(audioPath))
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener {
                    stopAndReleaseMediaPlayer()
                }
            }
        }

        layout.addView(newButton)
    }

    private fun saveAudioUri(uri: Uri) {
        val audioPath = copyFileToInternalStorage(uri)
        audioPath?.let {
            val editor = sharedPrefs.edit()
            editor.putString(it, it)
            editor.apply()

            createSoundButton(it)
        }
    }

    private fun copyFileToInternalStorage(uri: Uri): String? {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = File(uri.path ?: "").name
        val outputFile = File(filesDir, fileName)
        val outputStream = FileOutputStream(outputFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return outputFile.absolutePath
    }

    private fun loadSavedAudioUris() {
        val allUris = sharedPrefs.all
        for (entry in allUris.entries) {
            createSoundButton(entry.value.toString())
        }
    }

    private fun stopAndReleaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                saveAudioUri(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openAudioPicker()
            } else {
                Toast.makeText(this, "CHOCKBAR les permission audio sont refusée ???", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseMediaPlayer()
    }
}
