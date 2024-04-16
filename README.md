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

