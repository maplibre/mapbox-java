package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.model.FeatureCollection as CommonFeatureCollection

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
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("FeatureCollection", "org.maplibre.geojson.model.FeatureCollection"),
)
class FeatureCollection internal constructor(
    type: String,
    bbox: BoundingBox?,
    features: List<Feature>
) : CommonFeatureCollection(
    features,
    bbox
), GeoJson {

    /**
     * This describes the type of GeoJson this object is, thus this will always return
     * [FeatureCollection].
     *
     * @return a String which describes the TYPE of GeoJson, for this object it will always return
     * `FeatureCollection`
     * @since 1.0.0
     */
    override fun type(): String = TYPE

    /**
     * A Feature Collection might have a member named `bbox` to include information on the
     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of
     * size 2*n where n is the number of dimensions represented in the contained feature geometries,
     * with all axes of the most southwesterly point followed by all axes of the more northeasterly
     * point. The axes order of a bbox follows the axes order of geometries.
     *
     * @return a list of double coordinate values describing a bounding box
     * @since 3.0.0
     */
    override fun bbox(): BoundingBox? = bbox?.toJvm()

    /**
     * This provides the list of feature making up this Feature Collection. Note that if the
     * FeatureCollection was created through [.fromJson] this list could be null.
     * Otherwise, the list can't be null but the size of the list can equal 0.
     *
     * @return a list of [Feature]s which make up this Feature Collection
     * @since 1.0.0
     */
    fun features(): List<Feature> = features.map { feat -> feat.toJvm() }


    companion object {
        private const val TYPE = "FeatureCollection"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a FeatureCollection object from scratch it is better to use one of the other provided
         * static factory methods such as [.fromFeatures].
         *
         * @param json a formatted valid JSON string defining a GeoJson Feature Collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(json: String): FeatureCollection = CommonFeatureCollection.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by giving the feature collection an array of
         * [Feature]s. The array of features itself isn't null but it can be empty and have a length
         * of 0.
         *
         * @param features an array of features
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromFeatures(features: Array<Feature>): FeatureCollection {
            return FeatureCollection(TYPE, null, listOf(*features))
        }

        /**
         * Create a new instance of this class by giving the feature collection a list of
         * [Feature]s. The list of features itself isn't null but it can empty and have a size of 0.
         *
         * @param features a list of features
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromFeatures(features: List<Feature>): FeatureCollection {
            return FeatureCollection(TYPE, null, features)
        }

        /**
         * Create a new instance of this class by giving the feature collection an array of
         * [Feature]s. The array of features itself isn't null but it can be empty and have a length
         * of 0.
         *
         * @param features an array of features
         * @param bbox     optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromFeatures(
            features: Array<Feature>,
            bbox: BoundingBox?
        ): FeatureCollection {
            return FeatureCollection(TYPE, bbox, listOf(*features))
        }

        /**
         * Create a new instance of this class by giving the feature collection a list of
         * [Feature]s. The list of features itself isn't null but it can be empty and have a size of
         * 0.
         *
         * @param features a list of features
         * @param bbox     optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromFeatures(
            features: List<Feature>,
            bbox: BoundingBox?
        ): FeatureCollection {
            return FeatureCollection(TYPE, bbox, features)
        }

        /**
         * Create a new instance of this class by giving the feature collection a single [Feature].
         *
         * @param feature a single feature
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromFeature(feature: Feature): FeatureCollection {
            val featureList = listOf(feature)
            return FeatureCollection(TYPE, null, featureList)
        }

        /**
         * Create a new instance of this class by giving the feature collection a single [Feature].
         *
         * @param feature a single feature
         * @param bbox    optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromFeature(
            feature: Feature,
            bbox: BoundingBox?
        ): FeatureCollection {
            val featureList = listOf(feature)
            return FeatureCollection(TYPE, bbox, featureList)
        }
    }
}