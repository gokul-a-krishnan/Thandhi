package com.gstudios.thandhi.model

import java.util.*

object MessageType {
    const val TEXT = "Text"
    const val IMAGE = "Image"

}

interface Message {
    val time: Date
    val senderId: String
    val type: String
}