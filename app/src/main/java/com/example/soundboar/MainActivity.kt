package com.example.soundboar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var addSoundButton: Button
    private lateinit var layout: LinearLayout // Remplacez avec l'ID de votre LinearLayout
    companion object {
        private const val REQUEST_CODE_PICK_AUDIO = 1
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //layout = findViewById(R.id.activity_main) // Remplacez avec l'ID de votre LinearLayout

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            } else {
                openAudioPicker()
            }
        }
    }

    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO)
    }

    private fun playSound(soundResourceId: Int) {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        mediaPlayer = MediaPlayer.create(this, soundResourceId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }

    private fun createSoundButton(soundUri: Uri) {
        val newButton = Button(this).apply {
            text = soundUri.lastPathSegment // Vous pouvez personnaliser le texte du bouton ici
            setOnClickListener {
                if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }

                mediaPlayer = MediaPlayer.create(context, soundUri)
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    it.release()
                }
            }
        }

        layout.addView(newButton)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                createSoundButton(uri)
            }
        }
    }

    override fun superonRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAudioPicker()
                } else {
                    // Handle the case where the user denies the permission.
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
