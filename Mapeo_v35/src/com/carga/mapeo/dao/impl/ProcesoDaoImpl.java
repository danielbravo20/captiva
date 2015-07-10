package com.carga.mapeo.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.carga.mapeo.dao.ConexionBD;
import com.carga.portal.modelo.AtributoConsulta;
import com.carga.portal.modelo.AtributoProceso;
import com.carga.portal.modelo.CampoSQL;
import com.carga.portal.modelo.CampoSQLProceso;
import com.carga.portal.modelo.Consulta;
import com.carga.portal.modelo.Proceso;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.Tabla;
import com.carga.portal.modelo.Tarea;

public class ProcesoDaoImpl extends ConexionBD {

	public List<Proceso> obtenerProcesos(Proyecto proyecto, Connection conn) {
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		List<Proceso> procesos = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery( "SELECT COD_PROCESO, " +
									" COD_PROYECTO, "
					+ "INF_NOMBRE, " + "COD_UNI_NEGOCIO, " + "COD_PRODUCTO, "
					+ "INF_SUFIJO, " + "BPM_PLANTILLA, " + "BPM_ACT_INICIO, "
					+ "JAV_PAQUETE, " + "JAV_CLASE, " + "JAV_ALE_PROCESO, "
					+ "JAV_DATASOURCE, " + "COD_CON_RESUMEN, "
					+ "COD_CON_DETALLE, JAV_DOC_SEQUENCE " + "FROM BFP_CARTA_FIANZA.PROCESO "
					+ "WHERE COD_PROYECTO = " + proyecto.getCodigoProyecto()
					+ "");

			while (rs.next()) {
				if (procesos == null) {
					procesos = new ArrayList<Proceso>();
				}
				Proceso proceso = new Proceso();
				proceso.setCodigo(rs.getInt("COD_PROCESO"));
				proceso.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				proceso.setNombre(rs.getString("INF_NOMBRE"));
				proceso.setSufijoBanca(rs.getString("COD_UNI_NEGOCIO"));
				proceso.setSufijoProducto(rs.getString("COD_PRODUCTO"));
				proceso.setSufijoProceso(rs.getString("INF_SUFIJO"));
				proceso.setNombrePlantilla(rs.getString("BPM_PLANTILLA"));
				proceso.setActividadInicio(rs.getInt("BPM_ACT_INICIO"));
				proceso.setPaquete(rs.getString("JAV_PAQUETE"));
				proceso.setClase(rs.getString("JAV_CLASE"));
				proceso.setAleasSpring(rs.getString("JAV_ALE_PROCESO"));
				proceso.setDatasource(rs.getString("JAV_DATASOURCE"));
				proceso.setNombreSecuenciaDocumentos(rs.getString("JAV_DOC_SEQUENCE"));
				proceso.setConsultaResumen(consultaDaoImpl.obtenerConsulta(rs.getInt("COD_CON_RESUMEN"), conn));
				proceso.setConsultaDetalle(consultaDaoImpl.obtenerConsulta(rs.getInt("COD_CON_DETALLE"), conn));
				proceso.setTareas(obtenerTareas(proceso, conn));
				proceso.setAtributosEntrada(obtenerAtributos(proceso, conn));
				proceso.setCamposSQLProceso(obtenerCamposSQL(proceso, conn));
				proceso.setObjetosDOEntreda(obtenerObjetoBPMEntradaSinReferencia(proceso, conn));
				proceso.setObjetosDOsinEntreda(obtenerObjetoBPMEntradaConReferencia(proceso, conn));
				procesos.add(proceso);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return procesos;
	}

	public Proceso obtenerProceso(Tarea tarea, Connection conn) {
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		Statement stmt = null;
		ResultSet rs = null;
		Proceso proceso = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COD_PROCESO, " + "COD_PROYECTO, "
					+ "INF_NOMBRE, " + "COD_UNI_NEGOCIO, " + "COD_PRODUCTO, "
					+ "INF_SUFIJO, " + "BPM_PLANTILLA, " + "BPM_ACT_INICIO, "
					+ "JAV_PAQUETE, " + "JAV_CLASE, " + "JAV_ALE_PROCESO, "
					+ "JAV_DATASOURCE, " + "COD_CON_RESUMEN, "
					+ "COD_CON_DETALLE " + "FROM BFP_CARTA_FIANZA.PROCESO "
					+ "WHERE COD_PROYECTO = " + tarea.getProyecto().getCodigoProyecto() + "");

			while (rs.next()) {

				proceso = new Proceso();
				proceso.setCodigo(rs.getInt("COD_PROCESO"));
				proceso.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				proceso.setNombre(rs.getString("INF_NOMBRE"));
				proceso.setSufijoBanca(rs.getString("COD_UNI_NEGOCIO"));
				proceso.setSufijoProducto(rs.getString("COD_PRODUCTO"));
				proceso.setSufijoProceso(rs.getString("INF_SUFIJO"));
				proceso.setNombrePlantilla(rs.getString("BPM_PLANTILLA"));
				proceso.setActividadInicio(rs.getInt("BPM_ACT_INICIO"));
				proceso.setPaquete(rs.getString("JAV_PAQUETE"));
				proceso.setClase(rs.getString("JAV_CLASE"));
				proceso.setAleasSpring(rs.getString("JAV_ALE_PROCESO"));
				proceso.setDatasource(rs.getString("JAV_DATASOURCE"));
				proceso.setConsultaResumen(consultaDaoImpl.obtenerConsulta(rs.getInt("COD_CON_RESUMEN"), conn));
				proceso.setConsultaDetalle(consultaDaoImpl.obtenerConsulta(rs.getInt("COD_CON_DETALLE"), conn));
				proceso.setTareas(obtenerTareas(proceso, conn));
				proceso.setAtributosEntrada(obtenerAtributos(proceso, conn));

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return proceso;
	}

	public List<Tarea> obtenerTareas(Proceso proceso, Connection conn) {
		List<Tarea> tareas = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		ProyectoDaoImpl proyectoDaoImpl = new ProyectoDaoImpl();
		ProcesoDaoImpl procesoDaoImpl = new ProcesoDaoImpl();
		TareaDaoImpl tareaDaoImpl = new TareaDaoImpl();
		try {
			String sql = "SELECT COD_TAREA, COD_PROCESO, COD_PROYECTO, NOMBRE, VERSION, COD_CON_TRABAJAR, COD_CON_COMPLETAR, "
					+ "SQL_ALEAS, SQL_DATASOURCE, BPM_NOMBRE, JAV_PAQUETE, JAV_CLASE, WEB_ACC_COMPLETAR, WEB_ACC_GRABAR, "
					+ "WEB_ACC_CANCELAR, WEB_ACC_RECHAZAR, WEB_ACC_OBSERVAR, WEB_ACC_SALIR, WEB_ACC_SUBSANAR, WEB_PAR_HIS_COMENTARIO, "
					+ "WEB_PAR_HIS_ACCION, WEB_TIE_ROJO, WEB_TIE_AMARILLO, WEB_FLG_ARC_ADJUNTOS, WEB_FLG_ARC_ADICIONALES, WEB_NOM_CONFIGURACION "
					+ "FROM BFP_CARTA_FIANZA.TAREA WHERE COD_PROCESO  = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			tareas = new ArrayList<Tarea>();
			Tarea tarea = null;
			while (rs.next()) {
				tarea = new Tarea();
				tarea.setCodigo(rs.getInt("COD_TAREA"));
				tarea.setProyecto(proyectoDaoImpl.obtenerProyecto(rs.getString("COD_PROYECTO"), conn));
				tarea.setProceso(procesoDaoImpl.obtenerProceso(rs.getInt("COD_PROCESO"), conn));
				tarea.setSufijoBanca("");
				tarea.setClase(rs.getString("JAV_CLASE"));
				tarea.setNombre(rs.getString("NOMBRE"));
				tarea.setVersion(rs.getString("VERSION"));
				tarea.setAtributosCompletar(tareaDaoImpl.obtenerAtributosCompletar(tarea, conn));
				tarea.setAtributosCompletarValidacionWeb(tareaDaoImpl.obtenerAtributosCompletarValidacionWeb(tarea, conn));
				tarea.setWebNombreConfiguracion(rs.getString("WEB_NOM_CONFIGURACION"));
				tareas.add(tarea);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

	public List<AtributoProceso> obtenerAtributos(Proceso proceso, Connection conn) {
		List<AtributoProceso> atributos = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		ClaseDaoImpl claseDaoImpl = new ClaseDaoImpl();
		try {
			String sql = "SELECT PI.COD_PROCESO as COD_PROCESO, " +
					"PI.COD_ATRIBUTO as COD_ATRIBUTO, " +
					"PI.BPM_FLG_ENTRADA as BPM_FLG_ENTRADA, " +
					"PI.BPM_OBJ_REFERENCIA as BPM_OBJ_REFERENCIA, " +
					"PI.BPM_FLG_PIID as BPM_FLG_PIID, "+
					" PI.WEB_FLG_REFERENCIA as WEB_FLG_REFERENCIA, PI.WEB_FLG_VALIDACION as WEB_FLG_VALIDACION, PI.WEB_MEN_VALIDACION as WEB_MEN_VALIDACION, PI.WEB_VAL_OMISION as WEB_VAL_OMISION, PI.WEB_REQUERIDO as WEB_REQUERIDO, "+ 
					" PI.WEB_NOM_CAT_COMBO as WEB_NOM_CAT_COMBO, PI.SQL_FLG_AUTOGENERADO as SQL_FLG_AUTOGENERADO, PI.SQL_NOM_SECUENCIAL as SQL_NOM_SECUENCIAL, PI.COD_PROYECTO as COD_PROYECTO, ATR.COD_CLASE as COD_CLASE, "+
					" ATR.TIPO as TIPO, ATR.NOMBRE AS NOMBRE, ATR.FLG_LISTA AS FLG_LISTA, ATR.WEB_NOMBRE AS WEB_NOMBRE, ATR.WEB_FORMATO AS WEB_FORMATO " + 
					" FROM BFP_CARTA_FIANZA.PRO_INI_ATRIBUTO PI, BFP_CARTA_FIANZA.ATRIBUTO ATR WHERE COD_PROCESO = ? AND PI.COD_ATRIBUTO = ATR.COD_ATRIBUTO" +
					" ORDER BY PI.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			atributos = new ArrayList<AtributoProceso>();
			AtributoProceso atributo = null;
			while (rs.next()) {
				atributo = new AtributoProceso();
				atributo.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				atributo.setCodigoProceso(rs.getInt("COD_PROCESO"));
				atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
				atributo.setTipo(rs.getString("TIPO"));
				atributo.setNombre(rs.getString("NOMBRE"));
				atributo.setFlgLista(rs.getBoolean("FLG_LISTA"));
				atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
				atributo.setWebFormato(rs.getString("WEB_FORMATO"));
				atributo.setCodigoClase(rs.getInt("COD_CLASE"));
				atributo.setClase(claseDaoImpl.obtenerClase(atributo.getCodigoClase(), conn));
				atributo.setBpmFlgEntrada("1".equals(rs.getString("BPM_FLG_ENTRADA")));
				atributo.setBpmObjetoReferencia(rs.getString("BPM_OBJ_REFERENCIA"));
				atributo.setWebFlgReferencia("1".equals(rs.getString("WEB_FLG_REFERENCIA")));
				atributo.setWebFlgValidacion("1".equals(rs.getString("WEB_FLG_VALIDACION")));
				atributo.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				atributo.setWebValorOmision(rs.getString("WEB_VAL_OMISION"));
				atributo.setWebNombreCatalogoCombo(rs.getString("WEB_NOM_CAT_COMBO"));
				atributo.setSqlFlgAutogenerado("1".equals(rs.getString("SQL_FLG_AUTOGENERADO")));
				atributo.setSqlNombreSecuencial(rs.getString("SQL_NOM_SECUENCIAL"));
				atributo.setCampoSQLProceso(obtenerCampoSQL(atributo.getCodigo(), conn));
				atributo.setBpmFlgPIID(rs.getBoolean("BPM_FLG_PIID"));
				atributos.add(atributo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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
	
	public AtributoProceso obtenerAtributo(int codigoAtributo, Connection conn) {
		AtributoProceso atributo = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		ClaseDaoImpl claseDaoImpl = new ClaseDaoImpl();
		try {
			String sql = "SELECT PI.COD_PROCESO as COD_PROCESO, PI.COD_ATRIBUTO as COD_ATRIBUTO, PI.BPM_FLG_ENTRADA as BPM_FLG_ENTRADA, PI.BPM_OBJ_REFERENCIA as BPM_OBJ_REFERENCIA, PI.BPM_FLG_PIID as BPM_FLG_PIID, "+
					" PI.WEB_FLG_REFERENCIA as WEB_FLG_REFERENCIA, PI.WEB_FLG_VALIDACION as WEB_FLG_VALIDACION, PI.WEB_MEN_VALIDACION as WEB_MEN_VALIDACION, PI.WEB_VAL_OMISION as WEB_VAL_OMISION, PI.WEB_REQUERIDO as WEB_REQUERIDO, "+ 
					" PI.WEB_NOM_CAT_COMBO as WEB_NOM_CAT_COMBO, PI.SQL_FLG_AUTOGENERADO as SQL_FLG_AUTOGENERADO, PI.SQL_NOM_SECUENCIAL as SQL_NOM_SECUENCIAL, PI.COD_PROYECTO as COD_PROYECTO, ATR.COD_CLASE as COD_CLASE, "+
					" ATR.TIPO as TIPO, ATR.NOMBRE AS NOMBRE, ATR.FLG_LISTA AS FLG_LISTA, ATR.WEB_NOMBRE AS WEB_NOMBRE, ATR.WEB_FORMATO AS WEB_FORMATO " + 
					" FROM BFP_CARTA_FIANZA.PRO_INI_ATRIBUTO PI, BFP_CARTA_FIANZA.ATRIBUTO ATR WHERE ATR.COD_ATRIBUTO = ? AND PI.COD_ATRIBUTO = ATR.COD_ATRIBUTO " +
					" ORDER BY PI.COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoAtributo);
			rs = cs.executeQuery();
			
			while (rs.next()) {
				atributo = new AtributoProceso();
				atributo.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				atributo.setCodigoProceso(rs.getInt("COD_PROCESO"));
				atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
				atributo.setTipo(rs.getString("TIPO"));
				atributo.setNombre(rs.getString("NOMBRE"));
				atributo.setFlgLista(rs.getBoolean("FLG_LISTA"));
				atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
				atributo.setWebFormato(rs.getString("WEB_FORMATO"));
				atributo.setCodigoClase(rs.getInt("COD_CLASE"));
				atributo.setClase(claseDaoImpl.obtenerClase(atributo.getCodigoClase(), conn));
				atributo.setBpmFlgEntrada("1".equals(rs.getString("BPM_FLG_ENTRADA")));
				atributo.setBpmObjetoReferencia(rs.getString("BPM_OBJ_REFERENCIA"));
				atributo.setWebFlgReferencia("1".equals(rs.getString("WEB_FLG_REFERENCIA")));
				atributo.setWebFlgValidacion("1".equals(rs.getString("WEB_FLG_VALIDACION")));
				atributo.setWebMensajeValidacion(rs.getString("WEB_MEN_VALIDACION"));
				atributo.setWebValorOmision(rs.getString("WEB_VAL_OMISION"));
				atributo.setWebNombreCatalogoCombo(rs.getString("WEB_NOM_CAT_COMBO"));
				atributo.setSqlFlgAutogenerado("1".equals(rs.getString("SQL_FLG_AUTOGENERADO")));
				atributo.setSqlNombreSecuencial(rs.getString("SQL_NOM_SECUENCIAL"));
				atributo.setBpmFlgPIID(rs.getBoolean("BPM_FLG_PIID"));
				atributo.setCampoSQLProceso(obtenerCampoSQL(atributo.getCodigo(), conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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
		return atributo;
	}

	public List<CampoSQLProceso> obtenerCamposSQL(Proceso proceso, Connection conn) {
		List<CampoSQLProceso> camposSQL = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		TablaDaoImpl tablaDaoImpl = new TablaDaoImpl();
		try {
			String sql = "SELECT T1.COD_ATRIBUTO, T1.COD_TABLA, T1.COD_ATRIBUTO, T1.CAMPO, T1.TIPO, T1.LONGITUD, "
					+ "T1.PRECISION, T1.PK, T1.OBLIGATORIO, T1.FK_CAMPO, T1.FK_TABLA, T1.FK_UNO_MUCHO, T1.FN_BUS_NOMBRE, "
					+ "T1.FN_BUS_CATALOGO, T1.VAL_DEFECTO "
					+ "FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T1, BFP_CARTA_FIANZA.PRO_INI_ATRIBUTO T2 "
					+ "WHERE T1.COD_ATRIBUTO = T2.COD_ATRIBUTO AND T2.COD_PROCESO = ?" +
							" ORDER BY T1.COD_ATRIBUTO ";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			camposSQL = new ArrayList<CampoSQLProceso>();
			CampoSQLProceso campoSQL = null;
			while (rs.next()) {
				campoSQL = new CampoSQLProceso();
				campoSQL.setCodigo(rs.getInt("COD_ATRIBUTO"));
				campoSQL.setCodigoTabla(rs.getInt("COD_TABLA"));
				campoSQL.setTabla(tablaDaoImpl.obtenerTabla(campoSQL.getCodigoTabla(), conn));
				campoSQL.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
				campoSQL.setNombre(rs.getString("CAMPO"));
				campoSQL.setTipo(rs.getString("TIPO"));
				campoSQL.setLongitud(rs.getInt("LONGITUD"));
				campoSQL.setPrecision(rs.getInt("PRECISION"));
				campoSQL.setFlgPK("1".equals(rs.getString("PK")));
				campoSQL.setFlgObligatorio("1".equals(rs
						.getString("OBLIGATORIO")));
				CampoSQL campoFK = new CampoSQL();
				campoFK.setNombre(rs.getString("FK_CAMPO"));
				Tabla tablaFK = new Tabla();
				tablaFK.setNombre(rs.getString("FK_TABLA"));
				campoFK.setTabla(tablaFK);
				campoSQL.setFk(campoFK);

				camposSQL.add(campoSQL);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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
		return camposSQL;
	}

	public List<Tarea> obtenerTareas(Proyecto proyecto, Connection conn) {
		ProyectoDaoImpl proyectoDaoImpl = new ProyectoDaoImpl();
		ProcesoDaoImpl procesoDaoImpl = new ProcesoDaoImpl();
		
		List<Tarea> tareas = null;
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT COD_TAREA, COD_PROCESO, COD_PROYECTO, NOMBRE, VERSION, COD_CON_TRABAJAR, COD_CON_COMPLETAR, "
					+ "SQL_ALEAS, SQL_DATASOURCE, BPM_NOMBRE, JAV_PAQUETE, JAV_CLASE, WEB_ACC_COMPLETAR, WEB_ACC_GRABAR, "
					+ "WEB_ACC_CANCELAR, WEB_ACC_RECHAZAR, WEB_ACC_OBSERVAR, WEB_ACC_SALIR, WEB_ACC_SUBSANAR, WEB_PAR_HIS_COMENTARIO, "
					+ "WEB_PAR_HIS_ACCION, WEB_TIE_ROJO, WEB_TIE_AMARILLO, WEB_FLG_ARC_ADJUNTOS, WEB_FLG_ARC_ADICIONALES "
					+ "FROM BFP_CARTA_FIANZA.TAREA WHERE COD_PROYECTO  = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proyecto.getCodigoProyecto());
			rs = cs.executeQuery();

			tareas = new ArrayList<Tarea>();
			Tarea tarea = null;
			while (rs.next()) {
				tarea = new Tarea();
				tarea.setCodigo(rs.getInt("COD_TAREA"));
				tarea.setProyecto(proyectoDaoImpl.obtenerProyecto(rs.getString("COD_PROYECTO"), conn));
				tarea.setProceso(procesoDaoImpl.obtenerProceso(rs.getInt("COD_PROCESO"), conn));
				tarea.setPaquete(rs.getString("JAV_PAQUETE"));
				tarea.setClase(rs.getString("JAV_CLASE"));
				tarea.setSufijoBanca("");
				tarea.setNombre(rs.getString("NOMBRE"));
				tarea.setVersion(rs.getString("VERSION"));
				//tarea.setTablasTrabajar(tareaDaoImpl.obtenerTablaTareaTrabajar(tarea, conn));
				tareas.add(tarea);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

	public Proceso obtenerProceso(int codigoProceso, Connection conn) {
		Proceso bean = new Proceso();
		CallableStatement cs = null;
		ResultSet rs = null;
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		try {
			String sql = "SELECT COD_PROCESO, COD_PROYECTO, INF_NOMBRE, COD_UNI_NEGOCIO, COD_PRODUCTO, INF_SUFIJO, "
					+ "BPM_PLANTILLA, BPM_ACT_INICIO, JAV_PAQUETE, JAV_CLASE, JAV_ALE_PROCESO, JAV_DATASOURCE, "
					+ "COD_CON_RESUMEN, COD_CON_DETALLE "
					+ "FROM BFP_CARTA_FIANZA.PROCESO WHERE COD_PROCESO = ?";
			cs = conn.prepareCall(sql);
			int i = 1;
			cs.setInt(i++, codigoProceso);
			rs = cs.executeQuery();

			if (rs.next()) {
				bean.setCodigo(rs.getInt("COD_PROCESO"));
				bean.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				bean.setNombre(rs.getString("INF_NOMBRE"));
				bean.setSufijoProducto(rs.getString("COD_PRODUCTO"));
				bean.setSufijoProceso(rs.getString("INF_SUFIJO"));
				bean.setNombrePlantilla(rs.getString("BPM_PLANTILLA"));
				bean.setActividadInicio(rs.getInt("BPM_ACT_INICIO"));
				bean.setPaquete(rs.getString("JAV_PAQUETE"));
				bean.setClase(rs.getString("JAV_CLASE"));
				bean.setAleasSpring(rs.getString("JAV_ALE_PROCESO"));
				bean.setDatasource(rs.getString("JAV_DATASOURCE"));
				bean.setConsultaResumen(consultaDaoImpl.obtenerConsulta(rs.getInt("COD_CON_RESUMEN"), conn));
				bean.setConsultaDetalle(consultaDaoImpl.obtenerConsulta(rs.getInt("COD_CON_DETALLE"), conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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
	
	public Proceso obtenerProcesoResumen(int codigoProceso, Connection conn) {
		Proceso bean = new Proceso();
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT COD_PROCESO, COD_PROYECTO, INF_NOMBRE, COD_UNI_NEGOCIO, COD_PRODUCTO, INF_SUFIJO, "
					+ "BPM_PLANTILLA, BPM_ACT_INICIO, JAV_PAQUETE, JAV_CLASE, JAV_ALE_PROCESO, JAV_DATASOURCE, "
					+ "COD_CON_RESUMEN, COD_CON_DETALLE "
					+ "FROM BFP_CARTA_FIANZA.PROCESO WHERE COD_PROCESO = ?";
			cs = conn.prepareCall(sql);
			int i = 1;
			cs.setInt(i++, codigoProceso);
			rs = cs.executeQuery();

			if (rs.next()) {
				bean.setCodigo(rs.getInt("COD_PROCESO"));
				bean.setCodigoProyecto(rs.getInt("COD_PROYECTO"));
				bean.setNombre(rs.getString("INF_NOMBRE"));
				bean.setSufijoBanca(rs.getString("COD_UNI_NEGOCIO"));
				bean.setSufijoProducto(rs.getString("COD_PRODUCTO"));
				bean.setSufijoProceso(rs.getString("INF_SUFIJO"));
				bean.setNombrePlantilla(rs.getString("BPM_PLANTILLA"));
				bean.setActividadInicio(rs.getInt("BPM_ACT_INICIO"));
				bean.setPaquete(rs.getString("JAV_PAQUETE"));
				bean.setClase(rs.getString("JAV_CLASE"));
				bean.setAleasSpring(rs.getString("JAV_ALE_PROCESO"));
				bean.setDatasource(rs.getString("JAV_DATASOURCE"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

	public List<AtributoProceso> obtenerObjetoBPMEntradaSinReferencia(
			Proceso proceso, Connection conn) {
		return objetosBPMEntrada(proceso, conn, false);
	}

	public List<AtributoProceso> obtenerObjetoBPMEntradaConReferencia(
			Proceso proceso, Connection conn) {
		return objetosBPMEntrada(proceso, conn, true);
	}

	private List<AtributoProceso> objetosBPMEntrada(Proceso proceso, Connection conn,
			boolean referencia) {
		List<AtributoProceso> atributoProcesos = new ArrayList<AtributoProceso>();

		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT T3.NOMBRE AS NOMBRE FROM BFP_CARTA_FIANZA.OBJ_BPM T3 "
					+ " WHERE T3.COD_OBJ_BPM in (SELECT T2.COD_OBJ_BPM "
					+ " FROM BFP_CARTA_FIANZA.PRO_INI_ATRIBUTO T1, "
					+ " BFP_CARTA_FIANZA.ATRIBUTO_DO T2"
					+ " WHERE T1.COD_ATRIBUTO = T2.COD_ATRIBUTO "
					+ "  AND T1.COD_PROCESO  = ? "
					+ "  AND T1.BPM_OBJ_REFERENCIA IS <REFERENCIA> "
					+ " ) ";
			if (referencia) {
				sql = sql.replaceAll("<REFERENCIA>", "NOT NULL");
			} else {
				sql = sql.replaceAll("<REFERENCIA>", "NULL");
			}
			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			AtributoProceso bean = null;
			while (rs.next()) {
				bean = new AtributoProceso();

				bean.setBpmObjeto(rs.getString("NOMBRE"));

				atributoProcesos.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

		return atributoProcesos;
	}

	public List<AtributoProceso> obtenerAtributosObjetoBPM(String objeto,
			Connection conn) {
		List<AtributoProceso> atributoProcesos = new ArrayList<AtributoProceso>();
		ClaseDaoImpl claseDaoImpl = new ClaseDaoImpl();
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT T3.COD_ATRIBUTO, T3.COD_CLASE, T3.NOMBRE, T3.TIPO, T3.FLG_LISTA, T3.WEB_NOMBRE, T3.WEB_FORMATO, T3.INF_NOMBRE, " +
					"T3.INF_DESCRIPCION, T3.INF_AUTOR, T2.NOMBRE AS NOMBRE_BPM, T2.TIPO AS TIPO_BPM " +
					"FROM BFP_CARTA_FIANZA.OBJ_BPM T1, BFP_CARTA_FIANZA.ATRIBUTO_DO T2, BFP_CARTA_FIANZA.ATRIBUTO T3 " +
					"WHERE T1.NOMBRE = ? AND T1.COD_OBJ_BPM = T2.COD_OBJ_BPM AND T2.COD_ATRIBUTO = T3.COD_ATRIBUTO";

			cs = conn.prepareCall(sql);
			cs.setString(1, objeto);
			rs = cs.executeQuery();

			AtributoProceso atributo = null;
			while (rs.next()) {
				atributo = new AtributoProceso();

				atributo.setCodigo(rs.getInt("COD_ATRIBUTO"));
				atributo.setCodigoClase(rs.getInt("COD_CLASE"));
				atributo.setNombre(rs.getString("NOMBRE"));
				atributo.setTipo(rs.getString("TIPO"));
				atributo.setFlgLista("1".equals(rs.getString("FLG_LISTA")));
				atributo.setWebNombre(rs.getString("WEB_NOMBRE"));
				atributo.setWebFormato(rs.getString("WEB_FORMATO"));
				atributo.setInformacionNombre(rs.getString("INF_NOMBRE"));
				atributo.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				atributo.setInformacionAutor(rs.getString("INF_AUTOR"));
				atributo.setBpmNombre(rs.getString("NOMBRE_BPM"));
				atributo.setBpmTipo(rs.getString("TIPO_BPM"));
				atributo.setClase(claseDaoImpl.obtenerClase(atributo.getCodigoClase(), conn));
				atributoProcesos.add(atributo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

		return atributoProcesos;
	}

	public Consulta obtenerConsultaResumen(Proceso proceso, Connection conn) {
		Consulta consulta = new Consulta();

		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT T2.COD_CONSULTA, T2.SQL_ALE_STO_PROCEDURE, T2.JAV_PAQUETE, T2.JAV_INTERFACE, T2.COD_PROYECTO, T3.COD_TABLA FROM BFP_CARTA_FIANZA.PROCESO T1, BFP_CARTA_FIANZA.CONSULTA T2, BFP_CARTA_FIANZA.CON_TABLA T3 WHERE T1.COD_PROCESO = ? AND T1.COD_CON_RESUMEN = T2.COD_CONSULTA AND T2.COD_CONSULTA = T3.COD_CONSULTA AND T3.FK = '0'";

			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			if (rs.next()) {
				consulta.setCodigo(rs.getInt("COD_CONSULTA"));
				consulta.setAleas(rs.getString("SQL_ALE_STO_PROCEDURE"));
				consulta.setPaquete(rs.getString("JAV_PAQUETE"));
				consulta.setInterfase(rs.getString("JAV_INTERFACE"));
				ConsultaDaoImpl consultaDao = new ConsultaDaoImpl();
				consulta.setTablaPadre(consultaDao.obtenerTablaPadre(consulta,
						conn));
				consulta.setTablasFK(consultaDao
						.obtenerTablasFK(consulta, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

		return consulta;
	}

	public Consulta obtenerConsultaDetalle(Proceso proceso, Connection conn) {
		Consulta consulta = new Consulta();
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT T2.COD_CONSULTA, T2.SQL_ALE_STO_PROCEDURE, T2.JAV_PAQUETE, T2.JAV_INTERFACE, T2.COD_PROYECTO, T3.COD_TABLA FROM BFP_CARTA_FIANZA.PROCESO T1, BFP_CARTA_FIANZA.CONSULTA T2, BFP_CARTA_FIANZA.CON_TABLA T3 WHERE T1.COD_PROCESO = ? AND T1.COD_CON_DETALLE = T2.COD_CONSULTA AND T2.COD_CONSULTA = T3.COD_CONSULTA AND T3.FK = '1'";

			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			if (rs.next()) {
				consulta.setCodigo(rs.getInt("COD_CONSULTA"));
				consulta.setAleas(rs.getString("SQL_ALE_STO_PROCEDURE"));
				consulta.setPaquete(rs.getString("JAV_PAQUETE"));
				consulta.setInterfase(rs.getString("JAV_INTERFACE"));
				ConsultaDaoImpl consultaDao = new ConsultaDaoImpl();
				consulta.setTablaPadre(consultaDao.obtenerTablaPadre(consulta,
						conn));
				consulta.setTablasFK(consultaDao
						.obtenerTablasFK(consulta, conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

		return consulta;
	}

	public List<AtributoConsulta> obtenerCampoSQLPKConsultaResumen(
			Proceso proceso, Connection conn) {
		List<AtributoConsulta> consultas = new ArrayList<AtributoConsulta>();
		ClaseDaoImpl claseDaoImpl = new ClaseDaoImpl();
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT T5.COD_ATRIBUTO, T5.COD_CLASE, T5.NOMBRE, T5.TIPO, T5.FLG_LISTA, T5.WEB_NOMBRE, T5.WEB_FORMATO, "
					+ "T5.INF_NOMBRE, T5.INF_DESCRIPCION, T5.INF_AUTOR FROM BFP_CARTA_FIANZA.PROCESO T1, BFP_CARTA_FIANZA.CONSULTA T2, BFP_CARTA_FIANZA.CON_TABLA T3, BFP_CARTA_FIANZA.ATRIBUTO_SQL T4, BFP_CARTA_FIANZA.ATRIBUTO T5 WHERE T1.COD_PROCESO = ? AND T1.COD_CON_RESUMEN = T2.COD_CONSULTA AND T2.COD_CONSULTA = T3.COD_CONSULTA AND T3.COD_TABLA = T4.COD_TABLA AND T4.PK = '1' AND T4.COD_ATRIBUTO = T5.COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			AtributoConsulta bean = null;
			while (rs.next()) {
				bean = new AtributoConsulta();
				bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
				bean.setCodigoClase(rs.getInt("COD_CLASE"));
				bean.setClase(claseDaoImpl.obtenerClase(bean.getCodigoClase(), conn));
				bean.setNombre(rs.getString("NOMBRE"));
				bean.setTipo(rs.getString("TIPO"));
				bean.setFlgLista("1".equals(rs.getString("FLG_LISTA")));
				bean.setWebNombre(rs.getString("WEB_NOMBRE"));
				bean.setWebFormato(rs.getString("WEB_FORMATO"));
				bean.setInformacionNombre(rs.getString("INF_NOMBRE"));
				bean.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				bean.setInformacionAutor(rs.getString("INF_AUTOR"));

				consultas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

		return consultas;
	}

	public List<AtributoConsulta> obtenerCampoSQLPKConsultaDetalle(
			Proceso proceso, Connection conn) {
		List<AtributoConsulta> consultas = new ArrayList<AtributoConsulta>();

		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT T5.COD_ATRIBUTO, T5.COD_CLASE, T5.NOMBRE, T5.TIPO, T5.FLG_LISTA, T5.WEB_NOMBRE, T5.WEB_FORMATO, "
					+ "T5.INF_NOMBRE, T5.INF_DESCRIPCION, T5.INF_AUTOR "
					+ "FROM BFP_CARTA_FIANZA.PROCESO T1, BFP_CARTA_FIANZA.CONSULTA T2, BFP_CARTA_FIANZA.CON_TABLA T3, "
					+ "BFP_CARTA_FIANZA.ATRIBUTO_SQL T4, BFP_CARTA_FIANZA.ATRIBUTO T5 "
					+ "WHERE T1.COD_PROCESO = ? AND T1.COD_CON_DETALLE = T2.COD_CONSULTA AND T2.COD_CONSULTA = T3.COD_CONSULTA "
					+ "AND T3.COD_TABLA = T4.COD_TABLA AND T4.PK = '1' AND T4.COD_ATRIBUTO = T5.COD_ATRIBUTO";
			cs = conn.prepareCall(sql);
			cs.setInt(1, proceso.getCodigo());
			rs = cs.executeQuery();

			AtributoConsulta bean = null;
			while (rs.next()) {
				bean = new AtributoConsulta();
				bean.setCodigo(rs.getInt("COD_ATRIBUTO"));
				bean.setCodigoClase(rs.getInt("COD_CLASE"));
				bean.setNombre(rs.getString("NOMBRE"));
				bean.setTipo(rs.getString("TIPO"));
				bean.setFlgLista("1".equals(rs.getString("FLG_LISTA")));
				bean.setWebNombre(rs.getString("WEB_NOMBRE"));
				bean.setWebFormato(rs.getString("WEB_FORMATO"));
				bean.setInformacionNombre(rs.getString("INF_NOMBRE"));
				bean.setInformacionDescripcion(rs.getString("INF_DESCRIPCION"));
				bean.setInformacionAutor(rs.getString("INF_AUTOR"));

				consultas.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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

		return consultas;
	}
	
	public CampoSQLProceso obtenerCampoSQL(int codigoAtributo, Connection conn) {
		CampoSQLProceso campoSQL = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		TablaDaoImpl tablaDaoImpl = new TablaDaoImpl();
		try {
			String sql = "SELECT T1.COD_ATRIBUTO, T1.COD_TABLA, T1.COD_ATRIBUTO, T1.CAMPO, T1.TIPO, T1.LONGITUD, "
					+ "T1.PRECISION, T1.PK, T1.OBLIGATORIO, T1.FK_CAMPO, T1.FK_TABLA, T1.FK_UNO_MUCHO, T1.FN_BUS_NOMBRE, "
					+ "T1.FN_BUS_CATALOGO, T1.VAL_DEFECTO "
					+ "FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T1 "
					+ "WHERE T1.COD_ATRIBUTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoAtributo);
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campoSQL = new CampoSQLProceso();
				campoSQL.setCodigo(rs.getInt("COD_ATRIBUTO"));
				campoSQL.setCodigoTabla(rs.getInt("COD_TABLA"));
				campoSQL.setTabla(tablaDaoImpl.obtenerTabla(campoSQL.getCodigoTabla(), conn));
				campoSQL.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
				campoSQL.setNombre(rs.getString("CAMPO"));
				campoSQL.setTipo(rs.getString("TIPO"));
				campoSQL.setLongitud(rs.getInt("LONGITUD"));
				campoSQL.setPrecision(rs.getInt("PRECISION"));
				campoSQL.setFlgPK("1".equals(rs.getString("PK")));
				campoSQL.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				campoSQL.setFk(obtenerCampoSQLxObjetoSQL(rs.getInt("FK_CAMPO"), conn));
			
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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
		return campoSQL;
	}
	
	public CampoSQLProceso obtenerCampoSQLxObjetoSQL(int codigoAtributoSQL, Connection conn) {
		CampoSQLProceso campoSQL = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		TablaDaoImpl tablaDaoImpl = new TablaDaoImpl();
		try {
			String sql = "SELECT T1.COD_ATRIBUTO, T1.COD_TABLA, T1.COD_ATRIBUTO, T1.CAMPO, T1.TIPO, T1.LONGITUD, "
					+ "T1.PRECISION, T1.PK, T1.OBLIGATORIO, T1.FK_CAMPO, T1.FK_TABLA, T1.FK_UNO_MUCHO, T1.FN_BUS_NOMBRE, "
					+ "T1.FN_BUS_CATALOGO, T1.VAL_DEFECTO "
					+ "FROM BFP_CARTA_FIANZA.ATRIBUTO_SQL T1 "
					+ "WHERE T1.COD_ATRIBUTO = ?";
			cs = conn.prepareCall(sql);
			cs.setInt(1, codigoAtributoSQL);
			rs = cs.executeQuery();
			
			while (rs.next()) {
				campoSQL = new CampoSQLProceso();
				campoSQL.setCodigo(rs.getInt("COD_ATRIBUTO"));
				campoSQL.setCodigoTabla(rs.getInt("COD_TABLA"));
				campoSQL.setTabla(tablaDaoImpl.obtenerTabla(campoSQL.getCodigoTabla(), conn));
				campoSQL.setCodigoAtributo(rs.getInt("COD_ATRIBUTO"));
				campoSQL.setNombre(rs.getString("CAMPO"));
				campoSQL.setTipo(rs.getString("TIPO"));
				campoSQL.setLongitud(rs.getInt("LONGITUD"));
				campoSQL.setPrecision(rs.getInt("PRECISION"));
				campoSQL.setFlgPK("1".equals(rs.getString("PK")));
				campoSQL.setFlgObligatorio("1".equals(rs.getString("OBLIGATORIO")));
				campoSQL.setFk(obtenerCampoSQLxObjetoSQL(rs.getInt("FK_CAMPO"), conn));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
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
		return campoSQL;
	}
	
}
