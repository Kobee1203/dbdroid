package org.nds.dbdroid.dao;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.entity.Entity2;

public class Dao2 extends AndroidDAO<Entity2, Integer> {

    public Dao2(DataBaseManager dbManager) {
        super(dbManager);
    }

    public class InnerNotDao {
    }
}
