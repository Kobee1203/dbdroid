package org.nds.dbdroid.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.nds.dbdroid.DataBaseManager;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

public class AndroidDAO<T, ID extends Serializable> implements IAndroidDAO<T, ID> {

    private static final Logger log = LoggerFactory.getLogger(AndroidDAO.class);

    protected DataBaseManager dbManager;

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public AndroidDAO(DataBaseManager dbManager) {
        this.dbManager = dbManager;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public final DataBaseManager getDbManager() {
        return dbManager;
    }

    public final Class<T> getEntityClass() {
        return entityClass;
    }

    public void delete(T entity) {
        log.debug("Delete Entity");
        this.dbManager.delete(entity);
    }

    public List<T> findAll() {
        log.debug("Find all Entities");
        return this.dbManager.findAll(entityClass);
    }

    public T findById(Serializable id) {
        log.debug("Find Entity by Id: " + id);
        return this.dbManager.findById(id, entityClass);
    }

    public T saveOrUpdate(T entity) {
        log.debug("Save or Update Entity");
        return this.dbManager.saveOrUpdate(entity);
    }

}
