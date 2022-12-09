/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mapbox.geojson.internal.typeadapters

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * Adapts values whose runtime type may differ from their declaration type. This
 * is necessary when a field's type is not the same type that GSON should create
 * when deserializing that field. For example, consider these types:
 * <pre>   `abstract class Shape {
 * int x;
 * int y;
 * }
 * class Circle extends Shape {
 * int radius;
 * }
 * class Rectangle extends Shape {
 * int width;
 * int height;
 * }
 * class Diamond extends Shape {
 * int width;
 * int height;
 * }
 * class Drawing {
 * Shape bottomShape;
 * Shape topShape;
 * }
`</pre> *
 *
 * Without additional type information, the serialized JSON is ambiguous. Is
 * the bottom shape in this drawing a rectangle or a diamond? <pre>   `{
 * "bottomShape": {
 * "width": 10,
 * "height": 5,
 * "x": 0,
 * "y": 0
 * },
 * "topShape": {
 * "radius": 2,
 * "x": 4,
 * "y": 1
 * }
 * }`</pre>
 * This class addresses this problem by adding type information to the
 * serialized JSON and honoring that type information when the JSON is
 * deserialized: <pre>   `{
 * "bottomShape": {
 * "type": "Diamond",
 * "width": 10,
 * "height": 5,
 * "x": 0,
 * "y": 0
 * },
 * "topShape": {
 * "type": "Circle",
 * "radius": 2,
 * "x": 4,
 * "y": 1
 * }
 * }`</pre>
 * Both the type field name (`"type"`) and the type labels (`"Rectangle"`) are configurable.
 *
 * <h3>Registering Types</h3>
 * Create a `RuntimeTypeAdapterFactory` by passing the base type and type field
 * name to the [.of] factory method. If you don't supply an explicit type
 * field name, `"type"` will be used. <pre>   `RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory
 * = RuntimeTypeAdapterFactory.of(Shape.class, "type");
`</pre> *
 * Next register all of your subtypes. Every subtype must be explicitly
 * registered. This protects your application from injection attacks. If you
 * don't supply an explicit type label, the type's simple name will be used.
 * <pre>   `shapeAdapterFactory.registerSubtype(Rectangle.class, "Rectangle");
 * shapeAdapterFactory.registerSubtype(Circle.class, "Circle");
 * shapeAdapterFactory.registerSubtype(Diamond.class, "Diamond");
`</pre> *
 * Finally, register the type adapter factory in your application's GSON builder:
 * <pre>   `Gson gson = new GsonBuilder()
 * .registerTypeAdapterFactory(shapeAdapterFactory)
 * .create();
`</pre> *
 * Like `GsonBuilder`, this API supports chaining: <pre>   `RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory =
 * RuntimeTypeAdapterFactory.of(Shape.class)
 * .registerSubtype(Rectangle.class)
 * .registerSubtype(Circle.class)
 * .registerSubtype(Diamond.class);
`</pre> *
 *
 * @param <T> base type for this factory
 * @since 4.6.0
