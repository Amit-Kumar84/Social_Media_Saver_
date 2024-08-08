package com.rajput.socialmediasaver.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.databinding.ActivityMainBinding
import com.rajput.socialmediasaver.util.Utils

/**
 * MainActivity class handles the main screen of the application.
 * It manages user interactions, clipboard monitoring, and navigation to other activities.
 */
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var activity: MainActivity
    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false
    private lateinit var clipBoard: ClipboardManager
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activity = this
        initViews()
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    override fun onResume() {
        super.onResume()
        activity = this
        clipBoard = activity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    /**
     * Initialize the views and set up event listeners.
     */
    private fun initViews() {
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions(0)
        }
        binding.rvWhatsApp.setOnClickListener(this)
        binding.rvGallery.setOnClickListener(this)
        binding.rvAbout.setOnClickListener(this)
        binding.rvShareApp.setOnClickListener(this)
        binding.rvRateApp.setOnClickListener(this)
        binding.rvMoreApp.setOnClickListener(this)
        Utils.createFileFolder()
    }

    /**
     * Handle click events for the views.
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.rvWhatsApp -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(102)
                } else {
                    callWhatsappActivity()
                }
            }
            R.id.rvGallery -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(103)
                } else {
                    callGalleryActivity()
                }
            }
            R.id.rvAbout -> {
                val i = Intent(activity, AboutUsActivity::class.java)
                startActivity(i)
            }
            R.id.rvShareApp -> ShareApp(activity)
            R.id.rvRateApp -> RateApp(activity)
            R.id.rvMoreApp -> MoreApp(activity)
        }
    }



    /**
     * Start the Whatsapp activity.
     */
    private fun callWhatsappActivity() {
        val i = Intent(activity, WhatsappActivity::class.java)
        startActivity(i)
    }

    /**
     * Start the Gallery activity.
     */
    private fun callGalleryActivity() {
        val i = Intent(activity, GalleryActivity::class.java)
        startActivity(i)
    }

    /**
     * Check and request necessary permissions.
     * @param type The type of permission request.
     * @return True if permissions are already granted, false otherwise.
     */
    private fun checkPermissions(type: Int): Boolean {
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                listPermissionsNeeded.toTypedArray(), type
            )
            return false
        } else {
            when (type) {
                102 -> callWhatsappActivity()
                103 -> callGalleryActivity()
            }
        }
        return true
    }

    /**
     * Handle the result of permission requests.
     * @param requestCode The request code passed in requestPermissions(android.app.Activity, String[], int).
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            102 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callWhatsappActivity()
                }
            }
            103 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callGalleryActivity()
                }
            }
        }
    }

    /**
     * Handle the back button press.
     * If pressed twice within 2 seconds, the app will exit.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (doubleBackToExitPressedOnce) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        this.doubleBackToExitPressedOnce = true
        Utils.setToast(activity, resources.getString(R.string.pls_bck_again))
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    /**
     * Open the developer's page on the Google Play Store.
     * @param context The context from which the method is called.
     */
    fun MoreApp(context: Context) {
        val appName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://play.google.com/store/apps/dev?id=5921942607990954343"
                    )
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://play.google.com/store/apps/dev?id=5921942607990954343"
                    )
                )
            )
        }
    }

    /**
     * Share the app with others.
     * @param context The context from which the method is called.
     */
    fun ShareApp(context: Context) {
        val appLink = """

                 https://play.google.com/store/apps/details?id=${context.packageName}
                 """.trimIndent()
        val sendInt = Intent(Intent.ACTION_SEND)
        sendInt.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        sendInt.putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.share_app_message) + appLink
        )
        sendInt.setType("text/plain")
        context.startActivity(Intent.createChooser(sendInt, "Share"))
    }

    /**
     * Rate the app on the Google Play Store.
     * @param context The context from which the method is called.
     */
    fun RateApp(context: Context) {
        val appName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "market://details?id=com.rajputdev.mediasaver"
                    )
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.rajputdev.mediasaver"
                    )
                )
            )
        }
    }
}