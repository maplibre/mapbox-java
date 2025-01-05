package org.maplibre.turf

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.Test
import org.maplibre.geojson.BoundingBox
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.GeometryCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.turf.TestUtils.DELTA
import org.maplibre.turf.TurfMeta.coordAll
import org.maplibre.turf.TurfMeta.getCoord
import kotlin.test.assertFailsWith

class TurfMetaTest {

    @Test
    fun coordAllPoint() {
        val jsonPoint = "{type: 'Point', coordinates: [0, 0]}"
        val pointGeometry = Point.fromJson(jsonPoint)
        val resultList = coordAll(pointGeometry)

        assertEquals(resultList.size.toDouble(), 1.0, DELTA)
        assertEquals(resultList[0], Point(0.0, 0.0))
    }

    @Test
    fun coordAllLineString() {
        val jsonLineString = "{type: 'LineString', coordinates: [[0, 0], [1, 1]]}"
        val lineStringGeometry = LineString.fromJson(jsonLineString)
        val resultList = coordAll(lineStringGeometry)

        assertEquals(resultList.size.toDouble(), 2.0, DELTA)
        assertEquals(resultList[0], Point(0.0, 0.0))
        assertEquals(resultList[1], Point(1.0, 1.0))
    }

    @Test
    fun coordAllPolygon() {
        val polygonString = "{type: 'Polygon', coordinates: [[[0, 0], [1, 1], [0, 1], [0, 0]]]}"
        val polygonGeometry = Polygon.fromJson(polygonString)
        val resultList = coordAll(polygonGeometry, false)

        assertEquals(resultList.size.toDouble(), 4.0, DELTA)
        assertEquals(resultList[0], Point(0.0, 0.0))
        assertEquals(resultList[1], Point(1.0, 1.0))
        assertEquals(resultList[2], Point(0.0, 1.0))
        assertEquals(resultList[3], Point(0.0, 0.0))
    }

    @Test
    fun coordAllPolygonExcludeWrapCoord() {
        val polygonString = "{type: 'Polygon', coordinates: [[[0, 0], [1, 1], [0, 1], [0, 0]]]}"
        val polygonGeometry = Polygon.fromJson(polygonString)
        val resultList = coordAll(polygonGeometry, true)

        assertEquals(resultList.size.toDouble(), 3.0, DELTA)
        assertEquals(resultList[0], Point(0.0, 0.0))
        assertEquals(resultList[1], Point(1.0, 1.0))
        assertEquals(resultList[2], Point(0.0, 1.0))
    }

    @Test
    fun coordAllMultiPolygon() {
        val multipolygonString =
            "{type: 'MultiPolygon', coordinates: [[[[0, 0], [1, 1], [0, 1], [0, 0]]]]}"
        val multiPolygonGeometry = MultiPolygon.fromJson(multipolygonString)
        val resultList = coordAll(multiPolygonGeometry, false)

        assertEquals(resultList.size.toDouble(), 4.0, DELTA)
        assertEquals(resultList[0], Point(0.0, 0.0))
        assertEquals(resultList[1], Point(1.0, 1.0))
        assertEquals(resultList[2], Point(0.0, 1.0))
        assertEquals(resultList[3], Point(0.0, 0.0))
    }

    @Test
    fun testInvariantGetCoord() {
        val jsonFeature = "{type: Feature, geometry: {type: Point, coordinates: [1, 2]}}"
        assertEquals(
            getCoord(Feature.fromJson(jsonFeature)),
            Point(1.0, 2.0)
        )
    }

    @Test
    fun coordAllFeatureCollection() {
        val multipolygonJson =
            "{type: 'MultiPolygon', coordinates: [[[[0, 0], [1, 1], [0, 1], [0, 0]]]]}"
        val lineStringJson = "{type: 'LineString', coordinates: [[0, 0], [1, 1]]}"
        val featureCollection = FeatureCollection(
            listOf(
                Feature(MultiPolygon.fromJson(multipolygonJson)),
                Feature(LineString.fromJson(lineStringJson))
            )
        )
        assertNotNull(featureCollection)
        assertEquals(5, coordAll(featureCollection, true).size.toLong())
        assertEquals(0.0, coordAll(featureCollection, true)[0].latitude, DELTA)
        assertEquals(0.0, coordAll(featureCollection, true)[0].longitude, DELTA)
        assertEquals(1.0, coordAll(featureCollection, true)[4].latitude, DELTA)
        assertEquals(1.0, coordAll(featureCollection, true)[4].longitude, DELTA)
    }

    @Test
    fun coordAllSingleFeature() {
        val lineStringJson = "{type: 'LineString', coordinates: [[0, 0], [1, 1]]}"
        val featureCollection = FeatureCollection(
            Feature(LineString.fromJson(lineStringJson))
        )
        assertNotNull(featureCollection)
        assertEquals(2, coordAll(featureCollection, true).size.toLong())
        assertEquals(0.0, coordAll(featureCollection, true)[0].latitude, DELTA)
        assertEquals(0.0, coordAll(featureCollection, true)[0].longitude, DELTA)
        assertEquals(1.0, coordAll(featureCollection, true)[1].latitude, DELTA)
        assertEquals(1.0, coordAll(featureCollection, true)[1].longitude, DELTA)
    }

    @Test
    fun coordAllGeometryCollection() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )
        val lineString = LineString(points)
        val geometries = listOf(
            points[0],
            lineString
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val geometryCollection = GeometryCollection(geometries, bbox)

        val featureCollection = FeatureCollection(
            Feature(geometryCollection)
        )

        assertNotNull(featureCollection)
        assertNotNull(coordAll(featureCollection, true))
        assertEquals(3, coordAll(featureCollection, true).size.toLong())
        assertEquals(1.0, coordAll(featureCollection, true)[0].longitude, DELTA)
        assertEquals(2.0, coordAll(featureCollection, true)[0].latitude, DELTA)
    }

    @Test
    fun wrongFeatureGeometryForGetCoordThrowsException() {
        assertFailsWith(TurfException::class) {
            getCoord(
                Feature(
                    LineString(
                        listOf(
                            Point(0.0, 9.0),
                            Point(0.0, 10.0)
                        )
                    )
                )
            )
        }
    }
}
