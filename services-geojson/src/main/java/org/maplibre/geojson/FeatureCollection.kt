package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


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
 * <pre>
 * {
 * "TYPE": "FeatureCollection",
 * "bbox": [100.0, 0.0, -100.0, 105.0, 1.0, 0.0],
 * "features": [
 * //...
 * ]
 * }
</pre> *
 *
 * @param features a list of features
 * @param bbox     optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("FeatureCollection")
data class FeatureCollection
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

    override fun toJson() = json.encodeToString(this)

    companion object {
        @JvmStatic
        fun fromJson(jsonString: String): FeatureCollection = json.decodeFromString(jsonString)
    }
}
