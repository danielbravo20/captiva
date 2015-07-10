package com.carga.mapeo.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carga.mapeo.dao.ConexionBD;
import com.carga.portal.modelo.AtributoTarea;
import com.carga.portal.modelo.Clase;
import com.carga.portal.modelo.Proyecto;

public class ClaseDaoImpl extends ConexionBD{
	
	private static Map<Integer, Clase> clasesMap = new HashMap<Integer, Clase>();
	
	public List<Clase> obtenerPaquetes(Proyecto proyecto, Connection conn){
		List<Clase> clases = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_CLASE, NOMBRE, PAQUETE, COD_PROYECTO, INF_AUTOR, INF_DESCRIPCION FROM BFP_CARTA_FIANZA.CLASE WHERE COD_PROYECTO = "+proyecto.getCodigoProyecto()+"");
			while (rs.next()) {
				if(clases==null){
					clases = new ArrayList<Clase>();
				}
				Clase clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				clases.add(clase);
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
	    		if (rs != null){
					rs.close();
				}
				if(stmt!=null){
					stmt.close();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return clases;
	}
	
	public List<AtributoTarea> obtenerAtributos(Clase clase, Connection conn){
		List<AtributoTarea> atributos = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_CLASE, NOMBRE, TIPO, FLG_LISTA, WEB_NOMBRE, WEB_FORMATO, INF_NOMBRE, INF_DESCRIPCION, INF_AUTOR FROM BFP_CARTA_FIANZA.ATRIBUTO WHERE COD_CLASE = "+clase.getCodigo());
			while (rs.next()) {
				if(atributos==null){
					atributos = new ArrayList<AtributoTarea>();
				}
				AtributoTarea atributo = new AtributoTarea();
				atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
				atributo.setCodigoClase(rs.getInt("COD_CLASE"));
				atributo.setNombre(rs.getString("NOMBRE"));
				atributo.setTipo(rs.getString("TIPO"));
				atributo.setFlgLista(rs.getBoolean("FLG_LISTA"));
				atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
				atributo.setWebFormato(rs.getString("WEB_FORMATO"));
				atributo.setInformacionAutor(rs.getString("INF_AUTOR"));
				atributo.setInformacionNombre(rs.getString("INF_NOMBRE"));
				atributo.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				atributos.add(atributo);
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
	    		if (rs != null){
					rs.close();
				}
				if(stmt!=null){
					stmt.close();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return atributos;
	}
	
	
	public Clase obtenerClase(Integer codigoClase, Connection conn){
		Statement stmt = null;
		ResultSet rs = null;
		Clase clase = null;
		
		if(clasesMap.containsKey(codigoClase)==false){
			try{
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_CLASE, NOMBRE, PAQUETE, COD_PROYECTO, INF_AUTOR, INF_DESCRIPCION FROM BFP_CARTA_FIANZA.CLASE WHERE COD_CLASE = "+codigoClase+"");
				if (rs.next()) {
					clase = new Clase();
					clase.setCodigo(rs.getInt("COD_CLASE"));
					clase.setJavaClase(rs.getString("NOMBRE"));
					clase.setJavaPaquete(rs.getString("PAQUETE"));
					clase.setInformacionAutor(rs.getString("INF_AUTOR"));
					clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
					clasesMap.put(codigoClase, clase);
				 }
				
		    } catch ( Exception e ) {
		      e.printStackTrace();
		      System.exit(0);
		    } finally {
		    	try {
		    		if (rs != null){
						rs.close();
					}
					if(stmt!=null){
						stmt.close();
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		}else{
			clase = clasesMap.get(codigoClase);
		}
		return clase;
	}
	
}
