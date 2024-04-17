package ramzi.eljabali.androidcore.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckTextViewModel : ViewModel() {
    data class UserState(
        val didUserGetTextRightPersonal: Boolean = false,
        val didUserGetTextRightWork: Boolean = false,
        val didUserClickActionSnackbar: Boolean = false,
        val didUserClickDismissSnackbar: Boolean = false,
        val showRationale: Boolean = false,
        val failureMessage: String = "",
        val rationaleMessage: String = "Please accept Post Notification Permissions to get notifications!"
    )

    private val _userStateFlow = MutableStateFlow(UserState())
    val userStateFlow: StateFlow<UserState> = _userStateFlow

    fun checkIfUserTextMatches(text: String) {
        viewModelScope.launch {
            when (text) {
                "1234" -> {
                    Log.i("Log.d", "Personal text Correct")
                    _userStateFlow.value =
                        _userStateFlow.value.copy(
                            didUserGetTextRightPersonal = true,
                            didUserGetTextRightWork = false,
                            didUserClickActionSnackbar = false,
                            didUserClickDismissSnackbar = false,
                            failureMessage = ""
                        )
                }

                "4321" -> {
                    Log.d("Log.d", "Work text correct")
                    _userStateFlow.value =
                        _userStateFlow.value.copy(
                            didUserGetTextRightPersonal = false,
                            didUserGetTextRightWork = true,
                            didUserClickActionSnackbar = false,
                            didUserClickDismissSnackbar = false
                        )
                }

                else -> {
                    Log.d("Log.d", "Incorrect text")
                    _userStateFlow.value =
                        _userStateFlow.value.copy(
                            didUserGetTextRightPersonal = false,
                            didUserGetTextRightWork = false,
                            didUserClickActionSnackbar = false,
                            didUserClickDismissSnackbar = false,
                            showRationale = false,
                            failureMessage = "You got it next time"
                        )
                }
            }
        }
    }

    fun userClickedActionOnSnackBar() {
        _userStateFlow.value =
            _userStateFlow.value.copy(
                didUserClickActionSnackbar = true,
                didUserClickDismissSnackbar = false,
                showRationale = false
            )
    }
    fun userClickedDismissSnackBar(){
        _userStateFlow.update {
            it.copy(
                didUserGetTextRightPersonal = false,
                didUserGetTextRightWork = false,
                didUserClickActionSnackbar = false,
                didUserClickDismissSnackbar = true,
                showRationale = false
            )
        }
    }

    fun showRationale() {
        _userStateFlow.update {
            it.copy(
                showRationale = true,
            )
        }
    }

    fun resetViewState() {
        Log.d("Log.d", "Reset ViewState")
        _userStateFlow.value =
            UserState()
    }
}