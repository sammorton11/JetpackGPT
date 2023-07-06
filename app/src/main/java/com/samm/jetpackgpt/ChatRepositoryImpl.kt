package com.samm.jetpackgpt

import retrofit2.Call
import retrofit2.Response

class ChatRepositoryImpl(private val chatService: ChatService) : ChatRepository {
    override suspend fun createChatCompletion(request: ChatRequest): Response<ChatResponse> {
        return chatService.createChatCompletion(request)
    }
}
