package com.samm.jetpackgpt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samm.jetpackgpt.ui.theme.JetpackGPTTheme
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.context.startKoin

// Todo: Idk how to do chat bubbles lol


class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(ChatModule.chatModule, ChatModule.retrofitModule, ChatModule.repositoryModule)
        }

        setContent {
            JetpackGPTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(viewModel)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {

    val response = viewModel.state.collectAsState().value
    Log.d("response", response.toString())
    val messages = viewModel.list.collectAsState().value
    var userInput by remember { mutableStateOf("") }
    val bubbles = mutableListOf<MessageBubble>()
    val imeAction = ImeAction.Done
    val keyboardController = LocalSoftwareKeyboardController.current

    Box {

        val list = messages?.messageList?.toList()

        list?.let {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)) {
                items(list) {
                    MessageBubble(it.content)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }



        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomEnd)
                .height(56.dp),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colorScheme.onSurface,
                textColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = MaterialTheme.typography.bodySmall,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.fetchChatCompletion("$userInput - Talk to me like a pirate")
                viewModel.add(MessageBubble(userInput, true))
                keyboardController?.hide()
            })
        )
    }
}

data class MessageBubble(val content: String, val isSentByUser: Boolean)

@Composable
fun MessageBubble(text: String) {
    Row(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()) {

        Text(
            text = text,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .padding(8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

//        if (message.isSentByUser) {
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
//                text = message.content,
//                modifier = Modifier
//                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
//                    .padding(8.dp),
//                color = MaterialTheme.colorScheme.onPrimary
//            )
//        } else {
//            Text(
//                text = message.content,
//                modifier = Modifier
//                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
//                    .padding(8.dp),
//                color = MaterialTheme.colorScheme.onSecondary
//            )
//            Spacer(modifier = Modifier.weight(1f))
//        }
    }
}
