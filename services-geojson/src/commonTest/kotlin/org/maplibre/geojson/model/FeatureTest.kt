package org.maplibre.geojson.model

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson

class FeatureTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)
        assertNotNull(feature)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)
        assertNull(feature.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)

        val featureJsonString = feature.toJson()
        compareJson(
            featureJsonString,
            "{\"type\":\"Feature\",\"geometry\":{\"type\":"
                    + "\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}"
        )
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val feature = Feature(lineString, bbox = bbox)
        assertNotNull(feature.bbox)
        assertEquals(1.0, feature.bbox!!.west, DELTA)
        assertEquals(2.0, feature.bbox!!.south, DELTA)
        assertEquals(3.0, feature.bbox!!.east, DELTA)
        assertEquals(4.0, feature.bbox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val feature = Feature(lineString, bbox = bbox)
        compareJson(
            "{\"type\":\"Feature\",\"bbox\":[1.0,2.0,3.0,4.0],\"geometry\":"
                    + "{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}",
            feature.toJson()
        )
    }

    @Test
    fun point_feature_fromJson() {
        val json = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"Point\", \"coordinates\": [ 125.6, 10.1] }," +
                "\"properties\": {\"name\": \"Dinagat Islands\" }}"
        val feature = Feature.fromJson(json)
        assertEquals((feature.geometry as Point).longitude, 125.6, DELTA)
        assertEquals((feature.geometry as Point).latitude, 10.1, DELTA)
        assertEquals(feature.properties!!["name"]!!.jsonPrimitive.content, "Dinagat Islands")
    }

    @Test
    fun linestring_feature_fromJson() {
        val json = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"LineString\", " +
                " \"coordinates\": [[ 102.0, 20],[103.0, 3.0],[104.0, 4.0], [105.0, 5.0]]}," +
                "\"properties\": {\"name\": \"line name\" }}"
        val feature = Feature.fromJson(json)
        assertNotNull(feature.geometry)
        val coordinates = (feature.geometry as LineString).coordinates
        assertNotNull(coordinates)
        assertEquals(4, coordinates.size.toLong())
        assertEquals(105.0, coordinates[3].longitude, DELTA)
        assertEquals(5.0, coordinates[3].latitude, DELTA)
        assertEquals("line name", feature.properties!!["name"]!!.jsonPrimitive.content)
    }

    @Test
    fun point_feature_toJson() {
        val properties: MutableMap<String, JsonElement> = mutableMapOf(
            "name" to JsonPrimitive("Dinagat Islands")
        )
        val geo = Feature(
            Point(125.6, 10.1),
            properties = properties
        )
        val geoJsonString = geo.toJson()

        val expectedJson = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"Point\", \"coordinates\": [ 125.6, 10.1] }," +
                "\"properties\": {\"name\": \"Dinagat Islands\" }}"

        compareJson(expectedJson, geoJsonString)
    }

    @Test
    fun linestring_feature_toJson() {
        val properties: MutableMap<String, JsonElement> = mutableMapOf(
            "name" to JsonPrimitive("Dinagat Islands")
        )

        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val lineString = LineString(points)

        val geo = Feature(lineString, properties)
        val geoJsonString = geo.toJson()

        val expectedJson = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"LineString\", \"coordinates\": [[1.0,1.0],[2.0,2.0],[3.0,3.0]]}," +
                "\"properties\": {\"name\": \"Dinagat Islands\" }}"

        compareJson(expectedJson, geoJsonString)
    }

    @Test
    fun testNullProperties() {
        val points = listOf(
            Point(0.1, 2.3),
            Point(4.5, 6.7)
        )

        val line = LineString(points)
        val feature = Feature(line)
        val jsonString = feature.toJson()
        assertFalse(jsonString.contains("\"properties\":{}"))

        // Feature (empty Properties) -> Json (null Properties) -> Equavalent Feature
        val featureFromJson: Feature = Feature.fromJson(jsonString)
        assertEquals(featureFromJson, feature)
    }

    @Test
    fun testNonNullProperties() {
        val points = listOf(
            Point(0.1, 2.3),
            Point(4.5, 6.7)
        )

        val line = LineString(points)
        val properties: MutableMap<String, JsonElement> = mutableMapOf(
            "key" to JsonPrimitive("value")
        )

        val feature = Feature(line, properties)
        val jsonString = feature.toJson()
        assertTrue(jsonString.contains("\"properties\":{\"key\":\"value\"}"))

        // Feature (non-empty Properties) -> Json (non-empty Properties) -> Equavalent Feature
        assertEquals(Feature.fromJson(jsonString), feature)
    }

    @Test
    fun testNullPropertiesJson() {
        val jsonString =
            ("{\"type\":\"Feature\"," +
                    " \"bbox\":[1.0,2.0,3.0,4.0]," +
                    " \"geometry\":"
                    + "{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}")

        val feature = Feature.fromJson(jsonString)

        // Json( null Properties) -> Feature (empty Properties) -> Json(null Properties)
        val fromFeatureJsonString = feature.toJson()
        compareJson(fromFeatureJsonString, jsonString)
    }


    @Test
    fun pointFeature_fromJson_toJson() {
        val jsonString =
            "{\"id\" : \"id0\"," +
                    " \"bbox\": [-120.0, -60.0, 120.0, 60.0]," +
                    " \"geometry\": {" +
                    "    \"bbox\": [-110.0, -50.0, 110.0, 50.0]," +
                    "    \"coordinates\": [ 100.0, 0.0], " +
                    "     \"type\": \"Point\"}," +
                    "\"type\": \"Feature\"," +
                    "\"properties\": {\"prop0\": \"value0\", \"prop1\": \"value1\"}" +
                    "}"


        val featureFromJson = Feature.fromJson(jsonString)
        val jsonStringFromFeature = featureFromJson.toJson()

        compareJson(jsonString, jsonStringFromFeature)
    }

    @Test
    fun feature_getProperty_empty_property() {
        val jsonString =
            ("{\"type\":\"Feature\"," +
                    " \"geometry\":"
                    + "{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}")

        val feature = Feature.fromJson(jsonString)
        var value: Any? = feature.getStringProperty("does_not_exist")
        assertNull(value)

        value = feature.getBooleanProperty("does_not_exist")
        assertNull(value)

        value = feature.getIntProperty("does_not_exist")
        assertNull(value)

        value = feature.getLongProperty("does_not_exist")
        assertNull(value)

        value = feature.getFloatProperty("does_not_exist")
        assertNull(value)

        value = feature.getDoubleProperty("does_not_exist")
        assertNull(value)
    }

    @Test
    fun feature_property_doesnotexist() {
        val jsonString =
            "{ \"type\": \"Feature\"," +
                    "\"geometry\": { \"type\": \"LineString\", \"coordinates\": [[1,1],[2,2],[3,3]]}," +
                    "\"properties\": {\"some_name\": \"some_value\" }}"
        val feature = Feature.fromJson(jsonString)
        var value: Any? = feature.getStringProperty("does_not_exist")
        assertNull(value)

        value = feature.getBooleanProperty("does_not_exist")
        assertNull(value)

        value = feature.getFloatProperty("does_not_exist")
        assertNull(value)
    }

    @Test
    fun feature_getProperty_content_property() {
        val jsonString =
            "{ \"type\": \"Feature\"," +
                    "\"geometry\": { \"type\": \"LineString\", \"coordinates\": [[1,1],[2,2],[3,3]]}," +
                    "\"properties\": {\"long\": 42, \"double\": 4.2, \"string\": \"string\", \"boolean\": true }}"

        val feature = Feature.fromJson(jsonString)
        assertEquals(42, feature.getIntProperty("long"))
        assertEquals(42L, feature.getLongProperty("long"))
        assertEquals(4.2f, feature.getFloatProperty("double"))
        assertEquals(4.2, feature.getDoubleProperty("double"))
        assertEquals("string", feature.getStringProperty("string"))
        assertEquals(true, feature.getBooleanProperty("boolean"))
    }
}
