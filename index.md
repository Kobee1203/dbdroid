<h1>TABLE OF CONTENT</h1>





&lt;hr/&gt;



# Introduction #

**dbdroid** is an object-relational mapping (ORM) library for Android, providing a framework for mapping an object-oriented domain model to a traditional relational database.

**dbdroid** is free software that is distributed under the GNU Lesser General Public License.

**dbdroid** creates a mapping from Java classes to tables of a database, and from Java data types to SQL data types.

**dbdroid** uses an XML configuration file that declares the DAOs (Data Access Object). Each DAO is associated with a Java class with the Entity annotation. It is this Java class that is mapped to a table in the database.

So we have: a DAO => a Entity => a table.

# Database Manager #

To access a database, you must implement the abstract class `DatabaseManager`.
Currently, There are only the library [dbdroid-sqlite](http://code.google.com/p/dbdroid-sqlite/) which has an implementation. The class that implements `DatabaseManager` is `SQLiteDataBaseManager`, an implementation for the SQLite database.

# Entity #

## Entity Annotation ##

To create an Entity, just add the 'Entity' annotation to a Java class

```
@Entity
public class MyEntity {
...
}
```

You can add the 'name' attribute to the 'Entity' annotation to define the table name to create in the database.

```
@Entity(name="my_entity")
public class MyEntity {
...
}
```

## Entity fields ##

### Declaration ###

Each field of a class annotated as Entity corresponds to a column of the table.

So if we have:

```
@Entity(name="my_entity")
public class MyEntity {

    private int myField1;
    private String myField2;

    ...
}
```

Then the table "my\_entity" will be defined as having two columns :
  * "myField1" whose data type is Integer,
  * "myField2" whose data type is String.

### Default Constructor - Getter / Setter ###

Do not forget to add fields accessors (getters / setters), they are used to write and read data.

If you overload the default constructor of the class, do not forget to add the default constructor because it is used to create an instance of the class when retrieving data from the database.

So we have:

```
@Entity(name="my_entity")
public class MyEntity {

    private int myField1;
    private String myField2;
    
    // Default Constructor
    public MyEntity() {
    }
    
    public MyEntity(int myField1, String myField2) {
        this.myField1 = myField1;
        this.myField2 = myField2;
    }

    public int getMyField1() {
        return myField1;
    }
    public void setMyField1(int myField1) {
        this.myField1 = myField1;
    }

    public String getMyField2() {
        return myField2;
    }
    public void setMyField2(String myField2) {
        this.myField2 = myField2;
    }
}
```

### ID Annotation ###

It is possible to declare a field that represents the primary key. To do this, use the Id annotation.

Finally, we have:

```
@Entity(name="my_entity")
public class MyEntity {
    
    @Id
    private Long _id;
    private int myField1;
    private String myField2;
    
    // Default Constructor
    public MyEntity() {
    }
    
    public MyEntity(int myField1, String myField2) {
        this.myField1 = myField1;
        this.myField2 = myField2;
    }

    public Long get_id() {
        return _id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    
    public int getMyField1() {
        return myField1;
    }
    public void setMyField1(int myField1) {
        this.myField1 = myField1;
    }
    
    public String getMyField2() {
        return myField2;
    }
    public void setMyField2(String myField2) {
        this.myField2 = myField2;
    }
}
```

# DAO #

To access to data in database and retrieve an `Entity` object, you must create a DAO object.

To create a DAO object, just create a Java class wich extends `AndroidDAO` class :

```
public class MyDao extends AndroidDAO<MyEntity> {

    public MyDao(DataBaseManager dbManager) {
        super(dbManager);
    }

}
```

The DAO class inherits the following methods:
  * findAll (): find all entities
  * findById (String id): find entity according to the id in argument
  * saveOrUpdate (T entity): save or update an entity
  * delete (T entity): delete an entity

New methods can be added to the DAO class. For this it is recommended to use the [Query](index#Query.md) class.

# XML configuration file #

# Query #

The Query class is used to represent a query for a given Entity.
After adding Expressions to an instance of Query, the implementation of the abstract class `DatabaseManager` can then run this query.

To create an instance of Query, simply call the method `createQuery (Class <?> entityClass)` of the class `DatabaseManager`.

To add new Expressions in Query, simply call the method `add (Expression expression)` of the class Query.

To create new Expressions, just call one of the following static methods:
  * `createExpression(String name, Object val, DbDroidType type, Operator operator)`
  * `createLogicalExpression(Expression expression1, Expression expression2, LogicalOperator logicalOperator)`

## Types ##

```
public enum DbDroidType {
    BOOLEAN,
    LONG,
    SHORT,
    INTEGER,
    BYTE,
    FLOAT,
    DOUBLE,
    CHARACTER,
    STRING,
    TIMESTAMP,
    TIME,
    DATE,
    BIG_DECIMAL,
    BIG_INTEGER,
    LOCALE,
    CALENDAR,
    TIMEZONE,
    OBJECT,
    CLASS,
    BINARY,
    WRAPPER_BINARY,
    CHAR_ARRAY,
    CHARACTER_ARRAY,
    BLOB,
    CLOB,
    SERIALIZABLE;
}
```

## Operators ##

```
public enum Operator {

    EQUAL,
    NOT_EQUAL,
    LIKE,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    IN,
    NOT_IN,
    IS_NULL,
    IS_NOT_NULL

}
```

## Logical Operators ##

```
public enum LogicalOperator {

    NOT,
    AND,
    OR

}
```