package com.example.fomonator

import android.util.Log
import org.json.JSONObject
import kotlin.math.absoluteValue
import kotlin.random.Random

interface LLMClassifier {
    //should return 0-10
    suspend fun urgencify(fomoNotification: FomoNotification): Int?
    fun urgencify(fomoNotifications: Collection<FomoNotification>): Int
}

class LLamaClassifier : LLMClassifier {
    val prompt = """This GPT acts as an assistant to evaluate the urgency or importance of messages 
        |provided by the user. Every response is strictly a numeric value between 1 and 10, 
        |representing the urgency or importance factor of the input. It avoids providing 
        |explanations, feedback, or any extra commentary, focusing solely on returning 
        |the appropriate number based on the message content. When interpreting the urgency,  
        |it considers both direct and implied urgency from the user's tone, language, and content.
        |Anything that should be answered immediately is assigned a higher value than messages 
        |that can wait until later. The GPT responds in the form 'x', with x being 
        |the assigned urgency value.""".trimMargin()

    private fun generatePrompt(msg: String) = """$prompt\n Message is "$msg""""

    override suspend fun urgencify(fomoNotification: FomoNotification): Int? {
        val response = LLamaClient.ask(generatePrompt(fomoNotification.msg!!))
        Log.d("LLamaClassifier", "Response from Ollama $response")
        val urgency = response?.toInt()
        Log.d("LLamaClassifier", "Urgency $urgency")
        return urgency
    }

    override fun urgencify(fomoNotifications: Collection<FomoNotification>): Int {
        TODO("Not yet implemented")
    }

}

class MockLLMClassifier : LLMClassifier {
    override suspend fun urgencify(fomoNotification: FomoNotification): Int {
        return Random.nextInt().absoluteValue % 10
    }

    override fun urgencify(fomoNotifications: Collection<FomoNotification>): Int {
        return Random.nextInt().absoluteValue % 10
    }
}