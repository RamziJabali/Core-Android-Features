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

## Foreground Service with persistant notification

### how to start a foreground service

1. Create Foreground Service class
 
```
class ForegroundService : Service() { ... }
```

2. Add to manifest
    - You will have to Define it in the manifest
    - Your foreground service can be of a different type, mine is a location foreground service

```
     <service
            android:foregroundServiceType="location"
            android:exported="false"
            android:name=".loactionservice.ForegroundService">
     </service>
```

3. Override onStartCommand function

```
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
                Log.i("ForegroundService::Class", "Starting service")
                start()
            }
            Actions.STOP.toString() -> {
                Log.i("ForegroundService::Class", "Stopping service")
                stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

```

4. Create notifications and notification channels
    - Notification channels can be created in a seprate class and initialized on onCreate()
    - My notification includes Pending Intents


    * The first PendingIntent(Action that will take place at a later time) makes it so if the user clicks on the notification the MainActivity::class.java is started
        * `PendingIntent.getActivity()` ~ It is used to create a PendingIntent that will start an activity when triggered.
        * This happens because we pass it an Intent `Intent(this, MainActivity::class.java)` to start the MainActivity::class
        * `PendingIntent.FLAG_IMMUTABLE` This flag indicates that the PendingIntent should be immutable, meaning that its configuration cannot be changed after it is created. 

```
private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }
```

    * The Second PendingIntent(Action that will take place at a later time) makes it so if the user clicks on the notification action button the service is stopped
        * `PendingIntent.getService()` ~ Similar to getActivity(), it is used to create a PendingIntent, but instead of starting an activity, it starts a service when triggered. 
        * This happens because we pass it an Intent `Intent(this, ForegroundService::class.java).apply {action = Actions.STOP.name}` to start the ForegroundService::class.java
        * `PendingIntent.FLAG_IMMUTABLE` This flag indicates that the PendingIntent should be immutable, meaning that its configuration cannot be changed after it is created. 
        * Recall `onStartCommand()` contains an expression that evaluates what is passed into the intent

```
    private val stopServicePendingIntent: PendingIntent by lazy {
        PendingIntent.getService(
            this, 0, Intent(this, ForegroundService::class.java).apply {
                action = Actions.STOP.name
            },
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
```

```
private fun getNotification() =
        NotificationCompat.Builder(applicationContext, CHANNEL_ID_1)
            .setSmallIcon(R.mipmap.just_jog_icon_foreground)
            .setContentTitle(ContextCompat.getString(applicationContext, R.string.just_jog))
            .setContentText(ContextCompat.getString(applicationContext, R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setStyle(NotificationCompat.BigTextStyle())
            // Set the intent that fires when the user taps the notification.
            .setContentIntent(pendingIntent)
            .addAction(
                R.mipmap.cross_monochrome,
                getString(R.string.stop_jog),
                stopServicePendingIntent
            )
            .build()
```

