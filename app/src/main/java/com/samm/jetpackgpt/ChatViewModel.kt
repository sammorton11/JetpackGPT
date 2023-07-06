package com.samm.jetpackgpt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _state: MutableStateFlow<ChatResponse?> = MutableStateFlow(null)
    val state = _state.asStateFlow()

    private val _list: MutableStateFlow<ChatListState?> = MutableStateFlow(null)
    val list = _list.asStateFlow()

    private fun createChatCompletion(request: ChatRequest): Flow<Response<ChatResponse>> = flow<Response<ChatResponse>> {
        try {
            val apiResponse = chatRepository.createChatCompletion(request)
            emit(apiResponse)
            Log.d("Error 1", apiResponse.message())
        } catch (e: Exception) {
            Log.d("Error", e.toString() ?: "Unexpected Error")
        }
    }.catch { e -> Log.d("Error 2", e.toString() ?: "Unexpected Error")  }

    fun add(bubble: MessageBubble) = viewModelScope.launch(Dispatchers.IO) {
        _list.value?.messageList?.add(bubble)
    }

    fun fetchChatCompletion(message: String) {

//        _state.value = ChatResponse(
//            id = "id",
//            `object` = "object",
//            created =  1234L,
//            choices = listOf(Choice(1234, Message("", "Hello!"), "finish reason")),
//            usage = Usage(
//                1234,
//                1234,
//                1234,
//            )
//        )

        val request = ChatRequest(model = "gpt-3.5-turbo-16k-0613", messages = listOf(Message("user", message)))
        createChatCompletion(request).onEach {
            if (it.isSuccessful) {
                _state.value = it.body()
                it.body()?.choices?.first()?.message?.content?.let { it1 ->
                    MessageBubble(
                        it1, false)
                }?.let { it2 -> _list.value?.messageList?.add(it2) }
            } else {
                Log.d("Error", it.message())
            }
        }.launchIn(viewModelScope)
    }
}

data class ChatListState(
    val isLoading: Boolean = false,
    val messageList: MutableList<MessageBubble>? = null,
    val error: String = ""
)
