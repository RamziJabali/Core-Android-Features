package ramzi.eljabali.androidcore.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

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
        when (text) {
            "1234" -> {
                _userStateFlow.update {
                    it.copy(
                        didUserGetTextRightPersonal = true,
                        didUserGetTextRightWork = false,
                        didUserClickActionSnackbar = false,
                        didUserClickDismissSnackbar = false
                    )
                }
            }
            "4321" -> {
                _userStateFlow.update {
                    it.copy(
                        didUserGetTextRightPersonal = false,
                        didUserGetTextRightWork = true,
                        didUserClickActionSnackbar = false,
                        didUserClickDismissSnackbar = false
                    )
                }
            }
            else -> {
                _userStateFlow.update {
                    it.copy(
                        didUserGetTextRightPersonal = false,
                        didUserGetTextRightWork = false,
                        didUserClickActionSnackbar = false,
                        didUserClickDismissSnackbar = false,
                        failureMessage = "You got it next time"
                    )
                }
            }
        }
    }

    fun userClickedActionOnSnackbar(){
        _userStateFlow.update {
            it.copy(
                didUserClickActionSnackbar = true,
                didUserClickDismissSnackbar = false,
                showRationale = false
            )
        }
    }

    fun userClickedDismissOnSnackbar(){
        _userStateFlow.update {
            it.copy(
                didUserClickActionSnackbar = false,
                didUserClickDismissSnackbar = true
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

    fun resetSnackBar() {
        _userStateFlow.update {
            it.copy(
                didUserClickActionSnackbar = false,
                didUserClickDismissSnackbar = false,
                showRationale = false
            )
        }
    }
}