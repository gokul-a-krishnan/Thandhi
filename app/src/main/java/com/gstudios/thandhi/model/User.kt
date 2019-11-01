package com.gstudios.thandhi.model


data class User(
    val name: String,
    val bio: String,
    val profilePicturePath: String?,
    val tokens: MutableList<String>,
    val autoTranslate: Boolean,
    val language: String
) {
    constructor() : this("", "", null, mutableListOf(), false, "")
}