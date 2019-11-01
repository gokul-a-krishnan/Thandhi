package com.gstudios.thandhi.recyclerview.item

import android.content.Context
import com.gstudios.thandhi.R
import com.gstudios.thandhi.glide.GlideApp
import com.gstudios.thandhi.model.ImageMessage
import com.gstudios.thandhi.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.image_message.*

class ImageMessageItem(val message: ImageMessage, val context: Context) : MessageItem(message) {
    override fun getLayout() = R.layout.image_message

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        GlideApp.with(context)
            .load(StorageUtil.pathToReference(message.imagePath))
            .placeholder(R.drawable.ic_image_black_24dp)
            .into(viewHolder.imageView_message_image)

    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ImageMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ImageMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }
}