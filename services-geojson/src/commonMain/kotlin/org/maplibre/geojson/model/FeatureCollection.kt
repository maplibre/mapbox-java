package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * This represents a GeoJson Feature Collection which holds a list of [Feature] objects (when
 * serialized the feature list becomes a JSON array).
 *
 *
 * Note that the feature list could potentially be empty. Features within the list must follow the
 * specifications defined inside the [Feature] class.
 *
 *
 * An example of a Feature Collections given below:
 * ```json
 * {
 *   "TYPE": "FeatureCollection",
 *   "bbox": [100.0, 0.0, -100.0, 105.0, 1.0, 0.0],
 *   "features": [
 *     //...
 *   ]
 * }
 * ```
 *
 * @param features a list of features
 * @param bbox     optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("FeatureCollection")
open class FeatureCollection
@JvmOverloads
constructor(
    val features: List<Feature>,
    override val bbox: BoundingBox? = null,
) : GeoJson {

    /**
     * Create a new instance of this class by giving the feature collection a single [Feature].
     *
     * @param feature a single feature
     * @param bbox    optionally include a bbox definition as a double array
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 3.0.0
     */
    @JvmOverloads
    constructor(feature: Feature, bbox: BoundingBox? = null) : this(listOf(feature), bbox)

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Feature Collection
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FeatureCollection

        if (features != other.features) return false
        if (bbox != other.bbox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = features.hashCode()
        result = 31 * result + (bbox?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "FeatureCollection(features=$features, bbox=$bbox)"
    }

    companion object {

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a FeatureCollection object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Feature Collection
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): FeatureCollection = json.decodeFromString(jsonString)
    }
}
