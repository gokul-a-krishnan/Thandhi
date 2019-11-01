package com.gstudios.thandhi


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.gstudios.thandhi.model.ImageMessage
import com.gstudios.thandhi.model.MessageType
import com.gstudios.thandhi.model.TextMessage
import com.gstudios.thandhi.util.FirestoreUtil
import com.gstudios.thandhi.util.StorageUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

private const val RC_SELECT_IMAGE = 2473

class ChatActivity : AppCompatActivity() {
    private lateinit var channelId: String
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shallInitRecycleView = true
    private lateinit var section: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)
        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)
        FirestoreUtil.getOrCreateChatChannel(otherUserId!!) { channelId ->
            this.channelId = channelId
            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)
            img_btn_send.setOnClickListener {
                FirestoreUtil.getCurrentUser {
                    if (edit_text_message.text.isNotEmpty()) {
                        val messageToSend = TextMessage(
                            edit_text_message.text.toString(),
                            Calendar.getInstance().time,
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            it.autoTranslate,
                            it.language,
                            MessageType.TEXT
                        )
                        edit_text_message.setText("")
                        FirestoreUtil.sendMessage(messageToSend, channelId)
                    }

                }
            }
            fab_send_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        arrayOf("image/jpg", "image/jpeg", "image/png")
                    )
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    RC_SELECT_IMAGE
                )
            }
        }

    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recyclerView_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    section = Section(messages)
                    this.add(section)
                }
            }
            shallInitRecycleView = false
        }

        fun updateItems() = section.update(messages)

        if (shallInitRecycleView) init() else updateItems()
        recyclerView_messages.scrollToPosition(messages.lastIndex)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imagePath = data.data!!
            lateinit var imageBitmap: Bitmap
            imagePath.let {
                imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(this.contentResolver, imagePath)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, imagePath)
                    ImageDecoder.decodeBitmap(source)
                }
            }
            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val imageBytes = outputStream.toByteArray()
            StorageUtil.uploadMessageImage(imageBytes) { imgPath ->
                val messageToSend =
                    ImageMessage(
                        imgPath,
                        Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }
        }
    }
}
