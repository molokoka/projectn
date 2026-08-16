package molokoka.project.n

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import molokoka.project.n.log.log

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        log(TAG, "onCreate savedState=${if (savedInstanceState == null) "absent" else "restored"}")

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        log(TAG, "onDestroy changingConfigurations=$isChangingConfigurations")
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
