package com.example.cropcart.prompt

object PromptRepo {
    enum class Status(val value: Int) {
        NORMAL(0),
        ERROR(1),
        SUCCESS(1),
        FAILURE(1),
    }

    enum class AIAttachment(val value: Int){
        CART(0)
    }
}