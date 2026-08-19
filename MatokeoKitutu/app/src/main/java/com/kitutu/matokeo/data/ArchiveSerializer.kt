package com.kitutu.matokeo.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * Hutumika kuhifadhi "picha" (snapshot) ya wanafunzi wote wakati wa kuhifadhi
 * matokeo ya mwezi (archive), na kuyasoma tena baadaye — bila kutegemea
 * maktaba za nje (Gson/kotlinx.serialization), kwa kutumia org.json iliyopo
 * tayari kwenye Android SDK.
 */
object ArchiveSerializer {

    fun serialize(students: List<StudentEntity>): String {
        val array = JSONArray()
        students.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("firstName", s.firstName)
            obj.put("middleName", s.middleName)
            obj.put("lastName", s.lastName)
            obj.put("sex", s.sex)
            obj.put("examNumber", s.examNumber)
            obj.put("school", s.school)
            val scoresObj = JSONObject()
            s.scores.forEach { (subject, value) -> scoresObj.put(subject, value) }
            obj.put("scores", scoresObj)
            array.put(obj)
        }
        return array.toString()
    }

    fun deserialize(json: String): List<StudentEntity> {
        if (json.isBlank()) return emptyList()
        val array = JSONArray(json)
        val list = mutableListOf<StudentEntity>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val scoresObj = obj.optJSONObject("scores") ?: JSONObject()
            val scores = mutableMapOf<String, Int>()
            scoresObj.keys().forEach { key -> scores[key] = scoresObj.optInt(key, 0) }
            list.add(
                StudentEntity(
                    id = obj.optString("id"),
                    firstName = obj.optString("firstName"),
                    middleName = obj.optString("middleName"),
                    lastName = obj.optString("lastName"),
                    sex = obj.optString("sex"),
                    examNumber = obj.optString("examNumber"),
                    school = obj.optString("school"),
                    scores = scores
                )
            )
        }
        return list
    }
}
