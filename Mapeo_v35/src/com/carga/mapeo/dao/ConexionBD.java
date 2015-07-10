package com.carga.mapeo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
	
	public static Connection getConexion(){
		Connection connection = null;
		String jdbcClassName="com.ibm.db2.jcc.DB2Driver";
        String url="jdbc:db2://SDB2BPMBFDES01:50000/BFP_NEXO:retrieveMessagesFromServerOnGetMessage=true;";
        String user="usr_nexo";
        String password="usr_nexo";
        
        if(connection==null){
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
