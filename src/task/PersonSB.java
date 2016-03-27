/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.runtime.Debug.id;
import task.model.Person;

/**
 *
 * @author oksana
 */
public class PersonSB {
    
    private static final String userName = "taskadmin";
    private static final String password = "123456";
    private static final String dbms = "derby";
    private static final String derbyURL = "jdbc:derby://localhost:1527/taskDB";
    private static final String mssqlURL = "jdbc:derby://localhost:1527/taskDB";
    
    private static Connection conn;
    
    public static Connection getConnection() throws SQLException {
        if (conn == null) {
            Properties connectionProps = new Properties();
            connectionProps.put("user", userName);
            connectionProps.put("password", password);

            if (dbms.equals("mssql")) {
                conn = DriverManager.getConnection(
                           mssqlURL,
                           connectionProps);
            } else if (dbms.equals("derby")) {
                conn = DriverManager.getConnection(
                           derbyURL,
                           connectionProps);
            }
            System.out.println("Connected to database");
        }
        return conn;
    }
    
    public static List<Person> getPersons() {
        PreparedStatement stmt = null;
        List<Person> persons = new ArrayList<>();
        try {
            String selectSQL = "select id, first_name, last_name, email from person";
            stmt = getConnection().prepareStatement(selectSQL);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                    Integer userid = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String email = rs.getString("email");
                    Person person = new Person(userid, firstName, lastName, email);
                    persons.add(person);
            }  
        } catch (SQLException e ) {
            System.err.println(e.getMessage());
        } finally {
            if (stmt != null) { 
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PersonSB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return persons;
    }
    
    public static Integer getLastId() {
        PreparedStatement stmt = null;
        Integer maxId = 0;
        try {
            String selectSQL = "select max(id) as maxId from person";
            stmt = getConnection().prepareStatement(selectSQL);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                    maxId = rs.getInt("maxId");
            }  
        } catch (SQLException e ) {
            System.err.println(e.getMessage());
        } finally {
            if (stmt != null) { 
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PersonSB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return maxId;
    }

    public static void savePerson(String firstName, String lastName, String email) {
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement("insert into person (id, first_name, last_name, email) values (?, ?, ?, ?)");
            stmt.setInt(1, getLastId() + 1);
            stmt.setString(2, firstName); 
            stmt.setString(3, lastName); 
            stmt.setString(4, email); 
            stmt.executeUpdate();  
        } catch (SQLException e ) {
            System.err.println(e.getMessage());
        } finally {
            if (stmt != null) { 
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PersonSB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void updatePerson(Integer id, String firstName, String lastName, String email) {
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement("update person set first_name = ?, last_name = ?, email = ? where id = ?");
            stmt.setString(1, firstName); 
            stmt.setString(2, lastName); 
            stmt.setString(3, email); 
            stmt.setInt(4, id);
            stmt.executeUpdate();  
        } catch (SQLException e ) {
            System.err.println(e.getMessage());
        } finally {
            if (stmt != null) { 
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PersonSB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void deletePerson(Integer id) {
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement("delete from person where id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();  
        } catch (SQLException e ) {
            System.err.println(e.getMessage());
        } finally {
            if (stmt != null) { 
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PersonSB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    
}
