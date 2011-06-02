package org.nds.dbdroid.entity;

import org.nds.dbdroid.annotation.Entity;
import org.nds.dbdroid.annotation.Id;

@Entity
public class Entity4 {

    @Id
    private Integer _id;

    private String name;

    private byte[] document;

    // Default Constructor
    public Entity4() {
    }

    public Entity4(String name, byte[] document) {
        this.name = name;
        this.document = document;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer get_id() {
        return _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public byte[] getDocument() {
        return document;
    }
}
