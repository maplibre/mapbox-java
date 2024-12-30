package org.maplibre.turf

import com.google.gson.JsonParser
import org.junit.Assert
import org.junit.Assert.assertEquals
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs

object TestUtils {

    fun compareJson(expectedJson: String, actualJson: String) {
        assertEquals(
            JsonParser.parseString(actualJson),
            JsonParser.parseString(expectedJson)
        )
    }

    fun loadJsonFixture(filename: String): String {
        try {
            val filepath = "src/test/resources/$filename"
            val encoded = Files.readAllBytes(Paths.get(filepath))
            return String(encoded, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            throw IOException("Unable to load test resource $filename")
        }
    }

    fun getResourceFolderFileNames(folder: String?): List<String> {
        val loader = javaClass.classLoader
        val url = loader.getResource(folder)
        val path = url!!.path
        val names: MutableList<String> = ArrayList()
        for (file in File(path).listFiles()) {
            names.add(file.name)
        }
        return names
    }

    fun <T : Serializable?> serialize(obj: T): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(obj)
        oos.close()
        return baos.toByteArray()
    }

    fun <T : Serializable?> deserialize(bytes: ByteArray, cl: Class<T>): T {
        val bais = ByteArrayInputStream(bytes)
        val ois = ObjectInputStream(bais)
        val `object` = ois.readObject()
        return cl.cast(`object`)
    }

    /**
     * Comes from Google Utils Test Case.
     */
    fun expectNearNumber(expected: Double, actual: Double, epsilon: Double) {
        Assert.assertTrue(
            String.format("Expected %f to be near %f", actual, expected),
            abs(expected - actual) <= epsilon
        )
    }

    const val DELTA: Double = 1E-10
    const val ACCESS_TOKEN: String = "pk.XXX"
}
