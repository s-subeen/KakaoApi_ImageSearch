package com.android.imagesearch.ui.main.storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.databinding.StorageBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog(
    private val onRemoveClickListener: (SearchModel) -> Unit,
    private val searchModel: SearchModel

) : BottomSheetDialogFragment() {

    private var _binding: StorageBottomSheetBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StorageBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.btCancel.setOnClickListener {
            dismiss()
        }

        binding.btRemove.setOnClickListener {
            onRemoveClickListener.invoke(searchModel)
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}