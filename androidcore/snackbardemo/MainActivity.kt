package ramzi.eljabali.androidcore.snackbardemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ramzi.eljabali.androidcore.ui.theme.AndroidCoreTheme
import ramzi.eljabali.androidcore.snackbardemo.ScaffoldForSnackBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCoreTheme {
                ScaffoldForSnackBar()
            }
        }
    }
}
