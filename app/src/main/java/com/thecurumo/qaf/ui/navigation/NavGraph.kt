package com.thecurumo.qaf.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thecurumo.qaf.data.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun QafNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        composable(NavRoutes.Home.route) {
            HomeTestScreen()
        }

        composable(NavRoutes.Poets.route) {
            PlaceholderScreen(title = "دانشنامه سخنوران")
        }

        composable(
            route = NavRoutes.PoetDetail.route,
            arguments = listOf(navArgument("poetId") { type = NavType.IntType })
        ) { backStackEntry ->
            val poetId = backStackEntry.arguments?.getInt("poetId") ?: -1
            PlaceholderScreen(title = "شناسنامه شاعر (id=$poetId)")
        }

        composable(NavRoutes.Works.route) {
            PlaceholderScreen(title = "گنجینه آثار")
        }

        composable(
            route = NavRoutes.WorkDetail.route,
            arguments = listOf(navArgument("catId") { type = NavType.IntType })
        ) { backStackEntry ->
            val catId = backStackEntry.arguments?.getInt("catId") ?: -1
            PlaceholderScreen(title = "جزئیات اثر (catId=$catId)")
        }

        composable(
            route = NavRoutes.PoemReader.route,
            arguments = listOf(navArgument("poemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val poemId = backStackEntry.arguments?.getInt("poemId") ?: -1
            PlaceholderScreen(title = "خوانش شعر (poemId=$poemId)")
        }

        composable(NavRoutes.Search.route) {
            PlaceholderScreen(title = "نتایج جستجو")
        }

        composable(NavRoutes.Profile.route) {
            PlaceholderScreen(title = "بایگانی و نشان‌ها")
        }

        composable(NavRoutes.About.route) {
            PlaceholderScreen(title = "درباره قاف")
        }
    }
}

@Composable
private fun HomeTestScreen() {
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            errorMessage != null -> Text(text = "خطا: $errorMessage")
            poetCount == -1 -> CircularProgressIndicator()
            else -> Text(text = "صفحه اصلی — تعداد شاعران: $poetCount")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}