package com.seiko.player.delegate

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.seiko.player.util.livedata.LiveEvent
import org.videolan.libvlc.Dialog

interface IDialogDelegate {
    fun observeDialogs(lco: LifecycleOwner, manager: IDialogManager)
}

interface IDialogManager {
    fun fireDialog(dialog: Dialog)
    fun dialogCanceled(dialog: Dialog?)
}

class DialogDelegate : IDialogDelegate {
    override fun observeDialogs(lco: LifecycleOwner, manager: IDialogManager) {
        dialogEvt.observe(lco::getLifecycle) {
            when(it) {
                is Show -> manager.fireDialog(it.dialog)
                is Cancel -> manager.dialogCanceled(it.dialog)
            }
        }
    }

    companion object DialogsListener : Dialog.Callbacks {
        private val dialogEvt: LiveEvent<DialogEvt> = LiveEvent()
        var dialogCounter = 0

        override fun onProgressUpdate(dialog: Dialog.ProgressDialog) {
//            val vlcProgressDialog = dialog.context as? VlcProgressDialog ?: return
//            if (vlcProgressDialog.isVisible) vlcProgressDialog.updateProgress()
        }

        override fun onDisplay(dialog: Dialog.ErrorMessage) {
            dialogEvt.value = Cancel(dialog)
        }

        override fun onDisplay(dialog: Dialog.LoginDialog) {
            dialogEvt.value = Show(dialog)
        }

        override fun onDisplay(dialog: Dialog.QuestionDialog) {
            dialogEvt.value = Show(dialog)
        }

        override fun onDisplay(dialog: Dialog.ProgressDialog) {
            dialogEvt.value = Show(dialog)
        }

        override fun onCanceled(dialog: Dialog?) {
            (dialog?.context as? DialogFragment)?.dismiss()
            dialogEvt.value = Cancel(dialog)
        }
    }
}

private sealed class DialogEvt
private class Show(val dialog: Dialog) : DialogEvt()
private class Cancel(val dialog: Dialog?) : DialogEvt()