package com.livelike.engagementsdksample.widget.model

data class LiveLikeWidgetOption(
    val id: String,
    val description: String = "",
    val isCorrect: Boolean = false,
    val imageUrl: String? = "",
    var percentage: Int? = 0
)