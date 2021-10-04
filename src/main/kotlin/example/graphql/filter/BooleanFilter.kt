package example.graphql.filter

import graphql.Scalars
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType

enum class BooleanFilter : Filter<Boolean> {
    EQ {
        override fun test(expected: Boolean?, actual: Boolean?): Boolean {
            return expected == actual
        }
    },
    ;

    companion object {
        val FILTERS = GraphQLInputObjectType.newInputObject()
            .name(BooleanFilter::class.simpleName)
            .description("Filter for ${BooleanFilter::class.qualifiedName}")
            .fields(values().map {
                GraphQLInputObjectField.newInputObjectField()
                    .name(it.name.lowercase())
                    .type(Scalars.GraphQLBoolean)
                    .build()
            }.toList())
            .build()
    }
}
