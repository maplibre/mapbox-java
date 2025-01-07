package org.maplibre.turf

import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

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
     * provided [TurfUnit]; and steps for precision.
     *
     * @param center a [Point] which the circle will center around
     * @param radius the radius of the circle
     * @param steps  number of steps which make up the circle parameter
     * @param unit  one of the units found inside [TurfUnit]
     * @return a [Polygon] which represents the newly created circle
     * @since 3.0.0
     */
    @JvmStatic
    @JvmOverloads
    fun circle(
        center: Point,
        radius: Double,
        steps: Int = DEFAULT_STEPS,
        unit: TurfUnit = TurfUnit.DEFAULT
    ): Polygon {
        require(steps >= 1) { "Steps must be greater than 0" }

        val coordinates = (0 until steps)
            .map { value -> TurfMeasurement.destination(center, radius, value * 360.0 / steps, unit) }

        return Polygon(listOf(coordinates + coordinates.first()))
    }
}
