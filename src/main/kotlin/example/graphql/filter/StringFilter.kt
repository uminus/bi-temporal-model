package example.graphql.filter

import graphql.Scalars
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType

enum class StringFilter : Filter<String> {
    EQ {
        override fun test(expected: String?, actual: String?): Boolean {
            return actual == expected
        }
    },
    NEQ {
        override fun test(expected: String?, actual: String?): Boolean {
            return !EQ.test(actual, expected)
        }
    },
    REGEX {
        override fun test(expected: String?, actual: String?): Boolean {
            return Regex(expected ?: ".*").matches(actual ?: "")
        }
    }
    ;

    companion object {
        val FILTERS = GraphQLInputObjectType.newInputObject()
            .name(StringFilter::class.simpleName)
            .description("Filter for ${StringFilter::class.qualifiedName}")
            .fields(values().map {
                GraphQLInputObjectField.newInputObjectField()
                    .name(it.name.lowercase())
                    .type(Scalars.GraphQLString)
                    .build()
            }.toList())
            .build()
    }
}
