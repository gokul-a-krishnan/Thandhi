package com.gstudios.thandhi.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.gstudios.thandhi.AppConstants
import com.gstudios.thandhi.ChatActivity
import com.gstudios.thandhi.R
import com.gstudios.thandhi.recyclerview.item.PersonItem
import com.gstudios.thandhi.util.FirestoreUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_message.*
import org.jetbrains.anko.support.v4.startActivity

class MessageFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private var shallInitRecyclerView = true
    private lateinit var peopleSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userListenerRegistration =
            FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        FirestoreUtil.removeListener(userListenerRegistration)
        shallInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {
        fun init() {
            recyclerView_message.apply {
                layoutManager = LinearLayoutManager(this@MessageFragment.context)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shallInitRecyclerView = false
        }

        fun update() = peopleSection.update(items)

        if (shallInitRecyclerView) init() else update()
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is PersonItem) {
            startActivity<ChatActivity>(
                AppConstants.USER_NAME to item.person.name,
                AppConstants.USER_ID to item.userId
            )
        }
    }

}
