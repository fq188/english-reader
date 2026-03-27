package com.htv.player.ui.download

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.htv.player.R
import com.htv.player.data.model.DownloadTask
import com.htv.player.databinding.DialogDownloadOptionsBinding

class DownloadOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogDownloadOptionsBinding? = null
    private val binding get() = _binding!!

    private var onOptionSelected: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDownloadOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pauseOption.setOnClickListener {
            onOptionSelected?.invoke(OPTION_PAUSE)
            dismiss()
        }

        binding.resumeOption.setOnClickListener {
            onOptionSelected?.invoke(OPTION_RESUME)
            dismiss()
        }

        binding.deleteOption.setOnClickListener {
            onOptionSelected?.invoke(OPTION_DELETE)
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setOnOptionSelectedListener(listener: (String) -> Unit) {
        onOptionSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val OPTION_PAUSE = "pause"
        const val OPTION_RESUME = "resume"
        const val OPTION_DELETE = "delete"

        fun newInstance(download: DownloadTask): DownloadOptionsDialog {
            return DownloadOptionsDialog()
        }
    }
}
