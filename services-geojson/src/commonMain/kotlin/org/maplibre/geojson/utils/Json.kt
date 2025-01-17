package org.maplibre.geojson.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

/**
 * Internal JSON instance used for serialization and deserialization of GeoJSON models
 */
@OptIn(ExperimentalSerializationApi::class)
internal val json = Json {
    classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS

    // Encode
    encodeDefaults = true
    explicitNulls = false

    // Decode
    ignoreUnknownKeys = true
    isLenient = true
}