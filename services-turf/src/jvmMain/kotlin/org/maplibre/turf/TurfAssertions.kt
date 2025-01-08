package org.maplibre.turf

import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.GeoJson
import org.maplibre.geojson.Point

/**
 * Also called Assertions, these methods enforce expectations of a certain type or calculate various
 * shapes from given points.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
@Deprecated(message = "The assertions are not needed anymore. Use new Turf utils instead.")
object TurfAssertions {

    /**
     * Unwrap a coordinate [Point] from a Feature with a Point geometry.
     *
     * @param obj any value
     * @return a coordinate
     * @see [Turf getCoord documentation](http://turfjs.org/docs/.getcoord)
     *
     * @since 1.2.0
     */
    @JvmStatic
    @Deprecated("use {@link TurfMeta#getCoord(Feature)}")
    fun getCoord(obj: Feature): Point {
        return TurfMeta.getCoord(obj)
    }

    /**
     * Enforce expectations about types of GeoJson objects for Turf.
     *
     * @param value any GeoJson object
     * @param type  expected GeoJson type
     * @param name  name of calling function
     * @see [Turf geojsonType documentation](http://turfjs.org/docs/.geojsontype)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun geojsonType(value: GeoJson?, type: String, name: String) {
        if (type == null || type.length == 0 || name == null || name.length == 0) {
            throw TurfException("Type and name required")
        }
        if (value == null || value.type() != type) {
            throw TurfException(
                ("Invalid input to " + name + ": must be a " + type
                        + ", given " + (value?.type() ?: " null"))
            )
        }
    }

    /**
     * Enforce expectations about types of [Feature] inputs for Turf. Internally this uses
     * [Feature.type] to judge geometry types.
     *
     * @param feature with an expected geometry type
     * @param type    type expected GeoJson type
     * @param name    name of calling function
     * @see [Turf featureOf documentation](http://turfjs.org/docs/.featureof)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun featureOf(feature: Feature, type: String, name: String) {
        if (name == null || name.length == 0) {
            throw TurfException(".featureOf() requires a name")
        }
        if (feature == null || feature.type() != "Feature" || feature.geometry() == null) {
            throw TurfException(
                String.format(
                    "Invalid input to %s, Feature with geometry required", name
                )
            )
        }
        if (feature.geometry()!!.type() != type) {
            throw TurfException(
                String.format(
                    "Invalid input to %s: must be a %s, given %s",
                    name,
                    type,
                    feature.geometry()!!.type()
                )
            )
        }
    }

    /**
     * Enforce expectations about types of [FeatureCollection] inputs for Turf. Internally
     * this uses [Feature.type]} to judge geometry types.
     *
     * @param featureCollection for which features will be judged
     * @param type              expected GeoJson type
     * @param name              name of calling function
     * @see [Turf collectionOf documentation](http://turfjs.org/docs/.collectionof)
     *
     * @since 1.2.0
     */
    @JvmStatic
    fun collectionOf(featureCollection: FeatureCollection, type: String, name: String) {
        if (name == null || name.length == 0) {
            throw TurfException("collectionOf() requires a name")
        }
        if (featureCollection == null || featureCollection.type() != "FeatureCollection" || featureCollection.features() == null) {
            throw TurfException(
                String.format(
                    "Invalid input to %s, FeatureCollection required", name
                )
            )
        }
        for (feature in featureCollection.features()) {
            if (feature == null || feature.type() != "Feature" || feature.geometry() == null) {
                throw TurfException(
                    String.format(
                        "Invalid input to %s, Feature with geometry required", name
                    )
                )
            }
            if (feature.geometry()!!.type() != type) {
                throw TurfException(
                    String.format(
                        "Invalid input to %s: must be a %s, given %s",
                        name,
                        type,
                        feature.geometry()!!.type()
                    )
                )
            }
        }
    }
}