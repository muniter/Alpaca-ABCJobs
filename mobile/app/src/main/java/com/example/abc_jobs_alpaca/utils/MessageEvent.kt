package com.example.abc_jobs_alpaca.utils

data class MessageEvent(val type: MessageType, val content: Any)

enum class MessageType {
    SUCCESS,
    ERROR
}
