package com.samm.jetpackgpt

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatModule {

    val repositoryModule = module {
        single<ChatRepository> { ChatRepositoryImpl(get()) }
    }

    val retrofitModule = module {
        single {
            Retrofit.Builder()
                .baseUrl("https://api.openai.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single {
            val retrofit: Retrofit = get()
            retrofit.create(ChatService::class.java)
        }
    }

    val chatModule = module {
        viewModel { ChatViewModel(get()) }
    }
}