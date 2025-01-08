package org.maplibre.geojson

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.maplibre.geojson.common.toCommon
import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.common.toKtxJsonMap
import org.maplibre.geojson.model.Feature as CommonFeature

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
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("Feature", "org.maplibre.geojson.model.Feature"),
)
class Feature internal constructor(
    type: String,
    bbox: BoundingBox?,
    id: String?,
    geometry: Geometry?,
    private var props: JsonObject?
) : CommonFeature(
    geometry = geometry?.toCommon(),
    properties = null,
    id = id,
    bbox = bbox,
), GeoJson {

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [Feature].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `Feature`
     * @since 1.0.0
     */
    override fun type(): String = TYPE

    /**
     * A Feature Collection might have a member named `bbox` to include information on the
     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of
     * size 2*n where n is the number of dimensions represented in the contained feature geometries,
     * with all axes of the most southwesterly point followed by all axes of the more northeasterly
     * point. The axes order of a bbox follows the axes order of geometries.
     *
     * @return a list of double coordinate values describing a bounding box
     * @since 3.0.0
     */
    override fun bbox(): BoundingBox? = bbox?.toJvm()

    /**
     * A feature may have a commonly used identifier which is either a unique String or number.
     *
     * @return a String containing this features unique identification or null if one wasn't given
     * during creation.
     * @since 1.0.0
     */
    fun id(): String? = id

    /**
     * The geometry which makes up this feature. A Geometry object represents points, curves, and
     * surfaces in coordinate space. One of the seven geometries provided inside this library can be
     * passed in through one of the static factory methods.
     *
     * @return a single defined [Geometry] which makes this feature spatially aware
     * @since 1.0.0
     */
    fun geometry(): Geometry? = geometry?.toJvm()

    /**
     * This contains the JSON object which holds the feature properties. The value of the properties
     * member is a [JsonObject] and might be empty if no properties are provided.
     *
     * @return a [JsonObject] which holds this features current properties
     * @since 1.0.0
     */
    fun properties(): JsonObject? = props

    /**
     * Convenience method to add a String member.
     *
     * @param key   name of the member
     * @param value the String value associated with the member
     * @since 1.0.0
     */
    fun addStringProperty(key: String, value: String?) {
        properties()!!.addProperty(key, value)
    }

    /**
     * Convenience method to add a Number member.
     *
     * @param key   name of the member
     * @param value the Number value associated with the member
     * @since 1.0.0
     */
    fun addNumberProperty(key: String, value: Number?) {
        properties()!!.addProperty(key, value)
    }

    /**
     * Convenience method to add a Boolean member.
     *
     * @param key   name of the member
     * @param value the Boolean value associated with the member
     * @since 1.0.0
     */
    fun addBooleanProperty(key: String, value: Boolean?) {
        properties()!!.addProperty(key, value)
    }

    /**
     * Convenience method to add a Character member.
     *
     * @param key   name of the member
     * @param value the Character value associated with the member
     * @since 1.0.0
     */
    fun addCharacterProperty(key: String, value: Char?) {
        properties()!!.addProperty(key, value)
    }

    /**
     * Convenience method to add a JsonElement member.
     *
     * @param key   name of the member
     * @param value the JsonElement value associated with the member
     * @since 1.0.0
     */
    fun addProperty(key: String, value: JsonElement?) {
        properties()!!.add(key, value)
    }

    /**
     * Convenience method to get a Number member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getNumberProperty(key: String?): Number? {
        val propertyKey = properties()!![key]
        return propertyKey?.asNumber
    }

    /**
     * Convenience method to get a Character member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    @Deprecated(
        """This method was passing the call to JsonElement::getAsCharacter()
      which is in turn deprecated because of misleading nature, as it
      does not get this element as a char but rather as a string's first character."""
    )
    fun getCharacterProperty(key: String?): Char? {
        val propertyKey = properties()!![key]
        return propertyKey?.asCharacter
    }

    /**
     * Convenience method to get a JsonElement member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    fun getProperty(key: String?): JsonElement {
        return properties()!![key]
    }

    /**
     * Removes the property from the object properties.
     *
     * @param key name of the member
     * @return Removed `property` from the key string passed in through the parameter.
     * @since 1.0.0
     */
    fun removeProperty(key: String?): JsonElement {
        return properties()!!.remove(key)
    }

    /**
     * Convenience method to check if a member with the specified name is present in this object.
     *
     * @param key name of the member
     * @return true if there is the member has the specified name, false otherwise.
     * @since 1.0.0
     */
    fun hasProperty(key: String?): Boolean {
        return properties()!!.has(key)
    }

    /**
     * Convenience method to check for a member by name as well as non-null value.
     *
     * @param key name of the member
     * @return true if member is present with non-null value, false otherwise.
     * @since 1.3.0
     */
    fun hasNonNullValueForProperty(key: String?): Boolean {
        return hasProperty(key) && !getProperty(key).isJsonNull
    }

    override fun toJson(): String {
        return copy(properties = props?.toKtxJsonMap())
            .toJson()
    }

    companion object {
        private const val TYPE = "Feature"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Feature object from scratch it is better to use one of the other provided static
         * factory methods such as [.fromGeometry].
         *
         * @param json a formatted valid JSON string defining a GeoJson Feature
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(json: String): Feature = CommonFeature.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by giving the feature a [Geometry].
         *
         * @param geometry a single geometry which makes up this feature object
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometry(geometry: Geometry?): Feature {
            return Feature(TYPE, null, null, geometry, JsonObject())
        }

        /**
         * Create a new instance of this class by giving the feature a [Geometry]. You can also pass
         * in a double array defining a bounding box.
         *
         * @param geometry a single geometry which makes up this feature object
         * @param bbox     optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometry(geometry: Geometry?, bbox: BoundingBox?): Feature {
            return Feature(TYPE, bbox, null, geometry, JsonObject())
        }

        /**
         * Create a new instance of this class by giving the feature a [Geometry] and optionally a
         * set of properties.
         *
         * @param geometry   a single geometry which makes up this feature object
         * @param properties a [JsonObject] containing the feature properties
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometry(
            geometry: Geometry?,
            properties: JsonObject?
        ): Feature {
            return Feature(
                TYPE, null, null, geometry,
                properties ?: JsonObject()
            )
        }

        /**
         * Create a new instance of this class by giving the feature a [Geometry], optionally a
         * set of properties, and optionally pass in a bbox.
         *
         * @param geometry   a single geometry which makes up this feature object
         * @param bbox       optionally include a bbox definition as a double array
         * @param properties a [JsonObject] containing the feature properties
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometry(
            geometry: Geometry?, properties: JsonObject?,
            bbox: BoundingBox?
        ): Feature {
            return Feature(
                TYPE, bbox, null, geometry,
                properties ?: JsonObject()
            )
        }

        /**
         * Create a new instance of this class by giving the feature a [Geometry], optionally a
         * set of properties, and a String which represents the objects id.
         *
         * @param geometry   a single geometry which makes up this feature object
         * @param properties a [JsonObject] containing the feature properties
         * @param id         common identifier of this feature
         * @return [Feature]
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometry(
            geometry: Geometry?, properties: JsonObject?,
            id: String?
        ): Feature {
            return Feature(
                TYPE, null, id, geometry,
                properties ?: JsonObject()
            )
        }

        /**
         * Create a new instance of this class by giving the feature a [Geometry], optionally a
         * set of properties, and a String which represents the objects id.
         *
         * @param geometry   a single geometry which makes up this feature object
         * @param properties a [JsonObject] containing the feature properties
         * @param bbox       optionally include a bbox definition as a double array
         * @param id         common identifier of this feature
         * @return [Feature]
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometry(
            geometry: Geometry?, properties: JsonObject?,
            id: String?, bbox: BoundingBox?
        ): Feature {
            return Feature(
                TYPE, bbox, id, geometry,
                properties ?: JsonObject()
            )
        }
    }
}