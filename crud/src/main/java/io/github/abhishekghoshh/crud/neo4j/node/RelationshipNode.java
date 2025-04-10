package io.github.abhishekghoshh.crud.neo4j.node;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.StartNode;


@Getter
@NoArgsConstructor
public abstract class RelationshipNode extends Neo4jNode {

    @StartNode
    private Neo4jNode start;

    @EndNode
    private Neo4jNode end;

    private long uid;


    protected RelationshipNode(Neo4jNode start, Neo4jNode end) {
        this.start = start;
        this.end = end;
        this.uid = end.getId();
    }
}
