package io.github.abhishekghoshh.crud.model;

import io.github.abhishekghoshh.crud.neo4j.node.RelationshipNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.RelationshipEntity;

@Getter
@RelationshipEntity(type = "WROTE")
@NoArgsConstructor
public class UserWroteBlogsRelation extends RelationshipNode {

    public UserWroteBlogsRelation(User user, Blog blog) {
        super(user, blog);
    }
}
