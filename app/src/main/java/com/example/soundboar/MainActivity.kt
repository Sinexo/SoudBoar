package com.example.soundboar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.soundboar.ui.theme.SoundBoarTheme
import android.media.MediaPlayer
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSound1 = findViewById<Button>(R.id.soundButton1)
        val buttonSound2 = findViewById<Button>(R.id.soundButton2)
        val buttonSound3 = findViewById<Button>(R.id.soundButton3)
        val buttonSound4 = findViewById<Button>(R.id.soundButton4)

        buttonSound1.setOnClickListener { playSound(R.raw.sound1) }
        buttonSound2.setOnClickListener { playSound(R.raw.sound2) }
        buttonSound3.setOnClickListener { playSound(R.raw.sound3) }
        buttonSound4.setOnClickListener { playSound(R.raw.sound4) }
    }

    private fun playSound(soundResourceId: Int) {
        // Release any resources from previous MediaPlayer
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        mediaPlayer = MediaPlayer.create(this, soundResourceId)
        mediaPlayer.start()

        // Release resources once playback is complete
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
