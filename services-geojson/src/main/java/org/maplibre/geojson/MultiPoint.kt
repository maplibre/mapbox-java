package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointDoubleArraySerializer

/**
 * A MultiPoint represents two or more geographic points that share a relationship and is one of the
 * seven geometries found in the GeoJson spec.
 *
 * This adheres to the RFC 7946 internet standard
 * when serialized into JSON. When deserialized, this class becomes an immutable object which should
 * be initiated using its static factory methods. The list of points must be equal to or greater
 * than 2.
 *
 * A sample GeoJson MultiPoint's provided below (in it's serialized state).
 * <pre>
 * {
 * "TYPE": "MultiPoint",
 * "coordinates": [
 * [100.0, 0.0],
 * [101.0, 1.0]
 * ]
 * }
</pre> *
 * Look over the [Point] documentation to get more
 * information about formatting your list of point objects correctly.
 *
 * @since 1.0.0
 */
@Serializable
@SerialName("MultiPoint")
data class MultiPoint
@JvmOverloads
constructor(
    override val coordinates: List<@Serializable(with = PointDoubleArraySerializer::class) Point>,
    override val bbox: BoundingBox? = null,
//    override val type: String = TYPE
) : CoordinateContainer<List<Point>> {

    override fun toJson() = json.encodeToString(this)

    companion object {
        const val TYPE = "MultiPoint"

        @JvmStatic
        fun fromJson(jsonString: String): MultiPoint = json.decodeFromString(jsonString)
    }
}
