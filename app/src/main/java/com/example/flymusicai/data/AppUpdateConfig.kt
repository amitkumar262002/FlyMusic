package com.example.flymusicai.data

import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateConfig(
    val version_name: String,
    val title: String,
    val message: String,
    val update_now_text: String,
    val later_text: String,
    val show_later_button: Boolean,
    val update_link: String
)
