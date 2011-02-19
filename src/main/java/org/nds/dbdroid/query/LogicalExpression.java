package org.nds.dbdroid.query;

import org.nds.dbdroid.DataBaseManager;

public class LogicalExpression implements Expression {

    private final Expression expression1;
    private final Expression expression2;
    private final LogicalOperator logicalOperator;

    public LogicalExpression(Expression expression1, Expression expression2, LogicalOperator logicalOperator) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.logicalOperator = logicalOperator;
    }

    public String toQueryString(DataBaseManager dbManager) {
        return dbManager.toExpressionString(this);
    }

    public Expression getExpression1() {
        return expression1;
    }

    public Expression getExpression2() {
        return expression2;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }
}
