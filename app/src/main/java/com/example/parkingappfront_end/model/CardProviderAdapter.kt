package com.example.parkingappfront_end.model

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class CardProviderAdapter : TypeAdapter<CardProvider>() {
    override fun write(out: JsonWriter, value: CardProvider?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.name) // Scrive solo il nome dell'enum
        }
    }

    override fun read(`in`: JsonReader): CardProvider? {
        val name = `in`.nextString()
        return CardProvider.valueOf(name) // Deserializza usando il nome dell'enum
    }
}
