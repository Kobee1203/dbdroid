package org.nds.dbdroid.dao.subpkg;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.dao.AndroidDAO;
import org.nds.dbdroid.entity.Entity3;

public class Dao3 extends AndroidDAO<Entity3, Integer> {

    public Dao3(DataBaseManager dbManager) {
        super(dbManager);
    }

    public class InnerDao3 extends AndroidDAO<Entity3, Integer> {

        public InnerDao3(DataBaseManager dbManager) {
            super(dbManager);
        }

    }
}
