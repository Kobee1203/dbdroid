package org.nds.dbdroid.dao;

import java.io.Serializable;
import java.util.List;

public interface IAndroidDAO<T, ID extends Serializable> {

    Class<?> getEntityClass();

    T findById(ID id);

    List<T> findAll();

    T saveOrUpdate(T entity);

    void delete(T entity);

}
