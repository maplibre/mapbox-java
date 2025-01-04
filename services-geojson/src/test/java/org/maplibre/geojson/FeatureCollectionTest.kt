package org.maplibre.geojson

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson
import org.maplibre.geojson.TestUtils.loadJsonFixture

class FeatureCollectionTest {

    @Test
    fun sanity() {
        val features = listOf(
            Feature(),
            Feature()
        )

        val featureCollection = FeatureCollection(features)
        assertNotNull(featureCollection)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val features = listOf(
            Feature(),
            Feature()
        )

        val featureCollection = FeatureCollection(features)
        assertNull(featureCollection.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)

        val features = listOf(feature, feature)

        val featureCollection = FeatureCollection(features)
        compareJson(
            featureCollection.toJson(),
            ("{\"type\":\"FeatureCollection\",\"features\":[" +
                    "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]}}," +
                    "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]}}]}")
        )
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val features = listOf(
            Feature(),
            Feature()
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val featureCollection = FeatureCollection(features, bbox)
        assertNotNull(featureCollection.bbox)
        assertEquals(1.0, featureCollection.bbox!!.west, DELTA)
        assertEquals(2.0, featureCollection.bbox!!.south, DELTA)
        assertEquals(3.0, featureCollection.bbox!!.east, DELTA)
        assertEquals(4.0, featureCollection.bbox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )
        val lineString = LineString(points)
        val feature = Feature(lineString)

        val features = listOf(feature, feature)
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val featureCollection = FeatureCollection(features, bbox)
        compareJson(
            featureCollection.toJson(),
            ("{\"type\":\"FeatureCollection\",\"bbox\":[1.0,2.0,3.0,4.0],"
                    + "\"features\":[{\"type\":\"Feature\","
                    + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}},"
                    + "{\"type\":\"Feature\","
                    + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}"
                    + "]}")
        )
    }

    @Test
    fun passingInSingleFeature_doesHandleCorrectly() {
        val point = Point(1.0, 2.0)
        val feature = Feature(point)
        val geo = FeatureCollection(feature)
        assertNotNull(geo.features)
        assertEquals(1, geo.features.size)
        assertEquals(2.0, (geo.features.first().geometry as Point).coordinates[1], DELTA)
    }

    @Test
    fun fromJson() {
        val json = loadJsonFixture(SAMPLE_FEATURECOLLECTION)
        val geo = FeatureCollection.fromJson(json)
        assertEquals(geo.features.size, 3)
        assertTrue(geo.features[0].geometry is Point)
        assertTrue(geo.features[0].geometry is Point)
        assertTrue(geo.features[1].geometry is LineString)
        assertTrue(geo.features[2].geometry is Polygon)
    }

    @Test
    fun toJson() {
        val json = loadJsonFixture(SAMPLE_FEATURECOLLECTION_BBOX)
        val geo = FeatureCollection.fromJson(json)
        compareJson(json, geo.toJson())
    }

    companion object {
        private const val SAMPLE_FEATURECOLLECTION = "sample-featurecollection.json"
        private const val SAMPLE_FEATURECOLLECTION_BBOX = "sample-feature-collection-with-bbox.json"
    }
}
