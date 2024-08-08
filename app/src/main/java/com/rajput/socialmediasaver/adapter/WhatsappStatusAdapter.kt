@file:Suppress("DEPRECATION")

package com.rajput.socialmediasaver.adapter

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.activity.VideoPlayerActivity
import com.rajput.socialmediasaver.databinding.ItemsWhatsappViewBinding
import com.rajput.socialmediasaver.interfaces.FileListWhatsappClickInterface
import com.rajput.socialmediasaver.model.WhatsappStatusModel
import com.rajput.socialmediasaver.util.Utils
import com.rajput.socialmediasaver.util.Utils.Companion.RootDirectoryWhatsappShow
import com.rajput.socialmediasaver.util.Utils.Companion.createFileFolder
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*



class WhatsappStatusAdapter : RecyclerView.Adapter<WhatsappStatusAdapter.ViewHolder> {
    private val context: Context
    private val fileArrayList: ArrayList<WhatsappStatusModel>?
    private var layoutInflater: LayoutInflater? = null
    var dialogProgress: ProgressDialog? = null
    var fileName = ""
var saveFilePath = RootDirectoryWhatsappShow.toString() + File.separator
    private var fileListClickInterface: FileListWhatsappClickInterface? = null

    constructor(context: Context, files: ArrayList<WhatsappStatusModel>?) {
        this.context = context
        fileArrayList = files
        initProgress()
    }

    constructor(
        context: Context,
        files: ArrayList<WhatsappStatusModel>?,
        fileListClickInterface: FileListWhatsappClickInterface?
    ) {
        this.context = context
        fileArrayList = files
        this.fileListClickInterface = fileListClickInterface
        initProgress()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        return ViewHolder(
            DataBindingUtil.inflate(
                layoutInflater!!,
                R.layout.items_whatsapp_view,
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val fileItem: WhatsappStatusModel = fileArrayList!![i]
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            viewHolder.binding.ivPlay.visibility = View.VISIBLE
        } else {
            viewHolder.binding.ivPlay.visibility = View.GONE
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Glide.with(context)
    .load(fileItem.getUri())
    .into(viewHolder.binding.pcw)
        } else {
            Glide.with(context)
    .load(fileItem.getPath())
    .into(viewHolder.binding.pcw)
        }
        viewHolder.binding.tvDownload.setOnClickListener { view ->
            createFileFolder()
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                try {
                    if (fileItem.getUri().toString().endsWith(".mp4")) {
                        fileName = "status_" + System.currentTimeMillis() + ".mp4"
                        DownloadFileTask()
                            .execute(fileItem.getUri().toString())
                    } else {
                        fileName = "status_" + System.currentTimeMillis() + ".png"
                        DownloadFileTask()
                            .execute(fileItem.getUri().toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val path: String = fileItem.getPath().toString()
                val filename = path.substring(path.lastIndexOf("/") + 1)
                val file = File(path)
                val destFile = File(saveFilePath)
                try {
                    FileUtils.copyFileToDirectory(file, destFile)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val fileNameChange = filename.substring(12)
                val newFile = File(saveFilePath + fileNameChange)
                var contentType = "image/*"
                contentType = if (fileItem.getUri().toString().endsWith(".mp4")) {
                    "video/*"
                } else {
                    "image/*"
                }
                MediaScannerConnection.scanFile(context,
                    arrayOf(newFile.absolutePath),
                    arrayOf(contentType),
                    object : MediaScannerConnectionClient {
                        override fun onMediaScannerConnected() {
                            //NA
                        }

                        override fun onScanCompleted(path: String, uri: Uri) {
                            //NA
                        }
                    })
                val from = File(saveFilePath, filename)
                val to = File(saveFilePath, fileNameChange)
                from.renameTo(to)
                Toast.makeText(
                    context,
                    context.resources
                        .getString(R.string.saved_to) + saveFilePath + fileNameChange,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewHolder.binding.ivPlay.setOnClickListener(View.OnClickListener {
            val intent = Intent(
                context,
                VideoPlayerActivity::class.java
            )
            intent.putExtra("PathVideo", fileItem.getUri().toString())
            context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return fileArrayList?.size ?: 0
    }

    inner class ViewHolder(mbinding: ItemsWhatsappViewBinding) :
        RecyclerView.ViewHolder(mbinding.root) {
        var binding: ItemsWhatsappViewBinding

        init {
            binding = mbinding
        }
    }

    fun initProgress() {
        dialogProgress = ProgressDialog(context)
        dialogProgress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialogProgress!!.setTitle("Saving")
        dialogProgress!!.setMessage("Saving. Please wait...")
        dialogProgress!!.isIndeterminate = true
        dialogProgress!!.setCanceledOnTouchOutside(false)
    }

    internal inner class DownloadFileTask :
        AsyncTask<String?, String?, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            try {
               val `in` = context.contentResolver.openInputStream(Uri.parse(params[0]))
                var f: File? = null
                f = File(RootDirectoryWhatsappShow.toString() + File.separator + fileName)
                f.setWritable(true, false)
                val outputStream: OutputStream = FileOutputStream(f)
                val buffer = ByteArray(1024)
                var length = 0
                while (`in`!!.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.close()
                `in`.close()
            } catch (e: IOException) {
                println("error in creating a file")
                e.printStackTrace()
            }
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {}
        override fun onPostExecute(fileUrl: String?) {
            Utils.setToast(context, context.resources.getString(R.string.download_complete))
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf<String>(File(RootDirectoryWhatsappShow.toString() + File.separator + fileName).absolutePath),
                        null
                    ) { path: String?, uri: Uri? -> }
                } else {
                    context.sendBroadcast(
                        Intent(
                            "android.intent.action.MEDIA_MOUNTED",
                            Uri.fromFile(File(RootDirectoryWhatsappShow.toString() + File.separator + fileName))
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}

