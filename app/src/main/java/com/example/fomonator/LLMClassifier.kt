package com.example.fomonator

import kotlin.math.absoluteValue
import kotlin.random.Random

interface LLMClassifier {
    //should return 0-10
    fun urgencify(fomoNotification: FomoNotification): Int
    fun urgencify(fomoNotifications: Collection<FomoNotification>): Int
}

class MockLLMClassifier : LLMClassifier {
    override fun urgencify(fomoNotification: FomoNotification): Int {
        return Random.nextInt().absoluteValue % 10
    }

    override fun urgencify(fomoNotifications: Collection<FomoNotification>): Int {
        return Random.nextInt().absoluteValue % 10
    }
}