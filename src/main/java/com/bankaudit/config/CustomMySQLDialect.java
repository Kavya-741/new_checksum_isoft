package com.bankaudit.config;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomMySQLDialect extends org.hibernate.dialect.MySQLDialect {
    public CustomMySQLDialect() {
        super();
        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
}

