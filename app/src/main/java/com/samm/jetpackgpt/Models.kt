package com.samm.jetpackgpt

data class ChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val choices: List<Choice>,
    val usage: Usage
)

data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Message(
    val role: String,
    val content: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)







data class GptRequest(
    val prompt: String,
    val max_tokens: Int,
    val model: String
)

data class GptResponse(
    val choices: List<Choice>
)

data class Choices(
    val text: String
)
