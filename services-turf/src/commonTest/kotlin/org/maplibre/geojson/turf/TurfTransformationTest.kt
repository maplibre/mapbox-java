package org.maplibre.geojson.turf

import kotlin.test.Test
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import org.maplibre.geojson.turf.TestUtils.loadJsonFixture
import org.maplibre.geojson.turf.TurfTransformation.circle
import kotlin.test.assertEquals
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
        val circlePolygon = circle(
            center = featureIn.geometry as Point,
            radius = featureIn.getDoubleProperty("radius")!!
        )

        val expectedPolygonFeature = Feature.fromJson(loadJsonFixture(CIRCLE_OUT))
        val expectedPolygon = expectedPolygonFeature.geometry as Polygon

        assertEquals(circlePolygon.coordinates.size, expectedPolygon.coordinates.size)
        for (i in circlePolygon.coordinates.indices) {
            val circleLineCoordinates = circlePolygon.coordinates[i]
            val expectedLineCoordinates = expectedPolygon.coordinates[i]

            assertEquals(circleLineCoordinates.size, expectedLineCoordinates.size)
            for (x in circleLineCoordinates.indices) {
                val circlePoint = circleLineCoordinates[x]
                val expectedPoint = expectedLineCoordinates[x]

                assertEquals(circlePoint.longitude, expectedPoint.longitude, TestUtils.DELTA)
                assertEquals(circlePoint.latitude, expectedPoint.latitude, TestUtils.DELTA)
            }
        }
    }

    companion object {
        private const val CIRCLE_IN = "turf-transformation/circle_in.json"
        private const val CIRCLE_OUT = "turf-transformation/circle_out.json"
    }
}
