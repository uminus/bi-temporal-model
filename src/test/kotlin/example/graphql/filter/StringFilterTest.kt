package example.graphql.filter

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StringFilterTest {
    @Test
    fun op() {
        assertEquals(3, StringFilter.values().size)

        // equals
        assertTrue(StringFilter.EQ.test("string", "string"))
        assertFalse(StringFilter.EQ.test("string", "not"))

        // not equals
        assertFalse(StringFilter.NEQ.test("string", "string"))
        assertTrue(StringFilter.NEQ.test("string", "not"))

        // regex
        assertTrue(StringFilter.REGEX.test(".*", "string"))
        assertTrue(StringFilter.REGEX.test("^string$", "string"))
        assertFalse(StringFilter.REGEX.test("^string$", "stringstring"))
    }
}