</T> */
@Keep
class RuntimeTypeAdapterFactory<T> private constructor(
    baseType: Class<*>?,
    typeFieldName: String?,
    maintainType: Boolean
) : TypeAdapterFactory {
    private val baseType: Class<*>
    private val typeFieldName: String
    private val labelToSubtype: MutableMap<String, Class<*>> = LinkedHashMap()
    private val subtypeToLabel: MutableMap<Class<*>, String> = LinkedHashMap()
    private val maintainType: Boolean

    init {
        if (typeFieldName == null || baseType == null) {
            throw NullPointerException()
        }
        this.baseType = baseType
        this.typeFieldName = typeFieldName
        this.maintainType = maintainType
    }
    
    /**
     * Registers `type` identified by `label`. Labels are case
     * sensitive.
     *
     * @param type class of subtype of base type
     * @param label string value for field that distinguishes subtypes
     * @throws IllegalArgumentException if either `type` or `label`
     * have already been registered on this type adapter.
     * @return The same object it is called on, so the calls can be chained
     */
    @JvmOverloads
    fun registerSubtype(
        type: Class<out T>?,
        label: String? = type!!.simpleName
    ): RuntimeTypeAdapterFactory<T> {
        if (type == null || label == null) {
            throw NullPointerException()
        }
        require(!(subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label))) { "types and labels must be unique" }
        labelToSubtype[label] = type
        subtypeToLabel[type] = label
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (type.rawType != baseType) {
            return null
        }
        val labelToDelegate: MutableMap<String, TypeAdapter<*>> = LinkedHashMap()
        val subtypeToDelegate: MutableMap<Class<*>, TypeAdapter<*>> = LinkedHashMap()
        for ((key, value) in labelToSubtype) {
            val delegate = gson.getDelegateAdapter(
                this, TypeToken.get(
                    value
                )
            )
            labelToDelegate[key] = delegate
            subtypeToDelegate[value] = delegate
        }
        return object : TypeAdapter<R>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): R {
                val jsonElement = Streams.parse(`in`)
                val labelJsonElement = if (maintainType) {
                    jsonElement.asJsonObject[typeFieldName]
                } else {
                    jsonElement.asJsonObject.remove(typeFieldName)
                }
                if (labelJsonElement == null) {
                    throw JsonParseException(
                        "cannot deserialize " + baseType
                                + " because it does not define a field named " + typeFieldName
                    )
                }
                val label = labelJsonElement.asString
                val delegate// registration requires that subtype extends T
                        = labelToDelegate[label] as TypeAdapter<R>?
                    ?: throw JsonParseException(
                        "cannot deserialize " + baseType + " subtype named "
                                + label + "; did you forget to register a subtype?"
                    )
                return delegate.fromJsonTree(jsonElement)
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: R) {
                val srcType: Class<*> = value!!::class.java
                val delegate// registration requires that subtype extends T
                        = subtypeToDelegate[srcType] as TypeAdapter<R>?
                    ?: throw JsonParseException(
                        "cannot serialize " + srcType.name
                                + "; did you forget to register a subtype?"
                    )
                val jsonObject = delegate.toJsonTree(value).asJsonObject
                if (maintainType) {
                    Streams.write(jsonObject, out)
                    return
                }
                val clone = JsonObject()
                if (jsonObject.has(typeFieldName)) {
                    throw JsonParseException(
                        "cannot serialize " + srcType.name
                                + " because it already defines a field named " + typeFieldName
                    )
                }
                val label = subtypeToLabel[srcType]
                clone.add(typeFieldName, JsonPrimitive(label))
                for ((key, value1) in jsonObject.entrySet()) {
                    clone.add(key, value1)
                }
                Streams.write(clone, out)
            }
        }.nullSafe()
    }

    companion object {
        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         * `maintainType` flag decide if the type will be stored in pojo or not.
         *
         * @param <T> base type
         * @param baseType class of base type
         * @param typeFieldName field name used to distinguish subtypes
         * @param maintainType true if the type will be stored in pojo, false - otherwise
         * @return The RuntimeTypeAdaptorFactory instance created
        </T> */
        fun <T> of(
            baseType: Class<T>?,
            typeFieldName: String?,
            maintainType: Boolean
        ): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, maintainType)
        }

        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         *
         * @param <T> base type
         * @param baseType class of base type
         * @param typeFieldName field name used to distinguish subtypes
         * @return The RuntimeTypeAdaptorFactory instance created
        </T> */
        fun <T> of(baseType: Class<T>?, typeFieldName: String?): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, false)
        }

        /**
         * Creates a new runtime type adapter for `baseType` using `"type"` as
         * the type field name.
         *
         * @param <T> base type
         * @param baseType class of base type
         * @return The RuntimeTypeAdaptorFactory instance created
        </T> */
        fun <T> of(baseType: Class<T>?): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, "type", false)
        }
    }
}