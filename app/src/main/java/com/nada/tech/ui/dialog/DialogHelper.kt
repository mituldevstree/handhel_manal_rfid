package com.nada.tech.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.nada.tech.databinding.LayoutDialogBinding
import com.nada.tech.listener.IDialogButtonClick
import com.nada.tech.listener.IDialogDismissListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogHelper : BaseDialog(), View.OnClickListener {
    lateinit var binding: LayoutDialogBinding

    private var title: String? = null
    private var message: String? = null
    private var positive: String? = null
    private var negative: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LayoutDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
        binding.txtMessage.text = message

        if (!title.isNullOrEmpty()) {
            binding.txtTitle.text = title
            binding.txtTitle.visibility = View.VISIBLE
        } else {
            binding.txtTitle.visibility = View.GONE
        }

        if (negative.isNullOrEmpty()) {
            binding.btnNegative.visibility = View.GONE
        } else {
            binding.btnNegative.text = negative
            binding.btnNegative.visibility = View.VISIBLE
        }

        if (!positive.isNullOrEmpty()) {
            binding.btnPositive.text = positive
        }
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.btnPositive -> {
                dialogButtonClick?.onButtonClick(true)
                dismiss()
            }
            binding.btnNegative -> {
                dialogButtonClick?.onButtonClick(false)
                dismiss()
            }
        }
    }


    override fun show(activity: FragmentActivity): BaseDialog? {
        return super.show(activity, DialogHelper::class.java.simpleName)
    }

    companion object {
        private val TAG = DialogHelper::class.java.simpleName

        fun newInstance(
            title: String?,
            message: String?,
            positiveText: String?,
            negative: String?,
            dialogButtonClick: IDialogButtonClick?,
            dialogDismissListener: IDialogDismissListener?,
        ): DialogHelper {
            val fragment = DialogHelper()
            fragment.title = title
            fragment.message = message
            fragment.negative = negative
            fragment.positive = positiveText
            fragment.dialogButtonClick = dialogButtonClick
            fragment.dialogDismissListener = dialogDismissListener
            return fragment
        }

        fun newInstance(
            title: String?,
            message: String?,
            positiveText: String?,
            negative: String?,
            dialogButtonClick: IDialogButtonClick?,
        ): DialogHelper {
            val fragment = DialogHelper()
            fragment.title = title
            fragment.message = message
            fragment.negative = negative
            fragment.positive = positiveText
            fragment.dialogButtonClick = dialogButtonClick
            return fragment
        }

        fun newInstance(
            message: String?,
            positiveText: String?,
            dialogButtonClick: IDialogButtonClick?,
            dialogDismissListener: IDialogDismissListener?,
        ): DialogHelper {
            val fragment = DialogHelper()
            fragment.message = message
            fragment.dialogButtonClick = dialogButtonClick
            fragment.dialogDismissListener = dialogDismissListener
            fragment.positive = positiveText
            return fragment
        }

        fun newInstance(
            message: String?,
            positiveText: String?,
            negativeText: String?,
            dialogButtonClick: IDialogButtonClick?,
        ): DialogHelper {
            val fragment = DialogHelper()
            fragment.message = message
            fragment.dialogButtonClick = dialogButtonClick
            fragment.positive = positiveText
            fragment.negative = negativeText
            return fragment
        }

        fun newInstance(
            message: String?,
            positiveText: String?,
            dialogButtonClick: IDialogButtonClick?,
        ): DialogHelper {
            val fragment = DialogHelper()
            fragment.message = message
            fragment.dialogButtonClick = dialogButtonClick
            fragment.positive = positiveText
            return fragment
        }

        fun newInstance(
            message: String?,
            positiveText: String?,
            dialogDismissListener: IDialogDismissListener?,
        ): DialogHelper {
            val fragment = DialogHelper()
            fragment.message = message
            fragment.dialogDismissListener = dialogDismissListener
            fragment.positive = positiveText
            return fragment
        }

        fun newInstance(
            message: String?,
            dialogDismissListener: IDialogDismissListener?,
        ): DialogHelper {
            val fragment = DialogHelper()
            fragment.dialogDismissListener = dialogDismissListener
            return fragment
        }

        fun newInstance(message: String?): DialogHelper {
            val fragment = DialogHelper()
            fragment.message = message
            return fragment
        }

        fun newInstance(message: String?, dialogButtonClick: IDialogButtonClick?): DialogHelper {
            val fragment = DialogHelper()
            fragment.message = message
            fragment.dialogButtonClick = dialogButtonClick
            return fragment
        }
    }
}