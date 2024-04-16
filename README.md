# Android Features (Kotlin + Compose)

## Snack Bar 

This example shows how to display a SnackBar from a floating action button

```
@Composable
fun ScaffoldForSnackBar() {
    val coroutineScope = rememberCoroutineScope() // To launch your corotines
    val snackBarHostState = remember { SnackbarHostState() } // snack bar state
    val textState = remember {  mutableStateOf("") } // Text state
    Log.i("Scaffold", "In Scaffold") 
    Scaffold(
        // Snack bar parameter 
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        // floating action button parameter 
        floatingActionButton = {
            //defining floating action button 
            ExtendedFloatingActionButton(
                text = { Text("Show snackbar") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                // onClick listener
                onClick = {
                    coroutineScope.launch {
                        val result = snackBarHostState
                            .showSnackbar(
                                message = "Snackbar",
                                actionLabel = "Action",
                                // Defaults to SnackbarDuration.Short
                                duration = SnackbarDuration.Indefinite,
                                withDismissAction = true
                            )
                        // Logs user clicks within snackbar
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                Log.i("Scaffold", "In Action Performed")
                                textState.value = "Showing Snackbar"
                                snackBarHostState.showSnackbar(result.name)
                                /* Handle snackbar action performed */
                            }

                            SnackbarResult.Dismissed -> {
                                /* Handle snackbar dismissed */
                                Log.i("Scaffold", "In Dismissed")
                                textState.value = "Dismissed Snackbar"
                            }
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Text(text = textState.value, modifier = Modifier.fillMaxSize())
        }
    }
}
```

This example is done showcasing the use of SnackBar action button when handling notifications

```
@Composable
fun SimpleTextBoxView(
    checkText: (text: String) -> Unit,
    userClickedActionSnackbar: () -> Unit,
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
                        Log.i("snackbar state", "Displaying SnackBar")
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
                                Log.i("snackbar state", "In Action Performed")
                                userClickedActionSnackbar()

                            }

                            SnackbarResult.Dismissed -> {
                                /* Handle snackbar dismissed */
                                Log.i("snackbar state", "In Dismissed")
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
```

