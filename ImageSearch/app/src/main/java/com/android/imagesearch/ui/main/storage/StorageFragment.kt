package com.android.imagesearch.ui.main.storage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.databinding.FragmentStorageBinding
import com.android.imagesearch.repository.ImageSearchRepositoryImpl

class StorageFragment : Fragment() {
    companion object {
        fun newInstance() = StorageFragment()
    }

    private var _binding: FragmentStorageBinding? = null

    private val binding get() = _binding!!

    private val viewModel: StorageViewModel by viewModels {
        StorageViewModelProviderFactory(
            ImageSearchRepositoryImpl(requireActivity())
        )
    }

    private val storageListAdapter by lazy {
        StorageListAdapter(
            itemClickListener = {
                showBottomSheetDialog(it)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStorageItems()
    }

    private fun initView() {
        binding.recyclerFavorite.adapter = storageListAdapter
    }

    private fun initViewModel() = with(viewModel) {
        storageItems.observe(viewLifecycleOwner) {
            storageListAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun smoothScrollToTop() {
        binding.recyclerFavorite.smoothScrollToPosition(0)
    }

    private fun showBottomSheetDialog(searchModel: SearchModel) {
        BottomSheetDialog(
            onRemoveClickListener = { removedItem ->
                viewModel.removeStorageItem(removedItem)
            },
            searchModel = searchModel
        ).show(requireActivity().supportFragmentManager, null)
    }
}