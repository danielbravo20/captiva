package pe.com.extractor.dao.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pe.com.extractor.bean.Columna;
import pe.com.extractor.bean.Tabla;
import pe.com.extractor.dao.ConexionBD;

public class DBDao extends ConexionBD{

	public List<Tabla> obtenerTablas(String esquema){
		
		List<Tabla> tablas = new ArrayList<Tabla>();
		
		try {
			DatabaseMetaData metaDatos = getConexion().getMetaData();
			ResultSet rs = metaDatos.getTables(null, esquema, "%", null);
			while (rs.next()) {
				Tabla tabla = new Tabla();
				tabla.setEsquema(esquema);
				tabla.setNombre(rs.getString(3));
				tabla.setColumnas(obtenerColumnas(tabla));
				tablas.add(tabla);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tablas;
	}
	
	public List<Columna> obtenerColumnas(Tabla tabla){
		List<Columna> columnas = new ArrayList<Columna>();
		try {
			DatabaseMetaData metaDatos = getConexion().getMetaData();
			ResultSet rs = metaDatos.getColumns(null, tabla.getEsquema(), tabla.getNombre(), null);
			while (rs.next()) {
				Columna columna = new Columna();
				columna.setNombre(rs.getString(4));
				columna.setTipo(rs.getString(6));
				
				columnas.add(columna);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return columnas;
	}
	
	public List<String> obtenerRegistros(Tabla tabla){
		List<String> registros = new ArrayList<String>();
		
		Statement stmt = null;
		try{
			String cabecera = armarCabeceraInsert(tabla);
			stmt = getConexion().createStatement();
			ResultSet rs = stmt.executeQuery(armarBusqueda(tabla));
			while (rs.next()) {
				String registro = cabecera+armarCuerpoInsert(tabla, rs);
				System.out.println(registro);
				registros.add(registro);
			 }
			
			stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    } finally {
	    	if(stmt!=null){
	    		try{
	    			stmt.close();
	    		}catch(Exception e1){
	    			
	    		}
	    	}
	    }
		
		return registros;
	}
	
	public String armarBusqueda(Tabla tabla){
		
		StringBuffer cadena = new StringBuffer();
		cadena.append("SELECT ");
		
		for (int i = 0; i < tabla.getColumnas().size(); i++) {
			Columna columna = tabla.getColumnas().get(i);
			cadena.append(columna.getNombre());
			if(i!=(tabla.getColumnas().size()-1)){
				cadena.append(", ");
			}
		}
		
		cadena.append(" FROM ");
		cadena.append(tabla.getEsquema());
		cadena.append(".");
		cadena.append(tabla.getNombre());
		
		return cadena.toString();
	}
	
	public String armarCuerpoInsert(Tabla tabla, ResultSet rs) throws SQLException{
		
		StringBuffer cadena = new StringBuffer();
		cadena.append("(");
		for (int i = 0; i < tabla.getColumnas().size(); i++) {
			Columna columna = tabla.getColumnas().get(i);
			String valor = rs.getString(columna.getNombre());
			
			if(valor==null || valor.equalsIgnoreCase("NULL")){
				cadena.append("null");
			}else{
				if(columna.getTipo().equalsIgnoreCase("VARCHAR")){
					cadena.append("'"+valor+"'");
				}else if(columna.getTipo().equalsIgnoreCase("TIMESTAMP")){
					cadena.append("'"+valor+"'");
				}else if(columna.getTipo().equalsIgnoreCase("DATE")){
					cadena.append("'"+valor+"'");
				}else{
					cadena.append(valor);
				}
			}
			if(i!=(tabla.getColumnas().size()-1)){
				cadena.append(", ");
			}
		}
		cadena.append(");");
		
		return cadena.toString();
	}

	public String armarCabeceraInsert(Tabla tabla){
		
		StringBuffer cadena = new StringBuffer();
		cadena.append("INSERT INTO ");
		cadena.append(tabla.getEsquema());
		cadena.append(".");
		cadena.append(tabla.getNombre());
		cadena.append("(");
		for (int i = 0; i < tabla.getColumnas().size(); i++) {
			Columna columna = tabla.getColumnas().get(i);
			cadena.append(columna.getNombre());
			if(i!=(tabla.getColumnas().size()-1)){
				cadena.append(", ");
			}
		}
		cadena.append(") VALUES ");
		
		return cadena.toString();
	}
}
