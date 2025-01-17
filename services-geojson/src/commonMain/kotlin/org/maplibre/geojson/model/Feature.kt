package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

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
 * ```json
 * {
 *   "TYPE": "Feature",
 *   "geometry": {
 *     "TYPE": "Point",
 *     "coordinates": [102.0, 0.5]
 *   },
 *   "properties": {
 *   "prop0": "value0"
 * }
 * ```
 *
 * @param geometry   a single geometry which makes up this feature object
 * @param properties a map with [JsonElement]s containing the feature properties
 * @param bbox       optionally include a bbox definition as a double array
 * @param id         common identifier of this feature
 * @since 1.0.0
 */
@Serializable
@SerialName("Feature")
open class Feature
@JvmOverloads
constructor(
    val geometry: Geometry? = null,
    var properties: MutableMap<String, JsonElement>? = null,
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
    fun getStringProperty(key: String): String? {
        return properties?.get(key)?.jsonPrimitive?.contentOrNull
    }

    /**
     * Convenience method to get a Int member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getIntProperty(key: String): Int? {
        return properties?.get(key)?.jsonPrimitive?.intOrNull
    }

    /**
     * Convenience method to get a Long member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getLongProperty(key: String): Long? {
        return properties?.get(key)?.jsonPrimitive?.longOrNull
    }

    /**
     * Convenience method to get a Float member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getFloatProperty(key: String): Float? {
        return properties?.get(key)?.jsonPrimitive?.floatOrNull
    }

    /**
     * Convenience method to get a Double member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getDoubleProperty(key: String): Double? {
        return properties?.get(key)?.jsonPrimitive?.doubleOrNull
    }

    /**
     * Convenience method to get a Boolean member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getBooleanProperty(key: String): Boolean? {
        return properties?.get(key)?.jsonPrimitive?.booleanOrNull
    }

    /**
     * Convenience method to add a String member.
     *
     * @param key   name of the member
     * @param value the String value associated with the member
     * @since 1.0.0
     */
    fun addProperty(key: String, value: String) {
        createPropertiesIfNecessary()[key] = JsonPrimitive(value)
    }

    /**
     * Convenience method to add a Integer member.
     *
     * @param key   name of the member
     * @param value the Int value associated with the member
     */
    fun addProperty(key: String, value: Int) {
        createPropertiesIfNecessary()[key] = JsonPrimitive(value)
    }

    /**
     * Convenience method to add a Long member.
     *
     * @param key   name of the member
     * @param value the Long value associated with the member
     */
    fun addProperty(key: String, value: Long) {
        createPropertiesIfNecessary()[key] = JsonPrimitive(value)
    }

    /**
     * Convenience method to add a Float member.
     *
     * @param key   name of the member
     * @param value the Float value associated with the member
     */
    fun addProperty(key: String, value: Float) {
        createPropertiesIfNecessary()[key] = JsonPrimitive(value)
    }

    /**
     * Convenience method to add a Double member.
     *
     * @param key   name of the member
     * @param value the Double value associated with the member
     */
    fun addProperty(key: String, value: Double) {
        createPropertiesIfNecessary()[key] = JsonPrimitive(value)
    }

    /**
     * Convenience method to add a Boolean member.
     *
     * @param key   name of the member
     * @param value the Boolean value associated with the member
     * @since 1.0.0
     */
    fun addProperty(key: String, value: Boolean) {
        createPropertiesIfNecessary()[key] = JsonPrimitive(value)
    }

    /**
     * Get properties or create a new empty property map if it is null.
     */
    private fun createPropertiesIfNecessary(): MutableMap<String, JsonElement> {
        if (properties == null) {
            properties = mutableMapOf()
        }

        return properties!!
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Feature
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    fun copy(
        geometry: Geometry? = this.geometry,
        properties: MutableMap<String, JsonElement>? = this.properties,
        id: String? = this.id,
        bbox: BoundingBox? = this.bbox
    ): Feature {
        return Feature(geometry, properties, id, bbox)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Feature

        if (geometry != other.geometry) return false
        if (properties != other.properties) return false
        if (id != other.id) return false
        if (bbox != other.bbox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = geometry?.hashCode() ?: 0
        result = 31 * result + (properties?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (bbox?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Feature(geometry=$geometry, properties=$properties, id=$id, bbox=$bbox)"
    }

    companion object {

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Feature object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Feature
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): Feature = json.decodeFromString(jsonString)
    }
}
