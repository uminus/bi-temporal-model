package example.graphql.filter

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class NumericFilterTest {
    @Test
    fun op() {
        assertEquals(6, NumericFilter.values().size)

        // equals
        assertTrue(NumericFilter.EQ.test(123, 123))
        assertFalse(NumericFilter.EQ.test(123, 456))

        // not equals
        assertFalse(NumericFilter.NEQ.test(123, 123))
        assertTrue(NumericFilter.NEQ.test(123, 456))

        // greater than
        assertFalse(NumericFilter.GT.test(10, 9))
        assertFalse(NumericFilter.GT.test(10, 10))
        assertTrue(NumericFilter.GT.test(10, 11))

        // greater than or equal
        assertFalse(NumericFilter.GTE.test(10, 9))
        assertTrue(NumericFilter.GTE.test(10, 10))
        assertTrue(NumericFilter.GTE.test(10, 11))

        // less than
        assertTrue(NumericFilter.LT.test(10, 9))
        assertFalse(NumericFilter.LT.test(10, 10))
        assertFalse(NumericFilter.LT.test(10, 11))

        // less than or equal
        assertTrue(NumericFilter.LTE.test(10, 9))
        assertTrue(NumericFilter.LTE.test(10, 10))
        assertFalse(NumericFilter.LTE.test(10, 11))
    }
}