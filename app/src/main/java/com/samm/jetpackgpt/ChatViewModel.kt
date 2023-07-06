package com.samm.jetpackgpt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Response

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _state: MutableStateFlow<ChatListState?> = MutableStateFlow(null)
    val state = _state.asStateFlow()

    private fun createChatCompletion(request: ChatRequest): Flow<Response<ChatResponse>> = flow {
        try {
            val apiResponse = chatRepository.createChatCompletion(request)
            emit(apiResponse)
            Log.d("Error 1", apiResponse.message())
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }


    fun fetchChatCompletion(message: String) {

        _state.value = ChatListState(isLoading = true)

        val request = ChatRequest(model = "gpt-3.5-turbo-16k-0613", messages = listOf(Message("user", message)))
        createChatCompletion(request).onEach { response ->

            if (response.isSuccessful) {
                _state.value = ChatListState(isLoading = false, response = response.body())

            } else {
                Log.d("Error", response.message())
                _state.value = ChatListState(isLoading = false, error = response.message())
            }
        }.launchIn(viewModelScope)
    }
}

