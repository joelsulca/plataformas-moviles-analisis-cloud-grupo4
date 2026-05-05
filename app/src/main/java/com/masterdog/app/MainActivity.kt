package com.masterdog.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.masterdog.app.ui.navigation.MasterDogNavGraph
import com.masterdog.app.ui.theme.MasterDogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasterDogTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MasterDogNavGraph()
                }
            }
        }
    }
}
