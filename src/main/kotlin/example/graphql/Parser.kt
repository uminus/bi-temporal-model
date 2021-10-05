package example.graphql

import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLType
import org.neo4j.ogm.session.Session


abstract class Parser<T> {
    abstract fun parse(source: T): Array<Data>

    /**
     * TODO class responsibility violation
     */
    abstract fun dataFetchers(ses: Session, source: T): GraphQLCodeRegistry

    data class Data(val name: String, val fields: List<Field>)
    data class Field(val name: String, val type: GraphQLType, val nullable: Boolean)
}

