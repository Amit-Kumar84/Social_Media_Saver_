package com.rajput.socialmediasaver.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.databinding.ActivityAboutUsBinding
import com.rajput.socialmediasaver.util.Utils.Companion.PrivacyPolicyUrl
import java.util.Locale

/**
 * AboutUsActivity class handles the "About Us" screen of the application.
 * It provides options to view the privacy policy, visit the website, and send an email.
 */
class AboutUsActivity : AppCompatActivity() {
    private var binding: ActivityAboutUsBinding? = null

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us)

        // Set click listener for the privacy policy section
        binding?.RLPrivacyPolicy?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@AboutUsActivity, WebviewAcitivity::class.java)
            i.putExtra("URL", PrivacyPolicyUrl)
            i.putExtra("Title", resources.getString(R.string.prv_policy))
            startActivity(i)
        })

        // Set click listener for the back button
        binding?.imBack?.setOnClickListener(View.OnClickListener { onBackPressed() })

        // Set click listener for the website section
        binding?.RLWebsite?.setOnClickListener(View.OnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.website_tag)))
                startActivity(browserIntent)
            } catch (ex: Exception) {
                Log.d("WebsiteTag", "onClick: $ex")
            }
        })

        // Set click listener for the email section
        binding?.RLEmail?.setOnClickListener(View.OnClickListener {
            val email = this@AboutUsActivity.resources.getString(R.string.email_tag)
            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf(email)
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)
            intent.setType("text/html")
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))
        })
    }

    /**
     * Set the locale for the application.
     * @param lang The language code to set the locale to.
     */
    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }
}