package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.maplibre.geojson.model.Point

/**
 * Internal serializer/deserializer that is converting a [Point] object into a
 * double array (aka GeoJSON Position).
 */
internal class PointDoubleArraySerializer : KSerializer<Point> {
    private val delegateSerializer = DoubleArraySerializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("Point", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Point) {
        val data = listOfNotNull(
            value.longitude,
            value.latitude,
            value.altitude
        )

        encoder.encodeSerializableValue(delegateSerializer, data.toDoubleArray())
    }

    override fun deserialize(decoder: Decoder): Point {
        val array = decoder.decodeSerializableValue(delegateSerializer)
        return Point(longitude = array[0], latitude = array[1], altitude = array.getOrNull(2))
    }
}