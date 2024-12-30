package org.maplibre.turf

import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.GeoJson

/**
 * Also called Assertions, these methods enforce expectations of a certain type or calculate various
 * shapes from given points.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
object TurfAssertions {

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
    fun geojsonType(value: GeoJson, type: String, name: String) {
        if (value.type() != type) {
            throw TurfException("Invalid input to $name: must be a $type, given ${value.type()}")
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
        if (name.isEmpty()) {
            throw TurfException(".featureOf() requires a name")
        }

        val geometry = feature.geometry()
        if (feature.type() != "Feature" || geometry == null) {
            throw TurfException("Invalid input to $name, Feature with geometry required")
        }

        if (geometry.type() != type) {
            throw TurfException("Invalid input to $name: must be a $type, given ${geometry.type()}")
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
        if (name.isEmpty()) {
            throw TurfException("collectionOf() requires a name")
        }

        if (featureCollection.type() != "FeatureCollection" || featureCollection.features() == null) {
            throw TurfException("Invalid input to $name, FeatureCollection required")
        }

        for (feature in featureCollection.features()!!) {
            val geometry = feature.geometry()
            if (feature == null || feature.type() != "Feature" || geometry == null) {
                throw TurfException("Invalid input to $name, Feature with geometry required")
            }

            if (geometry.type() != type) {
                throw TurfException("Invalid input to $name: must be a $type, given ${geometry.type()}")
            }
        }
    }
}
