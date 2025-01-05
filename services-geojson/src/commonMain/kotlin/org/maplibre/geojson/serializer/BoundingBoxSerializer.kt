package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.maplibre.geojson.BoundingBox
import org.maplibre.geojson.Point

/**
 * Internal serializer/deserializer that is converting a [BoundingBox] object into
 * a double array (aka GeoJSON BoundingBox).
 */
internal class BoundingBoxSerializer : KSerializer<BoundingBox> {
    private val delegateSerializer = DoubleArraySerializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("BoundingBox", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: BoundingBox) {
        val data = listOfNotNull(
            value.southwest.longitude,
            value.southwest.latitude,
            value.southwest.altitude,
            value.northeast.longitude,
            value.northeast.latitude,
            value.northeast.altitude
        )
        encoder.encodeSerializableValue(delegateSerializer, data.toDoubleArray())
    }

    override fun deserialize(decoder: Decoder): BoundingBox {
        val array = decoder.decodeSerializableValue(delegateSerializer)
        return BoundingBox(
            southwest = Point(
                longitude = array[0],
                latitude = array[1],
                altitude = array.getOrNull(4)
            ),
            northeast = Point(
                longitude = array[2],
                latitude = array[3],
                altitude = array.getOrNull(5)
            ),
        )
    }
}