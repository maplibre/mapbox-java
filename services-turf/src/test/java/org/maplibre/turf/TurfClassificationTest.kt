package org.maplibre.turf

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Test
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.turf.TestUtils.DELTA
import org.maplibre.turf.TestUtils.loadJsonFixture
import org.maplibre.turf.TurfClassification.nearestPoint
import java.io.IOException

class TurfClassificationTest {

    @Test
    fun testLineDistanceWithGeometries() {
        val point = Feature.fromJson(loadJsonFixture(PT)).geometry() as Point
        val points = FeatureCollection.fromJson(loadJsonFixture(PTS))!!

        val pointFeatures = points.features()!!.map { feature ->
            feature.geometry() as Point
        }

        val closestPt = nearestPoint(point, pointFeatures)

        assertNotNull(closestPt)
        assertEquals(closestPt.type(), "Point")
        assertEquals(closestPt.longitude(), -75.33, DELTA)
        assertEquals(closestPt.latitude(), 39.44, DELTA)
    }

    companion object {
        private const val PT = "turf-classification/pt.json"
        private const val PTS = "turf-classification/pts.json"
    }
}
