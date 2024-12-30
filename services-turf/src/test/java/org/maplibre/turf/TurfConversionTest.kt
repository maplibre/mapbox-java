package org.maplibre.turf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.GeometryCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.MultiLineString
import org.maplibre.geojson.MultiPoint
import org.maplibre.geojson.MultiPolygon
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import org.maplibre.turf.TestUtils.DELTA
import org.maplibre.turf.TestUtils.compareJson
import org.maplibre.turf.TestUtils.loadJsonFixture
import org.maplibre.turf.TurfConversion.combine
import org.maplibre.turf.TurfConversion.convertLength
import org.maplibre.turf.TurfConversion.explode
import org.maplibre.turf.TurfConversion.lengthToDegrees
import org.maplibre.turf.TurfConversion.lengthToRadians
import org.maplibre.turf.TurfConversion.multiPolygonToLine
import org.maplibre.turf.TurfConversion.polygonToLine
import org.maplibre.turf.TurfConversion.radiansToLength

class TurfConversionTest {

    @Test
    fun radiansToDistance() {
        assertEquals(
            1.0, radiansToLength(1.0, TurfConstants.UNIT_RADIANS), DELTA
        )
        assertEquals(
            6373.0, radiansToLength(1.0, TurfConstants.UNIT_KILOMETERS), DELTA
        )
        assertEquals(
            3960.0, radiansToLength(1.0, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    fun distanceToRadians() {
        assertEquals(
            1.0, lengthToRadians(1.0, TurfConstants.UNIT_RADIANS), DELTA
        )
        assertEquals(
            1.0, lengthToRadians(6373.0, TurfConstants.UNIT_KILOMETERS), DELTA
        )
        assertEquals(
            1.0, lengthToRadians(3960.0, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    fun distanceToDegrees() {
        assertEquals(
            57.29577951308232, lengthToDegrees(1.0, TurfConstants.UNIT_RADIANS), DELTA
        )
        assertEquals(
            0.8990393772647469, lengthToDegrees(
                100.0,
                TurfConstants.UNIT_KILOMETERS
            ), DELTA
        )
        assertEquals(
            0.14468631190172304, lengthToDegrees(10.0, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    fun convertDistance() {
        assertEquals(
            1.0,
            convertLength(1000.0, TurfConstants.UNIT_METERS), DELTA
        )
        assertEquals(
            0.6213714106386318,
            convertLength(
                1.0, TurfConstants.UNIT_KILOMETERS,
                TurfConstants.UNIT_MILES
            ), DELTA
        )
        assertEquals(
            1.6093434343434343,
            convertLength(
                1.0, TurfConstants.UNIT_MILES,
                TurfConstants.UNIT_KILOMETERS
            ), DELTA
        )
        assertEquals(
            1.851999843075488,
            convertLength(1.0, TurfConstants.UNIT_NAUTICAL_MILES), DELTA
        )
        assertEquals(
            100.0,
            convertLength(
                1.0, TurfConstants.UNIT_METERS,
                TurfConstants.UNIT_CENTIMETERS
            ), DELTA
        )
    }


    @Test
    fun combinePointsToMultiPoint() {
        val pointFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(Point.fromLngLat(-2.46, 27.6835)),
                    Feature.fromGeometry(Point.fromLngLat(41.83, 7.3624))
                )
            )

        val featureCollectionWithNewMultiPointObject = combine(pointFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiPointObject)

        val multiPoint =
            featureCollectionWithNewMultiPointObject.features()!![0].geometry() as MultiPoint?
        assertNotNull(multiPoint)

        assertEquals(-2.46, multiPoint!!.coordinates()[0].longitude(), DELTA)
        assertEquals(27.6835, multiPoint.coordinates()[0].latitude(), DELTA)
        assertEquals(41.83, multiPoint.coordinates()[1].longitude(), DELTA)
        assertEquals(7.3624, multiPoint.coordinates()[1].latitude(), DELTA)
    }

    @Test
    fun combinePointAndMultiPointToMultiPoint() {
        val pointAndMultiPointFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(Point.fromLngLat(-2.46, 27.6835)),
                    Feature.fromGeometry(
                        MultiPoint.fromLngLats(
                            listOf(
                                Point.fromLngLat(41.83, 7.3624),
                                Point.fromLngLat(100.0, 101.0)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection =
            combine(pointAndMultiPointFeatureCollection)

        assertNotNull(combinedFeatureCollection)

        val multiPoint = combinedFeatureCollection.features()!![0].geometry() as MultiPoint?
        assertNotNull(multiPoint)

        assertEquals(-2.46, multiPoint!!.coordinates()[0].longitude(), DELTA)
        assertEquals(27.6835, multiPoint.coordinates()[0].latitude(), DELTA)
        assertEquals(41.83, multiPoint.coordinates()[1].longitude(), DELTA)
        assertEquals(7.3624, multiPoint.coordinates()[1].latitude(), DELTA)
        assertEquals(100.0, multiPoint.coordinates()[2].longitude(), DELTA)
        assertEquals(101.0, multiPoint.coordinates()[2].latitude(), DELTA)
    }

    @Test
    fun combineTwoLineStringsToMultiLineString() {
        val lineStringFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            listOf(
                                Point.fromLngLat(-11.25, 55.7765),
                                Point.fromLngLat(41.1328, 22.91792)
                            )
                        )
                    ),
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            listOf(
                                Point.fromLngLat(3.8671, 19.3111),
                                Point.fromLngLat(20.742, -20.3034)
                            )
                        )
                    )
                )
            )

        val featureCollectionWithNewMultiLineStringObject = combine(lineStringFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiLineStringObject)

        val multiLineString =
            featureCollectionWithNewMultiLineStringObject.features()!![0].geometry() as MultiLineString?
        assertNotNull(multiLineString)

        // Checking the first LineString in the MultiLineString
        assertEquals(-11.25, multiLineString!!.coordinates()[0][0].longitude(), DELTA)
        assertEquals(55.7765, multiLineString.coordinates()[0][0].latitude(), DELTA)

        // Checking the second LineString in the MultiLineString
        assertEquals(41.1328, multiLineString.coordinates()[0][1].longitude(), DELTA)
        assertEquals(22.91792, multiLineString.coordinates()[0][1].latitude(), DELTA)
    }

    @Test
    fun combineLineStringAndMultiLineStringToMultiLineString() {
        val lineStringFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            listOf(
                                Point.fromLngLat(-11.25, 55.7765),
                                Point.fromLngLat(41.1328, 22.91792)
                            )
                        )
                    ),
                    Feature.fromGeometry(
                        MultiLineString.fromLineStrings(
                            listOf(
                                LineString.fromLngLats(
                                    listOf(
                                        Point.fromLngLat(102.0, -10.0),
                                        Point.fromLngLat(130.0, 4.0)
                                    )
                                ),
                                LineString.fromLngLats(
                                    listOf(
                                        Point.fromLngLat(40.0, -20.0),
                                        Point.fromLngLat(150.0, 18.0)
                                    )
                                )
                            )
                        )
                    )
                )
            )

        val featureCollectionWithNewMultiLineStringObject =
            combine(lineStringFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiLineStringObject)

        val multiLineString = featureCollectionWithNewMultiLineStringObject
            .features()!![0].geometry() as MultiLineString?
        assertNotNull(multiLineString)

        // Checking the first LineString in the MultiLineString
        assertEquals(-11.25, multiLineString!!.coordinates()[0][0].longitude(), DELTA)
        assertEquals(55.7765, multiLineString.coordinates()[0][0].latitude(), DELTA)

        assertEquals(41.1328, multiLineString.coordinates()[0][1].longitude(), DELTA)
        assertEquals(22.91792, multiLineString.coordinates()[0][1].latitude(), DELTA)

        // Checking the second LineString in the MultiLineString
        assertEquals(102.0, multiLineString.coordinates()[1][0].longitude(), DELTA)
        assertEquals(-10.0, multiLineString.coordinates()[1][0].latitude(), DELTA)

        assertEquals(130.0, multiLineString.coordinates()[1][1].longitude(), DELTA)
        assertEquals(4.0, multiLineString.coordinates()[1][1].latitude(), DELTA)

        // Checking the third LineString in the MultiLineString
        assertEquals(40.0, multiLineString.coordinates()[2][0].longitude(), DELTA)
        assertEquals(-20.0, multiLineString.coordinates()[2][0].latitude(), DELTA)

        assertEquals(150.0, multiLineString.coordinates()[2][1].longitude(), DELTA)
        assertEquals(18.0, multiLineString.coordinates()[2][1].latitude(), DELTA)
    }

