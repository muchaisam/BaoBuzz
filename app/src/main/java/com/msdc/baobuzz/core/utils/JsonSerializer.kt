package com.msdc.baobuzz.core.utils

import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/** JSON serialization utility for caching complex objects */
@Singleton
class JsonSerializer @Inject constructor(private val gson: Gson) {

    /** Serialize any object to JSON string */
    fun <T> serialize(obj: T): String {
        return gson.toJson(obj)
    }

    /** Deserialize JSON string to specific type */
    fun <T> deserialize(json: String, clazz: Class<T>): T? {
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            null
        }
    }

    /** Deserialize JSON string to specific type using reified inline */
    inline fun <reified T> deserialize(json: String): T? {
        return deserialize(json, T::class.java)
    }

    /** Deserialize JSON string to list of specific type */
    fun <T> deserializeList(json: String, clazz: Class<T>): List<T>? {
        return try {
            val listType =
                com.google.gson.reflect.TypeToken.getParameterized(List::class.java, clazz).type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    /** Deserialize JSON string to list of specific type using reified inline */
    inline fun <reified T> deserializeList(json: String): List<T>? {
        return deserializeList(json, T::class.java)
    }
}
