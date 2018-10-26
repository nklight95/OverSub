package com.nklight.ultsub.Utils

import android.app.ProgressDialog
import android.content.Context

class Progress(context: Context?) : ProgressDialog(context) {
    override fun setProgress(value: Int) {
        super.setProgress(value)

    }
}