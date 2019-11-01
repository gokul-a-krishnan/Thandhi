package com.gstudios.thandhi.recyclerview.item

import android.content.Context
import com.gstudios.thandhi.R
import com.gstudios.thandhi.glide.GlideApp
import com.gstudios.thandhi.model.User
import com.gstudios.thandhi.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_person.*

class PersonItem(val person: User, val userId: String, private val context: Context) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
        if (person.profilePicturePath != null) {
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePicturePath))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.imageView_profile_picture)
        }
    }

    override fun getLayout() = R.layout.item_person

}