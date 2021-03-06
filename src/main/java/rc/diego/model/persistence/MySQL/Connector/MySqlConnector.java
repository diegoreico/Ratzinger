package rc.diego.model.persistence.MySQL.Connector;

import rc.diego.model.persistence.InterfaceConnector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by entakitos on 1/03/16.
 */
public class MySqlConnector implements InterfaceConnector {

    public Connection getConnection()throws Exception{

        /*
         * Está bien para testear con el Main
         */

//        String host="ratztest.cdh6gtvsyebq.us-east-1.rds.amazonaws.com";
//        String database="ratztest";
//        String user="ratztest";
//        String passwd="ratztest";
//        String url = "";

        String host="localhost:3306";
        String database="dawa_04_test";
        String user="root";
        String passwd="";
        String url = "";

        try {
            url = "jdbc:mysql://"+host+"/"+database+"?user="+user+"&password="+passwd;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url);
            System.out.println("Conexion establecida con " + url + "...");
            return con;
        } catch (java.sql.SQLException e) {
            System.out.println("Conexion NO establecida con " + url);
            throw (e);
        }

//        try {
//            Context ctx = new InitialContext();
//            return ((DataSource)ctx.lookup("java:comp/env/jdbc/amazon")).getConnection();
//        } catch (NamingException e) {
//            e.printStackTrace();
//            System.out.println("Conexion NO establecida");
//        } catch (java.sql.SQLException e) {
//            System.out.println("Conexion NO establecida");
//            throw (e);
//        }
//        return null;
    }

    static public void main(String... args){
        try {
            new MySqlConnector().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}