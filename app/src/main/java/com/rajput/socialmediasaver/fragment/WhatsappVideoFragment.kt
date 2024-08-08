package com.rajput.socialmediasaver.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.adapter.WhatsappStatusAdapter
import com.rajput.socialmediasaver.databinding.FragmentWhatsappImageBinding
import com.rajput.socialmediasaver.model.WhatsappStatusModel
import java.io.File
import java.util.Arrays


class WhatsappVideoFragment : Fragment() {
    var binding: FragmentWhatsappImageBinding? = null
    private lateinit var allfiles: Array<File>
    private var statusModelArrayList: ArrayList<WhatsappStatusModel>? = null
    private var whatsappStatusAdapter: WhatsappStatusAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_whatsapp_image, container, false)
        initViews()
        return binding?.root
    }

    private fun initViews() {
        statusModelArrayList = ArrayList()
        data
        binding?.swiperefresh?.setOnRefreshListener {
            statusModelArrayList = ArrayList()
            data
            binding?.swiperefresh!!.isRefreshing = false
        }
    }

    private val data: Unit
        get() {
            var whatsappStatusModel: WhatsappStatusModel
            var targetPath =
                Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                targetPath =
                    Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
            }
            val targetDirector = File(targetPath)
            allfiles = targetDirector.listFiles()!!
            var targetPathBusiness =
                Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                targetPathBusiness =
                    Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"
            }
            val targetDirectorBusiness = File(targetPathBusiness)
            var allfilesBusiness = targetDirectorBusiness.listFiles()
            if (allfilesBusiness == null) {
                val targetDirectorBusinessNew =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses")
                allfilesBusiness = targetDirectorBusinessNew.listFiles()
            }
            try {
                Arrays.sort(allfiles
                ) { o1: File, o2: File ->
                    when {
                        o1.lastModified() > o2.lastModified() -> -1
                        o1.lastModified() < o2.lastModified() -> 1
                        else -> 0
                    }
                }
                for (i in allfiles.indices) {
                    val file = allfiles[i]
                    if (Uri.fromFile(file).toString().endsWith(".mp4")) {
                        whatsappStatusModel = WhatsappStatusModel(
                            "WhatsStatus: " + (i + 1),
                            Uri.fromFile(file),
                            allfiles[i].absolutePath
                        )
                        statusModelArrayList!!.add(whatsappStatusModel)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (allfilesBusiness != null) {
                    Arrays.sort(allfilesBusiness
                    ) { o1: File, o2: File ->
                        when {
                            o1.lastModified() > o2.lastModified() -> -1
                            o1.lastModified() < o2.lastModified() -> 1
                            else -> 0
                        }
                    }
                }
                for (i in allfilesBusiness!!.indices) {
                    val file = allfilesBusiness[i]
                    if (Uri.fromFile(file).toString().endsWith(".mp4")) {
                        whatsappStatusModel = WhatsappStatusModel(
                            "WhatsStatusB: " + (i + 1),
                            Uri.fromFile(file),
                            allfilesBusiness[i].absolutePath
                        )
                        statusModelArrayList!!.add(whatsappStatusModel)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (statusModelArrayList!!.size != 0) {
                binding?.tvNoResult?.visibility = View.GONE
            } else {
                binding?.tvNoResult?.visibility = View.VISIBLE
            }
            whatsappStatusAdapter = activity?.let { WhatsappStatusAdapter(it, statusModelArrayList) }
            binding?.rvFileList?.adapter = whatsappStatusAdapter
        }
}

