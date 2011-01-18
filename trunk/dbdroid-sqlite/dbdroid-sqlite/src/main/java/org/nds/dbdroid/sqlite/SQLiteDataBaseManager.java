package org.nds.dbdroid.sqlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.helper.EntityHelper;
import org.nds.dbdroid.query.LogicalOperator;
import org.nds.dbdroid.query.Operator;
import org.nds.dbdroid.query.Query;
import org.nds.dbdroid.query.QueryValueResolver;
import org.nds.dbdroid.query.SimpleExpression;
import org.nds.dbdroid.type.DataType;
import org.nds.dbdroid.type.DbDroidType;
import org.nds.dbdroid.type.TypedValue;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;

public class SQLiteDataBaseManager extends DataBaseManager {

    private static final Logger log = LoggerFactory.getLogger(SQLiteDataBaseManager.class);

    private enum SQLiteDataType {
        BIGINT,
        BOOLEAN,
        BLOB,
        CLOB,
        DATE,
        DATETIME,
        DOUBLE,
        FLOAT,
        INTEGER,
        JAVA_OBJECT,
        LONG,
        NUMERIC,
        REAL,
        SHORT,
        TEXT,
        TIME,
        TIMESTAMP
    }

    private static final Map<DbDroidType, String> MAPPED_DATA_TYPES;

    static {
        HashMap<DbDroidType, String> basics = new HashMap<DbDroidType, String>();
        basics.put(DbDroidType.BOOLEAN, SQLiteDataType.BOOLEAN.toString());
        basics.put(DbDroidType.LONG, SQLiteDataType.LONG.toString());
        basics.put(DbDroidType.SHORT, SQLiteDataType.SHORT.toString());
        basics.put(DbDroidType.INTEGER, SQLiteDataType.INTEGER.toString());
        basics.put(DbDroidType.BYTE, "CHARACTER(1)");
        basics.put(DbDroidType.FLOAT, SQLiteDataType.FLOAT.toString());
        basics.put(DbDroidType.DOUBLE, SQLiteDataType.DOUBLE.toString());
        basics.put(DbDroidType.CHARACTER, "CHARACTER(1)");
        basics.put(DbDroidType.STRING, SQLiteDataType.TEXT.toString());
        basics.put(DbDroidType.TIMESTAMP, SQLiteDataType.TIMESTAMP.toString());
        basics.put(DbDroidType.TIME, SQLiteDataType.TIME.toString());
        basics.put(DbDroidType.DATE, SQLiteDataType.DATE.toString());
        basics.put(DbDroidType.BIG_DECIMAL, "DECIMAL(10,5)");
        basics.put(DbDroidType.BIG_INTEGER, SQLiteDataType.BIGINT.toString());
        basics.put(DbDroidType.LOCALE, "VARCHAR(50)");
        basics.put(DbDroidType.CALENDAR, SQLiteDataType.DATE.toString());
        basics.put(DbDroidType.TIMEZONE, SQLiteDataType.DATETIME.toString());
        basics.put(DbDroidType.OBJECT, SQLiteDataType.JAVA_OBJECT.toString());
        basics.put(DbDroidType.CLASS, SQLiteDataType.JAVA_OBJECT.toString());
        basics.put(DbDroidType.BINARY, SQLiteDataType.BLOB.toString());
        basics.put(DbDroidType.WRAPPER_BINARY, SQLiteDataType.BLOB.toString());
        basics.put(DbDroidType.CHAR_ARRAY, "CHARACTER(255)");
        basics.put(DbDroidType.CHARACTER_ARRAY, "VARCHAR(255)");
        basics.put(DbDroidType.BLOB, SQLiteDataType.BLOB.toString());
        basics.put(DbDroidType.CLOB, SQLiteDataType.CLOB.toString());
        basics.put(DbDroidType.SERIALIZABLE, SQLiteDataType.BLOB.toString());

        MAPPED_DATA_TYPES = Collections.unmodifiableMap(basics);
    }

    private final DataType dataType = new DataType(MAPPED_DATA_TYPES);

    private final SQLiteHelper sqliteHelper;

    public SQLiteDataBaseManager(InputStream config, Context context, String name, CursorFactory factory, int version) throws DBDroidException {
        super(config);
        sqliteHelper = new SQLiteHelper(context, name, factory, version);
    }

    class SQLiteHelper extends SQLiteOpenHelper {
        // The Android's default system path of your application database.
        private static final String DB_PATH_TOKEN = "@PACKAGE@";
        private static final String DB_PATH = "/data/data/" + DB_PATH_TOKEN + "/databases/";
        private final String dbPath;

        private final Context context;
        private final String name;

