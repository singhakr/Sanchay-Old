/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package sanchay.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 * This class is for storing two kinds of statements:\n
 * 1. For generic queries as defined in SQLDataQueryType\n
 * 2. Any additional custom queries.
 */
public class PreparedStatementStorage implements Serializable {
    
    private final Map<String, PreparedStatement> statements;

    PreparedStatementStorage() {
        statements = new HashMap<>();
    }

    public synchronized boolean statementExists(String statementName) {
        return statements.containsKey(statementName);
    }

    public synchronized PreparedStatement storeStatement(String statementName, PreparedStatement statement) {
        if (statements.containsKey(statementName)) {
            statement = statements.get(statementName);
            return statement;
        }
        
        statements.put(statementName, statement);
        return statement;
    }

    public synchronized PreparedStatement removeStatement(String statementName) {
        PreparedStatement statement = statements.remove(statementName);
        return statement;
    }
}
