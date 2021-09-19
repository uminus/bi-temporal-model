# WIP: BiTemporal Model for Neo4j
This library for BiTemporal Modeling in neo4j.

The BiTemporal Model has two different time axis.
1. ”Versioning” indicates when the change was commited.
2. "Timeline" indicates when the value will be applied.

With these two axes, we can see past and future events like a time machine.

- [x] versioning
- [ ] timeline
- [ ] support JSONSchema? YANG?
- [ ] Auto-generate GraphQL API
- [ ] support primitive type
- [ ] support array type
- [ ] support key-value type

# Models

```mermaid
graph TD
    subgraph object
        Object-->|fields| Field
        Field-->|changes| Change
        Change-->|value| Value
    end
    
    subgraph version
        Version-->|changes| Change
        Version-->|created| Object
        Version-->|deleted| Object
    end
    
    subgraph time
        Time-->|time| Change
    end
    
    Change-->|version| Version
```