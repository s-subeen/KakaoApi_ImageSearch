package com.android.imagesearch.ui.main.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.imagesearch.databinding.FragmentSearchBinding
import com.android.imagesearch.repository.ImageSearchRepositoryImpl
import com.google.android.material.snackbar.Snackbar

class SearchListFragment : Fragment() {
    companion object {
        fun newInstance() = SearchListFragment()
    }

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelProviderFactory(
            ImageSearchRepositoryImpl(requireActivity())
        )
    }

    private val searchListAdapter by lazy {
        SearchListAdapter(
            itemClickListener = {
                viewModel.updateStorageItem(it)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadStorageItems()
    }

    private fun initView() {
        initSearchView()

        initRecyclerView()
    }

    private fun initRecyclerView() = with(binding) {
        recyclerSearch.adapter = searchListAdapter

        recyclerSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.recyclerSearch.canScrollVertically(1)
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                ) {
                    viewModel.plusPageCount()
                }
            }
        })
    }

    private fun initViewModel() = with(viewModel) {
        searchResult.observe(viewLifecycleOwner) {
            searchListAdapter.submitList(it.list)

            if (it.showSnackMessage) {
                it.snackMessage?.let { resId ->
                    showSnackBar(resId)
                }
            }
        }

        searchWord.observe(viewLifecycleOwner) {
            binding.searchView.setQuery(it, false)
        }

        pageCounts.observe(viewLifecycleOwner) {
            val query = binding.searchView.query.toString()
            viewModel.searchCombinedResults(query)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onStop() {
        val query = binding.searchView.query.toString()
        viewModel.saveStorageSearchWord(query)
        super.onStop()
    }

    private fun initSearchView() = with(binding) {
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.resetPageCount()
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun showSnackBar(resId: Int) {
        Snackbar.make(
            binding.searchFragment,
            getString(resId),
            Snackbar.LENGTH_SHORT
        ).show()
    }


    fun smoothScrollToTop() =
        binding.recyclerSearch.smoothScrollToPosition(0)

}
