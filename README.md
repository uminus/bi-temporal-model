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

# Models

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