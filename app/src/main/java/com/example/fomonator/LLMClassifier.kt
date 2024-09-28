package com.example.fomonator

import kotlin.random.Random

interface LLMClassifier {
    //should return 0-10
    fun urgencify(fomoNotification: FomoNotification): Int
}

class MockLLMClassifier : LLMClassifier {
    override fun urgencify(fomoNotification: FomoNotification): Int {
        return Random.nextInt() % 10
    }
}