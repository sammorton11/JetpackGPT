package com.samm.jetpackgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.samm.jetpackgpt.ui.theme.JetpackGPTTheme
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


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

                    val state = viewModel.state.collectAsState().value
                    ChatScreen(state, viewModel::fetchChatCompletion)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    state: ChatListState?,
    fetchChatCompletion: (message: String) -> Unit
) {

    var userInput by remember { mutableStateOf("") }
    val imeAction = ImeAction.Done
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            when {
                state?.isLoading == true -> {
                    CircularProgressIndicator(modifier = Modifier.padding(25.dp))
                }
                !state?.response?.choices.isNullOrEmpty() -> {

                    LazyColumn(modifier = Modifier
                        .fillMaxHeight()
                        .padding(15.dp)) {
                        item {
                            MessageBubble(state?.response?.choices?.first()?.message?.content, userInput)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                state?.error?.isBlank() == true -> {
                    Text(text = state.error)
                }
            }
        }
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            placeholder = { Text(text = "Ask anything...") },
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colorScheme.onSurface,
                textColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = MaterialTheme.typography.bodySmall,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = KeyboardActions(onDone = {
                fetchChatCompletion(userInput)
                keyboardController?.hide()
            })
        )
    }
}

@Composable
fun MessageBubble(reply: String?, user: String) {
    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()) {

        Card(
            modifier = Modifier
                .align(Alignment.End)
                .padding(start = 25.dp)
                .padding(8.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "User: \n $user",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 15.dp, end = 25.dp)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

        Card(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(end = 25.dp)
                .padding(8.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Reply: \n\n $reply",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(end = 25.dp)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.padding(top = 15.dp))
    }
}
