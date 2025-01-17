package org.maplibre.turf

import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.geojson.common.toJvm
import org.maplibre.turf.TurfConstants.TurfUnitCriteria
import org.maplibre.turf.common.toUnit
import org.maplibre.geojson.turf.TurfTransformation as CommonTurfTransformation

/**
 * Methods in this class consume one GeoJSON object and output a new object with the defined
 * parameters provided.
 *
 * @since 3.0.0
 */
@Deprecated(
    message = "Use new common Turf utils instead.",
    replaceWith = ReplaceWith("TurfTransformation", "org.maplibre.geojson.turf.TurfTransformation"),
)
object TurfTransformation {

    /**
     * Takes a [Point] and calculates the circle polygon given a radius in the
     * provided [TurfConstants.TurfUnitCriteria]; and steps for precision. This
     * method uses the [.DEFAULT_STEPS].
     *
     * @param center a [Point] which the circle will center around
     * @param radius the radius of the circle
     * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return a [Polygon] which represents the newly created circle
     * @since 3.0.0
     */
    @JvmStatic
    fun circle(
        center: Point,
        radius: Double,
        @TurfUnitCriteria units: String
    ): Polygon {
        return CommonTurfTransformation.circle(center, radius, unit = units.toUnit()).toJvm()
    }

    /**
     * Takes a [Point] and calculates the circle polygon given a radius in the
     * provided [TurfConstants.TurfUnitCriteria]; and steps for precision.
     *
     * @param center a [Point] which the circle will center around
     * @param radius the radius of the circle
     * @param steps  number of steps which make up the circle parameter
     * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return a [Polygon] which represents the newly created circle
     * @since 3.0.0
     */
    @JvmStatic
    @JvmOverloads
    fun circle(
        center: Point,
        radius: Double,
        steps: Int = 64,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
    ): Polygon {
        return CommonTurfTransformation.circle(center, radius, steps, units.toUnit()).toJvm()
    }
}