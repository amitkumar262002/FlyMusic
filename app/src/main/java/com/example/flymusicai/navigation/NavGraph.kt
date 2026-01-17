package com.example.flymusicai.navigation

/** Navigation routes for the app */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Main : Screen("main")
    object Home : Screen("home")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object Equalizer : Screen("equalizer")
    object MusicPlayer : Screen("music_player/{songId}") {
        fun createRoute(songId: String) = "music_player/$songId"
    }
    object PlaylistDetail : Screen("playlist_detail/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist_detail/$playlistId"
    }
}

/** Bottom navigation items */
sealed class BottomNavItem(val route: String, val icon: String, val title: String) {
    object Home : BottomNavItem(Screen.Home.route, "home", "Home")
    object Search : BottomNavItem(Screen.Search.route, "search", "Search")
    object Favorites : BottomNavItem(Screen.Favorites.route, "favorite", "Favorites")
    object Settings : BottomNavItem(Screen.Settings.route, "settings", "Settings")
}
