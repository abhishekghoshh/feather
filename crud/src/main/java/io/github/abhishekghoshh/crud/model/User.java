package io.github.abhishekghoshh.crud.model;

import io.github.abhishekghoshh.crud.neo4j.node.Neo4jNode;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

@Getter
@Setter
@NodeEntity
public class User extends Neo4jNode {

    private String name;
    private int age;
    private String email;

//
//    @Relationship(type = "WROTE")
//    private Set<Blog> blogs = new HashSet<>();
}
