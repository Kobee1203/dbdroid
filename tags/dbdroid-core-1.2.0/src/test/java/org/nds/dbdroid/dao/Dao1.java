package org.nds.dbdroid.dao;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.entity.Entity1;

public class Dao1 extends AndroidDAO<Entity1, Integer> implements IDao1 {

    public Dao1(DataBaseManager dbManager) {
        super(dbManager);
    }

}
