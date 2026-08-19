package com.kitutu.matokeo.data

import androidx.room.TypeConverter
import org.json.JSONObject

/**
 * Room hairuhusu Map<String, Int> moja kwa moja kama safu (column), hivyo
 * tunaihifadhi kama maandishi ya JSON, mfano: {"KISWAHILI":70,"BUSINESS STUDIES":80}
 */
class ScoresConverter {

    @TypeConverter
    fun fromScores(scores: Map<String, Int>): String {
        val obj = JSONObject()
        scores.forEach { (subject, value) -> obj.put(subject, value) }
        return obj.toString()
    }

    @TypeConverter
    fun toScores(json: String): Map<String, Int> {
        if (json.isBlank()) return emptyMap()
        return try {
            val obj = JSONObject(json)
            val map = mutableMapOf<String, Int>()
            obj.keys().forEach { key -> map[key] = obj.optInt(key, 0) }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
