package com.rajput.socialmediasaver.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.activity.FullViewActivity
import com.rajput.socialmediasaver.activity.GalleryActivity
import com.rajput.socialmediasaver.adapter.FileListAdapter
import com.rajput.socialmediasaver.databinding.FragmentHistoryBinding
import com.rajput.socialmediasaver.interfaces.FileListClickInterface
import com.rajput.socialmediasaver.util.Utils.Companion.RootDirectoryWhatsappShow
import java.io.File
import java.util.Collections


class WhatsAppDowndlededFragment : Fragment(), FileListClickInterface {
    private var binding: FragmentHistoryBinding? = null
    private var fileListAdapter: FileListAdapter? = null
    private var fileArrayList: ArrayList<File>? = null
    private var activity: GalleryActivity? = null
    override fun onAttach(_context: Context) {
        super.onAttach(_context)
        activity = _context as GalleryActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val mParam1 = requireArguments().getString("m")
        }
    }

    override fun onResume() {
        super.onResume()
        activity = getActivity() as GalleryActivity?
        allFiles
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        initViews()
        return binding!!.root
    }

    private fun initViews() {
        binding!!.swiperefresh.setOnRefreshListener {
            allFiles
            binding!!.swiperefresh.isRefreshing = false
        }
    }

    private val allFiles: Unit
        private get() {
            fileArrayList = ArrayList()
            val files = RootDirectoryWhatsappShow.listFiles()
            if (files != null) {
                Collections.addAll(fileArrayList, *files)
                fileListAdapter =
                    FileListAdapter(requireActivity(), fileArrayList, this@WhatsAppDowndlededFragment)
                binding!!.rvFileList.adapter = fileListAdapter
            }
        }

    override fun getPosition(position: Int, file: File?) {
        val inNext = Intent(activity, FullViewActivity::class.java)
        inNext.putExtra("ImageDataFile", fileArrayList)
        inNext.putExtra("Position", position)
        requireActivity().startActivity(inNext)
    }

    companion object {
        fun newInstance(param1: String?): WhatsAppDowndlededFragment {
            val fragment = WhatsAppDowndlededFragment()
            val args = Bundle()
            args.putString("m", param1)
            fragment.arguments = args
            return fragment
        }
    }
}

