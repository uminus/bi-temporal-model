package example.graphql.filter

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BooleanFilterTest {
    @Test
    fun op() {
        assertEquals(1, BooleanFilter.values().size)

        // equals
        assertTrue(BooleanFilter.EQ.test(false, false))
        assertFalse(BooleanFilter.EQ.test(false, true))
    }
}