package com.thecurumo.qaf.ui.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Poets : NavRoutes("poets")
    object PoetDetail : NavRoutes("poet_detail/{poetId}") {
        fun createRoute(poetId: Int) = "poet_detail/$poetId"
    }
    object Works : NavRoutes("works")
    object WorkDetail : NavRoutes("work_detail/{catId}") {
        fun createRoute(catId: Int) = "work_detail/$catId"
    }
    object PoemReader : NavRoutes("poem_reader/{poemId}") {
        fun createRoute(poemId: Int) = "poem_reader/$poemId"
    }
    object Search : NavRoutes("search")
    object Profile : NavRoutes("profile")
    object About : NavRoutes("about")
}
