package com.carga.mapeo.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carga.mapeo.dao.ConexionBD;
import com.carga.portal.modelo.AtributoConsulta;
import com.carga.portal.modelo.CampoSQL;
import com.carga.portal.modelo.CampoSQLConsulta;
import com.carga.portal.modelo.Consulta;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.Tabla;
import com.carga.portal.modelo.TablaConsulta;

public class ConsultaDaoImpl extends ConexionBD{

	public static Map<Integer, Consulta> consultaMap = new HashMap<Integer, Consulta>();
	public static Map<Integer, AtributoConsulta> atributoConsultaMap = new HashMap<Integer, AtributoConsulta>();
	public static Map<Integer, CampoSQLConsulta> campoSQLConsultaMap = new HashMap<Integer, CampoSQLConsulta>();
	
	public List<Consulta> obtenerConsultas(Proyecto proyecto, Connection conn){
		List<Consulta> consultas = new ArrayList<Consulta>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T1.COD_CONSULTA, T1.SQL_ALE_STO_PROCEDURE, T1.JAV_PAQUETE, T1.JAV_INTERFACE, T1.COD_PROYECTO FROM BFP_CARTA_FIANZA.CONSULTA T1 WHERE T1.COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			int i=1;
			cs.setInt(i++, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();

			Consulta bean = null;
			while (rs.next()) {
				bean = new Consulta();
				bean.setCodigo(rs.getInt("COD_CONSULTA"));
				if(consultaMap.containsKey(bean.getCodigo())==false){
					bean.setAleas(rs.getString("SQL_ALE_STO_PROCEDURE"));
					bean.setPaquete(rs.getString("JAV_PAQUETE"));
					bean.setInterfase(rs.getString("JAV_INTERFACE"));
					bean.setTablaPadre(obtenerTablaPadre(bean,conn));
					bean.setTablasFK(obtenerTablasFK(bean,conn));
					consultaMap.put(bean.getCodigo(), bean);
				}else{
					bean = consultaMap.get(bean.getCodigo());
				}
				consultas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return consultas;
	}
	
	public List<TablaConsulta> obtenerTablasFK(Consulta consulta, Connection conn){
		List<TablaConsulta> tablaConsultas = new ArrayList<TablaConsulta>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T1.COD_TABLA, T1.ESQUEMA, T1.NOMBRE, T1.COD_PROYECTO, T2.FK, T2.FLG_UNO_MUCHOS, T2.COD_TAB_PADRE FROM BFP_CARTA_FIANZA.TABLA T1, BFP_CARTA_FIANZA.CON_TABLA T2 WHERE T1.COD_TABLA = T2.COD_TABLA AND T2.FK = '1' AND T2.COD_CONSULTA = ?";
			cs = conn.prepareCall(sql);
			int i=1;
			cs.setInt(i++, consulta.getCodigo());
			rs = cs.executeQuery();

			TablaConsulta bean = null;
			while (rs.next()) {
				bean = new TablaConsulta();
				bean.setCodigoConsulta(consulta.getCodigo());
				bean.setCodigo(rs.getInt("COD_TABLA"));
				bean.setEsquema(rs.getString("ESQUEMA"));
				bean.setNombre(rs.getString("NOMBRE"));
				bean.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				bean.setFlgFK(rs.getBoolean("FK"));
				bean.setFlgUnoMuchos(rs.getBoolean("FLG_UNO_MUCHOS"));
				
				bean.setCampoSQLConsultas(this.obtenerCamposSQLConsulta(bean, conn));
				bean.setAtributosConsulta(this.obtenerAtributoProcesoConsulta(consulta.getCodigo(), bean.getCodigo(), conn));
				tablaConsultas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return tablaConsultas;
	}
	
	public TablaConsulta obtenerTablaPadre(Consulta consulta, Connection conn){
		TablaConsulta tablaConsulta = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T1.COD_TABLA, T1.ESQUEMA, T1.NOMBRE, T1.COD_PROYECTO FROM BFP_CARTA_FIANZA.TABLA T1, BFP_CARTA_FIANZA.CON_TABLA T2 WHERE T1.COD_TABLA = T2.COD_TABLA AND T2.FK = '0' AND T2.COD_CONSULTA = ?";
			cs = conn.prepareCall(sql);
			int i=1;
			cs.setInt(i++, consulta.getCodigo());
			rs = cs.executeQuery();

			if (rs.next()) {
				tablaConsulta = new TablaConsulta();
				tablaConsulta.setCodigo(rs.getInt("COD_TABLA"));
				tablaConsulta.setCodigoConsulta(consulta.getCodigo());
				tablaConsulta.setEsquema(rs.getString("ESQUEMA"));
				tablaConsulta.setNombre(rs.getString("NOMBRE"));
				tablaConsulta.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				tablaConsulta.setCampoSQLConsultas(this.obtenerCamposSQLConsulta(tablaConsulta, conn));
				tablaConsulta.setAtributosConsulta(this.obtenerAtributoProcesoConsulta(consulta.getCodigo(), tablaConsulta.getCodigo(), conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return tablaConsulta;
	}
	
	public List<CampoSQLConsulta> obtenerCamposSQLConsulta(TablaConsulta tablaConsulta, Connection conn){
		List<CampoSQLConsulta> campoSQLConsultas = new ArrayList<CampoSQLConsulta>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T3.COD_ATRIBUTO, T3.COD_TABLA, T3.COD_ATRIBUTO, T3.CAMPO, T3.TIPO, T3.LONGITUD, " +
					"T3.PRECISION, T3.PK, T3.OBLIGATORIO, T3.FK_CAMPO, T3.FK_TABLA, T3.FK_UNO_MUCHO, T3.FN_BUS_NOMBRE, " +
					"T3.FN_BUS_CATALOGO, T3.VAL_DEFECTO, T1.FLG_CONDICION, T1.FLG_VISIBLE, T1.COD_CONSULTA " +
					"FROM BFP_CARTA_FIANZA.CON_ATRIBUTO T1, BFP_CARTA_FIANZA.CON_TABLA T2, BFP_CARTA_FIANZA.ATRIBUTO_SQL T3 " +
					"WHERE T1.COD_CONSULTA = ? AND T2.COD_TABLA = ? AND T1.COD_CONSULTA = T2.COD_CONSULTA AND T3.COD_ATRIBUTO = T1.COD_ATRIBUTO AND T3.COD_TABLA = T2.COD_TABLA";
			cs = conn.prepareCall(sql);
			int i=1;
			
			cs.setInt(i++, tablaConsulta.getCodigoConsulta());
			cs.setInt(i++, tablaConsulta.getCodigo());
			rs = cs.executeQuery();

			campoSQLConsultas = new ArrayList<CampoSQLConsulta>();
			CampoSQLConsulta bean = null;
			while (rs.next()) {
				bean = new CampoSQLConsulta();
				bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
				bean.setCodigoTabla(rs.getInt("COD_TABLA"));
				bean.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
				bean.setNombre(rs.getString("CAMPO"));
				bean.setTipo(rs.getString("TIPO"));
				bean.setLongitud(rs.getInt("LONGITUD"));
				bean.setPrecision(rs.getInt("PRECISION"));
				bean.setFlgPK("1".equals(rs.getString("PK")));
				bean.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				int codigoFK = rs.getInt("FK_CAMPO");
				if(codigoFK>0){
					bean.setFk(obtenerCampoSQLXCodigoObjetoSQL(codigoFK, conn));
				}
				
				bean.setFkUnoMuchos("1".equals(rs.getString("FK_UNO_MUCHO")));
				bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
				bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
				bean.setValorDefecto(rs.getString("VAL_DEFECTO"));
				bean.setFlgCondicion(rs.getBoolean("FLG_CONDICION"));
				bean.setFlgVisible(rs.getBoolean("FLG_VISIBLE"));
				bean.setTabla(obtenerTabla(rs.getInt("COD_CONSULTA"), bean.getCodigoTabla(), conn));
				campoSQLConsultas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return campoSQLConsultas;
	}
	
	public List<AtributoConsulta> obtenerAtributoProcesoConsulta(int codigoConsulta, int codigoTabla, Connection conn){
		List<AtributoConsulta> atributosProceso = new ArrayList<AtributoConsulta>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT CAT.COD_CONSULTA, CAT.COD_ATRIBUTO, CAT.FLG_CONDICION, CAT.FLG_VISIBLE "+ 
						 " FROM BFP_CARTA_FIANZA.CON_ATRIBUTO CAT, "+
						 " BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS "+
						 " WHERE OAS.COD_ATRIBUTO = CAT.COD_ATRIBUTO "+
						 " AND CAT.COD_CONSULTA = ? "+
						 " AND OAS.COD_TABLA = ? ";
			
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoConsulta);
			cs.setInt(2, codigoTabla);
			rs = cs.executeQuery();

			atributosProceso = new ArrayList<AtributoConsulta>();
			AtributoConsulta bean = null;
			while (rs.next()) {
				bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"), conn);
				bean.setFlgCondicion(rs.getBoolean("FLG_CONDICION"));
				bean.setFlgVisibleWeb(rs.getBoolean("FLG_VISIBLE"));
				bean.setCampoSQL(obtenerCampoSQLConsulta(codigoConsulta, bean.getCodigo(), conn));
				atributosProceso.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributosProceso;
	}
	
	public List<CampoSQLConsulta> obtenerCamposPKTablasFK(Consulta consulta, Connection conn){
		List<CampoSQLConsulta> campoSQLConsultas = new ArrayList<CampoSQLConsulta>();
				
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T2.COD_ATRIBUTO, T2.COD_TABLA, T2.COD_ATRIBUTO, T2.CAMPO, T2.TIPO, T2.LONGITUD, T2.PRECISION, T2.PK, " +
					"T2.OBLIGATORIO, T2.FK_CAMPO, T2.FK_TABLA, T2.FK_UNO_MUCHO, T2.FN_BUS_NOMBRE, T2.FN_BUS_CATALOGO, T2.VAL_DEFECTO " +
					"FROM BFP_CARTA_FIANZA.CON_TABLA T1, BFP_CARTA_FIANZA.ATRIBUTO_SQL T2 " +
					"WHERE T1.COD_TABLA = T2.COD_TABLA AND T1.FK = '1' AND T1.COD_CONSULTA = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, consulta.getCodigo());
			rs = cs.executeQuery();

			campoSQLConsultas = new ArrayList<CampoSQLConsulta>();
			CampoSQLConsulta bean = null;
			while (rs.next()) {
				bean = new CampoSQLConsulta();
				bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
				bean.setCodigoTabla(rs.getInt("COD_TABLA"));
				bean.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
				bean.setNombre(rs.getString("CAMPO"));
				bean.setTipo(rs.getString("TIPO"));
				bean.setLongitud(rs.getInt("LONGITUD"));
				bean.setPrecision(rs.getInt("PRECISION"));
				bean.setFlgPK("1".equals(rs.getString("PK")));
				bean.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				CampoSQL fk = new CampoSQL();
				fk.setNombre(rs.getString("FK_CAMPO"));
				Tabla tabla = new Tabla();
				tabla.setNombre(rs.getString("FK_TABLA"));
				fk.setTabla(tabla);
				bean.setFk(fk);
				bean.setFkUnoMuchos("1".equals(rs.getString("FK_UNO_MUCHO")));
				bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
				bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
				bean.setValorDefecto(rs.getString("VAL_DEFECTO"));
				campoSQLConsultas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return campoSQLConsultas;
	}
	
	public Consulta obtenerConsulta(int codigoConsulta, Connection conn){
		
		Consulta bean = null;
		
		if(consultaMap.containsKey(codigoConsulta)==false){
			
			CallableStatement cs = null;
			ResultSet rs = null;
			
			try {
				String sql = "SELECT T1.COD_CONSULTA, T1.SQL_ALE_STO_PROCEDURE, T1.JAV_PAQUETE, T1.JAV_INTERFACE, T1.COD_PROYECTO FROM BFP_CARTA_FIANZA.CONSULTA T1 WHERE T1.COD_CONSULTA = ?";
				cs = conn.prepareCall(sql);
				int i=1;
				cs.setInt(i++, codigoConsulta);
				rs = cs.executeQuery();
	
				while (rs.next()) {
					bean = new Consulta();
					bean.setCodigo(rs.getInt("COD_CONSULTA"));
					bean.setAleas(rs.getString("SQL_ALE_STO_PROCEDURE"));
					bean.setPaquete(rs.getString("JAV_PAQUETE"));
					bean.setInterfase(rs.getString("JAV_INTERFACE"));
					bean.setTablaPadre(obtenerTablaPadre(bean,conn));
					bean.setTablasFK(obtenerTablasFK(bean,conn));
				}
				consultaMap.put(bean.getCodigo(), bean);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (cs != null){
						cs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}else{
			bean = consultaMap.get(codigoConsulta);
		}
		return bean;
	}
	
	public AtributoConsulta obtenerAtributo(Integer codigoAtributo, Connection conn){
		AtributoConsulta atributo = null;
		if(atributoConsultaMap.containsKey(codigoAtributo)){
			atributo = atributoConsultaMap.get(codigoAtributo);
		}else{
		
			ClaseDaoImpl claseDaoImpl = new ClaseDaoImpl();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_CLASE, NOMBRE, TIPO, FLG_LISTA, WEB_NOMBRE, WEB_FORMATO, INF_NOMBRE, INF_DESCRIPCION, INF_AUTOR FROM BFP_CARTA_FIANZA.ATRIBUTO WHERE COD_ATRIBUTO = "+codigoAtributo);
				if (rs.next()) {
					atributo = new AtributoConsulta();
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
					atributo.setCampoSQL(obtenerCampoSQLXCodigoAtributo(atributo.getCodigo(), conn));
					atributo.setClase(claseDaoImpl.obtenerClase(atributo.getCodigoClase(), conn));
					atributoConsultaMap.put(codigoAtributo, atributo);
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
		}
		return atributo;
	}
	
	public CampoSQLConsulta obtenerCampoSQLXCodigoAtributo(Integer codigoAtributo, Connection conn){
		
		CampoSQLConsulta bean = null;
		
		if(campoSQLConsultaMap.containsKey(codigoAtributo)){
			
			bean = campoSQLConsultaMap.get(codigoAtributo);
			
		}else{
			
			CallableStatement cs = null;
			ResultSet rs = null;
			
			try {
				String sql = "SELECT T3.COD_ATRIBUTO, T3.COD_TABLA, T3.COD_ATRIBUTO, T3.CAMPO, T3.TIPO, T3.LONGITUD, T3.PRECISION, T3.PK, T3.OBLIGATORIO, T3.FK_CAMPO, T3.FK_TABLA, T3.FK_UNO_MUCHO, T3.FN_BUS_NOMBRE, T3.FN_BUS_CATALOGO, T3.VAL_DEFECTO " +
						" FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T3 " +
						" WHERE T3.COD_ATRIBUTO = ? ";
				cs = conn.prepareCall(sql);
				cs.setInt(1, codigoAtributo);
				rs = cs.executeQuery();
				
				while (rs.next()) {
					bean = new CampoSQLConsulta();
					bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
					bean.setCodigoTabla(rs.getInt("COD_TABLA"));
					bean.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
					bean.setNombre(rs.getString("CAMPO"));
					bean.setTipo(rs.getString("TIPO"));
					bean.setLongitud(rs.getInt("LONGITUD"));
					bean.setPrecision(rs.getInt("PRECISION"));
					bean.setFlgPK("1".equals(rs.getString("PK")));
					bean.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
					CampoSQL fk = new CampoSQL();
					fk.setNombre(rs.getString("FK_CAMPO"));
					Tabla tabla = new Tabla();
					tabla.setNombre(rs.getString("FK_TABLA"));
					fk.setTabla(tabla);
					bean.setFk(fk);
					bean.setFkUnoMuchos("1".equals(rs.getString("FK_UNO_MUCHO")));
					bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
					bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
					bean.setValorDefecto(rs.getString("VAL_DEFECTO"));
					campoSQLConsultaMap.put(codigoAtributo, bean);
				}
	
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (cs != null){
						cs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return bean;
	}
	
	public CampoSQLConsulta obtenerCampoSQLXCodigoObjetoSQL(int codigoObjetoSQL, Connection conn){
		CampoSQLConsulta bean = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T3.COD_ATRIBUTO, T3.COD_TABLA, T3.COD_ATRIBUTO, T3.CAMPO, T3.TIPO, T3.LONGITUD, T3.PRECISION, T3.PK, T3.OBLIGATORIO, T3.FK_CAMPO, T3.FK_TABLA, T3.FK_UNO_MUCHO, T3.FN_BUS_NOMBRE, T3.FN_BUS_CATALOGO, T3.VAL_DEFECTO " +
					" FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T3 " +
					" WHERE T3.COD_ATRIBUTO = ? ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoObjetoSQL);
			rs = cs.executeQuery();
			
			while (rs.next()) {
				bean = new CampoSQLConsulta();
				bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
				bean.setCodigoTabla(rs.getInt("COD_TABLA"));
				bean.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
				bean.setNombre(rs.getString("CAMPO"));
				bean.setTipo(rs.getString("TIPO"));
				bean.setLongitud(rs.getInt("LONGITUD"));
				bean.setPrecision(rs.getInt("PRECISION"));
				bean.setFlgPK("1".equals(rs.getString("PK")));
				bean.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				CampoSQL fk = new CampoSQL();
				fk.setNombre(rs.getString("FK_CAMPO"));
				int codigoTabla = rs.getInt("FK_TABLA");
				if(codigoTabla>0){
					fk.setTabla(obtenerTabla(0, codigoTabla, conn));
				}
				bean.setFk(fk);
				bean.setFkUnoMuchos("1".equals(rs.getString("FK_UNO_MUCHO")));
				bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
				bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
				bean.setValorDefecto(rs.getString("VAL_DEFECTO"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return bean;
	}
	
	public CampoSQLConsulta obtenerCampoSQLConsulta(int codigoConsulta, int codigoAtributo, Connection conn){
		
		CampoSQLConsulta bean = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T3.COD_ATRIBUTO, T3.COD_TABLA, T3.COD_ATRIBUTO, T3.CAMPO, T3.TIPO, T3.LONGITUD, " +
					"T3.PRECISION, T3.PK, T3.OBLIGATORIO, T3.FK_CAMPO, T3.FK_TABLA, T3.FK_UNO_MUCHO, T3.FN_BUS_NOMBRE, " +
					"T3.FN_BUS_CATALOGO, T3.VAL_DEFECTO, T1.FLG_CONDICION, T1.FLG_VISIBLE " +
					"FROM BFP_CARTA_FIANZA.CON_ATRIBUTO T1, BFP_CARTA_FIANZA.ATRIBUTO_SQL T3 " +
					"WHERE T1.COD_CONSULTA = ? AND T1.COD_ATRIBUTO = ? AND T3.COD_ATRIBUTO = T1.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			int i=1;
			cs.setInt(i++, codigoConsulta);
			cs.setInt(i++, codigoAtributo);
			rs = cs.executeQuery();
			
			while (rs.next()) {
				bean = new CampoSQLConsulta();
				bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
				bean.setCodigoTabla(rs.getInt("COD_TABLA"));
				bean.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
				bean.setNombre(rs.getString("CAMPO"));
				bean.setTipo(rs.getString("TIPO"));
				bean.setLongitud(rs.getInt("LONGITUD"));
				bean.setPrecision(rs.getInt("PRECISION"));
				bean.setFlgPK("1".equals(rs.getString("PK")));
				bean.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				int codigoAtributoFK = rs.getInt("FK_CAMPO");
				if(codigoAtributoFK>0){
					CampoSQL fk = obtenerCampoSQLXCodigoAtributo(codigoAtributoFK, conn);
					int codigoTablaFK = rs.getInt("FK_TABLA");
					if(codigoTablaFK>0){
						fk.setTabla(obtenerTabla(codigoConsulta, codigoTablaFK, conn));
					}
					bean.setFk(fk);
				}
				bean.setFkUnoMuchos("1".equals(rs.getString("FK_UNO_MUCHO")));
				bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
				bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
				bean.setValorDefecto(rs.getString("VAL_DEFECTO"));
				bean.setFlgCondicion(rs.getBoolean("FLG_CONDICION"));
				bean.setFlgVisible(rs.getBoolean("FLG_VISIBLE"));
				bean.setTabla(obtenerTabla(codigoConsulta, bean.getCodigoTabla(), conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return bean;
	}
	
	public TablaConsulta obtenerTabla(int codigoConsulta, int codigoTabla, Connection conn){
		TablaConsulta tablaConsulta = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T1.COD_TABLA, T1.ESQUEMA, T1.NOMBRE, T1.COD_PROYECTO FROM BFP_CARTA_FIANZA.TABLA T1 WHERE T1.COD_TABLA = ?";
			cs = conn.prepareCall(sql);
			int i=1;
			cs.setInt(i++, codigoTabla);
			rs = cs.executeQuery();

			if (rs.next()) {
				tablaConsulta = new TablaConsulta();
				tablaConsulta.setCodigo(rs.getInt("COD_TABLA"));
				tablaConsulta.setEsquema(rs.getString("ESQUEMA"));
				tablaConsulta.setNombre(rs.getString("NOMBRE"));
				tablaConsulta.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				/*
				if(codigoConsulta>0){
					tablaConsulta.setCampoSQLConsultas(this.obtenerCamposSQLConsulta(tablaConsulta, conn));
					tablaConsulta.setAtributosConsulta(this.obtenerAtributoProcesoConsulta(codigoConsulta, codigoTabla, conn));
				}
				*/
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null){
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return tablaConsulta;
	}
}
