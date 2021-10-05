package example

import org.neo4j.ogm.cypher.Filter
import org.neo4j.ogm.cypher.Filters
import org.neo4j.ogm.cypher.query.Pagination
import org.neo4j.ogm.cypher.query.SortOrder
import org.neo4j.ogm.model.Result
import org.neo4j.ogm.session.LoadStrategy
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.event.Event
import org.neo4j.ogm.session.event.EventListener
import org.neo4j.ogm.transaction.Transaction
import java.io.Serializable

class DummyNeo4jSession : Session {
    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?,
        sortOrder: SortOrder?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?,
        sortOrder: SortOrder?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?,
        pagination: Pagination?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?,
        sortOrder: SortOrder?,
        pagination: Pagination?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> loadAll(
        type: Class<T>?,
        ids: MutableCollection<ID>?,
        sortOrder: SortOrder?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(objects: MutableCollection<T>?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(objects: MutableCollection<T>?, depth: Int): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(objects: MutableCollection<T>?, sortOrder: SortOrder?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        objects: MutableCollection<T>?,
        sortOrder: SortOrder?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(objects: MutableCollection<T>?, pagination: Pagination?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        objects: MutableCollection<T>?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        objects: MutableCollection<T>?,
        sortOrder: SortOrder?,
        pagination: Pagination?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        objects: MutableCollection<T>?,
        sortOrder: SortOrder?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, depth: Int): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, sortOrder: SortOrder?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, sortOrder: SortOrder?, depth: Int): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, pagination: Pagination?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, pagination: Pagination?, depth: Int): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        sortOrder: SortOrder?,
        pagination: Pagination?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        sortOrder: SortOrder?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filter: Filter?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filter: Filter?, depth: Int): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filter: Filter?, sortOrder: SortOrder?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filter: Filter?,
        sortOrder: SortOrder?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filter: Filter?, pagination: Pagination?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filter: Filter?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filter: Filter?,
        sortOrder: SortOrder?,
        pagination: Pagination?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filter: Filter?,
        sortOrder: SortOrder?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filters: Filters?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filters: Filters?, depth: Int): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filters: Filters?, sortOrder: SortOrder?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filters: Filters?,
        sortOrder: SortOrder?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(type: Class<T>?, filters: Filters?, pagination: Pagination?): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filters: Filters?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filters: Filters?,
        sortOrder: SortOrder?,
        pagination: Pagination?
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> loadAll(
        type: Class<T>?,
        filters: Filters?,
        sortOrder: SortOrder?,
        pagination: Pagination?,
        depth: Int
    ): MutableCollection<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> load(type: Class<T>?, id: ID): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, ID : Serializable?> load(type: Class<T>?, id: ID, depth: Int): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> save(`object`: T) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> save(`object`: T, depth: Int) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> delete(`object`: T) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> delete(type: Class<T>?, filters: MutableIterable<Filter>?, listResults: Boolean): Any {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> deleteAll(type: Class<T>?) {
        TODO("Not yet implemented")
    }

    override fun purgeDatabase() {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun getTransaction(): Transaction {
        TODO("Not yet implemented")
    }

    override fun beginTransaction(): Transaction {
        TODO("Not yet implemented")
    }

    override fun beginTransaction(type: Transaction.Type?): Transaction {
        TODO("Not yet implemented")
    }

    override fun beginTransaction(type: Transaction.Type?, bookmarks: MutableIterable<String>?): Transaction {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> queryForObject(
        objectType: Class<T>?,
        cypher: String?,
        parameters: MutableMap<String, *>?
    ): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> query(
        objectType: Class<T>?,
        cypher: String?,
        parameters: MutableMap<String, *>?
    ): MutableIterable<T> {
        TODO("Not yet implemented")
    }

    override fun query(cypher: String?, parameters: MutableMap<String, *>?): Result {
        TODO("Not yet implemented")
    }

    override fun query(cypher: String?, parameters: MutableMap<String, *>?, readOnly: Boolean): Result {
        TODO("Not yet implemented")
    }

    override fun countEntitiesOfType(entity: Class<*>?): Long {
        TODO("Not yet implemented")
    }

    override fun count(clazz: Class<*>?, filters: MutableIterable<Filter>?): Long {
        TODO("Not yet implemented")
    }

    override fun resolveGraphIdFor(possibleEntity: Any?): Long {
        TODO("Not yet implemented")
    }

    override fun detachNodeEntity(id: Long?): Boolean {
        TODO("Not yet implemented")
    }

    override fun detachRelationshipEntity(id: Long?): Boolean {
        TODO("Not yet implemented")
    }

    override fun register(eventListener: EventListener?): EventListener {
        TODO("Not yet implemented")
    }

    override fun dispose(eventListener: EventListener?): Boolean {
        TODO("Not yet implemented")
    }

    override fun notifyListeners(event: Event?) {
        TODO("Not yet implemented")
    }

    override fun eventsEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLastBookmark(): String {
        TODO("Not yet implemented")
    }

    override fun withBookmark(bookmark: String?) {
        TODO("Not yet implemented")
    }

    override fun getLoadStrategy(): LoadStrategy {
        TODO("Not yet implemented")
    }

    override fun setLoadStrategy(loadStrategy: LoadStrategy?) {
        TODO("Not yet implemented")
    }

}