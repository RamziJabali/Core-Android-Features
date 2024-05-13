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
    * You can define an expression to handle the different actions tht are permitted within your service
    * In this case I have an actions enum defined with the service `enum class Actions { START, STOP}`

```
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.name -> {
                Log.i("ForegroundService::Class", "Starting service")
                start()
            }
            Actions.STOP.name -> {
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

5. Define your logic that is used when your service is started
    * Using a LocationManager and LocationListener
      
```
private val locationManager by lazy {
        ContextCompat.getSystemService(application, LocationManager::class.java) as LocationManager
    }

    private val locationListener: LocationListener by lazy {
        LocationListener { location ->
            Log.d(
                "ForegroundService::Class",
                "Time:${ZonedDateTime.now()}\nLatitude: ${location.latitude}, Longitude:${location.longitude}"
            )
        }
    }
```


```
 private fun stop() {
        locationManager.removeUpdates(locationListener)
        stopSelf()
    }

    private fun start() {
        Log.i("ForegroundService::Class", "Checking permissions")
        for (permission in permissions.list) {
            val currentPermission = ContextCompat.checkSelfPermission(this, permission)
            if (currentPermission == PackageManager.PERMISSION_DENIED) {
                // Without these permissions the service cannot run in the foreground
                // Consider informing user or updating your app UI if visible.
                Log.d("ForegroundService::Class", "Permissions were not given, stopping service!")
                stopSelf()
            }
        }

        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID, // Cannot be 0
                getNotification(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                } else {
                    0
                },
            )
            // getting user location
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_REQUEST_INTERVAL_MS,
                0F,
                locationListener
            )
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                // App not in a valid state to start foreground service
                // (e.g. started from bg)
                Log.d("ForegroundService::Class", e.message.toString())
            }
        }
    }
```
7. You have now defined a general skeleton for your foreground service
    * You created a means to handle intents to start your foreground service
    * You created your own notification
    * You created pending intents to go with your notification
        * Starts MainActivity::Class.java and stops the foreground service    
    * You created logic that is executed when the service is started

  
## Creating Room Database

### Where to start?

There are three components to creating a Room database

1. Database class: holds the database and contains refrences of all DAO's
2. DAO(Data Access Object): Get's entites from the database and pushes changes back to the database
3. Entites: Get/Set field values

### Now that we have the basic understanding down, let's get started:

1. Create an entity for your table:
    * You have to annotate your data class with the `@Entity or @Entity("jog_entries")`
        * You are defining this data class as an entity for your DAO, and you can decide to label your table
        * You have to define a primary key for your entity via the `@PrimaryKey()` annotation, you can also have it so the keys are autogenrated.
        * You can optionally have different names for your columns by using `@ColumnInfo(name = "id")` which sets a custom names for a table and it's columns
           
```
@Entity("jog_entries")
data class JogEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo("jog_id")
    val jogId: Int,
    @ColumnInfo("date_time")
    val dateTime: String,
    @ColumnInfo("latitude")
    val latitude: Double,
    @ColumnInfo("longitude")
    val longitude: Double,
)
```

2. Create a DAO for your table

    * The DAO(data access object) provides methods that the rest of your application uses to interact with your table.
        * So in this case my `JogEntryDAO` has multiple methods that will be used to interact with my `jog_entries` table previously defined.
     
```
@Dao
interface JogEntryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUpdateWorkout(jogEntries: JogEntry)

    @Query("SELECT * FROM jog_entries")
    fun getAll(): Flow<List<JogEntry>>

    @Query("SELECT * FROM jog_entries WHERE date_time LIKE (:stringDate)")
    fun getByDate(stringDate: String): Flow<List<JogEntry>?>

    @Query("SELECT * FROM jog_entries WHERE date_time BETWEEN (:startDate) AND (:endDate)")
    fun getByRangeOfDates(startDate: String, endDate: String): Flow<List<JogEntry>>

    @Query("SELECT * FROM jog_entries WHERE jog_summary_id IS :jogId")
    fun getByID(jogId: Int): Flow<List<JogEntry?>>

    @Query("DELETE FROM jog_entries")
    fun deleteAll()
}
```

* You could have a usecase class that transforms values returned from your DAO and can transorm values required for your DAO.

3. Create your Database

    1. Class creation
        * Create an abstract class that extends `RoomDatabase`, ex: `abstract class AppDatabase : RoomDatabase()`
        * Annotate your class with the `@Database()` annotation and provide it with all your entities and a * version number: `@Database(entities = [JogEntryDAO::class], version = 1)`
    
    2. Within the class
        * Define abstract functions for all your available DAO's: `abstract fun jogEntryDao(): JogEntryDAO`
    
    3. Refrence your database for usage
        * Use Rooms `databaseBuilder()` to create your database object
        * ```
          val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "just-jog-database")
          .build()
          ```
        * I prefer to do this with dependency injection framework like Koin.

```
@Database(entities = [JogEntryDAO::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jogEntryDao(): JogEntryDAO
}
```

4. Use your database

```
val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "just-jog-database")
          .build()

val jogEntryDao = db.jogEntryDao()
val users: List<User> = jogEntryDao.getAll()

```

5. Example:

   * Mock View Model to simulate an actual application
   * Pass it a dao refrence or a usecase refrence to be able to make calls to and from the database
   * Using a `CompletableDeferred` makes it so I know that the process completed successfully or failed exceptionally

```
     class MockVM(private val dao: JogEntryDAO): ViewModel {
   
     fun addJog(jogEntry: JogEntry) {
        val deferred = CompletableDeferred<Unit>()

        val job = viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    dao.addUpdateWorkout(jogEntry)
                }
                deferred.complete(Unit)
                Log.d("MockVM::Class.java", "added Jog: $deferred")
            } catch (e: CancellationException) {
                Log.d("MockVM::Class.java", "Coroutine canceled: ${e.message}")
                // Handle cancellation if needed
            }
        }

        deferred.invokeOnCompletion { cause ->
            if (cause != null) {
                if (cause is CancellationException) {
                    Log.d("MockVM::Class.java", "Coroutine canceled: ${cause.message}")
                } else {
                    Log.e("MockVM::Class.java", "Coroutine failed with: $cause")
                }
                job.cancel() // Cancel the job explicitly
                }
            }
         }

      fun getAllJogs() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = viewModelScope.async { dao.getAll() }.await()
                list.collect {
                    Log.d("MockVM::Class.java", "getAllJogs: $it")
                    }
                }
            }
        }
     }
   
```

* Within your `MainActivity` you can have a refrence to your `MockVM` and call the `getAllJogs()` function
   
```
override fun onCreate(){
vm.getAllJogs()
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            JustJogDataBase::class.java, "just-jog-database"
        )
            .build()
        val vm = MockVM(db.jogEntryDao())
        vm.addJog(
            JogEntry(
                id = 0, jogSummaryId = 0, dateTime = "", latitude = 0.0, longitude = 0.0
            )
        )

        vm.getAllJogs()
}
```
  

### Result:

LogCat
```
2024-05-13 13:42:03.554 17739-17763 MockVM::Class.java      ramzi.eljabali.justjog               D  addJog: true
2024-05-13 13:42:03.850 17739-17763 MockVM::Class.java      ramzi.eljabali.justjog               D  getAllJogs: [JogEntry(id=1, jogSummaryId=0, dateTime=, latitude=0.0, longitude=0.0)]
```


