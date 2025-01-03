package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedClassSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.maplibre.geojson.BoundingBox
import org.maplibre.geojson.Geometry
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

@OptIn(InternalSerializationApi::class)
class GeometrySerializer : KSerializer<Geometry> {

    private val delegateSerializer = SealedClassSerializer(
        "Geometry",
        Geometry::class,
        arrayOf(
            Point::class,
            LineString::class
        ),
        arrayOf(
            Point.serializer(),
            LineString.serializer()
        )
    )

    override val descriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Geometry) {
        return delegateSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Geometry {
        return delegateSerializer.deserialize(decoder)
    }
}