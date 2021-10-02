# WIP: BiTemporal Model for Neo4j
This library for BiTemporal Modeling in neo4j.

The BiTemporal Model has two different time axis.
1. ”Versioning” indicates when the change was commited.
2. "Timeline" indicates when the value will be applied.

With these two axes, we can see past and future events like a time machine.

- [x] versioning
- [x] timeline
- [wip] support Kotlin data class
- [ ] support JSONSchema? YANG?
- [ ] Auto-generate GraphQL API
- [ ] support primitive type
- [ ] support array type
- [ ] support key-value type
- [ ] constraints based on timeline

## Model diagram

```mermaid
graph TD
    subgraph object
        Entity
        Field
        Change
        Value
    end
    
    Entity-->|fields| Field
    Entity-->|deleted| Change
    Field-->|changes| Change
    Change-->|value| Value
    Change-->|version| Version
    Change-->|time| Timeline
    
    subgraph version
        Version
        Version-->|changes| Change
        Version-->|created| Entity
        Version-->|deleted| Entity
    end
    
    subgraph time
        Timeline
        Timeline-->|time| Change
    end
```
## Modeling(Kotlin data class)
```kotlin
data class Data(
    override var id: UUID?,
    val string_value: String,
    val long_value: Long,
    val double_value: Double,
    val boolean_value: Boolean,
) : Model
```

## Supported Types
- [x] String
- [x] Long
- [x] Double
- [x] Boolean
- [ ] DateTime
- [ ] Array
- [ ] Key-Value
- [ ] Reference