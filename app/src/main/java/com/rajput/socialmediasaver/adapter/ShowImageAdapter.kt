package com.rajput.socialmediasaver.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.activity.FullViewActivity
import com.rajput.socialmediasaver.activity.VideoPlayerActivity
import com.rajput.socialmediasaver.util.Utils


import java.io.File


class ShowImagesAdapter(
    private val context: Context,
    private val imageList: ArrayList<File>,
    fullViewActivity: FullViewActivity
) :
    PagerAdapter() {
    private val inflater: LayoutInflater
    private var fullViewActivity: FullViewActivity

    init {
        this.fullViewActivity = fullViewActivity
        inflater = LayoutInflater.from(context)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout: View =
            inflater.inflate(R.layout.slidingimages_layout, view, false)!!
        val imageView = imageLayout.findViewById<ImageView>(R.id.im_fullViewImage)
        val im_vpPlay = imageLayout.findViewById<ImageView>(R.id.im_vpPlay)
        val im_share = imageLayout.findViewById<ImageView>(R.id.im_share)
        val im_delete = imageLayout.findViewById<ImageView>(R.id.im_delete)
        Glide.with(context).load(imageList[position].path).into(imageView)
        view.addView(imageLayout, 0)
        val extension =
            imageList[position].name.substring(imageList[position].name.lastIndexOf("."))
        if (extension == ".mp4") {
            im_vpPlay.visibility = View.VISIBLE
        } else {
            im_vpPlay.visibility = View.GONE
        }
        im_vpPlay.setOnClickListener {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse(imageList.get(position).getPath()), "video/*");
//            context.startActivity(intent);
            val intent =
                Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("PathVideo", imageList[position].path)
            context.startActivity(intent)
        }
        im_delete.setOnClickListener {
            val b = imageList[position].delete()
            if (b) {
                fullViewActivity.deleteFileAA(position)
            }
        }
        im_share.setOnClickListener {
            val extension = imageList[position].name.substring(
                imageList[position].name.lastIndexOf(".")
            )
            if (extension == ".mp4") {
                shareVideo(context, imageList[position].path)
            } else {
                shareImage(context, imageList[position].path)
            }
        }
        return imageLayout
    }

    fun shareImage(context: Context, filePath: String?) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, context.resources.getString(R.string.share_txt))
            val path =
                MediaStore.Images.Media.insertImage(context.contentResolver, filePath, "", null)
            val screenshotUri = Uri.parse(path)
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
            intent.setType("image/*")
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.resources.getString(R.string.share_image_via)
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    fun shareVideo(context: Context, filePath: String?) {
        val mainUri = Uri.parse(filePath)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.setType("video/mp4")
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.no_app_installed),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }
}