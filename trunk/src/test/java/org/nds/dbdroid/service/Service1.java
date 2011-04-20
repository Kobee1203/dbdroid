package org.nds.dbdroid.service;

import org.nds.dbdroid.dao.IDao1;

public class Service1 implements IAndroidService {
    private IDao1 dao1;

    public IDao1 getDao1() {
        return dao1;
    }
}
