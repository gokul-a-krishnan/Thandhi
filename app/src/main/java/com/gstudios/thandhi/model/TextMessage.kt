package com.gstudios.thandhi.model

import java.util.*

data class TextMessage(
    val text: String,
    override val time: Date,
    override val senderId: String,
    val autoTranslate: Boolean,
    val language: String,
    override val type: String = MessageType.TEXT
) : Message {
    constructor() : this("", Date(), "", false, "11")
}