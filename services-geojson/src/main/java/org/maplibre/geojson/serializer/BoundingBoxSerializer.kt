package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.maplibre.geojson.BoundingBox
import org.maplibre.geojson.Point

class BoundingBoxSerializer : KSerializer<BoundingBox> {
    private val delegateSerializer = DoubleArraySerializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("BoundingBox", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: BoundingBox) {
        //TODO:  fabi755 more then lat long can be in the array
        val data = doubleArrayOf(
            value.southwest.longitude,
            value.southwest.latitude,
            value.northeast.longitude,
            value.northeast.latitude
        )
        encoder.encodeSerializableValue(delegateSerializer, data)
    }

    override fun deserialize(decoder: Decoder): BoundingBox {
        val array = decoder.decodeSerializableValue(delegateSerializer)
        return BoundingBox(
            southwest = Point(
                longitude = array[0],
                latitude = array[1]
            ),
            northeast = Point(
                longitude = array[2],
                latitude = array[3]
            ),
        )
    }
}