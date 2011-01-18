package org.nds.dbdroid.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T, ID extends Serializable> {

    T findById(ID id);

    List<T> findAll();

    T saveOrUpdate(T entity);

    void delete(T entity);
}
