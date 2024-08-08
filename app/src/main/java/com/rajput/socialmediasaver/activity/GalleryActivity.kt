package com.rajput.socialmediasaver.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.databinding.ActivityGalleryBinding
import com.rajput.socialmediasaver.fragment.WhatsAppDowndlededFragment
import com.rajput.socialmediasaver.util.Utils.Companion.createFileFolder
import java.util.Locale
import java.util.ArrayList


class GalleryActivity : AppCompatActivity() {
    var activity: GalleryActivity? = null
    var binding: ActivityGalleryBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        activity = this
        initViews()
    }

    fun initViews() {
        binding?.let { setupViewPager(it.viewpager) }
        binding?.tabs?.setupWithViewPager(binding!!.viewpager)
        binding?.imBack?.setOnClickListener(View.OnClickListener { onBackPressed() })
        for (i in 0 until (binding?.tabs?.tabCount ?: 0)) {
            val tv = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
            binding?.tabs?.getTabAt(i)?.setCustomView(tv)
        }
        binding?.viewpager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
        createFileFolder()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter: ViewPagerAdapter = ViewPagerAdapter(
            activity!!.supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        adapter.addFragment(WhatsAppDowndlededFragment(), "Whatsapp")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4

    }

    internal inner class ViewPagerAdapter(fm: FragmentManager?, behavior: Int) :
        FragmentPagerAdapter(fm!!, behavior) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
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

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }
}

