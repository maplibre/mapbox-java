package org.maplibre.geojson.common

import com.google.gson.JsonParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.JsonElement as KtxJsonElement
import kotlinx.serialization.json.JsonObject as KtxJsonObject
import com.google.gson.JsonElement as GsonJsonElement
import com.google.gson.JsonObject as GsonJsonObject
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.Polygon
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.Geometry
import org.maplibre.geojson.model.GeometryCollection
import org.maplibre.geojson.Feature as JvmFeature
import org.maplibre.geojson.FeatureCollection as JvmFeatureCollection
import org.maplibre.geojson.Point as JvmPoint
import org.maplibre.geojson.MultiPoint as JvmMultiPoint
import org.maplibre.geojson.LineString as JvmLineString
import org.maplibre.geojson.MultiLineString as JvmMultiLineString
import org.maplibre.geojson.Polygon as JvmPolygon
import org.maplibre.geojson.MultiPolygon as JvmMultiPolygon
import org.maplibre.geojson.BoundingBox as JvmBoundingBox
import org.maplibre.geojson.Geometry as JvmGeometry
import org.maplibre.geojson.GeometryCollection as JvmGeometryCollection

fun Geometry.toJvm(): JvmGeometry {
    return JvmGeometry.fromJson(toJson())
}

fun JvmGeometry.toCommon(): Geometry {
    return Geometry.fromJson(toJson())
}

fun GeometryCollection.toJvm(): JvmGeometryCollection {
    return JvmGeometryCollection(
        "GeometryCollection",
        bbox?.toJvm(),
        geometries.map { geom -> geom.toJvm() }
    )
}

fun Feature.toJvm(): JvmFeature {
    return JvmFeature(
        "Feature",
        bbox?.toJvm(),
        id,
        geometry?.toJvm(),
        properties?.let { props -> JsonParser.parseString(props.toString()).asJsonObject }
    )
}

fun FeatureCollection.toJvm(): JvmFeatureCollection {
    return JvmFeatureCollection(
        "FeatureCollection",
        bbox?.toJvm(),
        features.map { feat -> feat.toJvm() }
    )
}

fun Point.toJvm(): JvmPoint {
    return JvmPoint(
        "Point",
        bbox?.toJvm(),
        coordinates
    )
}

fun MultiPoint.toJvm(): JvmMultiPoint {
    return JvmMultiPoint(
        "MultiPoint",
        bbox?.toJvm(),
        coordinates.map { coord -> coord.toJvm() }
    )
}

fun LineString.toJvm(): JvmLineString {
    return JvmLineString(
        "LineString",
        bbox?.toJvm(),
        coordinates.map { coord -> coord.toJvm() }
    )
}

fun MultiLineString.toJvm(): JvmMultiLineString {
    return JvmMultiLineString(
        "MultiLineString",
        bbox?.toJvm(),
        coordinates.map { line -> line.map { coord -> coord.toJvm() } }
    )
}

fun Polygon.toJvm(): JvmPolygon {
    return JvmPolygon(
        "Polygon",
        bbox?.toJvm(),
        coordinates.map { line -> line.map { coord -> coord.toJvm() } }
    )
}

fun MultiPolygon.toJvm(): JvmMultiPolygon {
    return JvmMultiPolygon(
        "MultiPolygon",
        bbox?.toJvm(),
        coordinates.map { poly -> poly.map { line -> line.map { coord -> coord.toJvm() } } }
    )
}

fun BoundingBox.toJvm(): JvmBoundingBox {
    return JvmBoundingBox(
        southwest.toJvm(),
        northeast.toJvm()
    )
}

fun GsonJsonObject.toKtxJsonMap(): Map<String, KtxJsonElement> {
    return Json.parseToJsonElement(toString()).jsonObject
}