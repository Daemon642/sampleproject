package oracle.alpha;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;

import javax.naming.InitialContext;

import javax.naming.NamingException;

import javax.sql.DataSource;

public class dbUtilities {
    public dbUtilities() {
        super();
    }
        
    public static final Long getDatabaseId (String seqName) {
        DataSource dataSource = null;
        Context ic = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Long dbId = null;
        
        String selectString;
        selectString = "SELECT " + seqName + ".nextval FROM DUAL";
        
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("jdbc/AlphaOfficeAccessDS");
        } catch (NamingException e) {
        }
        System.out.println("lookup dataSource returned " + dataSource);
        try {
            Connection connection = dataSource.getConnection();
            System.out.println("Got connection: " + connection);

            stmt = connection.prepareStatement(selectString);
            rs = stmt.executeQuery();
            while (rs.next()) {
                dbId = new Long (rs.getInt(1));
            }
            stmt.close();
            connection.close();
        } catch (SQLException e) {
        }
        return dbId;
    }
}
