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

import com.carga.mapeo.dao.impl.ConsultaDaoImpl;
import com.carga.portal.modelo.Atributo;
import com.carga.portal.modelo.AtributoConsulta;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Consulta;
import com.carga.portal.modelo.ConsultaTareaCompletar;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.TablaConsulta;
import com.carga.portal.servicio.crear.util.Validaciones;

public class CrearDao {

	public void crear(Proyecto proyecto, Configuracion configuracion, Connection connection) throws Exception{
		ConsultaDaoImpl consultaDaoImpl = new ConsultaDaoImpl();
		List<Consulta> consultas = consultaDaoImpl.obtenerConsultas(proyecto, connection);
		for (int i = 0; i < consultas.size(); i++) {
			escribirConsulta(proyecto, configuracion, consultas.get(i), connection);
		}
	}
	
	private void escribirConsulta(Proyecto proyecto, Configuracion configuracion, Consulta consulta, Connection connection) throws Exception{
		
		File directorioDao = new File(configuracion.getDirectorioEJB().getAbsoluteFile()+"\\autogenerado\\"+(consulta.getPaquete()).replaceAll("\\.", "/"));
		if(directorioDao.exists()==false){
			directorioDao.mkdirs();
		}
		
		File directorioDaoImpl = new File(directorioDao.getAbsoluteFile()+"\\impl\\");
		if(directorioDaoImpl.exists()==false){
			directorioDaoImpl.mkdirs();
		}
		
			
		File archivoDao = new File(directorioDao.getAbsolutePath()+"\\",""+consulta.getInterfase()+".java");
		if(archivoDao.exists()){
			archivoDao.delete();
		}
		archivoDao.createNewFile();
		
		File archivoDaoImpl = new File(directorioDaoImpl.getAbsolutePath()+"\\",""+consulta.getInterfase()+"Impl.java");
		if(archivoDaoImpl.exists()){
			archivoDaoImpl.delete();
		}
		archivoDaoImpl.createNewFile();
		
		boolean uno = false;
		boolean muchos = false;
		boolean mixto = false;
		for(int y=0; y<consulta.getTablasFK().size(); y++){
			if(consulta.getTablasFK().get(y).isFlgUnoMuchos()){
				muchos = true;
			}else{
				uno = true;
			}
		}
		
		BufferedWriter bufferedWriterDao =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoDao,true), "ISO-8859-1"));
		BufferedWriter bufferedWriterDaoImpl =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoDaoImpl,true), "ISO-8859-1"));
		
		if(consulta.getTablasFK().size()==0){
			bufferedWriterDao.write(contenidoArchivoDao_simple(consulta));
			bufferedWriterDaoImpl.write(contenidoArchivoDaoImpl_uno(consulta)+"}\r\n");
		}else{
			if(uno && muchos){
				mixto = true;
			}
			
			if(mixto){
				for(int u=0; u<consulta.getTablasFK().size() ;u++){
					if(consulta.getTablasFK().get(u).isFlgUnoMuchos()){
						//bufferedWriterDaoImpl.write(contenidoArchivoDaoImpl_simpleMultiple(tabla));
					}
				}
				bufferedWriterDao.write(contenidoArchivoDao_uno(consulta));
				bufferedWriterDaoImpl.write(contenidoArchivoDaoImpl_mixto(consulta));
			}else if(uno){
				bufferedWriterDao.write(contenidoArchivoDao_uno(consulta));
				bufferedWriterDaoImpl.write(contenidoArchivoDaoImpl_uno(consulta));
			}else if(muchos){
				bufferedWriterDao.write(contenidoArchivoDao_simple(consulta));
				bufferedWriterDaoImpl.write(contenidoArchivoDaoImpl_simpleMultiple(consulta));
			}
		}
		bufferedWriterDao.close();
		bufferedWriterDaoImpl.close();
		
	}	
	
	public String obtenerInterfaseDao(Consulta consulta, String nombreMetodo, Connection conn) throws Exception{
		/**************************************************************************************************************/
		Consulta tabla = consulta;
		
		if(tabla!=null && tabla.getTablaPadre()!=null){
			boolean uno = false;
			boolean muchos = false;
			boolean mixto = false;
			for(int y=0; y<tabla.getTablasFK().size(); y++){
				if(tabla.getTablasFK().get(y).isFlgUnoMuchos()){
					muchos = true;
				}else{
					uno = true;
				}
			}
			
			if(tabla.getTablasFK().size()==0){
				return contenidoArchivoEJBDao_simple(tabla, nombreMetodo);
			}else{
				if(uno && muchos){
					mixto = true;
				}
				
				if(mixto){
					return contenidoArchivoEJBDao_uno(tabla, nombreMetodo);
				}else if(uno){
					return contenidoArchivoEJBDao_uno(tabla, nombreMetodo);
				}else if(muchos){
					return contenidoArchivoEJBDao_simple(tabla, nombreMetodo);
				}
			}
		}else{
			throw new Exception("La consulta configurada "+consulta.getCodigo()+" no existe...");
		}
		return null;
	}

	public String obtenerClaseDaoImpl(Consulta consulta, String nombreMetodo, Connection conn) throws Exception{
		Consulta tabla = consulta;
		if(tabla!=null){
			boolean uno = false;
			boolean muchos = false;
			boolean mixto = false;
			for(int y=0; y<tabla.getTablasFK().size(); y++){
				if(tabla.getTablasFK().get(y).isFlgUnoMuchos()){
					muchos = true;
				}else{
					uno = true;
				}
			}
			
			if(tabla.getTablasFK().size()==0){
				return contenidoArchivoEJBDaoImpl_uno(tabla, nombreMetodo);
			}else{
				if(uno && muchos){
					mixto = true;
				}
				
				if(mixto){
					return contenidoArchivoEJBDaoImpl_mixto(tabla, nombreMetodo, conn);
				}else if(uno){
					return contenidoArchivoEJBDaoImpl_uno(tabla, nombreMetodo);
				}else if(muchos){
					return contenidoArchivoEJBDaoImpl_simpleMultiple(tabla, nombreMetodo);
				}
			}
			
		}else{
			throw new Exception("La consulta configurada "+consulta.getCodigo()+" no existe...");
		}
		
		return null;
		
	}
	
	private String contenidoArchivoDao_simple(Consulta tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tabla.getPaquete()+";\r\n\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		int i = 0;
		List<AtributoConsulta> camposSQL = tabla.getTablaPadre().getAtributosConsulta();
		for (int x = 0; x < camposSQL.size(); x++) {	
			String paquete = camposSQL.get(x).getClase().getJavaPaquete();
			String clase = camposSQL.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, camposSQL.get(x));
				buffer.append("import "+paquete+"."+clase+";\r\n");
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		buffer.append("\r\n");
		buffer.append("public interface "+tabla.getInterfase()+" {\r\n\r\n");
		
		String nombreClase = tabla.getTablaPadre().getAtributosConsulta().get(0).getClase().getJavaClase();
		buffer.append("\t"+nombreClase+" obtener"+nombreClase+"("+parametros.toString()+");\r\n\r\n");
		
		buffer.append("}");
		
		return buffer.toString();
	}

	private String contenidoArchivoEJBDao_simple(Consulta tabla, String nombreMetodo){
		StringBuffer buffer = new StringBuffer();
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		List<AtributoConsulta> camposSQL = tabla.getTablaPadre().getAtributosConsulta();
		if(camposSQL.size()>0){	
			String paquete = camposSQL.get(0).getClase().getJavaPaquete();
			String clase = camposSQL.get(0).getClase().getJavaClase();
			map.put(paquete+clase, camposSQL.get(0));
			parametros.append(clase+" "+clase.toLowerCase());
			buffer.append(camposSQL.get(0).getClase().getJavaClase()+" "+nombreMetodo+"("+clase+" "+clase.toLowerCase()+") throws Exception;");
		}
		
		return buffer.toString();
	}
		
	private String contenidoArchivoDao_uno(Consulta tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tabla.getPaquete()+";\r\n\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		List<AtributoConsulta> camposSQL = tabla.getTablaPadre().getAtributosConsulta();
		for (int x = 0; x < camposSQL.size(); x++) {
			String paquete = camposSQL.get(x).getClase().getJavaPaquete();
			String clase = camposSQL.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, camposSQL.get(x));
				buffer.append("import "+paquete+"."+clase+";\r\n");
			}
		}
		
		int i = 0;
		for (int x = 0; x < camposSQL.size(); x++) {
			AtributoConsulta atributo = camposSQL.get(x);
			String clase = atributo.getClase().getJavaClase();
			if(atributo.getCampoSQL().isFlgCondicion()){
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		
		buffer.append("\r\n");
		buffer.append("public interface "+tabla.getInterfase()+" {\r\n\r\n");
		String nombreClase = tabla.getTablaPadre().getAtributosConsulta().get(0).getClase().getJavaClase();
		buffer.append("\t"+nombreClase+" obtener"+nombreClase+"("+parametros.toString()+");\r\n\r\n");
		
		buffer.append("}");
		
		return buffer.toString();
	}

	private String contenidoArchivoEJBDao_uno(Consulta tabla, String nombreMetodo){
		StringBuffer buffer = new StringBuffer();
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		List<AtributoConsulta> camposSQL = tabla.getTablaPadre().getAtributosConsulta();
		for (int x = 0; x < camposSQL.size(); x++) {
			String paquete = camposSQL.get(x).getClase().getJavaPaquete();
			String clase = camposSQL.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, camposSQL.get(x));
			}
		}
		
		int i = 0;
		for (int x = 0; x < camposSQL.size(); x++) {
			AtributoConsulta atributo = camposSQL.get(x);
			String clase = atributo.getClase().getJavaClase();
			if(atributo.getCampoSQL()!=null && atributo.getCampoSQL().isFlgCondicion()){
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		String nombreClase = tabla.getTablaPadre().getAtributosConsulta().get(0).getClase().getJavaClase();
		buffer.append(nombreClase+" "+nombreMetodo+"("+nombreClase+" "+nombreClase.toLowerCase()+") throws Exception;");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoDaoImpl_uno(Consulta tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tabla.getPaquete()+".impl;\r\n\r\n");

		buffer.append("import java.util.List;\r\n");
		buffer.append("import java.sql.ResultSet;\r\n");
		buffer.append("import java.sql.SQLException;\r\n\r\n");

		buffer.append("import org.springframework.jdbc.core.RowMapper;\r\n\r\n");

		buffer.append("import "+tabla.getPaquete()+"."+tabla.getInterfase()+";\r\n");
		buffer.append("import pe.com.financiero.bpm.core.dao.impl.BaseDaoImpl;\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		List<AtributoConsulta> camposSQL = tabla.getTablaPadre().getAtributosConsulta();
		for (int x = 0; x < camposSQL.size(); x++) {

			String paquete = camposSQL.get(x).getClase().getJavaPaquete();
			String clase = camposSQL.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, camposSQL.get(x));
				buffer.append("import "+paquete+"."+clase+";\r\n");
			}
		}
		
		int i = 0;
		for (int x = 0; x < camposSQL.size(); x++) {
			AtributoConsulta atributo = camposSQL.get(x);
			String clase = atributo.getClase().getJavaClase();
			if(atributo.getCampoSQL().isFlgCondicion()){
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		StringBuffer parametrosFK = new StringBuffer();
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			for(int y=0; y<tablaFK.getCamposSQL().size(); y++){
				String paquete = tablaFK.getAtributosConsulta().get(x).getClase().getJavaPaquete();
				String clase = tablaFK.getAtributosConsulta().get(x).getClase().getJavaClase();
				if(map.containsKey(paquete+clase)==false){
					map.put(paquete+clase, camposSQL.get(x));
					buffer.append("import "+paquete+"."+clase+";\r\n");
					if(i!=0){
						parametrosFK.append(", ");
					}
					parametrosFK.append(clase+" "+clase.toLowerCase());
					i++;
				}
			}
		}
		
		buffer.append("\r\n");
		buffer.append("public class "+tabla.getInterfase()+"Impl extends BaseDaoImpl implements "+tabla.getInterfase()+"  {\r\n\r\n");
		
		List<Atributo> atributosPK = new ArrayList<Atributo>();
		for(int x=0;x<camposSQL.size();x++){
			if(camposSQL.get(x).getCampoSQL().isFlgPK()){
				atributosPK.add(camposSQL.get(x));
			}
		}
		StringBuffer pkAleas = new StringBuffer();
		StringBuffer pkValores = new StringBuffer();
		int a = 0;
		for(int x=0;x<camposSQL.size();x++){
			AtributoConsulta atributo = camposSQL.get(x);
			if(atributo.getCampoSQL().isFlgCondicion()){
				if(0==a){
					pkAleas.append("?");
					pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}else{
					pkAleas.append(", ?");
					pkValores.append(", "+atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}
				a++;
			}
		}
		
		buffer.append("\tpublic "+camposSQL.get(0).getClase().getJavaClase()+" obtener"+camposSQL.get(0).getClase().getJavaClase()+"("+parametros.toString()+"){\r\n");
		buffer.append("\t\tList<"+camposSQL.get(0).getClase().getJavaClase()+"> "+camposSQL.get(0).getClase().getJavaClase().toLowerCase()+"s = getJdbcTemplate().query( \"call "+camposSQL.get(0).getCampoSQL().getTabla().getEsquema()+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_CON_VALOR("+pkAleas.toString()+")\", new Object[] { "+pkValores.toString()+" }, new "+camposSQL.get(0).getClase().getJavaClase()+"Mapper());\r\n");
		buffer.append("\t\tif("+camposSQL.get(0).getClase().getJavaClase().toLowerCase()+"s!=null && "+camposSQL.get(0).getClase().getJavaClase().toLowerCase()+"s.size()>0){\r\n");
		buffer.append("\t\t\treturn "+camposSQL.get(0).getClase().getJavaClase().toLowerCase()+"s.get(0);\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tprotected static final class "+camposSQL.get(0).getClase().getJavaClase()+"Mapper implements RowMapper<"+camposSQL.get(0).getClase().getJavaClase()+"> {\r\n");
		buffer.append("\t\tpublic "+camposSQL.get(0).getClase().getJavaClase()+" mapRow(ResultSet rs, int rowNum) throws SQLException {\r\n");
		buffer.append("\t\t\t"+camposSQL.get(0).getClase().getJavaClase()+" "+camposSQL.get(0).getClase().getJavaClase().toLowerCase()+" = new "+camposSQL.get(0).getClase().getJavaClase()+"();\r\n");
		
		for(int x=0; x<tabla.getTablaPadre().getAtributosConsulta().size();x++){
			AtributoConsulta atributo = camposSQL.get(x);
			buffer.append(escribirAtributo(tabla.getTablaPadre(), atributo));
		}
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			String nombreClase = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
			buffer.append("\t\t\t"+nombreClase+" "+nombreClase.toLowerCase()+" = new "+nombreClase+"();\r\n");
			for(int y=0; y<tablaFK.getAtributosConsulta().size(); y++){
				AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(y);
				buffer.append(escribirAtributo(tablaFK, atributo));
			}
			buffer.append("\t\t\t"+camposSQL.get(0).getClase().getJavaClase().toLowerCase()+".set"+nombreClase+"("+nombreClase.toLowerCase()+");\r\n");
		}
		
		buffer.append("\t\t\treturn "+camposSQL.get(0).getClase().getJavaClase().toLowerCase()+";\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n");
		
		buffer.append("}\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoEJBDaoImpl_uno(Object consulta, String nombreMetodo){
		StringBuffer buffer = new StringBuffer();
		
		Consulta tabla = (Consulta)consulta; 
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		List<AtributoConsulta> atributosPadre = tabla.getTablaPadre().getAtributosConsulta();
		
		for (int x = 0; x < atributosPadre.size(); x++){
			String paquete = atributosPadre.get(x).getClase().getJavaPaquete();
			String clase = atributosPadre.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributosPadre.get(x));
			}
		}
		
		int i = 0;
		Map<String, String> clasesMetodoMap = new HashMap<String, String>();
		for (int x = 0; x < atributosPadre.size(); x++) {
			AtributoConsulta atributo = atributosPadre.get(x);
			String clase = atributo.getClase().getJavaClase();
			if(atributo.getCampoSQL()!=null && clasesMetodoMap.containsKey(clase)==false && atributo.getCampoSQL().isFlgCondicion()){
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
				clasesMetodoMap.put(clase, clase);
			}
		}
		
		StringBuffer parametrosFK = new StringBuffer();
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			List<AtributoConsulta> atributosFK = tablaFK.getAtributosConsulta();
			for(int y=0; atributosFK!=null && y<atributosFK.size(); y++){
				String paquete = atributosFK.get(y).getClase().getJavaPaquete();
				String clase = atributosFK.get(y).getClase().getJavaClase();
				if(map.containsKey(paquete+clase)==false){
					map.put(paquete+clase, atributosPadre.get(y));
					if(i!=0){
						parametrosFK.append(", ");
					}
					parametrosFK.append(clase+" "+clase.toLowerCase());
					i++;
				}
			}
		}
		
		List<Atributo> atributosPK = new ArrayList<Atributo>();
		for(int x=0;x<atributosPadre.size();x++){
			if(atributosPadre.get(x).getCampoSQL()!=null && atributosPadre.get(x).getCampoSQL().isFlgPK()){
				atributosPK.add(atributosPadre.get(x));
			}
		}
		StringBuffer pkAleas = new StringBuffer();
		StringBuffer pkValores = new StringBuffer();
		StringBuffer querySB = new StringBuffer();
		int a = 0;
		for(int x=0;x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			if(atributo.getCampoSQL()!=null && atributo.getCampoSQL().isFlgCondicion()){
				if(0==a){
					pkAleas.append("?");
					pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}else{
					pkAleas.append(", ?");
					pkValores.append(", "+atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}
				
				querySB.append("\t\tcstmt.set"+atributo.getTipo()+"(1, "+atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()"+");\r\n");
				
				a++;
			}
		}
		String nombreClase = tabla.getTablaPadre().getAtributosConsulta().get(0).getClase().getJavaClase();
		String nombreEsquema = tabla.getTablaPadre().getEsquema();
		
		buffer.append("\tpublic "+nombreClase+" "+nombreMetodo+"("+nombreClase+" "+nombreClase.toLowerCase()+") throws Exception{\r\n");
		buffer.append("\t\ttry{\r\n");
		if(consulta instanceof ConsultaTareaCompletar){
			buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+nombreEsquema+".SP_PORTAL_TAR_"+tabla.getAleas().toUpperCase()+"(?)"+"}\");\r\n");
			buffer.append("\t\tcstmt.setString(1, "+nombreClase.toLowerCase()+".getPiid()"+");\r\n");
		}else{
			buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+nombreEsquema+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_CON_VALOR("+pkAleas.toString()+")"+"}\");\r\n");
			buffer.append(querySB.toString());
		}
		
		buffer.append("\t\tcstmt.execute();\r\n");
		
		buffer.append("\t\tResultSet rs = cstmt.getResultSet();\r\n");
		buffer.append("\t\tif (rs.next()) {\r\n");
		
		for(int x=0; x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			buffer.append(escribirAtributo(tabla.getTablaPadre(), atributo));
		}
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			if(tablaFK.getAtributosConsulta()!=null && tablaFK.getAtributosConsulta().size()>0){
				String nombreClaseFK = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
				buffer.append("\t\t\t"+nombreClaseFK+" "+nombreClaseFK.toLowerCase()+" = new "+nombreClaseFK+"();\r\n");
				for(int y=0; y<tablaFK.getAtributosConsulta().size(); y++){
					AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(y);
					buffer.append(escribirAtributo(tablaFK, atributo));
				}
				buffer.append("\t\t\t"+nombreClase.toLowerCase()+".set"+nombreClaseFK+"("+nombreClaseFK.toLowerCase()+");\r\n");
			}
		}
		buffer.append("\t\t}\r\n");
		buffer.append("\t\trs.close();\r\n");
		buffer.append("\t\tcstmt.close();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn "+nombreClase.toLowerCase()+";\r\n");
		
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoDaoImpl_mixto(Consulta tabla){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tabla.getPaquete()+".impl;\r\n\r\n");

		buffer.append("import java.util.List;\r\n");
		buffer.append("import java.sql.ResultSet;\r\n");
		buffer.append("import java.sql.SQLException;\r\n\r\n");

		buffer.append("import org.springframework.jdbc.core.RowMapper;\r\n\r\n");

		buffer.append("import "+tabla.getPaquete()+"."+tabla.getInterfase()+";\r\n");
		buffer.append("import pe.com.financiero.bpm.core.dao.impl.BaseDaoImpl;\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		List<AtributoConsulta> atributosPadre = tabla.getTablaPadre().getAtributosConsulta();
		String nombreClase = atributosPadre.get(0).getClase().getJavaClase();
		String nombreEsquema = atributosPadre.get(0).getCampoSQL().getTabla().getEsquema();
		
		for (int x = 0; x < atributosPadre.size(); x++) {
			String paquete = atributosPadre.get(x).getClase().getJavaPaquete();
			String clase = atributosPadre.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributosPadre.get(x));
				buffer.append("import "+paquete+"."+clase+";\r\n");
			}
		}
		
		int i = 0;
		for (int x = 0; x < atributosPadre.size(); x++) {
			AtributoConsulta atributo = atributosPadre.get(x);
			String clase = atributo.getClase().getJavaClase();
			if(atributo.getCampoSQL().isFlgCondicion()){
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		StringBuffer parametrosFK = new StringBuffer();
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			String paquete = tablaFK.getAtributosConsulta().get(0).getClase().getJavaPaquete();
			String clase = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
			buffer.append("import "+paquete+"."+clase+";\r\n");
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, tablaFK.getAtributosConsulta().get(0));
				if(i!=0){
					parametrosFK.append(", ");
				}
				parametrosFK.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		buffer.append("\r\n");
		buffer.append("public class "+tabla.getInterfase()+"Impl extends BaseDaoImpl implements "+tabla.getInterfase()+"  {\r\n\r\n");
		
		List<Atributo> atributosPK = new ArrayList<Atributo>();
		for(int x=0;x<atributosPadre.size();x++){
			if(atributosPadre.get(x).getCampoSQL().isFlgPK()){
				atributosPK.add(atributosPadre.get(x));
			}
		}
		StringBuffer pkAleas = new StringBuffer();
		StringBuffer pkValores = new StringBuffer();
		int a = 0;
		for(int x=0;x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			if(atributo.getCampoSQL().isFlgCondicion()){
				if(0==a){
					pkAleas.append("?");
					pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}else{
					pkAleas.append(", ?");
					pkValores.append(", "+atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}
				a++;
			}
		}
		
		
		buffer.append("\tpublic "+nombreClase+" obtener"+nombreClase+"("+parametros.toString()+"){\r\n");
		buffer.append("\t\tList<"+nombreClase+"> "+nombreClase.toLowerCase()+"s = getJdbcTemplate().query( \"call "+nombreEsquema+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_CON_VALOR("+pkAleas.toString()+")\", new Object[] { "+pkValores.toString()+" }, new "+nombreClase+"Mapper());\r\n");
		buffer.append("\t\tif("+nombreClase.toLowerCase()+"s!=null && "+nombreClase.toLowerCase()+"s.size()>0){\r\n");
		for (int j = 0; j < tabla.getTablasFK().size(); j++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(j);
			if(tablaFK.isFlgUnoMuchos()){
				AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(0);
				StringBuffer numeroAtributosFK = null;
				StringBuffer nombreAtributosFK = new StringBuffer();
				for (int k = 0; k < tablaFK.getAtributosConsulta().size(); k++) {
					AtributoConsulta atributoFK = tablaFK.getAtributosConsulta().get(k);
					if(numeroAtributosFK==null){
						numeroAtributosFK = new StringBuffer();
						numeroAtributosFK.append("?");
						for (int l = 0; l < atributosPadre.size(); l++) {
							Atributo atributoFKPadre = atributosPadre.get(l);
							if(atributoFKPadre.getCampoSQL().getTabla().getNombre().equalsIgnoreCase(atributoFK.getCampoSQL().getFk().getTabla().getNombre()) && 
									atributoFKPadre.getCampoSQL().getNombre().equalsIgnoreCase(atributoFK.getCampoSQL().getFk().getNombre())){
								nombreAtributosFK.append(""+atributoFKPadre.getClase().getJavaClase().toLowerCase()+"s.get(0).get"+Validaciones.nombreVariable(atributoFKPadre.getNombre())+"()");
							}
						}
					}else{
						numeroAtributosFK.append(" ,?");
						nombreAtributosFK.append(" ,"+atributoFK.getClase().getJavaClase().toLowerCase()+"get"+atributoFK.getNombre()+"()");
					}
				}
				
				buffer.append("\t\t\t"+nombreClase.toLowerCase()+"s.get(0).set"+atributo.getClase().getJavaClase()+"s(getJdbcTemplate().query( \"call "+atributo.getCampoSQL().getTabla().getEsquema()+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_"+atributo.getCampoSQL().getTabla().getNombre().toUpperCase()+"_CON_LISTA("+numeroAtributosFK.toString()+")\", new Object[] { "+nombreAtributosFK.toString()+" }, new "+atributo.getClase().getJavaClase()+"Mapper()));\r\n");
				
			}
		}
		buffer.append("\t\t\treturn "+nombreClase.toLowerCase()+"s.get(0);\r\n");
		
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tprotected static final class "+nombreClase+"Mapper implements RowMapper<"+nombreClase+"> {\r\n");
		buffer.append("\t\tpublic "+nombreClase+" mapRow(ResultSet rs, int rowNum) throws SQLException {\r\n");
		buffer.append("\t\t\t"+nombreClase+" "+nombreClase.toLowerCase()+" = new "+nombreClase+"();\r\n");
		
		for(int x=0; x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			buffer.append(escribirAtributo(tabla.getTablaPadre(), atributo));
		}
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			if(tablaFK.isFlgUnoMuchos()==false){
				buffer.append("\t\t\t"+nombreClase+" "+nombreClase.toLowerCase()+" = new "+nombreClase+"();\r\n");
				for(int y=0; y<tablaFK.getAtributosConsulta().size(); y++){
					AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(y);
					buffer.append(escribirAtributo(tablaFK, atributo));
				}
				buffer.append("\t\t\t"+nombreClase.toLowerCase()+".set"+nombreClase+"("+nombreClase.toLowerCase()+");\r\n");
			}
		}
		
		buffer.append("\t\t\treturn "+nombreClase.toLowerCase()+";\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n");
		
		
		//Mapper FK
		for (int j = 0; j < tabla.getTablasFK().size(); j++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(j);
			if(tablaFK.isFlgUnoMuchos()){
				Atributo atributoFK = tablaFK.getAtributosConsulta().get(0);
				
				buffer.append("\tprotected static final class "+atributoFK.getClase().getJavaClase()+"Mapper implements RowMapper<"+atributoFK.getClase().getJavaClase()+"> {\r\n");
				buffer.append("\t\tpublic "+atributoFK.getClase().getJavaClase()+" mapRow(ResultSet rs, int rowNum) throws SQLException {\r\n");
				buffer.append("\t\t\t"+atributoFK.getClase().getJavaClase()+" "+atributoFK.getClase().getJavaClase().toLowerCase()+" = new "+atributoFK.getClase().getJavaClase()+"();\r\n");
				
				for(int x=0; x<tablaFK.getAtributosConsulta().size();x++){
					AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(x);
					if(atributo.getCampoSQL().getTabla().getNombre().equalsIgnoreCase(tablaFK.getNombre())){
						buffer.append(escribirAtributo(tablaFK, atributo));
					}
				}
				
				buffer.append("\t\t\treturn "+atributoFK.getClase().getJavaClase().toLowerCase()+";\r\n");
				buffer.append("\t\t}\r\n");
				buffer.append("\t}\r\n");
			}
		}
		
		buffer.append("}\r\n");
		
		return buffer.toString();
	}

	private String contenidoArchivoEJBDaoImpl_mixto(Consulta tabla, String nombreMetodo, Connection connection){
		
		List<AtributoConsulta> atributosPadre = tabla.getTablaPadre().getAtributosConsulta();
		String nombreClase = atributosPadre.get(0).getClase().getJavaClase();
		String nombreEsquema = tabla.getTablaPadre().getEsquema();
		
		StringBuffer buffer = new StringBuffer();
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		for (int x = 0; x < atributosPadre.size(); x++) {

			String paquete = atributosPadre.get(x).getClase().getJavaPaquete();
			String clase = atributosPadre.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributosPadre.get(x));
			}
		}
		
		int i = 0;
		for (int x = 0; x < atributosPadre.size(); x++) {
			AtributoConsulta atributo = atributosPadre.get(x);
			String clase = atributo.getClase().getJavaClase();
			if(atributo.getCampoSQL().isFlgCondicion()){
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		StringBuffer parametrosFK = new StringBuffer();
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			for(int y=0; y<tablaFK.getAtributosConsulta().size(); y++){
				String paquete = tablaFK.getAtributosConsulta().get(y).getClase().getJavaPaquete();
				String clase = tablaFK.getAtributosConsulta().get(y).getClase().getJavaClase();
				if(map.containsKey(paquete+clase)==false){
					map.put(paquete+clase, atributosPadre.get(y));
					if(i!=0){
						parametrosFK.append(", ");
					}
					parametrosFK.append(clase+" "+clase.toLowerCase());
					i++;
				}
			}
		}
		
		List<AtributoConsulta> atributosPK = new ArrayList<AtributoConsulta>();
		for(int x=0;x<atributosPadre.size();x++){
			if(atributosPadre.get(x).getCampoSQL().isFlgPK()){
				atributosPK.add(atributosPadre.get(x));
			}
		}
		StringBuffer pkAleas = new StringBuffer();
		StringBuffer pkValores = new StringBuffer();
		int a = 0;
		for(int x=0;x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			if(atributo.getCampoSQL().isFlgCondicion()){
				if(0==a){
					pkAleas.append("?");
					pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}else{
					pkAleas.append(", ?");
					pkValores.append(", "+atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
				}
				a++;
			}
		}
		
		buffer.append("\tpublic "+nombreClase+" "+nombreMetodo+"("+nombreClase+" "+nombreClase.toLowerCase()+") throws Exception{\r\n");
		//buffer.append("\t\tList<"+nombreClase+"> "+nombreClase.toLowerCase()+"s = new ArrayList<"+nombreClase+">();\r\n\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+nombreEsquema+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_CON_VALOR(?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(1, "+nombreClase.toLowerCase()+".getPiid());\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tResultSet rs = cstmt.getResultSet();\r\n");
		buffer.append("\t\t\tif (rs.next()) {\r\n");
    	
		for(int x=0; x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			buffer.append(escribirAtributo(tabla.getTablaPadre(), atributo));
		}
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			if(tablaFK.isFlgUnoMuchos()==false){
				buffer.append("\t\t\t\t\r\n");
				buffer.append("\t\t\t\t"+tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase()+" "+tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase().toLowerCase()+" = new "+tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase()+"();\r\n");
				for(int y=0; y<tablaFK.getAtributosConsulta().size(); y++){
					AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(y);
					buffer.append(escribirAtributo(tablaFK, atributo));
				}
				buffer.append("\t\t\t\t"+nombreClase.toLowerCase()+".set"+tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase()+"("+tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase().toLowerCase()+");\r\n");
			}
		}
		
		buffer.append("\t\t\t\trs.close();\r\n");
		buffer.append("\t\t\t\tcstmt.close();\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		
		for (int j = 0; j < tabla.getTablasFK().size(); j++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(j);
			if(tablaFK.isFlgUnoMuchos()){
				
				StringBuffer numeroAtributosFK = null;
				StringBuffer nombreAtributosFK = new StringBuffer();
				String esquemaFK = null; 
				String nombreTablaFK = null; 
				String nombreClaseFK = null;
				List<AtributoConsulta> atributosFK = tablaFK.getAtributosConsulta();
				int numeroFK = 1;
				for (int k = 0; k < atributosFK.size(); k++) {
					AtributoConsulta atributoConsulta = atributosFK.get(k);
					if(atributoConsulta.getCampoSQL().isFlgTieneFK()){
						AtributoConsulta atributoFK  = new  ConsultaDaoImpl().obtenerAtributo(atributoConsulta.getCampoSQL().getFk().getCodigoAtributo(), connection);
						if(numeroAtributosFK == null){
							numeroAtributosFK = new StringBuffer("?");
						}else{
							numeroAtributosFK.append(" ,?");
						}
						
						nombreAtributosFK.append("\t\t\t\tcstmt.set"+Validaciones.nombreVariable(atributoFK.getTipo())+"("+numeroFK+", "+atributoFK.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributoFK.getNombre())+"());\r\n");
						numeroFK++;
						esquemaFK = atributoConsulta.getCampoSQL().getTabla().getEsquema();
						nombreTablaFK = atributoConsulta.getCampoSQL().getTabla().getNombre();
						nombreClaseFK = atributoConsulta.getClase().getJavaClase();
					}
				}
				
				buffer.append("\t\t\t\tcstmt = getConnection().prepareCall(\"{call "+esquemaFK+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_"+nombreTablaFK.toUpperCase()+"_CON_LISTA("+numeroAtributosFK.toString()+")}\");\r\n");
				buffer.append(nombreAtributosFK.toString());
				buffer.append("\t\t\t\tcstmt.execute();\r\n\r\n");
				buffer.append("\t\t\t\tList<"+nombreClaseFK+"> "+nombreClaseFK.toLowerCase()+"s = new ArrayList<"+nombreClaseFK+">();\r\n");
				buffer.append("\t\t\t\trs = cstmt.getResultSet();\r\n");
				buffer.append("\t\t\t\twhile (rs.next()) {\r\n");
				
				buffer.append("\t\t\t\t\t"+nombreClaseFK+" "+nombreClaseFK.toLowerCase()+" = new "+nombreClaseFK+"();\r\n");
				
				for(int x=0; x<tablaFK.getAtributosConsulta().size();x++){
					AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(x);
					if(atributo.getCampoSQL().getTabla().getNombre().equalsIgnoreCase(tablaFK.getNombre())){
						buffer.append("\t"+escribirAtributo(tablaFK, atributo));
					}
				}
				buffer.append("\t\t\t\t\t"+nombreClaseFK.toLowerCase()+"s.add("+nombreClaseFK.toLowerCase()+");\r\n");
				buffer.append("\t\t\t\t}\r\n");
				
				buffer.append("\t\t\t\t"+nombreClase.toLowerCase()+".set"+nombreClaseFK+"s("+nombreClaseFK.toLowerCase()+"s);\r\n");
				buffer.append("\t\t\t\trs.close();\r\n");
				buffer.append("\t\t\t\tcstmt.close();\r\n");
				buffer.append("\t\t\t\tgetConnection().close();\r\n");
		    	
			}
			
		}
		buffer.append("\t\t\t}\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn "+nombreClase.toLowerCase()+";\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoDaoImpl_simpleMultiple(Consulta tabla){
		List<AtributoConsulta> atributosPadre = tabla.getTablaPadre().getAtributosConsulta();
		String nombreClase = atributosPadre.get(0).getClase().getJavaClase();
		String nombreEsquema = atributosPadre.get(0).getCampoSQL().getTabla().getEsquema();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tabla.getPaquete()+".impl;\r\n\r\n");

		buffer.append("import java.util.List;\r\n");
		buffer.append("import java.sql.ResultSet;\r\n");
		buffer.append("import java.sql.SQLException;\r\n\r\n");

		buffer.append("import org.springframework.jdbc.core.RowMapper;\r\n\r\n");

		buffer.append("import "+tabla.getPaquete()+"."+tabla.getInterfase()+";\r\n");
		buffer.append("import pe.com.financiero.bpm.core.dao.impl.BaseDaoImpl;\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		StringBuffer parametrosObjetos = new StringBuffer();
		int i = 0;
		for (int x = 0; x < atributosPadre.size(); x++) {

			String paquete = atributosPadre.get(x).getClase().getJavaPaquete();
			String clase = atributosPadre.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributosPadre.get(x));
				buffer.append("import "+paquete+"."+clase+";\r\n");
				if(i!=0){
					parametros.append(", ");
					parametrosObjetos.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				parametrosObjetos.append(clase.toLowerCase());
				i++;
			}
		}
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			String paquete = tablaFK.getAtributosConsulta().get(x).getClase().getJavaPaquete();
			String clase = tablaFK.getAtributosConsulta().get(x).getClase().getJavaClase();
			for(int y=0; y<tablaFK.getAtributosConsulta().size(); y++){
				if(map.containsKey(paquete+clase)==false){
					map.put(paquete+clase, tablaFK.getAtributosConsulta().get(0));
					buffer.append("import "+paquete+"."+clase+";\r\n");
				}
			}
		}
		
		buffer.append("\r\n");
		buffer.append("public class "+tabla.getInterfase()+"Impl extends BaseDaoImpl implements "+tabla.getInterfase()+"  {\r\n\r\n");
		
		List<Atributo> atributosPK = new ArrayList<Atributo>();
		for(int x=0;x<atributosPadre.size();x++){
			if(atributosPadre.get(x).getCampoSQL().isFlgPK()){
				atributosPK.add(atributosPadre.get(x));
			}
		}
		StringBuffer pkAleas = new StringBuffer();
		StringBuffer pkValores = new StringBuffer();
		int limiteA = atributosPK.size()-1;
		for(int x=0;x<atributosPK.size();x++){
			Atributo atributo = atributosPK.get(x);
			if(limiteA==x){
				pkAleas.append("?");
				pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
			}else{
				pkAleas.append("?,");
				pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"(),");
			}
		}
		
		buffer.append("\tpublic "+nombreClase+" obtener"+nombreClase+"("+parametros.toString()+"){\r\n");
		buffer.append("\t\tList<"+nombreClase+"> "+nombreClase.toLowerCase()+"s = getJdbcTemplate().query( \"call "+nombreEsquema+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_CON_VALOR("+pkAleas.toString()+")\", new Object[] { "+pkValores.toString()+" }, new "+nombreClase+"Mapper());\r\n");
		buffer.append("\t\tif("+nombreClase.toLowerCase()+"s!=null && "+nombreClase.toLowerCase()+"s.size()>0){\r\n");
		buffer.append("\t\t\t"+nombreClase+" "+nombreClase.toLowerCase()+"Respuesta = "+nombreClase.toLowerCase()+"s.get(0);\r\n");
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			if(tablaFK.isFlgUnoMuchos()){
				String claseFK = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
				buffer.append("\t\t\t"+nombreClase.toLowerCase()+"Respuesta.set"+claseFK+"s(obtener"+claseFK+"("+parametrosObjetos.toString()+"));\r\n");
			}
		}
		
		buffer.append("\t\t\treturn "+nombreClase.toLowerCase()+"Respuesta;\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		
		for(int u=0; u<tabla.getTablasFK().size() ;u++){
			TablaConsulta tablaFK = tabla.getTablasFK().get(u);
			String claseFK = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
			String esquemaFK = tablaFK.getEsquema();
			buffer.append("\tpublic List<"+claseFK+"> obtener"+claseFK+"("+parametros.toString()+"){\r\n");
			buffer.append("\t\treturn getJdbcTemplate().query( \"call "+esquemaFK+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_"+esquemaFK.toUpperCase()+"_CON_LISTA("+pkAleas.toString()+")\", new Object[] { "+pkValores.toString()+" }, new "+claseFK+"Mapper());\r\n");
			buffer.append("\t}\r\n\r\n");
		}
		
		
		
		buffer.append("\tprotected static final class "+nombreClase+"Mapper implements RowMapper<"+nombreClase+"> {\r\n");
		buffer.append("\t\tpublic "+nombreClase+" mapRow(ResultSet rs, int rowNum) throws SQLException {\r\n");
		buffer.append("\t\t\t"+nombreClase+" "+nombreClase.toLowerCase()+" = new "+nombreClase+"();\r\n");
		
		for(int x=0; x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			buffer.append(escribirAtributo(tabla.getTablaPadre(), atributo));
		}
		
		buffer.append("\t\t\treturn "+nombreClase.toLowerCase()+";\r\n");
		buffer.append("\t\t}\r\n\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		for(int u=0; u<tabla.getTablasFK().size() ;u++){
			if(tabla.getTablasFK().get(u).isFlgUnoMuchos()){
				TablaConsulta tablaFK = tabla.getTablasFK().get(u); 
				String claseFK = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
				buffer.append("\tprotected static final class "+claseFK+"Mapper implements RowMapper<"+claseFK+"> {\r\n");
				buffer.append("\t\tpublic "+claseFK+" mapRow(ResultSet rs, int rowNum) throws SQLException {\r\n");
				buffer.append("\t\t\t"+claseFK+" "+claseFK.toLowerCase()+" = new "+claseFK+"();\r\n");
				
				for(int x=0; x<tablaFK.getAtributosConsulta().size();x++){
					AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(x);
					buffer.append(escribirAtributo(tablaFK, atributo));
				}
				
				buffer.append("\t\t\treturn "+claseFK.toLowerCase()+";\r\n");
				buffer.append("\t\t}\r\n\r\n");
				buffer.append("\t}\r\n\r\n");
			}
		}
		
		buffer.append("}\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoEJBDaoImpl_simpleMultiple(Consulta tabla, String nombreMetodo){
		List<AtributoConsulta> atributosPadre = tabla.getTablaPadre().getAtributosConsulta();
		String nombreClase = atributosPadre.get(0).getClase().getJavaClase();
		String nombreEsquema = atributosPadre.get(0).getCampoSQL().getTabla().getEsquema();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tabla.getPaquete()+".impl;\r\n\r\n");

		buffer.append("import java.util.List;\r\n");
		buffer.append("import java.sql.ResultSet;\r\n");
		buffer.append("import java.sql.SQLException;\r\n\r\n");

		buffer.append("import org.springframework.jdbc.core.RowMapper;\r\n\r\n");

		buffer.append("import "+tabla.getPaquete()+"."+tabla.getInterfase()+";\r\n");
		buffer.append("import pe.com.financiero.bpm.core.dao.impl.BaseDaoImpl;\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		StringBuffer parametrosObjetos = new StringBuffer();
		int i = 0;
		for (int x = 0; x < atributosPadre.size(); x++) {

			String paquete = atributosPadre.get(x).getClase().getJavaPaquete();
			String clase = atributosPadre.get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributosPadre.get(x));
				buffer.append("import "+paquete+"."+clase+";\r\n");
				if(i!=0){
					parametros.append(", ");
					parametrosObjetos.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				parametrosObjetos.append(clase.toLowerCase());
				i++;
			}
		}
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			for(int y=0; y<tablaFK.getAtributosConsulta().size(); y++){
				String paquete = tablaFK.getAtributosConsulta().get(x).getClase().getJavaPaquete();
				String clase = tablaFK.getAtributosConsulta().get(x).getClase().getJavaClase();
				if(map.containsKey(paquete+clase)==false){
					map.put(paquete+clase, atributosPadre.get(x));
					buffer.append("import "+paquete+"."+clase+";\r\n");
				}
			}
		}
		
		buffer.append("\r\n");
		buffer.append("public class "+tabla.getInterfase()+"Impl extends BaseDaoImpl implements "+tabla.getInterfase()+"  {\r\n\r\n");
		
		List<AtributoConsulta> atributosPK = new ArrayList<AtributoConsulta>();
		for(int x=0;x<atributosPadre.size();x++){
			if(atributosPadre.get(x).getCampoSQL().isFlgPK()){
				atributosPK.add(atributosPadre.get(x));
			}
		}
		StringBuffer pkAleas = new StringBuffer();
		StringBuffer pkValores = new StringBuffer();
		int limiteA = atributosPK.size()-1;
		for(int x=0;x<atributosPK.size();x++){
			Atributo atributo = atributosPK.get(x);
			if(limiteA==x){
				pkAleas.append("?");
				pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"()");
			}else{
				pkAleas.append("?,");
				pkValores.append(atributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(atributo.getNombre())+"(),");
			}
		}
		
		buffer.append("\tpublic "+nombreClase+" obtener"+nombreClase+"("+parametros.toString()+"){\r\n");
		buffer.append("\t\tList<"+nombreClase+"> "+nombreClase.toLowerCase()+"s = getJdbcTemplate().query( \"call "+nombreEsquema+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_CON_VALOR("+pkAleas.toString()+")\", new Object[] { "+pkValores.toString()+" }, new "+nombreClase+"Mapper());\r\n");
		buffer.append("\t\tif("+nombreClase.toLowerCase()+"s!=null && "+nombreClase.toLowerCase()+"s.size()>0){\r\n");
		buffer.append("\t\t\t"+nombreClase+" "+nombreClase.toLowerCase()+"Respuesta = "+nombreClase.toLowerCase()+"s.get(0);\r\n");
		
		for (int x = 0; x < tabla.getTablasFK().size(); x++) {
			TablaConsulta tablaFK = tabla.getTablasFK().get(x);
			String claseFK = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
			if(tablaFK.isFlgUnoMuchos()){
				buffer.append("\t\t\t"+nombreClase.toLowerCase()+"Respuesta.set"+claseFK+"s(obtener"+claseFK+"("+parametrosObjetos.toString()+"));\r\n");
			}
		}
		
		buffer.append("\t\t\treturn "+nombreClase.toLowerCase()+"Respuesta;\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		
		for(int u=0; u<tabla.getTablasFK().size() ;u++){
			TablaConsulta tablaFK = tabla.getTablasFK().get(u);
			String claseFK = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
			String esquemaFK = tablaFK.getEsquema();
			buffer.append("\tpublic List<"+claseFK+"> obtener"+claseFK+"("+parametros.toString()+"){\r\n");
			buffer.append("\t\treturn getJdbcTemplate().query( \"call "+esquemaFK+".SP_PORTAL_CON_"+tabla.getAleas().toUpperCase()+"_"+tablaFK.getNombre().toUpperCase()+"_CON_LISTA("+pkAleas.toString()+")\", new Object[] { "+pkValores.toString()+" }, new "+claseFK+"Mapper());\r\n");
			buffer.append("\t}\r\n\r\n");
		}
		
		
		
		buffer.append("\tprotected static final class "+nombreClase+"Mapper implements RowMapper<"+nombreClase+"> {\r\n");
		buffer.append("\t\tpublic "+nombreClase+" mapRow(ResultSet rs, int rowNum) throws SQLException {\r\n");
		buffer.append("\t\t\t"+nombreClase+" "+nombreClase.toLowerCase()+" = new "+nombreClase+"();\r\n");
		
		for(int x=0; x<atributosPadre.size();x++){
			AtributoConsulta atributo = atributosPadre.get(x);
			buffer.append(escribirAtributo(tabla.getTablaPadre(), atributo));
		}
		
		buffer.append("\t\t\treturn "+nombreClase.toLowerCase()+";\r\n");
		buffer.append("\t\t}\r\n\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		for(int u=0; u<tabla.getTablasFK().size() ;u++){
			if(tabla.getTablasFK().get(u).isFlgUnoMuchos()){
				TablaConsulta tablaFK = tabla.getTablasFK().get(u);
				String claseFK = tablaFK.getAtributosConsulta().get(0).getClase().getJavaClase();
				buffer.append("\tprotected static final class "+claseFK+"Mapper implements RowMapper<"+claseFK+"> {\r\n");
				buffer.append("\t\tpublic "+claseFK+" mapRow(ResultSet rs, int rowNum) throws SQLException {\r\n");
				buffer.append("\t\t\t"+claseFK+" "+claseFK.toLowerCase()+" = new "+claseFK+"();\r\n");
				
				for(int x=0; x<tablaFK.getAtributosConsulta().size();x++){
					AtributoConsulta atributo = tablaFK.getAtributosConsulta().get(x);
					buffer.append(escribirAtributo(tablaFK, atributo));
				}
				
				buffer.append("\t\t\treturn "+claseFK.toLowerCase()+";\r\n");
				buffer.append("\t\t}\r\n\r\n");
				buffer.append("\t}\r\n\r\n");
			}
		}
		
		buffer.append("}\r\n");
		
		return buffer.toString();
	}
	
	private String escribirAtributo(TablaConsulta tabla, AtributoConsulta atributo){
		StringBuffer buffer = new StringBuffer();
		String nombreClase = atributo.getClase().getJavaClase();
		String tabulacion = "\t\t\t\t";
		if(atributo.getCampoSQL()!=null){
			if(atributo.getCampoSQL().isTieneFuncion()){
				if(atributo.getTipo().equalsIgnoreCase("java.sql.Date")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getDate(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"_"+atributo.getCampoSQL().getFuncionBusquedaCatalogo()+"\"));\r\n");
				}else if(atributo.getTipo().equalsIgnoreCase("java.sql.Timestamp")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getTimestamp(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"_"+atributo.getCampoSQL().getFuncionBusquedaCatalogo()+"\"));\r\n");
				}else if(atributo.getTipo().equalsIgnoreCase("java.math.BigDecimal")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getBigDecimal(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"_"+atributo.getCampoSQL().getFuncionBusquedaCatalogo()+"\"));\r\n");			
				}else if(atributo.getTipo().equalsIgnoreCase("Integer")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getInt(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"_"+atributo.getCampoSQL().getFuncionBusquedaCatalogo()+"\"));\r\n");				
				}else{
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.get"+Validaciones.nombreVariable(atributo.getTipo())+"(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"_"+atributo.getCampoSQL().getFuncionBusquedaCatalogo()+"\"));\r\n");
				}
			}else{
				if(atributo.getTipo().equalsIgnoreCase("java.sql.Date")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getDate(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"\"));\r\n");
				}else if(atributo.getTipo().equalsIgnoreCase("java.sql.Timestamp")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getTimestamp(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"\"));\r\n");
				}else if(atributo.getTipo().equalsIgnoreCase("java.math.BigDecimal")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getBigDecimal(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"\"));\r\n");
				}else if(atributo.getTipo().equalsIgnoreCase("Integer")){
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.getInt(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"\"));\r\n");				
				}else{
					buffer.append(tabulacion+nombreClase.toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(rs.get"+Validaciones.nombreVariable(atributo.getTipo())+"(\""+tabla.getNombre()+"_"+atributo.getCampoSQL().getNombre()+"\"));\r\n");
				}
			}
		}
		return buffer.toString();
	}

}
