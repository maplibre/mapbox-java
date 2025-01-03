package org.maplibre.geojson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.maplibre.geojson.utils.json

/**
 * This defines a GeoJson Feature object which represents a spatially bound thing. Every Feature
 * object is a GeoJson object no matter where it occurs in a GeoJson text. A Feature object will
 * always have a "TYPE" member with the value "Feature".
 *
 *
 * A Feature object has a member with the name "geometry". The value of the geometry member SHALL be
 * either a Geometry object or, in the case that the Feature is unlocated, a JSON null value.
 *
 *
 * A Feature object has a member with the name "properties". The value of the properties member is
 * an object (any JSON object or a JSON null value).
 *
 *
 * If a Feature has a commonly used identifier, that identifier SHOULD be included as a member of
 * the Feature object through the [.id] method, and the value of this member is either a
 * JSON string or number.
 *
 *
 * An example of a serialized feature is given below:
 * <pre>
 * {
 * "TYPE": "Feature",
 * "geometry": {
 * "TYPE": "Point",
 * "coordinates": [102.0, 0.5]
 * },
 * "properties": {
 * "prop0": "value0"
 * }
</pre> *
 *
 * @since 1.0.0
 */
@Serializable
@SerialName("Feature")
data class Feature
@JvmOverloads
constructor(
    val geometry: Geometry? = null,
    val properties: Map<String, JsonElement>? = null,
    val id: String? = null,
    override val bbox: BoundingBox? = null,
) : GeoJson {

    /**
     * Convenience method to get a String member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getStringProperty(key: String?): String? {
        return properties?.get(key)?.jsonPrimitive?.contentOrNull
    }

    /**
     * Convenience method to get a Int member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getIntProperty(key: String?): Int? {
        return properties?.get(key)?.jsonPrimitive?.intOrNull
    }

    /**
     * Convenience method to get a Long member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getLongProperty(key: String?): Long? {
        return properties?.get(key)?.jsonPrimitive?.longOrNull
    }

    /**
     * Convenience method to get a Float member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getFloatProperty(key: String?): Float? {
        return properties?.get(key)?.jsonPrimitive?.floatOrNull
    }

    /**
     * Convenience method to get a Double member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getDoubleProperty(key: String?): Double? {
        return properties?.get(key)?.jsonPrimitive?.doubleOrNull
    }

    /**
     * Convenience method to get a Boolean member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getBooleanProperty(key: String?): Boolean? {
        return properties?.get(key)?.jsonPrimitive?.booleanOrNull
    }

    override fun toJson() = json.encodeToString(this)

    companion object {

        @JvmStatic
        fun fromJson(jsonString: String): Feature = json.decodeFromString(jsonString)
    }
}
