package com.carga.portal.servicio.crear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.carga.mapeo.dao.impl.ProcesoDaoImpl;
import com.carga.mapeo.dao.impl.ProyectoDaoImpl;
import com.carga.mapeo.dao.impl.TablaDaoImpl;
import com.carga.portal.modelo.AtributoProceso;
import com.carga.portal.modelo.CampoSQL;
import com.carga.portal.modelo.CampoSQLConsulta;
import com.carga.portal.modelo.CampoSQLProceso;
import com.carga.portal.modelo.CampoSQLTarea;
import com.carga.portal.modelo.Catalogo;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Consulta;
import com.carga.portal.modelo.Perfil;
import com.carga.portal.modelo.PerfilModulo;
import com.carga.portal.modelo.PerfilSubModuloProducto;
import com.carga.portal.modelo.Proceso;
import com.carga.portal.modelo.Producto;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.SubModulo;
import com.carga.portal.modelo.Tabla;
import com.carga.portal.modelo.TablaConsulta;
import com.carga.portal.modelo.Tarea;
import com.carga.portal.modelo.UnidadNegocio;
import com.carga.portal.servicio.crear.util.Cadena;
import com.carga.portal.servicio.crear.util.Validaciones;

public class CrearSQL {

	public static final String SP_CARACTER_CIERRE = "@";

	public void crear(Proyecto proyecto, Configuracion configuracion, Connection connection) throws Exception{
		
		/**************************************************************************************************************/
		/** creamos los directorios base */
		File directorioScriptCreate = new File(configuracion.getDirectorioSQL().getAbsoluteFile()+"\\script\\create");
		if(directorioScriptCreate.exists()==false){
			directorioScriptCreate.mkdirs();
		}
		
		File directorioScriptDrop = new File(configuracion.getDirectorioSQL().getAbsoluteFile()+"\\script\\rollback");
		if(directorioScriptDrop.exists()==false){
			directorioScriptDrop.mkdirs();
		}
		crearTablas(proyecto, directorioScriptCreate, directorioScriptDrop, connection);
		crearStoreProcedure(proyecto, directorioScriptCreate, directorioScriptDrop, connection);
		crearInsert(proyecto, configuracion, directorioScriptCreate, directorioScriptDrop, connection);
		crearInsertCatalogo(proyecto, configuracion, directorioScriptCreate, directorioScriptDrop, connection);
		borrarTablas(proyecto, directorioScriptCreate, directorioScriptDrop, connection);
	}
	
