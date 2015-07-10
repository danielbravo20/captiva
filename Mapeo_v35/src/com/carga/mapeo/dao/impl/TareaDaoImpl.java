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
import com.carga.portal.modelo.AtributoTarea;
import com.carga.portal.modelo.CampoSQL;
import com.carga.portal.modelo.CampoSQLConsulta;
import com.carga.portal.modelo.CampoSQLTarea;
import com.carga.portal.modelo.Clase;
import com.carga.portal.modelo.ConsultaTareaCompletar;
import com.carga.portal.modelo.ObjetoBPM;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.Tabla;
import com.carga.portal.modelo.TablaConsulta;
import com.carga.portal.modelo.Tarea;

public class TareaDaoImpl extends ConexionBD{

	public static final String EXT_ALEAS_COMPLETAR = "_CON_COMPLETAR";
	public static final String EXT_PAQUETE_DAO = ".dao";
	public static final String EXT_INTERFASE_DAO = "Dao";
	
	private static Map<Integer, AtributoTarea> atributosTareaMap = new HashMap<Integer, AtributoTarea>();
	private static Map<Integer, AtributoTarea> atributosTareaCancelarMap = new HashMap<Integer, AtributoTarea>();
	private static Map<Integer, AtributoTarea> atributosTareaRechazarMap = new HashMap<Integer, AtributoTarea>();
	private static Map<Integer, AtributoTarea> atributosTareaObservarMap = new HashMap<Integer, AtributoTarea>();
	
	private static Map<Integer, AtributoTarea> atributosTareaSinCampoSQLMap = new HashMap<Integer, AtributoTarea>();
	private static Map<Integer, CampoSQLTarea> CampoSQLTareaMap = new HashMap<Integer, CampoSQLTarea>();
	private static Map<Integer, Tabla> tablaMap = new HashMap<Integer, Tabla>(); 
	
