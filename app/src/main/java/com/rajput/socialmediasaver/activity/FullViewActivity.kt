@file:Suppress("DEPRECATION")

package com.rajput.socialmediasaver.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.adapter.ShowImagesAdapter
import com.rajput.socialmediasaver.databinding.ActivityFullViewBinding
import com.rajput.socialmediasaver.util.Utils

import java.io.File

/**
 * FullViewActivity class handles the full view of images and videos.
 * It provides options to delete, share, and view images/videos in a ViewPager.
 */
class FullViewActivity : AppCompatActivity() {
    private var binding: ActivityFullViewBinding? = null
    private var activity: FullViewActivity? = null
    private var fileArrayList: ArrayList<File>? = null
    private var Position = 0
    private var showImagesAdapter: ShowImagesAdapter? = null

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_view)
        activity = this
        val extras = intent.extras
        if (extras != null) {
            fileArrayList = intent.getSerializableExtra("ImageDataFile") as ArrayList<File>?
            Position = intent.getIntExtra("Position", 0)
        }
        initViews()
    }

    /**
     * Initialize the views and set up event listeners.
     */
    private fun initViews() {
        showImagesAdapter = fileArrayList?.let {
            ShowImagesAdapter(this, it, this@FullViewActivity)
        }
        binding?.vpView?.adapter = showImagesAdapter
        binding?.vpView?.currentItem = Position
        binding?.vpView?.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(arg0: Int) {
                Position = arg0
                println("Current position==$Position")
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(num: Int) {}
        })
        binding?.imDelete?.setOnClickListener {
            val ab = AlertDialog.Builder(activity!!)
            ab.setPositiveButton(resources.getString(R.string.yes)) { dialog, id ->
                val b = fileArrayList!![Position].delete()
                if (b) {
                    deleteFileAA(Position)
                }
            }
            ab.setNegativeButton(resources.getString(R.string.no)) { dialog, id -> dialog.cancel() }
            val alert = ab.create()
            alert.setTitle(resources.getString(R.string.do_u_want_to_dlt))
            alert.show()
        }
        binding?.imShare?.setOnClickListener {
            if (fileArrayList!![Position].name.contains(".mp4")) {
                Log.d("SSSSS", "onClick: " + fileArrayList!![Position])
                activity?.let { it1 -> shareVideo(it1, fileArrayList!![Position].path) }
            } else {
                activity?.let { it1 -> shareImage(it1, fileArrayList!![Position].path) }
            }
        }
        binding?.imWhatsappShare?.setOnClickListener {
            activity?.let { it1 ->
                shareImageVideoOnWhatsapp(
                    it1,
                    fileArrayList!![Position].path,
                    fileArrayList!![Position].name.contains(".mp4")
                )
            }
        }
        binding?.imClose?.setOnClickListener { onBackPressed() }
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    override fun onResume() {
        super.onResume()
        activity = this
    }

    /**
     * Delete the file at the specified position and update the adapter.
     * @param position The position of the file to delete.
     */
    fun deleteFileAA(position: Int) {
        fileArrayList!!.removeAt(position)
        showImagesAdapter?.notifyDataSetChanged()
        Utils.setToast(activity, resources.getString(R.string.file_deleted))
        if (fileArrayList!!.size == 0) {
            onBackPressed()
        }
    }

    /**
     * Handle the selection of menu items.
     * @param item The selected menu item.
     * @return True if the menu item is handled, false otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Share an image using an external application.
     * @param context The context from which the method is called.
     * @param filePath The path of the image file to share.
     */
    fun shareImage(context: Context, filePath: String?) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, context.resources.getString(R.string.share_txt))
            val path = MediaStore.Images.Media.insertImage(context.contentResolver, filePath, "", null)
            val screenshotUri = Uri.parse(path)
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
            intent.setType("image/*")
            context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.share_image_via)))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * Share an image or video on WhatsApp.
     * @param context The context from which the method is called.
     * @param filePath The path of the file to share.
     * @param isVideo True if the file is a video, false if it is an image.
     */
    fun shareImageVideoOnWhatsapp(context: Context, filePath: String?, isVideo: Boolean) {
        val imageUri = Uri.parse(filePath)
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.setPackage("com.whatsapp")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "")
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        if (isVideo) {
            shareIntent.setType("video/*")
        } else {
            shareIntent.setType("image/*")
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Utils.setToast(context, context.resources.getString(R.string.whatsapp_not_installed))
        }
    }

    /**
     * Share a video using an external application.
     * @param context The context from which the method is called.
     * @param filePath The path of the video file to share.
     */
    fun shareVideo(context: Context, filePath: String?) {
        val mainUri = Uri.parse(filePath)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.setType("video/mp4")
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, context.resources.getString(R.string.no_app_installed), Toast.LENGTH_LONG).show()
        }
    }
}