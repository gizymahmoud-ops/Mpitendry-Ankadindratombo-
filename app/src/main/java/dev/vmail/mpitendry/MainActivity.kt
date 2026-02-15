package dev.vmail.mpitendry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.vmail.mpitendry.ui.AppRoot
import dev.vmail.mpitendry.ui.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}