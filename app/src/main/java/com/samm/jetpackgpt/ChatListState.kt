package com.samm.jetpackgpt

data class ChatListState(
    val isLoading: Boolean = false,
    val response: ChatResponse? = null,
    val error: String = ""
)