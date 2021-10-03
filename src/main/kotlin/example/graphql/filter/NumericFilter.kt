package example.graphql.filter

import graphql.Scalars
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType

enum class NumericFilter : Filter<Number> {
    EQ {
        override fun test(expected: Number?, actual: Number?): Boolean {
            return actual == expected
        }
    },
    NEQ {
        override fun test(expected: Number?, actual: Number?): Boolean {
            return !EQ.test(actual, expected)
        }
    },
    GT {
        override fun test(expected: Number?, actual: Number?): Boolean {
            return (expected?.toDouble() ?: 0.0) < (actual?.toDouble() ?: 0.0)
        }
    },
    LT {
        override fun test(expected: Number?, actual: Number?): Boolean {
            return (expected?.toDouble() ?: 0.0) > (actual?.toDouble() ?: 0.0)
        }
    },
    GTE {
        override fun test(expected: Number?, actual: Number?): Boolean {
            return (expected?.toDouble() ?: 0.0) <= (actual?.toDouble() ?: 0.0)
        }
    },
    LTE {
        override fun test(expected: Number?, actual: Number?): Boolean {
            return (expected?.toDouble() ?: 0.0) >= (actual?.toDouble() ?: 0.0)
        }
    },
    ;

    companion object {
        val FLOAT_FILTERS = GraphQLInputObjectType.newInputObject()
            .name("FloatFilter")
            .description("Filter for ${StringFilter::class.qualifiedName}")
            .fields(values().map {
                GraphQLInputObjectField.newInputObjectField()
                    .name(it.name.lowercase())
                    .type(Scalars.GraphQLFloat)
                    .build()
            }.toList())
            .build()

        val INTEGER_FILTERS = GraphQLInputObjectType.newInputObject()
            .name("IntegerFilter")
            .description("Filter for ${StringFilter::class.qualifiedName}")
            .fields(values().map {
                GraphQLInputObjectField.newInputObjectField()
                    .name(it.name.lowercase())
                    .type(Scalars.GraphQLInt)
                    .build()
            }.toList())
            .build()
    }
}