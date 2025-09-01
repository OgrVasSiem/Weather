package com.kamaz.weather.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PairSerializer : KSerializer<Pair<String, String>> {
    override val descriptor = PrimitiveSerialDescriptor("Pair", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Pair<String, String>) {
        encoder.encodeString("${value.first}|${value.second}")
    }

    override fun deserialize(decoder: Decoder): Pair<String, String> {
        val decoded = decoder.decodeString()
        val parts = decoded.split("|")
        return parts.getOrNull(0).orEmpty() to parts.getOrNull(1).orEmpty()
    }
}
