package org.maplibre.geojson.turf

import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.Test
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.Geometry
import org.maplibre.geojson.model.GeometryCollection
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import org.maplibre.geojson.turf.TestUtils.DELTA
import org.maplibre.geojson.turf.TestUtils.loadJsonFixture
import org.maplibre.geojson.turf.TurfMeasurement.along
import org.maplibre.geojson.turf.TurfMeasurement.area
import org.maplibre.geojson.turf.TurfMeasurement.bbox
import org.maplibre.geojson.turf.TurfMeasurement.bboxPolygon
import org.maplibre.geojson.turf.TurfMeasurement.bearing
import org.maplibre.geojson.turf.TurfMeasurement.center
import org.maplibre.geojson.turf.TurfMeasurement.destination
import org.maplibre.geojson.turf.TurfMeasurement.distance
import org.maplibre.geojson.turf.TurfMeasurement.envelope
import org.maplibre.geojson.turf.TurfMeasurement.length
import org.maplibre.geojson.turf.TurfMeasurement.midpoint
import org.maplibre.geojson.turf.TurfMeasurement.square
import kotlin.math.roundToInt
import kotlin.test.assertContentEquals

class TurfMeasurementTest {

    @Test
    fun testBearing() {
        val pt1 = Point(-75.4, 39.4)
        val pt2 = Point(-75.534, 39.123)
        assertNotEquals(bearing(pt1, pt2), 0.0, DELTA)
    }

    @Test
    fun testDestination() {
        val pt1 = Point(-75.0, 39.0)
        val dist = 100.0
        val bear = 180.0
        assertNotNull(destination(pt1, dist, bear, TurfUnit.KILOMETERS))
    }

    //
    // Turf distance tests
    //

    @Test
    fun testDistance() {
        val pt1 = Point(-75.343, 39.984)
        val pt2 = Point(-75.534, 39.123)

        // Common cases
        assertEquals(60.37218405837491, distance(pt1, pt2, TurfUnit.MILES), DELTA)
        assertEquals(
            52.461979624130436,
            distance(pt1, pt2, TurfUnit.NAUTICAL_MILES),
            DELTA
        )
        assertEquals(
            97.15957803131901,
            distance(pt1, pt2, TurfUnit.KILOMETERS),
            DELTA
        )
        assertEquals(
            0.015245501024842149,
            distance(pt1, pt2, TurfUnit.RADIANS),
            DELTA
        )
        assertEquals(
            0.8735028650863799,
            distance(pt1, pt2, TurfUnit.DEGREES),
            DELTA
        )

        // This also works
        assertEquals(
            97.15957803131901,
            distance(pt1, pt2, TurfUnit.KILOMETERS),
            DELTA
        )

        // Default is kilometers
        assertEquals(97.15957803131901, distance(pt1, pt2), DELTA)

        // Bad units not possible
    }

    @Test
    fun lineDistance_returnsZeroWhenRouteIsPoint() {
        val coords = listOf(Point(1.0, 1.0))

        val lineString = LineString(coords)
        val distance = length(lineString, TurfUnit.METERS)
        assertEquals(0.0, distance, DELTA)
    }

    @Test
    fun testLineDistanceWithGeometries() {
        val route1 = Feature.fromJson(loadJsonFixture(LINE_DISTANCE_ROUTE_ONE))
        val route2 = Feature.fromJson(loadJsonFixture(LINE_DISTANCE_ROUTE_TWO))
        assertEquals(
            202,
            length(
                (route1.geometry as LineString?)!!,
                TurfUnit.MILES
            ).roundToInt()
        )
        assertEquals(
            741.7787396994203,
            length((route2.geometry as LineString?)!!, TurfUnit.KILOMETERS), DELTA
        )
    }

    @Test
    fun testLineDistancePolygon() {
        val feature = Feature.fromJson(loadJsonFixture(LINE_DISTANCE_POLYGON))
        assertEquals(
            5599,
            (1000 * length(
                (feature.geometry as Polygon?)!!,
                TurfUnit.KILOMETERS
            )).roundToInt()
        )
    }

