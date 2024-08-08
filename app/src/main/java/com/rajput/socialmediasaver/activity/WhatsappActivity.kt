package com.rajput.socialmediasaver.activity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.databinding.ActivityWhatsappBinding
import com.rajput.socialmediasaver.databinding.DialogWhatsappPermissionBinding
import com.rajput.socialmediasaver.fragment.WhatsappImageFragment
import com.rajput.socialmediasaver.fragment.WhatsappQImageFragment
import com.rajput.socialmediasaver.fragment.WhatsappQVideoFragment
import com.rajput.socialmediasaver.fragment.WhatsappVideoFragment
import com.rajput.socialmediasaver.util.Utils
import com.rajput.socialmediasaver.util.Utils.Companion.createFileFolder
import java.io.File
import java.util.*

class WhatsappActivity : AppCompatActivity() {
    private var binding: ActivityWhatsappBinding? = null
    private var activity: WhatsappActivity? = null
    private var allfiles: Array<File>? = null
    private var fileArrayList: ArrayList<Uri>? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_whatsapp)
        activity = this
        createFileFolder()
        initViews()
    }

    override fun onResume() {
        super.onResume()
        activity = this
    }

    private fun initViews() {
        binding!!.imBack.setOnClickListener { onBackPressed() }

        binding!!.LLOpenWhatsapp.setOnClickListener {
            Utils.OpenApp(activity!!, "com.whatsapp")
        }
        fileArrayList = ArrayList()
        initProgress()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (contentResolver.persistedUriPermissions.size > 0) {
                progressDialog!!.show()
                LoadAllFiles().execute()
                binding!!.tvAllowAccess.visibility = View.GONE
            } else {
                binding!!.tvAllowAccess.visibility = View.VISIBLE
            }
        } else {
            setupViewPager(binding!!.viewpager)
            binding!!.tabs.setupWithViewPager(binding!!.viewpager)

            for (i in 0 until binding!!.tabs.tabCount) {
                val tv = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
                binding!!.tabs.getTabAt(i)!!.customView = tv
            }
        }
        binding!!.tvAllowAccess.setOnClickListener {
            val dialog = Dialog(activity!!, R.style.SheetDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWhatsappPermissionBinding: DialogWhatsappPermissionBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.dialog_whatsapp_permission,
                null,
                false
            )
            dialog.setContentView(dialogWhatsappPermissionBinding.root)
            dialogWhatsappPermissionBinding.tvAllow.setOnClickListener {
                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        val sm = activity!!.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
                        val startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
                        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
                        var scheme = uri.toString()
                        scheme = scheme.replace("/root/", "/document/")
                        scheme += "%3A$startDir"
                        uri = Uri.parse(scheme)
                        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
                        startActivityForResult(intent, 2001)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun initProgress() {
        progressDialog = ProgressDialog(activity, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setTitle("Loading")
        progressDialog!!.setMessage("Loading Status. Please wait...")
        progressDialog!!.isIndeterminate = true
        progressDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        adapter.addFragment(WhatsappImageFragment(), resources.getString(R.string.images))
        adapter.addFragment(WhatsappVideoFragment(), resources.getString(R.string.videos))
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
    }

    inner class ViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 2001 && resultCode == RESULT_OK) {
                val dataUri = data?.data
                if (dataUri.toString().contains(".Statuses")) {
                    contentResolver.takePersistableUriPermission(
                        dataUri!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    progressDialog!!.show()
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        LoadAllFiles().execute()
                    }
                } else {
                    Utils.infoDialog(activity!!, resources.getString(R.string.wrong_folder), resources.getString(R.string.selected_wrong_folder))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    inner class LoadAllFiles : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg furl: String?): String? {
            val documentFile = DocumentFile.fromTreeUri(activity!!, contentResolver.persistedUriPermissions[0].uri)
            documentFile?.listFiles()?.forEach { file ->
                if (!file.isDirectory && file.name != ".nomedia") {
                    fileArrayList?.add(file.uri)
                }
            }
            return null
        }

        override fun onProgressUpdate(vararg progress: String?) {}

        override fun onPostExecute(fileUrl: String?) {
            progressDialog!!.dismiss()
            val adapter = ViewPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
            adapter.addFragment(WhatsappQImageFragment(fileArrayList!!), resources.getString(R.string.images))
            adapter.addFragment(WhatsappQVideoFragment(fileArrayList!!), resources.getString(R.string.videos))
            binding!!.viewpager.adapter = adapter
            binding!!.viewpager.offscreenPageLimit = 1
            binding!!.tabs.setupWithViewPager(binding!!.viewpager)
            binding!!.tvAllowAccess.visibility = View.GONE
            for (i in 0 until binding!!.tabs.tabCount) {
                val tv = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
                binding!!.tabs.getTabAt(i)!!.customView = tv
            }
        }

        override fun onCancelled() {
            super.onCancelled()
            progressDialog!!.dismiss()
        }
    }
}