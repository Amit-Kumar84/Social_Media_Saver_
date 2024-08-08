package com.rajput.socialmediasaver.activity

import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.rajput.socialmediasaver.R

/**
 * VideoPlayerActivity class handles the video player screen of the application.
 * It plays a video in full screen mode using a VideoView.
 */
class VideoPlayerActivity : AppCompatActivity() {
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_video_player)

        // Initialize the VideoView and get the video path from the intent
        val videoView = findViewById<VideoView>(R.id.videoView)
        val intent = intent
        val videoPath = intent.getStringExtra("PathVideo")

        try {
            // Set up the MediaController and play the video
            val mediaController = MediaController(this@VideoPlayerActivity)
            mediaController.setAnchorView(videoView)
            val video = Uri.parse(videoPath)
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(video)
            videoView.start()

            // Set listeners for video preparation and completion
            videoView.setOnPreparedListener { }
            videoView.setOnCompletionListener { finish() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}