package com.gstudios.thandhi.recyclerview.item

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.gstudios.thandhi.R
import com.gstudios.thandhi.model.Message
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import java.text.SimpleDateFormat

abstract class MessageItem(private val message: Message) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        setTimeText(viewHolder)
        setRootGravity(viewHolder)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setTimeText(viewHolder: GroupieViewHolder) {
        val dateFormat = SimpleDateFormat("dd/MMM/yyyy hh:mm a")
        viewHolder.textView_message_time.text = dateFormat.format((message.time))
    }

    private fun setRootGravity(viewHolder: GroupieViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_primary_white
                this.layoutParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
            }
        } else {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rec_round_primary_color
                this.layoutParams =
                    FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
            }
        }
    }
}