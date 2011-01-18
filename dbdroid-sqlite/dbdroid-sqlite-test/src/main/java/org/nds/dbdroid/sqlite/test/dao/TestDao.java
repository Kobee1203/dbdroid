package org.nds.dbdroid.sqlite.test.dao;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.dao.AndroidDAO;
import org.nds.dbdroid.sqlite.test.entity.Test;

public class TestDao extends AndroidDAO<Test> {

	public TestDao(DataBaseManager dbManager) {
		super(dbManager);
	}

}