	public List<Tarea> obtenerTareas(Proyecto proyecto, Connection conn){
		List<Tarea> tareas = new ArrayList<Tarea>();
		ProcesoDaoImpl procesoDaoImpl = new ProcesoDaoImpl();
		CallableStatement cs = null;
		ResultSet rs = null;
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		try {
			String sql = "SELECT COD_TAREA, COD_PROCESO, COD_PROYECTO, NOMBRE, VERSION, COD_CON_TRABAJAR, COD_CON_COMPLETAR, SQL_ALEAS, " +
					"SQL_DATASOURCE, BPM_NOMBRE, JAV_PAQUETE, JAV_CLASE, WEB_ACC_COMPLETAR, WEB_ACC_GRABAR, WEB_ACC_CANCELAR, " +
					"WEB_ACC_RECHAZAR, " +
					"WEB_ACC_OBSERVAR, WEB_ACC_SALIR, WEB_ACC_SUBSANAR, WEB_PAR_HIS_COMENTARIO, WEB_PAR_HIS_ACCION, WEB_TIE_ROJO, " +
					"WEB_TIE_AMARILLO, WEB_FLG_ARC_ADJUNTOS, WEB_FLG_ARC_ADICIONALES FROM BFP_CARTA_FIANZA.TAREA WHERE COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();
			
			Tarea tarea = null;
			while (rs.next()) {
				tarea = new Tarea();
				tarea.setCodigo(rs.getInt("COD_TAREA"));
				tarea.setProyecto(proyecto);
				tarea.setNombre(rs.getString("NOMBRE"));
				tarea.setVersion(rs.getString("VERSION"));
				tarea.setAleas(rs.getString("SQL_ALEAS"));
				tarea.setDataSource(rs.getString("SQL_DATASOURCE"));
				tarea.setPaquete(rs.getString("JAV_PAQUETE"));
				tarea.setClase(rs.getString("JAV_CLASE"));
				tarea.setPlantilla(rs.getString("BPM_NOMBRE"));
				tarea.setWebParametroComentario(rs.getString("WEB_PAR_HIS_COMENTARIO"));
				tarea.setConsultaTrabajar(consultaDaoImpl.obtenerConsulta(rs.getInt("COD_CON_TRABAJAR"), conn));
				tarea.setConsultaCompletar(obtenerConsultaTareaCompletar(tarea, conn));
				
				tarea.setCamposCompletar(obtenerCampoSQLTareaCompletar(tarea, conn));
				tarea.setCamposTrabajar(obtenerCampoSQLTareaTrabajar(tarea, conn));
				tarea.setCamposCancelar(obtenerCampoSQLTareaCancelar(tarea, conn));
				tarea.setCamposRechazar(obtenerCampoSQLTareaRechazar(tarea, conn));
				tarea.setCamposObservar(obtenerCampoSQLTareaObservar(tarea, conn));
				
				tarea.setAtributosTrabajar(obtenerAtributosTrabajar(tarea, conn));
				tarea.setAtributosCompletar(obtenerAtributosCompletar(tarea, conn));
				tarea.setAtributosCancelar(obtenerAtributosCancelar(tarea, conn));
				tarea.setAtributosRechazar(obtenerAtributosRechazar(tarea, conn));
				tarea.setAtributosObservar(obtenerAtributosObservar(tarea, conn));
				
				tarea.setAtributosTrabajarSinPK(obtenerAtributosTrabajarSinPK(tarea, conn));
				tarea.setAtributosCompletarSinPK(obtenerAtributosCompletarSinPK(tarea, conn));
				tarea.setAtributosCancelarSinPK(obtenerAtributosCancelarSinPK(tarea, conn));
				tarea.setAtributosRechazarSinPK(obtenerAtributosRechazarSinPK(tarea, conn));
				tarea.setAtributosObservarSinPK(obtenerAtributosObservarSinPK(tarea, conn));
				
				tarea.setClasePadre(obtenerClasePadre(proyecto, conn));
				
				tarea.setClasesCompletar(obtenerClasesCompletar(tarea, conn));
				tarea.setClasesObservar(obtenerClasesObservar(tarea, conn));
				tarea.setClasesCancelar(obtenerClasesCancelar(tarea, conn));
				tarea.setClasesRechazar(obtenerClasesRechazar(tarea, conn));
				
				tarea.setProceso(procesoDaoImpl.obtenerProcesoResumen(rs.getInt("COD_PROCESO"), conn));
				
				tarea.setObjetosBPMCompletar(obtenerObjetosBPMCompletar(tarea.getCodigo(), conn));
				tarea.setObjetosBPMTrabajar(obtenerObjetosBPMTrabajar(tarea.getCodigo(), conn));
				
				tarea.setAtributosCompletar(obtenerAtributosCompletar(tarea, conn));
				tarea.setAtributosCompletarValidacionWeb(obtenerAtributosCompletarValidacionWeb(tarea, conn));
				
				
				tareas.add(tarea);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return tareas;
	}
	
	public List<AtributoTarea> obtenerAtributosCompletar(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		atributos = obtenerAtributosCompletarPK(tarea.getCodigo(), atributos, conn);
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, " +
						"  		 TAC.JAV_VAL_OMISION, TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION, TAC.WEB_TAB_CAMPO " +
						"  FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						" WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setRequiereValidacion(rs.getBoolean("WEB_FLG_VALIDACION"));
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				bean.setWebTabCampo(rs.getInt("WEB_TAB_CAMPO"));
				boolean esPK = false;
				if(bean.getCampoSQLTarea()!=null &&
						bean.getCampoSQLTarea().isFlgPK()==true){
					esPK = true;
				}
				
				if(esPK==false){
					atributos.add(bean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosCompletarPK(int codigoTarea, List<AtributoTarea> atributos, Connection conn){
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = 	"SELECT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA ,OAS.CAMPO, OAS.TIPO,OAS.LONGITUD ,OAS.PRECISION ,OAS.PK ,OAS.OBLIGATORIO, OAS.FK_CAMPO ,OAS.FK_TABLA ,OAS.FK_UNO_MUCHO, OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO  " +
							" FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
							" WHERE OAS.PK = '1' " +
							" AND OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS WHERE TAC.COD_ATRIBUTO = OAS.COD_ATRIBUTO AND TAC.COD_TAREA = ? ORDER BY OAS.COD_TABLA)   " +
							" ORDER BY OAS.COD_ATRIBUTO ";
			
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoTarea);
			
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				int codigoAtributo = rs.getInt("COD_ATRIBUTO");
				bean = obtenerAtributoCompletar(codigoTarea, codigoAtributo, conn);
				if(bean==null){
					bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				}
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public AtributoTarea obtenerAtributoCompletar(int codigoTarea, int codigoAtributo, Connection conn){
		AtributoTarea bean = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, " +
					" TAC.JAV_VAL_OMISION, TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
					"FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, BFP_CARTA_FIANZA.ATRIBUTO ATR WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO AND TAC.COD_TAREA = ? AND TAC.COD_ATRIBUTO = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoTarea);
			cs.setInt(2, codigoAtributo);
			rs = cs.executeQuery();
			
			if (rs.next()) {
				bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setRequiereValidacion(rs.getBoolean("WEB_FLG_VALIDACION"));
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return bean;
	}
	
	public List<AtributoTarea> obtenerAtributosObservar(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = obtenerAtributosObservarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION, " +
						 "       TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
						 "  FROM BFP_CARTA_FIANZA.TAREA_ATR_OBSERVAR TAC, " +
						 "       BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						 " WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
						 "   AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoObservar(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosObservarPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAS.COD_ATRIBUTO " +
					     "  FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					     " WHERE OAS.PK = '1' AND OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_OBSERVAR TAC WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO AND TAC.COD_TAREA = ?) " +
					     " ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoObservar(rs.getInt("COD_ATRIBUTO"),conn);
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosCancelar(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = obtenerAtributosCancelarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION, " +
						 "       TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
						 "  FROM BFP_CARTA_FIANZA.TAREA_ATR_CANCELAR TAC, " +
						 "       BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						 " WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
						 "   AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoCancelar(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosCancelarPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAS.COD_ATRIBUTO " +
					     "  FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					     " WHERE OAS.PK = '1' AND OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_CANCELAR TAC WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO AND TAC.COD_TAREA = ?) " +
					     " ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoCancelar(rs.getInt("COD_ATRIBUTO"),conn);
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}

	public List<AtributoTarea> obtenerAtributosRechazar(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = obtenerAtributosRechazarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION, " +
						 "       TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
						 "  FROM BFP_CARTA_FIANZA.TAREA_ATR_RECHAZAR TAC, " +
						 "       BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						 " WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
						 "   AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoRechazar(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosRechazarPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAS.COD_ATRIBUTO " +
					     "  FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					     " WHERE OAS.PK = '1' AND OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_RECHAZAR TAC WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO AND TAC.COD_TAREA = ?) " +
					     " ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoRechazar(rs.getInt("COD_ATRIBUTO"),conn);
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<Clase> obtenerClasesCompletar(Tarea tarea, Connection conn){
		List<Clase> clases = new ArrayList<Clase>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			Clase bean = null;
			while (rs.next()) {
				bean = new Clase();
				bean.setCodigo(rs.getInt("COD_CLASE"));
				bean.setJavaClase(rs.getString("NOMBRE"));
				bean.setJavaPaquete(rs.getString("PAQUETE"));
				bean.setInformacionAutor(rs.getString("INF_AUTOR"));
				bean.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				bean.setAtributos(obtenerAtributosCompletar(tarea, conn));
				clases.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clases;
	}
	
	public List<Clase> obtenerClasesObservar(Tarea tarea, Connection conn){
		List<Clase> clases = new ArrayList<Clase>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TAREA_ATR_OBSERVAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			Clase bean = null;
			while (rs.next()) {
				bean = new Clase();
				bean.setCodigo(rs.getInt("COD_CLASE"));
				bean.setJavaClase(rs.getString("NOMBRE"));
				bean.setJavaPaquete(rs.getString("PAQUETE"));
				bean.setInformacionAutor(rs.getString("INF_AUTOR"));
				bean.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				bean.setAtributos(obtenerAtributosObservar(tarea, conn));
				clases.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clases;
	}
	
	public List<Clase> obtenerClasesCancelar(Tarea tarea, Connection conn){
		List<Clase> clases = new ArrayList<Clase>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TAREA_ATR_CANCELAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			Clase bean = null;
			while (rs.next()) {
				bean = new Clase();
				bean.setCodigo(rs.getInt("COD_CLASE"));
				bean.setJavaClase(rs.getString("NOMBRE"));
				bean.setJavaPaquete(rs.getString("PAQUETE"));
				bean.setInformacionAutor(rs.getString("INF_AUTOR"));
				bean.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				bean.setAtributos(obtenerAtributosCancelar(tarea, conn));
				clases.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clases;
	}
	
	public List<Clase> obtenerClasesRechazar(Tarea tarea, Connection conn){
		List<Clase> clases = new ArrayList<Clase>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TAREA_ATR_RECHAZAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			Clase bean = null;
			while (rs.next()) {
				bean = new Clase();
				bean.setCodigo(rs.getInt("COD_CLASE"));
				bean.setJavaClase(rs.getString("NOMBRE"));
				bean.setJavaPaquete(rs.getString("PAQUETE"));
				bean.setInformacionAutor(rs.getString("INF_AUTOR"));
				bean.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				bean.setAtributos(obtenerAtributosRechazar(tarea, conn));
				clases.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clases;
	}	
	
	public Clase obtenerClasePadreTrabajar(Proyecto proyecto, Connection conn){
		Clase clase = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
						 "FROM BFP_CARTA_FIANZA.CLASE T3 WHERE T3.NIVEL = 1 AND T3.COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();
			
			if (rs.next()) {
				clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clase;
	}
	
	public Clase obtenerClasePadreCompletar(Tarea tarea, Connection conn){
		Clase clase = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE AND T3.NIVEL = 1";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			if (rs.next()) {
				clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clase;
	}	
	
	public Clase obtenerClasePadreObservar(Tarea tarea, Connection conn){
		Clase clase = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TAR_ATR_OBSERVAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE AND T3.NIVEL = 1";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			if (rs.next()) {
				clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clase;
	}
	
	public Clase obtenerClasePadreCancelar(Tarea tarea, Connection conn){
		Clase clase = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TRA_ATR_CANCELAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE AND T3.NIVEL = 1";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			if (rs.next()) {
				clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clase;
	}	
	
	public Clase obtenerClasePadreRechazar(Tarea tarea, Connection conn){
		Clase clase = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.TAR_ATR_RECHAZAR T1, BFP_CARTA_FIANZA.ATRIBUTO T2, BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_CLASE = T3.COD_CLASE AND T3.NIVEL = 1";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			if (rs.next()) {
				clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clase;
	}
	
	public List<AtributoTarea> obtenerPKs(AtributoTarea atributoTarea, Connection conn){
		List<AtributoTarea> atributoTareas = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT T2.COD_ATRIBUTO, T2.COD_TABLA, T2.COD_ATRIBUTO, T2.CAMPO, T2.TIPO, T2.LONGITUD, T2.PRECISION, T2.PK, " +
					"T2.OBLIGATORIO, T2.FK_CAMPO, T2.FK_TABLA, T2.FK_UNO_MUCHO, T2.FN_BUS_NOMBRE, T2.FN_BUS_CATALOGO, T2.VAL_DEFECTO " +
					"FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T2 " +
					"WHERE T2.COD_TABLA IN (SELECT DISTINCT T1.COD_TABLA FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T1 WHERE T1.COD_ATRIBUTO = ?) " +
					"AND PK = '1'";
			cs = conn.prepareCall(sql);
			cs.setInt(1, atributoTarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				CampoSQL campo = new CampoSQL();
				campo.setNombre(rs.getString("CAMPO"));
				campo.setTipo(rs.getString("TIPO"));
				campo.setLongitud(rs.getInt("LONGITUD"));
				campo.setPrecision(rs.getInt("PRECISION"));
				campo.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				bean.setCampoSQL(campo);
				atributoTareas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributoTareas;
	}
	
	public List<AtributoTarea> obtenerFKs(Tarea tarea, Connection conn){
		List<AtributoTarea> atributoTareas = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT T3.COD_ATRIBUTO, T3.COD_TABLA, T3.COD_ATRIBUTO, T3.CAMPO, T3.TIPO, T3.LONGITUD, T3.PRECISION, T3.PK, " +
					"T3.OBLIGATORIO, T3.FK_CAMPO, T3.FK_TABLA, T3.FK_UNO_MUCHO, T3.FN_BUS_NOMBRE, T3.FN_BUS_CATALOGO, T3.VAL_DEFECTO " +
					"FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T3 " +
					"WHERE T3.COD_TABLA IN (SELECT DISTINCT T2.COD_TABLA FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR T1, BFP_CARTA_FIANZA.ATRIBUTO_SQL T2 WHERE T1.COD_TAREA = ? AND T1.COD_ATRIBUTO = T2.COD_ATRIBUTO) AND T3.PK <> '1'";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				CampoSQL campo = new CampoSQL();
				campo.setNombre(rs.getString("CAMPO"));
				campo.setTipo(rs.getString("TIPO"));
				campo.setLongitud(rs.getInt("LONGITUD"));
				campo.setPrecision(rs.getInt("PRECISION"));
				campo.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				bean.setCampoSQL(campo);
				atributoTareas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributoTareas;
	}
	
	public Map<Integer,Object> obtenerAtributosCompletarBPMMap(Proyecto proyecto, Connection conn){
		Map<Integer,Object> tareaCompleta = new HashMap<Integer, Object>();
		
		try{
			
			CallableStatement cs = null;
			ResultSet rs = null;
			
			//TODO daniel plz de la tabla: TAR_ATR_COMPLETAR
			String sql = 	"SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION, TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
					"  FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, " +
					"       BFP_CARTA_FIANZA.ATRIBUTO ATR " +
					" WHERE ATR.COD_ATRIBUTO = TAC.COD_ATRIBUTO " +
					"   AND TAC.COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				
				Integer codigoTarea = rs.getInt("COD_TAREA"); //codigo de tarea
				String codigoAtributo = rs.getString("COD_ATRIBUTO"); //codigo de atributo
				boolean columnaEntrada = false;
				
				if(tareaCompleta.get(codigoTarea) == null){
					Map<String,Boolean> esAtributoEntrada = new HashMap<String, Boolean>();
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				} else {
					Map<String,Boolean> esAtributoEntrada = (Map<String, Boolean>) tareaCompleta.get(codigoTarea);
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tareaCompleta;
	}
	
	public Map<String,Object> obtenerAtributosObservarBPMMap(Proyecto proyecto, Connection conn){
		Map<String,Object> tareaCompleta = new HashMap<String, Object>();
		
		try{
			
			CallableStatement cs = null;
			ResultSet rs = null;
			
			//TODO daniel plz de la tabla: TAR_ATR_OBSERVAR
			String sql = 	"SELECT COD_TAREA, COD_PROCESO, COD_PROYECTO, NOMBRE, VERSION, COD_CON_TRABAJAR, COD_CON_COMPLETAR, SQL_ALEAS, " +
							"SQL_DATASOURCE, BPM_NOMBRE, JAV_PAQUETE, JAV_CLASE, WEB_ACC_COMPLETAR, WEB_ACC_GRABAR, WEB_ACC_CANCELAR, " +
							"WEB_ACC_RECHAZAR, " +
							"WEB_ACC_OBSERVAR, WEB_ACC_SALIR, WEB_ACC_SUBSANAR, WEB_PAR_HIS_COMENTARIO, WEB_PAR_HIS_ACCION, WEB_TIE_ROJO, " +
							"WEB_TIE_AMARILLO, WEB_FLG_ARC_ADJUNTOS, WEB_FLG_ARC_ADICIONALES FROM BFP_CARTA_FIANZA.TAREA WHERE COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				
				String codigoTarea = rs.getString(""); //codigo de tarea
				String codigoAtributo = rs.getString(""); //codigo de atributo
				boolean columnaEntrada = rs.getBoolean(""); //codigo de Entrada BPM (si o no), campo BPM_FLG_ENTRADA
				
				if(tareaCompleta.get(codigoTarea) == null){
					Map<String,Boolean> esAtributoEntrada = new HashMap<String, Boolean>();
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				} else {
					Map<String,Boolean> esAtributoEntrada = (Map<String, Boolean>) tareaCompleta.get(codigoTarea);
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tareaCompleta;
	}
	
	public Map<Integer,Object> obtenerAtributosRechazarBPMMap(Proyecto proyecto, Connection conn){
		Map<Integer,Object> tareaCompleta = new HashMap<Integer, Object>();
		
		try{
			
			CallableStatement cs = null;
			ResultSet rs = null;
			
			//TODO daniel plz de la tabla: TAR_ATR_RECHAZAR
			String sql = 	"SELECT COD_TAREA, COD_PROCESO, COD_PROYECTO, NOMBRE, VERSION, COD_CON_TRABAJAR, COD_CON_COMPLETAR, SQL_ALEAS, " +
							"SQL_DATASOURCE, BPM_NOMBRE, JAV_PAQUETE, JAV_CLASE, WEB_ACC_COMPLETAR, WEB_ACC_GRABAR, WEB_ACC_CANCELAR, " +
							"WEB_ACC_RECHAZAR, " +
							"WEB_ACC_OBSERVAR, WEB_ACC_SALIR, WEB_ACC_SUBSANAR, WEB_PAR_HIS_COMENTARIO, WEB_PAR_HIS_ACCION, WEB_TIE_ROJO, " +
							"WEB_TIE_AMARILLO, WEB_FLG_ARC_ADJUNTOS, WEB_FLG_ARC_ADICIONALES FROM BFP_CARTA_FIANZA.TAREA WHERE COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				
				Integer codigoTarea = rs.getInt("COD_TAREA"); //codigo de tarea
				String codigoAtributo = rs.getString("COD_PROCESO"); //codigo de atributo
				boolean columnaEntrada = rs.getBoolean(""); //codigo de Entrada BPM (si o no), campo BPM_FLG_ENTRADA
				
				if(tareaCompleta.get(codigoTarea) == null){
					Map<String,Boolean> esAtributoEntrada = new HashMap<String, Boolean>();
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				} else {
					Map<String,Boolean> esAtributoEntrada = (Map<String, Boolean>) tareaCompleta.get(codigoTarea);
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tareaCompleta;
	}
	
	public Map<String,Object> obtenerAtributosCancelarBPMMap(Proyecto proyecto, Connection conn){
		Map<String,Object> tareaCompleta = new HashMap<String, Object>();
		
		try{
			
			CallableStatement cs = null;
			ResultSet rs = null;
			
			//TODO daniel plz de la tabla: TAR_ATR_CANCELAR
			String sql = 	"SELECT COD_TAREA, COD_PROCESO, COD_PROYECTO, NOMBRE, VERSION, COD_CON_TRABAJAR, COD_CON_COMPLETAR, SQL_ALEAS, " +
							"SQL_DATASOURCE, BPM_NOMBRE, JAV_PAQUETE, JAV_CLASE, WEB_ACC_COMPLETAR, WEB_ACC_GRABAR, WEB_ACC_CANCELAR, " +
							"WEB_ACC_RECHAZAR, " +
							"WEB_ACC_OBSERVAR, WEB_ACC_SALIR, WEB_ACC_SUBSANAR, WEB_PAR_HIS_COMENTARIO, WEB_PAR_HIS_ACCION, WEB_TIE_ROJO, " +
							"WEB_TIE_AMARILLO, WEB_FLG_ARC_ADJUNTOS, WEB_FLG_ARC_ADICIONALES FROM BFP_CARTA_FIANZA.TAREA WHERE COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				
				String codigoTarea = rs.getString(""); //codigo de tarea
				String codigoAtributo = rs.getString(""); //codigo de atributo
				boolean columnaEntrada = rs.getBoolean(""); //codigo de Entrada BPM (si o no), campo BPM_FLG_ENTRADA
				
				if(tareaCompleta.get(codigoTarea) == null){
					Map<String,Boolean> esAtributoEntrada = new HashMap<String, Boolean>();
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				} else {
					Map<String,Boolean> esAtributoEntrada = (Map<String, Boolean>) tareaCompleta.get(codigoTarea);
					if(columnaEntrada){
						esAtributoEntrada.put(codigoAtributo, true);
					} else {
						esAtributoEntrada.put(codigoAtributo, false);
					}
					tareaCompleta.put(codigoTarea, esAtributoEntrada);
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tareaCompleta;
	}
	
	public AtributoTarea obtenerAtributo(Integer codigoAtributo, Connection conn){
		AtributoTarea atributo = null;
		if(atributosTareaMap.containsKey(codigoAtributo)==false){
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_CLASE, NOMBRE, TIPO, FLG_LISTA, WEB_NOMBRE, WEB_FORMATO, INF_NOMBRE, INF_DESCRIPCION, INF_AUTOR, WEB_FOR_VALIDACION FROM BFP_CARTA_FIANZA.ATRIBUTO WHERE COD_ATRIBUTO = "+codigoAtributo);
				if (rs.next()) {
					atributo = new AtributoTarea();
					atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
					atributo.setCodigoClase(rs.getInt("COD_CLASE"));
					atributo.setClase(obtenerClase(atributo.getCodigoClase(), conn));
					atributo.setNombre(rs.getString("NOMBRE"));
					atributo.setTipo(rs.getString("TIPO"));
					atributo.setFlgLista(rs.getBoolean("FLG_LISTA"));
					atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
					atributo.setWebFormato(rs.getString("WEB_FORMATO"));
					atributo.setInformacionAutor(rs.getString("INF_AUTOR"));
					atributo.setInformacionNombre(rs.getString("INF_NOMBRE"));
					atributo.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
					atributo.setWebFormatoValidacion(rs.getString("WEB_FOR_VALIDACION"));
					atributo.setCampoSQLTarea(obtenerCampo(atributo.getCodigo(), conn));
					
					atributo = obtenerObjetoBPM(atributo, conn);
					atributosTareaMap.put(atributo.getCodigo(), atributo);
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
			atributo = atributosTareaMap.get(codigoAtributo);
		}
		return atributo;
	}
	
	public AtributoTarea obtenerAtributoCancelar(Integer codigoAtributo, Connection conn){
		AtributoTarea atributo = null;
		if(atributosTareaCancelarMap.containsKey(codigoAtributo)==false){
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_CLASE, NOMBRE, TIPO, FLG_LISTA, WEB_NOMBRE, WEB_FORMATO, INF_NOMBRE, INF_DESCRIPCION, INF_AUTOR, WEB_FOR_VALIDACION FROM BFP_CARTA_FIANZA.ATRIBUTO WHERE COD_ATRIBUTO = "+codigoAtributo);
				if (rs.next()) {
					atributo = new AtributoTarea();
					atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
					atributo.setCodigoClase(rs.getInt("COD_CLASE"));
					atributo.setClase(obtenerClase(atributo.getCodigoClase(), conn));
					atributo.setNombre(rs.getString("NOMBRE"));
					atributo.setTipo(rs.getString("TIPO"));
					atributo.setFlgLista(rs.getBoolean("FLG_LISTA"));
					atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
					atributo.setWebFormato(rs.getString("WEB_FORMATO"));
					atributo.setInformacionAutor(rs.getString("INF_AUTOR"));
					atributo.setInformacionNombre(rs.getString("INF_NOMBRE"));
					atributo.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
					atributo.setWebFormatoValidacion(rs.getString("WEB_FOR_VALIDACION"));
					atributo.setCampoSQLTarea(obtenerCampo(atributo.getCodigo(), conn));
					
					atributo = obtenerObjetoBPM(atributo, conn);
					atributosTareaCancelarMap.put(atributo.getCodigo(), atributo);
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
			atributo = atributosTareaCancelarMap.get(codigoAtributo);
		}
		return atributo;
	}
	
	public AtributoTarea obtenerAtributoRechazar(Integer codigoAtributo, Connection conn){
		AtributoTarea atributo = null;
		if(atributosTareaRechazarMap.containsKey(codigoAtributo)==false){
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_CLASE, NOMBRE, TIPO, FLG_LISTA, WEB_NOMBRE, WEB_FORMATO, INF_NOMBRE, INF_DESCRIPCION, INF_AUTOR, WEB_FOR_VALIDACION FROM BFP_CARTA_FIANZA.ATRIBUTO WHERE COD_ATRIBUTO = "+codigoAtributo);
				if (rs.next()) {
					atributo = new AtributoTarea();
					atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
					atributo.setCodigoClase(rs.getInt("COD_CLASE"));
					atributo.setClase(obtenerClase(atributo.getCodigoClase(), conn));
					atributo.setNombre(rs.getString("NOMBRE"));
					atributo.setTipo(rs.getString("TIPO"));
					atributo.setFlgLista(rs.getBoolean("FLG_LISTA"));
					atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
					atributo.setWebFormato(rs.getString("WEB_FORMATO"));
					atributo.setInformacionAutor(rs.getString("INF_AUTOR"));
					atributo.setInformacionNombre(rs.getString("INF_NOMBRE"));
					atributo.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
					atributo.setWebFormatoValidacion(rs.getString("WEB_FOR_VALIDACION"));
					atributo.setCampoSQLTarea(obtenerCampo(atributo.getCodigo(), conn));
					
					atributo = obtenerObjetoBPM(atributo, conn);
					atributosTareaRechazarMap.put(atributo.getCodigo(), atributo);
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
			atributo = atributosTareaRechazarMap.get(codigoAtributo);
		}
		return atributo;
	}
	
	public AtributoTarea obtenerAtributoSinCampoSQL(Integer codigoAtributo, Connection conn){
		AtributoTarea atributo = null;
		if(atributosTareaSinCampoSQLMap.containsKey(codigoAtributo)==false){
			ClaseDaoImpl claseDaoImpl = new ClaseDaoImpl();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_CLASE, NOMBRE, TIPO, FLG_LISTA, WEB_NOMBRE, WEB_FORMATO, INF_NOMBRE, INF_DESCRIPCION, INF_AUTOR FROM BFP_CARTA_FIANZA.ATRIBUTO WHERE COD_ATRIBUTO = "+codigoAtributo);
				if (rs.next()) {
					atributo = new AtributoTarea();
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
					if(atributo.getCodigoClase()>0){
						atributo.setClase(claseDaoImpl.obtenerClase(atributo.getCodigoClase(), conn));
					}
					//atributo.setCampoSQLTarea(obtenerCampo(atributo.getCodigo(), conn));
					atributosTareaSinCampoSQLMap.put(atributo.getCodigo(), atributo);
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
			atributo = atributosTareaSinCampoSQLMap.get(codigoAtributo);
		}
		return atributo;
	}
	
	public CampoSQLTarea obtenerCampo(Integer codigoAtributo, Connection conn){
		CampoSQLTarea campoSQL = null;
		if(CampoSQLTareaMap.containsKey(codigoAtributo)==false){
			Statement stmt = null;
			ResultSet rs = null;
			
			try{
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_ATRIBUTO, COD_TABLA, CAMPO, TIPO, LONGITUD, PRECISION, PK, OBLIGATORIO, FK_CAMPO, FK_UNO_MUCHO, FN_BUS_NOMBRE, FN_BUS_CATALOGO, VAL_DEFECTO FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL WHERE COD_ATRIBUTO = "+codigoAtributo+"");
				if (rs.next()) {
					campoSQL = parseCampo(rs, conn);
					if(campoSQL!=null && campoSQL.getCodigoTabla()>0){
						campoSQL.setTabla(obtenerTabla(campoSQL.getCodigoTabla(), conn));
					}
					CampoSQLTareaMap.put(codigoAtributo, campoSQL);
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
		}else{
			campoSQL = CampoSQLTareaMap.get(codigoAtributo);
		}
		return campoSQL;
	}
	
	private CampoSQLTarea parseCampo(ResultSet rs, Connection conn) throws SQLException{
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
		campoSQL.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
		campoSQL.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
		
		campoSQL.setAtributoTarea(obtenerAtributoSinCampoSQL(campoSQL.getCodigoAtributo(), conn));
		int codigoFK = rs.getInt("FK_CAMPO");
		if(codigoFK>0){
			campoSQL.setFk(obtenerCampo(codigoFK, conn));
		}
		return campoSQL;
	}
	
	public Tabla obtenerTabla(Integer codigoTabla, Connection conn){
		TablaDaoImpl tablaDaoImpl = new TablaDaoImpl();
		Tabla tabla = null;
		
		if(tablaMap.containsKey(codigoTabla)){
			tabla = tablaMap.get(codigoTabla);
		}else{
			Statement stmt = null;
			ResultSet rs = null;
			
			try{
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_TABLA, ESQUEMA, NOMBRE, COD_PROYECTO FROM BFP_CARTA_FIANZA.TABLA WHERE COD_TABLA = "+codigoTabla+"");
				if (rs.next()) {
					tabla = new Tabla();
					tabla.setCodigo(rs.getInt("COD_TABLA"));
					tabla.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
					tabla.setEsquema(rs.getString("ESQUEMA"));
					tabla.setNombre(rs.getString("NOMBRE"));
					tabla.setCamposPK(tablaDaoImpl.obtenerCamposPK(codigoTabla, conn));
					tabla.setCamposFK(tablaDaoImpl.obtenerCamposFK(codigoTabla, conn));
					tablaMap.put(codigoTabla, tabla);
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
		}
		return tabla;
	}
	
	public AtributoTarea obtenerObjetoBPM(AtributoTarea atributo, Connection conn){
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT OAB.COD_ATRIBUTO AS COD_ATRIBUTO, " +
					"OAB.COD_OBJ_BPM AS COD_OBJ_BPM, " +
					"OAB.NOMBRE AS OAB_NOMBRE, " +
					"OAB.TIPO AS OAB_TIPO, " +
					"OAB.OBJ_RELACIONADO AS OBJ_RELACIONADO, " +
					"OB.NOMBRE AS OB_NOMBRE " +
					"FROM BFP_CARTA_FIANZA.ATRIBUTO_DO OAB, " +
					"BFP_CARTA_FIANZA.OBJ_BPM OB " +
					"WHERE OAB.COD_OBJ_BPM = OB.COD_OBJ_BPM " +
					" AND OAB.COD_ATRIBUTO = "+atributo.getCodigo());
			if (rs.next()) {
				atributo.setProcesoObjeto(rs.getString("OB_NOMBRE"));
				atributo.setProcesoObjetoAtributo(rs.getString("OAB_NOMBRE"));
				atributo.setProcesoObjetoTipoDato(rs.getString("OAB_TIPO"));
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
		return atributo;
	}	
	
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaCompletar(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = new ArrayList<CampoSQLTarea>();
		
		campos = obtenerCampoSQLTareaCompletarPK(tarea, campos, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION ,TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION ,OAS.COD_ATRIBUTO,OAS.COD_TABLA ,OAS.CAMPO, OAS.TIPO,OAS.LONGITUD ,OAS.PRECISION ,OAS.PK ,OAS.OBLIGATORIO, OAS.FK_CAMPO ,OAS.FK_TABLA ,OAS.FK_UNO_MUCHO, OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO" +
					" FROM BFP_CARTA_FIANZA.ATRIBUTO ATR,BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS" +
					" WHERE TAC.COD_ATRIBUTO = OAS.COD_ATRIBUTO " +
					"   AND TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
					"   AND OAS.PK = '0' AND TAC.COD_TAREA = ? " +
					" ORDER BY TAC.COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaCompletarPK(Tarea tarea, List<CampoSQLTarea> campos, Connection conn){

		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA ,OAS.CAMPO, OAS.TIPO,OAS.LONGITUD ,OAS.PRECISION ,OAS.PK ,OAS.OBLIGATORIO, OAS.FK_CAMPO ,OAS.FK_TABLA ,OAS.FK_UNO_MUCHO, OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
					" FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					" WHERE OAS.PK = '1'  " +
					"   AND OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS WHERE TAC.COD_ATRIBUTO = OAS.COD_ATRIBUTO AND TAC.COD_TAREA = ? ORDER BY OAS.COD_TABLA)   " +
					" ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaTrabajar(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = obtenerCampoSQLTareaTrabajarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
						 " OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
						 " OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
						 " FROM BFP_CARTA_FIANZA.ATRIBUTO_DO OAB, BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS" +
						 " WHERE OAB.COD_ATRIBUTO = OAS.COD_ATRIBUTO " +
						 "   AND OAB.COD_OBJ_BPM IN (SELECT DISTINCT OB.COD_OBJ_BPM FROM BFP_CARTA_FIANZA.TAREA_TRA_BPM TTB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE TTB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND TTB.COD_TAREA = ?)" +
						 " ORDER BY OAS.COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaTrabajarPK(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = new ArrayList<CampoSQLTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
					" OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
					" OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO  " +
					" FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS WHERE OAS.PK = '1' AND OAS.COD_TABLA IN (SELECT DISTINCT OB.COD_TABLA FROM BFP_CARTA_FIANZA.TAREA_TRA_BPM TTB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE TTB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND TTB.COD_TAREA = ?) ORDER BY OAS.COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<AtributoTarea> obtenerAtributosTrabajar(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = obtenerAtributosTrabajarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAB.COD_ATRIBUTO " +
						 " FROM BFP_CARTA_FIANZA.ATRIBUTO_DO OAB" +
						 " WHERE OAB.COD_OBJ_BPM IN (SELECT DISTINCT OB.COD_OBJ_BPM FROM BFP_CARTA_FIANZA.TAREA_TRA_BPM TTB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE TTB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND TTB.COD_TAREA = ?)" +
						 " ORDER BY OAB.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			while (rs.next()) {
				AtributoTarea bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosTrabajarPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAS.COD_ATRIBUTO " +
					" FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					" WHERE OAS.PK = '1' AND OAS.COD_TABLA IN (SELECT DISTINCT OB.COD_TABLA FROM BFP_CARTA_FIANZA.TAREA_TRA_BPM TTB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE TTB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND TTB.COD_TAREA = ?)" +
					" ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			while (rs.next()) {
				AtributoTarea bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public CampoSQLTarea parseCampoSQLTarea(ResultSet rs, Connection conn) throws SQLException{
		CampoSQLTarea bean = new CampoSQLTarea();
		bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
		bean.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
		bean.setCodigoTabla(rs.getInt("COD_TABLA"));
		bean.setTabla(obtenerTabla(bean.getCodigoTabla(), conn));
		bean.setFlgPK(rs.getBoolean("PK"));
		bean.setNombre(rs.getString("CAMPO"));
		bean.setTipo(rs.getString("TIPO"));
		bean.setLongitud(rs.getInt("LONGITUD"));
		bean.setPrecision(rs.getInt("PRECISION"));
		bean.setFlgPK(rs.getBoolean("PK"));
		bean.setFlgObligatorio(rs.getBoolean("OBLIGATORIO"));
		int codigoFK = rs.getInt("FK_CAMPO");
		bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
		bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
		
		if(codigoFK>0){
			bean.setFk(obtenerCampo(codigoFK, conn));
		}
		return bean;
	}
	
	public List<ObjetoBPM> obtenerObjetosBPMCompletar(Integer codigoTarea, Connection conn){
		List<ObjetoBPM> objetosBPM = new ArrayList<ObjetoBPM>();
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT DISTINCT OB.COD_OBJ_BPM, OB.NOMBRE FROM BFP_CARTA_FIANZA.TAREA_COM_BPM TCB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE TCB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND TCB.COD_TAREA = "+codigoTarea);
			while (rs.next()) {
				ObjetoBPM objetoBPM = new ObjetoBPM();
				objetoBPM.setCodigo(rs.getInt("COD_OBJ_BPM"));
				objetoBPM.setNombre(rs.getString("NOMBRE"));
				objetoBPM.setAtributosTarea(obtenerObjetosBPMCompletar(codigoTarea, objetoBPM, conn));
				objetosBPM.add(objetoBPM);
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
		return objetosBPM;
	}
	
	public List<AtributoTarea> obtenerObjetosBPMCompletar(Integer codigoTarea, ObjetoBPM objetoBPM, Connection conn){
		List<AtributoTarea> atributosTareas = new ArrayList<AtributoTarea>();
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT DISTINCT OAB.COD_ATRIBUTO FROM BFP_CARTA_FIANZA.ATRIBUTO_DO OAB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE OAB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND OB.COD_OBJ_BPM = "+objetoBPM.getCodigo());
			while (rs.next()) {
				int codigoAtributo = rs.getInt("COD_ATRIBUTO");
				atributosTareas.add(obtenerAtributo(codigoAtributo, conn));
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
		return atributosTareas;
	}
	
	public Clase obtenerClase(Integer codigoClase, Connection conn){
		Clase clase = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
					"FROM BFP_CARTA_FIANZA.CLASE T3 " +
					"WHERE T3.COD_CLASE = "+codigoClase;
			cs = conn.prepareCall(sql);
			rs = cs.executeQuery();
			
			if (rs.next()) {
				clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clase;
	}

	public ConsultaTareaCompletar obtenerConsultaTareaCompletar(Tarea tarea, Connection conn){
		
		ConsultaTareaCompletar consultaTareaCompletar = new ConsultaTareaCompletar();
		consultaTareaCompletar.setAleas(tarea.getAleas()+EXT_ALEAS_COMPLETAR);
		consultaTareaCompletar.setPaquete(tarea.getPaquete()+EXT_PAQUETE_DAO);
		consultaTareaCompletar.setInterfase(tarea.getClase()+EXT_INTERFASE_DAO);
		
		consultaTareaCompletar.setTablaPadre(obtenerTablaPadre(tarea,conn));
		consultaTareaCompletar.setTablasFK(obtenerTablasFK(tarea,conn));
		
		return consultaTareaCompletar;
		
	}
	
	public TablaConsulta obtenerTablaPadre(Tarea tarea, Connection conn){
		TablaConsulta tablaConsulta = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T1.COD_TABLA, T1.ESQUEMA, T1.NOMBRE, T1.COD_PROYECTO " +
					" FROM BFP_CARTA_FIANZA.TABLA T1 " +
					" WHERE T1.COD_TABLA IN (SELECT OB.COD_TABLA FROM BFP_CARTA_FIANZA.OBJ_BPM OB WHERE OB.FLG_PADRE = '1' AND COD_PROYECTO = ?)";
			cs = conn.prepareCall(sql);
			int i=1;
			cs.setInt(i++, tarea.getProyecto().getCodigoProyecto());
			rs = cs.executeQuery();

			if (rs.next()) {
				tablaConsulta = new TablaConsulta();
				tablaConsulta.setCodigo(rs.getInt("COD_TABLA"));
				tablaConsulta.setCodigoConsulta(tarea.getCodigo());
				tablaConsulta.setEsquema(rs.getString("ESQUEMA"));
				tablaConsulta.setNombre(rs.getString("NOMBRE"));
				tablaConsulta.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				tablaConsulta.setCampoSQLConsultas(this.obtenerCamposSQLConsulta(tablaConsulta, conn));
				tablaConsulta.setAtributosConsulta(this.obtenerAtributoTareaConsulta(tablaConsulta.getCodigo(), conn));
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
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
				String sql = " (SELECT DISTINCT OAS.* FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS WHERE OAS.COD_TABLA = ? AND OAS.PK = '1') " +
						" UNION ( SELECT OAS.* FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.ATRIBUTO_DO OAB WHERE OAB.COD_ATRIBUTO = OAS.COD_ATRIBUTO AND OAB.COD_OBJ_BPM IN (SELECT DISTINCT OB.COD_OBJ_BPM FROM BFP_CARTA_FIANZA.OBJ_BPM OB WHERE OB.COD_TABLA = ?) AND OAS.PK = '0') ";
			cs = conn.prepareCall(sql);
			
			cs.setInt(1, tablaConsulta.getCodigo());
			cs.setInt(2, tablaConsulta.getCodigo());
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
					bean.setFk(consultaDaoImpl.obtenerCampoSQLXCodigoObjetoSQL(codigoFK, conn));
				}
				
				bean.setFkUnoMuchos("1".equals(rs.getString("FK_UNO_MUCHO")));
				bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
				bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
				bean.setValorDefecto(rs.getString("VAL_DEFECTO"));
				bean.setTabla(consultaDaoImpl.obtenerTabla(0, bean.getCodigoTabla(), conn));
				
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
	
	public List<AtributoConsulta> obtenerAtributoTareaConsulta(int codigoTabla, Connection conn){
		List<AtributoConsulta> atributosProceso = new ArrayList<AtributoConsulta>();
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "  SELECT OAS.COD_ATRIBUTO " +
					       "  FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, " +
					       "       BFP_CARTA_FIANZA.ATRIBUTO_DO OAB " +
					       " WHERE OAS.COD_ATRIBUTO = OAB.COD_ATRIBUTO " +
					       "   AND OAS.COD_TABLA = ?  " +
					       " ORDER BY OAS.COD_ATRIBUTO ";
			
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoTabla);
			rs = cs.executeQuery();

			atributosProceso = new ArrayList<AtributoConsulta>();
			AtributoConsulta bean = null;
			while (rs.next()) {
				bean = consultaDaoImpl.obtenerAtributo(rs.getInt("COD_ATRIBUTO"), conn);
				bean.setCampoSQL(obtenerCampoSQLConsulta(bean.getCodigo(), conn));
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
	
	public CampoSQLConsulta obtenerCampoSQLConsulta(int codigoAtributo, Connection conn){
		
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		CampoSQLConsulta bean = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T3.COD_ATRIBUTO, T3.COD_TABLA, T3.COD_ATRIBUTO, T3.CAMPO, T3.TIPO, T3.LONGITUD, " +
					"T3.PRECISION, T3.PK, T3.OBLIGATORIO, T3.FK_CAMPO, T3.FK_TABLA, T3.FK_UNO_MUCHO, T3.FN_BUS_NOMBRE, " +
					"T3.FN_BUS_CATALOGO, T3.VAL_DEFECTO " +
					"FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T3 " +
					"WHERE T3.COD_ATRIBUTO = ? ";
			cs = conn.prepareCall(sql);
			int i=1;
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
				bean.setFlgPK(rs.getBoolean("PK"));
				bean.setFlgObligatorio(rs.getBoolean("OBLIGATORIO"));
				int codigoAtributoFK = rs.getInt("FK_CAMPO");
				if(codigoAtributoFK>0){
					CampoSQL fk = consultaDaoImpl.obtenerCampoSQLXCodigoAtributo(codigoAtributoFK, conn);
					int codigoTablaFK = rs.getInt("FK_TABLA");
					if(codigoTablaFK>0){
						fk.setTabla(obtenerTabla(codigoTablaFK, conn));
					}
					bean.setFk(fk);
				}
				bean.setFkUnoMuchos("1".equals(rs.getString("FK_UNO_MUCHO")));
				bean.setFuncionBusqueda(rs.getString("FN_BUS_NOMBRE"));
				bean.setFuncionBusquedaCatalogo(rs.getString("FN_BUS_CATALOGO"));
				bean.setValorDefecto(rs.getString("VAL_DEFECTO"));
				bean.setTabla(obtenerTabla(bean.getCodigoTabla(), conn));
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
	
	public List<TablaConsulta> obtenerTablasFK(Tarea tarea, Connection conn){
		List<TablaConsulta> tablaConsultas = new ArrayList<TablaConsulta>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT T1.COD_TABLA, T1.ESQUEMA, T1.NOMBRE, T1.COD_PROYECTO FROM BFP_CARTA_FIANZA.TABLA T1, BFP_CARTA_FIANZA.OBJ_BPM T2, BFP_CARTA_FIANZA.TAREA_COM_BPM TCB WHERE TCB.COD_OBJ_BPM = T2.COD_OBJ_BPM AND T1.COD_TABLA = T2.COD_TABLA AND T2.FLG_PADRE = '0' AND TCB.COD_TAREA = ?";
			cs = conn.prepareCall(sql);
			int i=1;
			cs.setInt(i++, tarea.getCodigo());
			rs = cs.executeQuery();

			TablaConsulta bean = null;
			while (rs.next()) {
				bean = new TablaConsulta();
				bean.setCodigoConsulta(tarea.getCodigo());
				bean.setCodigo(rs.getInt("COD_TABLA"));
				bean.setEsquema(rs.getString("ESQUEMA"));
				bean.setNombre(rs.getString("NOMBRE"));
				bean.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				//bean.setFlgFK(rs.getBoolean("FK"));
				//bean.setFlgUnoMuchos(rs.getBoolean("FLG_UNO_MUCHOS"));//TODO pendiente!!
				
				bean.setCampoSQLConsultas(this.obtenerCamposSQLConsulta(bean, conn));
				bean.setAtributosConsulta(this.obtenerAtributoTareaConsulta(bean.getCodigo(), conn));
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
	

	public List<ObjetoBPM> obtenerObjetosBPMTrabajar(Integer codigoTarea, Connection conn){
		List<ObjetoBPM> objetosBPM = new ArrayList<ObjetoBPM>();
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT DISTINCT TTB.COD_TAREA, OB.COD_OBJ_BPM, OB.NOMBRE FROM BFP_CARTA_FIANZA.TAREA_TRA_BPM TTB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE TTB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND TTB.COD_TAREA = "+codigoTarea);
			while (rs.next()) {
				ObjetoBPM objetoBPM = new ObjetoBPM();
				objetoBPM.setCodigo(rs.getInt("COD_OBJ_BPM"));
				objetoBPM.setNombre(rs.getString("NOMBRE"));
				objetoBPM.setAtributosTarea(obtenerObjetosBPMCompletar(codigoTarea, objetoBPM, conn));
				objetoBPM.setClase(obtenerClase(rs.getInt("COD_OBJ_BPM"), conn));
				objetosBPM.add(objetoBPM);
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
		return objetosBPM;
	}

	public List<AtributoTarea> obtenerAtributosCompletarValidacionWeb(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		atributos = obtenerAtributosCompletarPK(tarea.getCodigo(), atributos, conn);
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, " +
					" TAC.JAV_VAL_OMISION, TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION, TAC.WEB_TAB_CAMPO, TAC.WEB_ORD_VALIDACION " +
					" FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, BFP_CARTA_FIANZA.ATRIBUTO ATR " +
					" WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO AND COD_TAREA = ? AND WEB_FLG_VALIDACION = '1' ORDER BY TAC.WEB_ORD_VALIDACION ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setRequiereValidacion(rs.getBoolean("WEB_FLG_VALIDACION"));
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				bean.setWebTabCampo(rs.getInt("WEB_TAB_CAMPO"));
				boolean esPK = false;
				if(bean.getCampoSQLTarea()!=null &&
						bean.getCampoSQLTarea().isFlgPK()==true){
					esPK = true;
				}
				
				if(esPK==false){
					atributos.add(bean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return atributos;
	}
	
	public Clase obtenerClasePadre(Proyecto proyecto, Connection conn){
		Clase clase = null;
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT T3.COD_CLASE, T3.NOMBRE, T3.PAQUETE, T3.COD_PROYECTO, T3.INF_AUTOR, T3.INF_DESCRIPCION " +
						 "FROM BFP_CARTA_FIANZA.CLASE T3 WHERE T3.NIVEL = 1 AND T3.COD_PROYECTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();
			
			if (rs.next()) {
				clase = new Clase();
				clase.setCodigo(rs.getInt("COD_CLASE"));
				clase.setJavaClase(rs.getString("NOMBRE"));
				clase.setJavaPaquete(rs.getString("PAQUETE"));
				clase.setInformacionAutor(rs.getString("INF_AUTOR"));
				clase.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return clase;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaCancelar(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = obtenerCampoSQLTareaCancelarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "  SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
					     "                  OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
					     "                  OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
					     "             FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_CANCELAR TAC " +
					     "            WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO " +
					     "              AND TAC.COD_TAREA = ? " +
					     "              AND OAS.PK = '0' " +
					     "         ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaCancelarPK(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = new ArrayList<CampoSQLTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = " SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
					     "                 OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
					     "                 OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
					     "            FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					     "           WHERE OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_CANCELAR TAC WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO AND TAC.COD_TAREA = ?)" +
					     "             AND OAS.PK = '1' " +
					     "        ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaRechazar(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = obtenerCampoSQLTareaRechazarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "  SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
					     "                  OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
					     "                  OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
					     "             FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_RECHAZAR TAC " +
					     "            WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO " +
					     "              AND TAC.COD_TAREA = ? " +
					     "              AND OAS.PK = '0' " +
					     "         ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaRechazarPK(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = new ArrayList<CampoSQLTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = " SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
					     "                 OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
					     "                 OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
					     "            FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					     "           WHERE OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_RECHAZAR TAC WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO AND TAC.COD_TAREA = ?)" +
					     "             AND OAS.PK = '1' " +
					     "        ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaObservar(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = obtenerCampoSQLTareaObservarPK(tarea, conn);
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "  SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
					     "                  OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
					     "                  OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
					     "             FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_OBSERVAR TAC " +
					     "            WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO " +
					     "              AND TAC.COD_TAREA = ? " +
					     "              AND OAS.PK = '0' " +
					     "         ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<CampoSQLTarea> obtenerCampoSQLTareaObservarPK(Tarea tarea, Connection conn){
		List<CampoSQLTarea> campos = new ArrayList<CampoSQLTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = " SELECT DISTINCT OAS.COD_ATRIBUTO, OAS.COD_ATRIBUTO,OAS.COD_TABLA , OAS.CAMPO, OAS.TIPO, OAS.LONGITUD, OAS.PRECISION, OAS.PK, OAS.OBLIGATORIO, " +
					     "                 OAS.FK_CAMPO, OAS.FK_TABLA, OAS.FK_UNO_MUCHO, " +
					     "                 OAS.FN_BUS_NOMBRE, OAS.FN_BUS_CATALOGO, OAS.VAL_DEFECTO " +
					     "            FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS " +
					     "           WHERE OAS.COD_TABLA IN (SELECT DISTINCT OAS.COD_TABLA FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL OAS, BFP_CARTA_FIANZA.TAREA_ATR_OBSERVAR TAC WHERE OAS.COD_ATRIBUTO = TAC.COD_ATRIBUTO AND TAC.COD_TAREA = ?)" +
					     "             AND OAS.PK = '1' " +
					     "        ORDER BY OAS.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			while (rs.next()) {
				campos.add(parseCampoSQLTarea(rs, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return campos;
	}
	
	public List<AtributoTarea> obtenerAtributosTrabajarSinPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT OAB.COD_ATRIBUTO " +
						 " FROM BFP_CARTA_FIANZA.ATRIBUTO_DO OAB" +
						 " WHERE OAB.COD_OBJ_BPM IN (SELECT DISTINCT OB.COD_OBJ_BPM FROM BFP_CARTA_FIANZA.TAREA_TRA_BPM TTB, BFP_CARTA_FIANZA.OBJ_BPM OB WHERE TTB.COD_OBJ_BPM = OB.COD_OBJ_BPM AND TTB.COD_TAREA = ?)" +
						 " ORDER BY OAB.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			while (rs.next()) {
				AtributoTarea bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosCompletarSinPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, " +
						"  		 TAC.JAV_VAL_OMISION, TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION, TAC.WEB_TAB_CAMPO " +
						"  FROM BFP_CARTA_FIANZA.TAREA_ATR_COMPLETAR TAC, BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						" WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributo(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setRequiereValidacion(rs.getBoolean("WEB_FLG_VALIDACION"));
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				bean.setWebTabCampo(rs.getInt("WEB_TAB_CAMPO"));
				boolean esPK = false;
				if(bean.getCampoSQLTarea()!=null &&
						bean.getCampoSQLTarea().isFlgPK()==true){
					esPK = true;
				}
				
				if(esPK==false){
					atributos.add(bean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosCancelarSinPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION, " +
						 "       TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
						 "  FROM BFP_CARTA_FIANZA.TAREA_ATR_CANCELAR TAC, " +
						 "       BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						 " WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
						 "   AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoCancelar(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosRechazarSinPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION, " +
						 "       TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
						 "  FROM BFP_CARTA_FIANZA.TAREA_ATR_RECHAZAR TAC, " +
						 "       BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						 " WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
						 "   AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoRechazar(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public List<AtributoTarea> obtenerAtributosObservarSinPK(Tarea tarea, Connection conn){
		List<AtributoTarea> atributos = new ArrayList<AtributoTarea>();
		
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TAC.COD_PROCESO, TAC.COD_PROYECTO, TAC.COD_TAREA, TAC.COD_ATRIBUTO, TAC.JAV_VAL_OMISION, " +
						 "       TAC.WEB_FLG_VALIDACION, ATR.WEB_MEN_VALIDACION " +
						 "  FROM BFP_CARTA_FIANZA.TAREA_ATR_OBSERVAR TAC, " +
						 "       BFP_CARTA_FIANZA.ATRIBUTO ATR " +
						 " WHERE TAC.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
						 "   AND COD_TAREA = ? ORDER BY COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, tarea.getCodigo());
			rs = cs.executeQuery();
			
			AtributoTarea bean = null;
			while (rs.next()) {
				bean = obtenerAtributoObservar(rs.getInt("COD_ATRIBUTO"),conn);
				bean.setWebValorOmision(rs.getString("JAV_VAL_OMISION"));
				bean.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				atributos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return atributos;
	}
	
	public AtributoTarea obtenerAtributoObservar(Integer codigoAtributo, Connection conn){
		AtributoTarea atributo = null;
		if(atributosTareaObservarMap.containsKey(codigoAtributo)==false){
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COD_ATRIBUTO, COD_CLASE, NOMBRE, TIPO, FLG_LISTA, WEB_NOMBRE, WEB_FORMATO, INF_NOMBRE, INF_DESCRIPCION, INF_AUTOR, WEB_FOR_VALIDACION FROM BFP_CARTA_FIANZA.ATRIBUTO WHERE COD_ATRIBUTO = "+codigoAtributo);
				if (rs.next()) {
					atributo = new AtributoTarea();
					atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
					atributo.setCodigoClase(rs.getInt("COD_CLASE"));
					atributo.setClase(obtenerClase(atributo.getCodigoClase(), conn));
					atributo.setNombre(rs.getString("NOMBRE"));
					atributo.setTipo(rs.getString("TIPO"));
					atributo.setFlgLista(rs.getBoolean("FLG_LISTA"));
					atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
					atributo.setWebFormato(rs.getString("WEB_FORMATO"));
					atributo.setInformacionAutor(rs.getString("INF_AUTOR"));
					atributo.setInformacionNombre(rs.getString("INF_NOMBRE"));
					atributo.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
					atributo.setWebFormatoValidacion(rs.getString("WEB_FOR_VALIDACION"));
					atributo.setCampoSQLTarea(obtenerCampo(atributo.getCodigo(), conn));
					
					atributo = obtenerObjetoBPM(atributo, conn);
					atributosTareaObservarMap.put(atributo.getCodigo(), atributo);
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
			atributo = atributosTareaObservarMap.get(codigoAtributo);
		}
		return atributo;
	}
	
}