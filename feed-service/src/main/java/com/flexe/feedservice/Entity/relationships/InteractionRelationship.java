package com.flexe.feedservice.Entity.relationships;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class InteractionRelationship<T> {

    private Long id;
    private Date timestamp;
    private T root;

    public InteractionRelationship(){}

}

