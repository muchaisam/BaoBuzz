package com.msdc.baobuzz.appnavigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Competition : Screen("competition")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}