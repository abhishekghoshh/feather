package io.github.abhishekghoshh.crud.model;

import io.github.abhishekghoshh.crud.neo4j.node.Neo4jNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@Getter
@Builder
@NodeEntity
@AllArgsConstructor
@NoArgsConstructor
public class Blog extends Neo4jNode {

    private String title;
    private String content;

    public Blog(Long id, String creationTimeStamp, String lastUpdateTimeStamp) {
        super(id, creationTimeStamp, lastUpdateTimeStamp);
    }
}
