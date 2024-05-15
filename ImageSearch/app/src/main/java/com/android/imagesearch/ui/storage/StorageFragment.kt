package com.android.imagesearch.ui.storage


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.ui.search.SearchList

data class BottomSheetState(
    val isOpen: Boolean = false,
    val searchModel: SearchModel? = null
)

@Composable
fun StorageScreen(viewModel: StorageViewModel) {
    val searchState by viewModel.storageItems.observeAsState()
    var bottomSheetState by remember { mutableStateOf(BottomSheetState()) }

    LaunchedEffect(viewModel) {
        viewModel.getStorageItems()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SearchList(
            items = searchState ?: emptyList(),
            onItemClick = { searchModel ->
                bottomSheetState = BottomSheetState(isOpen = true, searchModel = searchModel)
            },
            onScrollEnd = { viewModel.getStorageItems() }
        )
    }

    if (bottomSheetState.isOpen) {
        BottomSheetDialog(
            searchModel = bottomSheetState.searchModel!!,
            onRemoveClickListener = { searchModel ->
                viewModel.removeStorageItem(searchModel)
                bottomSheetState = BottomSheetState()
            },
        )
    }
}

//class StorageFragment : Fragment() {
//    companion object {
//        fun newInstance() = StorageFragment()
//    }
//
//    private var _binding: FragmentStorageBinding? = null
//
//    private val binding get() = _binding!!
//
//    private val viewModel: StorageViewModel by viewModels {
//        StorageViewModelProviderFactory(
//            ImageSearchRepositoryImpl(requireActivity())
//        )
//    }
//
//    private val storageListAdapter by lazy {
//        StorageListAdapter(
//            itemClickListener = {
//                showBottomSheetDialog(it)
//            }
//        )
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentStorageBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initView()
//        initViewModel()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        viewModel.getStorageItems()
//    }
//
//    private fun initView() {
//        binding.recyclerFavorite.adapter = storageListAdapter
//    }
//
//    private fun initViewModel() = with(viewModel) {
//        storageItems.observe(viewLifecycleOwner) {
//            storageListAdapter.submitList(it)
//        }
//    }
//
//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }
//
//    fun smoothScrollToTop() {
//        binding.recyclerFavorite.smoothScrollToPosition(0)
//    }
//
//    private fun showBottomSheetDialog(searchModel: SearchModel) {
//        BottomSheetDialog(
//            onRemoveClickListener = { removedItem ->
//                viewModel.removeStorageItem(removedItem)
//            },
//            searchModel = searchModel
//        ).show(requireActivity().supportFragmentManager, null)
//    }
//}