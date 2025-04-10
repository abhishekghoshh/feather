package io.github.abhishekghoshh.crud.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.abhishekghoshh.crud.neo4j.node.Neo4jNode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

@Getter
@Setter
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class User extends Neo4jNode {

    private String name;
    private int age;
    private String email;
    private String imgUrl;

    private List<Blog> blogs;
}
