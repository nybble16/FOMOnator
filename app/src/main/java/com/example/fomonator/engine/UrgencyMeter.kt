package com.example.fomonator.engine

interface UrgencyMeter {
    fun calculateUrgency(message: Message): Int
}

data class Message(
    val sender: String,
    val content: String,
    val timestamp: Long // Can represent the message time in milliseconds
)

class SenderUrgencyMeter(private val importantSenders: Set<String>) : UrgencyMeter {
    override fun calculateUrgency(message: Message): Int {
        return if (importantSenders.contains(message.sender)) {
            10 // High urgency for important senders
        } else {
            5  // Normal urgency
        }
    }
}
