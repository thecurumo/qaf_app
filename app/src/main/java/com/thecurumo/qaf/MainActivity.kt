package com.thecurumo.qaf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.thecurumo.qaf.ui.navigation.QafNavGraph
import com.thecurumo.qaf.ui.theme.QafTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QafApp()
        }
    }
}

@Composable
fun QafApp() {
    QafTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            QafNavGraph(navController = navController)
        }
    }
}