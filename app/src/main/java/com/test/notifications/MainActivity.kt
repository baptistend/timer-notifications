package com.test.notifications

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionHandler.checkAndRequestPermissions(this) {
            TimerNotification.createNotificationChannel(this)
            setContent {
                NotificationApp { TimerNotification.showNotification(this) }
            }
        }
    }



    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        NotificationApp {}
    }
}
