package com.gstudios.thandhi.model

import java.util.*

data class ImageMessage(
    val imagePath: String,
    override val time: Date,
    override val senderId: String,
    override val type: String = MessageType.IMAGE
) : Message {
    constructor() : this("", Date(), "")
}