package com.samm.jetpackgpt

import retrofit2.Call
import retrofit2.Response

interface ChatRepository {
    suspend fun createChatCompletion(request: ChatRequest): Response<ChatResponse>
}
