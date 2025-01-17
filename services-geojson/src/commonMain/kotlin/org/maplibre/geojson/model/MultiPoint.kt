package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointDoubleArraySerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

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
 * ```json
 * {
 *   "TYPE": "MultiPoint",
 *   "coordinates": [
 *     [100.0, 0.0],
 *     [101.0, 1.0]
 *   ]
 * }
 * ```
 *
 * Look over the [Point] documentation to get more
 * information about formatting your list of point objects correctly.
 *
 * @param coordinates a list of {@link Point}s which make up the LineString geometry
 * @param bbox   optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("MultiPoint")
open class MultiPoint
@JvmOverloads
constructor(
    override val coordinates: List<@Serializable(with = PointDoubleArraySerializer::class) Point>,
    override val bbox: BoundingBox? = null,
) : CoordinateContainer<List<Point>> {

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this MultiPoint geometry
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiPoint

        if (coordinates != other.coordinates) return false
        if (bbox != other.bbox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coordinates.hashCode()
        result = 31 * result + (bbox?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "MultiPoint(coordinates=$coordinates, bbox=$bbox)"
    }

    companion object {

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiPoint object from scratch it is better to use the constructor.
         * For a valid MultiPoint to exist, it must have at least 2 coordinate entries.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson MultiPoint
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiPoint = json.decodeFromString(jsonString)
    }
}
