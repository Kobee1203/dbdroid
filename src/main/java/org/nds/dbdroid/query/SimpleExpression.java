package org.nds.dbdroid.query;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.type.TypedValue;

public class SimpleExpression implements Expression {

    private final String name;
    private final TypedValue typedValue;
    private final Operator operator;

    public SimpleExpression(String name, TypedValue typedValue, Operator operator) {
        this.name = name;
        this.typedValue = typedValue;
        this.operator = operator;
    }

    public String toQueryString(DataBaseManager dbManager) {
        return dbManager.toExpressionString(this);
    }

    public String getName() {
        return name;
    }

    public String getValue(QueryValueResolver queryValueResolver) {
        return queryValueResolver.toString(typedValue.getValue());
    }

    public Operator getOperator() {
        return operator;
    }
}
