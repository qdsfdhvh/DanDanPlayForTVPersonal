package com.seiko.common.di

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsonModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(NullStringAdapter)
            .add(NullLongAdapter)
            .add(NullIntAdapter)
            .add(NullDoubleAdapter)
            .add(NullBooleanAdapter)
            .build()
    }

}

object NullStringAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): String {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextString()
        }
        reader.nextNull<Unit>()
        return ""
    }
}

object NullLongAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Long {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextLong()
        }
        reader.nextNull<Unit>()
        return 0
    }
}

object NullIntAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Int {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextInt()
        }
        reader.nextNull<Unit>()
        return 0
    }
}

object NullDoubleAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Double {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextDouble()
        }
        reader.nextNull<Unit>()
        return 0.0
    }
}

object NullBooleanAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Boolean {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextBoolean()
        }
        reader.nextNull<Unit>()
        return false
    }
}