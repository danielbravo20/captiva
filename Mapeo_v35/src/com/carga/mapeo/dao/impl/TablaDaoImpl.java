package com.carga.mapeo.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.carga.mapeo.dao.ConexionBD;
import com.carga.portal.modelo.CampoSQL;
import com.carga.portal.modelo.CampoSQLTarea;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.Tabla;

public class TablaDaoImpl extends ConexionBD{
	
	public List<Tabla> obtenerTablas(Proyecto proyecto, Connection conn){
		List<Tabla> tablas = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_TABLA, ESQUEMA, NOMBRE, COD_PROYECTO FROM BFP_CARTA_FIANZA.TABLA WHERE COD_PROYECTO = "+proyecto.getCodigoProyecto()+" ORDER BY ORDEN");
			while (rs.next()) {
				if(tablas==null){
					tablas = new ArrayList<Tabla>();
				}
				Tabla tabla = new Tabla();
				tabla.setCodigo(rs.getInt("COD_TABLA"));
				tabla.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				tabla.setEsquema(rs.getString("ESQUEMA"));
				tabla.setNombre(rs.getString("NOMBRE"));
				tabla.setCamposPK(obtenerCamposPK(tabla.getCodigo(), conn));
				tabla.setCamposSQL(obtenerCampos(tabla, conn));
				tablas.add(tabla);
			 }
			
			stmt.close();
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return tablas;
	}
	
	public Tabla obtenerTabla(int codigoTabla, Connection conn){
		Tabla tabla = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_TABLA, ESQUEMA, NOMBRE, COD_PROYECTO FROM BFP_CARTA_FIANZA.TABLA WHERE COD_TABLA = "+codigoTabla+"");
			while (rs.next()) {
				tabla = new Tabla();
				tabla.setCodigo(rs.getInt("COD_TABLA"));
				tabla.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				tabla.setEsquema(rs.getString("ESQUEMA"));
				tabla.setNombre(rs.getString("NOMBRE"));
			 }
			
			stmt.close();
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return tabla;
	}
	
	public List<CampoSQL> obtenerCampos(Tabla tabla, Connection conn){
		List<CampoSQL> campos = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_ATRIBUTO, COD_TABLA, CAMPO, TIPO, LONGITUD, PRECISION, PK, OBLIGATORIO, FK_CAMPO, FK_UNO_MUCHO, FN_BUS_NOMBRE, FN_BUS_CATALOGO, VAL_DEFECTO FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL WHERE COD_TABLA = "+tabla.getCodigo()+" ORDER BY COD_ATRIBUTO");
			while (rs.next()) {
				if(campos==null){
					campos = new ArrayList<CampoSQL>();
				}
				campos.add(parseCampo(rs, conn));
			 }
			
			stmt.close();
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return campos;
	}
		
	public CampoSQL obtenerCampo(int codigoCampoSQL, Connection conn){
		CampoSQL campoSQL = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_ATRIBUTO, COD_TABLA, CAMPO, TIPO, LONGITUD, PRECISION, PK, OBLIGATORIO, FK_CAMPO, FK_UNO_MUCHO, FN_BUS_NOMBRE, FN_BUS_CATALOGO, VAL_DEFECTO FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL WHERE COD_ATRIBUTO = "+codigoCampoSQL+"");
			while (rs.next()) {
				campoSQL = parseCampo(rs, conn);
				if(campoSQL!=null && campoSQL.getCodigoTabla()>0){
					campoSQL.setTabla(obtenerTabla(campoSQL.getCodigoTabla(), conn));
				}
			 }
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return campoSQL;
	}
	
