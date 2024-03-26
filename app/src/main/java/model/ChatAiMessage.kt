package model

data class ChatAiMessageResponse(
    val code: Int,
    val msg: String,
    val data : Message
)
data class Message(
    val msg: String
)