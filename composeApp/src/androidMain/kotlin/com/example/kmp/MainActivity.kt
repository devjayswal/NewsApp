package com.example.kmp

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.kmp.ui.theme.NewsAppTheme




class MainActivity : ComponentActivity() {

    var name: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Name", name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        name = savedInstanceState?.getString("Name") ?: ""
        
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { false }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        setContent {
            NewsAppTheme {
                MainScreen(
                    initalName = name,
                    onNameChange = { name = it }
                )
            }
        }
    }
}
