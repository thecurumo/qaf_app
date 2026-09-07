package com.thecurumo.qaf.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thecurumo.qaf.ui.screens.home.HomeScreen

@Composable
fun QafNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        composable(NavRoutes.Home.route) {
            HomeScreen(
                onPoetClick = { poetId ->
                    navController.navigate(NavRoutes.PoetDetail.createRoute(poetId))
                }
            )
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
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}