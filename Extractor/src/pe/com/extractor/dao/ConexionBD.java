package pe.com.extractor.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
	
	private static Connection connection = null;
	
	public static Connection getConexion(){
		String jdbcClassName="com.ibm.db2.jcc.DB2Driver";
        //String url="jdbc:db2://SDB2BPMBFDES01:50000/BFP_BE:retrieveMessagesFromServerOnGetMessage=true;";
		//String url="jdbc:db2://LOCALHOST:50000/BFP_BE:retrieveMessagesFromServerOnGetMessage=true;";
		//String url="jdbc:db2://erebazac:50000/BFP_BE:retrieveMessagesFromServerOnGetMessage=true;";
		String url="jdbc:db2://SDB2BPMBFDES01:50000/BFP_NEXO:retrieveMessagesFromServerOnGetMessage=true;";
        //String user="usr_be";
        //String password="usr_be";
		//String user="db2admin";
		//String password="db2admin";
		//String user="usr_be";
		//String password="db2admin";
		String user="usr_nexo";
        String password="usr_nexo";
		
        if(connection==null){
        	System.out.println("Iniciando la conexion a base de datos: "+url);
	        try {
	            Class.forName(jdbcClassName);
	            connection = DriverManager.getConnection(url, user, password);
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
        }
	    return connection;
	}

}
