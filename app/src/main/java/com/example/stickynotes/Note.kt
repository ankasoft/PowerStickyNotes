package com.example.stickynotes

data class Note(
    val id: String = System.currentTimeMillis().toString(),
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
