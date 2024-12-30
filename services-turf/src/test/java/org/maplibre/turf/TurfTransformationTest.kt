package org.maplibre.turf

import org.junit.Assert.assertThrows
import org.junit.Test
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import org.maplibre.turf.TestUtils.compareJson
import org.maplibre.turf.TestUtils.loadJsonFixture
import org.maplibre.turf.TurfTransformation.circle

class TurfTransformationTest {

    @Test
    fun throwOnInvalidSteps() {
        val featureIn = Feature.fromJson(loadJsonFixture(CIRCLE_IN))

        assertThrows(IllegalStateException::class.java) {
            circle(
                center = featureIn.geometry() as Point,
                steps = -1,
                radius = featureIn.getNumberProperty("radius").toDouble()
            )
        }

        assertThrows(IllegalStateException::class.java) {
            circle(
                center = featureIn.geometry() as Point,
                steps = 0,
                radius = featureIn.getNumberProperty("radius").toDouble()
            )
        }
    }

    @Test
    fun pointToCircle() {
        val featureIn = Feature.fromJson(loadJsonFixture(CIRCLE_IN))
        val polygon = circle(
            center = featureIn.geometry() as Point,
            radius = featureIn.getNumberProperty("radius").toDouble()
        )

        val featureOut = Feature.fromJson(loadJsonFixture(CIRCLE_OUT))
        compareJson(featureOut.geometry()!!.toJson(), polygon.toJson())
    }

    companion object {
        private const val CIRCLE_IN = "turf-transformation/circle_in.json"
        private const val CIRCLE_OUT = "turf-transformation/circle_out.json"
    }
}
