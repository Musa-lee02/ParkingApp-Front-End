package com.example.parkingappfront_end.model

import com.example.parkingappfront_end.model.domain.PaymentMethodType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class PaymentMethodTypeAdapter : TypeAdapter<PaymentMethodType>() {
    override fun write(out: JsonWriter, value: PaymentMethodType?) {
        if (value == null) {
            out.nullValue()
        } else {
            println("PaymentMethodTypeAdapter: write: value.name = ${value.name}")
            out.value(value.name) // Scrive solo il nome dell'enum, non il displayName
        }
    }

    override fun read(`in`: JsonReader): PaymentMethodType? {
        val name = `in`.nextString()
        println("PaymentMethodTypeAdapter: read: name = $name")
        return PaymentMethodType.valueOf(name) // Deserializza usando il nome dell'enum
    }
}
