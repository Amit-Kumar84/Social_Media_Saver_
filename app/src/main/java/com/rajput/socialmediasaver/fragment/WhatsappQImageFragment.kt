package com.rajput.socialmediasaver.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.adapter.WhatsappStatusAdapter
import com.rajput.socialmediasaver.databinding.FragmentWhatsappImageBinding
import com.rajput.socialmediasaver.interfaces.FileListWhatsappClickInterface
import com.rajput.socialmediasaver.model.WhatsappStatusModel
import java.io.File


class WhatsappQImageFragment(private val fileArrayList: ArrayList<Uri>) : Fragment(),
    FileListWhatsappClickInterface {
    var binding: FragmentWhatsappImageBinding? = null
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
            binding?.swiperefresh?.isRefreshing = false
        }
    }

    private val data: Unit
        get() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                try {
                    for (i in fileArrayList.indices) {
                        var whatsappStatusModel: WhatsappStatusModel
                        val uri = fileArrayList[i]
                        if (uri.toString().endsWith(".png") || uri.toString().endsWith(".jpg")) {
                            whatsappStatusModel = WhatsappStatusModel(
                                "WhatsStatus: " + (i + 1),
                                uri,
                                File(uri.toString()).absolutePath
                            )
                            statusModelArrayList!!.add(whatsappStatusModel)
                        }
                    }
                    if (statusModelArrayList!!.size != 0) {
                        binding?.tvNoResult?.visibility = View.GONE
                    } else {
                        binding?.tvNoResult?.visibility = View.VISIBLE
                    }
                    whatsappStatusAdapter = activity?.let {
                        WhatsappStatusAdapter(
                            it,
                            statusModelArrayList
                        )
                    }
                    binding?.rvFileList?.adapter = whatsappStatusAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    override fun getPosition(position: Int) {
        /*Intent inNext = new Intent(getActivity(), FullViewHomeWAActivity.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        getActivity().startActivity(inNext);*/
    }
}

