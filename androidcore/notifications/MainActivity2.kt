package ramzi.eljabali.androidcore.notifications

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ramzi.eljabali.androidcore.notifications.ui.theme.AndroidCoreTheme

class MainActivity2 : ComponentActivity() {
    private lateinit var notification: Notification
    private lateinit var viewModel: CheckTextViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = CheckTextViewModel()
        notification = Notification(this)
        notification.createNotificationChannels()
        checkPermissionStatus()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userStateFlow.collectLatest { userState ->
                    setContent {
                        AndroidCoreTheme {
                            // State hoisting
                            SimpleTextBoxView(
                                checkText = viewModel::checkIfUserTextMatches,
                                userClickedActionSnackBar = viewModel::userClickedActionOnSnackBar,
                                userClickedDismissSnackBar = viewModel::userClickedDismissSnackBar,
                                userState.failureMessage,
                                userState.showRationale,
                                userState.rationaleMessage
                            )
                            // Checking user input
                            if (userState.didUserGetTextRightPersonal || userState.didUserGetTextRightWork) {
                                Log.d("Log.d", "Correct Text Main")
                                // Checking permissions
                                if (checkPermissionStatus()) {
                                    notification.notifyUser(
                                        userState.didUserGetTextRightPersonal,
                                        userState.didUserGetTextRightWork
                                    )
                                }
                            }
                            if (userState.didUserClickActionSnackbar) {
                                openSettings()
                                viewModel.resetViewState()
                            }
                        }
                    }
                }
            }
        }
    }

    //Best way to check which permission status you are on
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionStatus(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Log.d", "Permission Already Granted")
                return true
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Log.d("Log.d", "Showing Request Rationale")
                viewModel.showRationale()
                return false
            }

            else -> {
                Log.d("Log.d", "Asking user to grant permission")
                return askForPermission()
            }
        }
    }

    // You are asking for permission
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun askForPermission(): Boolean {
        var isGrantedPermission = false
        val pushNotificationsResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Log.d("Log.d", "Permission has been denied")
            } else {
                Log.d("Log.d", "Permission has been granted")
            }
            isGrantedPermission = isGranted
        }
        pushNotificationsResult.launch(Manifest.permission.POST_NOTIFICATIONS)
        return isGrantedPermission
    }

    //Making an open detailed settings request
    private fun Activity.openSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }
}

