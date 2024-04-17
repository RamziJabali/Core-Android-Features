package ramzi.eljabali.androidcore.notifications

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun SimpleTextBoxView(
    checkText: (text: String) -> Unit,
    userClickedActionSnackBar: () -> Unit,
    userClickedDismissSnackBar: () -> Unit,
    failureMessage: String,
    showSnackBar: Boolean,
    rationaleMessage: String
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var text by remember { mutableStateOf("") }
    Log.i("Scaffold", "In Scaffold")
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
            if (showSnackBar) {
                Runnable {
                    coroutineScope.launch {
                        Log.i("Log.d", "Displaying SnackBar")
                        val result = snackBarHostState
                            .showSnackbar(
                                message = rationaleMessage,
                                actionLabel = "Accept",
                                // Defaults to SnackbarDuration.Short
                                duration = SnackbarDuration.Indefinite,
                                withDismissAction = true
                            )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                Log.i("Log.d", "In Action Performed")
                                userClickedActionSnackBar()
                            }

                            SnackbarResult.Dismissed -> {
                                /* Handle snackbar dismissed */
                                Log.i("Log.d", "SnackBar Dismissed")
                                userClickedDismissSnackBar()
                            }
                        }
                    }
                }.run()
            }
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Column(horizontalAlignment = CenterHorizontally) {
                Text("Enter Number 1234 for a Personal notification channel and 4321 for Work notification channel")
                TextField(value = text, onValueChange = { newText ->
                    text = newText
                }, Modifier.fillMaxWidth())
                Text(text = failureMessage)
                Button(onClick = {
                    checkText(text)
                }, Modifier.fillMaxWidth()) {
                    Text(text = "Click Me To Check IF you were RIGHT")
                }
            }
        }
    }
}