    @Test
    fun testLineDistanceMultiLineString() {
        val feature = Feature.fromJson(
            loadJsonFixture(
                LINE_DISTANCE_MULTILINESTRING
            )
        )
        assertEquals(
            4705.0, (
                    1000
                            * length(
                        (feature.geometry as MultiLineString?)!!,
                        TurfUnit.KILOMETERS
                    )
                    ).roundToInt().toDouble(), DELTA
        )
    }

    //
    // Turf midpoint tests
    //

    @Test
    fun testMidpointHorizontalEquator() {
        val pt1 = Point(0.0, 0.0)
        val pt2 = Point(10.0, 0.0)
        val mid = midpoint(pt1, pt2)

        assertEquals(
            distance(pt1, mid, TurfUnit.MILES),
            distance(pt2, mid, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun testMidpointVericalFromEquator() {
        val pt1 = Point(0.0, 0.0)
        val pt2 = Point(0.0, 10.0)
        val mid = midpoint(pt1, pt2)

        assertEquals(
            distance(pt1, mid, TurfUnit.MILES),
            distance(pt2, mid, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun testMidpointVericalToEquator() {
        val pt1 = Point(0.0, 10.0)
        val pt2 = Point(0.0, 0.0)
        val mid = midpoint(pt1, pt2)

        assertEquals(
            distance(pt1, mid, TurfUnit.MILES),
            distance(pt2, mid, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun testMidpointDiagonalBackOverEquator() {
        val pt1 = Point(-1.0, 10.0)
        val pt2 = Point(1.0, -1.0)
        val mid = midpoint(pt1, pt2)

        assertEquals(
            distance(pt1, mid, TurfUnit.MILES),
            distance(pt2, mid, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun testMidpointDiagonalForwardOverEquator() {
        val pt1 = Point(-5.0, -1.0)
        val pt2 = Point(5.0, 10.0)
        val mid = midpoint(pt1, pt2)

        assertEquals(
            distance(pt1, mid, TurfUnit.MILES),
            distance(pt2, mid, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun testMidpointLongDistance() {
        val pt1 = Point(22.5, 21.94304553343818)
        val pt2 = Point(92.10937499999999, 46.800059446787316)
        val mid = midpoint(pt1, pt2)

        assertEquals(
            distance(pt1, mid, TurfUnit.MILES),
            distance(pt2, mid, TurfUnit.MILES), DELTA
        )
    }

    // Custom test to make sure conversion of Position to point works correctly
    @Test
    fun testMidpointPositionToPoint() {
        val pt1 = Point(0.0, 0.0)
        val pt2 = Point(10.0, 0.0)
        val mid = midpoint(pt1, pt2)

        assertEquals(
            distance(pt1, mid, TurfUnit.MILES),
            distance(pt2, mid, TurfUnit.MILES), DELTA
        )
    }

    @Test
    fun turfAlong_returnsZeroWhenRouteIsPoint() {
        val coords = listOf(Point(1.0, 1.0))

        val lineString = LineString(coords)
        val point = along(lineString, 0.0, TurfUnit.METERS)
        assertEquals(1.0, point.latitude, DELTA)
        assertEquals(1.0, point.longitude, DELTA)
    }

    @Test
    fun testTurfAlong() {
        val feature = Feature.fromJson(loadJsonFixture(TURF_ALONG_DC_LINE))
        val line = feature.geometry as LineString

        val pt8 = along(line, 0.0, TurfUnit.MILES)
        val fc = FeatureCollection(
            listOf(
                Feature(along(line, 1.0, TurfUnit.MILES)),
                Feature(along(line, 1.2, TurfUnit.MILES)),
                Feature(along(line, 1.4, TurfUnit.MILES)),
                Feature(along(line, 1.6, TurfUnit.MILES)),
                Feature(along(line, 1.8, TurfUnit.MILES)),
                Feature(along(line, 2.0, TurfUnit.MILES)),
                Feature(along(line, 100.0, TurfUnit.MILES)),
                Feature(pt8)
            )
        )

        for (f in fc.features) {
            assertNotNull(f)
            assertTrue(f.geometry is Point)
        }

        assertEquals(8, fc.features.size.toLong())
        assertEquals(
            (fc.features[7].geometry as Point).longitude,
            pt8.longitude,
            DELTA
        )
        assertEquals(
            (fc.features[7].geometry as Point).latitude,
            pt8.latitude,
            DELTA
        )
    }

    //
    // Turf bbox Test
    //

    @Test
    fun bboxFromPoint() {
        val feature = Feature.fromJson(loadJsonFixture(TURF_BBOX_POINT))
        val bbox = bbox((feature.geometry as Point?)!!)

        assertEquals(4, bbox.size.toLong())
        assertEquals(102.0, bbox[0], DELTA)
        assertEquals(0.5, bbox[1], DELTA)
        assertEquals(102.0, bbox[2], DELTA)
        assertEquals(0.5, bbox[3], DELTA)
    }

    @Test
    fun bboxFromLine() {
        val lineString = LineString.fromJson(loadJsonFixture(TURF_BBOX_LINESTRING))
        val bbox = bbox(lineString)

        assertEquals(4, bbox.size.toLong())
        assertEquals(102.0, bbox[0], DELTA)
        assertEquals(-10.0, bbox[1], DELTA)
        assertEquals(130.0, bbox[2], DELTA)
        assertEquals(4.0, bbox[3], DELTA)
    }

    @Test
    fun bboxFromPolygon() {
        val feature = Feature.fromJson(loadJsonFixture(TURF_BBOX_POLYGON))
        val bbox = bbox(feature.geometry as Polygon)

        assertEquals(4, bbox.size.toLong())
        assertEquals(100.0, bbox[0], DELTA)
        assertEquals(0.0, bbox[1], DELTA)
        assertEquals(101.0, bbox[2], DELTA)
        assertEquals(1.0, bbox[3], DELTA)
    }

    @Test
    fun bboxFromMultiLineString() {
        val multiLineString =
            MultiLineString.fromJson(loadJsonFixture(TURF_BBOX_MULTILINESTRING))
        val bbox = bbox(multiLineString)

        assertEquals(4, bbox.size.toLong())
        assertEquals(100.0, bbox[0], DELTA)
        assertEquals(0.0, bbox[1], DELTA)
        assertEquals(103.0, bbox[2], DELTA)
        assertEquals(3.0, bbox[3], DELTA)
    }

    @Test
    fun bboxFromMultiPolygon() {
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON))
        val bbox = bbox(multiPolygon)

        assertEquals(4, bbox.size.toLong())
        assertEquals(100.0, bbox[0], DELTA)
        assertEquals(0.0, bbox[1], DELTA)
        assertEquals(103.0, bbox[2], DELTA)
        assertEquals(3.0, bbox[3], DELTA)
    }

    @Test
    fun bboxFromGeometry() {
        val geometry: Geometry = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON))
        val bbox = bbox(geometry)

        assertEquals(4, bbox.size.toLong())
        assertEquals(100.0, bbox[0], DELTA)
        assertEquals(0.0, bbox[1], DELTA)
        assertEquals(103.0, bbox[2], DELTA)
        assertEquals(3.0, bbox[3], DELTA)
    }

    @Test
    fun bboxFromGeometryCollection() {
        // Check that geometry collection and direct bbox are equal
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON))
        assertContentEquals(
            bbox(multiPolygon),
            bbox(GeometryCollection(multiPolygon))
        )

        // Check all geometry types
        val geometries = listOf(
            Feature.fromJson(loadJsonFixture(TURF_BBOX_POINT)).geometry!!,
            MultiPoint.fromJson(loadJsonFixture(TURF_BBOX_MULTI_POINT)),
            LineString.fromJson(loadJsonFixture(TURF_BBOX_LINESTRING)),
            MultiLineString.fromJson(loadJsonFixture(TURF_BBOX_MULTILINESTRING)),
            Feature.fromJson(loadJsonFixture(TURF_BBOX_POLYGON)).geometry!!,
            MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON)),
            GeometryCollection(Point(-1.0, -1.0)),
        )

        val bbox = bbox(GeometryCollection(geometries))

        assertEquals(4, bbox.size.toLong())
        assertEquals(-1.0, bbox[0], DELTA)
        assertEquals(-10.0, bbox[1], DELTA)
        assertEquals(130.0, bbox[2], DELTA)
        assertEquals(4.0, bbox[3], DELTA)
    }

    @Test
    fun bboxPolygonFromLineString() {
        // Create a LineString
        val lineString = LineString.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_LINESTRING))

        // Use the LineString object to calculate its BoundingBox area
        val bbox = bbox(lineString)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = BoundingBox(
            Point(bbox[0], bbox[1]),
            Point(bbox[2], bbox[3])
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox)

        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry as Polygon?

        assertNotNull(polygonRepresentingBoundingBox)
        assertEquals(0, polygonRepresentingBoundingBox.innerLines.size.toLong())
        assertEquals(5, polygonRepresentingBoundingBox.coordinates[0].size.toLong())
        assertEquals(
            Point(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates[0][0]
        )
        assertEquals(
            Point(130.0, -10.0),
            polygonRepresentingBoundingBox.coordinates[0][1]
        )
        assertEquals(
            Point(130.0, 4.0),
            polygonRepresentingBoundingBox.coordinates[0][2]
        )
        assertEquals(
            Point(102.0, 4.0),
            polygonRepresentingBoundingBox.coordinates[0][3]
        )
        assertEquals(
            Point(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates[0][4]
        )
    }

    @Test
    fun bboxPolygonFromLineStringWithId() {
        // Create a LineString
        val lineString = LineString.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_LINESTRING))

        // Use the LineString object to calculate its BoundingBox area
        val bbox = bbox(lineString)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = BoundingBox(
            Point(bbox[0], bbox[1]),
            Point(bbox[2], bbox[3])
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox, null, "TEST_ID")
        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry as Polygon?

        assertNotNull(polygonRepresentingBoundingBox)
        assertEquals(0, polygonRepresentingBoundingBox.innerLines.size.toLong())
        assertEquals(5, polygonRepresentingBoundingBox.coordinates[0].size.toLong())
        assertEquals("TEST_ID", featureRepresentingBoundingBox.id)
        assertEquals(
            Point(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates[0][0]
        )
        assertEquals(
            Point(130.0, -10.0),
            polygonRepresentingBoundingBox.coordinates[0][1]
        )
        assertEquals(
            Point(130.0, 4.0),
            polygonRepresentingBoundingBox.coordinates[0][2]
        )
        assertEquals(
            Point(102.0, 4.0),
            polygonRepresentingBoundingBox.coordinates[0][3]
        )
        assertEquals(
            Point(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates[0][4]
        )
    }

    @Test
    fun bboxPolygonFromMultiPolygon() {
        // Create a MultiPolygon
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_MULTIPOLYGON))

        // Use the MultiPolygon object to calculate its BoundingBox area
        val bbox = bbox(multiPolygon)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = BoundingBox(
            Point(bbox[0], bbox[1]),
            Point(bbox[2], bbox[3])
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox)

        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry as Polygon?

        assertNotNull(polygonRepresentingBoundingBox)
        assertEquals(0, polygonRepresentingBoundingBox.innerLines.size.toLong())
        assertEquals(5, polygonRepresentingBoundingBox.coordinates[0].size.toLong())
        assertEquals(
            Point(100.0, 0.0),
            polygonRepresentingBoundingBox.coordinates[0][4]
        )
    }

    @Test
    fun bboxPolygonFromMultiPoint() {
        // Create a MultiPoint
        val multiPoint = MultiPoint.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_MULTI_POINT))

        // Use the MultiPoint object to calculate its BoundingBox area
        val bbox = bbox(multiPoint)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = BoundingBox(
            Point(bbox[0], bbox[1]),
            Point(bbox[2], bbox[3])
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox)

        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry as Polygon?

        assertNotNull(polygonRepresentingBoundingBox)
        assertEquals(0, polygonRepresentingBoundingBox.innerLines.size.toLong())
        assertEquals(5, polygonRepresentingBoundingBox.coordinates[0].size.toLong())
    }

    @Test
    fun envelope() {
        val featureCollection = FeatureCollection.fromJson(
            loadJsonFixture(
                TURF_ENVELOPE_FEATURE_COLLECTION
            )
        )
        val polygon = envelope(featureCollection)

        val polygonPoints = listOf(
            listOf(
                Point(20.0, -10.0),
                Point(130.0, -10.0),
                Point(130.0, 4.0),
                Point(20.0, 4.0),
                Point(20.0, -10.0)
            )
        )
        val expected = Polygon(polygonPoints)
        assertEquals(expected, polygon, "Polygon should match.")
    }

    @Test
    fun square() {
        val bbox1 = BoundingBox(0.0, 0.0, 5.0, 10.0)
        val bbox2 = BoundingBox(0.0, 0.0, 10.0, 5.0)

        val sq1 = square(bbox1)
        val sq2 = square(bbox2)

        assertEquals(BoundingBox(-2.5, 0.0, 7.5, 10.0), sq1)
        assertEquals(BoundingBox(0.0, -2.5, 10.0, 7.5), sq2)
    }

    @Test
    fun areaPolygon() {
        val expected = loadJsonFixture(TURF_AREA_POLYGON_RESULT).toDouble()
        assertEquals(
            expected, area(
                Feature.fromJson(
                    loadJsonFixture(
                        TURF_AREA_POLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun areaMultiPolygon() {
        val expected = loadJsonFixture(TURF_AREA_MULTIPOLYGON_RESULT).toDouble()
        assertEquals(
            expected, area(
                Feature.fromJson(
                    loadJsonFixture(
                        TURF_AREA_MULTIPOLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun areaGeometry() {
        val expected = loadJsonFixture(TURF_AREA_GEOM_POLYGON_RESULT).toDouble()
        assertEquals(
            expected, area(
                Polygon.fromJson(
                    loadJsonFixture(
                        TURF_AREA_GEOM_POLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun areaFeatureCollection() {
        val expected = loadJsonFixture(TURF_AREA_FEATURECOLLECTION_POLYGON_RESULT).toDouble()
        assertEquals(
            expected, area(
                FeatureCollection.fromJson(
                    loadJsonFixture(
                        TURF_AREA_FEATURECOLLECTION_POLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun centerFeature() {
        val expectedFeature = Feature(Point(133.5, -27.0))
        val inputFeature = Feature.fromJson(
            loadJsonFixture(
                TURF_AREA_POLYGON_GEOJSON
            )
        )
        assertEquals(expectedFeature, center(inputFeature, null, null))
    }

    @Test
    fun centerFeatureWithProperties() {
        val properties = mapOf("key" to JsonPrimitive("value"))
        val inputFeature = Feature.fromJson(
            loadJsonFixture(
                TURF_AREA_POLYGON_GEOJSON
            )
        )
        val returnedCenterFeature = center(inputFeature, properties, null)
        val returnedPoint = returnedCenterFeature.geometry as Point?
        if (returnedPoint != null) {
            assertEquals(133.5, returnedPoint.longitude, 0.0)
            assertEquals(-27.0, returnedPoint.latitude, 0.0)
            if (returnedCenterFeature.properties != null) {
                assertEquals("value", returnedCenterFeature.getStringProperty("key"))
            }
        }
    }

    @Test
    fun centerFeatureWithId() {
        val testIdString = "testId"
        val inputFeature = Feature.fromJson(
            loadJsonFixture(
                TURF_AREA_POLYGON_GEOJSON
            )
        )
        val returnedCenterFeature = center(inputFeature, null, testIdString)
        val returnedPoint = returnedCenterFeature.geometry as Point?
        if (returnedPoint != null) {
            assertEquals(133.5, returnedPoint.longitude, 0.0)
            assertEquals(-27.0, returnedPoint.latitude, 0.0)
            if (returnedCenterFeature.id != null) {
                assertEquals(returnedCenterFeature.id, testIdString)
            }
        }
    }

    @Test
    fun centerFeatureCollection() {
        val inputFeatureCollection = FeatureCollection.fromJson(
            loadJsonFixture(
                TURF_AREA_FEATURECOLLECTION_POLYGON_GEOJSON
            )
        )
        val returnedCenterFeature = center(inputFeatureCollection, null, null)
        val returnedPoint = returnedCenterFeature.geometry as Point?
        if (returnedPoint != null) {
            assertEquals(4.1748046875, returnedPoint.longitude, DELTA)
            assertEquals(47.214224817196836, returnedPoint.latitude, DELTA)
        }
    }

    companion object {
        private const val LINE_DISTANCE_ROUTE_ONE = "turf-line-distance/route1.geojson"
        private const val LINE_DISTANCE_ROUTE_TWO = "turf-line-distance/route2.geojson"
        private const val LINE_DISTANCE_POLYGON = "turf-line-distance/polygon.geojson"
        private const val TURF_ALONG_DC_LINE = "turf-along/dc-line.geojson"
        private const val TURF_BBOX_POINT = "turf-bbox/point.geojson"
        private const val TURF_BBOX_MULTI_POINT = "turf-bbox/multipoint.geojson"
        private const val TURF_BBOX_LINESTRING = "turf-bbox/linestring.geojson"
        private const val TURF_BBOX_POLYGON = "turf-bbox/polygon.geojson"
        private const val TURF_BBOX_MULTILINESTRING = "turf-bbox/multilinestring.geojson"
        private const val TURF_BBOX_MULTIPOLYGON = "turf-bbox/multipolygon.geojson"
        private const val TURF_BBOX_POLYGON_LINESTRING = "turf-bbox-polygon/linestring.geojson"
        private const val TURF_BBOX_POLYGON_MULTIPOLYGON = "turf-bbox-polygon/multipolygon.geojson"
        private const val TURF_BBOX_POLYGON_MULTI_POINT = "turf-bbox-polygon/multipoint.geojson"
        private const val TURF_ENVELOPE_FEATURE_COLLECTION =
            "turf-envelope/feature-collection.geojson"
        private const val LINE_DISTANCE_MULTILINESTRING =
            "turf-line-distance/multilinestring.geojson"
        private const val TURF_AREA_POLYGON_GEOJSON = "turf-area/polygon.geojson"
        private const val TURF_AREA_POLYGON_RESULT = "turf-area/polygon.json"
        private const val TURF_AREA_MULTIPOLYGON_GEOJSON = "turf-area/multi-polygon.geojson"
        private const val TURF_AREA_MULTIPOLYGON_RESULT = "turf-area/multi-polygon.json"
        private const val TURF_AREA_GEOM_POLYGON_GEOJSON = "turf-area/geometry-polygon.geojson"
        private const val TURF_AREA_GEOM_POLYGON_RESULT = "turf-area/geometry-polygon.json"
        private const val TURF_AREA_FEATURECOLLECTION_POLYGON_GEOJSON =
            "turf-area/featurecollection-polygon.geojson"
        private const val TURF_AREA_FEATURECOLLECTION_POLYGON_RESULT =
            "turf-area/featurecollection-polygon.json"
    }
}
