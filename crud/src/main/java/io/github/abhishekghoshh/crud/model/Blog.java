package io.github.abhishekghoshh.crud.model;

import io.github.abhishekghoshh.crud.neo4j.node.Neo4jNode;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;

@Getter
@Builder
@NodeEntity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Blog extends Neo4jNode {

    private String title;
    private String content;
    private String imgUrl;

    public void update(Blog blog) {
        this.title = blog.title;
        this.content = blog.content;
        this.imgUrl = blog.imgUrl;
    }
}
