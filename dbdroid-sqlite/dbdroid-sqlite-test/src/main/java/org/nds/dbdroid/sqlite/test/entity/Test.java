package org.nds.dbdroid.sqlite.test.entity;

import org.nds.dbdroid.annotation.Entity;
import org.nds.dbdroid.annotation.Id;

@Entity
public class Test {

    @Id
    private Integer _id;

    private String name;

    // Default Constructor
    public Test() {
    }

    public Test(String name) {
        this.name = name;
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
}