        public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name.substring(name.lastIndexOf("/") + 1), factory, version);
            this.context = context;
            this.dbPath = DB_PATH.replace(DB_PATH_TOKEN, this.context.getPackageName());
            log.debug("[instance] database path: " + this.dbPath);
            this.name = name;
        }

        public SQLiteDatabase getDatabase() {
            return this.getWritableDatabase();
        }

        public void open() {
            getDatabase();
            log.debug("database opened.");
        }

        @Override
        public synchronized void close() {
            if (getDatabase() != null) {
                getDatabase().close();
            }
            super.close();
            log.debug("database closed.");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                // Open your local db as the input stream
                InputStream myInput = this.context.getAssets().open(name);

                // Path to the just created empty db
                File outFileName = new File(dbPath + name);
                if (!outFileName.getParentFile().exists() && !outFileName.getParentFile().mkdirs()) {
                    throw new Error("Error creating folder '" + outFileName.getParentFile().getAbsolutePath() + "' to copy database");
                }

                // Open the empty db as the output stream
                OutputStream myOutput = new FileOutputStream(outFileName);

                // transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                // Close the streams
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
                throw new Error("Error copying database", e);

            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // this.context.deleteDatabase(name);
        }
    }

    @Override
    public void onOpen() {
        sqliteHelper.open();
    }

    @Override
    public void onClose() {
        sqliteHelper.close();
    }

    @Override
    protected void onCheckEntity(Class<?> entityClass) throws DBDroidException {
        Field idField = EntityHelper.getIdField(entityClass);
        if (idField == null) {
            throw new DBDroidException("Field with annotation Id not found in the Entity '" + entityClass.getCanonicalName() + "'");
        }
        if (!ClassUtils.isAssignable(Integer.class, idField.getType(), true)) {
            throw new DBDroidException("Field with annotation Id in the Entity '" + entityClass.getCanonicalName() + "' must be of type "
                    + Integer.TYPE + " and not of type '" + idField.getType().getCanonicalName() + "'");
        }
    }

    @Override
    protected void onCreateTable(String tableName, Field[] fields) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE ").append(tableName).append("(");
        StringBuilder fieldsBuilder = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String columnName = EntityHelper.getColumnName(field);
            boolean isIdField = EntityHelper.isIdField(field);
            String type = dataType.getMappedType(field.getType());
            fieldsBuilder.append(columnName).append(" ").append(type);
            if (isIdField) {
                fieldsBuilder.append(" PRIMARY KEY AUTOINCREMENT");
            }
            if (i < (fields.length - 1)) {
                fieldsBuilder.append(",");
            }
        }
        sqlBuilder.append(fieldsBuilder);
        sqlBuilder.append(")");

        sqliteHelper.getDatabase().execSQL(sqlBuilder.toString());
    }

    @Override
    protected void onUpdateTable(String tableName, Field[] fields) {
        StringBuilder sqlBuilder = new StringBuilder();

        sqliteHelper.getDatabase().execSQL(sqlBuilder.toString());
    }

    @Override
    protected void onResetTable(String tableName, Field[] fields) {
        sqliteHelper.getDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreateTable(tableName, fields);
    }

    @Override
    public void delete(Object entity) {
        String tableName = EntityHelper.getTableName(entity.getClass());
        Field idField = EntityHelper.getIdField(entity.getClass());
        String columnName = EntityHelper.getColumnName(idField);
        String idFieldValue = EntityHelper.readField(idField, entity, String.class);
        sqliteHelper.getDatabase().delete(tableName, columnName + " = ?", new String[] { idFieldValue });
    }

    @Override
    public <E> List<E> findAll(Class<E> entityClazz) {
        Query query = createQuery(entityClazz);
        List<E> entities = queryList(query);

        return entities;
    }

    @Override
    public <E> E findById(String id, Class<E> entityClazz) {
        Field field = EntityHelper.getIdField(entityClazz);
        String idColumnName = EntityHelper.getColumnName(field);

        Query query = createQuery(entityClazz);
        query = query.setDistinct(true);
        query = query.groupBy(idColumnName);
        query = query.having(null);
        query = query.orderBy(null);
        query = query.setFirstRow(0);
        query = query.setMaxRows(-1);
        query = query.add(new SimpleExpression(idColumnName, new TypedValue(id, getDataType().getDbDroidType(field.getType())), Operator.EQUAL));

        List<E> list = queryList(query);

        return list == null ? null : list.get(0);
    }

    @Override
    public <E> E saveOrUpdate(E entity) {
        String tableName = EntityHelper.getTableName(entity.getClass());

        Map<String, Object> map = EntityHelper.getColumnNamesWithValues(entity);

        Parcel parcel = Parcel.obtain();
        parcel.writeMap(map);
        parcel.setDataPosition(0);
        ContentValues contentValues = ContentValues.CREATOR.createFromParcel(parcel);

        long id = sqliteHelper.getDatabase().insertOrThrow(tableName, null, contentValues);

        // Write id value
        EntityHelper.writeField(EntityHelper.getIdField(entity.getClass()), id, entity);

        return entity;
    }

    @Override
    public void rawQuery(String query) {
        sqliteHelper.getDatabase().rawQuery(query, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> queryList(Query query) {
        Class<E> entityClass = (Class<E>) query.getEntityClass();
        List<E> entities = new ArrayList<E>();

        String tableName = EntityHelper.getTableName(entityClass);
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = sqliteHelper.getDatabase().query(query.isDistinct(), tableName, null, whereClause, whereArgs, query.getGroupBy(),
                query.getHaving(), query.getOrderBy(), getLimit(query.getFirstRow(), query.getMaxRows()));
        if (cursor.moveToFirst()) {
            do {
                // Make entity with columns values
                E entity = makeEntity(entityClass, cursor);
                // Add new entity
                entities.add(entity);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return entities;
    }

    private static String getLimit(int firstRow, int maxRows) {
        String limit = null;
        if (maxRows != -1) {
            limit = String.valueOf(maxRows);
            if (firstRow != -1) {
                limit = String.valueOf(firstRow) + ", " + limit;
            }
        }

        return limit;
    }

    private <E> E makeEntity(Class<E> entityClazz, Cursor cursor) {
        E entity = null;
        try {
            entity = ConstructorUtils.invokeConstructor(entityClazz, (Object[]) null);

            for (String columnName : cursor.getColumnNames()) {
                int idx = cursor.getColumnIndex(columnName);
                Field field = EntityHelper.getFieldByColumnName(columnName, entity.getClass());
                if (field == null) {
                    throw new IllegalArgumentException("No fields are found for the column name is " + columnName);
                }

                Object value = getValue(idx, field.getType(), cursor);

                EntityHelper.writeField(field, value, entity);
            }
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
        }

        return entity;
    }

    private Object getValue(int idx, Class<?> fieldType, Cursor cursor) {
        Object value = null;
        if (!cursor.isNull(idx)) {
            Class<?> type = ClassUtils.primitiveToWrapper(fieldType);
            if (Boolean.TYPE.equals(type)) {
                long v = cursor.getLong(idx);
                if (v == 1) {
                    value = true;
                } else {
                    value = false;
                }
            } else if (Double.TYPE.equals(type) || Float.TYPE.equals(type)) {
                value = cursor.getDouble(idx);
            } else if (Integer.TYPE.equals(type)) {
                value = cursor.getInt(idx);
            } else if (Short.TYPE.equals(type)) {
                value = cursor.getShort(idx);
            } else if (Number.class.equals(type) || Long.TYPE.equals(type)) {
                value = cursor.getLong(idx);
            } else if (Character.TYPE.equals(type)) {
                value = (char) cursor.getInt(idx);
            } else if (byte[].class.equals(type)) {
                value = cursor.getBlob(idx);
            } else {
                value = cursor.getString(idx);
            }
        }

        return value;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    protected QueryValueResolver getQueryValueResolver() {
        return new SQLiteQueryValueResolver();
    }

    /*@Override
    protected String onExpressionString(SimpleExpression expression, QueryValueResolver queryValueResolver) {
        return expression.getName() + expression.getOperator().toQueryString(this) + expression.getValue(queryValueResolver);
    }

    @Override
    protected String onExpressionString(LogicalExpression conjunction, QueryValueResolver queryValueResolver) {
        String expr1 = conjunction.getExpression1().toQueryString(this);
        String expr2 = conjunction.getExpression2().toQueryString(this);
        String logicalConjunction = conjunction.getLogicalOperator().toQueryString(this);

        return expr1 + " " + logicalConjunction + " " + expr2;
    }*/

    @Override
    protected String onExpressionString(Operator operator, String value) {
        String op;
        switch (operator) {
            case EQUAL:
                op = " = ";
                break;
            case NOT_EQUAL:
                op = " <> ";
                break;
            case GREATER_THAN:
                op = " > ";
                break;
            case GREATER_THAN_OR_EQUAL:
                op = " >= ";
                break;
            case LESS_THAN:
                op = " < ";
                break;
            case LESS_THAN_OR_EQUAL:
                op = " <= ";
                break;
            case LIKE:
                op = " like ";
                break;
            case IN:
                op = " in ";
                break;
            case NOT_IN:
                op = " not in ";
                break;
            case IS_NULL:
                op = " is null";
                break;
            case IS_NOT_NULL:
                op = " is not null";
                break;
            default:
                op = "undefined: " + operator;
                break;
        }

        return op;
    }

    @Override
    protected String onExpressionString(LogicalOperator logicalOperator, String expression) {
        String expr;
        switch (logicalOperator) {
            case NOT:
                expr = " not (" + expression + ")";
                break;
            case AND:
                expr = " and " + expression;
                break;
            case OR:
                expr = " or " + expression;
                break;
            default:
                expr = "undefined: " + logicalOperator;
                break;
        }
        return expr;
    }

}
