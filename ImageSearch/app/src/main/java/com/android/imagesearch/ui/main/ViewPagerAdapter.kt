package com.android.imagesearch.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.imagesearch.R
import com.android.imagesearch.ui.main.search.SearchListFragment
import com.android.imagesearch.ui.main.storage.StorageFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf(
        MainTab(
            fragment = SearchListFragment.newInstance(),
            title = R.string.fragment_search,
            icon = R.drawable.selector_tab_search
        ),
        MainTab(
            fragment = StorageFragment.newInstance(),
            title = R.string.fragment_favorite,
            icon = R.drawable.selector_tab_storage
        )
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position].fragment
    }

    fun getFragment(position: Int): Fragment {
        return fragments[position].fragment
    }

    fun getTitle(position: Int): Int {
        return fragments[position].title
    }

    fun getIcon(position: Int): Int {
        return fragments[position].icon
    }

}