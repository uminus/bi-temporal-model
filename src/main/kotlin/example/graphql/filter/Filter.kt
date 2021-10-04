package example.graphql.filter

interface Filter<T> {
    fun test(expected: T?, actual: T?): Boolean
}