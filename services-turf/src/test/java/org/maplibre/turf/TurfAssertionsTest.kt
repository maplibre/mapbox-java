package org.maplibre.turf

import org.junit.Assert.assertThrows
import org.junit.Test
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.turf.TurfAssertions.collectionOf
import org.maplibre.turf.TurfAssertions.featureOf
import org.maplibre.turf.TurfAssertions.geojsonType

class TurfAssertionsTest {

    @Test
    fun testInvariantGeojsonType3() {
        val json = "{ type: 'Point', coordinates: [0, 0] }"
        assertThrows(TurfException::class.java) {
            geojsonType(Point.fromJson(json), "Polygon", "myfn")
        }
    }

    @Test
    fun testInvariantGeojsonType4() {
        val json = "{ type: 'Point', coordinates: [0, 0] }"
        geojsonType(Point.fromJson(json), "Point", "myfn")
    }

    @Test
    fun testInvariantFeatureOf2() {
        val json = "{ type: 'Feature'}"
        assertThrows(TurfException::class.java) {
            featureOf(Feature.fromJson(json), "Polygon", "foo")
        }
    }

    @Test
    fun testInvariantFeatureOf3() {
        val json = "{ type: 'Feature', geometry: { type: 'Point', coordinates: [0, 0] }}"
        assertThrows(TurfException::class.java) {
            featureOf(Feature.fromJson(json), "Polygon", "myfn")
        }
    }

    @Test
    fun testInvariantFeatureOf4() {
        val json =
            "{ type: 'Feature', geometry: { type: 'Point', coordinates: [0, 0]}, properties: {}}"
        featureOf(Feature.fromJson(json), "Point", "myfn")
    }

    @Test
    fun testInvariantCollectionOf1() {
        val json =
            "{type: 'FeatureCollection', features: [{ type: 'Feature', geometry: { type: 'Point', coordinates: [0, 0]}, properties: {}}]}"
        assertThrows(TurfException::class.java) {
            collectionOf(FeatureCollection.fromJson(json), "Polygon", "myfn")
        }
    }

    @Test
    fun testInvariantCollectionOf3() {
        val json = "{type: 'FeatureCollection'}"
        assertThrows(TurfException::class.java) {
            collectionOf(FeatureCollection.fromJson(json), "Polygon", "foo")
        }
    }

    @Test
    fun testInvariantCollectionOf4() {
        val json =
            "{type: 'FeatureCollection', features: [{ type: 'Feature', geometry: { type: 'Point', coordinates: [0, 0]}, properties: {}}]}"
        collectionOf(FeatureCollection.fromJson(json), "Point", "myfn")
    }
}
