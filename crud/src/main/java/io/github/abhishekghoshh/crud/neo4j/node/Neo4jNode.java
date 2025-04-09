package io.github.abhishekghoshh.crud.neo4j.node;

import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Neo4jNode {

    @Id
    @GeneratedValue
    protected Long id;

    @Property(name = "creationTimeStamp")
    private String creationTimeStamp;

    @Property(name = "lastUpdateTimeStamp")
    private String lastUpdateTimeStamp;

}
