package org.maplibre.geojson.turf

import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.Test
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.turf.TestUtils.DELTA
import org.maplibre.geojson.turf.TestUtils.loadJsonFixture
import org.maplibre.geojson.turf.TurfClassification.nearestPoint

class TurfClassificationTest {

    @Test
    fun testLineDistanceWithGeometries() {
        val point = Feature.fromJson(loadJsonFixture(PT)).geometry as Point
        val points = FeatureCollection.fromJson(loadJsonFixture(PTS))

        val pointFeatures = points.features.map { feature ->
            feature.geometry as Point
        }

        val closestPt = nearestPoint(point, pointFeatures)

        assertNotNull(closestPt)
        assertEquals(closestPt.longitude, -75.33, DELTA)
        assertEquals(closestPt.latitude, 39.44, DELTA)
    }

    companion object {
        private const val PT = "turf-classification/pt.json"
        private const val PTS = "turf-classification/pts.json"
    }
}
