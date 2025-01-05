package org.maplibre.turf

import kotlin.test.Test
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import org.maplibre.turf.TestUtils.compareJson
import org.maplibre.turf.TestUtils.loadJsonFixture
import org.maplibre.turf.TurfTransformation.circle
import kotlin.test.assertFailsWith

class TurfTransformationTest {

    @Test
    fun throwOnInvalidSteps() {
        val featureIn = Feature.fromJson(loadJsonFixture(CIRCLE_IN))

        assertFailsWith(IllegalArgumentException::class) {
            circle(
                center = featureIn.geometry as Point,
                steps = -1,
                radius = featureIn.getDoubleProperty("radius")!!
            )
        }

        assertFailsWith(IllegalArgumentException::class) {
            circle(
                center = featureIn.geometry as Point,
                steps = 0,
                radius = featureIn.getDoubleProperty("radius")!!
            )
        }
    }

    @Test
    fun pointToCircle() {
        val featureIn = Feature.fromJson(loadJsonFixture(CIRCLE_IN))
        val polygon = circle(
            center = featureIn.geometry as Point,
            radius = featureIn.getDoubleProperty("radius")!!
        )

        val featureOut = Feature.fromJson(loadJsonFixture(CIRCLE_OUT))
        compareJson(featureOut.geometry!!.toJson(), polygon.toJson())
    }

    companion object {
        private const val CIRCLE_IN = "turf-transformation/circle_in.json"
        private const val CIRCLE_OUT = "turf-transformation/circle_out.json"
    }
}
