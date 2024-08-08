package com.rajput.socialmediasaver.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rajput.socialmediasaver.adapter.WhatsappStatusAdapter
import com.rajput.socialmediasaver.databinding.FragmentWhatsappImageBinding
import com.rajput.socialmediasaver.interfaces.FileListWhatsappClickInterface
import com.rajput.socialmediasaver.model.WhatsappStatusModel
import java.io.File
import java.util.*

class WhatsappImageFragment : Fragment(), FileListWhatsappClickInterface {
    private var binding: FragmentWhatsappImageBinding? = null
    private var allfiles: Array<File>? = null
    private var statusModelArrayList: ArrayList<WhatsappStatusModel>? = null
    private var whatsappStatusAdapter: WhatsappStatusAdapter? = null
    private var fileArrayList: ArrayList<Uri>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWhatsappImageBinding.inflate(inflater, container, false)
        initViews()
        return binding?.root
    }

    private fun initViews() {
        statusModelArrayList = ArrayList()
        fileArrayList = ArrayList()
        getData()
        binding?.swiperefresh?.setOnRefreshListener {
            statusModelArrayList = ArrayList()
            getData()
            binding?.swiperefresh?.isRefreshing = false
        }
    }

    private fun getData() {
        var whatsappStatusModel: WhatsappStatusModel
        var targetPath = Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            targetPath = Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        }
        val targetDirector = File(targetPath)
        allfiles = targetDirector.listFiles()

        var targetPathBusiness = Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            targetPathBusiness = Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"
        }
        val targetDirectorBusiness = File(targetPathBusiness)
        var allfilesBusiness = targetDirectorBusiness.listFiles()
        if (allfilesBusiness == null) {
            val targetDirectorBusinessNew = File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses")
            allfilesBusiness = targetDirectorBusinessNew.listFiles()
        }

        try {
            Arrays.sort(allfiles) { o1, o2 ->
                when {
                    o1.lastModified() > o2.lastModified() -> -1
                    o1.lastModified() < o2.lastModified() -> 1
                    else -> 0
                }
            }

            for (i in allfiles!!.indices) {
                val file = allfiles!![i]
                if (Uri.fromFile(file).toString().endsWith(".png") || Uri.fromFile(file).toString().endsWith(".jpg")) {
                    fileArrayList?.add(Uri.fromFile(file))
                    whatsappStatusModel = WhatsappStatusModel(
                        "WhatsStatus: ${i + 1}",
                        Uri.fromFile(file),
                        file.absolutePath,

                    )
                    statusModelArrayList?.add(whatsappStatusModel)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            Arrays.sort(allfilesBusiness) { o1, o2 ->
                when {
                    o1.lastModified() > o2.lastModified() -> -1
                    o1.lastModified() < o2.lastModified() -> 1
                    else -> 0
                }
            }

            for (i in allfilesBusiness!!.indices) {
                val file = allfilesBusiness!![i]
                if (Uri.fromFile(file).toString().endsWith(".png") || Uri.fromFile(file).toString().endsWith(".jpg")) {
                    fileArrayList?.add(Uri.fromFile(file))
                    whatsappStatusModel = WhatsappStatusModel(
                        "WhatsStatusB: ${i + 1}",
                        Uri.fromFile(file),
                        file.absolutePath,
                    )
                    statusModelArrayList?.add(whatsappStatusModel)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (statusModelArrayList!!.isNotEmpty()) {
            binding?.tvNoResult?.visibility = View.GONE
        } else {
            binding?.tvNoResult?.visibility = View.VISIBLE
        }
        whatsappStatusAdapter = activity?.let { WhatsappStatusAdapter(it, statusModelArrayList, this) }
        binding?.rvFileList?.adapter = whatsappStatusAdapter
    }

    override fun getPosition(position: Int) {
        // Implement the method as needed
    }
}