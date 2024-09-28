package com.example.fomonator

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object LLamaClient {

    private const val BASE_URL = "http://localhost:11434/api/generate"

    // This is a suspend function that can be called from a coroutine
    suspend fun ask(msg: String): String? {
        val jsonPayload = JSONObject()
            .put("model", "llama3.2:1b")
            .put("prompt", msg)
            .put("stream", false)

        return withContext(Dispatchers.IO) {
            Log.d("LLamaClient", "Sending request to $BASE_URL")
            val (request, response, result) = Fuel.post(BASE_URL)
                .header("Content-Type", "application/json")
                .body(jsonPayload.toString())
                .timeout(30_000)
                .timeoutRead(30_000)
                .responseString()

            handleResponse(response, result)
        }
    }

    private fun handleResponse(response: Response, result: Result<String, FuelError>): String? {
        return when (result) {
            is Result.Success -> {
                val jsonResponse = JSONObject(result.get())
                jsonResponse.optString("response")  // Adjust this based on the actual response structure
            }
            is Result.Failure -> {
                Log.d("LLamaClient","Error from ollama: ${result.getException()}")
                null
            }
        }
    }
}

