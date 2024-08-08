package com.rajput.socialmediasaver.activity

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.databinding.ActivityWebviewAcitivityBinding
import java.util.Locale

/**
 * WebviewAcitivity class handles the web view screen of the application.
 * It loads a web page and provides options to refresh the page and navigate back.
 */
class WebviewAcitivity : AppCompatActivity() {
    var binding: ActivityWebviewAcitivityBinding? = null
    var IntentURL: String? = null
    var IntentTitle: String? = ""

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview_acitivity)
        IntentURL = intent.getStringExtra("URL")
        IntentTitle = intent.getStringExtra("Title")
        binding?.imBack?.setOnClickListener(View.OnClickListener { onBackPressed() })
        binding?.TVTitle?.text = IntentTitle
        binding?.swipeRefreshLayout?.post(Runnable {
            binding?.swipeRefreshLayout!!.isRefreshing = true
            LoadPage(IntentURL)
        })
        binding?.swipeRefreshLayout?.setOnRefreshListener(OnRefreshListener { LoadPage(IntentURL) })
    }

    /**
     * Load the specified URL in the WebView.
     * @param Url The URL to load.
     */
    fun LoadPage(Url: String?) {
        binding?.webView1?.webViewClient = MyBrowser()
        binding?.webView1?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                binding?.swipeRefreshLayout?.isRefreshing = progress != 100
            }
        }
        binding?.webView1?.settings?.loadsImagesAutomatically = true
        binding?.webView1?.settings?.javaScriptEnabled = true
        binding?.webView1?.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        if (Url != null) {
            binding?.webView1?.loadUrl(Url)
        }
    }

    /**
     * Custom WebViewClient to handle URL loading within the WebView.
     */
    private inner class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
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