package com.gstudios.thandhi.recyclerview.item

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.gstudios.thandhi.R
import com.gstudios.thandhi.model.TextMessage
import com.gstudios.thandhi.translator.OnDeviceTranslator
import com.gstudios.thandhi.util.FirestoreUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_text_message.*

class TextMessageItem(
    val message: TextMessage,
    val context: Context
) : MessageItem(message) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if (FirebaseAuth.getInstance().currentUser?.uid != message.senderId) {
            FirestoreUtil.getCurrentUser {
                if (it.autoTranslate)
                    OnDeviceTranslator.detectLanguage(message.text) { language, status ->
                        if (status) {
                            OnDeviceTranslator.translateNow(
                                message.text,
                                language,
                                it.language.toInt()
                            ) { output, _ ->
                                viewHolder.textView_message_text.text = output
                            }
                        } else {
                            viewHolder.textView_message_text.text = message.text
                        }
                    }

            }
        } else {
            viewHolder.textView_message_text.text = message.text
        }
        super.bind(viewHolder, position)

    }

    override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }
}