package com.example.cropcart.ai

data class AIChatMessage(
    val isUser: Boolean,
    var msg: String,
    var isBlinking: Boolean = false,
    var status: AIRepo.MessageStatus = AIRepo.MessageStatus.NORMAL,
)