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

import com.carga.mapeo.dao.impl.TareaDaoImpl;
import com.carga.portal.modelo.Atributo;
import com.carga.portal.modelo.AtributoTarea;
import com.carga.portal.modelo.Clase;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.ObjetoBPM;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.Tarea;
import com.carga.portal.servicio.crear.util.Validaciones;

public class CrearTarea {
	
	public void crear(Proyecto proyecto, Configuracion configuracion, Connection connection) throws Exception{
		crearBase(proyecto, proyecto.getTareas(), configuracion, connection);
	}
	
	private void crearBase(Proyecto proyecto, List<Tarea> tareas, Configuracion configuracion, Connection connection) throws Exception{
		
		//Servlet controllador
		File directorioControllador = new File(configuracion.getDirectorioWEB().getAbsoluteFile()+"\\src\\"+(proyecto.getJavaNombrePaqueteControlador()).replaceAll("\\.", "/"));
		if(directorioControllador.exists()==false){
			directorioControllador.mkdirs();
		}
		
		File archivoProcesoController = new File(directorioControllador.getAbsolutePath()+"\\"+proyecto.getJavaPreSufijoControlador()+"TareaController.java");
		if(archivoProcesoController.exists()){
			archivoProcesoController.delete();
		}
		archivoProcesoController.createNewFile();
		BufferedWriter bwProcesoController =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoProcesoController,true), "ISO-8859-1"));
		bwProcesoController.write(contenidoServletProcesoController(proyecto, tareas));
		bwProcesoController.close();
		
		
		for(int x=0;x<tareas.size(); x++){
			Tarea tarea = tareas.get(x);
			
			File directorioPaquetesAutogeneradoDaoImpl = new File(configuracion.getDirectorioEJBExt().getAbsoluteFile()+"\\src\\"+(tarea.getPaquete()).replaceAll("\\.", "/")+"\\dao\\impl\\");
			if(directorioPaquetesAutogeneradoDaoImpl.exists()==false){
				directorioPaquetesAutogeneradoDaoImpl.mkdirs();
			}
			
			File directorioPaquetesAutogeneradoDao = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJBCliente()+"\\ejbModule\\"+(tarea.getPaquete()).replaceAll("\\.", "/")+"\\dao\\");
			if(directorioPaquetesAutogeneradoDao.exists()==false){
				directorioPaquetesAutogeneradoDao.mkdirs();
			}
			
			File directorioPaquetesDaoImpl = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJB()+"\\ejbModule\\"+(tarea.getPaquete()).replaceAll("\\.", "/")+"\\dao\\impl\\");
			if(directorioPaquetesDaoImpl.exists()==false){
				directorioPaquetesDaoImpl.mkdirs();
			}
			
			File directorioPaquetesAutogeneradoInterface = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJBCliente()+"\\ejbModule\\"+(tarea.getPaquete()).replaceAll("\\.", "/"));
			if(directorioPaquetesAutogeneradoInterface.exists()==false){
				directorioPaquetesAutogeneradoInterface.mkdirs();
			}
			
			File archivoClaseAutogeneradoInterface = new File(directorioPaquetesAutogeneradoInterface.getAbsolutePath()+"\\",tarea.getClase()+".java");
			if(archivoClaseAutogeneradoInterface.exists()){
				archivoClaseAutogeneradoInterface.delete();
			}
			archivoClaseAutogeneradoInterface.createNewFile();
			BufferedWriter bwInterface =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClaseAutogeneradoInterface,true), "ISO-8859-1"));
			bwInterface.write(contenidoArchivoPlantillaInterface(tarea));
			bwInterface.close();
			
			/******************************************************************************************************************************************************/
			File directorioPaquetesAutogeneradoTemplate = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJB()+"\\ejbModule\\"+(tarea.getPaquete()).replaceAll("\\.", "/"));
			if(directorioPaquetesAutogeneradoTemplate.exists()==false){
				directorioPaquetesAutogeneradoTemplate.mkdirs();
			}
			
			File archivoClaseAutogeneradoTemplate = new File(directorioPaquetesAutogeneradoTemplate.getAbsolutePath()+"\\",tarea.getClase()+"Impl.java");
			if(archivoClaseAutogeneradoTemplate.exists()){
				archivoClaseAutogeneradoTemplate.delete();
			}
			archivoClaseAutogeneradoInterface.createNewFile();
			BufferedWriter bwTemplate =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClaseAutogeneradoTemplate,true), "ISO-8859-1"));
			bwTemplate.write(contenidoArchivoPlantillaTemplate(tarea));
			bwTemplate.close();
			/******************************************************************************************************************************************************/
			
			File directorioPaquetesAutogenerado = new File(configuracion.getDirectorioEJBExt().getAbsoluteFile()+"\\src\\"+(tarea.getPaquete()).replaceAll("\\.", "/"));
			if(directorioPaquetesAutogenerado.exists()==false){
				directorioPaquetesAutogenerado.mkdirs();
			}
			
			File archivoClaseAutogenerado = new File(directorioPaquetesAutogenerado.getAbsolutePath()+"\\","Pre"+tarea.getClase()+"Impl.java");
			if(archivoClaseAutogenerado.exists()){
				archivoClaseAutogenerado.delete();
			}
			
			archivoClaseAutogenerado.createNewFile();
			BufferedWriter bufferedWriterAutogenerado =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClaseAutogenerado,true), "ISO-8859-1"));
			bufferedWriterAutogenerado.write(contenidoArchivoClase(proyecto, tarea, connection));
			bufferedWriterAutogenerado.close();
			
			//crear el dao de tarea
			File archivoClaseDao = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJBCliente()+"\\ejbModule\\"+(tarea.getPaquete()).replaceAll("\\.", "/")+"\\dao\\",tarea.getClase()+"Dao.java");
			if(archivoClaseDao.exists()){
				archivoClaseDao.delete();
			}
			archivoClaseDao.createNewFile();
			BufferedWriter bufferedWriterDao =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClaseDao,true), "ISO-8859-1"));
			bufferedWriterDao.write(contenidoArchivoClaseDao(proyecto, tarea, connection));
			bufferedWriterDao.close();
			
			/*****************************************************************************************************************************************************/
			//crear el predaoImpl de tarea
			File archivoClasePreDaoImpl = new File(directorioPaquetesAutogeneradoDaoImpl.getAbsolutePath()+"\\Pre"+tarea.getClase()+"DaoImpl.java");
			if(archivoClasePreDaoImpl.exists()){
				archivoClasePreDaoImpl.delete();
			}
			archivoClasePreDaoImpl.createNewFile();
			BufferedWriter bufferedWriterPreDaoImpl =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClasePreDaoImpl,true), "ISO-8859-1"));
			bufferedWriterPreDaoImpl.write(contenidoArchivoClasePreDaoImpl(proyecto, tarea, connection));
			bufferedWriterPreDaoImpl.close();
			/*****************************************************************************************************************************************************/
			
			/*****************************************************************************************************************************************************/
			//crear el daoImpl de tarea
			File archivoClaseDaoImpl = new File(directorioPaquetesDaoImpl.getAbsolutePath()+"\\"+tarea.getClase()+"DaoImpl.java");
			if(archivoClaseDaoImpl.exists()){
				archivoClaseDaoImpl.delete();
			}
			archivoClaseDaoImpl.createNewFile();
			BufferedWriter bufferedWriterDaoImpl =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClaseDaoImpl,true), "ISO-8859-1"));
			bufferedWriterDaoImpl.write(contenidoArchivoClaseDaoImpl(tarea));
			bufferedWriterDaoImpl.close();
			
			/*****************************************************************************************************************************************************/
			/** crear clase util **/
			
			File directorioPaquetesAutogeneradoUtil = new File(configuracion.getDirectorioEJBExt().getAbsoluteFile()+"\\src\\"+(tarea.getPaquete()).replaceAll("\\.", "/")+"\\util\\");
			if(directorioPaquetesAutogeneradoUtil.exists()==false){
				directorioPaquetesAutogeneradoUtil.mkdirs();
			}
			
			File archivoClaseAutogeneradoUtil = new File(directorioPaquetesAutogeneradoUtil.getAbsolutePath()+"\\","Pre"+tarea.getClase()+"Util.java");
			if(archivoClaseAutogeneradoUtil.exists()){
				archivoClaseAutogeneradoUtil.delete();
			}
			
			archivoClaseAutogeneradoUtil.createNewFile();
			BufferedWriter bufferedWriterAutogeneradoUtil =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClaseAutogeneradoUtil,true), "ISO-8859-1"));
			bufferedWriterAutogeneradoUtil.write(contenidoArchivoClaseUtil(proyecto, tarea, connection));
			bufferedWriterAutogeneradoUtil.close();
			
		}
	}
	
	private String contenidoArchivoClase(Proyecto proyecto, Tarea tarea, Connection connection) throws Exception{
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tarea.getPaquete()+";\r\n\r\n");
		
		buffer.append("import java.sql.Timestamp;\r\n");
		buffer.append("import java.util.Calendar;\r\n");
		buffer.append("import java.util.TimeZone;\r\n\r\n");

		buffer.append("import javax.ejb.EJB;\r\n");
		buffer.append("import javax.naming.Context;\r\n");
		buffer.append("import javax.naming.InitialContext;\r\n");
		buffer.append("import javax.servlet.http.HttpServletRequest;\r\n");
		buffer.append("import javax.servlet.http.HttpServletResponse;\r\n\r\n");

		buffer.append("import com.ibm.task.api.ClientObjectWrapper;\r\n");
		buffer.append("import com.ibm.task.api.HumanTaskManager;\r\n");
		buffer.append("import com.ibm.task.api.HumanTaskManagerHome;\r\n");
		buffer.append("import com.ibm.task.api.Task;\r\n");
		buffer.append("import commonj.sdo.DataObject;\r\n\r\n");
		
		buffer.append("import pe.com.financiero.bpm.core.util.ArchivoUtil;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.util.BpmTareaUtil;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.MensajeValidacion;\r\n");
		if(tarea.isFlujoCancelar() && tarea.getAtributosCancelar().size()>0){
			buffer.append("import pe.com.financiero.bpm.core.modelo.TareaCancelada;\r\n");
		}
		if(tarea.isFlujoRechazar()){
			buffer.append("import pe.com.financiero.bpm.core.modelo.TareaRechazo;\r\n\r\n");
		}
		buffer.append("import pe.com.financiero.bpm.core.modelo.TareaHistorial;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.Usuario;\r\n\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n\r\n");
		
		
		if(tarea.isObservarFuncion() || tarea.isSubsanarFuncion()){
			buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoObservacion;\r\n");
		}
		buffer.append("import pe.com.financiero.bpm.core.ejb.TareaBaseImpl;\r\n");
		buffer.append("import "+tarea.getPaquete()+".dao."+tarea.getClase()+"Dao;\r\n\r\n");
		buffer.append("import "+tarea.getPaquete()+".util.Pre"+tarea.getClase()+"Util;\r\n\r\n");
		
		buffer.append("import "+proyecto.getProcesos().get(0).getAtributosEntrada().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		for (int x = 0; x < tarea.getAtributosCompletar().size(); x++) {

			String paquete = tarea.getAtributosCompletar().get(x).getClase().getJavaPaquete();
			String clase = tarea.getAtributosCompletar().get(x).getClase().getJavaClase();

			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, tarea.getAtributosCompletar().get(x));
			}
		}
		/*
		Map<String, Atributo> map3 = new HashMap<String, Atributo>();
		List<AtributoTarea> atributosTrabajar =  tarea.getAtributosTrabajar();
		for (int x = 0; x < atributosTrabajar.size(); x++) {
			AtributoTarea atributo = atributosTrabajar.get(x);
			String paquete = atributo.getClase().getJavaPaquete();
			String clase = atributo.getClase().getJavaClase();

			if(map3.containsKey(paquete+clase)==false){
				map3.put(paquete+clase, atributo);
			}
		}
		*/
		buffer.append("public abstract class Pre"+tarea.getClase()+"Impl extends TareaBaseImpl{\r\n\r\n");

		buffer.append("\tpublic static final TimeZone ZONA_HORARIA = TimeZone.getTimeZone(\"GMT-5\");\r\n\r\n");
		
		buffer.append("\tpublic final static String CODIGO_TAREA = \""+tarea.getPlantilla()+"\";\r\n");
		buffer.append("\tpublic final static String NOMBRE_TAREA = \""+tarea.getNombre()+"\";\r\n");
		buffer.append("\tpublic final static String CODIGO_BANCA = \""+tarea.getProceso().getSufijoBanca()+"\";\r\n");
		buffer.append("\tpublic final static String CODIGO_PRODUCTO = \""+tarea.getProceso().getSufijoProducto()+"\";\r\n");
		buffer.append("\tpublic final static String CODIGO_PROCESO = \""+tarea.getProceso().getSufijoProceso()+"\";\r\n\r\n");
		
		buffer.append("\t@EJB\r\n");
		buffer.append("\tprivate "+tarea.getClase()+"Dao "+tarea.getClase().toLowerCase()+"Dao;\r\n");
		buffer.append("\tprivate Pre"+tarea.getClase()+"Util pre"+tarea.getClase()+"Util = new Pre"+tarea.getClase()+"Util();\r\n\r\n");
		
		buffer.append("\tprivate HumanTaskManager humanTaskManager;\r\n\r\n");
		
		buffer.append("\tpublic HumanTaskManager getHumanTaskManager() {\r\n");
		buffer.append("\t\treturn humanTaskManager;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic Pre"+tarea.getClase()+"Impl() {\r\n");
		buffer.append("\t\ttry {\r\n");
		buffer.append("\t\t\tContext ctx = new InitialContext();\r\n");
		buffer.append("\t\t\thumanTaskManager = ((HumanTaskManagerHome)ctx.lookup(\"com/ibm/task/api/HumanTaskManagerHome\")).create();\r\n");
		buffer.append("\t\t} catch (Exception e){\r\n");
		buffer.append("\t\t\te.printStackTrace();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n");
	    
		
		buffer.append("\tpublic MensajeValidacion completarValidacion(HttpServletRequest request, HttpServletResponse response){\r\n");
		buffer.append("\t\treturn new Pre"+tarea.getClase()+"Util().completarValidacion(request, response);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append(contenidoMetodoGuardarMensajeEntrada(tarea, connection));
		
		buffer.append(contenidoMetodoTrabajar(tarea));
		
		buffer.append(contenidoMetodoHistorial(tarea));
		
		buffer.append(contenidoMetodoCompletar(proyecto, tarea, connection));
		
		if(tarea.getAtributosCancelar()!=null && tarea.getAtributosCancelar().size()>0){
			buffer.append(contenidoMetodoCancelar(proyecto, tarea, connection));
		}else{
			buffer.append(contenidoMetodoSinCancelar(tarea));
		}
		
		if(tarea.isFlujoRechazar()){
			buffer.append(contenidoMetodoRechazar(proyecto, tarea, connection));
		}else{
			buffer.append(contenidoMetodoSinRechazar(tarea));
		}
		
		if(tarea.isFlujoObservar()){
			buffer.append(contenidoMetodoObservar(proyecto, tarea, connection));
		}else{
			buffer.append(contenidoMetodoSinObservar(tarea));
		}
		
		if(tarea.isObservarFuncion()){
			buffer.append(contenidoMetodosObservar(tarea));
		}
		if(tarea.isSubsanarFuncion()){
			buffer.append(contenidoMetodosSubsanar(tarea));
		}
		if(!tarea.isObservarFuncion() && !tarea.isSubsanarFuncion()){
			buffer.append(contenidoMetodosNoObservar(tarea));
		}
		
		buffer.append(contenidodMetodoGuardar(tarea));
		
		buffer.append(registrarHistorialDocumento(tarea));
		
		buffer.append(eliminarHistorialDocumento(tarea));
		
		buffer.append(cargarHistoricoDocumento(tarea));
		
		buffer.append("\r\n\tpublic Object cargarAccion(HttpServletRequest request, HttpServletResponse response) {\r\n");
		buffer.append("\t\treturn true;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("}\r\n");
		return buffer.toString();
	}
	
	private String contenidoMetodosNoObservar(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic java.util.Map<String, String> cargarTipoDocumento(String codigoObservacion,String codigoExcepcion) throws Exception {\n");
		buffer.append("\t\treturn null;\n");
		buffer.append("\t}\n");
		buffer.append("\tpublic boolean registrarSubsanacion(HttpServletRequest request, java.util.Map<String, String> mapRequest, java.util.Map mapInputStream) throws Exception {\n");
		buffer.append("\t\treturn false;\n");
		buffer.append("\t}\n");
		buffer.append("\tpublic Object registrarObservacion(HttpServletRequest request) throws Exception {\n");
		buffer.append("\t\treturn null;\n");
		buffer.append("\t}\n");
		buffer.append("\tpublic boolean eliminarObservacion(HttpServletRequest request) throws Exception {\n");
		buffer.append("\t\treturn false;\n");
		buffer.append("\t}\n");
		buffer.append("\tpublic boolean confirmarObservacion(HttpServletRequest request) throws Exception {\n");
		buffer.append("\t\treturn false;\n");
		buffer.append("\t}\n");
		return buffer.toString();
	}
	
	private String contenidoMetodosObservar(Tarea tarea){
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic Object registrarObservacion(HttpServletRequest request) throws Exception{\r\n");
		buffer.append("\t\tUsuario usuario = obtenerUsuario(request);\r\n");
		buffer.append("\t\tHistoricoObservacion historicoObservacion = new HistoricoObservacion();\r\n");
		buffer.append("\t\thistoricoObservacion.setCodigoObservacion(request.getParameter(\"codigoObservacion\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setCodigoTipoObservacion(Integer.parseInt(request.getParameter(\"codigoTipoObservacion\")));\r\n");
		buffer.append("\t\thistoricoObservacion.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setNombreTarea(request.getParameter(\"nombreTarea\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setComentarioObservacion(request.getParameter(\"comentarioObservacion\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setUsuarioObservacion(usuario.getCodigo());\r\n");
		buffer.append("\t\thistoricoObservacion.setNombreUsuarioObservacion(usuario.getNombre());\r\n");
		buffer.append("\t\thistoricoObservacion.setFechaObservacion(new java.sql.Timestamp(new java.util.Date().getTime()));\r\n");
		buffer.append("\t\thistoricoObservacion.setEstado(\"PEN\");\r\n");
		buffer.append("\t\tif("+tarea.getClase().toLowerCase()+"Dao.registrarObservacion(historicoObservacion)){\r\n");
		buffer.append("\t\t\treturn historicoObservacion;\r\n");
		buffer.append("\t\t} else {\r\n");
		buffer.append("\t\t\treturn null;\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n\r\n");
/*
		if(tarea.isSubsanarFuncion()==false){
			buffer.append("\tpublic java.util.Map<String, String> cargarTipoDocumento(String codigoObservacion,String codigoExcepcion) throws Exception {\n");
			buffer.append("\t\treturn null;\n");
			buffer.append("\t}\n");
		}
		*/
		return buffer.toString();
	}
	
	private String contenidoMetodosSubsanar(Tarea tarea){
		
		StringBuffer buffer = new StringBuffer();

		buffer.append("\tpublic java.util.Map<String, String> cargarTipoDocumento(String codigoObservacion,String codigoExcepcion) throws Exception {\r\n");
		buffer.append("\t\treturn "+tarea.getClase().toLowerCase()+"Dao.cargarTipoDocumento(codigoObservacion,codigoExcepcion);\r\n");
		buffer.append("\t}\r\n");
		
		buffer.append("\tpublic abstract String obtenerDirectorioDestino();\r\n");
		buffer.append("\tpublic abstract String obtenerNuevoNombreArchivo(String nombreAdicional);\r\n");
		
		buffer.append("\tpublic boolean registrarSubsanacion(HttpServletRequest request, java.util.Map<String, String> mapRequest, java.util.Map mapInputStream) throws Exception{\r\n");
		buffer.append("\t\tHistoricoObservacion historicoObservacion = new HistoricoObservacion();\r\n");
		buffer.append("\t\thistoricoObservacion.setPiid(					mapRequest.get(\"piid\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setCodigoObservacion(		mapRequest.get(\"codigoObservacion\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setCodigoTarea(			mapRequest.get(\"codigoTarea\"));\r\n");
		buffer.append("\t\thistoricoObservacion.setCodigoTipoExcepcion(	Integer.parseInt(mapRequest.get(\"codigoTipoExcepcion\")));\r\n");
		buffer.append("\t\thistoricoObservacion.setComentarioExcepcion(	mapRequest.get(\"comentarioExcepcion\"));\r\n");
		buffer.append("\t\tboolean indicadorSustento = Boolean.parseBoolean(mapRequest.get(\"indicadorSustento\"));\r\n");
		buffer.append("\t\t\tif(indicadorSustento){\r\n");
		buffer.append("\t\t\tif(mapRequest.get(\"indicadorAdjunto\").equals(\"0\")){ // Nuevo o Modificado\r\n");
		buffer.append("\t\t\t\tString rutaArchivo = obtenerNuevoNombreArchivo(mapRequest.get(\"nombreArchivoSE\"));\r\n");
		buffer.append("\t\t\t\tString nombreArchivo = mapRequest.get(\"nombreArchivo\");\r\n");
		buffer.append("\t\t\t\tArchivoUtil.guardarArchivo((java.io.InputStream) mapInputStream.get(\"file_"+ tarea.getClase() +"_\"+mapRequest.get(\"nroSecuencia\")), nombreArchivo, obtenerDirectorioDestino(), rutaArchivo);\r\n");
		buffer.append("\t\t\t\thistoricoObservacion.setRutaArchivo(rutaArchivo);\r\n");
		buffer.append("\t\t\t\thistoricoObservacion.setNombreArchivo(mapRequest.get(\"nombreArchivo\"));\r\n");
		buffer.append("\t\t\t} else {\r\n");
		buffer.append("\t\t\t\thistoricoObservacion.setRutaArchivo(mapRequest.get(\"rutaArchivo\"));\r\n");
		buffer.append("\t\t\t\thistoricoObservacion.setNombreArchivo(mapRequest.get(\"nombreArchivo\"));\r\n");
		buffer.append("\t\t\t}\r\n");
		buffer.append("\t\t\thistoricoObservacion.setCodigoTipoDocumento(Integer.parseInt(mapRequest.get(\"codigoTipoDocumento\")));\r\n");
		buffer.append("\t\t} else {\r\n");
		buffer.append("\t\t\tArchivoUtil.eliminarArchivo(obtenerDirectorioDestino(), obtenerNuevoNombreArchivo(mapRequest.get(\"nombreArchivo\")));\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\tUsuario usuario = obtenerUsuario(request);\r\n");
		buffer.append("\thistoricoObservacion.setUsuarioExcepcion(usuario.getCodigo());\r\n");
		buffer.append("\thistoricoObservacion.setNombreUsuarioExcepcion(usuario.getNombre());\r\n");
		buffer.append("\thistoricoObservacion.setFechaExcepcion(new java.sql.Timestamp(new java.util.Date().getTime()));\r\n");
		buffer.append("\thistoricoObservacion.setEstado(\"ATE\");\r\n");
		buffer.append("\treturn "+tarea.getClase().toLowerCase()+"Dao.registrarSubsanacion(historicoObservacion);\r\n");
		buffer.append("\t}\r\n");
		
		if(tarea.isObservarFuncion()==false){
			buffer.append("public Object registrarObservacion(HttpServletRequest request)\n");
			buffer.append("		throws Exception {\n");
			buffer.append("	return null;\n");
			buffer.append("}\n");
			buffer.append("public boolean eliminarObservacion(HttpServletRequest request)\n");
			buffer.append("		throws Exception {\n");
			buffer.append("	return false;\n");
			buffer.append("}\n");
			buffer.append("public boolean confirmarObservacion(HttpServletRequest request)\n");
			buffer.append("		throws Exception {\n");
			buffer.append("	return false;\n");
			buffer.append("}\n");
		}
		return buffer.toString();
	}
	
	private String registrarHistorialDocumento(Tarea tarea) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic Object registrarHistorialDocumento(HttpServletRequest request, java.util.Map<String, String> mapRequest, java.util.Map mapInputStream)  throws Exception{\r\n");
		buffer.append("\t\tUsuario usuario =  obtenerUsuario(request);\r\n");
		
		buffer.append("\t\tHistoricoDocumento historicoDocumento = new HistoricoDocumento();\r\n");
		buffer.append("\t\thistoricoDocumento.setPiid(mapRequest.get(\"piid\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoTarea(mapRequest.get(\"codigoTarea\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setDescripcionTarea(mapRequest.get(\"descripcionTarea\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoTipoDocumento(Integer.parseInt(mapRequest.get(\"codigoTipoDocumento\")));\r\n");
		buffer.append("\t\thistoricoDocumento.setComentario(mapRequest.get(\"comentario\"));\r\n");
		buffer.append("\t\tif(mapRequest.get(\"adicional\")!=null){\r\n");
		buffer.append("\t\t	historicoDocumento.setAdicional(Boolean.parseBoolean(mapRequest.get(\"adicional\")));\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\thistoricoDocumento.setSoloLectura(Boolean.parseBoolean(mapRequest.get(\"soloLectura\")));\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreUsuario(usuario.getNombre());\r\n");
		buffer.append("\t\thistoricoDocumento.setUsuario(usuario.getCodigo());\r\n");
		buffer.append("\t\thistoricoDocumento.setFecha(new Timestamp(new java.util.Date().getTime()));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoDocumento("+tarea.getClase().toLowerCase()+"Dao.registrarHistorialDocumento(historicoDocumento));\r\n");
		buffer.append("\t\thistoricoDocumento = cargarHistoricoDocumento(historicoDocumento,mapRequest);\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreDocumento(ArchivoUtil.guardarArchivo((java.io.InputStream) mapInputStream.get(mapRequest.get(\"nombreObjeto\")), mapRequest.get(\"nombreOriginal\"), historicoDocumento.getRutaDocumento(), historicoDocumento.getNombreDocumento()));\r\n");
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.actualizarHistorialDocumento(historicoDocumento);\r\n");
		buffer.append("\t\treturn historicoDocumento;\r\n");
		buffer.append("\t}\r\n");
		return buffer.toString();
	}
	
	private String eliminarHistorialDocumento(Tarea tarea) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic void eliminarHistorialDocumento(HttpServletRequest request) throws Exception{\r\n");
		buffer.append("\t\tHistoricoDocumento historicoDocumento = new HistoricoDocumento();\r\n");
		buffer.append("\t\thistoricoDocumento.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoDocumento(Integer.parseInt(request.getParameter(\"codigoDocumento\")));\r\n");
		buffer.append("\t\thistoricoDocumento = cargarHistoricoDocumento(historicoDocumento,request);\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreDocumento(request.getParameter(\"nombreDocumento\"));\r\n");
		buffer.append("\t\tArchivoUtil.eliminarArchivo(historicoDocumento.getRutaDocumento(), historicoDocumento.getNombreDocumento());\r\n");
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.eliminarHistorialDocumento(historicoDocumento);\r\n");
		buffer.append("\t}\r\n");
		return buffer.toString();
	}
	
	private String cargarHistoricoDocumento(Tarea tarea) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic String obtenerNombreRutaDocumento(HttpServletRequest request) throws Exception {\r\n");
		buffer.append("\t\tHistoricoDocumento historicoDocumento = new HistoricoDocumento();\r\n");
		buffer.append("\t\thistoricoDocumento = cargarHistoricoDocumento(historicoDocumento,request);\r\n");
		buffer.append("\t\treturn historicoDocumento.getRutaDocumento();\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic abstract HistoricoDocumento cargarHistoricoDocumento(HistoricoDocumento historicoDocumento,java.util.Map<String, String> mapRequest)throws Exception;\r\n");
		buffer.append("\tpublic abstract HistoricoDocumento cargarHistoricoDocumento(HistoricoDocumento historicoDocumento,HttpServletRequest request)throws Exception;\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoMetodoCompletar(Proyecto proyecto, Tarea tarea, Connection connection){
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic ClientObjectWrapper completarPoblarEntrada(HttpServletRequest request, HttpServletResponse response,ClientObjectWrapper clientObjectWrapper) throws Exception{\r\n");
		Clase padre = tarea.getClasePadre();
		
		if(tarea.getClasePadre()!=null){
			if(tarea.getAtributosCompletar()!=null){
				Clase clase = tarea.getClasePadre();
				buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
				buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setPiid(request.getParameter(\"piid\"));\r\n");
				buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+" = "+tarea.getClase().toLowerCase()+"Dao.consultaCompletar("+clase.getJavaClase().toLowerCase()+");\r\n\r\n");
			}
				
			List<Clase> clases = tarea.getClasesCompletar();
			for (int i = 0; i < clases.size(); i++) {
				Clase clase = clases.get(i);
				if(clase.getJavaClase().equalsIgnoreCase(padre.getJavaClase())==false){
					buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+"(pre"+tarea.getClase()+"Util.cargaCompletar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+", obtenerUsuario(request), request));\r\n");
				} else {
					buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+" = pre"+tarea.getClase()+"Util.cargaCompletar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+", obtenerUsuario(request), request);\r\n");
				}
			}
		}
		buffer.append("\t\tpre"+tarea.getClase()+"Util.completarDO((DataObject)clientObjectWrapper.getObject(), "+padre.getJavaClase().toLowerCase()+");\r\n");
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.completarTarea("+tarea.getClasePadre().getJavaClase().toLowerCase()+");\r\n");
		buffer.append("\t\treturn clientObjectWrapper;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoMetodoTrabajar(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic Object trabajar(HttpServletRequest request, ClientObjectWrapper clientObjectWrapperEntrada, ClientObjectWrapper clientObjectWrapperSalida) throws Exception{\r\n\r\n"); 
		buffer.append("\t\tString tkiid = request.getParameter(\"tkiid\");\r\n");
		buffer.append("\t\tString piid = request.getParameter(\"piid\");\r\n");
		buffer.append("\t\tString codigoTarea = request.getParameter(\"codigoTarea\");\r\n");
		//Recuperamos las tablas 
		if(tarea.getClasePadre()!=null){
			Clase clase = tarea.getClasePadre(); //falta recuperar la clase principal de la tabla
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setPiid(piid);\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+" = "+tarea.getClase().toLowerCase()+"Dao.verDetalle("+clase.getJavaClase().toLowerCase()+");\r\n\r\n");
			
			/*if(tarea.isObservarFuncion()){
				buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setTipoObservaciones("+tarea.getClase().toLowerCase()+"Dao.obtenerTipoObservaciones(request.getParameter(\"codigoTarea\"))); // SUBSANACION\r\n");
			}*/
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setHistoricoObservaciones("+tarea.getClase().toLowerCase()+"Dao.consultarObservaciones("+clase.getJavaClase().toLowerCase()+".getPiid()));\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setExcepciones("+tarea.getClase().toLowerCase()+"Dao.consultarExcepcionxObservacion("+clase.getJavaClase().toLowerCase()+".getHistoricoObservaciones()));\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setTareaHistoriales("+tarea.getClase().toLowerCase()+"Dao.obtenerHistorial("+clase.getJavaClase().toLowerCase()+".getPiid()));\r\n");
			
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setDocumentos("+tarea.getClase().toLowerCase()+"Dao.obtenerDocumentos(codigoTarea));\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setHistorialDocumento("+tarea.getClase().toLowerCase()+"Dao.obtenerHistorialDocumento(piid));\r\n");
		}
		
		buffer.append("\t\tif(clientObjectWrapperSalida.getObject()==null){\r\n");
		buffer.append("\t\t\tBpmTareaUtil bpmTareaUtil = new BpmTareaUtil();\r\n");
		buffer.append("\t\t\tclientObjectWrapperSalida = bpmTareaUtil.crearMensajeSalida(getHumanTaskManagerService(), tkiid);\r\n");
		buffer.append("\t\t\tDataObject doGuardado = (DataObject)clientObjectWrapperSalida.getObject();\r\n");
		buffer.append("\t\t\tdoGuardado.createDataObject(0);\r\n");
		
		String claseTareaTrabajar = "null";
		if(tarea.getClasePadre()!=null){
			claseTareaTrabajar = tarea.getClasePadre().getJavaClase();
		}
		
		buffer.append("\t\t\tguardarMensajeEntrada("+claseTareaTrabajar.toLowerCase()+", clientObjectWrapperEntrada);\r\n");
		buffer.append("\t\t\tbpmTareaUtil.guardar(getHumanTaskManagerService(), tkiid, clientObjectWrapperSalida);\r\n");
		
		buffer.append("\t\t}\r\n");
		
		buffer.append("\t\treturn "+tarea.getClasePadre().getJavaClase().toLowerCase()+";\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoMetodoGuardarMensajeEntrada(Tarea tarea, Connection conn){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic void guardarMensajeEntrada("+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+", ClientObjectWrapper clientObjectWrapperEntrada) throws Exception{\r\n\r\n");
		
		if(tarea.getObjetosBPMTrabajar().size()>0){
			buffer.append("\t\tDataObject dataObject = (DataObject)clientObjectWrapperEntrada.getObject();\r\n");
		}
		
		for(int x=0; x<tarea.getObjetosBPMTrabajar().size(); x++){
			Clase clase = tarea.getObjetosBPMTrabajar().get(x).getClase();
			if(clase.getJavaClase().equals(tarea.getClasePadre().getJavaClase())==false){
				buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = "+tarea.getClasePadre().getJavaClase().toLowerCase()+".get"+clase.getJavaClase()+"();\r\n");
				buffer.append("\t\tif("+clase.getJavaClase().toLowerCase()+"==null){\r\n");
				buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
				buffer.append("\t\t}\r\n");
				
			}
		}	
			
		for (int i = 0; i < tarea.getObjetosBPMTrabajar().size(); i++) {
			ObjetoBPM objetosBPM = tarea.getObjetosBPMTrabajar().get(i);
			if(objetosBPM.getAtributosTarea()!=null && objetosBPM.getAtributosTarea().size()>0){
				buffer.append("\t\tDataObject "+objetosBPM.getNombre()+"DO = dataObject.getDataObject(\""+objetosBPM.getNombre()+"\");\r\n");
				buffer.append("\t\tif("+objetosBPM.getNombre()+"DO!=null){\r\n");
				
				for (int j = 0; j < objetosBPM.getAtributosTarea().size(); j++) {
					AtributoTarea atributo = objetosBPM.getAtributosTarea().get(j);
					if(atributo.getTipo().equalsIgnoreCase("java.sql.Date")){
						buffer.append("\t\t\t\tif("+atributo.getProcesoObjeto()+"DO.get"+Validaciones.nombreVariable(atributo.getProcesoObjetoTipoDato())+"(\""+atributo.getProcesoObjetoAtributo()+"\")!=null){\r\n");
						buffer.append("\t\t\t\t\t"+objetosBPM.getClase().getJavaClase().toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(new java.sql.Date("+atributo.getProcesoObjeto()+"DO.get"+Validaciones.nombreVariable(atributo.getProcesoObjetoTipoDato())+"(\""+atributo.getProcesoObjetoAtributo()+"\").getTime()));\r\n");
						buffer.append("\t\t\t}\r\n");
					}else{
						buffer.append("\t\t\t"+objetosBPM.getClase().getJavaClase().toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"("+atributo.getProcesoObjeto()+"DO.get"+Validaciones.nombreVariable(atributo.getProcesoObjetoTipoDato())+"(\""+atributo.getProcesoObjetoAtributo()+"\"));\r\n");
					}
				}
				buffer.append("\t\t\t"+tarea.getClasePadre().getJavaClase().toLowerCase()+".set"+objetosBPM.getClase().getJavaClase()+"("+objetosBPM.getClase().getJavaClase().toLowerCase()+");\r\n");
				buffer.append("\t\t}\r\n");
			}
		}
			
		
		buffer.append("\r\n");
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.registarTarea("+tarea.getClasePadre().getJavaClase().toLowerCase()+");\r\n\r\n");
		
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoPlantillaInterface(Tarea tarea){
		
		StringBuffer buffer = new StringBuffer();
	
		buffer.append("package "+tarea.getPaquete()+";\r\n\r\n");
		buffer.append("import javax.ejb.Local;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.ejb.TareaBase;\r\n\r\n");

		buffer.append("@Local\r\n");
		buffer.append("public interface "+tarea.getClase()+" extends TareaBase{\r\n\r\n");

		buffer.append("}\r\n");
		return buffer.toString();
	}

	private String contenidoArchivoPlantillaTemplate(Tarea tarea){
		
		StringBuffer buffer = new StringBuffer();
	
		buffer.append("package "+tarea.getPaquete()+";\r\n\r\n");
		buffer.append("import javax.servlet.http.HttpServletRequest;\r\n");
		buffer.append("import javax.servlet.http.HttpServletResponse;\r\n\r\n");
		buffer.append("import javax.ejb.Stateless;\r\n\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.MensajeValidacion;\r\n\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n\r\n");
		
		buffer.append("@Stateless\r\n");
		buffer.append("public class "+tarea.getClase()+"Impl extends Pre"+tarea.getClase()+"Impl implements "+tarea.getClase()+"{\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic Object completarPoblarSalida(HttpServletRequest request, HttpServletResponse response) throws Exception {\r\n");
		buffer.append("\t\treturn new MensajeValidacion(true);\r\n");
		buffer.append("\t}\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic Object cancelarPoblarSalida(HttpServletRequest request, HttpServletResponse response) throws Exception {\r\n");
		buffer.append("\t\treturn new MensajeValidacion(true);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic Object rechazarPoblarSalida(HttpServletRequest request, HttpServletResponse response) throws Exception {\r\n");
		buffer.append("\t\treturn new MensajeValidacion(true);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic Object observarPoblarSalida(HttpServletRequest request, HttpServletResponse response) throws Exception {\r\n");
		buffer.append("\t\treturn new MensajeValidacion(true);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic HistoricoDocumento cargarHistoricoDocumento(HistoricoDocumento historicoDocumento,java.util.Map<String, String> mapRequest) {\r\n");
		buffer.append("\t\thistoricoDocumento.setRutaDocumento(\"D://temp\");\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreDocumento(\"BE_CF_EMCF_\"+historicoDocumento.getCodigoDocumento());\r\n");
		buffer.append("\t\treturn historicoDocumento;\r\n");
		buffer.append("\t}\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic HistoricoDocumento cargarHistoricoDocumento(HistoricoDocumento historicoDocumento,HttpServletRequest request) {\r\n");
		buffer.append("\t\thistoricoDocumento.setRutaDocumento(\"D://temp\");\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreDocumento(\"BE_CF_EMCF_\"+historicoDocumento.getCodigoDocumento());\r\n");
		buffer.append("\t\treturn historicoDocumento;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		if(tarea.isSubsanarFuncion()){
			buffer.append("\t// OBSERVACIONES ------\r\n");
			buffer.append("\t@Override\r\n");
			buffer.append("\tpublic String obtenerDirectorioDestino() {\r\n");
			buffer.append("\t\treturn null;\r\n");
			buffer.append("\t}\r\n\r\n");
			
			buffer.append("\t@Override\r\n");
			buffer.append("\tpublic String obtenerNuevoNombreArchivo(String nombreAdicional) {\r\n");
			buffer.append("\t\treturn null;\r\n");
			buffer.append("\t}\r\n");
			buffer.append("\t// --------------------\r\n");
		}

		buffer.append("}\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoClaseDao(Proyecto proyecto, Tarea tarea, Connection connection) throws Exception{
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tarea.getPaquete()+".dao;\r\n\r\n");
		
		buffer.append("import java.util.List;\r\n\r\n");
		
		buffer.append("import javax.ejb.Local;\r\n\r\n");
		
		if(tarea.getAtributosTrabajar()!=null && tarea.getAtributosTrabajar().size()>0){
			buffer.append("import "+tarea.getAtributosTrabajar().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		}else{
			buffer.append("import "+proyecto.getProcesos().get(0).getAtributosEntrada().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		}
		if(tarea.isFlujoCancelar()){
			buffer.append("import pe.com.financiero.bpm.core.modelo.TareaCancelada;\r\n");
		}
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.TareaHistorial;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.Documento;\r\n");
		
		if(tarea.isFlujoRechazar()){
			buffer.append("import pe.com.financiero.bpm.core.modelo.TareaRechazo;\r\n\r\n");
		}
		//OBSERVACIONES
		buffer.append("import pe.com.financiero.bpm.core.modelo.Excepcion;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoObservacion;\r\n");

		//logica para obtener los parametros del metodo cancelar
		Map<String, Atributo> mapRechazar = new HashMap<String, Atributo>();
		Map<String, Atributo> mapRechazar2 = new HashMap<String, Atributo>();
		StringBuffer parametrosRechazar = new StringBuffer();
		int iRechazar = 0;
		for (int x = 0; x < tarea.getAtributosRechazar().size(); x++) {
			AtributoTarea atributo =tarea.getAtributosRechazar().get(x); 
			String paquete 	= atributo.getClase().getJavaPaquete();
			String clase 	= atributo.getClase().getJavaClase();
			
			if(mapRechazar.containsKey(paquete+clase)==false){
				mapRechazar.put(paquete+clase, atributo);
			}
			
			if(mapRechazar2.containsKey(paquete+clase)==false){
				mapRechazar2.put(paquete+clase, atributo);
				if(iRechazar!=0){
					parametrosRechazar.append(", ");
				}
				parametrosRechazar.append(clase+" "+clase.toLowerCase());
				iRechazar++;
			}
		}
		
		//logica para obtener los parametros del metodo cancelar
		Map<String, Atributo> mapObservar = new HashMap<String, Atributo>();
		Map<String, Atributo> mapObservar2 = new HashMap<String, Atributo>();
		StringBuffer parametrosObservar = new StringBuffer();
		int iObservar = 0;
		for (int x = 0; x < tarea.getAtributosObservar().size(); x++) {
			AtributoTarea atributo =tarea.getAtributosObservar().get(x); 
			String paquete 	= atributo.getClase().getJavaPaquete();
			String clase 	= atributo.getClase().getJavaClase();
			
			if(mapObservar.containsKey(paquete+clase)==false){
				mapObservar.put(paquete+clase, atributo);
			}
			
			if(mapObservar2.containsKey(paquete+clase)==false){
				mapObservar2.put(paquete+clase, atributo);
				if(iObservar!=0){
					parametrosObservar.append(", ");
				}
				parametrosObservar.append(clase+" "+clase.toLowerCase());
				iObservar++;
			}
		}
		
		//logica para obtener los parametros del metodo cancelar
		Map<String, Atributo> mapCancelar = new HashMap<String, Atributo>();
		Map<String, Atributo> mapCancelar2 = new HashMap<String, Atributo>();
		StringBuffer parametrosCancelar = new StringBuffer();
		int iCancelar = 0;
		for (int x = 0; x < tarea.getAtributosCancelar().size(); x++) {
			AtributoTarea atributo =tarea.getAtributosCancelar().get(x); 
			String paquete 	= atributo.getClase().getJavaPaquete();
			String clase 	= atributo.getClase().getJavaClase();
			
			if(mapCancelar.containsKey(paquete+clase)==false){
				mapCancelar.put(paquete+clase, atributo);
			}
			
			if(mapCancelar2.containsKey(paquete+clase)==false){
				mapCancelar2.put(paquete+clase, atributo);
				if(iCancelar!=0){
					parametrosCancelar.append(", ");
				}
				parametrosCancelar.append(clase+" "+clase.toLowerCase());
				iCancelar++;
			}
		}
		
		buffer.append("@Local\r\n");
		buffer.append("public interface "+tarea.getClase()+"Dao {\r\n\r\n");
		buffer.append("\tvoid registarTarea("+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception;\r\n");
		buffer.append("\tvoid registraHistorial(TareaHistorial tareaHistorial) throws Exception;\r\n");
		buffer.append("\tList<TareaHistorial> obtenerHistorial(String piid) throws Exception;\r\n");
		buffer.append("\t"+new CrearDao().obtenerInterfaseDao(tarea.getConsultaTrabajar(), "verDetalle", connection)+"\r\n\r\n");
		buffer.append("\t"+new CrearDao().obtenerInterfaseDao(tarea.getConsultaCompletar(), "consultaCompletar", connection)+"\r\n\r\n");
		buffer.append("\tvoid completarTarea("+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception;\r\n");
		buffer.append("\tvoid guardarTarea("+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception;\r\n");
		
		if(tarea.isFlujoCancelar()){
			buffer.append("\tvoid cancelarTarea(TareaCancelada tareaCancelada, "+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception;\r\n");
		}

		if(tarea.isFlujoRechazar()){
			buffer.append("\tvoid rechazarTarea(TareaRechazo tareaRechazo, "+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception;\r\n");
		}
		
		if(tarea.isFlujoObservar()){
			buffer.append("\tvoid observarTarea("+parametrosObservar.toString()+") throws Exception;\r\n");
		}
		
		// OBSERVACIONES -------------- DAO
		if(tarea.isObservarFuncion()){
			//buffer.append("\tboolean registrarObservacion(HistoricoObservacion historicoObservacion) throws Exception;\r\n");
			//buffer.append("\tboolean eliminarObservacion(String codigoObservacion,String piid,String codigoTarea) throws Exception;\r\n");
			//buffer.append("\tboolean confirmarObservacion(String codigoObservacion,String piid,String codigoTarea,String tipoConfirmado,String comentarioObservacion,String Estado) throws Exception;\r\n");
			//buffer.append("\tpublic java.util.Map<String, String> obtenerTipoObservaciones(String codigoTarea) throws Exception;\r\n");
		}
		if(tarea.isSubsanarFuncion()){
			//buffer.append("\tjava.util.Map<String, String> cargarTipoDocumento(String codigoObservacion,String codigoExcepcion) throws Exception;\r\n");
			//buffer.append("\tboolean registrarSubsanacion(HistoricoObservacion historicoObservacion) throws Exception;\r\n");
		}
		
		buffer.append("\tList<Documento> obtenerDocumentos(String parameter) throws Exception;\r\n");
		buffer.append("\tint registrarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception;\r\n");
		buffer.append("\tvoid actualizarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception;\r\n");
		buffer.append("\tvoid eliminarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception;\r\n");
		buffer.append("\tList<HistoricoDocumento> obtenerHistorialDocumento(String piid) throws Exception;\r\n\r\n");
		
		buffer.append("\t// OBSERVACIONES y SUBSANACIONES\r\n");
		buffer.append("\tList<HistoricoObservacion> consultarObservaciones(String piid) throws Exception;\r\n");
		buffer.append("\tList<Excepcion> consultarExcepcionxObservacion(List<HistoricoObservacion> observaciones) throws Exception;\r\n");
	
		buffer.append("}");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoClaseDaoImpl(Tarea tarea) throws Exception{
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("package "+tarea.getPaquete()+".dao.impl;\r\n\r\n");
		
		buffer.append("import java.sql.Connection;\r\n");
		buffer.append("import javax.annotation.Resource;\r\n");
		buffer.append("import javax.ejb.Stateless;\r\n");
		buffer.append("import javax.sql.DataSource;\r\n\r\n");
		
		buffer.append("import "+tarea.getPaquete()+".dao."+ tarea.getClase()+"Dao;\r\n\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		int y = 0;
		for (int x = 0; x < tarea.getAtributosTrabajar().size(); x++) {
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(x);
			String paquete = 	atributo.getClase().getJavaPaquete();
			String clase = 		atributo.getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributo);
				
				if(y==0){
					parametros.append(clase+" "+clase.toLowerCase());
				}else{
					parametros.append(", "+clase+" "+clase.toLowerCase()+"  ");
				}
				
				y++;
			}
		}
		
		Map<String, Atributo> map2 = new HashMap<String, Atributo>();
		StringBuffer parametrosCompletar = new StringBuffer();
		int i = 0;
		for (int x = 0; x < tarea.getAtributosCompletar().size(); x++) {
			AtributoTarea atributo =tarea.getAtributosCompletar().get(x); 
			String paquete 	= atributo.getClase().getJavaPaquete();
			String clase 	= atributo.getClase().getJavaClase();
			
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributo);
			}
			
			if(map2.containsKey(paquete+clase)==false){
				map2.put(paquete+clase, atributo);
				if(i!=0){
					parametrosCompletar.append(", ");
				}
				parametrosCompletar.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		buffer.append("@Stateless\r\n");
		buffer.append("public class "+ tarea.getClase() +"DaoImpl extends Pre"+ tarea.getClase() +"DaoImpl implements "+ tarea.getClase()+"Dao" + " {\r\n\r\n");
		
		buffer.append("\t@Resource(name = \""+tarea.getDataSource()+"\")\r\n");
		buffer.append("\tDataSource dataSource;\r\n\r\n");
		
		buffer.append("\tpublic Connection getConnection() throws Exception{\r\n");
		buffer.append("\t\treturn dataSource.getConnection();\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("} \r\n\r\n");
		
		return buffer.toString();	
	}
	
	private String contenidoArchivoClasePreDaoImpl(Proyecto proyecto, Tarea tarea, Connection connection) throws Exception{
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("package "+tarea.getPaquete()+".dao.impl;\r\n\r\n");
		
		buffer.append("import java.sql.CallableStatement;\r\n");
		buffer.append("import java.sql.Connection;\r\n");
		buffer.append("import java.sql.ResultSet;\r\n");
		buffer.append("import java.util.ArrayList;\r\n");
		buffer.append("import java.util.List;\r\n\r\n");

		buffer.append("import pe.com.financiero.bpm.core.modelo.Documento;\r\n");
		if(tarea.isFlujoCancelar()){
			buffer.append("import pe.com.financiero.bpm.core.modelo.TareaCancelada;\r\n");
		}
		buffer.append("import pe.com.financiero.bpm.core.modelo.TareaHistorial;\r\n");
		if(tarea.isFlujoRechazar()){
			buffer.append("import pe.com.financiero.bpm.core.modelo.TareaRechazo;\r\n\r\n");
		}
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n\r\n");
		buffer.append("import "+tarea.getPaquete()+".dao."+ tarea.getClase()+"Dao;\r\n\r\n");

		buffer.append("import pe.com.financiero.bpm.core.modelo.Excepcion;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoObservacion;\r\n");
		
		buffer.append("import java.sql.Types;\r\n");
		buffer.append("import java.sql.SQLException;\r\n");
		
		if(tarea.getAtributosTrabajar()!=null && tarea.getAtributosTrabajar().size()>0){
			buffer.append("import "+tarea.getAtributosTrabajar().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		}else{
			buffer.append("import "+proyecto.getProcesos().get(0).getAtributosEntrada().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		}
		
		buffer.append("public abstract class Pre"+ tarea.getClase() +"DaoImpl"+" implements "+ tarea.getClase()+"Dao" + " {\r\n\r\n");
		buffer.append("\tpublic abstract Connection getConnection() throws Exception;\r\n\r\n");
		
		buffer.append(contenidoDaoImplRegistrar(tarea, connection));
		buffer.append(contenidoDaoImplCompletar(tarea));
		buffer.append(contenidoDaoImplHistorial(proyecto, tarea));
		buffer.append(contenidoDaoImplObtenerHistorial(proyecto, tarea));
		buffer.append(new CrearDao().obtenerClaseDaoImpl(tarea.getConsultaTrabajar(), "verDetalle", connection));
		buffer.append(new CrearDao().obtenerClaseDaoImpl(tarea.getConsultaCompletar(), "consultaCompletar", connection));
		buffer.append(contenidoDaoImplGuardar(tarea));
		if(tarea.isFlujoCancelar()){
			buffer.append(contenidoDaoImplCancelar(tarea));
		}
		if(tarea.isFlujoRechazar()){
			buffer.append(contenidoDaoImplRechazar(tarea));
		}
		if(tarea.isFlujoObservar()){
			buffer.append(contenidoDaoImplObservar(tarea));
		}
		// OBSERVACIONES -------------- DAOimp
		if(tarea.isObservarFuncion()){
			//buffer.append(contenidoDaoImplObservarFuncion(tarea));
		}
		if(tarea.isSubsanarFuncion()){
			//buffer.append(contenidoDaoImplSubsanarFuncion(tarea));
		}
		buffer.append(contenidoDaoImplObservarComunFuncion(proyecto, tarea));

		buffer.append(contenidoDaoImplRegistrarHistorialDocumento(proyecto, tarea));
		buffer.append(contenidoDaoImplEliminarHistorialDocumento(proyecto, tarea));
		buffer.append(contenidoDaoImplObtenerHistorialDocumento(proyecto));
		buffer.append(metodoDaoObtenerDocumentos(proyecto, tarea));
		buffer.append("} \r\n\r\n");
		
		return buffer.toString();	
	}
	
	private String contenidoDaoImplObservarComunFuncion(Proyecto proyecto, Tarea tarea){
		
		String esquema = proyecto.getTablas().get(0).getEsquema();
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic List<HistoricoObservacion> consultarObservaciones(String piid) throws Exception{\n");
		buffer.append("\t\tList<HistoricoObservacion> observaciones = new ArrayList<HistoricoObservacion>();\n");
		buffer.append("\t\ttry{\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + esquema + ".SP_PORTAL_HIS_OBSERVACION_CON_VALOR(?)}\");\n");
		buffer.append("\t\t\tcstmt.setString(1, piid);\n");
		buffer.append("\t\t\tcstmt.execute();\n");

		
		buffer.append("\t\t\tResultSet rs = cstmt.getResultSet();\n");
			
		buffer.append("\t\t\twhile (rs.next()) {\n");
				
		buffer.append("\t\t\t\tHistoricoObservacion observacion = new HistoricoObservacion();\n");
		buffer.append("\t\t\t\tobservacion.setPiid(rs.getString(\"PIID\"));\n");
		buffer.append("\t\t\t\tobservacion.setCodigoIteracion(rs.getString(\"COD_ITERACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setCodigoTarea(rs.getString(\"COD_TAREA\"));\n");
		buffer.append("\t\t\t\tobservacion.setCodigoObservacion(rs.getString(\"COD_OBSERVACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setCodigoTipoObservacion(rs.getInt(\"COD_TIP_OBSERVACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setNombreObservacion(rs.getString(\"NOM_OBSERVACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setComentarioObservacion(rs.getString(\"COM_OBSERVACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setUsuarioObservacion(rs.getString(\"USU_OBSERVACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setNombreUsuarioObservacion(rs.getString(\"USU_NOM_OBSERVACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setFechaObservacion(rs.getTimestamp(\"FEC_OBSERVACION\"));\n");
		buffer.append("\t\t\t\tobservacion.setCodigoTipoExcepcion(rs.getInt(\"COD_TIP_EXCEPCION\"));\n");
		buffer.append("\t\t\t\tobservacion.setNombreExcepcion(rs.getString(\"NOM_EXCEPCION\"));\n");
		buffer.append("\t\t\t\tobservacion.setComentarioExcepcion(rs.getString(\"COM_EXCEPCION\"));\n");
		buffer.append("\t\t\t\tobservacion.setUsuarioExcepcion(rs.getString(\"USU_EXCEPCION\"));\n");
		buffer.append("\t\t\t\tobservacion.setNombreUsuarioExcepcion(rs.getString(\"USU_NOM_EXCEPCION\"));\n");
		buffer.append("\t\t\t\tobservacion.setFechaExcepcion(rs.getTimestamp(\"FEC_EXCEPCION\"));\n");
		buffer.append("\t\t\t\tobservacion.setCodigoTipoDocumento(rs.getInt(\"COD_TIP_DOCUMENTO\"));\n");
		buffer.append("\t\t\t\tobservacion.setNombreDocumento(rs.getString(\"NOM_DOCUMENTO\"));\n");
		buffer.append("\t\t\t\tobservacion.setEstado(rs.getString(\"FLG_ESTADO\"));\n");
		buffer.append("\t\t\t\tif(rs.getString(\"FLG_CONFIRMADO\")!=null){\n");
		buffer.append("	\t\t\t\t\tobservacion.setEsConfirmado(rs.getBoolean(\"FLG_CONFIRMADO\"));\n");
		buffer.append("\t\t\t\t}\n");
		
		buffer.append("\t\t\t\tobservaciones.add(observacion);\n");
				
		buffer.append("\t\t\t}\n");
		buffer.append("\t\trs.close();\n");
		buffer.append("\t\tcstmt.close();\n");
		buffer.append("\t\t}catch(SQLException s){\n");
		buffer.append("\t\t\tthrow new Exception(s);\n");
		buffer.append("\t\t}finally{\n");
		buffer.append("\t\t\tif(getConnection()!= null)\n");
		buffer.append("\t\t\t\tgetConnection().close();\n");
		buffer.append("\t\t\t}\n");
		buffer.append("\t\treturn observaciones;\n");
		
		buffer.append("\t}\n");
		
		buffer.append("\tpublic List<Excepcion> consultarExcepcionxObservacion(List<HistoricoObservacion> observaciones) throws Exception{\n");
		buffer.append("\t\tList<Excepcion> excepciones = new ArrayList<Excepcion>();\n");
		buffer.append("\t\tif(observaciones.size()>0){\n");
		buffer.append("\t\t\tString tipoObservaciones = \"\";\n");
		buffer.append("\t\t\tfor(int i=0;i<observaciones.size();i++){\n");
		buffer.append("\t\t\t\ttipoObservaciones += \"'\"+observaciones.get(i).getCodigoTipoObservacion()+\"',\";\n");
		buffer.append("\t\t\t}\n");
		buffer.append("\t\ttipoObservaciones = tipoObservaciones.substring(0,tipoObservaciones.length()-1);\n");
		buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + esquema + ".SP_PORTAL_MAE_EXCEPCION_CON_LISTA(?)}\");\n");
		buffer.append("\t\t\tcstmt.setString(1, tipoObservaciones);\n");
		buffer.append("\t\t\tcstmt.execute();\n");
		buffer.append("\t\t\tResultSet rs = cstmt.getResultSet();\n");
		buffer.append("\t\t\twhile (rs.next()) {\n");
		buffer.append("\t\t\tExcepcion excepcion = new Excepcion();\n");
		buffer.append("\t\t\texcepcion.setCodigoExcepcion(rs.getInt(\"COD_TIP_EXCEPCION\"));\n");
		buffer.append("\t\t\texcepcion.setCodigoObservacion(rs.getInt(\"COD_TIP_OBSERVACION\"));\n");
		buffer.append("\t\t\texcepcion.setCodigoDocumento(rs.getInt(\"COD_TIP_DOCUMENTO\"));\n");
		buffer.append("\t\t\texcepcion.setNombreExcepcion(rs.getString(\"NOM_EXCEPCION\"));\n");
		buffer.append("\t\t\texcepcion.setDescripcion(rs.getString(\"DESCRIPCION\"));\n");
		buffer.append("\t\t\texcepcion.setFlgSustento(rs.getBoolean(\"FLG_DIG_SUSTENTO\"));\n");
					
		buffer.append("\t\t\texcepciones.add(excepcion);\n");
					
		buffer.append("\t\t\t}\n");
		buffer.append("\t\t\trs.close();\n");
		buffer.append("\t\t\tcstmt.close();\n");
		buffer.append("\t\t\tgetConnection().close();\n");
		
		buffer.append("\t\t}\n");
			
		buffer.append("\t\treturn excepciones;\n");
		
		buffer.append("\t}\n");
		

		return buffer.toString();
		
	}

	private String contenidoDaoImplRegistrarHistorialDocumento(Proyecto proyecto, Tarea tarea) throws Exception {
		
		String esquema = proyecto.getTablas().get(0).getEsquema();
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic int registrarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception {\r\n");
		buffer.append("\t\tint resultado = 0;\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + esquema + ".SP_PORTAL_HIS_DOCUMENTO_REGISTRO(?,?,?,?,?,?,?,?,?,?,?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(1, historicoDocumento.getPiid());\r\n");
		buffer.append("\t\t\tcstmt.setString(2, historicoDocumento.getCodigoTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(3, historicoDocumento.getDescripcionTarea());\r\n");
		buffer.append("\t\t\tcstmt.setInt(4, historicoDocumento.getCodigoTipoDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(5, historicoDocumento.getRutaDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(6, historicoDocumento.getNombreDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(7, historicoDocumento.getComentario());\r\n");
		buffer.append("\t\t\tcstmt.setString(8, historicoDocumento.getUsuario());\r\n");
		buffer.append("\t\t\tcstmt.setString(9, historicoDocumento.getNombreUsuario());\r\n");
		buffer.append("\t\t\tcstmt.setBoolean(10, historicoDocumento.isAdicional());\r\n");
		buffer.append("\t\t\tcstmt.setBoolean(11, historicoDocumento.isSoloLectura());\r\n");
		buffer.append("\t\t\tcstmt.setTimestamp(12, historicoDocumento.getFecha());\r\n");
		buffer.append("\t\t\tcstmt.registerOutParameter(13, Types.INTEGER);\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tresultado = cstmt.getInt(13);\r\n");
		buffer.append("\t\t\tcstmt.close();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn resultado;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		buffer.append("\tpublic void actualizarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception {\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + esquema + ".SP_PORTAL_HIS_DOCUMENTO_ACTUALIZAR(?,?,?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setInt(1, historicoDocumento.getCodigoDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(2, historicoDocumento.getPiid());\r\n");
		buffer.append("\t\t\tcstmt.setString(3, historicoDocumento.getCodigoTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(4, historicoDocumento.getRutaDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(5, historicoDocumento.getNombreDocumento());\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tcstmt.close();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();	
	}
	
	private String contenidoDaoImplEliminarHistorialDocumento(Proyecto proyecto, Tarea tarea) throws Exception {
		String esquema = proyecto.getTablas().get(0).getEsquema();
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic void eliminarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception {\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + esquema + ".SP_PORTAL_HIS_DOCUMENTO_ELIMINAR(?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setInt(1, historicoDocumento.getCodigoDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(2, historicoDocumento.getPiid());\r\n");
		buffer.append("\t\t\tcstmt.setString(3, historicoDocumento.getCodigoTarea());\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tcstmt.close();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();	
	}
	
	public String contenidoDaoImplObtenerHistorialDocumento(Proyecto proyecto) throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic List<HistoricoDocumento> obtenerHistorialDocumento(String piid) throws Exception {\r\n");
		buffer.append("\t\tList<HistoricoDocumento> listaDocumentos = new ArrayList<HistoricoDocumento>();\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + proyecto.getTablas().get(0).getEsquema() + ".SP_PORTAL_HIS_DOCUMENTO_CON_LISTA(?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(1, piid);\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tResultSet rs = cstmt.getResultSet();\r\n");
		buffer.append("\t\t\tHistoricoDocumento historicoDocumento = null;\r\n");
		buffer.append("\t\t\twhile (rs.next()) {\r\n");
		buffer.append("\t\t\t\thistoricoDocumento = new HistoricoDocumento();\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setCodigoHistoricoDocumento(rs.getLong(\"COD_HIS_DOCUMENTO\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setCodigoDocumento(rs.getInt(\"COD_DOCUMENTO\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setPiid(rs.getString(\"PIID\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setCodigoTarea(rs.getString(\"COD_TAREA\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setCodigoTipoDocumento(rs.getInt(\"COD_TIPO_DOCUMENTO\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setNombreTipoDocumento(rs.getString(\"NOM_TIPO_DOCUMENTO\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setNombreDocumento(rs.getString(\"NOM_DOCUMENTO\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setComentario(rs.getString(\"COMENTARIO\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setSoloLectura(rs.getBoolean(\"SOL_LECTURA\"));\r\n");
		buffer.append("\t\t\t\thistoricoDocumento.setAdicional(rs.getBoolean(\"FLG_ADICIONAL\"));\r\n");
		buffer.append("\t\t\t\tlistaDocumentos.add(historicoDocumento);\r\n");
		buffer.append("\t\t\t}\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tcstmt.close();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn listaDocumentos;\r\n");
		buffer.append("\t}\r\n");
		return buffer.toString();	
	}
	
	private String contenidoDaoImplRegistrar(Tarea tarea, Connection conn) throws Exception {
		StringBuffer buffer = new StringBuffer();
		TareaDaoImpl tareaDaoImpl = new TareaDaoImpl();
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		StringBuffer atributosParametros = new StringBuffer();
		StringBuffer atributosParametrosEliminar = new StringBuffer();
		StringBuffer atributosParametro = new StringBuffer();
		StringBuffer atributosParametroEliminar = new StringBuffer();
		
		List<AtributoTarea> atributosTrabajar = tarea.getAtributosTrabajar();
		List<AtributoTarea> atributosEntrada = new ArrayList<AtributoTarea>();
		
		int y = 0;
		
		for (int i = 0; i < tarea.getObjetosBPMTrabajar().size(); i++) {
			ObjetoBPM objetoBPM = tarea.getObjetosBPMTrabajar().get(i);
			for (int x = 0; x < objetoBPM.getAtributosTarea().size(); x++) {
				AtributoTarea atributo = objetoBPM.getAtributosTarea().get(x);
				String paquete = 	atributo.getClase().getJavaPaquete();
				String clase = 		atributo.getClase().getJavaClase();
				
				if(map.containsKey(paquete+clase)==false){
					map.put(paquete+clase, atributo);
					atributosEntrada.add(atributo);
					if(y==0){
						if(atributo.isBpmListado()){
							parametros.append("List<"+clase+"> "+clase.toLowerCase().trim()+"s");
							atributosParametros.append(clase.toLowerCase().trim()+"s.get(x)");
							atributosParametro.append(clase+" "+clase.toLowerCase().trim());
						}else{
							parametros.append(clase+" "+clase.toLowerCase().trim());
							atributosParametros.append(clase.toLowerCase().trim());
							atributosParametrosEliminar.append(clase.toLowerCase().trim());
							atributosParametro.append(clase+" "+clase.toLowerCase().trim());
							atributosParametroEliminar.append(clase+" "+clase.toLowerCase().trim());
						}
					}else{
						if(atributo.isBpmListado()){
							parametros.append(", List<"+clase+"> "+clase.toLowerCase().trim()+"s");
							atributosParametros.append(", "+clase.toLowerCase().trim()+"s.get(x)");
							atributosParametro.append(", "+clase+" "+clase.toLowerCase().trim());
						}else{
							parametros.append(", "+clase+" "+clase.toLowerCase());
							atributosParametros.append(", "+clase.toLowerCase().trim());
							atributosParametrosEliminar.append(", "+clase.toLowerCase().trim());
							atributosParametro.append(", "+clase+" "+clase.toLowerCase().trim());
							atributosParametroEliminar.append(", "+clase+" "+clase.toLowerCase().trim());
						}
					}
					
					y++;
				}
			}
		}
	
		//sacamos todas las tablas que deben registrarse segun el proceso
		Map<String, String> mapTablas = new HashMap<String, String>();
		for (int u = 0; u < tarea.getObjetosBPMTrabajar().size(); u++) {
			ObjetoBPM objetoBPM = tarea.getObjetosBPMTrabajar().get(u);
			for (int i = 0; i < objetoBPM.getAtributosTarea().size(); i++) {
				AtributoTarea atributo = objetoBPM.getAtributosTarea().get(i);
				if(atributo.getCampoSQLTarea()!=null && mapTablas.containsKey(atributo.getCampoSQLTarea().getTabla().getNombre())==false){
					mapTablas.put(atributo.getCampoSQLTarea().getTabla().getNombre(), atributo.getCampoSQLTarea().getTabla().getNombre());
				}
			}
		}
		
		List<AtributoTarea> atributosInput = new ArrayList<AtributoTarea>();
		List<AtributoTarea> atributosPKs = new ArrayList<AtributoTarea>();
		Map<Integer, AtributoTarea> atributosPKMap = new HashMap<Integer, AtributoTarea>();
		for (int i = 0; i < atributosTrabajar.size(); i++) {
			AtributoTarea atributo = atributosTrabajar.get(i);
			
			if(atributo.getCampoSQLTarea()!=null && atributo.getCampoSQLTarea().isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false && mapTablas.containsKey(atributo.getCampoSQLTarea().getTabla().getNombre())){
				atributosPKMap.put(atributo.getCodigo(), atributo);
				atributosInput.add(atributo);
				atributosPKs.add(atributo);
			}
		}
		
		for (int i = 0; i < tarea.getAtributosTrabajar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(i);
			if(atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosInput.add(atributo);
			}
		}
		
		StringBuffer numeroAtributosFK = new StringBuffer();
		boolean marcaOperacionListadoProceso = false;
		AtributoTarea atributoBaseListadoProceso = null;
		
		//logica para recuperar los parametros de una lista
		List<AtributoTarea> clasesLista = new ArrayList<AtributoTarea>();
		Map<String, String> mapFiltroClaseLista = new HashMap<String, String>();
		for (int i=0;i<tarea.getAtributosTrabajar().size();i++){
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(i);
			if(atributo.getCampoSQLTarea()!=null && atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
				if(atributo.isBpmListado()==true){
					if(mapFiltroClaseLista.containsKey(atributo.getClase().getJavaClase())==false){
						mapFiltroClaseLista.put(atributo.getClase().getJavaClase(), atributo.getClase().getJavaClase());
						clasesLista.add(atributo);
					}
				}
			}
		}
		
		for (int i=0;i<atributosInput.size();i++){
			AtributoTarea atributo = atributosInput.get(i);
			if(atributo.getCampoSQLTarea()!=null 
					&& atributo.getCampoSQLTarea().getTabla().getNombre()!=null 
					&& atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0
					&& atributo.getCampoSQLTarea().isTieneFuncion()==false){
				if(atributo.isBpmListado()==false){
					if(numeroAtributosFK.length()==0){
						numeroAtributosFK.append("?");
					}else{
						numeroAtributosFK.append(",?");
					}
				}else{
					marcaOperacionListadoProceso = true;
					atributoBaseListadoProceso = atributo;
				}
			}
		}
		
		//en caso no se tenga parametros de entreda se obtiene la clase padre
		boolean actualizaCabecera = false;
		
		for (int i = 0; i < atributosEntrada.size(); i++) {
			AtributoTarea atributo = atributosEntrada.get(i);
			if(atributo.isBpmListado()==false){
				actualizaCabecera = true;
				break;
			}
		}
		
		if(actualizaCabecera==false){
			Map<String, Atributo> tempMapAtributosEntrantes = new HashMap<String, Atributo>();
			for (int x = 0; x < atributosEntrada.size(); x++) {
				List<AtributoTarea> atributoEntradaPK = tareaDaoImpl.obtenerPKs(atributosEntrada.get(x),conn);
				for (int z = 0; z < atributoEntradaPK.size(); z++) {
					AtributoTarea atributoTemp = atributoEntradaPK.get(z);
					if(atributoTemp.getCampoSQLTarea().isFlgTieneFK()){
						atributoTemp = atributoTemp.getCampoSQLTarea().getFk().getAtributoTarea();
						if(tempMapAtributosEntrantes.containsKey(atributoTemp.getClase().getJavaClase())==false){
							tempMapAtributosEntrantes.put(atributoTemp.getClase().getJavaClase(), atributoTemp);
							String clase = atributoTemp.getClase().getJavaClase();
							if(parametros.length()==0){
								parametros.append(clase+" "+clase.toLowerCase());
							}else{
								parametros.append(", "+clase+" "+clase.toLowerCase());
							}
							
							if(atributosParametros.length()==0){
								atributosParametros.append(clase.toLowerCase().trim());
							}else{
								atributosParametros.append(", "+clase.toLowerCase().trim());
							}
							if(atributosParametrosEliminar.length()==0){
								atributosParametrosEliminar.append(clase.toLowerCase().trim());
							}else{
								atributosParametrosEliminar.append(", "+clase.toLowerCase().trim());
							}
							if(atributosParametro.length()==0){
								atributosParametro.append(clase+" "+clase.toLowerCase().trim());
							}else{
								atributosParametro.append(", "+clase+" "+clase.toLowerCase().trim());
							}
							if(atributosParametroEliminar.length()==0){
								atributosParametroEliminar.append(clase+" "+clase.toLowerCase().trim());
							}else{
								atributosParametroEliminar.append(", "+clase+" "+clase.toLowerCase().trim());
							}
						}
					}
				}
				
			}
		}
		
	 	buffer.append("\tpublic void registarTarea("+ tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception{\r\n");
		 if(tarea.getAtributosCompletar()!=null && tarea.getAtributosCompletar().size()>0){	
	 		if(actualizaCabecera){
		 		buffer.append("\t\ttry{\r\n");
		 		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosCompletar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_TRABAJAR("+numeroAtributosFK.toString()+")}\");\r\n");
		 		buffer.append(Validaciones.obtenerDaoCallableStatementTareaConObjetoPadre(tarea.getClasePadre(), tarea.getAtributosTrabajar()));
		 		buffer.append("\t\t\tcstmt.execute();\r\n");
		 		buffer.append("\t\t\tcstmt.close();\r\n");
		 		buffer.append("\t\t}catch(SQLException s){\r\n");
		 		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		 		buffer.append("\t\t}finally{\r\n");
		 		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		 		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		 		buffer.append("\t\t}\r\n");
		 	}
		 	//TODO dbravo revisar, error dado que solo elimina y carga una lista, mas listas no trabaja
		 	if(marcaOperacionListadoProceso){
		 		buffer.append("\t\teliminar"+atributoBaseListadoProceso.getClase().getJavaClase()+"("+atributosParametrosEliminar.toString()+");\r\n");
		 		buffer.append("\t\tfor (int x = 0; x < "+atributoBaseListadoProceso.getClase().getJavaClase().toLowerCase().trim()+"s.size(); x++) {\r\n");
		 		buffer.append("\t\t\tregistar"+atributoBaseListadoProceso.getClase().getJavaClase()+"("+atributosParametros.toString()+");\r\n");
		 		buffer.append("\t\t}\r\n");
		 	}
		 	
		 	if(clasesLista.size()>0){
		 		for (int i = 0; i < clasesLista.size(); i++) {
		 			AtributoTarea atributo = clasesLista.get(i);
					if(atributo.isRequiereEliminar()){
						buffer.append("\t\teliminar"+atributo.getClase().getJavaClase()+"("+atributosParametrosEliminar.toString()+");\r\n");
					}
				}
		 		for (int i = 0; i < clasesLista.size(); i++) {
		 			AtributoTarea atributo = clasesLista.get(i);
		 			
			 		buffer.append("\t\tfor (int x = 0; x < "+atributo.getClase().getJavaClase().toLowerCase().trim()+"s.size(); x++) {\r\n");
			 		StringBuffer parametrosListaTemp = new StringBuffer();
			 		for (int j = 0; j < clasesLista.size(); j++) {
			 			AtributoTarea atributoParametro = clasesLista.get(j);
			 			if(atributo.getClase().getJavaClase().equals(atributoParametro.getClase().getJavaClase())){
			 				parametrosListaTemp.append(atributoParametro.getClase().getJavaClase().toLowerCase()+"s.get(x)");
			 			}
					}
			 		
			 		buffer.append("\t\t\tregistar"+atributo.getClase().getJavaClase()+"("+parametrosListaTemp.toString()+", "+atributosParametrosEliminar.toString()+");\r\n");
			 		buffer.append("\t\t}\r\n");
		 		}
		 	}
		}
	
		buffer.append("\t} \r\n\r\n");
		if(marcaOperacionListadoProceso){
			for (int x = 0; x < tarea.getObjetosBPMTrabajar().size(); x++) {
				Clase clase = tarea.getObjetosBPMTrabajar().get(x).getClase();
				AtributoTarea atributoBase = clase.getAtributos().get(0);
				
				if(atributoBase.isBpmListado()){
					
					//si requiere que la lista se elimine previamente
					
					if(atributoBase.getCampoSQLTarea().isFlgRequiereEliminar()){
						List<AtributoTarea> atributosFK = tareaDaoImpl.obtenerFKs(tarea, conn);
						StringBuffer listaCamposFK = new StringBuffer();
						for (int i = 0; i < atributosFK.size(); i++) {
							AtributoTarea atributo = atributosFK.get(i);
							if(atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
								if(listaCamposFK.length()==0){
									listaCamposFK.append("?");
								}else{
									listaCamposFK.append(",?");
								}
							}
						}
						buffer.append("\tpublic void eliminar"+clase.getJavaClase()+"("+ atributosParametroEliminar.toString()+") throws Exception{\r\n");
						buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+atributoBase.getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_ELI_"+atributoBase.getCampoSQLTarea().getTabla().getNombre()+"("+listaCamposFK.toString()+")}\");\r\n");
						buffer.append(Validaciones.obtenerDaoCallableStatementReferenciaFK(atributosFK));
						buffer.append("\t\tcstmt.execute();\r\n");
					 	buffer.append("\t\tcstmt.close();\r\n");
					 	buffer.append("\t\tgetConnection().close();\r\n");
					 	buffer.append("\t}\r\n\r\n");
					}
					//obtenemos los campos a registrar
					List<AtributoTarea> atributos = tarea.getAtributosCompletar();
					List<AtributoTarea> atributosFiltrados = new ArrayList<AtributoTarea>();
					StringBuffer listaCampos = new StringBuffer();
					for (int i = 0; i < atributos.size(); i++) {
						AtributoTarea atributo = atributos.get(i);
						if(atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
							if(listaCampos.length()==0){
								listaCampos.append("?");
							}else{
								listaCampos.append(",?");
							}
						}
						//buscamos los FK que formen parte de la PK para reemplazarlo por el objeto padre
						if(atributo.getCampoSQLTarea().isFlgPK() && atributo.getCampoSQLTarea().isFlgTieneFK()){
							atributo = atributo.getCampoSQLTarea().getFk().getAtributoTarea();
						}
						atributosFiltrados.add(atributo);
					}
					
					StringBuffer parametrosListaTemp = new StringBuffer();
			 		for (int j = 0; j < clasesLista.size(); j++) {
			 			AtributoTarea atributoParametro = clasesLista.get(j);
			 			if(atributoBase.getClase().getJavaClase().equals(atributoParametro.getClase().getJavaClase())){
			 				parametrosListaTemp.append(atributoParametro.getClase().getJavaClase()+" "+atributoParametro.getClase().getJavaClase().toLowerCase());
			 			}
					}
					
					buffer.append("\tpublic void registar"+clase.getJavaClase()+"("+ parametrosListaTemp.toString()+", "+atributosParametroEliminar.toString()+") throws Exception{\r\n");
					buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+atributoBase.getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_"+atributoBase.getCampoSQLTarea().getTabla().getNombre()+"("+listaCampos.toString()+")}\");\r\n");
					buffer.append(Validaciones.obtenerDaoCallableStatementTarea(tarea.getClasePadre(), atributosFiltrados));
					buffer.append("\t\tcstmt.execute();\r\n");
				 	buffer.append("\t\tcstmt.close();\r\n");
				 	buffer.append("\t\tgetConnection().close();\r\n");
					buffer.append("\t}\r\n\r\n");
				}
			}
		}

		return buffer.toString();
	}

	private String contenidoDaoImplCompletar(Tarea tarea) throws Exception {
		StringBuffer buffer = new StringBuffer();
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		int y = 0;
		for (int x = 0; x < tarea.getAtributosCompletar().size(); x++) {
			AtributoTarea atributo = tarea.getAtributosCompletar().get(x);
			String paquete = 	atributo.getClase().getJavaPaquete();
			String clase = 		atributo.getClase().getJavaClase();
			
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributo);
				if(y==0){
					parametros.append(clase+" "+clase.toLowerCase());
				}else{
					parametros.append(", "+clase+" "+clase.toLowerCase());
				}
				
				y++;
			}
		}
		
		List<AtributoTarea> atributosInput = new ArrayList<AtributoTarea>();
		List<AtributoTarea> atributosPKs = new ArrayList<AtributoTarea>();
		Map<Integer, Atributo> atributosPKMap = new HashMap<Integer, Atributo>();
		for (int i = 0; i < tarea.getAtributosCompletar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosCompletar().get(i);
			
			if(atributo.getCampoSQLTarea()!=null && 
					atributo.getCampoSQLTarea().isFlgPK() && 
					atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosPKMap.put(atributo.getCodigo(), atributo);
				atributosInput.add(atributo);
				atributosPKs.add(atributo);
			}
		}
		
		for (int i = 0; i < tarea.getAtributosTrabajar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(i);
			if(atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosInput.add(atributo);
			}
		}
		
		StringBuffer listaCampos = new StringBuffer();
		for (int i=0;i<tarea.getAtributosCompletar().size();i++){
			AtributoTarea atributo 	= 	tarea.getAtributosCompletar().get(i);
			if(atributo.getCampoSQLTarea()!=null && 
					atributo.getCampoSQLTarea().getTabla().getNombre()!=null && 
					atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
				if(listaCampos.length()==0){
					listaCampos.append("?");
				}else{
					listaCampos.append(",?");
				}
			}
		}
		
	 	buffer.append("\tpublic void completarTarea("+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception{\r\n\r\n");
	 	if(tarea.getAtributosCompletar()!=null && tarea.getAtributosCompletar().size()>0){
	 		buffer.append("\t\ttry{\r\n");
	 		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosCompletar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_COMPLETAR("+listaCampos.toString()+")}\");\r\n");
		 	buffer.append(Validaciones.obtenerDaoCallableStatementTarea(tarea.getClasePadre(), tarea.getAtributosCompletar()));
		 	buffer.append("\t\t\tcstmt.execute();\r\n");
		 	buffer.append("\t\t\tcstmt.close();\r\n");
		 	buffer.append("\t\t}catch(SQLException s){\r\n");
		 	buffer.append("\t\t\tthrow new Exception(s);\r\n");
		 	buffer.append("\t\t}finally{\r\n");
		 	buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		 	buffer.append("\t\t\t\tgetConnection().close();\r\n");
		 	buffer.append("\t\t}\r\n");
	 	}
		buffer.append("\t} \r\n\r\n");
		return buffer.toString();
	}

	private String contenidoDaoImplGuardar(Tarea tarea) throws Exception {
		StringBuffer buffer = new StringBuffer();
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		int y = 0;
		for (int x = 0; x < tarea.getAtributosCompletar().size(); x++) {
			AtributoTarea atributo = tarea.getAtributosCompletar().get(x);
			String paquete = 	atributo.getClase().getJavaPaquete();
			String clase = 		atributo.getClase().getJavaClase();
			
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributo);
				if(y==0){
					parametros.append(clase+" "+clase.toLowerCase());
				}else{
					parametros.append(", "+clase+" "+clase.toLowerCase());
				}
				
				y++;
			}
		}
		
		List<AtributoTarea> atributosInput = new ArrayList<AtributoTarea>();
		List<AtributoTarea> atributosPKs = new ArrayList<AtributoTarea>();
		Map<Integer, Atributo> atributosPKMap = new HashMap<Integer, Atributo>();
		for (int i = 0; i < tarea.getAtributosCompletar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosCompletar().get(i);
			
			if(atributo.getCampoSQLTarea()!=null && 
					atributo.getCampoSQLTarea().isFlgPK() && 
					atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosPKMap.put(atributo.getCodigo(), atributo);
				atributosInput.add(atributo);
				atributosPKs.add(atributo);
			}
		}
		
		for (int i = 0; i < tarea.getAtributosTrabajar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(i);
			if(atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosInput.add(atributo);
			}
		}
		
		StringBuffer listaCampos = new StringBuffer();
		for (int i=0;i<tarea.getAtributosCompletar().size();i++){
			AtributoTarea atributo 	= 	tarea.getAtributosCompletar().get(i);
			if(atributo.getCampoSQLTarea()!=null && 
					atributo.getCampoSQLTarea().getTabla().getNombre()!=null && 
					atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
				if(listaCampos.length()==0){
					listaCampos.append("?");
				}else{
					listaCampos.append(",?");
				}
			}
		}
		
	 	buffer.append("\tpublic void guardarTarea("+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception{\r\n\r\n");
	 	if(tarea.getAtributosCompletar()!=null && tarea.getAtributosCompletar().size()>0){
	 		buffer.append("\t\ttry{\r\n");
		 	buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosCompletar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_COMPLETAR("+listaCampos.toString()+")}\");\r\n");
		 	buffer.append(Validaciones.obtenerDaoCallableStatementTarea(tarea.getClasePadre(), tarea.getAtributosCompletar()));
		 	buffer.append("\t\t\tcstmt.execute();\r\n");
		 	buffer.append("\t\t\tcstmt.close();\r\n");
		 	buffer.append("\t\t}catch(SQLException s){\r\n");
		 	buffer.append("\t\t\tthrow new Exception(s);\r\n");
		 	buffer.append("\t\t}finally{\r\n");
		 	buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		 	buffer.append("\t\t\t\tgetConnection().close();\r\n");
		 	buffer.append("\t\t}\r\n");
		}
		buffer.append("\t} \r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoDaoImplHistorial(Proyecto proyecto, Tarea tarea) throws Exception {
		String esquema = proyecto.getTablas().get(0).getEsquema();
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic void registraHistorial(TareaHistorial tareaHistorial) throws Exception{\r\n");
		buffer.append("\t\ttry{\r\n\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + esquema + ".SP_PORTAL_HIS_TAREA_REGISTRO(?,?,?,?,?,?,?,?,?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(	1,  tareaHistorial.getPIID());\r\n");
		buffer.append("\t\t\tcstmt.setString(	2,  tareaHistorial.getTKIID());\r\n");
		buffer.append("\t\t\tcstmt.setString(	3,  tareaHistorial.getCodigoTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(	4,  tareaHistorial.getDescripcionTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(	5,  tareaHistorial.getUsuarioPropietario());\r\n");
		buffer.append("\t\t\tcstmt.setString(	6,  tareaHistorial.getComentario());\r\n");
		buffer.append("\t\t\tcstmt.setString(	7,  tareaHistorial.getCodigoResultado());\r\n");
		buffer.append("\t\t\tcstmt.setString(	8, 	tareaHistorial.getDescripcionResultado());\r\n");
		buffer.append("\t\t\tcstmt.setTimestamp(	9, 	tareaHistorial.getFechaCreacion());\r\n");
		buffer.append("\t\t\tcstmt.setTimestamp(	10, tareaHistorial.getFechaInicio());\r\n");
		buffer.append("\t\t\tcstmt.setTimestamp(	11, tareaHistorial.getFechaTermino());\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tcstmt.close();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	
	private String contenidoDaoImplObtenerHistorial(Proyecto proyecto, Tarea tarea) throws Exception{
		String esquema = proyecto.getTablas().get(0).getEsquema();
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic List<TareaHistorial> obtenerHistorial(String piid) throws Exception{\r\n");
		buffer.append("\t\tList<TareaHistorial> tareas = new ArrayList<TareaHistorial>();\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + esquema +".SP_PORTAL_HIS_TAREA_CON_LISTA(?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(1, piid);\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n\r\n");
		
		
		buffer.append("\t\t\tResultSet rs = cstmt.getResultSet();\r\n");
		buffer.append("\t\t\twhile (rs.next()) {\r\n");
		
		buffer.append("\t\t\t\tTareaHistorial tareaHistorial = new TareaHistorial();\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setPIID(rs.getString(\"HIS_TAREA_PIID\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setTKIID(rs.getString(\"HIS_TAREA_TKIID\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setCodigoTarea(rs.getString(\"HIS_TAREA_COD_TAREA\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setDescripcionTarea(rs.getString(\"HIS_TAREA_DES_TAREA\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setUsuarioPropietario(rs.getString(\"HIS_TAREA_USU_PROPIETARIO\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setComentario(rs.getString(\"HIS_TAREA_COMENTARIO\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setCodigoResultado(rs.getString(\"HIS_TAREA_COD_RESULTADO\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setDescripcionResultado(rs.getString(\"HIS_TAREA_DES_RESULTADO\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setFechaCreacion(rs.getTimestamp(\"HIS_TAREA_FEC_CREACION\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setFechaInicio(rs.getTimestamp(\"HIS_TAREA_FEC_INICIO\"));\r\n");
		buffer.append("\t\t\t\ttareaHistorial.setFechaTermino(rs.getTimestamp(\"HIS_TAREA_FEC_TERMINO\"));\r\n");
		
		buffer.append("\t\t\t\ttareas.add(tareaHistorial);\r\n");
		buffer.append("\t\t\t}\r\n");
		buffer.append("\t\t\trs.close();\r\n");
		buffer.append("\t\t\tcstmt.close();\r\n");
		buffer.append("\t\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t\t}finally{\r\n");
		buffer.append("\t\t\t\tif(getConnection()!=null)\r\n");
		buffer.append("\t\t\t\t\tgetConnection().close();\r\n");
		buffer.append("\t\t\t}\r\n");
		buffer.append("\t\t\treturn tareas;\r\n");
		buffer.append("\t\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoMetodoHistorial(Tarea tarea) throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic void registrarHistorialTarea(HttpServletRequest request, Task task) throws Exception{\r\n");
		buffer.append("\t\tCalendar fechaCreacionCalendar = task.getStartTime();\r\n");
		buffer.append("\t\tfechaCreacionCalendar.setTimeZone(ZONA_HORARIA);\r\n");
		buffer.append("\t\tCalendar fechaCreacionInicio = task.getFirstActivationTime();\r\n");
		buffer.append("\t\tfechaCreacionInicio.setTimeZone(ZONA_HORARIA);\r\n");
		buffer.append("\t\tCalendar fechaCreacionTermino = task.getCompletionTime();\r\n");
		buffer.append("\t\tfechaCreacionTermino.setTimeZone(ZONA_HORARIA);\r\n");
		buffer.append("\t\tTareaHistorial tareaHistorial = new TareaHistorial();\r\n");
		buffer.append("\t\ttareaHistorial.setPIID(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\ttareaHistorial.setTKIID(request.getParameter(\"tkiid\"));\r\n");
		buffer.append("\t\ttareaHistorial.setUsuarioPropietario(task.getOwner());\r\n");
		buffer.append("\t\ttareaHistorial.setFechaCreacion(new Timestamp(fechaCreacionCalendar.getTimeInMillis()));\r\n");
		buffer.append("\t\ttareaHistorial.setFechaInicio(new Timestamp(fechaCreacionInicio.getTimeInMillis()));\r\n");
		buffer.append("\t\ttareaHistorial.setFechaTermino(new Timestamp(fechaCreacionTermino.getTimeInMillis()));\r\n");
		buffer.append("\t\ttareaHistorial.setCodigoTarea(CODIGO_TAREA);\r\n");
		buffer.append("\t\ttareaHistorial.setCodigoBanca(CODIGO_BANCA);\r\n");
		buffer.append("\t\ttareaHistorial.setCodigoProducto(CODIGO_PRODUCTO);\r\n");
		buffer.append("\t\ttareaHistorial.setCodigoProceso(CODIGO_PROCESO);\r\n");
		buffer.append("\t\ttareaHistorial.setDescripcionTarea(NOMBRE_TAREA);\r\n");
		buffer.append("\t\ttareaHistorial.setComentario(request.getParameter(\""+tarea.getWebParametroComentario()+"\"));\r\n");
		buffer.append("\t\ttareaHistorial.setCodigoResultado(\"0\");\r\n");
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.registraHistorial(tareaHistorial);\r\n");
		buffer.append("\t}\r\n");
		return buffer.toString();
	}
	
	private String contenidoServletProcesoController(Proyecto proyecto, List<Tarea> tareas){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("package "+proyecto.getJavaNombrePaqueteControlador()+";\r\n\r\n");
		buffer.append("import java.util.HashMap;\r\n");
		buffer.append("import java.util.Map;\r\n\r\n");
		buffer.append("import pe.com.financiero.bpm.core.controller.TareaController;\r\n\r\n");
		buffer.append("public class "+proyecto.getJavaPreSufijoControlador()+"TareaController extends TareaController{\r\n\r\n");
		buffer.append("\tprivate static final long serialVersionUID = 1L;\r\n\r\n");
		buffer.append("\tpublic Map<String, String> cargarLookupMap() {\r\n");
		buffer.append("\t\tMap<String, String> lookMap = new HashMap<String, String>();\r\n");
		for (int i = 0; i < tareas.size(); i++) {
			Tarea tarea = tareas.get(i);
			buffer.append("\t\tlookMap.put(\""+tarea.getPlantilla()+"\", \"ejblocal:"+tarea.getPaquete()+"."+tarea.getClase()+"\");\r\n");
		}
		buffer.append("\t\treturn lookMap;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic int getMaxFileSize() {\r\n");
		buffer.append("\t\treturn 1024*1024*1;\r\n");
		buffer.append("\t}\r\n\r\n");

		buffer.append("\tpublic int getMaxRequestSize() {\r\n");
		buffer.append("\t\treturn 1024*1024*1;\r\n");
		buffer.append("\t}\r\n\r\n");

		buffer.append("\tpublic int getThresholdSize() {\r\n");
		buffer.append("\t\treturn 1024*1024*1;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("}\r\n");
	
		return buffer.toString();
	}
	
	private String contenidodMetodoGuardar(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic MensajeValidacion guardar(HttpServletRequest request, HttpServletResponse response) throws Exception{\r\n");
		
		if(tarea.getClasePadre()!=null){
			Clase clase = tarea.getClasePadre(); //falta recuperar la clase principal de la tabla
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setPiid(request.getParameter(\"piid\"));\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+" = "+tarea.getClase().toLowerCase()+"Dao.consultaCompletar("+clase.getJavaClase().toLowerCase()+");\r\n\r\n");
		}
		Clase padre = tarea.getClasePadre();
		List<Clase> clases = Validaciones.filtrarClasesTarea(tarea.getAtributosCompletar());
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			String clasePadre = Validaciones.obtenerClaseHijaPadre(padre, clase);
			if(clasePadre.equalsIgnoreCase(clase.getJavaClase())){
				buffer.append("\t\t"+clasePadre+" = pre"+tarea.getClase()+"Util.cargaCompletar"+clase.getJavaClase()+"("+clasePadre+", obtenerUsuario(request), request);\r\n");
			}else{
				buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+"(pre"+tarea.getClase()+"Util.cargaCompletar"+clase.getJavaClase()+"("+clasePadre+", obtenerUsuario(request), request));\r\n");
			}
		}
		
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.guardarTarea("+tarea.getClasePadre().getJavaClase().toLowerCase()+");\r\n");
		buffer.append("\t\treturn new MensajeValidacion(true);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoMetodoCancelar(Proyecto proyecto, Tarea tarea, Connection connection){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic ClientObjectWrapper cancelarPoblarEntrada(HttpServletRequest request, HttpServletResponse response,ClientObjectWrapper clientObjectWrapper) throws Exception{\r\n");
		
		Clase padre = tarea.getClasePadre();
		
		if(tarea.getAtributosCancelar()!=null && tarea.getAtributosCancelar().size()>0){
			buffer.append("\t\t"+padre.getJavaClase()+" "+padre.getJavaClase().toLowerCase()+" = new "+padre.getJavaClase()+"();\r\n");
			buffer.append("\t\t"+padre.getJavaClase().toLowerCase()+".setPiid(request.getParameter(\"piid\"));\r\n");
			buffer.append("\t\t"+padre.getJavaClase().toLowerCase()+" = "+tarea.getClase().toLowerCase()+"Dao.consultaCompletar("+padre.getJavaClase().toLowerCase()+");\r\n\r\n");
		}
		
		List<Clase> clases = Validaciones.filtrarClasesTarea(tarea.getAtributosCancelar());
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			if(clase.getJavaClase().equalsIgnoreCase(padre.getJavaClase())==false){
				buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+"(pre"+tarea.getClase()+"Util.cargaCancelar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+", obtenerUsuario(request), request));\r\n");
			} else {
				buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+" = pre"+tarea.getClase()+"Util.cargaCancelar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+",  obtenerUsuario(request), request);\r\n");
			}
		}
		
		buffer.append("\t\tpre"+tarea.getClase()+"Util.completarDO((DataObject)clientObjectWrapper.getObject(), "+padre.getJavaClase().toLowerCase()+");\r\n");
		buffer.append("\t\tUsuario usuario = obtenerUsuario(request);\r\n");
		buffer.append("\t\tTareaCancelada tareaCancelada = new TareaCancelada();\r\n");
		buffer.append("\t\ttareaCancelada.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\ttareaCancelada.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("\t\ttareaCancelada.setNombreTarea(request.getParameter(\"nombreTarea\"));\r\n");
		buffer.append("\t\ttareaCancelada.setTipo(request.getParameter(\"cancelacion.tipo\"));\r\n");
		buffer.append("\t\ttareaCancelada.setComentario(request.getParameter(\"cancelacion.comentario\"));\r\n");
		buffer.append("\t\ttareaCancelada.setUsuario(usuario.getCodigo());\r\n");
		buffer.append("\t\ttareaCancelada.setNombreUsuario(usuario.getNombre());\r\n");
		buffer.append("\t\ttareaCancelada.setFecha(new java.sql.Timestamp(new java.util.Date().getTime()));\r\n");
		
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.cancelarTarea(tareaCancelada, "+padre.getJavaClase().toLowerCase()+");\r\n\r\n");
		
		
		buffer.append("\t\treturn clientObjectWrapper;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoMetodoSinCancelar(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic ClientObjectWrapper cancelarPoblarEntrada(HttpServletRequest request, HttpServletResponse response,ClientObjectWrapper clientObjectWrapper) throws Exception{\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoMetodoSinRechazar(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic ClientObjectWrapper rechazarPoblarEntrada(HttpServletRequest request, HttpServletResponse response,ClientObjectWrapper clientObjectWrapper) throws Exception{\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoMetodoSinObservar(Tarea tarea){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic ClientObjectWrapper observarPoblarEntrada(HttpServletRequest request, HttpServletResponse response,ClientObjectWrapper clientObjectWrapper) throws Exception{\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoDaoImplCancelar(Tarea tarea) throws Exception {
		StringBuffer buffer 		= new StringBuffer();
		/*
		Map<String, Atributo> map 	= new HashMap<String, Atributo>();
		StringBuffer parametros 	= new StringBuffer();
		int y = 0;
		for (int x = 0; x < tarea.getAtributosCancelar().size(); x++) {
			Atributo atributo = tarea.getAtributosCancelar().get(x);
			String paquete = 	atributo.getClase().getJavaPaquete();
			String clase = 		atributo.getClase().getJavaClase();
			
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributo);
				if(y==0){
					parametros.append(clase+" "+clase.toLowerCase());
				}else{
					parametros.append(", "+clase+" "+clase.toLowerCase());
				}
				
				y++;
			}
		}
		*/
		//logica para obtener los parametros del metodo cancelar
		Map<String, Atributo> mapCancelar = new HashMap<String, Atributo>();
		Map<String, Atributo> mapCancelar2 = new HashMap<String, Atributo>();
		StringBuffer parametrosCancelar = new StringBuffer();
		int iCancelar = 0;
		for (int x = 0; x < tarea.getAtributosCancelar().size(); x++) {
			AtributoTarea atributo =tarea.getAtributosCancelar().get(x); 
			String paquete 	= atributo.getClase().getJavaPaquete();
			String clase 	= atributo.getClase().getJavaClase();
			
			if(mapCancelar.containsKey(paquete+clase)==false){
				mapCancelar.put(paquete+clase, atributo);
			}
			
			if(mapCancelar2.containsKey(paquete+clase)==false){
				mapCancelar2.put(paquete+clase, atributo);
				if(iCancelar!=0){
					parametrosCancelar.append(", ");
				}
				parametrosCancelar.append(clase+" "+clase.toLowerCase());
				iCancelar++;
			}
		}
		
		
		List<AtributoTarea> atributosInput = new ArrayList<AtributoTarea>();
		List<AtributoTarea> atributosPKs = new ArrayList<AtributoTarea>();
		Map<Integer, Atributo> atributosPKMap = new HashMap<Integer, Atributo>();
		for (int i = 0; i < tarea.getAtributosCancelar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosCancelar().get(i);
			
			if(atributo.getCampoSQLTarea().isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosPKMap.put(atributo.getCodigo(), atributo);
				atributosInput.add(atributo);
				atributosPKs.add(atributo);
			}
		}
		
		for (int i = 0; i < tarea.getAtributosTrabajar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(i);
			if(atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosInput.add(atributo);
			}
		}
		
		StringBuffer listaCampos = new StringBuffer();
		for (int i=0;i<tarea.getAtributosCancelar().size();i++){
			AtributoTarea atributo 	= 	tarea.getAtributosCancelar().get(i);
			if(atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
				if(listaCampos.length()==0){
					listaCampos.append("?");
				}else{
					listaCampos.append(",?");
				}
			}
		}
		
	 	buffer.append("\tpublic void cancelarTarea(TareaCancelada tareaCancelada, "+ parametrosCancelar.toString()+") throws Exception{\r\n\r\n");
	 	if(tarea.getAtributosCancelar()!=null && tarea.getAtributosCancelar().size()>0){
	 		buffer.append("\t\ttry{\r\n");
	 		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosCancelar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_CANCELAR("+listaCampos.toString()+")}\");\r\n");
		 	buffer.append(Validaciones.obtenerDaoCallableStatementTarea(tarea.getClasePadre(), tarea.getAtributosCancelar()));
		 	buffer.append("\t\t\tcstmt.execute();\r\n");
		 	buffer.append("\t\t\tcstmt.close();\r\n");
		 	
		 	buffer.append("\t\t\tcstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosCancelar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_HIS_CANCELACION_REGISTRO(?,?,?,?,?,?,?,?)}\");\r\n");
		 	buffer.append("\t\t\tcstmt.setString(1, 	tareaCancelada.getPiid());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(2,	tareaCancelada.getCodigoTarea());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(3,	tareaCancelada.getNombreTarea());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(4, 	tareaCancelada.getTipo());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(5,	tareaCancelada.getComentario());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(6,	tareaCancelada.getUsuario());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(7,	tareaCancelada.getNombreUsuario());\r\n");
		 	buffer.append("\t\t\tcstmt.setTimestamp(8,tareaCancelada.getFecha());\r\n");
		 	buffer.append("\t\t\tcstmt.execute();\r\n");
		 	buffer.append("\t\t\tcstmt.close();\r\n");
		 	buffer.append("\t\t\t}catch(SQLException s){\r\n");
		 	buffer.append("\t\t\t\tthrow new Exception(s);\r\n");
		 	buffer.append("\t\t\t}finally{\r\n");
		 	buffer.append("\t\t\t\tif(getConnection()!=null)\r\n"); 
		 	buffer.append("\t\t\t\t\tgetConnection().close();\r\n");
		 	buffer.append("\t\t\t}\r\n");	
	 	}
		buffer.append("\t} \r\n\r\n");
		
		return buffer.toString();
	}

	private String contenidoDaoImplRechazar(Tarea tarea) throws Exception {
		StringBuffer buffer 		= new StringBuffer();
		
		List<AtributoTarea> atributosInput = new ArrayList<AtributoTarea>();
		List<AtributoTarea> atributosPKs = new ArrayList<AtributoTarea>();
		Map<Integer, Atributo> atributosPKMap = new HashMap<Integer, Atributo>();
		for (int i = 0; i < tarea.getAtributosRechazar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosRechazar().get(i);
			
			if(atributo.getCampoSQLTarea().isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosPKMap.put(atributo.getCodigo(), atributo);
				atributosInput.add(atributo);
				atributosPKs.add(atributo);
			}
		}
		
		for (int i = 0; i < tarea.getAtributosTrabajar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(i);
			if(atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosInput.add(atributo);
			}
		}
		
		StringBuffer listaCampos = new StringBuffer();
		for (int i=0;i<tarea.getAtributosRechazar().size();i++){
			AtributoTarea atributo 	= 	tarea.getAtributosRechazar().get(i);
			if(atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
				if(listaCampos.length()==0){
					listaCampos.append("?");
				}else{
					listaCampos.append(",?");
				}
			}
		}
		
		buffer.append("\tpublic void rechazarTarea(TareaRechazo tareaRechazo, "+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception{\r\n\r\n");
		
	 	if(tarea.getAtributosTrabajar()!=null && tarea.getAtributosTrabajar().size()>0){
	 		buffer.append("\t\ttry{\r\n");
	 		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosCompletar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_RECHAZAR("+listaCampos.toString()+")}\");\r\n");
		 	buffer.append(Validaciones.obtenerDaoCallableStatementTarea(tarea.getClasePadre(), tarea.getAtributosRechazar()));
		 	buffer.append("\t\t\tcstmt.execute();\r\n");
		 	buffer.append("\t\t\tcstmt.close();\r\n");
		 	
		 	buffer.append("\t\t\tcstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosCompletar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_HIS_RECHAZO_REGISTRO(?,?,?,?,?,?,?,?)}\");\r\n");
		 	buffer.append("\t\t\tcstmt.setString(1, 	tareaRechazo.getPiid());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(2,	tareaRechazo.getCodigoTarea());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(3,	tareaRechazo.getNombreTarea());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(4, 	tareaRechazo.getTipo());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(5,	tareaRechazo.getComentario());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(6,	tareaRechazo.getUsuario());\r\n");
		 	buffer.append("\t\t\tcstmt.setString(7,	tareaRechazo.getNombreUsuario());\r\n");
		 	buffer.append("\t\t\tcstmt.setTimestamp(8,tareaRechazo.getFecha());\r\n");
		 	buffer.append("\t\t\tcstmt.execute();\r\n");
		 	buffer.append("\t\t\tcstmt.close();\r\n");
		 	buffer.append("\t\t\t}catch(SQLException s){\r\n");
		 	buffer.append("\t\t\t\tthrow new Exception(s);\r\n");
		 	buffer.append("\t\t\t}finally{\r\n");
		 	buffer.append("\t\t\t\tif(getConnection()!=null)\r\n");
		 	buffer.append("\t\t\t\t\tgetConnection().close();\r\n");
		 	buffer.append("\t\t\t\t}\r\n");
		}
		buffer.append("\t} \r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoDaoImplObservar(Tarea tarea) throws Exception {
		StringBuffer buffer 		= new StringBuffer();
		
		Map<String, Atributo> map 	= new HashMap<String, Atributo>();
		StringBuffer parametros 	= new StringBuffer();
		int y = 0;
		for (int x = 0; x < tarea.getAtributosObservar().size(); x++) {
			AtributoTarea atributo = tarea.getAtributosObservar().get(x);
			String paquete = 	atributo.getClase().getJavaPaquete();
			String clase = 		atributo.getClase().getJavaClase();
			
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, atributo);
				if(y==0){
					parametros.append(clase+" "+clase.toLowerCase());
				}else{
					parametros.append(", "+clase+" "+clase.toLowerCase());
				}
				
				y++;
			}
		}
		
		List<AtributoTarea> atributosInput = new ArrayList<AtributoTarea>();
		List<AtributoTarea> atributosPKs = new ArrayList<AtributoTarea>();
		Map<Integer, Atributo> atributosPKMap = new HashMap<Integer, Atributo>();
		for (int i = 0; i < tarea.getAtributosObservar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosObservar().get(i);
			
			if(atributo.getCampoSQLTarea().isFlgPK() && atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosPKMap.put(atributo.getCodigo(), atributo);
				atributosInput.add(atributo);
				atributosPKs.add(atributo);
			}
		}
		
		for (int i = 0; i < tarea.getAtributosTrabajar().size(); i++) {
			AtributoTarea atributo = tarea.getAtributosTrabajar().get(i);
			if(atributosPKMap.containsKey(atributo.getCodigo())==false){
				atributosInput.add(atributo);
			}
		}
		
		StringBuffer listaCampos = new StringBuffer();
		for (int i=0;i<tarea.getAtributosObservar().size();i++){
			AtributoTarea atributo 	= 	tarea.getAtributosObservar().get(i);
			if(atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
				if(listaCampos.length()==0){
					listaCampos.append("?");
				}else{
					listaCampos.append(",?");
				}
			}
		}
		
	 	buffer.append("\tpublic void observarTarea("+ parametros.toString()+") throws Exception{\r\n\r\n");
	 	if(tarea.getAtributosObservar()!=null && tarea.getAtributosObservar().size()>0){
	 		buffer.append("\t\ttry{\r\n");
	 		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tarea.getAtributosObservar().get(0).getCampoSQLTarea().getTabla().getEsquema()+".SP_PORTAL_TAR_"+tarea.getAleas()+"_REG_OBSERVAR("+listaCampos.toString()+")}\");\r\n");
		 	buffer.append(Validaciones.obtenerDaoCallableStatementTarea(tarea.getClasePadre(), tarea.getAtributosObservar()));
		 	buffer.append("\t\t\tcstmt.execute();\r\n");
		 	buffer.append("\t\t\tcstmt.close();\r\n");
		 	buffer.append("\t\t}catch(SQLException s){\r\n");
		 	buffer.append("\t\t\tthrow new Exception(s);\r\n");
		 	buffer.append("\t\t}finally{\r\n");
		 	buffer.append("\t\t\tif(getConnection()!=null)\r\n");
		 	buffer.append("\t\t\t\tgetConnection().close();\r\n");
		 	buffer.append("\t\t}\r\n");
		}
		buffer.append("\t} \r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoMetodoRechazar(Proyecto proyecto, Tarea tarea, Connection connection){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic ClientObjectWrapper rechazarPoblarEntrada(HttpServletRequest request, HttpServletResponse response,ClientObjectWrapper clientObjectWrapper) throws Exception{\r\n");
		
		if(tarea.getCamposRechazar()!=null){
			Clase clase = tarea.getClasePadre();
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setPiid(request.getParameter(\"piid\"));\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+" = "+tarea.getClase().toLowerCase()+"Dao.consultaCompletar("+clase.getJavaClase().toLowerCase()+");\r\n\r\n");
		}
		
		Clase padre = tarea.getClasePadre();
		
		List<Clase> clases = Validaciones.filtrarClasesTarea(tarea.getAtributosRechazar());
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			if(clase.getJavaClase().equalsIgnoreCase(padre.getJavaClase())==false){
				buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+"(pre"+tarea.getClase()+"Util.cargaRechazar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+", obtenerUsuario(request), request));\r\n");
			} else {
				buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+" = pre"+tarea.getClase()+"Util.cargaRechazar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+",  obtenerUsuario(request), request);\r\n");
			}
		}
		
		buffer.append("\t\tpre"+tarea.getClase()+"Util.completarDO((DataObject)clientObjectWrapper.getObject(), "+padre.getJavaClase().toLowerCase()+");\r\n");
		buffer.append("\t\tUsuario usuario = obtenerUsuario(request);\r\n");
		buffer.append("\t\tTareaRechazo tareaRechazo = new TareaRechazo();\r\n");
		buffer.append("\t\ttareaRechazo.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\ttareaRechazo.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("\t\ttareaRechazo.setNombreTarea(request.getParameter(\"nombreTarea\"));\r\n");
		buffer.append("\t\ttareaRechazo.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\ttareaRechazo.setTipo(request.getParameter(\"rechazo.tipo\"));\r\n");
		buffer.append("\t\ttareaRechazo.setComentario(request.getParameter(\"rechazo.comentario\"));\r\n");
		buffer.append("\t\ttareaRechazo.setUsuario(usuario.getCodigo());\r\n");
		buffer.append("\t\ttareaRechazo.setNombreUsuario(usuario.getNombre());\r\n");
		buffer.append("\t\ttareaRechazo.setFecha(new java.sql.Timestamp(new java.util.Date().getTime()));\r\n");
		
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.rechazarTarea(tareaRechazo, "+padre.getJavaClase().toLowerCase()+");\r\n\r\n");
		
		buffer.append("\t\treturn clientObjectWrapper;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoMetodoObservar(Proyecto proyecto, Tarea tarea, Connection connection){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic ClientObjectWrapper observarPoblarEntrada(HttpServletRequest request, HttpServletResponse response,ClientObjectWrapper clientObjectWrapper) throws Exception{\r\n");
		
		if(tarea.getClasePadre()!=null){
			Clase clase = tarea.getClasePadre(); //falta recuperar la clase principal de la tabla
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".setPiid(request.getParameter(\"piid\"));\r\n");
			buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+" = "+tarea.getClase().toLowerCase()+"Dao.consultaCompletar("+clase.getJavaClase().toLowerCase()+");\r\n\r\n");
		}
		
		Clase padre = tarea.getClasePadre();
		
		List<Clase> clases = Validaciones.filtrarClasesTarea(tarea.getAtributosObservar());
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			if(clase.getJavaClase().equalsIgnoreCase(padre.getJavaClase())==false){
				buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+"(pre"+tarea.getClase()+"Util.cargaObservar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+", obtenerUsuario(request), request));\r\n");
			} else {
				buffer.append("\t\t"+Validaciones.ponerClaseHijaPadre(padre, clase)+" = pre"+tarea.getClase()+"Util.cargaObservar"+clase.getJavaClase()+"("+Validaciones.obtenerClaseHijaPadre(padre, clase)+", obtenerUsuario(request), request);\r\n");
			}
		}
		
		buffer.append("\t\tpre"+tarea.getClase()+"Util.completarDO((DataObject)clientObjectWrapper.getObject(), "+padre.getJavaClase().toLowerCase()+");\r\n");
				
		buffer.append("\t\t"+tarea.getClase().toLowerCase()+"Dao.observarTarea("+tarea.getClasePadre().getJavaClase().toLowerCase()+");\r\n\r\n");
		buffer.append("\t\treturn clientObjectWrapper;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String metodoDaoObtenerDocumentos(Proyecto proyecto, Tarea tarea) {
		String esquema = proyecto.getTablas().get(0).getEsquema();
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic List<Documento> obtenerDocumentos(String codigoTarea) throws Exception{\r\n");
		buffer.append("\t\tList<Documento> listaDocumentos = new ArrayList<Documento>();\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+ esquema +".SP_PORTAL_MAE_DOCUMENTO_CON_LISTA(?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(1, codigoTarea);\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tResultSet rs = cstmt.getResultSet();\r\n");
		
		buffer.append("\t\t\tDocumento documento = null;\r\n");
		buffer.append("\t\t\twhile (rs.next()) {\n");
		buffer.append("\t\t\t\tdocumento = new Documento();\n");
		buffer.append("\t\t\t\tdocumento.setCodigoDocumento(rs.getInt(\"COD_DOCUMENTO\"));\n");
		buffer.append("\t\t\t\tdocumento.setCodigoTipoDocumento(rs.getString(\"COD_TIP_DOCUMENTO\"));\n");
		buffer.append("\t\t\t\tdocumento.setNombreTipoDocumento(rs.getString(\"NOM_DOCUMENTO\"));\n");
		buffer.append("\t\t\t\tdocumento.setFormatos(rs.getString(\"FORMATOS\"));\n");
		buffer.append("\t\t\t\tdocumento.setObligatorio(rs.getBoolean(\"OBLIGATORIO\"));\n");
		buffer.append("\t\t\t\tdocumento.setAdicional(rs.getBoolean(\"FLG_ADICIONAL\"));\n");
		//buffer.append("\t\t\t\tdocumento.setVisibleDefecto(rs.getBoolean(\"FLG_VISIBLE_DEFECTO\"));\n");
		buffer.append("\t\t\t\tlistaDocumentos.add(documento);\n");
		buffer.append("\t\t\t}\n");
		buffer.append("\t\t\trs.close();\n");
		buffer.append("\t\t\tcstmt.close();\n");
		buffer.append("\t\t\t}catch (SQLException s){\n");
		buffer.append("\t\t\t\tthrow new Exception(s);\n");
		buffer.append("\t\t\t}finally{\n");
		buffer.append("\t\t\t\tif(getConnection()!=null)\n");
		buffer.append("\t\t\t\t\tgetConnection().close();\n");
		buffer.append("\t\t\t}\n");
		buffer.append("\t\treturn listaDocumentos;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	
	private String contenidoArchivoClaseUtil(Proyecto proyecto, Tarea tarea, Connection connection) throws Exception{
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+tarea.getPaquete()+".util;\r\n\r\n");
		
		buffer.append("import javax.servlet.http.HttpServletRequest;\r\n");
		buffer.append("import javax.servlet.http.HttpServletResponse;\r\n\r\n");

		buffer.append("import commonj.sdo.DataObject;\r\n\r\n");
		
		buffer.append("import pe.com.financiero.bpm.core.modelo.MensajeValidacion;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.Usuario;\r\n\r\n");
		
		if(tarea.getObjetosBPMCompletar()==null){
			buffer.append("import "+tarea.getObjetosBPMCompletar().get(0).getAtributosTarea().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		}else{
			buffer.append("import "+proyecto.getProcesos().get(0).getAtributosEntrada().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		}
		
		buffer.append("public class Pre"+tarea.getClase()+"Util {\r\n\r\n");
	    
		buffer.append("\tpublic MensajeValidacion completarValidacion(HttpServletRequest request, HttpServletResponse response){\r\n");
		buffer.append("\t\tMensajeValidacion mensajeValidacion = new MensajeValidacion();\r\n");
		for (int x = 0; x < tarea.getAtributosCompletar().size(); x++) {
			AtributoTarea atributo = tarea.getAtributosCompletar().get(x);
			
			if(atributo.isRequiereValidacion() && (atributo.getWebValorOmision()==null || atributo.getWebValorOmision().trim().length()==0)){
				if(atributo.getTipo().equals("String")){
					buffer.append(Validaciones.escribirValidacionString(atributo));
				}else if(atributo.getTipo().equalsIgnoreCase("long")){
					buffer.append(Validaciones.escribirValidacionLong(atributo));
				}else if(atributo.getTipo().equals("Integer") || atributo.getTipo().equals("int")){
					buffer.append(Validaciones.escribirValidacionInteger(atributo));
				}else if(atributo.getTipo().equalsIgnoreCase("java.sql.Date")){
					buffer.append(Validaciones.escribirValidacionDate(atributo));
				}else if(atributo.getTipo().equalsIgnoreCase("java.math.BigDecimal")){
					buffer.append(Validaciones.escribirValidacionBigDecimal(atributo));
				}else if(atributo.getTipo().equalsIgnoreCase("boolean")){
					buffer.append(Validaciones.escribirValidacionBoolean(atributo));
				}
			}else if(atributo.getTipo().equalsIgnoreCase("boolean")){
				buffer.append(Validaciones.escribirValidacionBoolean(atributo));
			}
		}
		buffer.append("\t\tmensajeValidacion.setConforme(true);\r\n");
		buffer.append("\t\treturn mensajeValidacion;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append(Validaciones.escribirCompletarObjetoTarea("cargaCompletar",tarea.getAtributosCompletarSinPK()));
		if(tarea.isFlujoCancelar()){
			buffer.append(Validaciones.escribirCompletarObjetoTarea("cargaCancelar",tarea.getAtributosCancelarSinPK()));
		}
		
		if(tarea.isFlujoRechazar()){
			buffer.append(Validaciones.escribirCompletarObjetoTarea("cargaRechazar",tarea.getAtributosRechazarSinPK()));
		}
		
		if(tarea.isFlujoObservar()){
			buffer.append(Validaciones.escribirCompletarObjetoTarea("cargaObservar",tarea.getAtributosObservarSinPK()));
		}
		
		buffer.append("\tpublic void completarDO(DataObject dataObject, "+tarea.getClasePadre().getJavaClase()+" "+tarea.getClasePadre().getJavaClase().toLowerCase()+") throws Exception{\r\n");
		if(tarea.getObjetosBPMCompletar()!=null 
				&& tarea.getObjetosBPMCompletar().size()>0){
			
			Clase padre = tarea.getClasePadre();
			
			for (int b = 0; b < tarea.getObjetosBPMCompletar().size(); b++) {
				ObjetoBPM objetoBPM = tarea.getObjetosBPMCompletar().get(b);
				buffer.append("\t\tDataObject "+objetoBPM.getNombre()+"DO = dataObject.createDataObject(\""+objetoBPM.getNombre()+"\");\r\n");
				for (int i = 0; i < objetoBPM.getAtributosTarea().size(); i++) {
					AtributoTarea subAtributo = objetoBPM.getAtributosTarea().get(i);
					if(subAtributo.isProcesoAtributoTieneBO()){
						if(subAtributo.getTipo().equalsIgnoreCase("boolean")){
							buffer.append("\t\t"+subAtributo.getProcesoObjeto()+"DO.set"+Validaciones.nombreVariable(subAtributo.getProcesoObjetoTipoDato())+"(\""+subAtributo.getProcesoObjetoAtributo()+"\", "+Validaciones.obtenerClaseHijaPadre(padre, subAtributo.getClase())+".is"+Validaciones.nombreVariable(subAtributo.getNombre())+"());\r\n");
						}else{
							buffer.append("\t\t"+subAtributo.getProcesoObjeto()+"DO.set"+Validaciones.nombreVariable(subAtributo.getProcesoObjetoTipoDato())+"(\""+subAtributo.getProcesoObjetoAtributo()+"\", "+Validaciones.obtenerClaseHijaPadre(padre, subAtributo.getClase())+".get"+Validaciones.nombreVariable(subAtributo.getNombre())+"());\r\n");
						}
					}
				}
			}
		}
		buffer.append("\t}\r\n");
		
		
		buffer.append("}\r\n");
		return buffer.toString();
	}
	
}
