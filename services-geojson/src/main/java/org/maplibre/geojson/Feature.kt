package org.maplibre.geojson

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
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

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


//@Keep
//class Feature internal constructor(
//    type: String, , id: String?,
//    geometry: Geometry?, properties: JsonObject?
//) : GeoJson {
//    override val type: String
//
//    @JsonAdapter(BoundingBoxTypeAdapter::class)
//    override val bbox: BoundingBox?
//
//    private val id: String?
//
//    private val geometry: Geometry?
//
//    private val properties: JsonObject?
//
//    init {
//        if (type == null) {
//            throw NullPointerException("Null type")
//        }
//        this.type = type
//        this.bbox = bbox
//        this.id = id
//        this.geometry = geometry
//        this.properties = properties
//    }
//
//    /**
//     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
//     * [Feature].
//     *
//     * @return a String which describes the TYPE of geometry, for this object it will always return
//     * `Feature`
//     * @since 1.0.0
//     */
//    override fun type(): String {
//        return type
//    }
//
//    /**
//     * A Feature Collection might have a member named `bbox` to include information on the
//     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of
//     * size 2*n where n is the number of dimensions represented in the contained feature geometries,
//     * with all axes of the most southwesterly point followed by all axes of the more northeasterly
//     * point. The axes order of a bbox follows the axes order of geometries.
//     *
//     * @return a list of double coordinate values describing a bounding box
//     * @since 3.0.0
//     */
//    override fun bbox(): BoundingBox? {
//        return bbox
//    }
//
//    /**
//     * A feature may have a commonly used identifier which is either a unique String or number.
//     *
//     * @return a String containing this features unique identification or null if one wasn't given
//     * during creation.
//     * @since 1.0.0
//     */
//    fun id(): String? {
//        return id
//    }
//
//    /**
//     * The geometry which makes up this feature. A Geometry object represents points, curves, and
//     * surfaces in coordinate space. One of the seven geometries provided inside this library can be
//     * passed in through one of the static factory methods.
//     *
//     * @return a single defined [Geometry] which makes this feature spatially aware
//     * @since 1.0.0
//     */
//    fun geometry(): Geometry? {
//        return geometry
//    }
//
//    /**
//     * This contains the JSON object which holds the feature properties. The value of the properties
//     * member is a [JsonObject] and might be empty if no properties are provided.
//     *
//     * @return a [JsonObject] which holds this features current properties
//     * @since 1.0.0
//     */
//    fun properties(): JsonObject? {
//        return properties
//    }
//
//    /**
//     * This takes the currently defined values found inside this instance and converts it to a GeoJson
//     * string.
//     *
//     * @return a JSON string which represents this Feature
//     * @since 1.0.0
//     */
//    override fun toJson(): String {
//        val gson: Gson = GsonBuilder()
//            .registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
//            .registerTypeAdapterFactory(GeometryAdapterFactory.create())
//            .create()
//
//
//        // Empty properties -> should not appear in json string
//        var feature = this
//        if (properties().size() == 0) {
//            feature = Feature(TYPE, bbox(), id(), geometry(), null)
//        }
//
//        return gson.toJson(feature)
//    }
//
//    /**
//     * Convenience method to add a String member.
//     *
//     * @param key   name of the member
//     * @param value the String value associated with the member
//     * @since 1.0.0
//     */
//    fun addStringProperty(key: String, value: String?) {
//        properties().addProperty(key, value)
//    }
//
//    /**
//     * Convenience method to add a Number member.
//     *
//     * @param key   name of the member
//     * @param value the Number value associated with the member
//     * @since 1.0.0
//     */
//    fun addNumberProperty(key: String, value: Number?) {
//        properties().addProperty(key, value)
//    }
//
//    /**
//     * Convenience method to add a Boolean member.
//     *
//     * @param key   name of the member
//     * @param value the Boolean value associated with the member
//     * @since 1.0.0
//     */
//    fun addBooleanProperty(key: String, value: Boolean?) {
//        properties().addProperty(key, value)
//    }
//
//    /**
//     * Convenience method to add a Character member.
//     *
//     * @param key   name of the member
//     * @param value the Character value associated with the member
//     * @since 1.0.0
//     */
//    fun addCharacterProperty(key: String, value: Char?) {
//        properties().addProperty(key, value)
//    }
//
//    /**
//     * Convenience method to add a JsonElement member.
//     *
//     * @param key   name of the member
//     * @param value the JsonElement value associated with the member
//     * @since 1.0.0
//     */
//    fun addProperty(key: String, value: JsonElement?) {
//        properties().add(key, value)
//    }
//
//    /**
//     * Convenience method to get a String member.
//     *
//     * @param key name of the member
//     * @return the value of the member, null if it doesn't exist
//     * @since 1.0.0
//     */
//    fun getStringProperty(key: String?): String? {
//        val propertyKey: JsonElement = properties().get(key)
//        return if (propertyKey == null) null else propertyKey.getAsString()
//    }
//
//    /**
//     * Convenience method to get a Number member.
//     *
//     * @param key name of the member
//     * @return the value of the member, null if it doesn't exist
//     * @since 1.0.0
//     */
//    fun getNumberProperty(key: String?): Number? {
//        val propertyKey: JsonElement = properties().get(key)
//        return if (propertyKey == null) null else propertyKey.getAsNumber()
//    }
//
//    /**
//     * Convenience method to get a Boolean member.
//     *
//     * @param key name of the member
//     * @return the value of the member, null if it doesn't exist
//     * @since 1.0.0
//     */
//    fun getBooleanProperty(key: String?): Boolean? {
//        val propertyKey: JsonElement = properties().get(key)
//        return if (propertyKey == null) null else propertyKey.getAsBoolean()
//    }
//
//    /**
//     * Convenience method to get a Character member.
//     *
//     * @param key name of the member
//     * @return the value of the member, null if it doesn't exist
//     * @since 1.0.0
//     */
//    @Deprecated(
//        """This method was passing the call to JsonElement::getAsCharacter()
//      which is in turn deprecated because of misleading nature, as it
//      does not get this element as a char but rather as a string's first character."""
//    )
//    fun getCharacterProperty(key: String?): Char? {
//        val propertyKey: JsonElement = properties().get(key)
//        return if (propertyKey == null) null else propertyKey.getAsCharacter()
//    }
//
//    /**
//     * Convenience method to get a JsonElement member.
//     *
//     * @param key name of the member
//     * @return the value of the member, null if it doesn't exist
//     * @since 1.0.0
//     */
//    fun getProperty(key: String?): JsonElement {
//        return properties().get(key)
//    }
//
//    /**
//     * Removes the property from the object properties.
//     *
//     * @param key name of the member
//     * @return Removed `property` from the key string passed in through the parameter.
//     * @since 1.0.0
//     */
//    fun removeProperty(key: String?): JsonElement {
//        return properties().remove(key)
//    }
//
//    /**
//     * Convenience method to check if a member with the specified name is present in this object.
//     *
//     * @param key name of the member
//     * @return true if there is the member has the specified name, false otherwise.
//     * @since 1.0.0
//     */
//    fun hasProperty(key: String?): Boolean {
//        return properties().has(key)
//    }
//
//    /**
//     * Convenience method to check for a member by name as well as non-null value.
//     *
//     * @param key name of the member
//     * @return true if member is present with non-null value, false otherwise.
//     * @since 1.3.0
//     */
//    fun hasNonNullValueForProperty(key: String?): Boolean {
//        return hasProperty(key) && !getProperty(key).isJsonNull()
//    }
//
//    override fun toString(): String {
//        return ("Feature{"
//                + "type=" + type + ", "
//                + "bbox=" + bbox + ", "
//                + "id=" + id + ", "
//                + "geometry=" + geometry + ", "
//                + "properties=" + properties
//                + "}")
//    }
//
//    override fun equals(obj: Any?): Boolean {
//        if (obj === this) {
//            return true
//        }
//        if (obj is Feature) {
//            val that = obj
//            return (this.type == that.type())
//                    && (if ((this.bbox == null)) (that.bbox() == null) else (this.bbox == that.bbox()))
//                    && (if ((this.id == null)) (that.id() == null) else (this.id == that.id()))
//                    && (if ((this.geometry == null))
//                (that.geometry() == null)
//            else
//                (this.geometry == that.geometry()))
//                    && (if ((this.properties == null))
//                (that.properties == null)
//            else
//                (this.properties == that.properties()))
//        }
//        return false
//    }
//
//    override fun hashCode(): Int {
//        var hashCode = 1
//        hashCode *= 1000003
//        hashCode = hashCode xor type.hashCode()
//        hashCode *= 1000003
//        hashCode = hashCode xor if ((bbox == null)) 0 else bbox.hashCode()
//        hashCode *= 1000003
//        hashCode = hashCode xor if ((id == null)) 0 else id.hashCode()
//        hashCode *= 1000003
//        hashCode = hashCode xor if ((geometry == null)) 0 else geometry.hashCode()
//        hashCode *= 1000003
//        hashCode = hashCode xor if ((properties == null)) 0 else properties.hashCode()
//        return hashCode
//    }
//
//    /**
//     * TypeAdapter to serialize/deserialize Feature objects.
//     *
//     * @since 4.6.0
//     */
//    internal class GsonTypeAdapter(gson: Gson) : TypeAdapter<Feature?>() {
//        @Volatile
//        private var stringTypeAdapter: TypeAdapter<String>? = null
//
//        @Volatile
//        private var boundingBoxTypeAdapter: TypeAdapter<BoundingBox>? = null
//
//        @Volatile
//        private var geometryTypeAdapter: TypeAdapter<Geometry>? = null
//
//        @Volatile
//        private var jsonObjectTypeAdapter: TypeAdapter<JsonObject>? = null
//        private val gson: Gson = gson
//
//        @Throws(IOException::class)
//        override fun write(jsonWriter: JsonWriter, `object`: Feature?) {
//            if (`object` == null) {
//                jsonWriter.nullValue()
//                return
//            }
//            jsonWriter.beginObject()
//            jsonWriter.name("type")
//            var stringTypeAdapter: TypeAdapter<String?> = this.stringTypeAdapter
//            if (stringTypeAdapter == null) {
//                stringTypeAdapter = gson.getAdapter<String>(String::class.java)
//                this.stringTypeAdapter = stringTypeAdapter
//            }
//            stringTypeAdapter.write(jsonWriter, `object`.type())
//            jsonWriter.name("bbox")
//            if (`object`.bbox() == null) {
//                jsonWriter.nullValue()
//            } else {
//                var boundingBoxTypeAdapter: TypeAdapter<BoundingBox?> = this.boundingBoxTypeAdapter
//                if (boundingBoxTypeAdapter == null) {
//                    boundingBoxTypeAdapter = gson.getAdapter<BoundingBox>(BoundingBox::class.java)
//                    this.boundingBoxTypeAdapter = boundingBoxTypeAdapter
//                }
//                boundingBoxTypeAdapter.write(jsonWriter, `object`.bbox())
//            }
//            jsonWriter.name("id")
//            if (`object`.id() == null) {
//                jsonWriter.nullValue()
//            } else {
//                stringTypeAdapter = this.stringTypeAdapter
//                if (stringTypeAdapter == null) {
//                    stringTypeAdapter = gson.getAdapter<String>(String::class.java)
//                    this.stringTypeAdapter = stringTypeAdapter
//                }
//                stringTypeAdapter.write(jsonWriter, `object`.id())
//            }
//            jsonWriter.name("geometry")
//            if (`object`.geometry() == null) {
//                jsonWriter.nullValue()
//            } else {
//                var geometryTypeAdapter: TypeAdapter<Geometry?> = this.geometryTypeAdapter
//                if (geometryTypeAdapter == null) {
//                    geometryTypeAdapter = gson.getAdapter<Geometry>(Geometry::class.java)
//                    this.geometryTypeAdapter = geometryTypeAdapter
//                }
//                geometryTypeAdapter.write(jsonWriter, `object`.geometry())
//            }
//            jsonWriter.name("properties")
//            if (`object`.properties == null) {
//                jsonWriter.nullValue()
//            } else {
//                var jsonObjectTypeAdapter: TypeAdapter<JsonObject?> = this.jsonObjectTypeAdapter
//                if (jsonObjectTypeAdapter == null) {
//                    jsonObjectTypeAdapter = gson.getAdapter<JsonObject>(JsonObject::class.java)
//                    this.jsonObjectTypeAdapter = jsonObjectTypeAdapter
//                }
//                jsonObjectTypeAdapter.write(jsonWriter, `object`.properties())
//            }
//            jsonWriter.endObject()
//        }
//
//        @Throws(IOException::class)
//        override fun read(jsonReader: JsonReader): Feature? {
//            if (jsonReader.peek() == JsonToken.NULL) {
//                jsonReader.nextNull()
//                return null
//            }
//            jsonReader.beginObject()
//            var type: String? = null
//            var bbox: BoundingBox? = null
//            var id: String? = null
//            var geometry: Geometry? = null
//            var properties: JsonObject? = null
//            while (jsonReader.hasNext()) {
//                val name: String = jsonReader.nextName()
//                if (jsonReader.peek() == JsonToken.NULL) {
//                    jsonReader.nextNull()
//                    continue
//                }
//                when (name) {
//                    "type" -> {
//                        var strTypeAdapter: TypeAdapter<String?> = this.stringTypeAdapter
//                        if (strTypeAdapter == null) {
//                            strTypeAdapter = gson.getAdapter<String>(String::class.java)
//                            this.stringTypeAdapter = strTypeAdapter
//                        }
//                        type = strTypeAdapter.read(jsonReader)
//                    }
//
//                    "bbox" -> {
//                        var boundingBoxTypeAdapter: TypeAdapter<BoundingBox?> =
//                            this.boundingBoxTypeAdapter
//                        if (boundingBoxTypeAdapter == null) {
//                            boundingBoxTypeAdapter =
//                                gson.getAdapter<BoundingBox>(BoundingBox::class.java)
//                            this.boundingBoxTypeAdapter = boundingBoxTypeAdapter
//                        }
//                        bbox = boundingBoxTypeAdapter.read(jsonReader)
//                    }
//
//                    "id" -> {
//                        strTypeAdapter = this.stringTypeAdapter
//                        if (strTypeAdapter == null) {
//                            strTypeAdapter = gson.getAdapter<String>(String::class.java)
//                            this.stringTypeAdapter = strTypeAdapter
//                        }
//                        id = strTypeAdapter.read(jsonReader)
//                    }
//
//                    "geometry" -> {
//                        var geometryTypeAdapter: TypeAdapter<Geometry?> = this.geometryTypeAdapter
//                        if (geometryTypeAdapter == null) {
//                            geometryTypeAdapter = gson.getAdapter<Geometry>(Geometry::class.java)
//                            this.geometryTypeAdapter = geometryTypeAdapter
//                        }
//                        geometry = geometryTypeAdapter.read(jsonReader)
//                    }
//
//                    "properties" -> {
//                        var jsonObjectTypeAdapter: TypeAdapter<JsonObject?> =
//                            this.jsonObjectTypeAdapter
//                        if (jsonObjectTypeAdapter == null) {
//                            jsonObjectTypeAdapter =
//                                gson.getAdapter<JsonObject>(JsonObject::class.java)
//                            this.jsonObjectTypeAdapter = jsonObjectTypeAdapter
//                        }
//                        properties = jsonObjectTypeAdapter.read(jsonReader)
//                    }
//
//                    else -> jsonReader.skipValue()
//
//                }
//            }
//            jsonReader.endObject()
//            return Feature(type!!, bbox, id, geometry, properties)
//        }
//    }
//
//    companion object {
//        private const val TYPE = "Feature"
//
//        /**
//         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
//         * creating a Feature object from scratch it is better to use one of the other provided static
//         * factory methods such as [.fromGeometry].
//         *
//         * @param json a formatted valid JSON string defining a GeoJson Feature
//         * @return a new instance of this class defined by the values passed inside this static factory
//         * method
//         * @since 1.0.0
//         */
//        @JvmStatic
//        fun fromJson(json: String): Feature {
//            val gson: GsonBuilder = GsonBuilder()
//            gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
//            gson.registerTypeAdapterFactory(GeometryAdapterFactory.create())
//
//            val feature: Feature = gson.create().fromJson<Feature>(
//                json,
//                Feature::class.java
//            )
//
//            // Even thought properties are Nullable,
//            // Feature object will be created with properties set to an empty object,
//            // so that addProperties() would work
//            if (feature.properties != null) {
//                return feature
//            }
//            return Feature(
//                TYPE, feature.bbox(),
//                feature.id(), feature.geometry(), JsonObject()
//            )
//        }
//
//        /**
//         * Create a new instance of this class by giving the feature a [Geometry].
//         *
//         * @param geometry a single geometry which makes up this feature object
//         * @return a new instance of this class defined by the values passed inside this static factory
//         * method
//         * @since 1.0.0
//         */
//        fun fromGeometry(geometry: Geometry?): Feature {
//            return Feature(TYPE, null, null, geometry, JsonObject())
//        }
//
//        /**
//         * Create a new instance of this class by giving the feature a [Geometry]. You can also pass
//         * in a double array defining a bounding box.
//         *
//         * @param geometry a single geometry which makes up this feature object
//         * @param bbox     optionally include a bbox definition as a double array
//         * @return a new instance of this class defined by the values passed inside this static factory
//         * method
//         * @since 1.0.0
//         */
//        fun fromGeometry(geometry: Geometry?, bbox: BoundingBox?): Feature {
//            return Feature(TYPE, bbox, null, geometry, JsonObject())
//        }
//
//        /**
//         * Create a new instance of this class by giving the feature a [Geometry] and optionally a
//         * set of properties.
//         *
//         * @param geometry   a single geometry which makes up this feature object
//         * @param properties a [JsonObject] containing the feature properties
//         * @return a new instance of this class defined by the values passed inside this static factory
//         * method
//         * @since 1.0.0
//         */
//        @JvmStatic
//        fun fromGeometry(geometry: Geometry?, properties: JsonObject?): Feature {
//            return Feature(
//                TYPE, null, null, geometry,
//                if (properties == null) JsonObject() else properties
//            )
//        }
//
//        /**
//         * Create a new instance of this class by giving the feature a [Geometry], optionally a
//         * set of properties, and optionally pass in a bbox.
//         *
//         * @param geometry   a single geometry which makes up this feature object
//         * @param bbox       optionally include a bbox definition as a double array
//         * @param properties a [JsonObject] containing the feature properties
//         * @return a new instance of this class defined by the values passed inside this static factory
//         * method
//         * @since 1.0.0
//         */
//        @JvmStatic
//        fun fromGeometry(
//            geometry: Geometry?, properties: JsonObject?,
//            bbox: BoundingBox?
//        ): Feature {
//            return Feature(
//                TYPE, bbox, null, geometry,
//                if (properties == null) JsonObject() else properties
//            )
//        }
//
//        /**
//         * Create a new instance of this class by giving the feature a [Geometry], optionally a
//         * set of properties, and a String which represents the objects id.
//         *
//         * @param geometry   a single geometry which makes up this feature object
//         * @param properties a [JsonObject] containing the feature properties
//         * @param id         common identifier of this feature
//         * @return [Feature]
//         * @since 1.0.0
//         */
//        fun fromGeometry(
//            geometry: Geometry?, properties: JsonObject?,
//            id: String?
//        ): Feature {
//            return Feature(
//                TYPE, null, id, geometry,
//                if (properties == null) JsonObject() else properties
//            )
//        }
//
//        /**
//         * Create a new instance of this class by giving the feature a [Geometry], optionally a
//         * set of properties, and a String which represents the objects id.
//         *
//         * @param geometry   a single geometry which makes up this feature object
//         * @param properties a [JsonObject] containing the feature properties
//         * @param bbox       optionally include a bbox definition as a double array
//         * @param id         common identifier of this feature
//         * @return [Feature]
//         * @since 1.0.0
//         */
//        fun fromGeometry(
//            geometry: Geometry?, properties: JsonObject?,
//            id: String?, bbox: BoundingBox?
//        ): Feature {
//            return Feature(
//                TYPE, bbox, id, geometry,
//                if (properties == null) JsonObject() else properties
//            )
//        }
//
//        /**
//         * Gson TYPE adapter for parsing Gson to this class.
//         *
//         * @param gson the built [Gson] object
//         * @return the TYPE adapter for this class
//         * @since 3.0.0
//         */
//        fun typeAdapter(gson: Gson): TypeAdapter<Feature> {
//            return GsonTypeAdapter(gson)
//        }
//    }
//}
