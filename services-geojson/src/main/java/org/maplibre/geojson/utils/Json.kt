package org.maplibre.geojson.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS

    // Encode
    encodeDefaults = true
    explicitNulls = false

    // Decode
    ignoreUnknownKeys = true
    isLenient = true
}