package com.thecurumo.qaf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.thecurumo.qaf.data.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.LaunchedEffect

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
    val context = LocalContext.current
    var poetCount by remember { mutableStateOf(-1) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                val db = DatabaseProvider.getDatabase(context)
                val poets = db.poetDao().getAllPoets()
                poetCount = poets.size
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "خطای نامشخص"
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    errorMessage != null -> {
                        Text(text = "خطا: $errorMessage")
                    }
                    poetCount == -1 -> {
                        CircularProgressIndicator()
                    }
                    else -> {
                        Text(text = "قاف — تعداد شاعران: $poetCount")
                    }
                }
            }
        }
    }
}