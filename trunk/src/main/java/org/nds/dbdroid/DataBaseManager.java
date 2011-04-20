package org.nds.dbdroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.nds.dbdroid.annotation.Entity;
import org.nds.dbdroid.config.ConfigXMLErrorHandler;
import org.nds.dbdroid.config.ConfigXMLHandler;
import org.nds.dbdroid.dao.IAndroidDAO;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.helper.EntityHelper;
import org.nds.dbdroid.query.LogicalExpression;
import org.nds.dbdroid.query.LogicalOperator;
import org.nds.dbdroid.query.Operator;
import org.nds.dbdroid.query.Query;
import org.nds.dbdroid.query.QueryValueResolver;
import org.nds.dbdroid.query.SimpleExpression;
import org.nds.dbdroid.service.IAndroidService;
import org.nds.dbdroid.type.DataType;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;

public abstract class DataBaseManager {

    private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);

    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private static final String CLASSPATH_PREFIX = "classpath:";

    private static final String CREATE_VALUE = "create";
    private static final String UPDATE_VALUE = "update";
    private static final String RESET_VALUE = "reset";

    private enum PropertyKey {
        GENERATE_DB("dbdroid.generate"),
        SHOW_QUERY("dbdroid.show_query"),
        SCRIPT("dbdroid.script"),
        SCRIPT_ENCODING("dbdroid.script_encoding");

        private String key;

        private PropertyKey(String key) {
            this.key = key;
        }

        public static PropertyKey getValueOf(String key) {
            for (PropertyKey propertyKey : values()) {
                if (key.equals(propertyKey.key)) {
                    return propertyKey;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.key;
        }
    }

    private final InputStream config;

    private boolean xmlConfigValidating;

    private ClassLoader classLoader;

    private Properties properties;
    private Map<Class<? extends IAndroidDAO<?, Serializable>>, IAndroidDAO<?, Serializable>> daos = new HashMap<Class<? extends IAndroidDAO<?, Serializable>>, IAndroidDAO<?, Serializable>>();
    private Map<Class<? extends IAndroidService>, IAndroidService> services = new HashMap<Class<? extends IAndroidService>, IAndroidService>();

    private final List<Class<?>> entities = new ArrayList<Class<?>>();

    private final Map<Class<?>, IAndroidDAO<?, Serializable>> daoFromEntity = new HashMap<Class<?>, IAndroidDAO<?, Serializable>>();

    private final Map<String, Class<?>> entityFromTableName = new HashMap<String, Class<?>>();

    private final Map<Class<?>, String> tableNameFromEntity = new HashMap<Class<?>, String>();

    private final Map<Class<?>, Field[]> fieldsFromEntity = new HashMap<Class<?>, Field[]>();

    public DataBaseManager(InputStream config) {
        this.config = config;
    }

    /**
     * Validate the XML configuration. Default value is false.
     * 
     * @param xmlConfigValidating
     */
    public final void setXmlConfigValidating(boolean xmlConfigValidating) {
        this.xmlConfigValidating = xmlConfigValidating;
    }

    /**
     * Class Loader which can be used to load DAO classes within a package.
     * 
     * @param classLoader
     */
    public final void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public final void open() throws DBDroidException {
        if (config != null) {
            loadConfig(config, xmlConfigValidating);
        } else {
            log.warn("XML dbdroid configuration not found." + (config == null ? "Config inputStream object is NULL." : ""));
        }

        onOpen();
    }

    public final void close() throws DBDroidException {
        onClose();
    }

    private void loadConfig(InputStream config, boolean validate) throws DBDroidException {
        try {
            /** Handling XML */
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(validate);
            SAXParser parser = factory.newSAXParser();
            if (validate) {
                try {
                    parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                    parser.setProperty(JAXP_SCHEMA_SOURCE, new File(getClass().getResource("/xsd/dbdroid.xsd").toURI()));
                } catch (SAXNotRecognizedException x) {
                    // Happens if the parser does not support JAXP 1.2
                    log.debug("parser does not support JAXP 1.2");
                }
            }
            XMLReader reader = parser.getXMLReader();

            /** Create handler to handle XML Tags ( extends DefaultHandler ) */
            ConfigXMLHandler configXMLHandler = getConfigXMLHandler(this, classLoader);
            reader.setErrorHandler(new ConfigXMLErrorHandler());
            reader.setContentHandler(configXMLHandler);
            reader.parse(new InputSource(config));

            daos = configXMLHandler.getDaos();
            services = configXMLHandler.getServices();
            properties = configXMLHandler.getProperties();
        } catch (Exception e) {
            throw new DBDroidException("XML Pasing Exception = " + e, e);
        }

        initializeMaps();

        processProperties();
    }

    protected ConfigXMLHandler getConfigXMLHandler(DataBaseManager dataBaseManager, ClassLoader classLoader) {
        return new ConfigXMLHandler(this, classLoader);
    }

    private void initializeMaps() throws DBDroidException {
        try {
            for (Map.Entry<Class<? extends IAndroidDAO<?, Serializable>>, IAndroidDAO<?, Serializable>> e : daos.entrySet()) {
                IAndroidDAO<?, Serializable> dao = e.getValue();
                Class<?> entityClass = dao.getEntityClass();
                log.debug("entityClass: " + entityClass);
                String tableName = EntityHelper.getTableName(entityClass);
                log.debug("Table name: " + tableName);
                Field[] fields = EntityHelper.getFields(entityClass);
                log.debug("fields: " + Arrays.toString(fields));

                entities.add(entityClass);
                daoFromEntity.put(entityClass, dao);
                entityFromTableName.put(tableName, entityClass);
                tableNameFromEntity.put(entityClass, tableName);
                fieldsFromEntity.put(entityClass, fields);

                onCheckEntity(entityClass);
            }
        } catch (Exception e) {
            throw new DBDroidException(e.getMessage(), e);
        }
    }

    private void processProperties() throws DBDroidException {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            PropertyKey propertyKey = PropertyKey.getValueOf(key);
            if (propertyKey != null) {
                switch (propertyKey) {
                    case GENERATE_DB:
                        log.debug("-- generate DB --");
                        generateDataBase(value);
                        break;
                    case SCRIPT:
                        log.debug("-- script --");
                        String encoding = properties.getProperty(PropertyKey.SCRIPT_ENCODING.toString());
                        runScript(value, encoding);
                        break;
                    case SCRIPT_ENCODING:
                        log.debug("-- script encoding: " + value + " --");
                    case SHOW_QUERY:
                        log.debug("-- show query --");
                        break;
                    default:
                        log.info("Property key: " + key + " (value: " + value + ")");
                }
            } else {
                log.warn("Unknown property key: " + key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends IAndroidDAO<?, ?>> T getDAO(Class<T> daoClass) {
        T dao = (T) daos.get(daoClass);
        if (dao == null) {
            throw new NullPointerException("DAO class '" + daoClass + "' not found. Verify the XML dbdroid configuration.");
        }
        return dao;
    }

    public <T extends IAndroidService> T getService(Class<T> serviceClass) {
        T service = (T) services.get(serviceClass);
        if (service == null) {
            throw new NullPointerException("Service class '" + serviceClass + "' not found. Verify the XML dbdroid configuration.");
        }
        return service;
    }

    protected final List<Class<?>> getEntities() {
        return entities;
    }

    protected final IAndroidDAO<?, Serializable> getDAOFromEntity(Class<?> entity) {
        return daoFromEntity.get(entity);
    }

    protected final String getTableNameFromEntity(Class<?> entity) {
        return tableNameFromEntity.get(entity);
    }

    protected final Class<?> getEntityFromTableName(String tableName) {
        return entityFromTableName.get(tableName);
    }

    protected final Field[] getFieldsFromEntity(Class<?> entity) {
        return fieldsFromEntity.get(entity);
    }

    public final Query createQuery(Class<?> entityClass) {
        return new Query(this, entityClass);
    }

    public final String toExpressionString(SimpleExpression expression) {
        String name = expression.getName();
        String expr = toExpressionString(expression.getOperator(), expression.getValue(getQueryValueResolver()));
        return name + expr;
    }

    public final String toExpressionString(LogicalExpression expression) {
        String expr1 = expression.getExpression1().toQueryString(this);
        String expr2 = toExpressionString(expression.getLogicalOperator(), expression.getExpression2().toQueryString(this));
        return expr1 + expr2;
    }

    public final String toExpressionString(Operator operator, String value) {
        return onExpressionString(operator, value);
    }

    public final String toExpressionString(LogicalOperator logicalOperator, String expression) {
        return onExpressionString(logicalOperator, expression);
    }

    private void generateDataBase(String type) throws DBDroidException {
        try {
            for (Map.Entry<Class<? extends IAndroidDAO<?, Serializable>>, IAndroidDAO<?, Serializable>> e : daos.entrySet()) {
                IAndroidDAO<?, Serializable> dao = e.getValue();
                Class<?> entityClass = dao.getEntityClass();
                log.debug("entityClass: " + entityClass);
                String tableName = tableNameFromEntity.get(entityClass);
                log.debug("Table name: " + tableName);
                Field[] fields = fieldsFromEntity.get(entityClass);
                log.debug("fields: " + fields);

                if (CREATE_VALUE.equalsIgnoreCase(type)) {
                    onCreateTable(tableName, fields);
                } else if (UPDATE_VALUE.equalsIgnoreCase(type)) {
                    onUpdateTable(tableName, fields);
                } else if (RESET_VALUE.equalsIgnoreCase(type)) {
                    onResetTable(tableName, fields);
                }
            }
        } catch (Exception e) {
            throw new DBDroidException(e.getMessage(), e);
        }
    }

    private void runScript(String scriptPath, String encoding) throws DBDroidException {
        try {
            List<String> queries = getQueries(scriptPath, encoding);
            for (String query : queries) {
                rawQuery(query.trim());
            }
        } catch (Exception e) {
            throw new DBDroidException(e.getMessage(), e);
        }
    }

    private List<String> getQueries(String value, String encoding) throws IOException {
        List<String> queries = new ArrayList<String>();

        if (value != null) {
            InputStream is;
            if (value.startsWith(CLASSPATH_PREFIX)) {
                is = getClass().getResourceAsStream(value.substring(value.indexOf(CLASSPATH_PREFIX) + (CLASSPATH_PREFIX.length())));
            } else {
                is = new FileInputStream(new File(value));
            }

            if (is == null) {
                throw new IOException("Script file not found with path: " + value);
            }

            BufferedReader reader = null;
            InputStreamReader isReader = null;
            try {
                isReader = encoding != null ? new InputStreamReader(is, encoding) : new InputStreamReader(is);
                reader = new BufferedReader(isReader);

                String line;
                String query = "";
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines
                    if (line.trim().equals("")) {
                        continue;
                    }

                    // Skip comments
                    if (line.startsWith("--") || line.startsWith("//") || line.startsWith("#")) {
                        continue;
                    }

                    query += " " + line;

                    if (query.endsWith("/")) { // complete command
                        query = query.replace('/', ' '); // Remove the '/' since
                                                         // jdbc complains
                        queries.add(replaceArguments(query, (String[]) null));
                        query = "";
                    } else if (query.contains(";")) { // One or several complete
                                                      // query(ies)
                        String[] q = query.split(";");
                        // Loop on different queries
                        for (int i = 0; i < (q.length - 1); i++) {
                            queries.add(replaceArguments(q[i], (String[]) null));
                        }
                        // Check if the line ends with
                        if (query.endsWith(";")) {
                            queries.add(replaceArguments(q[q.length - 1], (String[]) null));
                            query = "";
                        } else {
                            query = q[q.length - 1];
                        }
                    }
                }
            } finally {
                try {
                    is.close();
                } catch (Exception exc) {
                }
                try {
                    isReader.close();
                } catch (Exception exc) {
                }
                try {
                    reader.close();
                } catch (Exception exc) {
                }
            }
        }
        return queries;
    }

    private static String replaceArguments(String sqlQuery, String... args) {
        String query = sqlQuery;
        if (args != null) {
            for (int a = 0; a < args.length; a++) {
                query = query.replace("&" + (a + 1), args[a]);
            }
        }

        return query;
    }

    /**
     * Opens the connection to the database
     * 
     * @throws DBDroidException
     *             if the connection opening fails.
     */
    public abstract void onOpen() throws DBDroidException;

    /**
     * Closes the connection to the database
     * 
     * @throws DBDroidException
     *             if the connection closing fails.
     */
    public abstract void onClose() throws DBDroidException;

    /**
     * Checks if the {@link Entity} class is valid. If not, this method returns an {@link DBDroidException}
     * 
     * @param entityClass
     *            : {@link Entity} class to check
     * @throws DBDroidException
     *             : throwed if the {@link Entity} is not valid
     */
    protected abstract void onCheckEntity(Class<?> entityClass) throws DBDroidException;

    /**
     * Creates a table
     * 
     * @param tableName
     *            : table name
     * @param fields
     *            : {@link Entity} fields used to create the table
     * @throws DBDroidException
     *             : throwed if the table cannot be created
     */
    protected abstract void onCreateTable(String tableName, Field[] fields) throws DBDroidException;

    /**
     * Updates a table
     * 
     * @param tableName
     *            : table name
     * @param fields
     *            : {@link Entity} fields used to update the table
     * @throws DBDroidException
     *             throwed if the table cannot be updated
     */
    protected abstract void onUpdateTable(String tableName, Field[] fields) throws DBDroidException;

    /**
     * Resets a table
     * 
     * @param tableName
     *            : table name
     * @param fields
     *            : {@link Entity} fields used to reset the table
     * @throws DBDroidException
     */
    protected abstract void onResetTable(String tableName, Field[] fields) throws DBDroidException;

    /**
     * Deletes {@link Entity}
     * 
     * @param entity
     *            : {@link Entity} to delete
     */
    public abstract void delete(Object entity);

    /**
     * Finds all rows in database for {@link Entity} class in argument
     * 
     * @param <E>
     *            : {@link Entity} type
     * @param entityClass
     *            : {@link Entity} class to find
     * @return list of rows converted to {@link Entity} objects E
     */
    public abstract <E> List<E> findAll(Class<E> entityClass);

    /**
     * Finds a row in database for {@link Entity} class in argument and with id in argument
     * 
     * @param <E>
     *            : {@link Entity} type
     * @param id
     *            : id to find in database
     * @param entityClass
     *            : {@link Entity} class to find
     * @return row converted to {@link Entity} object E
     */
    public abstract <E> E findById(Serializable id, Class<E> entityClass);

    /**
     * Saves an {@link Entity} object
     * 
     * @param <E>
     *            : {@link Entity} type
     * @param entity
     *            : {@link Entity} object
     * @return {@link Entity} object saved or updated
     */
    public abstract <E> E saveOrUpdate(E entity);

    /**
     * Runs the query in argument
     * 
     * @param query
     *            : the raw query.
     */
    public abstract void rawQuery(String query);

    /**
     * Runs a query according to the Query object in argument, and return the query result.<br/>
     * Method called in the method queryList() from Query object.
     * 
     * @param <E>
     *            : {@link Entity} type
     * @param query
     *            : Query object
     * @return: List of {@link Entity} objects found with the Query
     */
    public abstract <E> List<E> queryList(Query query);

    /**
     * This method returns a DataType Object, containing the mapping between the java types and the DbDroidTypes and the database types
     * 
     * @return DataType object
     */
    public abstract DataType getDataType();

    /**
     * Returns a {@link QueryValueResolver} used to convert an object value to a {@link String}.<br/>
     * Resolves an object value used in a query. converts this value to a {@link String}.
     * 
     * @return
     */
    protected abstract QueryValueResolver getQueryValueResolver();

    /**
     * Returns a {@link String} representing the {@link SimpleExpression} in argument. This method can use the {@link QueryValueResolver} object in
     * argument to replace a value by a {@link String} recognized by the database engine in a query.<br/>
     * This method is used in the method toExpressionString(SimpleExpression expression), used in the method toQueryString(DataBaseManager dbManager)
     * from the {@link SimpleExpression} class to convert this {@link SimpleExpression} to a {@link String}.
     * 
     * @param expression
     *            : {@link SimpleExpression} object
     * @param queryValueResolver
     *            : {@link QueryValueResolver}
     * @return a {@link String} representing the {@link SimpleExpression} in argument
     */
    //protected abstract String onExpressionString(SimpleExpression expression, QueryValueResolver queryValueResolver);

    /**
     * Returns a {@link String} representing the {@link LogicalExpression} in argument. This method can use the {@link QueryValueResolver} object in
     * argument to replace a value by a {@link String} recognized by the database engine in a query.<br/>
     * This method is used in the method toExpressionString(LogicalExpression expression), used in the method toQueryString(DataBaseManager dbManager)
     * from the {@link LogicalExpression} class to convert this {@link LogicalExpression} to a {@link String}.
     * 
     * @param expression
     *            : {@link LogicalExpression} object
     * @param queryValueResolver
     *            : {@link QueryValueResolver}
     * @return a {@link String} representing the {@link LogicalExpression} in argument
     */
    //protected abstract String onExpressionString(LogicalExpression expression, QueryValueResolver queryValueResolver);

    /**
     * Returns a {@link String} representing the {@link Operator} and the {@link String} value in argument.<br/>
     * This method is used in the method toExpressionString(Operator operator), used in the method toQueryString(DataBaseManager dbManager) from the
     * {@link Operator} class to convert this {@link Operator} to a {@link String}.
     * 
     * @param operator
     *            : {@link Operator} object
     * @param value
     *            : {@link String} value
     * @return a {@link String} representing the {@link Operator} and the {@link String} value in argument
     */
    protected abstract String onExpressionString(Operator operator, String value);

    /**
     * Returns a {@link String} representing the {@link LogicalOperator} and the {@link String} expression in argument.<br/>
     * This method is used in the method toExpressionString(LogicalOperator logicalOperator), used in the method toQueryString(DataBaseManager
     * dbManager) from the {@link LogicalOperator} class to convert this {@link LogicalOperator} to a {@link String}.
     * 
     * @param logicalOperator
     *            : {@link LogicalOperator} object
     * @param expression
     *            : {@link String} expression after the logical operator
     * @return a {@link String} representing the {@link LogicalOperator} and the {@link String} expression in argument
     */
    protected abstract String onExpressionString(LogicalOperator logicalOperator, String expression);

}
