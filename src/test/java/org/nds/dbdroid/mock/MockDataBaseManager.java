package org.nds.dbdroid.mock;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.annotation.Id;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.query.LogicalOperator;
import org.nds.dbdroid.query.Operator;
import org.nds.dbdroid.query.Query;
import org.nds.dbdroid.query.QueryValueResolver;
import org.nds.dbdroid.reflect.utils.AnnotationUtils;
import org.nds.dbdroid.type.DataType;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

public class MockDataBaseManager extends DataBaseManager {

    private static final Logger log = LoggerFactory.getLogger(MockDataBaseManager.class);

    private static final Map<Class<?>, Map<String, Object>> entities = new HashMap<Class<?>, Map<String, Object>>();

    public MockDataBaseManager(InputStream config) throws DBDroidException {
        super(config);
    }

    @Override
    public void onOpen() {
        log.debug("## open");
    }

    @Override
    public void onClose() {
        log.debug("## close");
    }

    @Override
    protected void onCheckEntity(Class<?> entityClass) throws DBDroidException {
        // Nothing to do
    }

    @Override
    protected void onCreateTable(String tableName, Field[] fields) {
        log.debug("## create table " + tableName + " " + fields);
    }

    @Override
    protected void onUpdateTable(String tableName, Field[] fields) {
        log.debug("## update table " + tableName + " " + fields);
    }

    @Override
    protected void onResetTable(String tableName, Field[] fields) {
        log.debug("## reset table " + tableName + " " + fields);
    }

    @Override
    public void delete(Object entity) {
        log.debug("## delete " + entity);
    }

    @Override
    public <E> List<E> findAll(Class<E> entityClass) {
        log.debug("## find all (" + entityClass + ")");

        List<E> list = null;

        Map<String, Object> objects = entities.get(entityClass);
        if (objects != null) {
            list = new ArrayList<E>();
            for (Object o : objects.values()) {
                list.add((E) o);
            }
        }

        return list;
    }

    @Override
    public <E> E findById(String id, Class<E> entityClazz) {
        log.debug("## find by id " + id);

        E object = null;

        Map<String, Object> objects = entities.get(entityClazz);
        if (objects != null) {
            object = (E) objects.get(id);
        }

        return object;
    }

    @Override
    public <E> E saveOrUpdate(E entity) {
        log.debug("## save or update (" + entity + ")");

        Map<String, Object> objects = entities.get(entity.getClass());
        if (objects == null) {
            objects = new HashMap<String, Object>();
        }
        String id = AnnotationUtils.getPropertyFieldValueAsString(entity, Id.class);
        objects.put(id, entity);

        entities.put(entity.getClass(), objects);

        return entity;
    }

    @Override
    public void rawQuery(String query) {
        log.debug("## run raw query: " + query);
        if (query.toUpperCase().startsWith("INSERT INTO")) {
            int beginIndex = query.toUpperCase().indexOf("INSERT INTO") + "INSERT INTO".length();
            int endIndex = query.toUpperCase().indexOf("VALUES");
            String tableNameWithFields = query.substring(beginIndex, endIndex);
            // Retrieve table name
            String tableName = tableNameWithFields.substring(0, tableNameWithFields.indexOf("("));

            beginIndex = tableNameWithFields.toUpperCase().indexOf("(") + "(".length();
            endIndex = tableNameWithFields.toUpperCase().indexOf(")");
            String concatFields = tableNameWithFields.substring(beginIndex, endIndex);
            // Retrieve fields
            String[] fields = concatFields.split(",");

            String subQuery = query.substring(query.toUpperCase().indexOf("VALUES") + "VALUES".length());
            beginIndex = subQuery.toUpperCase().indexOf("(") + "(".length();
            endIndex = subQuery.toUpperCase().indexOf(")");
            String concatValues = subQuery.substring(beginIndex, endIndex);
            // Retrieve fields values
            String[] values = concatValues.split(",");

            Class<?> entityClass = getEntityFromTableName(tableName.trim().toUpperCase());
            if (entityClass != null) {
                Object entity;
                try {
                    entity = ConstructorUtils.invokeConstructor(entityClass, (Object[]) null);

                    for (int i = 0; i < fields.length; i++) {
                        String fieldName = fields[i].replace("'", "").trim();
                        Object value = values[i].replace("'", "").trim();
                        Field field = FieldUtils.getField(entityClass, fieldName, true);
                        if (field.getType().equals(byte[].class)) {
                            value = value.toString().getBytes();
                        }
                        FieldUtils.writeField(field, entity, ConvertUtils.convert(value, field.getType()), true);
                    }
                    saveOrUpdate(entity);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            } else {
                log.warn("Entity not found from table name: " + tableName);
            }
        }
    }

    @Override
    public <E> List<E> queryList(Query query) {
        return null;
    }

    @Override
    public DataType getDataType() {
        return null;
    }

    @Override
    protected QueryValueResolver getQueryValueResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String onExpressionString(Operator operator, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String onExpressionString(LogicalOperator logicalConjunction, String expression) {
        // TODO Auto-generated method stub
        return null;
    }
}
