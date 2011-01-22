package org.nds.dbdroid.query;

import org.nds.dbdroid.DataBaseManager;

public interface Expression {

    String toQueryString(DataBaseManager dbManager);
}