	public List<CampoSQL> obtenerCamposPK(int codigoTabla, Connection conn){
		List<CampoSQL> campos = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_ATRIBUTO, COD_TABLA, CAMPO, TIPO, LONGITUD, PRECISION, PK, OBLIGATORIO, FK_CAMPO, FK_UNO_MUCHO, FN_BUS_NOMBRE, FN_BUS_CATALOGO, VAL_DEFECTO FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL WHERE COD_TABLA = "+codigoTabla+" AND PK = '1'");
			while (rs.next()) {
				if(campos==null){
					campos = new ArrayList<CampoSQL>();
				}
				campos.add(parseCampo(rs, conn));
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return campos;
	}
	
	public List<CampoSQL> obtenerCamposFK(Integer codigoTabla, Connection conn){
		List<CampoSQL> campos = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_ATRIBUTO, COD_TABLA, CAMPO, TIPO, LONGITUD, PRECISION, PK, OBLIGATORIO, FK_CAMPO, FK_UNO_MUCHO, FN_BUS_NOMBRE, FN_BUS_CATALOGO, VAL_DEFECTO FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL WHERE COD_TABLA = "+codigoTabla+" AND not FK_CAMPO is null");
			while (rs.next()) {
				if(campos==null){
					campos = new ArrayList<CampoSQL>();
				}
				campos.add(parseCampo(rs, conn));
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCamposFK(CampoSQLTarea campoSQLTarea, Connection conn){
		List<CampoSQLTarea> campos = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_ATRIBUTO, COD_TABLA, CAMPO, TIPO, LONGITUD, PRECISION, PK, OBLIGATORIO, FK_CAMPO, FK_UNO_MUCHO, FN_BUS_NOMBRE, FN_BUS_CATALOGO, VAL_DEFECTO FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL WHERE COD_TABLA = "+campoSQLTarea.getCodigoTabla()+" AND not FK_CAMPO is null");
			while (rs.next()) {
				if(campos==null){
					campos = new ArrayList<CampoSQLTarea>();
				}
				campos.add(parseCampoTarea(rs, conn));
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return campos;
	}
	
	private CampoSQL parseCampo(ResultSet rs, Connection conn) throws SQLException{
		CampoSQL campoSQL = new CampoSQL();
		campoSQL.setCodigo(rs.getInt("COD_ATRIBUTO"));
		campoSQL.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
		campoSQL.setCodigoTabla(rs.getInt("COD_TABLA"));
		campoSQL.setNombre(rs.getString("CAMPO"));
		campoSQL.setTipo(rs.getString("TIPO"));
		campoSQL.setLongitud(rs.getInt("LONGITUD"));
		campoSQL.setPrecision(rs.getInt("PRECISION"));
		campoSQL.setFlgPK(rs.getBoolean("PK"));
		campoSQL.setFlgObligatorio(rs.getBoolean("OBLIGATORIO"));
		campoSQL.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
		campoSQL.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
		int codigoFK = rs.getInt("FK_CAMPO");
		if(codigoFK>0){
			campoSQL.setFk(obtenerCampo(codigoFK, conn));
		}
		campoSQL.setTabla(obtenerTabla(campoSQL.getCodigoTabla(), conn));
		return campoSQL;
	}
	
	private CampoSQLTarea parseCampoTarea(ResultSet rs, Connection conn) throws SQLException{
		CampoSQLTarea campoSQL = new CampoSQLTarea();
		campoSQL.setCodigo(rs.getInt("COD_ATRIBUTO"));
		campoSQL.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
		campoSQL.setCodigoTabla(rs.getInt("COD_TABLA"));
		campoSQL.setNombre(rs.getString("CAMPO"));
		campoSQL.setTipo(rs.getString("TIPO"));
		campoSQL.setLongitud(rs.getInt("LONGITUD"));
		campoSQL.setPrecision(rs.getInt("PRECISION"));
		campoSQL.setFlgPK(rs.getBoolean("PK"));
		campoSQL.setFlgObligatorio(rs.getBoolean("OBLIGATORIO"));
		int codigoFK = rs.getInt("FK_CAMPO");
		if(codigoFK>0){
			campoSQL.setFk(obtenerCampo(codigoFK, conn));
		}
		return campoSQL;
	}
	
	public Tabla obtenerTabla(Proyecto proyecto, Connection conn){
		Tabla tabla = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_TABLA, ESQUEMA, NOMBRE, COD_PROYECTO FROM BFP_CARTA_FIANZA.TABLA WHERE COD_PROYECTO = "+proyecto.getCodigoProyecto()+"");
			while (rs.next()) {
				tabla = new Tabla();
				tabla.setCodigo(rs.getInt("COD_TABLA"));
				tabla.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				tabla.setEsquema(rs.getString("ESQUEMA"));
				tabla.setNombre(rs.getString("NOMBRE"));
			 }
			
	    } catch ( Exception e ) {
	      e.printStackTrace();
	      System.exit(0);
	    } finally {
	    	try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return tabla;
	}

}
