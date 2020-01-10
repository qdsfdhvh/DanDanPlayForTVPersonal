package com.seiko.core.di

import com.google.gson.*
import org.koin.dsl.module
import java.lang.reflect.Type

internal val gsonModule = module {

    single { createGson() }

}

private fun createGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, BooleanDefaultAdapter())
        .registerTypeAdapter(Int::class.java, IntegerDefaultAdapter())
        .registerTypeAdapter(Long::class.java, LongDefaultAdapter())
        .registerTypeAdapter(Double::class.java, DoubleDefaultAdapter())
        .disableHtmlEscaping()
        .create()
}

class BooleanDefaultAdapter : JsonSerializer<Boolean>, JsonDeserializer<Boolean> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Boolean {
        try {
            if (json == null || json.asString == "" || json.asInt == 0) {
                return false
            } else if (json.asInt == 1) {
                return true
            }
        } catch (ignore: Exception) {
        }

        try {
            return json?.asBoolean ?: false
        } catch (e: Exception) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(src: Boolean?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}

class IntegerDefaultAdapter : JsonSerializer<Int>, JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        try {
            // 定义为int类型,如果后台返回""或者null,则返回0
            if (json == null || json.asString == "" || json.asString == null) {
                return 0
            }
        } catch (ignore: Exception) {
        }

        try {
            return json?.asInt ?: 0
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(src: Int?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}

class LongDefaultAdapter : JsonSerializer<Long>, JsonDeserializer<Long> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        try {
            // 定义为long类型,如果后台返回""或者null,则返回0
            if (json == null || json.asString == "" || json.asString == null) {
                return 0
            }
        } catch (ignore: Exception) {
        }

        try {
            return json?.asLong ?: 0
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(src: Long?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}

class DoubleDefaultAdapter : JsonSerializer<Double>, JsonDeserializer<Double> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Double {
        try {
            // 定义为double类型,如果后台返回""或者null,则返回0.00
            if (json == null || json.asString == "" || json.asString == null) {
                return 0.00
            }
        } catch (ignore: Exception) {
        }

        try {
            return json?.asDouble ?: 0.00
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(src: Double?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}