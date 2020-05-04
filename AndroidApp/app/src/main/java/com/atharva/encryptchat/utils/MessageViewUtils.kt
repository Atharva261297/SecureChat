package com.atharva.encryptchat.utils

import android.content.Context
import android.content.res.Resources
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.atharva.encryptchat.R

object MessageViewUtils {
    private fun messageArea(context: Context): TableRow {
        val layoutParams = TableLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val tableRow = TableRow(context)
        tableRow.layoutParams = layoutParams
        return tableRow
    }

    private fun layoutParamsForMessageBox(resources: Resources, column: Int): TableRow.LayoutParams {
        val layoutParams = TableRow.LayoutParams( 0, TableRow.LayoutParams.WRAP_CONTENT )

        layoutParams.span = 2
        layoutParams.column = column

        val margin = resources.getDimensionPixelSize(R.dimen.margin_size)
        layoutParams.setMargins(margin, margin, margin, margin)

        return layoutParams
    }

    fun messageBoxForReadMy(context: Context, message: String, resources: Resources): TableRow {
        val textView = TextView( ContextThemeWrapper(context, R.style.message), null, 0 )
        textView.layoutParams = layoutParamsForMessageBox(resources, 1)

        val padding = resources.getDimensionPixelSize( R.dimen.margin_size )
        textView.setPadding( padding, 0, padding, 0 )

        textView.background = ContextCompat.getDrawable(context, R.drawable.read_message_outline)

        textView.text = message

        val messageArea = messageArea(context)
        messageArea.addView(textView)
        return messageArea
    }

    fun messageBoxForReadTheir(context: Context, message: String, resources: Resources): TableRow {
        val textView = TextView( ContextThemeWrapper(context, R.style.message), null, 0 )
        textView.layoutParams = layoutParamsForMessageBox(resources, 0)

        val padding = resources.getDimensionPixelSize( R.dimen.margin_size )
        textView.setPadding( padding, 0, padding, 0 )

        textView.background = ContextCompat.getDrawable(context, R.drawable.read_message_outline)

        textView.text = message

        val messageArea = messageArea(context)
        messageArea.addView(textView)
        return messageArea
    }

    fun messageBoxForUnread(context: Context, message: String, resources: Resources): TableRow {
        val textView = TextView( ContextThemeWrapper(context, R.style.message), null, 0 )
        textView.layoutParams = layoutParamsForMessageBox(resources, 0)

        val padding = resources.getDimensionPixelSize( R.dimen.margin_size )
        textView.setPadding( padding, 0, padding, 0 )

        textView.background = ContextCompat.getDrawable(context, R.drawable.read_message_outline)

        textView.text = message

        val messageArea = messageArea(context)
        messageArea.addView(textView)
        return messageArea
    }

}