    @Test
    fun combinePolygonToMultiPolygon() {
        val polygonFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(
                        Polygon.fromLngLats(
                            listOf(
                                listOf(
                                    Point.fromLngLat(61.938950426660604, 5.9765625),
                                    Point.fromLngLat(52.696361078274485, 33.046875),
                                    Point.fromLngLat(69.90011762668541, 28.828124999999996),
                                    Point.fromLngLat(61.938950426660604, 5.9765625)
                                )
                            )
                        )
                    ),
                    Feature.fromGeometry(
                        Polygon.fromLngLats(
                            listOf(
                                listOf(
                                    Point.fromLngLat(11.42578125, 16.636191878397664),
                                    Point.fromLngLat(7.91015625, -9.102096738726443),
                                    Point.fromLngLat(31.113281249999996, 17.644022027872726),
                                    Point.fromLngLat(11.42578125, 16.636191878397664)
                                )
                            )
                        )
                    )
                )
            )

        val featureCollectionWithNewMultiPolygonObject = combine(polygonFeatureCollection)
        assertNotNull(featureCollectionWithNewMultiPolygonObject)

        val multiPolygon =
            featureCollectionWithNewMultiPolygonObject.features()!![0].geometry() as MultiPolygon?
        assertNotNull(multiPolygon)

        // Checking the first Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(
            61.938950426660604,
            multiPolygon!!.coordinates()[0][0][0].longitude(),
            DELTA
        )
        assertEquals(5.9765625, multiPolygon.coordinates()[0][0][0].latitude(), DELTA)

        // Checking the second Point
        assertEquals(
            52.696361078274485,
            multiPolygon.coordinates()[0][0][1].longitude(),
            DELTA
        )
        assertEquals(33.046875, multiPolygon.coordinates()[0][0][1].latitude(), DELTA)

        // Checking the second Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon.coordinates()[1][0][0].longitude(), DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates()[1][0][0].latitude(),
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates()[1][0][1].longitude(), DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates()[1][0][1].latitude(),
            DELTA
        )
    }

    @Test
    fun combinePolygonAndMultiPolygonToMultiPolygon() {
        val polygonFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(
                        Polygon.fromLngLats(
                            listOf(
                                listOf(
                                    Point.fromLngLat(61.938950426660604, 5.9765625),
                                    Point.fromLngLat(52.696361078274485, 33.046875),
                                    Point.fromLngLat(69.90011762668541, 28.828124999999996),
                                    Point.fromLngLat(61.938950426660604, 5.9765625)
                                )
                            )
                        )
                    ),
                    Feature.fromGeometry(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon.fromLngLats(
                                    listOf(
                                        listOf(
                                            Point.fromLngLat(11.42578125, 16.636191878397664),
                                            Point.fromLngLat(7.91015625, -9.102096738726443),
                                            Point.fromLngLat(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point.fromLngLat(11.42578125, 16.636191878397664)
                                        )
                                    )
                                ),
                                Polygon.fromLngLats(
                                    listOf(
                                        listOf(
                                            Point.fromLngLat(30.0, 0.0),
                                            Point.fromLngLat(102.0, 0.0),
                                            Point.fromLngLat(103.0, 1.0),
                                            Point.fromLngLat(30.0, 0.0)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(polygonFeatureCollection)
        assertNotNull(combinedFeatureCollection)

        val multiPolygon = combinedFeatureCollection.features()!![0].geometry() as MultiPolygon?
        assertNotNull(multiPolygon)

        // Checking the first Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(
            61.938950426660604,
            multiPolygon!!.coordinates()[0][0][0].longitude(),
            DELTA
        )
        assertEquals(5.9765625, multiPolygon.coordinates()[0][0][0].latitude(), DELTA)

        // Checking the second Point
        assertEquals(
            52.696361078274485,
            multiPolygon.coordinates()[0][0][1].longitude(),
            DELTA
        )
        assertEquals(33.046875, multiPolygon.coordinates()[0][0][1].latitude(), DELTA)

        // Checking the second Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon.coordinates()[1][0][0].longitude(), DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates()[1][0][0].latitude(),
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates()[1][0][1].longitude(), DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates()[1][0][1].latitude(),
            DELTA
        )

        // Checking the third Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(30.0, multiPolygon.coordinates()[2][0][0].longitude(), DELTA)
        assertEquals(0.0, multiPolygon.coordinates()[2][0][0].latitude(), DELTA)

        // Checking the second Point
        assertEquals(102.0, multiPolygon.coordinates()[2][0][1].longitude(), DELTA)
        assertEquals(0.0, multiPolygon.coordinates()[2][0][1].latitude(), DELTA)
    }

    @Test
    fun combinePolygonAndMultiPolygonAndPointToMultiPolygon() {
        val featureCollectionWithPointPolygonAndMultiPolygon =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(
                        Point.fromLngLat(-2.46, 27.6835)
                    ),
                    Feature.fromGeometry(
                        Polygon.fromLngLats(
                            listOf(
                                listOf(
                                    Point.fromLngLat(61.938950426660604, 5.9765625),
                                    Point.fromLngLat(52.696361078274485, 33.046875),
                                    Point.fromLngLat(69.90011762668541, 28.828124999999996),
                                    Point.fromLngLat(61.938950426660604, 5.9765625)
                                )
                            )
                        )
                    ),
                    Feature.fromGeometry(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon.fromLngLats(
                                    listOf(
                                        listOf(
                                            Point.fromLngLat(11.42578125, 16.636191878397664),
                                            Point.fromLngLat(7.91015625, -9.102096738726443),
                                            Point.fromLngLat(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point.fromLngLat(11.42578125, 16.636191878397664)
                                        )
                                    )
                                ),
                                Polygon.fromLngLats(
                                    listOf(
                                        listOf(
                                            Point.fromLngLat(30.0, 0.0),
                                            Point.fromLngLat(102.0, 0.0),
                                            Point.fromLngLat(103.0, 1.0),
                                            Point.fromLngLat(30.0, 0.0)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(featureCollectionWithPointPolygonAndMultiPolygon)
        assertNotNull(combinedFeatureCollection)
        var multiPolygon: MultiPolygon? = null
        var multiPoint: MultiPoint? = null
        for (x in combinedFeatureCollection.features()!!.indices) {
            val singleFeature = combinedFeatureCollection.features()!![x]
            if (singleFeature.geometry() is MultiPolygon) {
                multiPolygon = combinedFeatureCollection.features()!![x].geometry() as MultiPolygon?
            }
            if (singleFeature.geometry() is MultiPoint) {
                multiPoint = combinedFeatureCollection.features()!![x].geometry() as MultiPoint?
            }
        }
        assertNotNull(multiPolygon)
        assertNotNull(multiPoint)

        // Checking the first Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(
            61.938950426660604,
            multiPolygon!!.coordinates()[0][0][0].longitude(),
            DELTA
        )
        assertEquals(5.9765625, multiPolygon.coordinates()[0][0][0].latitude(), DELTA)

        // Checking the second Point
        assertEquals(
            52.696361078274485,
            multiPolygon.coordinates()[0][0][1].longitude(),
            DELTA
        )
        assertEquals(33.046875, multiPolygon.coordinates()[0][0][1].latitude(), DELTA)

        // Checking the second Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon.coordinates()[1][0][0].longitude(), DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates()[1][0][0].latitude(),
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates()[1][0][1].longitude(), DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates()[1][0][1].latitude(),
            DELTA
        )

        // Checking the third Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(30.0, multiPolygon.coordinates()[2][0][0].longitude(), DELTA)
        assertEquals(0.0, multiPolygon.coordinates()[2][0][0].latitude(), DELTA)

        // Checking the second Point
        assertEquals(102.0, multiPolygon.coordinates()[2][0][1].longitude(), DELTA)
        assertEquals(0.0, multiPolygon.coordinates()[2][0][1].latitude(), DELTA)
    }

    @Test
    fun combinePointAndLineStringGeometry() {
        val pointAndLineStringFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(Point.fromLngLat(-2.46, 27.6835)),
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            listOf(
                                Point.fromLngLat(-11.25, 55.7765),
                                Point.fromLngLat(41.1328, 22.91792)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(pointAndLineStringFeatureCollection)
        assertNotNull(combinedFeatureCollection)
        var multiPoint: MultiPoint? = null
        var multiLineString: MultiLineString? = null
        for (x in combinedFeatureCollection.features()!!.indices) {
            val singleFeature = combinedFeatureCollection.features()!![x]
            if (singleFeature.geometry() is MultiPoint) {
                multiPoint = combinedFeatureCollection.features()!![x].geometry() as MultiPoint?
            }
            if (singleFeature.geometry() is MultiLineString) {
                multiLineString =
                    combinedFeatureCollection.features()!![x].geometry() as MultiLineString?
            }
        }
        assertNotNull(multiPoint)
        assertNotNull(multiLineString)

        // Checking the LineString in the MultiLineString

        // Checking the first LineString location
        assertEquals(-11.25, multiLineString!!.coordinates()[0][0].longitude(), DELTA)
        assertEquals(55.7765, multiLineString.coordinates()[0][0].latitude(), DELTA)

        // Checking the second LineString location
        assertEquals(41.1328, multiLineString.coordinates()[0][1].longitude(), DELTA)
        assertEquals(22.91792, multiLineString.coordinates()[0][1].latitude(), DELTA)

        // Checking the Point in the MultiPoint

        // Checking the first and only Point
        assertEquals(-2.46, multiPoint!!.coordinates()[0].longitude(), DELTA)
        assertEquals(27.6835, multiPoint.coordinates()[0].latitude(), DELTA)
    }

    @Test
    fun combinePointAndMultiPolygonAndLineStringGeometry() {
        val pointMultiPolygonAndLineStringFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(Point.fromLngLat(-2.46, 27.6835)),
                    Feature.fromGeometry(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon.fromLngLats(
                                    listOf(
                                        listOf(
                                            Point.fromLngLat(11.42578125, 16.636191878397664),
                                            Point.fromLngLat(7.91015625, -9.102096738726443),
                                            Point.fromLngLat(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point.fromLngLat(11.42578125, 16.636191878397664)
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            listOf(
                                Point.fromLngLat(-11.25, 55.7765),
                                Point.fromLngLat(41.1328, 22.91792)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(pointMultiPolygonAndLineStringFeatureCollection)
        assertNotNull(combinedFeatureCollection)
        var multiPoint: MultiPoint? = null
        var multiLineString: MultiLineString? = null
        var multiPolygon: MultiPolygon? = null
        for (x in combinedFeatureCollection.features()!!.indices) {
            val singleFeature = combinedFeatureCollection.features()!![x]
            if (singleFeature.geometry() is MultiPoint) {
                multiPoint = combinedFeatureCollection.features()!![x].geometry() as MultiPoint?
            }
            if (singleFeature.geometry() is MultiLineString) {
                multiLineString =
                    combinedFeatureCollection.features()!![x].geometry() as MultiLineString?
            }
            if (singleFeature.geometry() is MultiPolygon) {
                multiPolygon = combinedFeatureCollection.features()!![x].geometry() as MultiPolygon?
            }
        }
        assertNotNull(multiPoint)
        assertNotNull(multiLineString)
        assertNotNull(multiPolygon)

        // Checking the Polygon in the MultiPolygon

        // Checking the first Point
        assertEquals(11.42578125, multiPolygon!!.coordinates()[0][0][0].longitude(), DELTA)
        assertEquals(
            16.636191878397664,
            multiPolygon.coordinates()[0][0][0].latitude(),
            DELTA
        )

        // Checking the second Point
        assertEquals(7.91015625, multiPolygon.coordinates()[0][0][1].longitude(), DELTA)
        assertEquals(
            -9.102096738726443,
            multiPolygon.coordinates()[0][0][1].latitude(),
            DELTA
        )

        // Checking the LineString in the MultiLineString

        // Checking the first LineString location
        assertEquals(-11.25, multiLineString!!.coordinates()[0][0].longitude(), DELTA)
        assertEquals(55.7765, multiLineString.coordinates()[0][0].latitude(), DELTA)

        // Checking the second LineString location
        assertEquals(41.1328, multiLineString.coordinates()[0][1].longitude(), DELTA)
        assertEquals(22.91792, multiLineString.coordinates()[0][1].latitude(), DELTA)

        // Checking the Point in the MultiPoint

        // Checking the first and only Point
        assertEquals(-2.46, multiPoint!!.coordinates()[0].longitude(), DELTA)
        assertEquals(27.6835, multiPoint.coordinates()[0].latitude(), DELTA)
    }

    @Test
    fun combine_featureCollectionSizeCheck() {
        val pointMultiPolygonAndLineStringFeatureCollection =
            FeatureCollection.fromFeatures(
                listOf(
                    Feature.fromGeometry(Point.fromLngLat(-2.46, 27.6835)),
                    Feature.fromGeometry(
                        MultiPolygon.fromPolygons(
                            listOf(
                                Polygon.fromLngLats(
                                    listOf(
                                        listOf(
                                            Point.fromLngLat(11.42578125, 16.636191878397664),
                                            Point.fromLngLat(7.91015625, -9.102096738726443),
                                            Point.fromLngLat(
                                                31.113281249999996,
                                                17.644022027872726
                                            ),
                                            Point.fromLngLat(11.42578125, 16.636191878397664)
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            listOf(
                                Point.fromLngLat(-11.25, 55.7765),
                                Point.fromLngLat(41.1328, 22.91792)
                            )
                        )
                    )
                )
            )

        val combinedFeatureCollection = combine(pointMultiPolygonAndLineStringFeatureCollection)
        assertNotNull(combinedFeatureCollection)
        assertEquals(3, combinedFeatureCollection.features()!!.size.toLong())
    }

    @Test
    fun combineEmptyFeatureCollectionThrowsException() {
        assertThrows(TurfException::class.java) {
            combine(
                FeatureCollection.fromJson(
                    """{
                          "type": "FeatureCollection",
                          "features": []
                        }"""
                )
            )
        }
    }

    @Test
    fun explodePointSingleFeature() {
        val point = Point.fromLngLat(102.0, 0.5)
        assertEquals(
            1, explode(
                Feature.fromGeometry(
                    point
                )
            ).features()!!.size.toLong()
        )
    }

    @Test
    fun explodeMultiPointSingleFeature() {
        val multiPoint = MultiPoint.fromJson(loadJsonFixture(TURF_EXPLODE_MULTI_POINT))
        assertEquals(
            4, explode(
                Feature.fromGeometry(
                    multiPoint
                )
            ).features()!!.size.toLong()
        )
    }

    @Test
    fun explodeLineStringSingleFeature() {
        val lineString = LineString.fromJson(loadJsonFixture(TURF_EXPLODE_LINESTRING))
        assertEquals(
            4, explode(
                Feature.fromGeometry(
                    lineString
                )
            ).features()!!.size.toLong()
        )
    }

    @Test
    fun explodePolygonSingleFeature() {
        val polygon = Polygon.fromLngLats(
            listOf(
                listOf(
                    Point.fromLngLat(0.0, 101.0),
                    Point.fromLngLat(1.0, 101.0),
                    Point.fromLngLat(1.0, 100.0),
                    Point.fromLngLat(0.0, 100.0)
                )
            )
        )
        assertEquals(
            3, explode(
                Feature.fromGeometry(
                    polygon
                )
            ).features()!!.size.toLong()
        )
    }

    @Test
    fun explodeMultiLineStringSingleFeature() {
        val multiLineString =
            MultiLineString.fromJson(loadJsonFixture(TURF_EXPLODE_MULTILINESTRING))
        assertEquals(
            4, explode(
                Feature.fromGeometry(
                    multiLineString
                )
            ).features()!!.size.toLong()
        )
    }

    @Test
    fun explodeMultiPolygonSingleFeature() {
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_EXPLODE_MULTIPOLYGON))
        assertEquals(
            12, explode(
                Feature.fromGeometry(
                    multiPolygon
                )
            ).features()!!.size.toLong()
        )
    }

    @Test
    fun explodeGeometryCollectionSingleFeature() {
        val geometryCollection = GeometryCollection.fromJson(
            loadJsonFixture(
                TURF_EXPLODE_GEOMETRY_COLLECTION
            )
        )
        assertEquals(
            3, explode(
                Feature.fromGeometry(
                    geometryCollection
                )
            ).features()!!.size.toLong()
        )
    }

    @Test
    fun explodeFeatureCollection() {
        val featureCollection = FeatureCollection.fromFeatures(
            arrayOf(
                Feature.fromGeometry(
                    MultiLineString.fromJson(
                        loadJsonFixture(
                            TURF_EXPLODE_MULTILINESTRING
                        )
                    )
                ),
                Feature.fromGeometry(
                    MultiPolygon.fromJson(
                        loadJsonFixture(
                            TURF_EXPLODE_MULTIPOLYGON
                        )
                    )
                )
            )
        )
        assertEquals(16, explode(featureCollection).features()!!.size.toLong())
    }

    @Test
    fun polygonToLine_GeometryPolygon() {
        val polygon = Polygon.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_GEOMETRY_POLYGON
            )
        )
        val expected = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_GEOMETRY_POLYGON
            )
        )
        compareJson(expected.toJson(), polygonToLine(polygon)!!.toJson())
    }

    @Test
    fun polygonToLine_Polygon() {
        val polygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_POLYGON
            )
        )
        val expected = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_POLYGON
            )
        )
        compareJson(expected.toJson(), polygonToLine(polygon).toJson())
    }

    @Test
    fun polygonToLine_PolygonWithHole() {
        val polygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_POLYGON_WITH_HOLE
            )
        )
        val expected = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_POLYGON_WITH_HOLE
            )
        )
        compareJson(expected.toJson(), polygonToLine(polygon).toJson())
    }

    @Test
    fun polygonToLine_MultiPolygon() {
        val multiPolygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON
            )
        )
        val expected =
            FeatureCollection.fromJson(loadJsonFixture(TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON))
        compareJson(expected.toJson(), multiPolygonToLine(multiPolygon).toJson())
    }

    @Test
    fun polygonToLine_MultiPolygonWithHoles() {
        val multiPolygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_WITH_HOLES
            )
        )
        val expected =
            FeatureCollection.fromJson(loadJsonFixture(TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_WITH_HOLES))
        compareJson(expected.toJson(), multiPolygonToLine(multiPolygon).toJson())
    }

    @Test
    fun polygonToLine_MultiPolygonWithOuterDoughnut() {
        val multiPolygon = Feature.fromJson(
            loadJsonFixture(
                TURF_POLYGON_TO_LINE_PATH_IN + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_OUTER_DOUGHNUT
            )
        )
        val expected =
            FeatureCollection.fromJson(loadJsonFixture(TURF_POLYGON_TO_LINE_PATH_OUT + TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_OUTER_DOUGHNUT))
        compareJson(expected.toJson(), multiPolygonToLine(multiPolygon).toJson())
    }

    companion object {
        private const val TURF_EXPLODE_MULTI_POINT = "turf-explode/multipoint.geojson"
        private const val TURF_EXPLODE_LINESTRING = "turf-explode/linestring.geojson"
        private const val TURF_EXPLODE_MULTILINESTRING = "turf-explode/multilinestring.geojson"
        private const val TURF_EXPLODE_MULTIPOLYGON = "turf-explode/multipolygon.geojson"
        private const val TURF_EXPLODE_GEOMETRY_COLLECTION =
            "turf-explode/geometrycollection.geojson"

        private const val TURF_POLYGON_TO_LINE_PATH_IN = "turf-polygon-to-line/in/"
        private const val TURF_POLYGON_TO_LINE_PATH_OUT = "turf-polygon-to-line/expected/"

        private const val TURF_POLYGON_TO_LINE_FILENAME_POLYGON = "polygon.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_GEOMETRY_POLYGON =
            "geometry-polygon.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_POLYGON_WITH_HOLE =
            "polygon-with-hole.geojson"

        private const val TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON = "multi-polygon.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_OUTER_DOUGHNUT =
            "multi-polygon-outer-doughnut.geojson"
        private const val TURF_POLYGON_TO_LINE_FILENAME_MULTIPOLYGON_WITH_HOLES =
            "multi-polygon-with-holes.geojson"
    }
}
