package com.samm.jetpackgpt

import com.samm.jetpackgpt.Constants.API_KEY
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer $API_KEY"
    )
    @POST("/v1/chat/completions")
    suspend fun createChatCompletion(@Body requestBody: ChatRequest): Response<ChatResponse>


    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer $API_KEY"
    )
    @POST("/v1/completions")
    fun getCompletion(
        @Body requestBody: GptRequest
    ): Call<GptResponse>
}
