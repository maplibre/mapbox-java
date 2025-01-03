package org.maplibre.geojson

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val module = SerializersModule {
    polymorphic(Geometry::class) {
        subclass(Point::class)
        subclass(LineString::class)
//        defaultDeserializer { Point.serializer() }
//        defaultDeserializer { LineString.serializer() }
//        subclass(MultiPoint::class)
//        subclass(MultiLineString::class)
//        subclass(Polygon::class)
//        subclass(MultiPolygon::class)
//        subclass(GeometryCollection::class)
    }
}

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
//    serializersModule = module
//    useArrayPolymorphism = true
//    classDiscriminatorMode = ClassDiscriminatorMode.NONE
    classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS

    // Encode
    encodeDefaults = true
    explicitNulls = false

    // Decode
    ignoreUnknownKeys = true
    isLenient = true
}