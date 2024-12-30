package org.maplibre.turf

import androidx.annotation.IntRange
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.turf.TurfConstants.TurfUnitCriteria
import org.maplibre.turf.TurfMeasurement.destination

/**
 * Methods in this class consume one GeoJSON object and output a new object with the defined
 * parameters provided.
 *
 * @since 3.0.0
 */
object TurfTransformation {

    private const val DEFAULT_STEPS = 64

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
        steps: Int = DEFAULT_STEPS,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
    ): Polygon {
        check(steps >= 1) { "Steps must be greater than 0" }

        val coordinates = (0 until steps)
            .map { value -> destination(center, radius, value * 360.0 / steps, units) }

        return Polygon.fromLngLats(listOf(coordinates + coordinates.first()))
    }
}
