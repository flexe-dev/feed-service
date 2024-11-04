package com.flexe.feedservice.Entity.relationships;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreationRelationship<T> {

    private Long id;
    private Date createdAt;
    private T root;

    public CreationRelationship(){
    }

}