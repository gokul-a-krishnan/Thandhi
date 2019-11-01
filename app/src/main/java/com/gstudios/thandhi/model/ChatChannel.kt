package com.gstudios.thandhi.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}