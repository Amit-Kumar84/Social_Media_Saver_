package com.rajput.socialmediasaver.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.rajput.socialmediasaver.R

/**
 * SplashScreen class handles the splash screen of the application.
 * It checks for app updates and navigates to the main screen.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var activity: SplashScreen
    private lateinit var context: Context
    private lateinit var appUpdateManager: AppUpdateManager

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        context = this
        activity = this
        appUpdateManager = AppUpdateManagerFactory.create(context)
        updateApp()
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        activity = this
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, AppUpdateType.IMMEDIATE, activity, 101
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Navigate to the home screen after a delay.
     */
    @Suppress("DEPRECATION")
    private fun homeScreen() {
        Handler().postDelayed({
            val i = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(i)
        }, 1000)
    }

    /**
     * Check for app updates and start the update flow if an update is available.
     * If no update is available, navigate to the home screen.
     */
    @Suppress("DEPRECATION")
    private fun updateApp() {
        try {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE, activity, 101
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                } else {
                    homeScreen()
                }
            }.addOnFailureListener {
                it.printStackTrace()
                homeScreen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Handle the result of the update flow.
     * @param requestCode The request code passed in startActivityForResult(Intent, int).
     * @param resultCode The result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            homeScreen()
        }
    }
}