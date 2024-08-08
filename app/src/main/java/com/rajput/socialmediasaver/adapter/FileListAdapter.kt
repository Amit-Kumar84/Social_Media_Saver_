package com.rajput.socialmediasaver.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.activity.VideoPlayerActivity
import com.rajput.socialmediasaver.databinding.ItemsFileViewBinding
import com.rajput.socialmediasaver.interfaces.FileListClickInterface
import java.io.File


class FileListAdapter(
    private val context: Context,
    private val fileArrayList: ArrayList<File>?,
    fileListClickInterface: FileListClickInterface
) :
    RecyclerView.Adapter<FileListAdapter.ViewHolder>() {
    private var layoutInflater: LayoutInflater? = null
    private val fileListClickInterface: FileListClickInterface

    init {
        this.fileListClickInterface = fileListClickInterface
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.context)
        }
        return ViewHolder(
            DataBindingUtil.inflate(
                layoutInflater!!,
                R.layout.items_file_view,
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val fileItem = fileArrayList!![i]
        try {
            val extension = fileItem.name.substring(fileItem.name.lastIndexOf("."))
            if (extension == ".mp4") {
                viewHolder.mbinding.ivPlay.visibility = View.VISIBLE
            } else {
                viewHolder.mbinding.ivPlay.visibility = View.GONE
            }
            viewHolder.mbinding.ivPlay.setOnClickListener { v ->
                val intent = Intent(
                    context,
                    VideoPlayerActivity::class.java
                )
                intent.putExtra("PathVideo", fileItem.path)
                context.startActivity(intent)
            }
            Glide.with(context)
                .load(fileItem.path)
                .into(viewHolder.mbinding.pc)
        } catch (ex: Exception) {
        }
        viewHolder.mbinding.rlMain.setOnClickListener(View.OnClickListener {
            fileListClickInterface.getPosition(
                i,
                fileItem
            )
        })
    }

    override fun getItemCount(): Int {
        return fileArrayList?.size ?: 0
    }

    inner class ViewHolder(mbinding: ItemsFileViewBinding) :
        RecyclerView.ViewHolder(mbinding.root) {
        var mbinding: ItemsFileViewBinding

        init {
            this.mbinding = mbinding
        }
    }
}