	public static void crearTablas(Proyecto proyecto, File directorioScriptCreate, File directorioScriptDrop, Connection conn) throws Exception{
		
		File archivo = new File(directorioScriptCreate.getAbsolutePath()+"\\","1_CREATE_TABLE.sql");
		if(archivo.exists()){
			archivo.delete();
		}
		archivo.createNewFile();
		
		BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo,true), "ISO-8859-1"));
		bufferedWriter.write("--<ScriptOptions statementTerminator=\"@\"/>\r\n\r\n");
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--CREAR TABLAS\r\n");
		
		List<Tabla> tablas = proyecto.getTablas();
		
		for (int i = 0; i < tablas.size(); i++) {
			Tabla tabla = tablas.get(i);
			if(tabla!=null && tabla.getCamposPK()!=null && tabla.getCamposPK().size()>0){
				bufferedWriter.write(crearTablasDLL(tabla,conn));
			}
		}
		
		bufferedWriter.write("\r\n");
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--CREAR TABLAS PLANTILLAS\r\n");
		bufferedWriter.write(crearTablasDLLPlantillas(tablas.get(0)));
		
		bufferedWriter.close();
		
	}
	
	public static void borrarTablas(Proyecto proyecto, File directorioScriptCreate, File directorioScriptDrop, Connection conn) throws Exception{
		
		List<Tabla> tablas = proyecto.getTablas();
		
		File archivoDrop = new File(directorioScriptDrop.getAbsolutePath()+"\\","3_DROP_TABLE.sql");
		if(archivoDrop.exists()){
			archivoDrop.delete();
		}
		archivoDrop.createNewFile();
		
		BufferedWriter bufferedWriterDrop =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoDrop,true), "ISO-8859-1"));
		bufferedWriterDrop.write("--<ScriptOptions statementTerminator=\"@\"/>\r\n\r\n");
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR TABLAS\r\n");
		for (int i = tablas.size()-1; i > -1; i--) {
			bufferedWriterDrop.write("DROP TABLE "+tablas.get(i).getEsquema()+"."+tablas.get(i).getNombre()+"@\r\n");
		}
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR TABLAS PLANTILLAS\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".MAE_CATALOGO@\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".HIS_TAREA@\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".HIS_CANCELACION@\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".HIS_RECHAZO@\r\n");
		// observacion
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".MAE_OBSERVACION@\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".MAE_DOCUMENTO@\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".MAE_EXCEPCION@\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".HIS_OBSERVACION@\r\n");
		
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".MAE_DOCUMENTO_TAREA@\r\n");
		bufferedWriterDrop.write("DROP TABLE "+tablas.get(0).getEsquema()+".HIS_DOCUMENTO@\r\n");
		
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.close();
	}
	
	public static String crearTablasDLLPlantillas(Tabla tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"MAE_CATALOGO\" (\r\n");
		buffer.append("\t\t\"COD_CATALOGO\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"COD_ATRIBUTO\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"VALOR_1\" VARCHAR(100) NOT NULL,\r\n");
		buffer.append("\t\t\"VALOR_2\" VARCHAR(100),\r\n");
		buffer.append("\t\t\"DESCRIPCION\" VARCHAR(100),\r\n");
		buffer.append("\t\t\"ESTADO\" CHAR(1) NOT NULL,\r\n");
		buffer.append("\t\t\"LIM_COD_ATRIBUTO\" INT NULL,\r\n");
		buffer.append("\t\t\"LIM_VALOR_1\" INT NULL,\r\n");
		buffer.append("\t\t\"LIM_VALOR_2\" INT NULL,\r\n");
		buffer.append("\t\t\"JERARQUIA\" CHAR(1) NOT NULL,\r\n");
		buffer.append("\t\t\"USUARIO_CREACION\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"FECHA_CREACION\" TIMESTAMP NOT NULL,\r\n");
		buffer.append("\t\t\"USUARIO_ACTUALIZACION\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"FECHA_ACTUALIZACION\" TIMESTAMP NOT NULL\r\n");
		buffer.append("\t\t)\r\n");
		buffer.append("\t\tDATA CAPTURE NONE@\r\n");
		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"MAE_CATALOGO\" ADD CONSTRAINT \"PK_MAE_CATALOGO\" PRIMARY KEY (\"COD_CATALOGO\", \"COD_ATRIBUTO\")@\r\n\r\n");
		
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"HIS_TAREA\" (\r\n");
		buffer.append("\t\t\"PIID\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"TKIID\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"COD_TAREA\" VARCHAR(80),\r\n");
		buffer.append("\t\t\"DES_TAREA\" VARCHAR(120),\r\n");
		buffer.append("\t\t\"USU_PROPIETARIO\" VARCHAR(16),\r\n");
		buffer.append("\t\t\"COMENTARIO\" VARCHAR(250),\r\n");
		buffer.append("\t\t\"COD_RESULTADO\" VARCHAR(6),\r\n");
		buffer.append("\t\t\"DES_RESULTADO\" VARCHAR(20),\r\n");
		buffer.append("\t\t\"FEC_CREACION\" TIMESTAMP NOT NULL,\r\n");
		buffer.append("\t\t\"FEC_INICIO\" TIMESTAMP,\r\n");
		buffer.append("\t\t\"FEC_TERMINO\" TIMESTAMP\r\n");
		buffer.append("\t)\r\n");
		buffer.append("\tDATA CAPTURE NONE @\r\n");
		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"HIS_TAREA\" ADD CONSTRAINT \"HIS_TAREA_PK\" PRIMARY KEY (\"PIID\", \"TKIID\")@\r\n\r\n");
		
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"HIS_CANCELACION\" (\r\n");
		buffer.append("\t\t\"PIID\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"COD_TAREA\" VARCHAR(80),\r\n");
		buffer.append("\t\t\"DES_TAREA\" VARCHAR(120),\r\n");
		buffer.append("\t\t\"TIPO\" VARCHAR(5),\r\n");
		buffer.append("\t\t\"COMENTARIO\" VARCHAR(250),\r\n");
		buffer.append("\t\t\"USUARIO\" VARCHAR(20),\r\n");
		buffer.append("\t\t\"USU_NOMBRE\" VARCHAR(100),\r\n");
		buffer.append("\t\t\"FECHA\" TIMESTAMP NOT NULL \r\n");
		buffer.append("\t)\r\n");
		buffer.append("\t@\r\n");
		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"HIS_CANCELACION\" ADD CONSTRAINT \"HIS_CANCELACION_PK\" PRIMARY KEY (\"PIID\")@\r\n\r\n");
		
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"HIS_RECHAZO\" (\r\n");
		buffer.append("\t\t\"PIID\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"COD_TAREA\" VARCHAR(80),\r\n");
		buffer.append("\t\t\"DES_TAREA\" VARCHAR(120),\r\n");
		buffer.append("\t\t\"TIPO\" VARCHAR(5),\r\n");
		buffer.append("\t\t\"COMENTARIO\" VARCHAR(250),\r\n");
		buffer.append("\t\t\"USUARIO\" VARCHAR(20),\r\n");
		buffer.append("\t\t\"USU_NOMBRE\" VARCHAR(100),\r\n");
		buffer.append("\t\t\"FECHA\" TIMESTAMP NOT NULL \r\n");
		buffer.append("\t)\r\n");
		buffer.append("\t@\r\n");
		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"HIS_RECHAZO\" ADD CONSTRAINT \"HIS_RECHAZO_PK\" PRIMARY KEY (\"PIID\")@\r\n\r\n");
		
		// OBSERVACION ---->
		
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"HIS_OBSERVACION\" (\r\n");
		buffer.append("\t\"PIID\" VARCHAR(50) NOT NULL, \r\n");
		buffer.append("\t\"COD_ITERACION\" VARCHAR(20) NOT NULL,\r\n"); 
		buffer.append("\t\"COD_OBSERVACION\" VARCHAR(20) NOT NULL,\r\n"); 
		buffer.append("\t\"COD_TAREA\" VARCHAR(80) NOT NULL, \r\n");
		buffer.append("\t\"NOM_TAREA\" VARCHAR(120) NOT NULL, \r\n");
		buffer.append("\t\"COD_TIP_OBSERVACION\" INTEGER NOT NULL, \r\n");
		buffer.append("\t\"COM_OBSERVACION\" VARCHAR(250), \r\n");
		buffer.append("\t\"USU_OBSERVACION\" VARCHAR(25), \r\n");
		buffer.append("\t\"USU_NOM_OBSERVACION\" VARCHAR(250), \r\n");
		buffer.append("\t\"FEC_OBSERVACION\" TIMESTAMP NOT NULL, \r\n");
		buffer.append("\t\"NOM_OBSERVACION\" VARCHAR(100), \r\n");
		buffer.append("\t\"COD_TIP_EXCEPCION\" INTEGER, \r\n");
		buffer.append("\t\"COM_EXCEPCION\" VARCHAR(250), \r\n");
		buffer.append("\t\"RUT_ARCHIVO\" VARCHAR(250), \r\n");
		buffer.append("\t\"NOM_ARCHIVO\" VARCHAR(50), \r\n");
		buffer.append("\t\"USU_EXCEPCION\" VARCHAR(25), \r\n");
		buffer.append("\t\"USU_NOM_EXCEPCION\" VARCHAR(25), \r\n");
		buffer.append("\t\"FEC_EXCEPCION\" TIMESTAMP, \r\n");
		buffer.append("\t\"COD_TIP_DOCUMENTO\" INTEGER, \r\n");
		buffer.append("\t\"FLG_ESTADO\" VARCHAR(3), \r\n");
		buffer.append("\t\"FLG_CONFIRMADO\" CHAR(1)\r\n");
		buffer.append(")@\r\n");

		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"HIS_OBSERVACION\" ADD CONSTRAINT \"HIS_OBSERVACION_PK\" PRIMARY KEY\r\n");
		buffer.append("\t(\"PIID\", \r\n");
		buffer.append("\t\"COD_ITERACION\",\r\n"); 
		buffer.append("\t\"COD_OBSERVACION\", \r\n");
		buffer.append("\t\"COD_TAREA\")@\r\n");
		

		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"MAE_DOCUMENTO\" ( \r\n");
		buffer.append("\t\"COD_TIP_DOCUMENTO\" INTEGER NOT NULL,  \r\n");
		buffer.append("\t\"NOM_DOCUMENTO\" VARCHAR(100),  \r\n");
		buffer.append("\t\"ESTADO\" CHAR(1),  \r\n");
		buffer.append("\t\t\"FORMATOS\" VARCHAR(50),\r\n");
		buffer.append("\t\"DESCRIPCION\" VARCHAR(250),  \r\n");
		buffer.append("\t\"USUARIO_CREACION\" VARCHAR(25),  \r\n");
		buffer.append("\t\"FECHA_CREACION\" TIMESTAMP NOT NULL,  \r\n");
		buffer.append("\t\"USUARIO_ACTUALIZACION\" VARCHAR(100),  \r\n");
		buffer.append("\t\"FECHA_ACTUALIZACION\" TIMESTAMP \r\n");
		buffer.append(")@\r\n");

		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"MAE_EXCEPCION\" ( \r\n");
		buffer.append("\t\"COD_TIP_EXCEPCION\" INTEGER NOT NULL,  \r\n");
		buffer.append("\t\"COD_TIP_OBSERVACION\" INTEGER NOT NULL,  \r\n");
		buffer.append("\t\"COD_TIP_DOCUMENTO\" INTEGER NOT NULL,  \r\n");
		buffer.append("\t\"NOM_EXCEPCION\" VARCHAR(100),  \r\n");
		buffer.append("\t\"ESTADO\" CHAR(1),  \r\n");
		buffer.append("\t\"DESCRIPCION\" VARCHAR(250),  \r\n");
		buffer.append("\t\"FLG_DIG_SUSTENTO\" CHAR(1),  \r\n");
		buffer.append("\t\"USUARIO_CREACION\" VARCHAR(20),  \r\n");
		buffer.append("\t\"FECHA_CREACION\" TIMESTAMP NOT NULL,  \r\n");
		buffer.append("\t\"USUARIO_ACTUALIZACION\" VARCHAR(20),  \r\n");
		buffer.append("\t\"FECHA_ACTUALIZACION\" TIMESTAMP \r\n");
		buffer.append(")@\r\n");

		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"MAE_OBSERVACION\" ( \r\n");
		buffer.append("\t\"COD_TIP_OBSERVACION\" INTEGER NOT NULL,  \r\n");
		buffer.append("\t\"COD_TAREA\" VARCHAR(80),  \r\n");
		buffer.append("\t\"NOM_OBSERVACION\" VARCHAR(100),  \r\n");
		buffer.append("\t\"ESTADO\" CHAR(1),  \r\n");
		buffer.append("\t\"DESCRIPCION\" VARCHAR(250),  \r\n");
		buffer.append("\t\"USUARIO_CREACION\" VARCHAR(20),  \r\n");
		buffer.append("\t\"FECHA_CREACION\" TIMESTAMP NOT NULL,  \r\n");
		buffer.append("\t\"USUARIO_ACTUALIZACION\" VARCHAR(20),  \r\n");
		buffer.append("\t\"FECHA_ACTUALIZACION\" TIMESTAMP \r\n");
		buffer.append(")@\r\n");

		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"MAE_DOCUMENTO\" ADD CONSTRAINT \"MAE_DOCUMENTO_PK\" PRIMARY KEY \r\n");
		buffer.append("	(\"COD_TIP_DOCUMENTO\")@\r\n");

		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"MAE_EXCEPCION\" ADD CONSTRAINT \"MAE_EXCEPCION_PK\" PRIMARY KEY \r\n");
		buffer.append("	(\"COD_TIP_EXCEPCION\",  \r\n");
		buffer.append("	 \"COD_TIP_OBSERVACION\")@\r\n");

		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"MAE_OBSERVACION\" ADD CONSTRAINT \"MAE_OBSERVACION_PK\" PRIMARY KEY \r\n");
		buffer.append("	(\"COD_TIP_OBSERVACION\")@\r\n");
		
		// OBSERVACION ----<
		
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"MAE_DOCUMENTO_TAREA\" (\r\n");
		buffer.append("\t\t\"COD_DOCUMENTO\" INT NOT NULL,\r\n");
		buffer.append("\t\t\"COD_TAREA\" VARCHAR(80) NOT NULL,\r\n");
		buffer.append("\t\t\"COD_TIP_DOCUMENTO\" INTEGER NOT NULL,\r\n");
		buffer.append("\t\t\"OBLIGATORIO\" CHAR(1),\r\n");
		buffer.append("\t\t\"FLG_ADICIONAL\" CHAR(1) NOT NULL DEFAULT '0',\r\n");
		//buffer.append("\t\t\"FLG_VISIBLE_DEFECTO\" CHAR(1) NOT NULL DEFAULT '1',\r\n");
		buffer.append("\t\t\"ESTADO\" CHAR(1),\r\n");
		buffer.append("\t\t\"USUARIO_CREACION\" VARCHAR(20) NOT NULL,\r\n");
		buffer.append("\t\t\"FECHA_CREACION\" TIMESTAMP NOT NULL, \r\n");
		buffer.append("\t\t\"USUARIO_ACTUALIZACION\" VARCHAR(20),\r\n");
		buffer.append("\t\t\"FECHA_ACTUALIZACION\" TIMESTAMP \r\n");
		buffer.append("\t)\r\n");
		buffer.append("\t@\r\n");
		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"MAE_DOCUMENTO_TAREA\" ADD CONSTRAINT \"DOCUMENTO_PK\" PRIMARY KEY (\"COD_DOCUMENTO\")@\r\n\r\n");
		
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\"HIS_DOCUMENTO\" (\r\n");
		buffer.append("\t\t\"COD_HIS_DOCUMENTO\" BIGINT NOT NULL,\r\n");
		buffer.append("\t\t\"COD_DOCUMENTO\" INT NOT NULL,\r\n");
		buffer.append("\t\t\"PIID\" VARCHAR(50) NOT NULL,\r\n");
		buffer.append("\t\t\"COD_TAREA\" VARCHAR(80) NOT NULL,\r\n");
		buffer.append("\t\t\"DES_TAREA\" VARCHAR(120),\r\n");
		buffer.append("\t\t\"COD_TIPO_DOCUMENTO\" INT NOT NULL,\r\n");
		buffer.append("\t\t\"RUT_DOCUMENTO\" VARCHAR(250),\r\n");
		buffer.append("\t\t\"NOM_DOCUMENTO\" VARCHAR(50),\r\n");
		buffer.append("\t\t\"COMENTARIO\" VARCHAR(250),\r\n");
		buffer.append("\t\t\"USUARIO\" VARCHAR(20),\r\n");
		buffer.append("\t\t\"USU_NOMBRE\" VARCHAR(100),\r\n");
		buffer.append("\t\t\"FLG_ADICIONAL\" CHAR(1) NOT NULL DEFAULT '0',\r\n");
		buffer.append("\t\t\"SOL_LECTURA\" CHAR(1),\r\n");
		buffer.append("\t\t\"FECHA\" TIMESTAMP NOT NULL \r\n");
		buffer.append("\t)\r\n");
		buffer.append("\t@\r\n");
		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\"HIS_DOCUMENTO\" ADD CONSTRAINT \"HIS_DOCUMENTO_PK\" PRIMARY KEY (\"COD_HIS_DOCUMENTO\")@\r\n\r\n");
		
		return buffer.toString();
	}

	
	public static String crearTablasDLL(Tabla tabla, Connection conn){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("CREATE TABLE \""+tabla.getEsquema()+"\".\""+tabla.getNombre()+"\"(\r\n");
		for (int i = 0; i < tabla.getCamposSQL().size(); i++) {
			CampoSQL campoSQL = tabla.getCamposSQL().get(i);
			
			if(campoSQL.isTieneFuncion()==false){
				if(i!=0){
					buffer.append(",\r\n");
				}else{
					buffer.append("\r\n");
				}
				
				if(campoSQL.getTipo().equalsIgnoreCase("VARCHAR") || campoSQL.getTipo().equalsIgnoreCase("CHAR") || campoSQL.getTipo().equalsIgnoreCase("CHARACTER")){
					buffer.append("\t\t\""+campoSQL.getNombre()+"\" "+campoSQL.getTipo()+"("+campoSQL.getLongitud()+")");
				}else if(campoSQL.getTipo().equalsIgnoreCase("DECIMAL")){
					buffer.append("\t\t\""+campoSQL.getNombre()+"\" "+campoSQL.getTipo()+"("+campoSQL.getLongitud()+", "+campoSQL.getPrecision()+")");
				}else{
					buffer.append("\t\t\""+campoSQL.getNombre()+"\" "+campoSQL.getTipo());
				}
				if(campoSQL.isFlgObligatorio() || campoSQL.isFlgPK()){
					buffer.append(" NOT NULL");
				}
				
			}
		}
		buffer.append(")@\r\n\r\n");
		
		buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\""+tabla.getNombre()+"\" ADD CONSTRAINT \"PK_BFP_"+tabla.getNombre()+"\" PRIMARY KEY (");
		
		List<CampoSQL> camposPK = tabla.getCamposPK();
		for (int i = 0; i < camposPK.size(); i++) {
			CampoSQL campoSQL = camposPK.get(i);
			if(i==0){
				buffer.append("\""+campoSQL.getNombre()+"\" ");
			}else{
				buffer.append(", \""+campoSQL.getNombre()+"\" ");
			}
		}
		
		
		buffer.append(")@\r\n\r\n");
		
		List<CampoSQL> camposFK = tabla.getCamposFK();
		if(camposFK!=null && camposFK.size()>0){
			Map<Integer, Tabla> tablasFK = new HashMap<Integer, Tabla>();
			List<Integer> nombreTablas = new ArrayList<Integer>();
			for (int i = 0; i < camposFK.size(); i++) {
				CampoSQL campoFK = camposFK.get(i);
				if(tablasFK.containsKey(campoFK.getFk().getTabla().getCodigo())==false){
					tablasFK.put(campoFK.getFk().getTabla().getCodigo(), campoFK.getFk().getTabla());
					nombreTablas.add(campoFK.getFk().getTabla().getCodigo());
				}
			}
			
			
			for (int i = 0; i < nombreTablas.size(); i++) {
				Tabla tablaFK = tablasFK.get(nombreTablas.get(i));
				buffer.append("ALTER TABLE \""+tabla.getEsquema()+"\".\""+tabla.getNombre()+"\" ADD CONSTRAINT \"FK_BFP_"+tabla.getEsquema()+"_"+tabla.getNombre()+"\" FOREIGN KEY ");
				StringBuffer camposA = new StringBuffer();
				StringBuffer camposB = new StringBuffer();
				for (int j = 0; j < camposFK.size(); j++) {
					CampoSQL campoSQLFK = camposFK.get(j);
					if(tablaFK.getCodigo()==campoSQLFK.getFk().getTabla().getCodigo()){
						if(j==0){
							camposA.append(campoSQLFK.getNombre());
							camposB.append(campoSQLFK.getFk().getNombre());
						}else{
							camposA.append(","+campoSQLFK.getNombre());
							camposB.append(","+campoSQLFK.getFk().getNombre());
						}
					}
				}
				
				buffer.append("(\""+camposA.toString()+"\") REFERENCES \""+tablaFK.getEsquema()+"\".\""+tablaFK.getNombre()+"\" (\""+camposB.toString()+"\")@\r\n\r\n");
			}
		}
		
		return buffer.toString();
	}
	
	public static void crearStoreProcedure(Proyecto proyecto, File directorioScriptCreate, File directorioScriptDrop, Connection conn) throws Exception{
		Tabla tabla = proyecto.getTablas().get(0);
		
		/**********************************************************************************************************************************/
		/*** CREATE ***/
		File fileCreate = new File(directorioScriptCreate.getAbsolutePath()+"\\","2_STORE_PROCEDURE.sql");
		if(fileCreate.exists()){
			fileCreate.delete();
		}
		fileCreate.createNewFile();
		
		BufferedWriter bwCreate =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileCreate,true), "ISO-8859-1"));
		bwCreate.write("--<ScriptOptions statementTerminator=\"@\"/>");
		bwCreate.write(spConsultasCreate(proyecto, conn));
		bwCreate.write(spProcesosCreate(proyecto, tabla, conn));
		bwCreate.write(spTareasCreate(proyecto, conn));
		bwCreate.write(spConsultasTareaCreate(proyecto, conn));
		bwCreate.close();
		/**********************************************************************************************************************************/
		
		/**********************************************************************************************************************************/
		/*** ROLLBACK ***/
		File fileDrop = new File(directorioScriptDrop.getAbsolutePath()+"\\","2_DROP_STORE_PROCEDURE.sql");
		if(fileDrop.exists()){
			fileDrop.delete();
		}
		fileDrop.createNewFile();
		BufferedWriter bwDrop =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDrop,true), "ISO-8859-1"));
		bwDrop.write("--<ScriptOptions statementTerminator=\"@\"/>\r\n\r\n");
		
		bwDrop.write(spConsultasContenidoArchivoDLL_plantillaDrop(proyecto, conn));
		bwDrop.write("\r\n");
		bwDrop.write("--------------------------------------------------------------------------------\r\n");
		bwDrop.write("--ELIMINAR STORE PROCEDURE DE LAS CONSULTAS\r\n");
		bwDrop.write(spConsultasRollback(proyecto, conn));
		
		bwDrop.write("\r\n");
		bwDrop.write("--------------------------------------------------------------------------------\r\n");
		bwDrop.write("--ELIMINAR STORE PROCEDURE DE LOS PROCESOS\r\n");
		bwDrop.write(spProcesosRollback(proyecto, tabla, conn));
		
		bwDrop.write("\r\n");
		bwDrop.write("--------------------------------------------------------------------------------\r\n");
		bwDrop.write("--ELIMINAR STORE PROCEDURE DE LAS TAREAS\r\n");
		bwDrop.write(spTareasRollback(proyecto, conn));
		bwDrop.write("\r\n");
		
		bwDrop.close();
		/**********************************************************************************************************************************/
	}
	
	
	public static String spConsultasCreate(Proyecto proyecto, Connection conn) throws Exception{
		List<Tabla> tablas = proyecto.getTablas();
		StringBuffer sb = new StringBuffer();
		sb.append(spConsultasContenidoArchivoDLL_plantilla(tablas.get(0)));
		
		List<Consulta> consultas = proyecto.getConsultas();
		
		for (int x= 0; x< consultas.size(); x++) {
			Consulta consulta = consultas.get(x);
			
			if(consulta.getTablaPadre()==null){
				continue;
			}
			
			boolean uno = false;
			boolean muchos = false;
			boolean mixto = false;
			
			TablaConsulta tablaPadre = consulta.getTablaPadre();
			List<TablaConsulta> tablasFK = consulta.getTablasFK();
			
			for(int y=0; tablasFK!=null && y<tablasFK.size(); y++){
				if(tablasFK.get(y).isFlgUnoMuchos()){
					muchos = true;
				}else{
					uno = true;
				}
			}
			
			if(uno && muchos){
				mixto = true;
			}
			
			sb.append("\r\n\r\n");
			if(tablasFK==null || tablasFK.size()==0){
				sb.append(spConsultasContenidoArchivoDLL_uno(consulta, tablaPadre));
			}else{
				if(mixto){
					for(int u=0; u<tablasFK.size() ;u++){
						if(tablasFK.get(u).isFlgUnoMuchos()){
							sb.append(spConsultasContenidoArchivoDLL_multiple(consulta, tablaPadre, tablasFK.get(u))+"\r\n\r\n\r\n");
						}
					}
					sb.append(spConsultasContenidoArchivoDLL_uno(consulta, tablaPadre));
				}else if(uno){
					sb.append(spConsultasContenidoArchivoDLL_uno(consulta, tablaPadre));
				}else if(muchos){
					for(int u=0; u<tablasFK.size() ;u++){
						if(tablasFK.get(u).isFlgUnoMuchos()){
							sb.append(spConsultasContenidoArchivoDLL_multiple(consulta, tablaPadre, tablasFK.get(u))+"\r\n\r\n\r\n");
						}
					}
					sb.append(spConsultasContenidoArchivoDLL_uno(consulta, tablaPadre));
				}
			}
		}
		return sb.toString();
	}
	
	public static String spConsultasRollback(Proyecto proyecto, Connection conn) throws Exception{

		StringBuffer sb = new StringBuffer();
		
		List<Consulta> consultas = proyecto.getConsultas();
		
		for (int x= 0; x< consultas.size(); x++) {
			Consulta consulta = consultas.get(x);
			Tabla tablaPadre = consulta.getTablaPadre();
			List<TablaConsulta> tablasFK = consulta.getTablasFK();
			
			
			boolean uno = false;
			boolean muchos = false;
			boolean mixto = false;
			for(int y=0; y<tablasFK.size(); y++){
				if(tablasFK.get(y).isFlgUnoMuchos()){
					muchos = true;
				}else{
					uno = true;
				}
			}
			if(uno && muchos){
				mixto = true;
			}
			if(tablasFK.size()==0){
				sb.append(spConsultasContenidoArchivoDLL_simpleDrop(consulta, tablaPadre));
			}else{
				if(mixto){
					for(int u=0; u<tablasFK.size() ;u++){
						if(tablasFK.get(u).isFlgUnoMuchos()){
							sb.append(spConsultasContenidoArchivoDLL_multipleDrop(consulta, tablaPadre, tablasFK.get(u)));
						}
					}
					sb.append(spConsultasContenidoArchivoDLL_unoDrop(consulta, tablaPadre));
				}else if(uno){
					sb.append(spConsultasContenidoArchivoDLL_unoDrop(consulta, tablaPadre));
				}else if(muchos){
					for(int u=0; u<tablasFK.size() ;u++){
						if(tablasFK.get(u).isFlgUnoMuchos()){
							sb.append(spConsultasContenidoArchivoDLL_multipleDrop(consulta, tablaPadre, tablasFK.get(u)));
						}
					}
					sb.append(spConsultasContenidoArchivoDLL_simpleDrop(consulta, tablaPadre));
				}
			}
		}
		return sb.toString();
	}
	
	public static String spProcesosCreate(Proyecto proyecto, Tabla tabla, Connection conn) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		List<Proceso> procesos = proyecto.getProcesos();
		if (procesos != null){
			for(int x=0;x<procesos.size(); x++){
				Proceso proceso = procesos.get(x);
				sb.append("\r\n\r\n");
				List<AtributoProceso> atributoProceso = proceso.getAtributosEntrada();
				if(atributoProceso!=null){
					for (int y = 0; y < atributoProceso.size(); y++) {
						if(atributoProceso.get(y).isSqlFlgAutogenerado()){		
							sb.append(spProcesosContenidoArchivoSec(proceso, atributoProceso.get(y)));
						}
					}
				}
				sb.append(spProcesosContenidoSecuencialDocumento(proyecto, proceso));
				sb.append(spProcesosContenidoArchivoSP(proceso, tabla));
			}
		}
		
		return sb.toString();
	}
	
	public static String spProcesosRollback(Proyecto proyecto, Tabla tabla, Connection conn) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		List<Proceso> procesos = proyecto.getProcesos();
		if (procesos != null){
			for(int x=0;x<procesos.size(); x++){
				Proceso proceso = procesos.get(x);
				for (int y = 0; y < proceso.getAtributosEntrada().size(); y++) {
					if(proceso.getAtributosEntrada().get(y).isSqlFlgAutogenerado()){
						sb.append(spProcesosContenidoArchivoSecDrop(proceso, tabla));
					}
				}
				
				sb.append(spProcesosContenidoArchivoSPDrop(proceso, tabla));
			}
		}
		
		return sb.toString();
	}
	
	public static String spTareasCreate(Proyecto proyecto, Connection conn) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		List<Tarea> tareas = proyecto.getTareas();
		for(int x=0;x<tareas.size(); x++){
			Tarea tarea = tareas.get(x);
			
			if(tarea.getAtributosCompletar()!=null && tarea.getAtributosCompletar().size()>0){
				sb.append("\r\n");
				sb.append(spTareasContenidoArchivoDLLCompletar(tarea));
				sb.append("\r\n");
			}
			if(tarea.getAtributosTrabajar()!=null 
					&& tarea.getAtributosTrabajar().size()>0){
				sb.append(spTareasContenidoArchivoDLLTrabajar(tarea, conn));
				sb.append("\r\n");
			}
			if(tarea.isFlujoCancelar()){
				if(tarea.getAtributosCancelar()!=null && tarea.getAtributosCancelar().size()>0){
					sb.append(spTareasContenidoArchivoDLLCancelar(tarea));
					sb.append("\r\n");
				}
			}
			if(tarea.isFlujoRechazar()){
				if(tarea.getAtributosRechazar()!=null && tarea.getAtributosRechazar().size()>0){
					sb.append(spTareasContenidoArchivoDLLRechazar(tarea));
					sb.append("\r\n");
				}
			}
			if(tarea.isFlujoObservar()){
				if(tarea.getAtributosObservar()!=null && tarea.getAtributosObservar().size()>0){
					sb.append(spTareasContenidoArchivoDLLObservar(proyecto, tarea));
				}
			}
			
		}
		
		return sb.toString();
	}
	
	public static String spTareasRollback(Proyecto proyecto, Connection conn) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		List<Tarea> tareas = proyecto.getTareas();
		for(int x=0;x<tareas.size(); x++){
			Tarea tarea = tareas.get(x);
			if(tarea.getAtributosTrabajar()!=null && tarea.getAtributosTrabajar().size()>0){	
				sb.append(spTareasContenidoArchivoDLLTrabajarDrop(tarea,conn));
			}
			if(tarea.getAtributosCompletar()!=null && tarea.getAtributosCompletar().size()>0){
				sb.append(spTareasContenidoArchivoDLLCompletarDrop(tarea));
			}
			if(tarea.isFlujoCancelar()){
				sb.append(spTareasContenidoArchivoDLLCancelarDrop(tarea));
			}
			if(tarea.isFlujoRechazar()){
				sb.append(spTareasContenidoArchivoDLLRechazarDrop(tarea));
			}
			if(tarea.isFlujoObservar()){
				sb.append(spTareasContenidoArchivoDLLObservarDrop(tarea));
			}
			
		}
		
		return sb.toString();
	}
	
	private static String spConsultasContenidoArchivoDLL_plantilla(Tabla tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\r\n\r\n");
		buffer.append("CREATE OR REPLACE FUNCTION "+tabla.getEsquema()+".FN_PORTAL_CATALOGO_CON_VALOR_1(\r\n");
		buffer.append("\t\t\tIN IN_COD_CATALOGO VARCHAR(50),\r\n");
		buffer.append("\t\t\tIN IN_COD_ATRIBUTO VARCHAR(50))\r\n");
		buffer.append("\t\t\tRETURNS VARCHAR(100)\r\n");
		buffer.append("\t\t\tNO EXTERNAL ACTION\r\n");
		buffer.append("\t\t\tSPECIFIC \""+tabla.getEsquema()+"\".\"FN_PORTAL_CATALOGO_CON_VALOR_1\"\r\n");
		buffer.append("F1: BEGIN ATOMIC\r\n");
		buffer.append("\t\t\tRETURN (SELECT VALOR_1 FROM "+tabla.getEsquema()+".MAE_CATALOGO WHERE COD_CATALOGO = IN_COD_CATALOGO AND COD_ATRIBUTO = IN_COD_ATRIBUTO);\r\n");
		buffer.append("END@\r\n\r\n");
		
		buffer.append("CREATE OR REPLACE FUNCTION "+tabla.getEsquema()+".FN_PORTAL_CATALOGO_CON_VALOR_2 (\r\n");
		buffer.append("\t\t\tIN IN_COD_CATALOGO VARCHAR(50),\r\n");
		buffer.append("\t\t\tIN IN_COD_ATRIBUTO VARCHAR(50))\r\n");
		buffer.append("\t\t\tRETURNS VARCHAR(100)\r\n");
		buffer.append("\t\t\tNO EXTERNAL ACTION\r\n");
		buffer.append("\t\t\tSPECIFIC \""+tabla.getEsquema()+"\".\"FN_PORTAL_CATALOGO_CON_VALOR_2\"\r\n");
		buffer.append("F1: BEGIN ATOMIC\r\n");
		buffer.append("\t\t\tRETURN (SELECT VALOR_2 FROM "+tabla.getEsquema()+".MAE_CATALOGO WHERE COD_CATALOGO = IN_COD_CATALOGO AND COD_ATRIBUTO = IN_COD_ATRIBUTO);\r\n");
		buffer.append("END@\r\n\r\n");
		
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_TAREA_REGISTRO(\r\n");
		buffer.append("\t\t\t\tIN PI_PIID VARCHAR(50),\r\n");
		buffer.append("\t\t\t\tIN PI_TKIID VARCHAR(50),\r\n");
		buffer.append("\t\t\t\tIN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("\t\t\t\tIN PI_DES_TAREA VARCHAR(120),\r\n");
		buffer.append("\t\t\t\tIN PI_USU_PROPIETARIO VARCHAR(16),\r\n");
		buffer.append("\t\t\t\tIN PI_COMENTARIO VARCHAR(250),\r\n");
		buffer.append("\t\t\t\tIN PI_COD_RESULTADO VARCHAR(6),\r\n");
		buffer.append("\t\t\t\tIN PI_DES_RESULTADO VARCHAR(20),\r\n");
		buffer.append("\t\t\t\tIN PI_FEC_CREACION TIMESTAMP,\r\n");
		buffer.append("\t\t\t\tIN PI_FEC_INICIO TIMESTAMP,\r\n");
		buffer.append("\t\t\t\tIN PI_FEC_TERMINO TIMESTAMP\r\n");
		buffer.append("\t)\r\n");
		buffer.append("\t\tSPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_TAREA_REGISTRO\r\n");
		buffer.append("\tBEGIN\r\n"); 
		buffer.append("\t\tINSERT INTO "+tabla.getEsquema()+".HIS_TAREA(\r\n");
		buffer.append("\t\t\t\tPIID, \r\n");
		buffer.append("\t\t\t\tTKIID, \r\n");
		buffer.append("\t\t\t\tCOD_TAREA, \r\n");
		buffer.append("\t\t\t\tDES_TAREA, \r\n");
		buffer.append("\t\t\t\tUSU_PROPIETARIO, \r\n");
		buffer.append("\t\t\t\tCOMENTARIO, \r\n");
		buffer.append("\t\t\t\tCOD_RESULTADO, \r\n");
		buffer.append("\t\t\t\tDES_RESULTADO, \r\n");
		buffer.append("\t\t\t\tFEC_CREACION, \r\n");
		buffer.append("\t\t\t\tFEC_INICIO,  \r\n");
		buffer.append("\t\t\t\tFEC_TERMINO) \r\n");
		buffer.append("\t\tVALUES (\r\n");
		buffer.append("\t\t\t\tPI_PIID,\r\n"); 
		buffer.append("\t\t\t\tPI_TKIID, \r\n");
		buffer.append("\t\t\t\tPI_COD_TAREA, \r\n");
		buffer.append("\t\t\t\t\t\tPI_DES_TAREA, \r\n");
		buffer.append("\t\t\t\t\t\tPI_USU_PROPIETARIO, \r\n");
		buffer.append("\t\t\t\tPI_COMENTARIO, \r\n");
		buffer.append("\t\t\t\tPI_COD_RESULTADO, \r\n");
		buffer.append("\t\t\t\tPI_DES_RESULTADO, \r\n");
		buffer.append("\t\t\t\tPI_FEC_CREACION, \r\n");
		buffer.append("\t\t\t\tPI_FEC_INICIO,  \r\n");
		buffer.append("\t\t\t\tPI_FEC_TERMINO); \r\n");
		buffer.append("\t\tEND@\r\n\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_CATALOGO_CON_LISTA(\r\n");
		buffer.append("IN    PI_COD_CATALOGO             VARCHAR(1000),\r\n");
		buffer.append("OUT   PO_CODIGO_RESPUESTA         INTEGER,\r\n");
		buffer.append("OUT   PO_MENSAJE_RESPUESTA        VARCHAR(200)\r\n");
		buffer.append(")SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_CATALOGO_CON_LISTA\r\n");
		buffer.append("RESULT SETS 1\r\n");
		buffer.append("LANGUAGE SQL\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("DECLARE SQLCODE INT DEFAULT 0;\r\n");
		buffer.append("DECLARE V_SQL VARCHAR(8000);\r\n");
		buffer.append("DECLARE c1 CURSOR WITH RETURN FOR V_DINAMICO;\r\n");
		buffer.append("DECLARE CONTINUE HANDLER FOR SQLEXCEPTION \r\n");
		buffer.append("BEGIN \r\n");
		buffer.append("SET PO_CODIGO_RESPUESTA = 0;\r\n"); 
		buffer.append("SET PO_MENSAJE_RESPUESTA = 'ERROR EN \""+tabla.getEsquema()+".SP_PORTAL_CATALOGO_CON_LISTA\": '||  CAST(SQLCODE AS CHAR(10))   ;\r\n"); 
		buffer.append("END;\r\n");
		buffer.append("SET PO_CODIGO_RESPUESTA = 1;\r\n");
		buffer.append("SET V_SQL = 'SELECT  COD_CATALOGO,\r\n"); 
		buffer.append("COD_ATRIBUTO,\r\n");
		buffer.append("VALOR_1,\r\n");
		buffer.append("VALOR_2\r\n");
		buffer.append("					   FROM "+tabla.getEsquema()+".MAE_CATALOGO\r\n"); 
		buffer.append("WHERE COD_ATRIBUTO!='||'''0000'''|| ' AND ESTADO = '||'''1'''||' AND JERARQUIA = '||'''0'''||' AND COD_CATALOGO IN ('||PI_COD_CATALOGO||')';\r\n");
		buffer.append("PREPARE V_DINAMICO FROM V_SQL;\r\n");
		buffer.append("OPEN c1;\r\n");
		buffer.append("END@\r\n\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_CANCELACION_REGISTRO(\r\n");
		buffer.append("\t\t\t\tIN PI_PIID VARCHAR(50),\r\n");
		buffer.append("\t\t\t\tIN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("\t\t\t\tIN PI_DES_TAREA VARCHAR(120),\r\n");
		buffer.append("\t\t\t\tIN PI_TIPO VARCHAR(5),\r\n");
		buffer.append("\t\t\t\tIN PI_COMENTARIO VARCHAR(250),\r\n");
		buffer.append("\t\t\t\tIN PI_USUARIO VARCHAR(20),\r\n");
		buffer.append("\t\t\t\tIN PI_USU_NOMBRE VARCHAR(100),\r\n");
		buffer.append("\t\t\t\tIN PI_FECHA TIMESTAMP \r\n");
		buffer.append("\t)\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_CANCELACION_REGISTRO \r\n");
		buffer.append("\tBEGIN\r\n"); 
		buffer.append("\t\tINSERT INTO "+tabla.getEsquema()+".HIS_CANCELACION(\r\n");
		buffer.append("\t\t\t\tPIID, \r\n");
		buffer.append("\t\t\t\tCOD_TAREA, \r\n");
		buffer.append("\t\t\t\tDES_TAREA, \r\n");
		buffer.append("\t\t\t\tFECHA, \r\n");
		buffer.append("\t\t\t\tUSUARIO, \r\n");
		buffer.append("\t\t\t\tUSU_NOMBRE, \r\n");
		buffer.append("\t\t\t\tTIPO, \r\n");
		buffer.append("\t\t\t\tCOMENTARIO) \r\n");
		buffer.append("\t\tVALUES (\r\n");
		buffer.append("\t\t\t\tPI_PIID,\r\n"); 
		buffer.append("\t\t\t\tPI_COD_TAREA, \r\n");
		buffer.append("\t\t\t\tPI_DES_TAREA, \r\n");
		buffer.append("\t\t\t\tPI_FECHA, \r\n");
		buffer.append("\t\t\t\tPI_USUARIO, \r\n");
		buffer.append("\t\t\t\tPI_USU_NOMBRE, \r\n");
		buffer.append("\t\t\t\tPI_TIPO, \r\n");
		buffer.append("\t\t\t\tPI_COMENTARIO) ;\r\n");
		buffer.append("\t\tEND@\r\n\r\n");
		
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_RECHAZO_REGISTRO(\r\n");
		buffer.append("\t\t\t\tIN PI_PIID VARCHAR(50),\r\n");
		buffer.append("\t\t\t\tIN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("\t\t\t\tIN PI_DES_TAREA VARCHAR(120),\r\n");
		buffer.append("\t\t\t\tIN PI_TIPO VARCHAR(5),\r\n");
		buffer.append("\t\t\t\tIN PI_COMENTARIO VARCHAR(250),\r\n");
		buffer.append("\t\t\t\tIN PI_USUARIO VARCHAR(20),\r\n");
		buffer.append("\t\t\t\tIN PI_USU_NOMBRE VARCHAR(100),\r\n");
		buffer.append("\t\t\t\tIN PI_FECHA TIMESTAMP \r\n");
		buffer.append("\t)\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_RECHAZO_REGISTRO \r\n");
		buffer.append("\tBEGIN\r\n"); 
		buffer.append("\t\tINSERT INTO "+tabla.getEsquema()+".HIS_RECHAZO(\r\n");
		buffer.append("\t\t\t\tPIID, \r\n");
		buffer.append("\t\t\t\tCOD_TAREA, \r\n");
		buffer.append("\t\t\t\tDES_TAREA, \r\n");
		buffer.append("\t\t\t\tFECHA, \r\n");
		buffer.append("\t\t\t\tUSUARIO, \r\n");
		buffer.append("\t\t\t\tUSU_NOMBRE, \r\n");
		buffer.append("\t\t\t\tTIPO, \r\n");
		buffer.append("\t\t\t\tCOMENTARIO) \r\n");
		buffer.append("\t\tVALUES (\r\n");
		buffer.append("\t\t\t\tPI_PIID,\r\n"); 
		buffer.append("\t\t\t\tPI_COD_TAREA, \r\n");
		buffer.append("\t\t\t\tPI_DES_TAREA, \r\n");
		buffer.append("\t\t\t\tPI_FECHA, \r\n");
		buffer.append("\t\t\t\tPI_USUARIO, \r\n");
		buffer.append("\t\t\t\tPI_USU_NOMBRE, \r\n");
		buffer.append("\t\t\t\tPI_TIPO, \r\n");
		buffer.append("\t\t\t\tPI_COMENTARIO) ;\r\n");
		buffer.append("END@\r\n\r\n");
		
		
		buffer.append("\tCREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_CON_VALOR (\r\n");
		buffer.append("\t\t\tIN IN_PIID VARCHAR(50)\r\n");
		buffer.append("\t)\r\n");
		buffer.append("\tSPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_CON_VALOR\r\n"); 
		buffer.append("\tDYNAMIC RESULT SETS 1 \r\n");
		buffer.append("\tLANGUAGE SQL \r\n");
		buffer.append("\tBEGIN \r\n");
		buffer.append("\t\tDECLARE result_set_1 CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("\t\t\tSELECT\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.PIID AS PIID,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COD_ITERACION AS COD_ITERACION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COD_OBSERVACION AS COD_OBSERVACION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COD_TAREA AS COD_TAREA,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COD_TIP_OBSERVACION AS COD_TIP_OBSERVACION,\r\n");
		buffer.append("\t\t\t\t(SELECT NOM_OBSERVACION FROM "+tabla.getEsquema()+".MAE_OBSERVACION WHERE COD_TIP_OBSERVACION = T_OBSERVACION.COD_TIP_OBSERVACION) AS NOM_OBSERVACION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COM_OBSERVACION AS COM_OBSERVACION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.USU_OBSERVACION AS USU_OBSERVACION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.USU_NOM_OBSERVACION AS USU_NOM_OBSERVACION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.FEC_OBSERVACION AS FEC_OBSERVACION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COD_TIP_EXCEPCION AS COD_TIP_EXCEPCION,\r\n");
		buffer.append("\t\t\t\t(SELECT NOM_EXCEPCION FROM "+tabla.getEsquema()+".MAE_EXCEPCION WHERE COD_TIP_EXCEPCION = T_OBSERVACION.COD_TIP_EXCEPCION AND COD_TIP_OBSERVACION = T_OBSERVACION.COD_TIP_OBSERVACION) AS NOM_EXCEPCION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COM_EXCEPCION AS COM_EXCEPCION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.USU_EXCEPCION AS USU_EXCEPCION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.USU_NOM_EXCEPCION AS USU_NOM_EXCEPCION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.FEC_EXCEPCION AS FEC_EXCEPCION,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.COD_TIP_DOCUMENTO AS COD_TIP_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t(SELECT NOM_DOCUMENTO FROM "+tabla.getEsquema()+".MAE_DOCUMENTO WHERE COD_TIP_DOCUMENTO = T_OBSERVACION.COD_TIP_DOCUMENTO) AS NOM_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.FLG_CONFIRMADO AS FLG_CONFIRMADO,\r\n");
		buffer.append("\t\t\t\tT_OBSERVACION.FLG_ESTADO AS FLG_ESTADO\r\n");
		buffer.append("\t\t\tFROM "+tabla.getEsquema()+".HIS_OBSERVACION T_OBSERVACION\r\n"); 
		buffer.append("\t\t\tWHERE T_OBSERVACION.PIID = IN_PIID \r\n");
		buffer.append("\t\t\tORDER BY T_OBSERVACION.FEC_OBSERVACION ASC;\r\n");
		buffer.append("\t\tOPEN result_set_1;\r\n");
		buffer.append("END"+SP_CARACTER_CIERRE+"\r\n");
		
		buffer.append("\r\n");
		
		// OBSERVACIONES
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_ELIMINAR (\r\n");
		buffer.append("	IN PI_PIID VARCHAR(50),\r\n");
		buffer.append("	IN PI_COD_ITERACION VARCHAR(20),\r\n");
		buffer.append("	IN PI_COD_OBSERVACION VARCHAR(20)\r\n");
		buffer.append(")\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_ELIMINAR\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("	DELETE\r\n");
		buffer.append("		"+tabla.getEsquema()+".HIS_OBSERVACION\r\n");
		buffer.append("	WHERE\r\n");
		buffer.append("		COD_OBSERVACION = PI_COD_OBSERVACION AND\r\n");
		buffer.append("		PIID = PI_PIID AND\r\n");
		buffer.append("		COD_ITERACION = PI_COD_ITERACION;\r\n");
		buffer.append("END"+SP_CARACTER_CIERRE+"\r\n");
		
		buffer.append("\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_MAE_OBSERVACION_CON_LISTA (\r\n");
		buffer.append("	IN PI_COD_TAREA VARCHAR(80)\r\n");
		buffer.append(")\r\n");
		buffer.append("RESULT SETS 1\r\n");
		buffer.append("LANGUAGE SQL\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_MAE_OBSERVACION_CON_LISTA\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("	DECLARE RS CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("		SELECT\r\n");
		buffer.append("			COD_TIP_OBSERVACION,\r\n");
		buffer.append("			NOM_OBSERVACION\r\n");
		buffer.append("		FROM\r\n");
		buffer.append("			"+tabla.getEsquema()+".MAE_OBSERVACION\r\n");
		buffer.append("		WHERE\r\n");
		buffer.append("			COD_TAREA = PI_COD_TAREA AND\r\n");
		buffer.append("			ESTADO = 1;\r\n");
		buffer.append("	OPEN RS;\r\n");
		buffer.append("END"+SP_CARACTER_CIERRE+"\r\n");
		
		buffer.append("\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_REGISTRO (\r\n");
		buffer.append("	IN PI_COD_ITERACION VARCHAR(20),\r\n");
		buffer.append("	IN PI_COD_OBSERVACION VARCHAR(20),\r\n");
		buffer.append("	IN PI_PIID VARCHAR(50),\r\n");
		buffer.append("	IN PI_COD_TIP_OBSERVACION INTEGER,\r\n");
		buffer.append("	IN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("	IN PI_NOM_TAREA VARCHAR(120),\r\n");
		buffer.append("	IN PI_COM_OBSERVACION VARCHAR(250),\r\n");
		buffer.append("	IN PI_USU_OBSERVACION VARCHAR(25),\r\n");
		buffer.append("	IN PI_USU_NOM_OBSERVACION VARCHAR(250),\r\n");
		buffer.append("	IN PI_FEC_OBSERVACION TIMESTAMP,\r\n");
		buffer.append("	IN PI_FLG_ESTADO VARCHAR(3)\r\n");
		buffer.append(")\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_REGISTRO\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("	INSERT INTO "+tabla.getEsquema()+".HIS_OBSERVACION\r\n");
		buffer.append("		(COD_ITERACION, COD_OBSERVACION, PIID, COD_TIP_OBSERVACION, COD_TAREA, NOM_TAREA, COM_OBSERVACION, USU_OBSERVACION, USU_NOM_OBSERVACION, FEC_OBSERVACION, FLG_ESTADO)\r\n");
		buffer.append("	VALUES \r\n");
		buffer.append("		(PI_COD_ITERACION, PI_COD_OBSERVACION, PI_PIID, PI_COD_TIP_OBSERVACION, PI_COD_TAREA, PI_NOM_TAREA, PI_COM_OBSERVACION, PI_USU_OBSERVACION, PI_USU_NOM_OBSERVACION, PI_FEC_OBSERVACION, PI_FLG_ESTADO);\r\n"); 
		buffer.append("END"+SP_CARACTER_CIERRE+"\r\n");
		
		buffer.append("\r\n");
		
		// SUBSANACIONES
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_SUBSANACION_REGISTRO (\r\n");
		buffer.append("	IN PI_PIID VARCHAR(50),\r\n");
		buffer.append("	IN PI_COD_ITERACION VARCHAR(20),\r\n");
		buffer.append("	IN PI_COD_OBSERVACION VARCHAR(20),\r\n");
		buffer.append("	IN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("	IN PI_COD_TIP_EXCEPCION INTEGER,\r\n");
		buffer.append("	IN PI_COM_EXCEPCION VARCHAR(250),\r\n");
		buffer.append("	IN PI_USU_EXCEPCION VARCHAR(25),\r\n");
		buffer.append("	IN PI_USU_NOM_EXCEPCION VARCHAR(250),\r\n");
		buffer.append("	IN PI_FEC_EXCEPCION TIMESTAMP,\r\n");
		buffer.append("	IN PI_COD_TIP_DOCUMENTO INTEGER,\r\n");
		buffer.append("	IN PI_FLG_ESTADO VARCHAR(3)\r\n");
		buffer.append(")\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_SUBSANACION_REGISTRO\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("	UPDATE\r\n");
		buffer.append("		"+tabla.getEsquema()+".HIS_OBSERVACION\r\n");
		buffer.append("	SET\r\n");
		buffer.append("		COD_TIP_EXCEPCION = PI_COD_TIP_EXCEPCION,\r\n");
		buffer.append("		COM_EXCEPCION = PI_COM_EXCEPCION,\r\n");
		buffer.append("		USU_EXCEPCION = PI_USU_EXCEPCION,\r\n");
		buffer.append("		USU_NOM_EXCEPCION = PI_USU_NOM_EXCEPCION,\r\n");
		buffer.append("		FEC_EXCEPCION = PI_FEC_EXCEPCION,\r\n");
		buffer.append("		COD_TIP_DOCUMENTO = PI_COD_TIP_DOCUMENTO,\r\n");
		buffer.append("		FLG_ESTADO = PI_FLG_ESTADO,\r\n");
		buffer.append("		FLG_CONFIRMADO = NULL\r\n");
		buffer.append("	WHERE\r\n");
		buffer.append("		PIID = PI_PIID AND\r\n");
		buffer.append("		COD_ITERACION = PI_COD_ITERACION AND\r\n");
		buffer.append("		COD_OBSERVACION = PI_COD_OBSERVACION AND\r\n");
		buffer.append("		COD_TAREA = PI_COD_TAREA;\r\n");
		buffer.append("END"+SP_CARACTER_CIERRE+"\r\n");
		
		buffer.append("\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_SUBSANACION_CONFIRMAR (\r\n");
		buffer.append("	IN PI_PIID VARCHAR(50),\r\n");
		buffer.append("	IN PI_COD_ITERACION VARCHAR(20),\r\n");
		buffer.append("	IN PI_COD_OBSERVACION VARCHAR(20),\r\n");
		buffer.append("	IN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("	IN PI_FLG_ESTADO VARCHAR(3),\r\n");
		buffer.append("	IN PI_FLG_CONFIRMADO CHARACTER(1)\r\n");
		buffer.append(")\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_SUBSANACION_CONFIRMAR\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("    UPDATE\r\n");
		buffer.append("		"+tabla.getEsquema()+".HIS_OBSERVACION\r\n");
		buffer.append("    SET\r\n");
		buffer.append("		FLG_CONFIRMADO = PI_FLG_CONFIRMADO,\r\n");
		buffer.append("		FLG_ESTADO = PI_FLG_ESTADO\r\n");
		buffer.append("    WHERE\r\n");
		buffer.append("		PIID = PI_PIID AND\r\n");
		buffer.append("		COD_ITERACION = PI_COD_ITERACION AND\r\n");
		buffer.append("		COD_OBSERVACION = PI_COD_OBSERVACION AND\r\n");
		buffer.append("		COD_TAREA = PI_COD_TAREA;\r\n");
		buffer.append("END"+SP_CARACTER_CIERRE+"\r\n");
		
		buffer.append("\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_MAE_EXCEPCION_CON_LISTA\r\n");
		buffer.append("  (\r\n");
		buffer.append("     IN    PI_CODIGOS_TIP_OBSERVACION     VARCHAR(500)\r\n");
		buffer.append("  )\r\n");
		buffer.append("RESULT SETS 1\r\n");
		buffer.append("LANGUAGE SQL\r\n");
		buffer.append(" SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_MAE_EXCEPCION_CON_LISTA \r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("  DECLARE SQLCODE INT DEFAULT 0;\r\n");
		buffer.append("  DECLARE V_DINAMICO VARCHAR(500);\r\n");
		buffer.append("  DECLARE V_SQL VARCHAR(2500);\r\n");
		buffer.append("  DECLARE c1 CURSOR WITH RETURN FOR V_DINAMICO;\r\n");
		buffer.append("  SET V_SQL = '\r\n");
		buffer.append("    SELECT \r\n");
		buffer.append("       COD_TIP_EXCEPCION,\r\n");
		buffer.append("       COD_TIP_OBSERVACION,\r\n");
		buffer.append("       COD_TIP_DOCUMENTO,\r\n");
		buffer.append("       NOM_EXCEPCION,\r\n");
		buffer.append("       ESTADO,\r\n");
		buffer.append("       DESCRIPCION,\r\n");
		buffer.append("       FLG_DIG_SUSTENTO\r\n");
		buffer.append("    FROM\r\n");
		buffer.append("       "+tabla.getEsquema()+".MAE_EXCEPCION\r\n");
		buffer.append("    WHERE\r\n");
		buffer.append("       COD_TIP_OBSERVACION IN ('||PI_CODIGOS_TIP_OBSERVACION||')';\r\n");
		buffer.append("  PREPARE V_DINAMICO FROM V_SQL;\r\n");
		buffer.append("  OPEN c1;\r\n");
		buffer.append("END"+SP_CARACTER_CIERRE+"\r\n");
		buffer.append("\r\n");
				
		// observaciones FIN
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_MAE_DOCUMENTO_CON_LISTA(\r\n");
		buffer.append("\t\t\t\tIN PI_COD_TAREA VARCHAR(80))\r\n");
		buffer.append("\t\t\t\tSPECIFIC "+tabla.getEsquema()+".SP_PORTAL_MAE_DOCUMENTO_CON_LISTA\r\n");
		buffer.append("\t\t\t\tDYNAMIC RESULT SETS 1\r\n");
		buffer.append("\t\t\t\tLANGUAGE SQL\r\n");
		buffer.append("\t\t\t\tNOT DETERMINISTIC\r\n");
		buffer.append("\t\t\t\tEXTERNAL ACTION\r\n");
		buffer.append("\t\t\t\tMODIFIES SQL DATA\r\n");
		buffer.append("\t\t\t\tCALLED ON NULL INPUT\r\n");
		buffer.append("\t\t\t\tINHERIT SPECIAL REGISTERS\r\n");
		buffer.append("\t\t\t\tOLD SAVEPOINT LEVEL\r\n");
		buffer.append("\t\t\tBEGIN\r\n");
		buffer.append("\t\t\t\tDECLARE result_set_1 CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("\t\t\t\tSELECT MDT.COD_DOCUMENTO,\r\n"); 
		buffer.append("\t\t\t\t\tMDT.COD_TIP_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t\tMDO.NOM_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t\tMDO.FORMATOS,\r\n");
		buffer.append("\t\t\t\t\tMDT.OBLIGATORIO,\r\n");
		buffer.append("\t\t\t\t\tMDT.FLG_ADICIONAL\r\n");
		//buffer.append("\t\t\t\t\tMDT.FLG_VISIBLE_DEFECTO\r\n");
		buffer.append("\t\t\t\tFROM "+tabla.getEsquema()+".MAE_DOCUMENTO_TAREA MDT\r\n");
		buffer.append("\t\t\t\tINNER JOIN "+tabla.getEsquema()+".MAE_DOCUMENTO MDO\r\n"); 
		buffer.append("\t\t\t\t\tON MDO.COD_TIP_DOCUMENTO = MDT.COD_TIP_DOCUMENTO \r\n");
		buffer.append("\t\t\t\tWHERE MDT.COD_TAREA = PI_COD_TAREA AND MDT.ESTADO = 1 ;\r\n");
		buffer.append("\t\t\tOPEN result_set_1;\r\n");
		buffer.append("END@\r\n\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_REGISTRO(\r\n");
		buffer.append("\t\t\t\tIN PI_COD_HIS_DOCUMENTO BIGINT,\r\n");
		buffer.append("\t\t\t\tIN PI_COD_DOCUMENTO INTEGER,\r\n");
		buffer.append("\t\t\t\tIN PI_PIID VARCHAR(50),\r\n");
		buffer.append("\t\t\t\tIN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("\t\t\t\tIN PI_DES_TAREA VARCHAR(120),\r\n");
		buffer.append("\t\t\t\tIN PI_COD_TIPO_DOCUMENTO INTEGER,\r\n");
		buffer.append("\t\t\t\tIN PI_RUT_DOCUMENTO VARCHAR(250),\r\n");
		buffer.append("\t\t\t\tIN PI_NOM_DOCUMENTO VARCHAR(50),\r\n");
		buffer.append("\t\t\t\tIN PI_COMENTARIO VARCHAR(250),\r\n");
		buffer.append("\t\t\t\tIN PI_USUARIO VARCHAR(20),\r\n");
		buffer.append("\t\t\t\tIN PI_USU_NOMBRE VARCHAR(100),\r\n");
		buffer.append("\t\t\t\tIN PI_FLG_ADICIONAL CHAR(1),\r\n");
		buffer.append("\t\t\t\tIN PI_SOL_LECTURA CHAR(1),\r\n");
		buffer.append("\t\t\t\tIN PI_FECHA TIMESTAMP\r\n");
		buffer.append("\t)\r\n");
		buffer.append("\t\t\t\tSPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_REGISTRO\r\n");
		buffer.append("\tBEGIN\r\n");
		buffer.append("\t\tINSERT INTO "+tabla.getEsquema()+".HIS_DOCUMENTO(\r\n");
		buffer.append("\t\t\t\tCOD_HIS_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tCOD_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tPIID, \r\n");
		buffer.append("\t\t\t\tCOD_TAREA, \r\n");
		buffer.append("\t\t\t\tDES_TAREA, \r\n");
		buffer.append("\t\t\t\tCOD_TIPO_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tRUT_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tNOM_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tCOMENTARIO, \r\n");
		buffer.append("\t\t\t\tUSUARIO, \r\n");
		buffer.append("\t\t\t\tUSU_NOMBRE, \r\n");
		buffer.append("\t\t\t\tFLG_ADICIONAL, \r\n");
		buffer.append("\t\t\t\tSOL_LECTURA, \r\n");
		buffer.append("\t\t\t\tFECHA) \r\n");
		buffer.append("\t\tVALUES (\r\n");
		buffer.append("\t\t\t\tPI_COD_HIS_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\tPI_COD_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tPI_PIID, \r\n");
		buffer.append("\t\t\t\tPI_COD_TAREA, \r\n");
		buffer.append("\t\t\t\tPI_DES_TAREA, \r\n");
		buffer.append("\t\t\t\tPI_COD_TIPO_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tPI_RUT_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tPI_NOM_DOCUMENTO, \r\n");
		buffer.append("\t\t\t\tPI_COMENTARIO, \r\n");
		buffer.append("\t\t\t\tPI_USUARIO, \r\n");
		buffer.append("\t\t\t\tPI_USU_NOMBRE, \r\n");
		buffer.append("\t\t\t\tPI_FLG_ADICIONAL, \r\n");
		buffer.append("\t\t\t\tPI_SOL_LECTURA, \r\n");
		buffer.append("\t\t\t\tPI_FECHA); \r\n");
		buffer.append("\t\tEND@\r\n\r\n");
		/*
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_ACTUALIZAR(\r\n");
		buffer.append("\tIN PI_COD_DOCUMENTO INTEGER,\r\n");
		buffer.append("\tIN PI_PIID VARCHAR(50),\r\n");
		buffer.append("\tIN PI_COD_TAREA VARCHAR(80),\r\n");
		buffer.append("\tIN PI_RUT_DOCUMENTO VARCHAR(250),\r\n");
		buffer.append("\tIN PI_NOM_DOCUMENTO VARCHAR(50)\r\n");
		buffer.append(")\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_ACTUALIZAR\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("\tUPDATE "+tabla.getEsquema()+".HIS_DOCUMENTO SET NOM_DOCUMENTO = PI_NOM_DOCUMENTO, RUT_DOCUMENTO = PI_RUT_DOCUMENTO WHERE COD_DOCUMENTO = PI_COD_DOCUMENTO AND PIID = PI_PIID AND COD_TAREA = PI_COD_TAREA;\r\n");
		buffer.append("END@\r\n");
		*/
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_ELIMINAR(\r\n");
		buffer.append("\t\t\t\tIN PI_COD_HIS_DOCUMENTO BIGINT,\r\n");
		buffer.append("\t\t\t\tIN PI_PIID VARCHAR(50),\r\n");
		buffer.append("\t\t\t\tIN PI_COD_TAREA VARCHAR(80)\r\n");
		buffer.append("\t)\r\n");
		buffer.append("\t\t\t\tSPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_ELIMINAR\r\n");
		buffer.append("\tBEGIN\r\n"); 
		buffer.append("\t\tDELETE "+tabla.getEsquema()+".HIS_DOCUMENTO\r\n");
		buffer.append("\t\t\t\tWHERE COD_HIS_DOCUMENTO = PI_COD_HIS_DOCUMENTO;\r\n");
		buffer.append("\t\tEND@\r\n\r\n");
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_CON_LISTA(\r\n");
		buffer.append("\t\t\t\tIN PI_PIID VARCHAR(50))\r\n");
		buffer.append("\t\t\t\tSPECIFIC "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_CON_LISTA\r\n");
		buffer.append("\t\t\t\tDYNAMIC RESULT SETS 1\r\n");
		buffer.append("\t\t\t\tLANGUAGE SQL\r\n");
		buffer.append("\t\t\t\tNOT DETERMINISTIC\r\n");
		buffer.append("\t\t\t\tEXTERNAL ACTION\r\n");
		buffer.append("\t\t\t\tMODIFIES SQL DATA\r\n");
		buffer.append("\t\t\t\tCALLED ON NULL INPUT\r\n");
		buffer.append("\t\t\t\tINHERIT SPECIAL REGISTERS\r\n");
		buffer.append("\t\t\t\tOLD SAVEPOINT LEVEL\r\n");
		buffer.append("\t\t\tBEGIN\r\n");
		buffer.append("\t\t\t\tDECLARE result_set_1 CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("\t\t\t\tSELECT \r\n");
		buffer.append("\t\t\t\t\tHDO.COD_HIS_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t\tHDO.COD_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t\tHDO.PIID,\r\n");
		buffer.append("\t\t\t\t\tHDO.COD_TAREA,\r\n");
		buffer.append("\t\t\t\t\tHDO.COD_TIPO_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t\t(SELECT NOM_DOCUMENTO FROM "+tabla.getEsquema()+".MAE_DOCUMENTO MED WHERE HDO.COD_TIPO_DOCUMENTO = MED.COD_TIP_DOCUMENTO) AS NOM_TIPO_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t\tHDO.NOM_DOCUMENTO,\r\n");
		buffer.append("\t\t\t\t\tHDO.COMENTARIO,\r\n");
		buffer.append("\t\t\t\t\tHDO.FLG_ADICIONAL,\r\n");
		buffer.append("\t\t\t\t\tHDO.SOL_LECTURA\r\n");
		buffer.append("\t\t\t\tFROM "+tabla.getEsquema()+".HIS_DOCUMENTO HDO\r\n");
		buffer.append("\t\t\t\tWHERE PIID = PI_PIID ;\r\n");
		buffer.append("\t\t\tOPEN result_set_1;\r\n");
		buffer.append("END@\r\n\r\n");
		
		
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema().toUpperCase()+".SP_PORTAL_HIS_TAREA_CON_LISTA( IN PI_PIID VARCHAR(50) )\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema().toUpperCase()+".SP_PORTAL_HIS_TAREA_CON_LISTA\r\n");
		buffer.append("DYNAMIC RESULT SETS 1 \r\n");
		buffer.append("LANGUAGE SQL \r\n");
		buffer.append("BEGIN \r\n");
		buffer.append("DECLARE result_set_1 CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("\t\tSELECT  T_HIS_TAREA.PIID AS HIS_TAREA_PIID, \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.TKIID AS HIS_TAREA_TKIID, \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.COD_TAREA AS HIS_TAREA_COD_TAREA,\r\n"); 
		buffer.append("\t\t\tT_HIS_TAREA.DES_TAREA AS HIS_TAREA_DES_TAREA, \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.USU_PROPIETARIO AS HIS_TAREA_USU_PROPIETARIO,\r\n"); 
		buffer.append("\t\t\tT_HIS_TAREA.COMENTARIO AS HIS_TAREA_COMENTARIO, \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.COD_RESULTADO AS HIS_TAREA_COD_RESULTADO, \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.DES_RESULTADO AS HIS_TAREA_DES_RESULTADO, \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.FEC_CREACION AS HIS_TAREA_FEC_CREACION, \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.FEC_INICIO AS HIS_TAREA_FEC_INICIO,  \r\n");
		buffer.append("\t\t\tT_HIS_TAREA.FEC_TERMINO AS HIS_TAREA_FEC_TERMINO\r\n");
		buffer.append("\t\tFROM "+tabla.getEsquema().toUpperCase()+".HIS_TAREA T_HIS_TAREA \r\n");
		buffer.append("\t\tWHERE T_HIS_TAREA.PIID = PI_PIID;\r\n");
		buffer.append("\tOPEN result_set_1; \r\n");
		buffer.append("END@\r\n");
		
		return buffer.toString();
	}
	
	private static String spConsultasContenidoArchivoDLL_plantillaDrop(Proyecto proyecto, Connection conn){
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		buffer.append("--------------------------------------------------------------------------------\r\n");
		buffer.append("--ELIMINAR FUNCIONES PLANTILLAS\r\n");
		buffer.append("DROP FUNCTION "+tabla.getEsquema()+".FN_PORTAL_CATALOGO_CON_VALOR_1@\r\n");
		buffer.append("DROP FUNCTION "+tabla.getEsquema()+".FN_PORTAL_CATALOGO_CON_VALOR_2@\r\n");
		
		buffer.append("\r\n");
		buffer.append("--------------------------------------------------------------------------------\r\n");
		buffer.append("--ELIMINA STORE PROCEDURE PLANTILLAS\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_CATALOGO_CON_LISTA@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_TAREA_REGISTRO@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_CANCELACION_REGISTRO@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_RECHAZO_REGISTRO@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_CON_LISTA@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_CON_VALOR@\r\n");
		buffer.append("\r\n");
		buffer.append("--------------------------------------------------------------------------------\r\n");
		buffer.append("--ELIMINAR STORE PROCEDURE DE OBSERVACIONES Y SUBSANACIONES\r\n");
		// OBSERVACIONES
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_ELIMINAR@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_MAE_EXCEPCION_CON_LISTA@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_OBSERVACION_REGISTRO@\r\n");
		// SUBSANACIONES
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_SUBSANACION_REGISTRO@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_SUBSANACION_CONFIRMAR@\r\n");
		buffer.append("--ELIMINAR STORE PROCEDURE DE DOCUMENTOS\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_MAE_DOCUMENTO_CON_LISTA@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_REGISTRO@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_HIS_DOCUMENTO_ELIMINAR@\r\n");
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_MAE_OBSERVACION_CON_LISTA@\r\n");
		
		buffer.append("DROP PROCEDURE "+tabla.getEsquema().toUpperCase()+".SP_PORTAL_HIS_TAREA_CON_LISTA@\r\n");
		
		return buffer.toString();
	}
	
	private static String spConsultasContenidoArchivoDLL_simpleDrop(Consulta consulta, Tabla tabla){
		StringBuffer buffer = new StringBuffer();
		if(tabla!=null){
			buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_CON_"+consulta.getAleas().toUpperCase()+"_CON_VALOR@ \r\n");
		}
		return buffer.toString();
	}
	
	private static String spConsultasContenidoArchivoDLL_unoDrop(Consulta consulta, Tabla tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_CON_"+consulta.getAleas().toUpperCase()+"_CON_VALOR@ \r\n");
		return buffer.toString();
	}
	
	private static String spConsultasContenidoArchivoDLL_uno(Consulta consulta, TablaConsulta tabla){

		TablaConsulta tablaPadre = consulta.getTablaPadre();
		List<TablaConsulta> tablasFK = consulta.getTablasFK();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_CON_"+consulta.getAleas().toUpperCase()+"_CON_VALOR (\r\n");
		int limiteA = tabla.getCampoSQLConsultas().size()-1;
		List<CampoSQLConsulta> atributosPK = new ArrayList<CampoSQLConsulta>();
		for(int x=0;x<tabla.getCampoSQLConsultas().size();x++){
			if(tabla.getCampoSQLConsultas().get(x).isFlgPK()){
				atributosPK.add(tabla.getCampoSQLConsultas().get(x));
			}
		}
		
		List<CampoSQLConsulta> atributosCondicion = new ArrayList<CampoSQLConsulta>();
		for(int x=0;x<tabla.getCampoSQLConsultas().size();x++){
			if(tabla.getCampoSQLConsultas().get(x).isFlgCondicion()){
				atributosCondicion.add(tabla.getCampoSQLConsultas().get(x));
			}
		}
		
		int indicadorInicio = 0;
		for(int x=0;x<atributosCondicion.size();x++){
			CampoSQLConsulta atributo = atributosCondicion.get(x);
			if(atributo.isTieneFuncion()==false){
				if(indicadorInicio==0){
					indicadorInicio = 1;
					if(atributosCondicion.get(x).getTipo().equalsIgnoreCase("VARCHAR") || atributosCondicion.get(x).getTipo().equalsIgnoreCase("CHAR")){
						buffer.append("\t\tIN IN_"+atributosCondicion.get(x).getNombre()+" "+atributosCondicion.get(x).getTipo()+"("+atributosCondicion.get(x).getLongitud()+")\r\n" );
					}else{
						buffer.append("\t\tIN IN_"+atributosCondicion.get(x).getNombre()+" "+atributosCondicion.get(x).getTipo()+"\r\n" );
					}
				}else{
					
					if(atributosCondicion.get(x).getTipo().equalsIgnoreCase("VARCHAR") || atributosCondicion.get(x).getTipo().equalsIgnoreCase("CHAR")){
						buffer.append("\t\t,IN IN_"+atributosCondicion.get(x).getNombre()+" "+atributosCondicion.get(x).getTipo()+"("+atributosCondicion.get(x).getLongitud()+")\r\n" );
					}else{
						buffer.append("\t\t,IN IN_"+atributosCondicion.get(x).getNombre()+" "+atributosCondicion.get(x).getTipo()+"\r\n" );
					}
				}
			}
		}
		buffer.append(")SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_CON_"+consulta.getAleas().toUpperCase()+"_CON_VALOR\r\n");
		buffer.append("DYNAMIC RESULT SETS 1 \r\n");
		buffer.append("LANGUAGE SQL \r\n");
		buffer.append("BEGIN \r\n\r\n");
		buffer.append("\tDECLARE result_set_1 CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("\t\tSELECT \r\n");
		
		List<CampoSQLConsulta> atributosIncluidos = consulta.getTablaPadre().getCampoSQLConsultas();
		
		limiteA = atributosIncluidos.size()-1;
		for(int x=0;x<atributosIncluidos.size();x++){
			CampoSQLConsulta atributo = atributosIncluidos.get(x);
			if(atributo.isTieneFuncion()){				
				if(x==limiteA){
					buffer.append("\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo()+"\r\n");
				}else{
					buffer.append("\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo()+",\r\n");
				}
			}else{
				if(x==limiteA){
					buffer.append("\t\t\tT_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre()+"\r\n");
				}else{
					buffer.append("\t\t\tT_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre()+",\r\n");
				}
			}
		}
		
		List<TablaConsulta> tablasFKConsulta = consulta.getTablasFK();
		for (int i = 0; i < tablasFKConsulta.size(); i++) {
			atributosIncluidos = tablasFKConsulta.get(i).getCampoSQLConsultas();
			
			limiteA = atributosIncluidos.size()-1;
			for(int x=0;x<atributosIncluidos.size();x++){
				CampoSQLConsulta atributo = atributosIncluidos.get(x);
				buffer.append(",");
				if(atributo.isTieneFuncion()){				
					if(x==limiteA){
						buffer.append("\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo()+"\r\n");
					}else{
						buffer.append("\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo()+"\r\n");
					}
				}else{
					if(x==limiteA){
						buffer.append("\t\t\tT_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre()+"\r\n");
					}else{
						buffer.append("\t\t\tT_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre()+"\r\n");
					}
				}
			}
		}
		
		buffer.append("\t\tFROM "+tablaPadre.getEsquema()+"."+tablaPadre.getNombre()+" T_"+tablaPadre.getNombre()+" \r\n");
		if(tablasFK.size()>0){
			for(int x=0;x<tablasFK.size();x++){
				TablaConsulta tablaFK = tablasFK.get(x);
				buffer.append("\t\tLEFT JOIN "+tablaFK.getEsquema()+"."+tablaFK.getNombre()+" T_"+tablaFK.getNombre()+" \r\n");
				
				List<CampoSQLConsulta> camposSQLFK = tablaFK.getCampoSQLConsultas();
				int z=0;
				for(int y=0;y<camposSQLFK.size();y++){
					CampoSQLConsulta campoSQLConsulta = camposSQLFK.get(y);
					if(campoSQLConsulta.isFlgTieneFK()){
						if(z==0){
							buffer.append("\t\t\tON T_"+campoSQLConsulta.getTabla().getNombre()+"."+campoSQLConsulta.getNombre()+" = T_"+tablaPadre.getNombre()+"."+campoSQLConsulta.getFk().getNombre()+" \r\n");
						}else{
							buffer.append("\t\t\tAND T_"+campoSQLConsulta.getTabla().getNombre()+"."+campoSQLConsulta.getNombre()+" = T_"+tablaPadre.getNombre()+"."+campoSQLConsulta.getFk().getNombre()+" \r\n");
						}
						z++;
					}
				}	
			}
		}
		buffer.append("\t\tWHERE \r\n");
		
		indicadorInicio = 0;
		for(int x=0;x<atributosCondicion.size();x++){
			if(indicadorInicio==0){
				buffer.append("\t\t\tT_"+atributosCondicion.get(x).getTabla().getNombre()+"."+atributosCondicion.get(x).getNombre()+" = IN_"+atributosCondicion.get(0).getNombre());
				indicadorInicio = 1;
			}else{
				buffer.append("\t\t\tAND T_"+atributosCondicion.get(x).getTabla().getNombre()+"."+atributosCondicion.get(x).getNombre()+" = IN_"+atributosCondicion.get(0).getNombre()+" \r\n");
			}
		}
		buffer.append(";\r\n");
		buffer.append("\tOPEN result_set_1;\r\n");
		buffer.append("END@");
		return buffer.toString();
	}
	
	private static String spConsultasContenidoArchivoDLL_multipleDrop(Consulta consulta, Tabla tabla, Tabla tablaRF){
		StringBuffer buffer = new StringBuffer();
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_CON_"+consulta.getAleas().toUpperCase()+"_"+tablaRF.getNombre().toUpperCase()+"_CON_LISTA@ \r\n");
		return buffer.toString();
	}
	
	private static String spConsultasContenidoArchivoDLL_multiple(Consulta consulta, TablaConsulta tablaPadre, TablaConsulta tablaFK){
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE "+tablaPadre.getEsquema()+".SP_PORTAL_CON_"+consulta.getAleas().toUpperCase()+"_"+tablaFK.getNombre().toUpperCase()+"_CON_LISTA (\r\n");
		List<CampoSQLConsulta> campoSQLs = tablaFK.getCampoSQLConsultas();
		int limiteA = campoSQLs.size()-1;
		List<CampoSQLConsulta> atributosPK = new ArrayList<CampoSQLConsulta>();
		for(int x=0;x<campoSQLs.size();x++){
			if(campoSQLs.get(x).isFlgPK()){
				atributosPK.add(campoSQLs.get(x));
			}
		}
		
		List<CampoSQLConsulta> atributosFK = new ArrayList<CampoSQLConsulta>();
		for(int x=0;x<campoSQLs.size();x++){
			CampoSQLConsulta campoSQLConsulta = campoSQLs.get(x);
			if(campoSQLConsulta.isFlgTieneFK()){
				atributosFK.add(campoSQLConsulta);
			}
		}
		
		int limiteB = atributosFK.size()-1;
		
		for(int x=0;x<atributosFK.size();x++){
			CampoSQLConsulta campoSQLConsulta = atributosFK.get(x);
			if(campoSQLConsulta.isTieneFuncion()==false){
				if(x==limiteB){
					buffer.append("\t\tIN IN_"+atributosFK.get(x).getNombre()+" "+atributosFK.get(x).getTipo()+"\r\n" );
				}else{
					buffer.append("\t\tIN IN_"+atributosFK.get(x).getNombre()+" "+atributosFK.get(x).getTipo()+",\r\n" );
				}
			}
		}
		buffer.append(")SPECIFIC "+tablaPadre.getEsquema()+".SP_PORTAL_CON_"+consulta.getAleas().toUpperCase()+"_"+tablaFK.getNombre().toUpperCase()+"_CON_LISTA\r\n");
		buffer.append("DYNAMIC RESULT SETS 1 \r\n");
		buffer.append("LANGUAGE SQL \r\n");
		buffer.append("BEGIN \r\n\r\n");
		buffer.append("\tDECLARE result_set_1 CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("\t\tSELECT \r\n");
		
		
		List<CampoSQLConsulta> camposSQLFK = tablaFK.getCampoSQLConsultas();
		limiteA = camposSQLFK.size()-1;
		for(int x=0;x<camposSQLFK.size();x++){
			CampoSQLConsulta campoSQLConsulta = camposSQLFK.get(x);
			if(campoSQLConsulta.isTieneFuncion()){
				if(x==limiteA){
					buffer.append("\t\t\t"+campoSQLConsulta.getFuncionBusqueda()+"('"+campoSQLConsulta.getFuncionBusquedaCatalogo()+"', T_"+tablaFK.getNombre()+"."+campoSQLConsulta.getNombre()+") AS "+tablaFK.getNombre()+"_"+campoSQLConsulta.getNombre()+"_"+campoSQLConsulta.getFuncionBusquedaCatalogo()+"\r\n");
				}else{
					buffer.append("\t\t\t"+campoSQLConsulta.getFuncionBusqueda()+"('"+campoSQLConsulta.getFuncionBusquedaCatalogo()+"', T_"+tablaFK.getNombre()+"."+campoSQLConsulta.getNombre()+") AS "+tablaFK.getNombre()+"_"+campoSQLConsulta.getNombre()+"_"+campoSQLConsulta.getFuncionBusquedaCatalogo()+",\r\n");
				}
			}else{
				if(x==limiteA){
					buffer.append("\t\t\tT_"+tablaFK.getNombre()+"."+campoSQLConsulta.getNombre()+" AS "+tablaFK.getNombre()+"_"+campoSQLConsulta.getNombre()+"\r\n");
				}else{
					buffer.append("\t\t\tT_"+tablaFK.getNombre()+"."+campoSQLConsulta.getNombre()+" AS "+tablaFK.getNombre()+"_"+campoSQLConsulta.getNombre()+",\r\n");
				}
			}
		}
		buffer.append("\t\tFROM "+tablaFK.getEsquema()+"."+tablaFK.getNombre()+" T_"+tablaFK.getNombre()+" \r\n");
		buffer.append("\t\t\tWHERE ");
		
		List<CampoSQLConsulta> atributosWhere = new ArrayList<CampoSQLConsulta>();
		List<CampoSQL> atributosWhereFK = new ArrayList<CampoSQL>();
		for(int x=0;x<atributosFK.size();x++){
			CampoSQLConsulta atributoFK = atributosFK.get(x);
			if(atributoFK.isFlgTieneFK()){
				atributosWhere.add(atributoFK);
				atributosWhereFK.add(atributoFK.getFk());
			}
		}
		
		limiteB = atributosWhere.size()-1;
		for(int x=0;x<atributosWhere.size();x++){
			CampoSQL atributo = atributosWhere.get(x);
			if(x==limiteB){
				buffer.append("T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+" = IN_"+atributo.getFk().getNombre());
			}else{
				buffer.append("T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+" = IN_"+atributo.getFk().getNombre()+" AND \r\n");
			}
		}
		buffer.append(";\r\n");
		buffer.append("\t\tOPEN result_set_1;\r\n");
		buffer.append("END@");
		return buffer.toString();
	}

	
	private static String spProcesosContenidoArchivoSPDrop(Proceso proceso, Tabla tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("DROP PROCEDURE " + tabla.getEsquema().toUpperCase() + ".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_REGISTRO@ \r\n");
		return buffer.toString();
	}
	
	private static String spProcesosContenidoArchivoSP(Proceso proceso, Tabla tabla){
		
		StringBuffer buffer = new StringBuffer();
		List<CampoSQLProceso> camposSQLProceso = proceso.getCamposSQLProceso();
		if(camposSQLProceso!=null && camposSQLProceso.size()>0){
			
			buffer.append("CREATE OR REPLACE PROCEDURE " + tabla.getEsquema().toUpperCase() + ".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_REGISTRO(\r\n");    
			
			for (int x=0;x<camposSQLProceso.size();x++){
				 
				String tablaTemp = camposSQLProceso.get(x).getTabla().getNombre();
				String parametro = camposSQLProceso.get(x).getNombre();
				String tipoparametro = camposSQLProceso.get(x).getTipo();
				int longitud = camposSQLProceso.get(x).getLongitud();
				int precision = camposSQLProceso.get(x).getPrecision();
				
			    if (x==camposSQLProceso.size()-1){
			    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
						buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
			    	}else if(tipoparametro.equalsIgnoreCase("CHAR") || tipoparametro.equalsIgnoreCase("CHARACTER")){
					    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
					}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("DATE")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+" \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
				    }else{
				    	System.out.println("...DATO NO SOPORTADO.."+parametro);
				    }
				    
				    buffer.append("\t) \r\n");	
			    }else{
			    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
						buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
				    }else if(tipoparametro.equalsIgnoreCase("CHAR") || tipoparametro.equalsIgnoreCase("CHARACTER")){
					    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
					}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("DATE")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+", \r\n");
				    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
				    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
				    }else{
				    	System.out.println("...DATO NO SOPORTADO.."+parametro);
				    }
			    }
			}
			buffer.append("SPECIFIC " + camposSQLProceso.get(0).getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_REGISTRO\r\n");
			buffer.append("BEGIN \r\n");
			    
			    
			Map<String, List<CampoSQLProceso>> tablas = new HashMap<String, List<CampoSQLProceso>>();
			List<String> nombreTablas = new ArrayList<String>();
		    for (int x=0;x<camposSQLProceso.size();x++){
		    	CampoSQLProceso campoSQLProceso = camposSQLProceso.get(x);
		    	if(campoSQLProceso!=null && campoSQLProceso.getTabla().getNombre()!=null && campoSQLProceso.getTabla().getNombre().trim().length()>0){
		    		 if(tablas.containsKey(campoSQLProceso.getTabla().getNombre())){
		    			 tablas.get(campoSQLProceso.getTabla().getNombre()).add(campoSQLProceso);
		    		 }else{
		    			 List<CampoSQLProceso> atributos = new ArrayList<CampoSQLProceso>();
		    			 atributos.add(campoSQLProceso);
		    			 tablas.put(campoSQLProceso.getTabla().getNombre(), atributos);
		    			 nombreTablas.add(campoSQLProceso.getTabla().getNombre());
		    		 }
		    	}
		    }
			
		    for(int x=0; x<nombreTablas.size();x++){
		    	List<CampoSQLProceso> atributosTabla = tablas.get(nombreTablas.get(x));
		    	buffer.append("\t\t\tINSERT INTO "+ Cadena.convertirMayuscula(atributosTabla.get(0).getTabla().getEsquema())+"."+ atributosTabla.get(0).getTabla().getNombre()+"("+"\r\n");
		    	int z = atributosTabla.size()-1;
		    	for(int y=0; y<atributosTabla.size(); y++){
		    		CampoSQLProceso atributo = atributosTabla.get(y);
			       	if (z==y){
			       		buffer.append("\t\t\t\t\t\t"+ atributo.getNombre() +") \r\n");
			       	}else{
			       		buffer.append("\t\t\t\t\t\t"+ atributo.getNombre() +", \r\n");
			       	}
			    }
				buffer.append("\t\t\tVALUES (\r\n");
				for(int y=0; y<atributosTabla.size(); y++){
					CampoSQLProceso atributo = atributosTabla.get(y);
			       	if (z==y){
			       		buffer.append("\t\t\t\t\t\tPI_"+ atributo.getTabla().getNombre() +"_"+ atributo.getNombre() +"); \r\n\r\n");
			       	}else{
			       		buffer.append("\t\t\t\t\t\tPI_"+ atributo.getTabla().getNombre() +"_"+ atributo.getNombre() +", \r\n");
			       	}
			    }
		    }
			buffer.append("END@\r\n\r\n");
		
		}
	 	return buffer.toString();
		
		
	}
	
	private static String spProcesosContenidoArchivoSecDrop(Proceso proceso, Tabla tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("DROP PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_SECUENCIA@ \r\n");
		return buffer.toString();
	}
	
	private static String spProcesosContenidoArchivoSec(Proceso proceso, AtributoProceso atributo){
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE "+atributo.getCampoSQLProceso().getTabla().getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_SECUENCIA(\r\n");
		buffer.append("\t\t\tOUT PO_"+atributo.getCampoSQLProceso().getNombre()+"	"+atributo.getCampoSQLProceso().getTipo()+")\r\n");
		buffer.append("SPECIFIC "+atributo.getCampoSQLProceso().getTabla().getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_SECUENCIA\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("\t\t\tSELECT (NEXTVAL FOR "+atributo.getCampoSQLProceso().getTabla().getEsquema()+"."+atributo.getSqlNombreSecuencial()+") INTO PO_"+atributo.getCampoSQLProceso().getNombre()+" FROM SYSIBM.SYSDUMMY1;\r\n");
		buffer.append("END@\r\n\r\n");
		return buffer.toString();
	}
	
	private static String spProcesosContenidoSecuencialDocumento(Proyecto proyecto, Proceso proceso){
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_SECUENCIA_DOCUMENTO(\r\n");
		buffer.append("\t\t\tOUT PO_COD_DOCUMENTO	BIGINT)\r\n");
		buffer.append("SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_SECUENCIA_DOCUMENTO\r\n");
		buffer.append("BEGIN\r\n");
		buffer.append("\t\t\tSELECT (NEXTVAL FOR "+tabla.getEsquema()+"."+proceso.getNombreSecuenciaDocumentos()+") INTO PO_COD_DOCUMENTO FROM SYSIBM.SYSDUMMY1;\r\n");
		buffer.append("END@\r\n\r\n");
		return buffer.toString();
	}

	private static String spTareasContenidoArchivoDLLTrabajar(Tarea tarea, Connection conn){
		
		StringBuffer buffer = new StringBuffer();
		List<CampoSQLTarea> campoSQLTarea = tarea.getCamposTrabajar();
		String esquema = campoSQLTarea.get(0).getTabla().getEsquema();
		
		//sacamos todas las tablas que deben registrarse segun el proceso
		Map<String, String> mapTablas = new HashMap<String, String>();
		for (int i = 0; i < campoSQLTarea.size(); i++) {
			CampoSQLTarea atributo = campoSQLTarea.get(i);
			if(mapTablas.containsKey(atributo.getTabla().getNombre())==false){
				mapTablas.put(atributo.getTabla().getNombre(), atributo.getTabla().getNombre());
			}
		}
		
		List<CampoSQLTarea> atributosInput = new ArrayList<CampoSQLTarea>();
		List<CampoSQLTarea> atributosPKs = new ArrayList<CampoSQLTarea>();
		Map<Integer, CampoSQLTarea> atributosPKMap = new HashMap<Integer, CampoSQLTarea>();
		for (int i = 0; i < campoSQLTarea.size(); i++) {
			CampoSQLTarea atributo = campoSQLTarea.get(i);
			if(atributo.isFlgListado()==false){
				if(atributo.isFlgPK() && atributosPKMap.containsKey(Integer.valueOf(atributo.getCodigo()))==false && mapTablas.containsKey(atributo.getTabla().getNombre())){
					atributosPKMap.put(Integer.valueOf(atributo.getCodigo()), atributo);
					atributosInput.add(atributo);
					atributosPKs.add(atributo);
				}
			}
		}
		
		for (int i = 0; i < campoSQLTarea.size(); i++) {
			CampoSQLTarea atributo = campoSQLTarea.get(i);
			if(atributo.isFlgListado()==false){
				if(atributosPKMap.containsKey(Integer.valueOf(atributo.getCodigo()))==false){
					atributosInput.add(atributo);
				}
			}
		}
		
		if(atributosInput.size()>0){
			buffer.append("CREATE OR REPLACE PROCEDURE " + campoSQLTarea.get(0).getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_TRABAJAR(\r\n");    
			
			for (int x=0;x<atributosInput.size();x++){
				CampoSQLTarea atributo = atributosInput.get(x);
				if(atributo.isTieneFuncion()==false){
					String tablaTemp = atributo.getTabla().getNombre();
					String parametro = atributo.getNombre();
					String tipoparametro = atributo.getTipo();
					int longitud = atributo.getLongitud();
					int precision = atributo.getPrecision();
					
				    if (x==atributosInput.size()-1){
				    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
							buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
					    }
						if(tipoparametro.equalsIgnoreCase("CHAR")){
						    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
						}
					    if(tipoparametro.equalsIgnoreCase("INTEGER")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
					    }
					    if(tipoparametro.equalsIgnoreCase("DATE")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
					    }
					    if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
					    }
					    if(tipoparametro.equalsIgnoreCase("DECIMAL")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+" \r\n");
					    }
						
					    if(tipoparametro.equalsIgnoreCase("BIGINT")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
					    }
					    buffer.append("\t) \r\n");	
				    }else{
				    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
							buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
					    }
						if(tipoparametro.equalsIgnoreCase("CHAR")){
						    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
						}
					    if(tipoparametro.equalsIgnoreCase("INTEGER")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
					    }
					    if(tipoparametro.equalsIgnoreCase("DATE")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
					    }
					    if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
					    }
					    if(tipoparametro.equalsIgnoreCase("DECIMAL")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+", \r\n");
					    }
						
					    if(tipoparametro.equalsIgnoreCase("BIGINT")){
					    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
					    }
				    }
				}
			}
			buffer.append("SPECIFIC " + campoSQLTarea.get(0).getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_TRABAJAR\r\n");
			buffer.append("BEGIN \r\n");
			    
			    
			Map<String, List<CampoSQLTarea>> tablas = new HashMap<String, List<CampoSQLTarea>>();
			List<String> nombreTablas = new ArrayList<String>();
		    for (int x=0;x<campoSQLTarea.size();x++){
		    	CampoSQLTarea atributo = campoSQLTarea.get(x);
		    	if(atributo!=null && atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
		    		 if(tablas.containsKey(atributo.getTabla().getNombre())){
		    			 tablas.get(atributo.getTabla().getNombre()).add(atributo);
		    		 }else{
		    			 List<CampoSQLTarea> atributos = new ArrayList<CampoSQLTarea>();
		    			 atributos.add(atributo);
		    			 tablas.put(atributo.getTabla().getNombre(), atributos);
		    			 nombreTablas.add(atributo.getTabla().getNombre());
		    		 }
		    	}
		    }
			
		    for(int x=0; x<nombreTablas.size();x++){
		    	List<CampoSQLTarea> atributosTabla = tablas.get(nombreTablas.get(x));
		    	if(atributosTabla.get(0).isFlgListado()==false){
			    	buffer.append("\t\t\tUPDATE "+atributosTabla.get(0).getTabla().getEsquema().toUpperCase()+"."+atributosTabla.get(0).getTabla().getNombre()+"\r\n");
			    	buffer.append("\t\t\t\t SET	");
			    	for(int y=0; y<atributosTabla.size(); y++){
			    		CampoSQLTarea atributo = atributosTabla.get(y);
			    		if(atributo.isTieneFuncion()==false){
				    		
				    		if(y!=0){
				    			buffer.append(",\r\n");
				    			buffer.append("\t\t\t\t\t\t");
				    		}
				    		
				    		buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre().toUpperCase()+"_"+atributo.getNombre().toUpperCase());
				    		
				    		
				    		
			    		}
			    	}
			    	buffer.append("\r\n");
			    	buffer.append("\t\t\tWHERE ");
			    	
			    	List<CampoSQLTarea> atributosPKTabla = new ArrayList<CampoSQLTarea>();
			    	for(int y=0; y<atributosPKs.size(); y++){
			    		CampoSQLTarea atributo = atributosPKs.get(y);
			    		if(nombreTablas.get(x).equalsIgnoreCase(atributo.getTabla().getNombre())){
			    			atributosPKTabla.add(atributo);
			    		}
					}
			    	
			    	for(int y=0; y<atributosPKTabla.size(); y++){
			    		CampoSQLTarea atributo = atributosPKTabla.get(y);
			    		if(y!=0){
			    			buffer.append("\t\t\t\t\t");
			    		}
			    		
			    		if(y==atributosPKTabla.size()-1){
			    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+";\r\n\r\n");
			    		}else{
			    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+" AND\r\n");
			    		}
			    	}
		    	}
		    	
		    }
			buffer.append("END@\r\n");
		}
		
		TablaDaoImpl tablaDaoImpl = new TablaDaoImpl();
		
		for (int x = 0; x < campoSQLTarea.size(); x++) {
			CampoSQLTarea atributoBase = campoSQLTarea.get(x);
			if(atributoBase.isFlgRequiereEliminar()){
				List<CampoSQLTarea> atributosFK = tablaDaoImpl.obtenerCamposFK(atributoBase, conn);
				buffer.append("CREATE OR REPLACE PROCEDURE "+esquema+".SP_PORTAL_TAR_"+tarea.getAleas()+"_ELI_"+atributoBase.getTabla().getNombre()+"(\r\n");
				
				for (int y = 0; y<atributosFK.size(); y++) {
					CampoSQLTarea atributoFK = atributosFK.get(y);
					if(y==0){
						buffer.append("\t\t\tIN PI_"+atributoFK.getTabla().getNombre()+"_"+atributoFK.getNombre()+" "+atributoFK.getTipo()+"\r\n");
					}else{
						buffer.append("\t\t\t,IN PI_"+atributoFK.getTabla().getNombre()+"_"+atributoFK.getNombre()+" "+atributoFK.getTipo()+"\r\n");
					}
				}
				buffer.append(")\r\n");
				buffer.append("\tSPECIFIC "+esquema+".SP_PORTAL_TAR_"+tarea.getAleas()+"_ELI_"+atributoBase.getTabla().getNombre()+"\r\n");
				buffer.append("BEGIN\r\n"); 
				buffer.append("\tDELETE FROM "+esquema+"."+atributoBase.getTabla().getNombre()+" WHERE ");
				for (int y = 0; y<atributosFK.size(); y++) {
					CampoSQLTarea atributoFK = atributosFK.get(y);
					if(y==0){
						buffer.append(atributoFK.getNombre()+" = PI_"+atributoFK.getTabla().getNombre()+"_"+atributoFK.getNombre());
					}else{
						buffer.append(" AND "+atributoFK.getNombre()+" = PI_"+atributoFK.getTabla().getNombre()+"_"+atributoFK.getNombre());
					}
					if(y==(atributosFK.size()-1)){
						buffer.append(";\r\n");
					}else{
						buffer.append("\r\n");
					}
				}
				
				buffer.append("END@\r\n\r\n");
			}
		}
		
		for (int x = 0; x < campoSQLTarea.size(); x++) {
			CampoSQLTarea atributoBase = campoSQLTarea.get(0);
			
			if(atributoBase.isFlgListado()){
				buffer.append("CREATE OR REPLACE PROCEDURE "+esquema+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_"+atributoBase.getTabla().getNombre()+"(\r\n");
				
				List<CampoSQLTarea> atributos = campoSQLTarea;
				for (int y = 0; y < atributos.size(); y++) {
					CampoSQLTarea atributo = atributos.get(y);
					buffer.append(Validaciones.obtenerSQLIntro(atributo));
					if(y==(atributos.size()-1)){
						buffer.append("\r\n");
					}else{
						buffer.append(",\r\n");
					}
				}
				buffer.append(")\r\n");
				buffer.append("\tSPECIFIC "+esquema+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_"+atributoBase.getTabla().getNombre()+"\r\n");
				buffer.append("BEGIN\r\n"); 
				buffer.append("\t\t\tINSERT INTO "+esquema+"."+atributoBase.getTabla().getNombre()+"(\r\n");
				
				for (int y = 0; y < atributos.size(); y++) {
					CampoSQLTarea atributo = atributos.get(y);
					buffer.append("\t\t\t\t\t"+atributo.getNombre()+"");
					if(y==(atributos.size()-1)){
						buffer.append("\r\n");
					}else{
						buffer.append(",\r\n");
					}
				}
				buffer.append("\t\t\t) VALUES (\r\n");
				for (int y = 0; y < atributos.size(); y++) {
					CampoSQLTarea atributo = atributos.get(y);
					buffer.append("\t\t\t\t\tPI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"");
					if(y==(atributos.size()-1)){
						buffer.append(");\r\n");
					}else{
						buffer.append(",\r\n");
					}
				}
				buffer.append("END@\r\n");
			}
		}
		
	 	return buffer.toString();
		
		
	}
	
	private static String spTareasContenidoArchivoDLLTrabajarDrop(Tarea tarea, Connection conn){
		
		Map<String, String> mapTablas = new HashMap<String, String>();
		List<CampoSQLTarea> camposTrabajar = tarea.getCamposTrabajar();
		String esquema = camposTrabajar.get(0).getTabla().getEsquema();
		
		for (int i = 0; i < camposTrabajar.size(); i++) {
			CampoSQLTarea atributo = camposTrabajar.get(i);
			if(mapTablas.containsKey(atributo.getTabla().getNombre())==false){
				mapTablas.put(atributo.getTabla().getNombre(), atributo.getTabla().getNombre());
			}
		}
		
		List<CampoSQLTarea> atributosInput = new ArrayList<CampoSQLTarea>();
		List<CampoSQLTarea> atributosPKs = new ArrayList<CampoSQLTarea>();
		Map<Integer, CampoSQLTarea> atributosPKMap = new HashMap<Integer, CampoSQLTarea>();
		for (int i = 0; i < camposTrabajar.size(); i++) {
			CampoSQLTarea atributo = camposTrabajar.get(i);
			if(atributo.isFlgListado()==false){
				if(atributo.isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false && mapTablas.containsKey(atributo.getTabla().getNombre())){
					atributosPKMap.put(atributo.getCodigo(), atributo);
					atributosInput.add(atributo);
					atributosPKs.add(atributo);
				}
			}
		}
		
		for (int i = 0; i < camposTrabajar.size(); i++) {
			CampoSQLTarea atributo = camposTrabajar.get(i);
			if(atributo.isFlgListado()==false){
				if(atributosPKMap.containsKey(atributo.getCodigo())==false){
					atributosInput.add(atributo);
				}
			}
		}
		
		StringBuffer buffer = new StringBuffer();
		if(atributosInput.size()>0){
			buffer.append("DROP PROCEDURE " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_TRABAJAR@\r\n");
		}
		
		return buffer.toString();
	}
	
	private static String spTareasContenidoArchivoDLLCompletar(Tarea tarea) throws Exception{
		List<CampoSQLTarea> camposCompletar = tarea.getCamposCompletar();
		String esquema = camposCompletar.get(0).getTabla().getEsquema();
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_COMPLETAR(\r\n");    
		
		//sacamos todas las tablas que deben registrarse segun el proceso
		Map<String, String> mapTablas = new HashMap<String, String>();
		for (int i = 0; i < camposCompletar.size(); i++) {
			CampoSQLTarea atributo = camposCompletar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0 && mapTablas.containsKey(atributo.getTabla().getNombre())==false){
				mapTablas.put(atributo.getTabla().getNombre(), atributo.getTabla().getNombre());
			}
		}
		
		List<CampoSQL> atributosInput = new ArrayList<CampoSQL>();
		List<CampoSQL> atributosPKs = new ArrayList<CampoSQL>();
		Map<String, CampoSQLTarea> atributosPKMap = new HashMap<String, CampoSQLTarea>();
		for (int i = 0; i < camposCompletar.size(); i++) {
			CampoSQLTarea atributo = camposCompletar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				if(atributo.isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false && mapTablas.containsKey(atributo.getTabla().getNombre())){
					atributosPKs.add(atributo);
				}
			}
		}
		
		for (int i = 0; i < camposCompletar.size(); i++) {
			CampoSQLTarea atributo = camposCompletar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				atributosInput.add(atributo);
			}
		}
		
		Map<String, List<CampoSQLTarea>> tablas = new HashMap<String, List<CampoSQLTarea>>();
		List<String> nombreTablas = new ArrayList<String>();
	    for (int x=0;x<camposCompletar.size();x++){
	    	CampoSQLTarea atributo = camposCompletar.get(x);
	    	if(atributo!=null && atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
	    		 if(tablas.containsKey(atributo.getTabla().getNombre())){
	    			 tablas.get(atributo.getTabla().getNombre()).add(atributo);
	    		 }else{
	    			 List<CampoSQLTarea> atributos = new ArrayList<CampoSQLTarea>();
	    			 atributos.add(atributo);
	    			 tablas.put(atributo.getTabla().getNombre(), atributos);
	    			 nombreTablas.add(atributo.getTabla().getNombre());
	    		 }
	    	}
	    }
		for (int x=0;x<atributosInput.size();x++){
			CampoSQL atributo = atributosInput.get(x);
			String tablaTemp = atributo.getTabla().getNombre();
			String parametro = atributo.getNombre();
			String tipoparametro = atributo.getTipo();
			int longitud = atributo.getLongitud();
			int precision = atributo.getPrecision();
			
		    if (x==atributosInput.size()-1){
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
			    buffer.append("\t) \r\n");	
		    }else{
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
		    }
		}
		buffer.append("\tSPECIFIC " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_COMPLETAR\r\n");
		buffer.append("BEGIN\r\n");
		
	    for(int x=0; x<nombreTablas.size();x++){
	    	List<CampoSQLTarea> atributosTabla = tablas.get(nombreTablas.get(x));
	    	
	    	buffer.append("\t\t\tUPDATE "+esquema.toUpperCase()+"."+atributosTabla.get(0).getTabla().getNombre()+"\r\n");
	    	buffer.append("\t\t\t\t SET	");
	    	for(int y=0; y<atributosTabla.size(); y++){
	    		CampoSQLTarea atributo = atributosTabla.get(y);
	    		if(y!=0){
	    			buffer.append("\t\t\t\t\t\t");
	    		}
	    		buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre().toUpperCase()+"_"+atributo.getNombre().toUpperCase());
	    		if(y==(atributosTabla.size()-1)){
	    			buffer.append("\r\n");
	    		}else{
	    			buffer.append(",\r\n");
	    		}
	    	}
	    	buffer.append("\t\t\tWHERE ");
	    	int cantidadWhere = 0;
	    	for(int y=0; y<atributosPKs.size(); y++){
	    		CampoSQL atributo = atributosPKs.get(y);
	    		if(atributosTabla.get(0).getTabla().getNombre().equalsIgnoreCase(atributo.getTabla().getNombre())){
	    			if(cantidadWhere == 0){
	    				buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+" ");
	    				cantidadWhere++;
	    			}else{
			    		buffer.append("\r\n AND " + atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre());	
	    			}
		    		
	    		}
	    	}
	    	if (cantidadWhere>0) {
    			buffer.append(";\r\n");
			}
	    	
	    }
		buffer.append("END@ \r\n");
	
	 	return buffer.toString();
				
	}
	
	private static String spTareasContenidoArchivoDLLCompletarDrop(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("DROP PROCEDURE " + tarea.getAtributosCompletar().get(0).getCampoSQLTarea().getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_COMPLETAR@\r\n");
		buffer.append("DROP PROCEDURE " + tarea.getAtributosCompletar().get(0).getCampoSQLTarea().getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_CON_COMPLETAR@\r\n");
		
		return buffer.toString();
	}
	
	private static String spTareasContenidoArchivoDLLCancelar(Tarea tarea) throws Exception{
		
		List<CampoSQLTarea> camposCancelar = tarea.getCamposCancelar();
		String esquema = camposCancelar.get(0).getTabla().getEsquema();
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_CANCELAR(\r\n");    
		
		//sacamos todas las tablas que deben registrarse segun el proceso
		Map<String, String> mapTablas = new HashMap<String, String>();
		for (int i = 0; i < camposCancelar.size(); i++) {
			CampoSQLTarea atributo = camposCancelar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0 && mapTablas.containsKey(atributo.getTabla().getNombre())==false){
				mapTablas.put(atributo.getTabla().getNombre(), atributo.getTabla().getNombre());
			}
		}
		
		List<CampoSQLTarea> atributosInput = new ArrayList<CampoSQLTarea>();
		List<CampoSQLTarea> atributosPKs = new ArrayList<CampoSQLTarea>();
		Map<Integer, CampoSQLTarea> atributosPKMap = new HashMap<Integer, CampoSQLTarea>();
		for (int i = 0; i < camposCancelar.size(); i++) {
			CampoSQLTarea atributo = camposCancelar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				if(atributo.isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false && mapTablas.containsKey(atributo.getTabla().getNombre())){
					atributosPKMap.put(atributo.getCodigo(), atributo);
					atributosInput.add(atributo);
					atributosPKs.add(atributo);
				}
			}
		}
		
		if(atributosPKs.size()==0){
			throw new Exception("No se puede armar el Store Procedure de cancelación, por que en el excel de configuración de la tarea "+tarea.getCodigo()+" no se incluyo un PK, por favor ingrese un PK y vuelva a intentarlo...");
		}
		
		for (int i = 0; i < camposCancelar.size(); i++) {
			CampoSQLTarea atributo = camposCancelar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				if(atributosPKMap.containsKey(atributo.getCodigo())==false){
					atributosInput.add(atributo);
				}
			}
		}
		
		for (int x=0;x<atributosInput.size();x++){
			CampoSQLTarea atributo = atributosInput.get(x);
			String tablaTemp = atributo.getTabla().getNombre();
			String parametro = atributo.getNombre();
			String tipoparametro = atributo.getTipo();
			int longitud = atributo.getLongitud();
			int precision = atributo.getPrecision();
			
		    if (x==atributosInput.size()-1){
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
			    buffer.append("\t) \r\n");	
		    }else{
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
		    }
		}
		buffer.append("\tSPECIFIC " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_CANCELAR\r\n");
		buffer.append("BEGIN\r\n");
		    
		    
		Map<String, List<CampoSQLTarea>> tablas = new HashMap<String, List<CampoSQLTarea>>();
		List<String> nombreTablas = new ArrayList<String>();
	    for (int x=0;x<camposCancelar.size();x++){
	    	CampoSQLTarea atributo = camposCancelar.get(x);
	    	if(atributo!=null && atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
	    		 if(tablas.containsKey(atributo.getTabla().getNombre())){
	    			 tablas.get(atributo.getTabla().getNombre()).add(atributo);
	    		 }else{
	    			 List<CampoSQLTarea> atributos = new ArrayList<CampoSQLTarea>();
	    			 atributos.add(atributo);
	    			 tablas.put(atributo.getTabla().getNombre(), atributos);
	    			 nombreTablas.add(atributo.getTabla().getNombre());
	    		 }
	    	}
	    }
		
	    for(int x=0; x<nombreTablas.size();x++){
	    	List<CampoSQLTarea> atributosTabla = tablas.get(nombreTablas.get(x));
	    	
	    	
	    	buffer.append("\t\t\tUPDATE "+esquema.toUpperCase()+"."+atributosTabla.get(0).getTabla().getNombre()+"\r\n");
	    	buffer.append("\t\t\t\t SET	");
	    	for(int y=0; y<atributosTabla.size(); y++){
	    		CampoSQLTarea atributo = atributosTabla.get(y);
	    		if(y!=0){
	    			buffer.append("\t\t\t\t\t\t");
	    		}
	    		buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre().toUpperCase()+"_"+atributo.getNombre().toUpperCase());
	    		if(y==(atributosTabla.size()-1)){
	    			buffer.append("\r\n");
	    		}else{
	    			buffer.append(",\r\n");
	    		}
	    	}
	    	buffer.append("\t\t\tWHERE ");
	    	for(int y=0; y<atributosPKs.size(); y++){
	    		CampoSQLTarea atributo = atributosPKs.get(y);
	    		if(y==atributosPKs.size()-1){
	    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+";\r\n");
	    		}else{
	    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"\r\n");
	    		}
	    	}
	    	
	    }
		buffer.append("END@ \r\n");
	
	 	return buffer.toString();
				
	}
	
	private static String spTareasContenidoArchivoDLLCancelarDrop(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("DROP PROCEDURE " + tarea.getCamposCancelar().get(0).getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_CANCELAR@\r\n");
		
		return buffer.toString();
	}
	
	private static String spTareasContenidoArchivoDLLRechazar(Tarea tarea) throws Exception{
		
		List<CampoSQLTarea> camposRechazar = tarea.getCamposRechazar();
		String esquema = camposRechazar.get(0).getTabla().getEsquema();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_RECHAZAR(\r\n");    
		
		//sacamos todas las tablas que deben registrarse segun el proceso
		Map<String, String> mapTablas = new HashMap<String, String>();
		for (int i = 0; i < camposRechazar.size(); i++) {
			CampoSQLTarea atributo = camposRechazar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0 && mapTablas.containsKey(atributo.getTabla().getNombre())==false){
				mapTablas.put(atributo.getTabla().getNombre(), atributo.getTabla().getNombre());
			}
		}
		
		List<CampoSQLTarea> atributosInput = new ArrayList<CampoSQLTarea>();
		List<CampoSQLTarea> atributosPKs = new ArrayList<CampoSQLTarea>();
		Map<Integer, CampoSQLTarea> atributosPKMap = new HashMap<Integer, CampoSQLTarea>();
		for (int i = 0; i < camposRechazar.size(); i++) {
			CampoSQLTarea atributo = camposRechazar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				if(atributo.isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false && mapTablas.containsKey(atributo.getTabla().getNombre())){
					atributosPKMap.put(atributo.getCodigo(), atributo);
					atributosInput.add(atributo);
					atributosPKs.add(atributo);
				}
			}
		}
		
		if(atributosPKs.size()==0){
			throw new Exception("No se puede armar el Store Procedure de cancelación, por que en el excel de configuración de la tarea "+tarea.getCodigo()+" no se incluyo un PK, por favor ingrese un PK y vuelva a intentarlo...");
		}
		
		for (int i = 0; i < camposRechazar.size(); i++) {
			CampoSQLTarea atributo = camposRechazar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				if(atributosPKMap.containsKey(atributo.getCodigo())==false){
					atributosInput.add(atributo);
				}
			}
		}
		
		for (int x=0;x<atributosInput.size();x++){
			CampoSQLTarea atributo = atributosInput.get(x);
			String tablaTemp = atributo.getTabla().getNombre();
			String parametro = atributo.getNombre();
			String tipoparametro = atributo.getTipo();
			int longitud = atributo.getLongitud();
			int precision = atributo.getPrecision();
			
		    if (x==atributosInput.size()-1){
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
			    buffer.append("\t) \r\n");	
		    }else{
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
		    }
		}
		buffer.append("\tSPECIFIC " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_RECHAZAR\r\n");
		buffer.append("BEGIN\r\n");
		    
		    
		Map<String, List<CampoSQLTarea>> tablas = new HashMap<String, List<CampoSQLTarea>>();
		List<String> nombreTablas = new ArrayList<String>();
	    for (int x=0;x<camposRechazar.size();x++){
	    	CampoSQLTarea atributo = camposRechazar.get(x);
	    	if(atributo!=null && atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
	    		 if(tablas.containsKey(atributo.getTabla().getNombre())){
	    			 tablas.get(atributo.getTabla().getNombre()).add(atributo);
	    		 }else{
	    			 List<CampoSQLTarea> atributos = new ArrayList<CampoSQLTarea>();
	    			 atributos.add(atributo);
	    			 tablas.put(atributo.getTabla().getNombre(), atributos);
	    			 nombreTablas.add(atributo.getTabla().getNombre());
	    		 }
	    	}
	    }
		
	    for(int x=0; x<nombreTablas.size();x++){
	    	List<CampoSQLTarea> atributosTabla = tablas.get(nombreTablas.get(x));
	    	
	    	
	    	buffer.append("\t\t\tUPDATE "+esquema.toUpperCase()+"."+atributosTabla.get(0).getTabla().getNombre()+"\r\n");
	    	buffer.append("\t\t\t\t SET	");
	    	for(int y=0; y<atributosTabla.size(); y++){
	    		CampoSQLTarea atributo = atributosTabla.get(y);
	    		if(y!=0){
	    			buffer.append("\t\t\t\t\t\t");
	    		}
	    		buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre().toUpperCase()+"_"+atributo.getNombre().toUpperCase());
	    		if(y==(atributosTabla.size()-1)){
	    			buffer.append("\r\n");
	    		}else{
	    			buffer.append(",\r\n");
	    		}
	    	}
	    	buffer.append("\t\t\tWHERE ");
	    	for(int y=0; y<atributosPKs.size(); y++){
	    		CampoSQLTarea atributo = atributosPKs.get(y);
	    		if(y==atributosPKs.size()-1){
	    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+";\r\n");
	    		}else{
	    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"\r\n");
	    		}
	    	}
	    	
	    }
		buffer.append("END@ \r\n");
	
	 	return buffer.toString();
				
	}
	
	private static String spTareasContenidoArchivoDLLRechazarDrop(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("DROP PROCEDURE " + tarea.getCamposRechazar().get(0).getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_RECHAZAR@\r\n");
		
		return buffer.toString();
	}
	
	private static String spTareasContenidoArchivoDLLObservarDrop(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("DROP PROCEDURE " + tarea.getCamposObservar().get(0).getTabla().getEsquema().toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_OBSERVAR@\r\n");
		
		return buffer.toString();
	}
	
	private static String spTareasContenidoArchivoDLLObservar(Proyecto proyecto, Tarea tarea) throws Exception{
		
		List<CampoSQLTarea> camposObservar = tarea.getCamposObservar();
		String esquema = proyecto.getTablas().get(0).getEsquema();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_OBSERVAR(\r\n");    
		
		//sacamos todas las tablas que deben registrarse segun el proceso
		Map<String, String> mapTablas = new HashMap<String, String>();
		for (int i = 0; i < tarea.getAtributosObservar().size(); i++) {
			CampoSQLTarea atributo = camposObservar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0 && mapTablas.containsKey(atributo.getTabla().getNombre())==false){
				mapTablas.put(atributo.getTabla().getNombre(), atributo.getTabla().getNombre());
			}
		}
		
		List<CampoSQLTarea> atributosInput = new ArrayList<CampoSQLTarea>();
		List<CampoSQLTarea> atributosPKs = new ArrayList<CampoSQLTarea>();
		Map<Integer, CampoSQLTarea> atributosPKMap = new HashMap<Integer, CampoSQLTarea>();
		for (int i = 0; i < camposObservar.size(); i++) {
			CampoSQLTarea atributo = camposObservar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				if(atributo.isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false && mapTablas.containsKey(atributo.getTabla().getNombre())){
					atributosPKMap.put(atributo.getCodigo(), atributo);
					atributosInput.add(atributo);
					atributosPKs.add(atributo);
				}
			}
		}
		
		if(atributosPKs.size()==0){
			throw new Exception("No se puede armar el Store Procedure de cancelación, por que en el excel de configuración de la tarea "+tarea.getCodigo()+" no se incluyo un PK, por favor ingrese un PK y vuelva a intentarlo...");
		}
		
		for (int i = 0; i < camposObservar.size(); i++) {
			CampoSQLTarea atributo = camposObservar.get(i);
			if(atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
				if(atributosPKMap.containsKey(atributo.getCodigo())==false){
					atributosInput.add(atributo);
				}
			}
		}
		
		for (int x=0;x<atributosInput.size();x++){
			CampoSQLTarea atributo = atributosInput.get(x);
			String tablaTemp = atributo.getTabla().getNombre();
			String parametro = atributo.getNombre();
			String tipoparametro = atributo.getTipo();
			int longitud = atributo.getLongitud();
			int precision = atributo.getPrecision();
			
		    if (x==atributosInput.size()-1){
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+" \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+" \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+" \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
			    buffer.append("\t) \r\n");	
		    }else{
		    	if(tipoparametro.equalsIgnoreCase("VARCHAR")){
					buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
			    }else if(tipoparametro.equalsIgnoreCase("CHAR")){
				    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")"+", \r\n");	
				}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DATE")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("TIMESTAMP")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")"+", \r\n");
			    }else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+", \r\n");
			    }else{
			    	throw new Exception("No hay tipo de dato disponible, tipoparametro; "+tipoparametro+":: "+atributo.getCodigo());
			    }
		    }
		}
		buffer.append("\tSPECIFIC " + esquema.toUpperCase() + ".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_OBSERVAR\r\n");
		buffer.append("BEGIN\r\n");
		    
		    
		Map<String, List<CampoSQLTarea>> tablas = new HashMap<String, List<CampoSQLTarea>>();
		List<String> nombreTablas = new ArrayList<String>();
	    for (int x=0;x<camposObservar.size();x++){
	    	CampoSQLTarea atributo = camposObservar.get(x);
	    	if(atributo!=null && atributo.getTabla().getNombre()!=null && atributo.getTabla().getNombre().trim().length()>0){
	    		 if(tablas.containsKey(atributo.getTabla().getNombre())){
	    			 tablas.get(atributo.getTabla().getNombre()).add(atributo);
	    		 }else{
	    			 List<CampoSQLTarea> atributos = new ArrayList<CampoSQLTarea>();
	    			 atributos.add(atributo);
	    			 tablas.put(atributo.getTabla().getNombre(), atributos);
	    			 nombreTablas.add(atributo.getTabla().getNombre());
	    		 }
	    	}
	    }
		
	    for(int x=0; x<nombreTablas.size();x++){
	    	List<CampoSQLTarea> atributosTabla = tablas.get(nombreTablas.get(x));
	    	
	    	
	    	buffer.append("\t\t\tUPDATE "+esquema.toUpperCase()+"."+atributosTabla.get(0).getTabla().getNombre()+"\r\n");
	    	buffer.append("\t\t\t\t SET	");
	    	for(int y=0; y<atributosTabla.size(); y++){
	    		CampoSQLTarea atributo = atributosTabla.get(y);
	    		if(y!=0){
	    			buffer.append("\t\t\t\t\t\t");
	    		}
	    		buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre().toUpperCase()+"_"+atributo.getNombre().toUpperCase());
	    		if(y==(atributosTabla.size()-1)){
	    			buffer.append("\r\n");
	    		}else{
	    			buffer.append(",\r\n");
	    		}
	    	}
	    	buffer.append("\t\t\tWHERE ");
	    	for(int y=0; y<atributosPKs.size(); y++){
	    		CampoSQLTarea atributo = atributosPKs.get(y);
	    		if(y==atributosPKs.size()-1){
	    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+";\r\n");
	    		}else{
	    			buffer.append(atributo.getNombre()+" = PI_"+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"\r\n");
	    		}
	    	}
	    	
	    }
		buffer.append("END@ \r\n");
	
	 	return buffer.toString();
				
	}
	
	private static String crearInsertNexo_UnidadesNegocio(Proyecto proyecto, Configuracion configuracion, Connection conn){
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		
		List<UnidadNegocio> uns = proyecto.getUnidadesNegocio();
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<uns.size();i++){
			buffer.append("INSERT INTO BFP_PORTAL.BFPBM_UNIDAD_NEGOCIO(CODIGO_UNIDAD_NEGOCIO, DESCRIPCION, URL, VERSION, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) ");
			buffer.append("VALUES('"+uns.get(i).getSufijoBanca()+"', '"+uns.get(i).getDescripcion()+"', '"+uns.get(i).getSufijoBanca()+"', '"+uns.get(i).getVersion()+"', '1', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearRollbackNexo_UnidadesNegocio(Proyecto proyecto,Connection conn){
		List<UnidadNegocio> uns = proyecto.getUnidadesNegocio();
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<uns.size();i++){
			buffer.append("DELETE FROM BFP_PORTAL.BFPBM_UNIDAD_NEGOCIO WHERE CODIGO_UNIDAD_NEGOCIO = '"+uns.get(i).getSufijoBanca()+"'@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearInsertNexo_Productos(Proyecto proyecto, Configuracion configuracion){
		ProyectoDaoImpl proyectoDaoImpl = new ProyectoDaoImpl();
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		List<Producto> productos = proyectoDaoImpl.obtenerProductos(proyecto);
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<productos.size();i++){
			buffer.append("INSERT INTO BFP_PORTAL.BFPBM_PRODUCTO(CODIGO_UNIDAD_NEGOCIO,CODIGO_PRODUCTO, DESCRIPCION, URL, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) ");
			buffer.append("VALUES('"+productos.get(i).getUn().getSufijoBanca()+"', '"+productos.get(i).getSufijoProducto()+"', '"+productos.get(i).getDescripcion()+"', '"+productos.get(i).getUrl()+"', '1', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearRollbackNexo_Productos(Proyecto proyecto){
		List<Producto> productos = proyecto.getProductos();
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<productos.size();i++){
			buffer.append("DELETE FROM BFP_PORTAL.BFPBM_PRODUCTO WHERE CODIGO_UNIDAD_NEGOCIO = '"+productos.get(i).getUn().getSufijoBanca()+"' AND CODIGO_PRODUCTO = '"+productos.get(i).getSufijoProducto()+"'@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearInsertNexo_Procesos(Proyecto proyecto, Configuracion configuracion, Connection conn){
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		List<Proceso> procesos = proyecto.getProcesos();
		StringBuilder buffer = new StringBuilder();
		if (procesos != null){
			for(int i = 0;i<procesos.size();i++){
				buffer.append("INSERT INTO BFP_PORTAL.BFPBM_PROCESO(CODIGO_UNIDAD_NEGOCIO,CODIGO_PRODUCTO, CODIGO_PROCESO, DESCRIPCION, URL, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) ");
				buffer.append("VALUES('"+procesos.get(i).getSufijoBanca()+"', '"+procesos.get(i).getSufijoProducto()+"', '"+procesos.get(i).getSufijoProceso()+"', '"+procesos.get(i).getNombre()+"', '"+procesos.get(i).getNombrePlantilla()+"', '1', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
				buffer.append("\r\n");
			}
		}
		
		return buffer.toString();
	}
	
	private static String crearRollbackNexo_Procesos(Proyecto proyecto, Connection conn){
		List<Proceso> procesos = proyecto.getProcesos();
		StringBuilder buffer = new StringBuilder();
		if (procesos != null){
			for(int i = 0;i<procesos.size();i++){
				buffer.append("DELETE FROM BFP_PORTAL.BFPBM_PROCESO WHERE CODIGO_UNIDAD_NEGOCIO = '"+procesos.get(i).getSufijoBanca()+"' AND CODIGO_PRODUCTO = '"+procesos.get(i).getSufijoProducto()+"' AND CODIGO_PROCESO = '"+procesos.get(i).getSufijoProceso()+"'@");
				buffer.append("\r\n");
			}
		}
		
		return buffer.toString();
	}
	
	private static String crearInsertNexo_PerfilModuloWeb(Proyecto proyecto, Configuracion configuracion){
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		List<PerfilModulo> perfilesxModulo = proyecto.getPerfilesxModulo();
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<perfilesxModulo.size();i++){
			buffer.append("INSERT INTO BFP_PORTAL.BFPBM_PERFIL_MODULO_WEB(CODIGO_PERFIL, CODIGO_MODULO_WEB, INDICADOR_VISIBLE_DEFECTO, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) ");
			buffer.append("VALUES('"+perfilesxModulo.get(i).getCodigoPerfil()+"', '"+perfilesxModulo.get(i).getCodigoModulo()+"', '1', '1', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearRollbackNexo_PerfilModuloWeb(Proyecto proyecto){
		List<PerfilModulo> perfilesxModulo = proyecto.getPerfilesxModulo();
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<perfilesxModulo.size();i++){
			buffer.append("DELETE FROM BFP_PORTAL.BFPBM_PERFIL_MODULO_WEB WHERE CODIGO_PERFIL = '"+perfilesxModulo.get(i).getCodigoPerfil()+"' AND CODIGO_MODULO_WEB = '"+perfilesxModulo.get(i).getCodigoModulo()+"'@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearInsertNexo_PerfilProcesoModuloWeb(Proyecto proyecto, Configuracion configuracion, Connection connection){
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		ProcesoDaoImpl procesoDaoImpl = new ProcesoDaoImpl();
		List<PerfilModulo> perfilesxModulo = proyecto.getPerfilesxModulo();
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<perfilesxModulo.size();i++){
			Proceso proceso = procesoDaoImpl.obtenerProceso(perfilesxModulo.get(i).getCodigoProceso(), connection);
			buffer.append("INSERT INTO BFP_PORTAL.BFPBM_PERFIL_PROCESO_MODULO_WEB(CODIGO_PERFIL, CODIGO_UNIDAD_NEGOCIO, CODIGO_PRODUCTO, CODIGO_PROCESO, CODIGO_MODULO_WEB, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) ");
			buffer.append("VALUES('"+perfilesxModulo.get(i).getCodigoPerfil()+"', '"+proceso.getSufijoBanca()+"', '"+proceso.getSufijoProducto()+"', '"+proceso.getSufijoProceso()+"', '"+perfilesxModulo.get(i).getCodigoModulo()+"', '1', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearRollbackNexo_PerfilProcesoModuloWeb(Proyecto proyecto, Connection connection){
		ProcesoDaoImpl procesoDaoImpl = new ProcesoDaoImpl();
		List<PerfilModulo> perfilesxModulo = proyecto.getPerfilesxModulo();
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<perfilesxModulo.size();i++){
			Proceso proceso = procesoDaoImpl.obtenerProceso(perfilesxModulo.get(i).getCodigoProceso(), connection);
			buffer.append("DELETE FROM BFP_PORTAL.BFPBM_PERFIL_PROCESO_MODULO_WEB WHERE CODIGO_PERFIL = '"+perfilesxModulo.get(i).getCodigoPerfil()+"' AND CODIGO_UNIDAD_NEGOCIO = '"+proceso.getSufijoBanca()+"' AND CODIGO_PRODUCTO = '"+proceso.getSufijoProducto()+"' AND CODIGO_PROCESO = '"+proceso.getSufijoProceso()+"' AND CODIGO_MODULO_WEB = '"+perfilesxModulo.get(i).getCodigoModulo()+"'@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearInsertNexo_SubModuloWeb(Proyecto proyecto, Configuracion configuracion){
		ProyectoDaoImpl proyectoDaoImpl = new ProyectoDaoImpl();
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		List<SubModulo> subModulo = proyectoDaoImpl.obtenerSubModulos(proyecto);
		StringBuilder buffer = new StringBuilder();
		int orden = 1;
		String tipo = "";
		for(int i = 0;i<subModulo.size();i++){
	
			if(!tipo.equals(subModulo.get(i).getCodigoModulo())){
				tipo = subModulo.get(i).getCodigoModulo();
				orden = 1;
			}
			
			buffer.append("INSERT INTO BFP_PORTAL.BFPBM_SUB_MODULO_WEB(CODIGO_SUB_MODULO_WEB, CODIGO_MODULO_WEB, DESCRIPCION, URL, ORDEN, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) ");
			buffer.append("VALUES('"+subModulo.get(i).getCodigoSubModulo()+"', '"+subModulo.get(i).getCodigoModulo()+"', '"+subModulo.get(i).getDescipcion()+"', '"+subModulo.get(i).getCodigoSubModulo()+".jsp', '"+orden+"', '1', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
			buffer.append("\r\n");
			
			if(tipo.equals(subModulo.get(i).getCodigoModulo())){
				orden++;
			}
		}
		return buffer.toString();
	}
	
	private static String crearRollbackNexo_SubModuloWeb(Proyecto proyecto){
		ProyectoDaoImpl proyectoDaoImpl = new ProyectoDaoImpl();
		List<SubModulo> subModulo = proyectoDaoImpl.obtenerSubModulos(proyecto);
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<subModulo.size();i++){
			buffer.append("DELETE FROM BFP_PORTAL.BFPBM_SUB_MODULO_WEB WHERE CODIGO_SUB_MODULO_WEB = '"+subModulo.get(i).getCodigoSubModulo()+"'@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearInsertNexo_PerfilProductoSubModuloWeb(Proyecto proyecto, Configuracion configuracion){
		ProyectoDaoImpl proyectoDaoImpl = new ProyectoDaoImpl();
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		List<PerfilSubModuloProducto> PSMP = proyectoDaoImpl.obtenerPerfilSubModuloProductos(proyecto);
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<PSMP.size();i++){
			Producto producto = proyectoDaoImpl.obtenerProducto(PSMP.get(i).getProducto());
			buffer.append("INSERT INTO BFP_PORTAL.BFPBM_PERFIL_PRODUCTO_SUB_MODULO_WEB(CODIGO_PERFIL, CODIGO_PRODUCTO, CODIGO_UNIDAD_NEGOCIO, CODIGO_SUB_MODULO_WEB, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) ");
			buffer.append("VALUES('"+PSMP.get(i).getCodigoPerfil()+"', '"+producto.getSufijoProducto()+"', '"+producto.getUn().getSufijoBanca()+"', '"+PSMP.get(i).getCodigoSubModulo()+"', '1', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	private static String crearRollbackNexo_PerfilProductoSubModuloWeb(Proyecto proyecto, Configuracion configuracion){
		ProyectoDaoImpl proyectoDaoImpl = new ProyectoDaoImpl();
		List<PerfilSubModuloProducto> PSMP = proyectoDaoImpl.obtenerPerfilSubModuloProductos(proyecto);
		StringBuilder buffer = new StringBuilder();
		for(int i = 0;i<PSMP.size();i++){
			Producto producto = proyectoDaoImpl.obtenerProducto(PSMP.get(i).getProducto());
			buffer.append("DELETE FROM BFP_PORTAL.BFPBM_PERFIL_PRODUCTO_SUB_MODULO_WEB WHERE CODIGO_PERFIL = '"+PSMP.get(i).getCodigoPerfil()+"' AND CODIGO_PRODUCTO = '"+producto.getSufijoProducto()+"' AND CODIGO_UNIDAD_NEGOCIO = '"+producto.getUn().getSufijoBanca()+"' AND CODIGO_SUB_MODULO_WEB = '"+PSMP.get(i).getCodigoSubModulo()+"'@");
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	
	
	public static void crearInsertCatalogo(Proyecto proyecto, Configuracion configuracion, File directorioScriptCreate, File directorioScriptDrop, Connection conn) throws Exception{
		
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		List<Catalogo> catalogos =  proyecto.getCatalogos();
		Tabla tabla = proyecto.getTablas().get(0);
		String esquema = tabla.getEsquema();
		
		File archivo = new File(directorioScriptCreate.getAbsolutePath()+"\\","4_INSERT_CATALOGO.sql");
		if(archivo.exists()){
			archivo.delete();
		}
		archivo.createNewFile();

		BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo,true), "ISO-8859-1"));
		bufferedWriter.write("--<ScriptOptions statementTerminator=\"@\"/>\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE CATALOGOS");
		bufferedWriter.write("\r\n");
		
		for (int i = 0; i < catalogos.size(); i++) {
			Catalogo catalogo = catalogos.get(i);
			bufferedWriter.write("INSERT INTO "+esquema.toUpperCase()+".MAE_CATALOGO(COD_CATALOGO, COD_ATRIBUTO, VALOR_1, VALOR_2, DESCRIPCION, ESTADO, LIM_COD_ATRIBUTO, LIM_VALOR_1, LIM_VALOR_2, JERARQUIA,USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) VALUES ");
			
			String valor1 = null;
			String valor2 = null;
			int limiteCodAtributo = 0;
			int limiteValor1 = 0;
			int limiteValor2 = 0;
			String jerarquia = null;
			
			if(catalogo.isCabecera()){
				jerarquia = "1";
				limiteCodAtributo = catalogo.getLimiteCodAtributo();
				valor1 = (catalogo.getValor1().trim().length()!=0)?"'"+catalogo.getValor1()+"'":null;
				limiteValor1 = catalogo.getLimiteValor1();
				limiteValor2 = catalogo.getLimiteValor2();
			}else{
				jerarquia = "0";
				valor1 = (catalogo.getValor1().trim().length()!=0)?"'"+catalogo.getValor1()+"'":null;
				valor2 = (catalogo.getValor2().trim().length()!=0)?"'"+catalogo.getValor2()+"'":null;
			}
			bufferedWriter.write("('"+catalogo.getCodigoCatalogo()+"', '"+catalogo.getCodigoAtributo()+"', "+valor1+", "+valor2+", '"+catalogo.getDescripcion()+"', '1',  "+limiteCodAtributo+",  "+limiteValor1+",  "+limiteValor2+", '"+jerarquia+"', '"+usuarioCreacion+"', current_timestamp, '"+usuarioCreacion+"', current_timestamp)@");
			
			bufferedWriter.write("\r\n");
		}
		
		bufferedWriter.close();
		
		archivo = new File(directorioScriptDrop.getAbsolutePath()+"\\","4_DELETE_CATALOGO.sql");
		if(archivo.exists()){
			archivo.delete();
		}
		archivo.createNewFile();

		bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo,true), "ISO-8859-1"));
		bufferedWriter.write("--<ScriptOptions statementTerminator=\"@\"/>\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--ELIMINACION DEL REGISTRO DE CATALOGO");
		bufferedWriter.write("\r\n");
		
		for (int i = 0; i < catalogos.size(); i++) {
			Catalogo catalogo = catalogos.get(i);
			bufferedWriter.write("DELETE FROM "+esquema.toUpperCase()+".MAE_CATALOGO WHERE COD_CATALOGO = '"+catalogo.getCodigoCatalogo()+"' AND COD_ATRIBUTO = '"+catalogo.getCodigoAtributo()+"'@");
			bufferedWriter.write("\r\n");
		}
		
		bufferedWriter.close();
		
	}
	public static void crearInsert(Proyecto proyecto, Configuracion configuracion, File directorioScriptCreate, File directorioScriptDrop, Connection connection) throws Exception{
		
		List<Tarea> tareas =  proyecto.getTareas();
		String usuarioCreacion = configuracion.getUsuarioCreacion();
		List<Perfil> perfiles = proyecto.getPerfiles();
		
		File archivo = new File(directorioScriptCreate.getAbsolutePath()+"\\","3_INSERT_NEXO.sql");
		if(archivo.exists()){
			archivo.delete();
		}
		archivo.createNewFile();
		
		
		BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo,true), "ISO-8859-1"));
		bufferedWriter.write("--<ScriptOptions statementTerminator=\"@\"/>\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE LAS UNIDADES DE NEGOCIO");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(crearInsertNexo_UnidadesNegocio(proyecto, configuracion, connection));
		bufferedWriter.write("\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE LOS PRODUCTOS");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(crearInsertNexo_Productos(proyecto, configuracion));
		bufferedWriter.write("\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE LOS PROCESOS");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(crearInsertNexo_Procesos(proyecto,configuracion, connection));
		bufferedWriter.write("\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE LAS TAREAS");
		bufferedWriter.write("\r\n");
		
		//Registra las tareas
		for (int i = 0; i < tareas.size(); i++) {
			Tarea tarea = tareas.get(i);
			Proceso proceso = tarea.getProceso();
			bufferedWriter.write("INSERT INTO BFP_PORTAL.BFPBM_TAREA(CODIGO_UNIDAD_NEGOCIO, CODIGO_PRODUCTO, CODIGO_PROCESO, CODIGO_TAREA, DESCRIPCION, URL, VERSION, TIEMPO_ROJO, TIEMPO_AMARILLO, NAMESPACE, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) VALUES(");
			bufferedWriter.write("'"+proceso.getSufijoBanca()+"', ");
			bufferedWriter.write("'"+proceso.getSufijoProducto()+"', ");
			bufferedWriter.write("'"+proceso.getSufijoProceso()+"', ");
			bufferedWriter.write("'"+tarea.getPlantilla()+"', ");
			bufferedWriter.write("'"+tarea.getNombre()+"', ");
			bufferedWriter.write("'"+tarea.getClase()+".jsp', ");
			bufferedWriter.write("'"+tarea.getVersion()+"', ");
			bufferedWriter.write(""+tarea.getTiempoRojo()+", ");
			bufferedWriter.write(""+tarea.getTiempoAmarillo()+", ");
			bufferedWriter.write("' ', ");
			bufferedWriter.write("'1', ");
			bufferedWriter.write("'"+usuarioCreacion+"', ");
			bufferedWriter.write("current_timestamp, ");
			bufferedWriter.write("'"+usuarioCreacion+"', ");
			bufferedWriter.write("current_timestamp)@\r\n");
		}
		
		bufferedWriter.write("\r\n");
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE LOS PERFILES");
		bufferedWriter.write("\r\n");

		for (int i = 0; i < perfiles.size(); i++) {
			Perfil perfil = perfiles.get(i);
			bufferedWriter.write("INSERT INTO BFP_PORTAL.BFPBM_PERFIL(CODIGO_PERFIL, DESCRIPCION, ESTADO, USUARIO_CREACION, FECHA_CREACION, USUARIO_ACTUALIZACION, FECHA_ACTUALIZACION) VALUES( ");
			bufferedWriter.write("'"+perfil.getCodigoPerfil()+"', ");
			bufferedWriter.write("'"+perfil.getNombre()+"', ");
			bufferedWriter.write("'1', ");
			bufferedWriter.write("'BPMUSER', ");
			bufferedWriter.write("current_timestamp, ");
			bufferedWriter.write("'BPMUSER', ");
			bufferedWriter.write("current_timestamp)@\r\n");
		}
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE PERFIL POR MODULO WEB");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(crearInsertNexo_PerfilModuloWeb(proyecto, configuracion));
		bufferedWriter.write("\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE PERFIL PROCESO MODULO WEB");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(crearInsertNexo_PerfilProcesoModuloWeb(proyecto, configuracion, connection));
		bufferedWriter.write("\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE SUB MODULOS WEB");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(crearInsertNexo_SubModuloWeb(proyecto, configuracion));
		bufferedWriter.write("\r\n");
		
		bufferedWriter.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriter.write("--REGISTRO DE PERFIL PRODUCTO SUB MODULOS WEB");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(crearInsertNexo_PerfilProductoSubModuloWeb(proyecto, configuracion));
		bufferedWriter.write("\r\n");
		
		bufferedWriter.close();
		
		
		File archivoDrop = new File(directorioScriptDrop.getAbsolutePath()+"\\","1_DELETE_NEXO.sql");
		if(archivoDrop.exists()){
			archivoDrop.delete();
		}
		
		archivoDrop.createNewFile();
		
		BufferedWriter bufferedWriterDrop =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoDrop,true), "ISO-8859-1"));
		bufferedWriterDrop.write("--<ScriptOptions statementTerminator=\"@\"/>\r\n\r\n");
		
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR DE PERFIL PRODUCTO SUB MODULOS WEB");
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write(crearRollbackNexo_PerfilProductoSubModuloWeb(proyecto, configuracion));
		bufferedWriterDrop.write("\r\n");
		
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR DE SUB MODULOS WEB");
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write(crearRollbackNexo_SubModuloWeb(proyecto));
		bufferedWriterDrop.write("\r\n");
		
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR DE PERFIL PROCESO MODULO WEB");
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write(crearRollbackNexo_PerfilProcesoModuloWeb(proyecto, connection));
		bufferedWriterDrop.write("\r\n");
		
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR DE PERFIL POR MODULO WEB");
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write(crearRollbackNexo_PerfilModuloWeb(proyecto));
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR REGISTRO DE PERFILES");
		bufferedWriterDrop.write("\r\n");
		
		for (int i = 0; i < perfiles.size(); i++) {
			Perfil perfil = perfiles.get(i);
			bufferedWriterDrop.write("DELETE FROM BFP_PORTAL.BFPBM_PERFIL WHERE ");
			bufferedWriterDrop.write("CODIGO_PERFIL = '"+perfil.getCodigoPerfil()+"'@\r\n ");
		}
		
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR REGISTRO DE TAREAS");
		bufferedWriterDrop.write("\r\n");
		
		for (int i = 0; i < tareas.size(); i++) {
			Tarea tarea = tareas.get(i);
			Proceso proceso = tarea.getProceso();
			bufferedWriterDrop.write("DELETE FROM BFP_PORTAL.BFPBM_TAREA WHERE ");
			bufferedWriterDrop.write("CODIGO_UNIDAD_NEGOCIO = '"+proceso.getSufijoBanca()+"' AND ");
			bufferedWriterDrop.write("CODIGO_PRODUCTO = '"+proceso.getSufijoProducto()+"' AND ");
			bufferedWriterDrop.write("CODIGO_PROCESO = '"+proceso.getSufijoProceso()+"' AND ");
			bufferedWriterDrop.write("CODIGO_TAREA = '"+tarea.getPlantilla()+"'@\r\n");
		}
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR REGISTRO DE LOS PROCESOS");
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write(crearRollbackNexo_Procesos(proyecto, connection));
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR REGISTRO DE LOS PRODUCTOS");
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write(crearRollbackNexo_Productos(proyecto));
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write("--------------------------------------------------------------------------------\r\n");
		bufferedWriterDrop.write("--ELIMINAR REGISTRO DE LAS UNIDADES DE NEGOCIO");
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.write(crearRollbackNexo_UnidadesNegocio(proyecto, connection));
		bufferedWriterDrop.write("\r\n");
		bufferedWriterDrop.close();
	}
	
	public static String spConsultasTareaCreate(Proyecto proyecto, Connection conn) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		
		
		List<Tarea> tareas = proyecto.getTareas();
		
		for (int x= 0; x< tareas.size(); x++) {
			Consulta consulta = tareas.get(x).getConsultaCompletar();
			
			if(consulta.getTablaPadre()==null){
				continue;
			}
			
			boolean uno = false;
			boolean muchos = false;
			boolean mixto = false;
			
			TablaConsulta tablaPadre = consulta.getTablaPadre();
			List<TablaConsulta> tablasFK = consulta.getTablasFK();
			
			for(int y=0; tablasFK!=null && y<tablasFK.size(); y++){
				if(tablasFK.get(y).isFlgUnoMuchos()){
					muchos = true;
				}else{
					uno = true;
				}
			}
			
			if(uno && muchos){
				mixto = true;
			}
			
			sb.append("\r\n\r\n");
			if(tablasFK==null || tablasFK.size()==0){
				sb.append(spConsultasTareaContenidoArchivoDLL_uno(consulta, tablaPadre));
			}else{
				if(mixto){
					for(int u=0; u<tablasFK.size() ;u++){
						if(tablasFK.get(u).isFlgUnoMuchos()){
							sb.append(spConsultasContenidoArchivoDLL_multiple(consulta, tablaPadre, tablasFK.get(u))+"\r\n\r\n\r\n");
						}
					}
					sb.append(spConsultasTareaContenidoArchivoDLL_uno(consulta, tablaPadre));
				}else if(uno){
					sb.append(spConsultasTareaContenidoArchivoDLL_uno(consulta, tablaPadre));
				}else if(muchos){
					for(int u=0; u<tablasFK.size() ;u++){
						if(tablasFK.get(u).isFlgUnoMuchos()){
							sb.append(spConsultasContenidoArchivoDLL_multiple(consulta, tablaPadre, tablasFK.get(u))+"\r\n\r\n\r\n");
						}
					}
					sb.append(spConsultasTareaContenidoArchivoDLL_uno(consulta, tablaPadre));
				}
			}
		}
		return sb.toString();
	}
	
	private static String spConsultasTareaContenidoArchivoDLL_uno(Consulta consulta, TablaConsulta tabla){

		TablaConsulta tablaPadre = consulta.getTablaPadre();
		List<TablaConsulta> tablasFK = consulta.getTablasFK();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE OR REPLACE PROCEDURE "+tabla.getEsquema()+".SP_PORTAL_TAR_"+consulta.getAleas().toUpperCase()+" (\r\n");
		buffer.append("\t\tIN PI_PIID	VARCHAR(50)\r\n" );	
		
		int limiteA = tabla.getCampoSQLConsultas().size()-1;
		List<CampoSQLConsulta> atributosPK = new ArrayList<CampoSQLConsulta>();
		for(int x=0;x<tabla.getCampoSQLConsultas().size();x++){
			if(tabla.getCampoSQLConsultas().get(x).isFlgPK()){
				atributosPK.add(tabla.getCampoSQLConsultas().get(x));
			}
		}
		
		buffer.append(")SPECIFIC "+tabla.getEsquema()+".SP_PORTAL_TAR_"+consulta.getAleas().toUpperCase()+"\r\n");
		buffer.append("DYNAMIC RESULT SETS 1 \r\n");
		buffer.append("LANGUAGE SQL \r\n");
		buffer.append("BEGIN \r\n\r\n");
		buffer.append("\tDECLARE result_set_1 CURSOR WITH RETURN TO CLIENT FOR\r\n");
		buffer.append("\t\tSELECT \r\n");
		
		List<CampoSQLConsulta> atributosIncluidos = consulta.getTablaPadre().getCampoSQLConsultas();
		
		limiteA = atributosIncluidos.size()-1;
		for(int x=0;x<atributosIncluidos.size();x++){
			CampoSQLConsulta atributo = atributosIncluidos.get(x);
			if(x>0){
				buffer.append(",\r\n");
			}
			if(atributo.isTieneFuncion()){				
				if(x==limiteA){
					buffer.append("\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo());
				}else{
					buffer.append("\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo());
				}
			}else{
				if(x==limiteA){
					buffer.append("\t\t\tT_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre());
				}else{
					buffer.append("\t\t\tT_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre());
				}
			}
		}
		
		List<TablaConsulta> tablasFKConsulta = consulta.getTablasFK();
		for (int i = 0; i < tablasFKConsulta.size(); i++) {
			atributosIncluidos = tablasFKConsulta.get(i).getCampoSQLConsultas();
			
			limiteA = atributosIncluidos.size()-1;
			for(int x=0;x<atributosIncluidos.size();x++){
				CampoSQLConsulta atributo = atributosIncluidos.get(x);
				buffer.append(",");
				if(atributo.isTieneFuncion()){				
					if(x==limiteA){
						buffer.append("\r\n"+"\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo());
					}else{
						buffer.append("\r\n"+"\t\t\t"+atributo.getFuncionBusqueda()+"('"+atributo.getFuncionBusquedaCatalogo()+"', T_"+atributo.getTabla().getNombre()+"."+atributo.getNombre()+") AS "+atributo.getTabla().getNombre()+"_"+atributo.getNombre()+"_"+atributo.getFuncionBusquedaCatalogo());
					}
				}else{
					if(x==limiteA){
						buffer.append("\r\n"+"\t\t\t"+"T_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre());
					}else{
						buffer.append("\r\n"+"\t\t\t"+"T_"+atributosIncluidos.get(x).getTabla().getNombre()+"."+atributosIncluidos.get(x).getNombre()+" AS "+atributosIncluidos.get(x).getTabla().getNombre()+"_"+atributosIncluidos.get(x).getNombre());
					}
				}
			}
		}
		
		buffer.append("\t\t"+"\r\n"+"\t\tFROM "+tablaPadre.getEsquema()+"."+tablaPadre.getNombre()+" T_"+tablaPadre.getNombre()+" \r\n");
		if(tablasFK.size()>0){
			for(int x=0;x<tablasFK.size();x++){
				TablaConsulta tablaFK = tablasFK.get(x);
				buffer.append("\t\tLEFT JOIN "+tablaFK.getEsquema()+"."+tablaFK.getNombre()+" T_"+tablaFK.getNombre()+" \r\n");
				
				List<CampoSQLConsulta> camposSQLFK = tablaFK.getCampoSQLConsultas();
				int z=0;
				for(int y=0;y<camposSQLFK.size();y++){
					CampoSQLConsulta campoSQLConsulta = camposSQLFK.get(y);
					if(campoSQLConsulta.isFlgTieneFK()){
						if(z==0){
							buffer.append("\t\t\tON T_"+campoSQLConsulta.getTabla().getNombre()+"."+campoSQLConsulta.getNombre()+" = T_"+tablaPadre.getNombre()+"."+campoSQLConsulta.getFk().getNombre()+" \r\n");
						}else{
							buffer.append("\t\t\tAND T_"+campoSQLConsulta.getTabla().getNombre()+"."+campoSQLConsulta.getNombre()+" = T_"+tablaPadre.getNombre()+"."+campoSQLConsulta.getFk().getNombre()+" \r\n");
						}
						z++;
					}
				}	
			}
		}
		buffer.append("\t\tWHERE T_"+tablaPadre.getNombre()+".PIID = PI_PIID");
		
		buffer.append(";\r\n");
		buffer.append("\tOPEN result_set_1;\r\n");
		buffer.append("END@");
		return buffer.toString();
	}
	
}