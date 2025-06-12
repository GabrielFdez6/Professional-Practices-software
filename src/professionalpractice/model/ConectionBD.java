package professionalpractice.model;

import java.sql.*;

public class ConectionBD {
    private Connection conn;
    private String host;
    private String db;
    private String username;
    private String password;

    private static ConectionBD conection;
    public ConectionBD()
    {
        host = "localhost:3306";
    	db = "tintoreria";
    	username = "tinto"; //LO MEJOR ES INCLUIR OTRO USUARIO
    	password = "G1212";
        try
        {
  		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		System.out.println("Conectando a la base...");
		String url ="jdbc:mysql://"+host+"/"+db;
                conn = DriverManager.getConnection(url,username, password);
		System.out.println("Conexion a BD establecida");
  	} catch(SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
	} catch (ClassNotFoundException e) {
                e.printStackTrace();
	} catch(Exception e) {
		System.out.println("Se produjo un error inesperado: "+e.getMessage());
	}
	conection = this;
    }

    public ConectionBD(String host, String db, String username, String password) throws ClassNotFoundException, SQLException
    {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.host = host;
            this.db = db;
            this.username = username;
            this.password = password;
            conn = DriverManager.getConnection ("jdbc:mysql://" + host +"/"+db,username,password);
            conection = this;
    }

    public String getHost() {
 	   return host;
    }

    public void setHost(String host) {
 	   this.host = host;
    }

    public String getDb() {
 	   return db;
    }

    public void setDb(String db) {
 	   this.db = db;
    }

    public String getUsername() {
 	   return username;
    }

    public void setUsername(String username) {
 	   this.username = username;
    }

    public String getPassword() {
 	   return password;
    }

    public void setPassword(String password) {
 	   this.password = password;
    }

    public Connection getConnection() {
 	       return conn;
    }

    public void close() {
 	   try
 	   {
 		   conn.close();
 	   }
 	   catch (SQLException e)
 	   {
 	       System.err.println ("Error: " + e.getMessage () + "\n" + e.getErrorCode ());
 	   }
    }
    
    public static ConectionBD getConection() {
        return conection;
    }
    
    public static void setConection(ConectionBD conection) {
        ConectionBD.conection = conection;
    }
}