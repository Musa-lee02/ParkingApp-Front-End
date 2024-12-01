package com.example.parkingappfront_end.utils

import org.json.JSONException
import org.json.JSONObject

fun ErrorMessageParser(errorBody: String?): String {
    if (!errorBody.isNullOrEmpty()) {
        try {
            val jsonObject = JSONObject(errorBody)
            val message = jsonObject.optString("message", "No message available.")
            return "Error: $message"
        } catch (e: JSONException) {
            return "Unexpected Error: Unable to parse error message."
        }
    } else {
        return "Unexpected Error: No error details provided."
    }
}