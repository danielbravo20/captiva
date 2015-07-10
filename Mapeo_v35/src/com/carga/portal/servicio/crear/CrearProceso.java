package com.carga.portal.servicio.crear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carga.mapeo.dao.impl.ProcesoDaoImpl;
import com.carga.portal.modelo.Atributo;
import com.carga.portal.modelo.AtributoConsulta;
import com.carga.portal.modelo.AtributoProceso;
import com.carga.portal.modelo.Clase;
import com.carga.portal.modelo.ClaseProceso;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Consulta;
import com.carga.portal.modelo.Proceso;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.Tabla;
import com.carga.portal.modelo.Tarea;
import com.carga.portal.servicio.crear.util.Validaciones;

public class CrearProceso {
	
	public void crear(Proyecto proyecto, Configuracion configuracion, Connection connection) throws Exception{
		
		List<Proceso> procesos = proyecto.getProcesos();
		
		//Archivo de configuración de enlace ejb
		File directorioMETAINF = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoEJBCliente()+"\\ejbModule"+"\\META-INF");
		if(directorioMETAINF.exists()==false){
			directorioMETAINF.mkdirs();
		}
		
		File archivoEjbBnd = new File(directorioMETAINF.getAbsolutePath()+"\\ibm-ejb-jar-bnd.xml");
		if(archivoEjbBnd.exists()){
			archivoEjbBnd.delete();
		}
		archivoEjbBnd.createNewFile();
		BufferedWriter bufferedWriterEjbBnd =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoEjbBnd,true), "ISO-8859-1"));
		bufferedWriterEjbBnd.write(contenidoCabeceraBnd(proyecto, procesos, connection));
		bufferedWriterEjbBnd.close();
		
		//Servlet controllador
		File directorioControllador = new File(configuracion.getDirectorioWEB().getAbsoluteFile()+"\\src\\"+(proyecto.getJavaNombrePaqueteControlador()).replaceAll("\\.", "/"));
		if(directorioControllador.exists()==false){
			directorioControllador.mkdirs();
		}
		
		File archivoProcesoController = new File(directorioControllador.getAbsolutePath()+"\\"+proyecto.getJavaPreSufijoControlador()+"ProcesoController.java");
		if(archivoProcesoController.exists()){
			archivoProcesoController.delete();
		}
		archivoProcesoController.createNewFile();
		BufferedWriter bwProcesoController =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoProcesoController,true), "ISO-8859-1"));
		bwProcesoController.write(contenidoServletProcesoController(procesos, proyecto));
		bwProcesoController.close();
		
		if (procesos != null){
			for(int x=0;x<procesos.size(); x++){
				Proceso proceso = procesos.get(x);
				
				if(proceso!=null && 
						proceso.getAtributosEntrada()!=null && 
						proceso.getAtributosEntrada().size()>0){
					/********************************************************************************/
					/** Inicio paquete Proceso **/
					File directorioPaquetesAutogenerado = new File(configuracion.getDirectorioEJBExt().getAbsoluteFile()+"\\src\\"+(proceso.getPaquete()).replaceAll("\\.", "/"));
					if(directorioPaquetesAutogenerado.exists()==false){
						directorioPaquetesAutogenerado.mkdirs();
					}
					
					File directorioPaquetesAutogeneradoDao = new File(configuracion.getDirectorioEJBCliente()+"\\ejbModule\\"+(proceso.getPaquete()).replaceAll("\\.", "/")+"\\dao");
					if(directorioPaquetesAutogeneradoDao.exists()==false){
						directorioPaquetesAutogeneradoDao.mkdirs();
					}
					
					File directorioPaquetesAutogeneradoDaoImpl = new File(configuracion.getDirectorioEJBExt().getAbsolutePath()+"\\src\\"+(proceso.getPaquete()).replaceAll("\\.", "/")+"\\dao\\impl");
					if(directorioPaquetesAutogeneradoDaoImpl.exists()==false){
						directorioPaquetesAutogeneradoDaoImpl.mkdirs();
					}
					
					File archivoClasePre = new File(directorioPaquetesAutogenerado.getAbsolutePath()+"\\","Pre"+proceso.getClase()+"Impl.java");
					if(archivoClasePre.exists()){
						archivoClasePre.delete();
					}
					
					archivoClasePre.createNewFile();
					BufferedWriter bufferedWriterPre =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClasePre,true), "ISO-8859-1"));
					bufferedWriterPre.write(contenidoArchivoClase(proyecto, proceso, connection));
					bufferedWriterPre.close();
					
					File archivoClasePreDao = new File(directorioPaquetesAutogeneradoDao.getAbsolutePath()+"\\",proceso.getClase()+"Dao.java");
					if(archivoClasePreDao.exists()){
						archivoClasePreDao.delete();
					}
					archivoClasePreDao.createNewFile();
					BufferedWriter bufferedWriterPreDao =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClasePreDao,true), "ISO-8859-1"));
					bufferedWriterPreDao.write(contenidoArchivoClaseDao(proceso, connection));
					bufferedWriterPreDao.close();
					
					/**********************************************************************************************************************************************/
					File archivoClasePreDaoImpl = new File(directorioPaquetesAutogeneradoDaoImpl.getAbsolutePath()+"\\","Pre"+proceso.getClase()+"DaoImpl.java");
					if(archivoClasePreDaoImpl.exists()){
						archivoClasePreDaoImpl.delete();
					}
					archivoClasePreDaoImpl.createNewFile();
					BufferedWriter bufferedWriterPreDaoImpl =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClasePreDaoImpl,true), "ISO-8859-1"));
					bufferedWriterPreDaoImpl.write(contenidoArchivoClaseDaoImpl(proyecto, proceso, connection));
					bufferedWriterPreDaoImpl.close();
					/**********************************************************************************************************************************************/
					
					
					File directorioPaquetes = new File(configuracion.getDirectorioSQL()+"\\"+proyecto.getJavaNombreProyectoEJB()+"\\ejbModule\\"+(proceso.getPaquete()).replaceAll("\\.", "/"));
					if(directorioPaquetes.exists()==false){
						directorioPaquetes.mkdirs();
					}
					
					File directorioPaquetesDaoTempl = new File(configuracion.getDirectorioSQL()+"\\"+proyecto.getJavaNombreProyectoEJB()+"\\ejbModule\\"+(proceso.getPaquete()).replaceAll("\\.", "/")+"\\dao\\impl\\");
					if(directorioPaquetesDaoTempl.exists()==false){
						directorioPaquetesDaoTempl.mkdirs();
					}
					
					/**********************************************************************************************************************************************/
					File archivoClasePreDaoImplTempl = new File(directorioPaquetesDaoTempl.getAbsolutePath()+"\\",proceso.getClase()+"DaoImpl.java");
					if(archivoClasePreDaoImplTempl.exists()){
						archivoClasePreDaoImplTempl.delete();
					}
					archivoClasePreDaoImplTempl.createNewFile();
					BufferedWriter bufferedWriterPreDaoImplTempl =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClasePreDaoImplTempl,true), "ISO-8859-1"));
					bufferedWriterPreDaoImplTempl.write(contenidoArchivoClaseDaoImplTempl(proceso));
					bufferedWriterPreDaoImplTempl.close();
					/**********************************************************************************************************************************************/
					
					
					File archivoClase = new File(directorioPaquetes.getAbsolutePath()+"\\", proceso.getClase()+"Impl.java");
					if(archivoClase.exists()){
						archivoClase.delete();
					}
					
					archivoClase.createNewFile();
					BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoClase,true), "ISO-8859-1"));
					bufferedWriter.write(contenidoArchivoPlantilla(proceso));
					bufferedWriter.close();	
					
					File directorioPaquetesInterfase = new File(configuracion.getDirectorioSQL()+"\\"+proyecto.getJavaNombreProyectoEJBCliente()+"\\ejbModule\\"+(proceso.getPaquete()).replaceAll("\\.", "/"));
					if(directorioPaquetesInterfase.exists()==false){
						directorioPaquetesInterfase.mkdirs();
					}
					
					File archivoInterfase = new File(directorioPaquetesInterfase.getAbsolutePath()+"\\", proceso.getClase()+".java");
					if(archivoInterfase.exists()){
						archivoInterfase.delete();
					}
					
					archivoClase.createNewFile();
					BufferedWriter bufferedInterfaseWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoInterfase,true), "ISO-8859-1"));
					bufferedInterfaseWriter.write(contenidoArchivoPlantillaInterfase(proceso));
					bufferedInterfaseWriter.close();
				}
			}
		}
	}
	
	private String contenidoArchivoClase(Proyecto proyecto, Proceso proceso, Connection connection) throws Exception{
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+proceso.getPaquete()+";\r\n\r\n");
		
		buffer.append("import javax.ejb.EJB;\r\n");
		buffer.append("import javax.servlet.http.HttpServletRequest;\r\n");
		buffer.append("import javax.servlet.http.HttpServletResponse;\r\n\r\n");
		
		buffer.append("import com.ibm.bpe.api.ClientObjectWrapper;\r\n");
		buffer.append("import commonj.sdo.DataObject;\r\n\r\n");
		
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoObservacion;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.Usuario;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.ejb.ProcesoBase;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.ejb.ProcesoBaseImpl;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.MensajeValidacion;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.util.ArchivoUtil;\r\n");
		
		buffer.append("import java.sql.Timestamp;\r\n");
		buffer.append("import java.util.Map;\r\n");
		buffer.append("import java.util.HashMap;\r\n");
		
		buffer.append("import "+proceso.getPaquete()+".dao."+proceso.getClase()+"Dao;\r\n\r\n");

		buffer.append("import "+proceso.getAtributosEntrada().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		
		int y = 0;
		StringBuffer atributosDao = new StringBuffer();
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {
			
			String paquete = proceso.getAtributosEntrada().get(x).getClase().getJavaPaquete();
			String clase = proceso.getAtributosEntrada().get(x).getClase().getJavaClase();

			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, proceso.getAtributosEntrada().get(x));
				if(y!=0){
					atributosDao.append(",");
				}
				atributosDao.append(clase.toLowerCase());
			}
			y++;
		}
		
		buffer.append("public abstract class Pre"+proceso.getClase()+"Impl extends ProcesoBaseImpl implements ProcesoBase{\r\n\r\n");
		
		String nombreEJBDaoCliente = proceso.getClase().toLowerCase()+"Dao";
		buffer.append("\t@EJB\r\n");
		buffer.append("\t"+proceso.getClase()+"Dao "+nombreEJBDaoCliente+";\r\n\r\n");
		
		buffer.append("\tpublic String getIniciarProcesoNombreInstancia(HttpServletRequest request) throws Exception{\r\n");
		
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {
			if(proceso.getAtributosEntrada().get(x).isSqlFlgAutogenerado()){
				buffer.append("\t\trequest.getParameterMap().put(\""+proceso.getAtributosEntrada().get(x).getWebNombre()+"\", new String[]{String.valueOf("+nombreEJBDaoCliente+".getSecuencial"+proceso.getAtributosEntrada().get(x).getNombre()+"())});\r\n");	
			}
		}
		
		StringBuffer atributosExtra = new StringBuffer();
		for(int c=0; c<proceso.getAtributosEntrada().size(); c++){
			if(proceso.getAtributosEntrada().get(c).isBpmFlgPIID()){
				atributosExtra.append("+\"_\"+request.getParameter(\""+proceso.getAtributosEntrada().get(c).getWebNombre()+"\")");
			}
		}		
		buffer.append("\t\treturn \""+proceso.getSufijoBanca()+"_"+proceso.getSufijoProducto()+"_"+proceso.getSufijoProceso()+"\""+atributosExtra.toString()+";\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic String getIniciarProcesoNombrePlantilla() {\r\n");
		buffer.append("\t\treturn \""+proceso.getNombrePlantilla()+"\";\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic int getIniciarProcesoActividadInicio() {\r\n");
		buffer.append("\t\treturn "+proceso.getActividadInicio()+";\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic MensajeValidacion iniciarProcesoValidacion(HttpServletRequest request, HttpServletResponse response){\r\n");
		buffer.append("\t\tMensajeValidacion mensajeValidacion = new MensajeValidacion();\r\n");
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {
			AtributoProceso atributo = proceso.getAtributosEntrada().get(x);
			if(atributo.isWebFlgValidacion() && atributo.isSqlFlgAutogenerado()==false && atributo.isJavaTieneValorOmision()==false){
				if(atributo.getTipo().equals("String")){
					buffer.append(Validaciones.escribirValidacionString(atributo));
				}else if(atributo.getTipo().equalsIgnoreCase("boolean")){
					buffer.append(Validaciones.escribirValidacionBoolean(atributo));					
				}else if(atributo.getTipo().equalsIgnoreCase("long")){
					buffer.append(Validaciones.escribirValidacionLong(atributo));
				}else if(atributo.getTipo().equals("Integer") || atributo.getTipo().equals("int")){
					buffer.append(Validaciones.escribirValidacionInteger(atributo));
				}else if(atributo.getTipo().equalsIgnoreCase("java.sql.Date")){
					buffer.append(Validaciones.escribirValidacionDate(atributo));
				}else if(atributo.getTipo().equalsIgnoreCase("java.math.BigDecimal")){
					buffer.append(Validaciones.escribirValidacionBigDecimal(atributo));
				}
			}
		}
		buffer.append("\t\tmensajeValidacion.setConforme(true);\r\n");
		buffer.append("\t\treturn mensajeValidacion;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		buffer.append(Validaciones.escribirCargaObjeto(proceso.getAtributosEntrada()));
		
		
		buffer.append("\tpublic ClientObjectWrapper getIniciarProcesoObjetoEntrada(ClientObjectWrapper clientObjectWrapper, HttpServletRequest request) throws Exception{\r\n");
		
		List<ClaseProceso> clases = Validaciones.filtrarClases(proceso.getAtributosEntrada());
		StringBuffer clasesInstanciadas = new StringBuffer();
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = cargar"+clase.getJavaClase()+"(request);\r\n");
			if(i==0){
				clasesInstanciadas.append(clase.getJavaClase().toLowerCase());
			}else{
				clasesInstanciadas.append(", "+clase.getJavaClase().toLowerCase());
			}
		}
		
		buffer.append("\t\tDataObject dataObject = (DataObject)clientObjectWrapper.getObject();\r\n");
		ProcesoDaoImpl procesoDaoImpl = new ProcesoDaoImpl();
		List<AtributoProceso> objetosDOEntreda = proceso.getObjetosDOEntreda();
		List<AtributoProceso> objetosDOsinEntreda = proceso.getObjetosDOsinEntreda();
		
		for (int t = 0; t < objetosDOEntreda.size(); t++) {
			List<AtributoProceso> listaDOEntreda = procesoDaoImpl.obtenerAtributosObjetoBPM(objetosDOEntreda.get(t).getBpmObjeto(), connection);
			buffer.append("\t\tDataObject "+objetosDOEntreda.get(t).getBpmObjeto()+"DO = dataObject.createDataObject(\""+objetosDOEntreda.get(t).getBpmObjeto()+"\");\r\n");
			for (int x = 0; x < listaDOEntreda.size(); x++) {
				AtributoProceso subAtributo = listaDOEntreda.get(x);
				buffer.append("\t\t"+objetosDOEntreda.get(t).getBpmObjeto()+"DO.set"+Validaciones.nombreVariable(subAtributo.getBpmTipo())+"(\""+subAtributo.getBpmNombre()+"\", "+subAtributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(subAtributo.getNombre())+"());\r\n");
			}
		}
		
		for (int t = 0; t < objetosDOsinEntreda.size(); t++) {
			List<AtributoProceso> listaDOEntreda = procesoDaoImpl.obtenerAtributosObjetoBPM(objetosDOsinEntreda.get(t).getBpmObjeto(), connection);
			buffer.append("\t\tDataObject "+listaDOEntreda.get(t).getBpmObjeto()+"DO = dataObject.createDataObject(\""+listaDOEntreda.get(t).getBpmObjeto()+"\");\r\n");
			for (int x = 0; x < listaDOEntreda.size(); x++) {
				AtributoProceso subAtributo = listaDOEntreda.get(x);
				buffer.append("\t\t"+subAtributo.getBpmObjeto()+"DO.set"+Validaciones.nombreVariable(subAtributo.getBpmTipo())+"(\""+subAtributo.getBpmNombre()+"\", "+subAtributo.getClase().getJavaClase().toLowerCase()+".get"+Validaciones.nombreVariable(subAtributo.getNombre())+"());\r\n");
			}
		}
		
		buffer.append("\t\treturn clientObjectWrapper;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic Object verResumen(String piid, HttpServletRequest request) throws Exception{\r\n");
		
		Consulta consultaResumen = proceso.getConsultaResumen(); 
		
		StringBuffer implementacionTablas = new StringBuffer();
		AtributoConsulta atributoConsultaPadreResumen = consultaResumen.getTablaPadre().getAtributosConsulta().get(0);
		String clasePadreResumen = atributoConsultaPadreResumen.getClase().getJavaClase();
		buffer.append("\t\t"+clasePadreResumen+" "+clasePadreResumen.toLowerCase()+" = new "+clasePadreResumen+"();\r\n");
		implementacionTablas.append(clasePadreResumen);
		
		for(int x=0;x<consultaResumen.getTablaPadre().getAtributosConsulta().size();x++){
			AtributoConsulta atributo = consultaResumen.getTablaPadre().getAtributosConsulta().get(x);
			if(atributo.isFlgCondicion()){
				buffer.append("\t\t"+implementacionTablas.toString().toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(request.getParameter(\"piid\"));\r\n");
			}
		}
		
		buffer.append("\t\treturn "+nombreEJBDaoCliente+".verResumen("+implementacionTablas.toString().toLowerCase()+");\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic Object verDetalle(String piid, HttpServletRequest request) throws Exception{\r\n");
		
		consultaResumen = proceso.getConsultaResumen(); 
		
		StringBuffer implementacionTablasDetalle = new StringBuffer();
		AtributoConsulta atributoConsultaPadreDetalle = consultaResumen.getTablaPadre().getAtributosConsulta().get(0);
		String clasePadreDetalle = atributoConsultaPadreDetalle.getClase().getJavaClase();
		buffer.append("\t\t"+clasePadreDetalle+" "+clasePadreDetalle.toLowerCase()+" = new "+clasePadreDetalle+"();\r\n");
		implementacionTablasDetalle.append(clasePadreDetalle);
		
		for(int x=0;x<consultaResumen.getTablaPadre().getAtributosConsulta().size();x++){
			AtributoConsulta atributo = consultaResumen.getTablaPadre().getAtributosConsulta().get(x);
			if(atributo.isFlgCondicion()){
				buffer.append("\t\t"+implementacionTablasDetalle.toString().toLowerCase()+".set"+Validaciones.nombreVariable(atributo.getNombre())+"(piid);\r\n");
			}
		}
		
		buffer.append("\t\t" + clasePadreDetalle.toLowerCase() + " = "+nombreEJBDaoCliente+".verDetalle("+implementacionTablasDetalle.toString().toLowerCase()+");\r\n");
		buffer.append("\t\t" + clasePadreDetalle.toLowerCase() + ".setHistorialDocumento("+nombreEJBDaoCliente+".obtenerHistorialDocumento(piid));\r\n");
		buffer.append("\t\t" + clasePadreDetalle.toLowerCase() + ".setTareaHistoriales("+nombreEJBDaoCliente+".obtenerHistorial(piid));\r\n");
		//buffer.append("\t\t" + clasePadreDetalle.toLowerCase() + ".setHistoricoObservaciones("+nombreEJBDaoCliente+".consultarObservaciones(piid));\r\n");
		
		buffer.append("\t\treturn "+clasePadreDetalle.toLowerCase()+";\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic void iniciarProcesoGuardar(HttpServletRequest request, HttpServletResponse response) throws Exception{\r\n");

		clases = Validaciones.filtrarClases(proceso.getAtributosEntrada());
		clasesInstanciadas = new StringBuffer();
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = cargar"+clase.getJavaClase()+"(request);\r\n");
			if(i==0){
				clasesInstanciadas.append(clase.getJavaClase().toLowerCase());
			}else{
				clasesInstanciadas.append(", "+clase.getJavaClase().toLowerCase());
			}
		}
		buffer.append("\t\t"+nombreEJBDaoCliente+".registarSolicitud("+clasesInstanciadas.toString()+");\r\n\r\n");
		buffer.append("\t}\r\n\r\n");
		
		
		buffer.append("\tpublic Object iniciarProcesoPoblarSalida(HttpServletRequest request, HttpServletResponse response) throws Exception {\r\n");
		buffer.append("\t\tMap<String,String> resultado = new HashMap<String,String>();\r\n");
		buffer.append("\t\tresultado.put(\"piid\", request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\treturn new MensajeValidacion(true, resultado);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic Object obtenerCatalogo(String catalogos) throws Exception{\r\n");
		buffer.append("\t\tif(catalogos!=null && catalogos.length()>0){\r\n");
		buffer.append("\t\t\treturn "+nombreEJBDaoCliente+".obtenerCatalogos(catalogos);\r\n");
		buffer.append("\t\t}else{\r\n");
		buffer.append("\t\t\treturn \"\";\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic Object historial(String piid, HttpServletRequest request) throws Exception{\r\n");
		buffer.append("\t\treturn "+nombreEJBDaoCliente+".obtenerHistorial(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t}\r\n\r\n");

		buffer.append("	// OBSERVACIONES\r\n\r\n");
		
		buffer.append("\tpublic boolean eliminarObservaciones(String piid,String codigoIteracion,String codigoObservacion) throws Exception{\r\n");
		buffer.append("\t\treturn "+nombreEJBDaoCliente+".eliminarObservaciones(piid,codigoIteracion,codigoObservacion);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic Object verTipoObservaciones(String codigoTarea) throws Exception {\r\n");
		buffer.append("\t\treturn "+nombreEJBDaoCliente+".verTipoObservaciones(codigoTarea);\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("	public boolean registrarObservaciones(HttpServletRequest request) throws Exception{\r\n\r\n");
		
		buffer.append("		HistoricoObservacion historicoObservacion = new HistoricoObservacion();\r\n");
		buffer.append("		historicoObservacion.setCodigoObservacion(request.getParameter(\"codigoObservacion\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoIteracion(request.getParameter(\"codigoIteracion\"));\r\n");
		buffer.append("		historicoObservacion.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoTipoObservacion(Integer.parseInt(request.getParameter(\"codigoTipoObservacion\")));\r\n");
		buffer.append("		historicoObservacion.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("		historicoObservacion.setNombreTarea(request.getParameter(\"nombreTarea\"));\r\n");
		buffer.append("		historicoObservacion.setComentarioObservacion(request.getParameter(\"comentarioObservacion\"));\r\n");
		buffer.append("		historicoObservacion.setUsuarioObservacion(obtenerUsuario(request).getCodigo());\r\n");
		buffer.append("		historicoObservacion.setNombreUsuarioObservacion(obtenerUsuario(request).getNombre());\r\n");
		buffer.append("		historicoObservacion.setFechaObservacion(new java.sql.Timestamp(new java.util.Date().getTime()));\r\n");
		buffer.append("		historicoObservacion.setEstado(request.getParameter(\"estado\"));\r\n\r\n");
		
		buffer.append("		return "+nombreEJBDaoCliente+".registrarObservaciones(historicoObservacion);\r\n");
		buffer.append("	}\r\n\r\n");
		
		buffer.append("	// SUBSANACIONES\r\n\r\n");
		
		buffer.append("	public boolean registrarSubsanaciones(HttpServletRequest request) throws Exception {\r\n\r\n");
		
		buffer.append("		HistoricoObservacion historicoObservacion = new HistoricoObservacion();\r\n");
		buffer.append("		historicoObservacion.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoIteracion(request.getParameter(\"codigoIteracion\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoObservacion(request.getParameter(\"codigoObservacion\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("		String tipoAccion = request.getParameter(\"tipoAccion\");\r\n");
		buffer.append("		if(tipoAccion != null && tipoAccion.equals(\"borrarSubsanacion\")){\r\n");
		buffer.append("		} else {\r\n");
		buffer.append("			historicoObservacion.setCodigoTipoExcepcion(Integer.parseInt(request.getParameter(\"codigoTipoExcepcion\")));\r\n");
		buffer.append("			historicoObservacion.setUsuarioExcepcion(obtenerUsuario(request).getCodigo());\r\n");
		buffer.append("			historicoObservacion.setNombreUsuarioExcepcion(obtenerUsuario(request).getNombre());\r\n");
		buffer.append("			historicoObservacion.setFechaExcepcion(new java.sql.Timestamp(new java.util.Date().getTime()));\r\n");
		buffer.append("			historicoObservacion.setComentarioExcepcion(request.getParameter(\"comentarioExcepcion\"));\r\n");
		buffer.append("			historicoObservacion.setCodigoTipoDocumento(Integer.parseInt(request.getParameter(\"codigoTipoDocumento\")));\r\n");
		buffer.append("		}\r\n");
		buffer.append("		historicoObservacion.setEstado(request.getParameter(\"estado\"));\r\n");
		buffer.append("		return "+nombreEJBDaoCliente+".registrarSubsanaciones(historicoObservacion);\r\n\r\n");
		
		buffer.append("	}\r\n\r\n");
	
		buffer.append("	public boolean confirmarSubsanacion(HttpServletRequest request) throws Exception {\r\n\r\n");
		
		buffer.append("		HistoricoObservacion historicoObservacion = new HistoricoObservacion();\r\n");
		buffer.append("		historicoObservacion.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoIteracion(request.getParameter(\"codigoIteracion\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoObservacion(request.getParameter(\"codigoObservacion\"));\r\n");
		buffer.append("		historicoObservacion.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("		historicoObservacion.setEsConfirmado(Boolean.parseBoolean(request.getParameter(\"esConfirmado\")));\r\n");
		buffer.append("		historicoObservacion.setEstado(request.getParameter(\"estado\"));\r\n");
		buffer.append("		return "+nombreEJBDaoCliente+".confirmarSubsanacion(historicoObservacion);\r\n\r\n");
		
		buffer.append("	}\r\n");
		
		buffer.append(registrarHistorialDocumento(proceso));
		
		buffer.append(eliminarHistorialDocumento(proceso));
		
		buffer.append(cargarHistoricoDocumento(proceso));
		
		buffer.append("}\r\n\r\n");
		
		
		return buffer.toString();
	}
	
	private String contenidoArchivoPlantilla(Proceso proceso){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("package "+proceso.getPaquete()+";\r\n\r\n");

		buffer.append("import javax.ejb.Stateless;\r\n");
		buffer.append("import javax.servlet.http.HttpServletRequest;\r\n\r\n");

		buffer.append("import pe.com.financiero.bpm.core.modelo.GrillaLista;\r\n\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.MensajeValidacion;\r\n\r\n");
		
		
		buffer.append("@Stateless\r\n");
		buffer.append("public class "+proceso.getClase()+"Impl extends Pre"+proceso.getClase()+"Impl implements "+proceso.getClase()+"{\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic HttpServletRequest iniciarProcesoPreValidacion(HttpServletRequest request) throws Exception {\r\n");
		buffer.append("\t\treturn request;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic GrillaLista buscarSolicitudes(String tipoBusqueda,String tipoSolicitud,String filtro,String fechaDesde,String fechaHasta,String paginacionNro,String ordenFecha,int cargarTotal) throws Exception{\r\n");
		buffer.append("\t\treturn new GrillaLista();\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic MensajeValidacion transferirSolicitud(String piid, String nuevoUsuario, HttpServletRequest request) {\r\n");
		buffer.append("\t\treturn new MensajeValidacion(true, \"Falta implementar el metodo de transferencia...\");\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic String obtenerNombreRutaDocumento(HttpServletRequest request) throws Exception {\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n");
		
		buffer.append("\t@Override\r\n");
		buffer.append("\tpublic HistoricoDocumento cargarHistoricoDocumento(HistoricoDocumento historicoDocumento, java.util.Map<String, String> mapRequest) {\r\n");
		buffer.append("\t\thistoricoDocumento.setRutaDocumento(\"D://temp\");\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreDocumento(\"BE_CF_EMCF_\"+historicoDocumento.getCodigoDocumento());\r\n");
		buffer.append("\t\treturn historicoDocumento;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("}\r\n");
		return buffer.toString();
	}

	private String contenidoArchivoPlantillaInterfase(Proceso proceso){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+proceso.getPaquete()+";\r\n\r\n");
		
		buffer.append("import javax.ejb.Local;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.ejb.ProcesoBase;\r\n");
		
		buffer.append("@Local\r\n");
		buffer.append("public interface "+proceso.getClase()+" extends ProcesoBase{\r\n\r\n");
		
		buffer.append("}\r\n");
		return buffer.toString();
	}
	
	private String contenidoArchivoClaseDao(Proceso proceso, Connection conn) throws Exception{
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+proceso.getPaquete()+".dao;\r\n\r\n");

		buffer.append("import java.util.List;\r\n");
		buffer.append("import java.util.Map;\r\n\r\n");

		buffer.append("import javax.ejb.Local;\r\n\r\n");

		buffer.append("import pe.com.financiero.bpm.core.modelo.TareaHistorial;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoObservacion;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.GrillaLista;\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		int i = 0;
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {

			String paquete = proceso.getAtributosEntrada().get(x).getClase().getJavaPaquete();
			String clase = proceso.getAtributosEntrada().get(x).getClase().getJavaClase();
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, proceso.getAtributosEntrada().get(x));
				buffer.append("import "+paquete+"."+clase+";\r\n");
				if(i!=0){
					parametros.append(", ");
				}
				parametros.append(clase+" "+clase.toLowerCase());
				i++;
			}
		}
		
		buffer.append("\r\n");
		buffer.append("@Local\r\n");
		buffer.append("public interface "+proceso.getClase()+"Dao {\r\n\r\n");
		
		CrearDao crearDao = new CrearDao();
		buffer.append("\t"+crearDao.obtenerInterfaseDao(proceso.getConsultaResumen(), "verResumen",conn)+"\r\n");
		buffer.append("\t"+crearDao.obtenerInterfaseDao(proceso.getConsultaDetalle(), "verDetalle",conn)+"\r\n");
		buffer.append("\tMap<String, Map> obtenerCatalogos(String catalogos) throws Exception;\r\n");
		buffer.append("\tvoid registarSolicitud("+parametros.toString()+") throws Exception;\r\n");
		buffer.append("\tList<TareaHistorial> obtenerHistorial(String piid) throws Exception;\r\n");
		buffer.append("\tGrillaLista buscarSolicitud(int skip, int cantidad,String tipoSolicitud, String filtro,String fechaDesde, String fechaHasta, String ordenFecha,int cargarTotal) throws Exception;\r\n");
		buffer.append("\tList<HistoricoDocumento> obtenerHistorialDocumento(String piid) throws Exception;\r\n");
		
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {
			if(proceso.getAtributosEntrada().get(x).isSqlFlgAutogenerado()){
				buffer.append("\t"+proceso.getAtributosEntrada().get(x).getTipo()+" getSecuencial"+proceso.getAtributosEntrada().get(x).getNombre()+"() throws Exception;\r\n");
			}
		}
		
		buffer.append("\tLong obtenerSecuencialDocumento() throws Exception;\r\n");
		buffer.append("\tvoid registrarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception;\r\n");
		buffer.append("\tvoid actualizarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception;\r\n");
		buffer.append("\tvoid eliminarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception;\r\n");
		
		buffer.append("		// OBSERVACIONES\r\n");
		buffer.append("		boolean eliminarObservaciones(String piid, String codigoIteracion, String codigoObservacion) throws Exception;\r\n");
		buffer.append("		Map<String, String> verTipoObservaciones(String parameter) throws Exception;\r\n");
		buffer.append("		boolean registrarObservaciones(HistoricoObservacion historicoObservacion) throws Exception;\r\n");
		
		buffer.append("		// SUBSANACIONES\r\n");
		buffer.append("		boolean registrarSubsanaciones(HistoricoObservacion historicoObservacion) throws Exception;\r\n");
		buffer.append("		boolean confirmarSubsanacion(HistoricoObservacion historicoObservacion) throws Exception;\r\n");
		
		buffer.append("}");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoClaseDaoImplTempl(Proceso proceso) throws Exception{
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+proceso.getPaquete()+".dao.impl;\r\n\r\n");
		buffer.append("import java.sql.Connection;\r\n\r\n");

		buffer.append("import javax.annotation.Resource;\r\n");
		buffer.append("import javax.ejb.Stateless;\r\n");
		buffer.append("import javax.sql.DataSource;\r\n\r\n");

		buffer.append("import "+ proceso.getPaquete() +".dao."+ proceso.getClase() +"Dao;\r\n\r\n");

		buffer.append("@Stateless\r\n");
		buffer.append("public class "+ proceso.getClase() +"DaoImpl extends Pre"+ proceso.getClase() +"DaoImpl implements "+ proceso.getClase() +"Dao {\r\n\r\n");

		buffer.append("\t@Resource(name = \"" + proceso.getDatasource() + "\")\r\n");
		buffer.append("\tDataSource dataSource;\r\n\r\n");

		buffer.append("\tpublic Connection getConnection() throws Exception{\r\n");
		buffer.append("\t\treturn dataSource.getConnection();\r\n");
		buffer.append("\t}\r\n\r\n");
			
		buffer.append("}\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoArchivoClaseDaoImpl(Proyecto proyecto, Proceso proceso, Connection conn) throws Exception{
		CrearTarea crearTarea = new CrearTarea();
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("package "+proceso.getPaquete()+".dao.impl;\r\n\r\n");
		
		buffer.append("import java.sql.CallableStatement;\r\n");
		buffer.append("import java.sql.Connection;\r\n");
		buffer.append("import java.sql.ResultSet;\r\n");
		buffer.append("import java.sql.Types;\r\n");
		buffer.append("import java.sql.SQLException;\r\n");
		buffer.append("import java.util.ArrayList;\r\n");
		buffer.append("import java.util.LinkedHashMap;\r\n");
		buffer.append("import java.util.List;\r\n");
		buffer.append("import java.util.Map;\r\n\r\n");
		
		buffer.append("import pe.com.financiero.bpm.core.modelo.TareaHistorial;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoDocumento;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.Catalogo;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.HistoricoObservacion;\r\n");
		buffer.append("import pe.com.financiero.bpm.core.modelo.GrillaLista;\r\n\r\n");
		
		buffer.append("import "+proceso.getAtributosEntrada().get(0).getClase().getJavaPaquete()+".*;\r\n\r\n");
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		
		int y = 0;
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {
			String paquete = proceso.getAtributosEntrada().get(x).getClase().getJavaPaquete();
			String clase = proceso.getAtributosEntrada().get(x).getClase().getJavaClase();
			
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, proceso.getAtributosEntrada().get(x));
				if(y==0){
					parametros.append(clase+" "+clase.toLowerCase());
				}else{
					parametros.append(", "+clase+" "+clase.toLowerCase()+"  ");
				}
				
				y++;
			}
		}		
		buffer.append("import "+proceso.getPaquete()+".dao."+proceso.getClase()+"Dao;\r\n\r\n");
		
		buffer.append("public abstract class Pre"+ proceso.getClase() +"DaoImpl implements "+ proceso.getClase()+"Dao" + " {\r\n\r\n");
		
		buffer.append("\tpublic abstract Connection getConnection() throws Exception;\r\n\r\n");
		
		CrearDao crearDao = new CrearDao();
		buffer.append(crearDao.obtenerClaseDaoImpl(proceso.getConsultaResumen(), "verResumen", conn));
		
		buffer.append(crearDao.obtenerClaseDaoImpl(proceso.getConsultaDetalle(), "verDetalle", conn));
		
		buffer.append(contenidoDaoImplObtenerCatalogos(proyecto, proceso, conn));
		
		buffer.append(contenidoDaoImplRegistrarSolicitud(proceso, conn));

		buffer.append(contenidoDaoImplSecuencial(proyecto, proceso, conn));
		
		buffer.append(contenidoDaoImplObtenerHistorial(proyecto, proceso, conn));
		
		buffer.append(contenidoBuscarSolicitud(proceso));
				
		buffer.append(crearTarea.contenidoDaoImplObtenerHistorialDocumento(proyecto));
		
		buffer.append(contenidoDaoImplRegistrarHistorialDocumento(proyecto));
		
		buffer.append(contenidoDaoImplSecuencialDocumento(proyecto, proceso));
		
		// OBSERVACIONES
		
		buffer.append(eliminarObservaciones(proyecto));
		
		buffer.append(verTipoObservaciones(proyecto));
		
		buffer.append(registrarObservaciones(proyecto));
		
		// SUBSANACIONES	
		
		buffer.append(registrarSubsanaciones(proyecto));
		
		buffer.append(confirmarSubsanacion(proyecto));
		
		buffer.append(contenidoDaoImplEliminarHistorialDocumento(proyecto));
		
		buffer.append("} \r\n\r\n");
		
		return buffer.toString();	
	}

	private String eliminarObservaciones(Proyecto proyecto) {
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("	// OBSERVACIONES\r\n\r\n");
		buffer.append("\tpublic boolean eliminarObservaciones(String piid,String codigoIteracion,String codigoObservacion) throws Exception {\r\n");
		buffer.append("\t\tboolean resultado = false;\r\n");
		buffer.append("\t\tConnection conn = null;\r\n");
		buffer.append("\t\tCallableStatement cstmt = null;\r\n");
		buffer.append("\t\tResultSet rs = null;\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tconn = getConnection();\r\n");
		buffer.append("\t\t\tcstmt = conn.prepareCall(\"{call " +tabla.getEsquema() + ".SP_PORTAL_HIS_OBSERVACION_ELIMINAR(?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(1, piid);\r\n");
		buffer.append("\t\t\tcstmt.setString(2, codigoIteracion);\r\n");
		buffer.append("\t\t\tcstmt.setString(3, codigoObservacion);\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tresultado = true;\r\n");
		buffer.append("\t\t} catch(SQLException e){\r\n");
		buffer.append("\t\t\tthrow new Exception(e);\r\n");
		buffer.append("\t\t} finally {\r\n");
		buffer.append("\t\t\tif (rs != null){	rs.close();             }\r\n");
		buffer.append("\t\t\tif (cstmt != null){	cstmt.close();          }\r\n");
		buffer.append("\t\t\tif (conn != null){	conn.close();           }\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn resultado;\r\n");
		buffer.append("\t}\r\n\r\n");

		return buffer.toString();
	}

	private String verTipoObservaciones(Proyecto proyecto) {
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic Map<String, String> verTipoObservaciones(String codigoTarea) throws Exception{\r\n");
		buffer.append("\t\tMap<String, String> mapTabla = new LinkedHashMap<String, String>();\r\n");
		buffer.append("\t\tConnection conn = null;\r\n");
		buffer.append("\t\tCallableStatement cstmt = null;\r\n");
		buffer.append("\t\tResultSet rs = null;\r\n");
		buffer.append("\t\t\ttry{\r\n");
		buffer.append("\t\t\t\tconn = getConnection();\r\n");
		buffer.append("\t\t\t\tcstmt = conn.prepareCall(\"{call "+tabla.getEsquema() +".SP_PORTAL_MAE_OBSERVACION_CON_LISTA(?)}\");\r\n");
		buffer.append("\t\t\t\tcstmt.setString(1, codigoTarea);\r\n");
		buffer.append("\t\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\t\trs = cstmt.getResultSet();\r\n");
		buffer.append("\t\t\t\twhile (rs.next()) {\r\n");
		buffer.append("\t\t\t\tmapTabla.put(rs.getString(\"COD_TIP_OBSERVACION\"), rs.getString(\"NOM_OBSERVACION\"));\r\n");
		buffer.append("\t\t\t}\r\n");
		buffer.append("\t\t} catch(SQLException e){\r\n");
		buffer.append("\t\t\tthrow new Exception(e);\r\n");
		buffer.append("\t\t} finally {\r\n");
		buffer.append("\t\t\tif (rs != null){	rs.close();             }\r\n");
		buffer.append("\t\t\tif (cstmt != null){	cstmt.close();          }\r\n");
		buffer.append("\t\t\tif (conn != null){	conn.close();           }\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn mapTabla;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String registrarObservaciones(Proyecto proyecto) {
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic boolean registrarObservaciones(HistoricoObservacion historicoObservacion) throws Exception {\r\n");
		buffer.append("\t\tboolean resultado = false;\r\n");
		buffer.append("\t\tConnection conn = null;\r\n");
		buffer.append("\t\tCallableStatement cstmt = null;\r\n");
		buffer.append("\t\tResultSet rs = null;\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tconn = getConnection();\r\n");
		buffer.append("\t\t\tcstmt = conn.prepareCall(\"{call " +tabla.getEsquema() + ".SP_PORTAL_HIS_OBSERVACION_REGISTRO(?,?,?,?,?,?,?,?,?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setString(1, historicoObservacion.getCodigoIteracion());\r\n");
		buffer.append("\t\t\tcstmt.setString(2, historicoObservacion.getCodigoObservacion());\r\n");
		buffer.append("\t\t\tcstmt.setString(3, historicoObservacion.getPiid());\r\n");
		buffer.append("\t\t\tcstmt.setInt(4, historicoObservacion.getCodigoTipoObservacion());\r\n");
		buffer.append("\t\t\tcstmt.setString(5, historicoObservacion.getCodigoTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(6, historicoObservacion.getNombreTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(7, historicoObservacion.getComentarioObservacion());\r\n");
		buffer.append("\t\t\tcstmt.setString(8, historicoObservacion.getUsuarioObservacion());\r\n");
		buffer.append("\t\t\tcstmt.setString(9, historicoObservacion.getNombreUsuarioObservacion());\r\n");
		buffer.append("\t\t\tcstmt.setTimestamp(10, historicoObservacion.getFechaObservacion());\r\n");
		buffer.append("\t\t\tcstmt.setString(11, historicoObservacion.getEstado());\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\tresultado = true;\r\n");
		buffer.append("\t\t} catch(SQLException e){\r\n");
		buffer.append("\t\tthrow new Exception(e);\r\n");
		buffer.append("\t\t} finally {\r\n");
		buffer.append("\t\t\tif (rs != null){	rs.close();             }\r\n");
		buffer.append("\t\t\tif (cstmt != null){	cstmt.close();          }\r\n");
		buffer.append("\t\t\tif (conn != null){	conn.close();           }\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn resultado;\r\n");
        buffer.append("\t}\r\n");

        return buffer.toString();
	}
	
	private String registrarSubsanaciones(Proyecto proyecto) {
		
		Tabla tabla = proyecto.getTablas().get(0);
		
		StringBuilder buffer = new StringBuilder();

		buffer.append("	// SUBSANACIONES\r\n\r\n");
		
		buffer.append("	public boolean registrarSubsanaciones(HistoricoObservacion historicoObservacion) throws Exception {\r\n");
		buffer.append("		boolean resultado = false;\r\n");
		buffer.append("		Connection conn = null;\r\n");
		buffer.append("		CallableStatement cstmt = null;\r\n");
		buffer.append("		ResultSet rs = null;\r\n");
		buffer.append("		try{\r\n");
		buffer.append("			conn = getConnection();\r\n");
		buffer.append("			cstmt = conn.prepareCall(\"{call " +tabla.getEsquema() + ".SP_PORTAL_HIS_SUBSANACION_REGISTRO(?,?,?,?,?,?,?,?,?,?,?)}\");\r\n");
		buffer.append("			cstmt.setString(1, historicoObservacion.getPiid());\r\n");
		buffer.append("			cstmt.setString(2, historicoObservacion.getCodigoIteracion());\r\n");
		buffer.append("			cstmt.setString(3, historicoObservacion.getCodigoObservacion());\r\n");
		buffer.append("			cstmt.setString(4, historicoObservacion.getCodigoTarea());\r\n");
		buffer.append("			cstmt.setInt(5, historicoObservacion.getCodigoTipoExcepcion());\r\n");
		buffer.append("			cstmt.setString(6, historicoObservacion.getComentarioExcepcion());\r\n");
		buffer.append("			cstmt.setString(7, historicoObservacion.getUsuarioExcepcion());\r\n");
		buffer.append("			cstmt.setString(8, historicoObservacion.getNombreUsuarioExcepcion());\r\n");
		buffer.append("			cstmt.setTimestamp(9, historicoObservacion.getFechaExcepcion());\r\n");
		buffer.append("			cstmt.setInt(10, historicoObservacion.getCodigoTipoDocumento());\r\n");
		buffer.append("			cstmt.setString(11, historicoObservacion.getEstado());\r\n");
		buffer.append("			cstmt.execute();\r\n");
		buffer.append("			resultado = true;\r\n");
		buffer.append("		} catch(SQLException e){\r\n");
		buffer.append("			throw new Exception(e);\r\n");
		buffer.append("		} finally {\r\n");
		buffer.append("			if (rs != null){	rs.close();             }\r\n");
		buffer.append("			if (cstmt != null){	cstmt.close();          }\r\n");
		buffer.append("			if (conn != null){	conn.close();           }\r\n");
		buffer.append("		}\r\n");
		buffer.append("		return resultado;\r\n");
		buffer.append("	}\r\n\r\n");
		
		return buffer.toString();
		
	}
	
	private String confirmarSubsanacion(Proyecto proyecto) {
		
		Tabla tabla = proyecto.getTablas().get(0);
		
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("	public boolean confirmarSubsanacion(HistoricoObservacion historicoObservacion) throws Exception {\r\n");
		buffer.append("		boolean resultado = false;\r\n");
		buffer.append("		Connection conn = null;\r\n");
		buffer.append("		CallableStatement cstmt = null;\r\n");
		buffer.append("		ResultSet rs = null;\r\n");
		buffer.append("		try{\r\n");
		buffer.append("			conn = getConnection();\r\n");
		buffer.append("			cstmt = conn.prepareCall(\"{call " +tabla.getEsquema() + ".SP_PORTAL_HIS_SUBSANACION_CONFIRMAR(?,?,?,?,?,?)}\");\r\n");
		buffer.append("			cstmt.setString(1, historicoObservacion.getPiid());\r\n");
		buffer.append("			cstmt.setString(2, historicoObservacion.getCodigoIteracion());\r\n");
		buffer.append("			cstmt.setString(3, historicoObservacion.getCodigoObservacion());\r\n");
		buffer.append("			cstmt.setString(4, historicoObservacion.getCodigoTarea());\r\n");
		buffer.append("			cstmt.setString(5, historicoObservacion.getEstado());\r\n");
		buffer.append("			cstmt.setBoolean(6, historicoObservacion.isEsConfirmado());\r\n");
		buffer.append("			cstmt.execute();\r\n");
		buffer.append("			resultado = true;\r\n");
		buffer.append("		} catch(SQLException e){\r\n");
		buffer.append("			throw new Exception(e);\r\n");
		buffer.append("		} finally {\r\n");
		buffer.append("			if (rs != null){	rs.close();             }\r\n");
		buffer.append("			if (cstmt != null){	cstmt.close();          }\r\n");
		buffer.append("			if (conn != null){	conn.close();           }\r\n");
		buffer.append("		}\r\n");
		buffer.append("		return resultado;\r\n");
		buffer.append("	}\r\n\r\n");
		
		return buffer.toString();
		
	}
		
	private String contenidoDaoImplObtenerHistorial(Proyecto proyecto, Proceso proceso, Connection conn) throws Exception{
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic List<TareaHistorial> obtenerHistorial(String piid) throws Exception{\r\n");
		
		buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tabla.getEsquema()+".SP_PORTAL_HIS_TAREA_CON_LISTA(?)}\");\r\n");
		buffer.append("\t\tcstmt.setString(1, piid);\r\n");
		buffer.append("\t\tcstmt.execute();\r\n\r\n");
		
		buffer.append("\t\tList<TareaHistorial> tareas = new ArrayList<TareaHistorial>();\r\n");
		buffer.append("\t\tResultSet rs = cstmt.getResultSet();\r\n");
		buffer.append("\t\twhile (rs.next()) {\r\n");
		
		buffer.append("\t\t\tTareaHistorial tareaHistorial = new TareaHistorial();\r\n");
		buffer.append("\t\t\ttareaHistorial.setPIID(rs.getString(\"HIS_TAREA_PIID\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setTKIID(rs.getString(\"HIS_TAREA_TKIID\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setCodigoTarea(rs.getString(\"HIS_TAREA_COD_TAREA\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setDescripcionTarea(rs.getString(\"HIS_TAREA_DES_TAREA\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setUsuarioPropietario(rs.getString(\"HIS_TAREA_USU_PROPIETARIO\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setComentario(rs.getString(\"HIS_TAREA_COMENTARIO\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setCodigoResultado(rs.getString(\"HIS_TAREA_COD_RESULTADO\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setDescripcionResultado(rs.getString(\"HIS_TAREA_DES_RESULTADO\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setFechaCreacion(rs.getTimestamp(\"HIS_TAREA_FEC_CREACION\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setFechaInicio(rs.getTimestamp(\"HIS_TAREA_FEC_INICIO\"));\r\n");
		buffer.append("\t\t\ttareaHistorial.setFechaTermino(rs.getTimestamp(\"HIS_TAREA_FEC_TERMINO\"));\r\n");
		
		buffer.append("\t\t\ttareas.add(tareaHistorial);\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\trs.close();\r\n");
		buffer.append("\t\tcstmt.close();\r\n");
		buffer.append("\t\tgetConnection().close();\r\n");
		buffer.append("\t\treturn tareas;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoDaoImplObtenerCatalogos(Proyecto proyecto, Proceso proceso, Connection conn) throws Exception{
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic Map<String, Map> obtenerCatalogos(String tipoCatalogos) throws Exception{\r\n");
		
		buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tabla.getEsquema()+".SP_PORTAL_CATALOGO_CON_LISTA(?,?,?)}\");\r\n");
		buffer.append("\t\tcstmt.setString(1, tipoCatalogos);\r\n");
		buffer.append("\t\tcstmt.registerOutParameter(2, Types.INTEGER);\r\n");
		buffer.append("\t\tcstmt.registerOutParameter(3, Types.VARCHAR);\r\n");
		buffer.append("\t\tcstmt.execute();\r\n\r\n");
		
		buffer.append("\t\tList<Catalogo> catalogos = null;\r\n");
		buffer.append("\t\tif(cstmt.getInt(2)!=0){\r\n");
		buffer.append("\t\t\tcatalogos = new ArrayList<Catalogo>();\r\n");
		buffer.append("\t\t\tResultSet rs = cstmt.getResultSet();\r\n");
		buffer.append("\t\t\t\twhile (rs.next()) {\r\n");
		buffer.append("\t\t\t\t\tCatalogo catalogo = new Catalogo();\r\n");
		buffer.append("\t\t\t\t\tcatalogo.setTabla(rs.getString(\"COD_CATALOGO\"));\r\n");
		buffer.append("\t\t\t\t\tcatalogo.setCodigoAtributo(rs.getString(\"COD_ATRIBUTO\"));\r\n");
		buffer.append("\t\t\t\t\tcatalogo.setValor1(rs.getString(\"VALOR_1\"));\r\n");
		buffer.append("\t\t\t\t\tcatalogo.setValor2(rs.getString(\"VALOR_2\"));\r\n");
		buffer.append("\t\t\t\t\tcatalogos.add(catalogo);\r\n");
		buffer.append("\t\t\t\t}\r\n");
		buffer.append("\t\t\t\trs.close();\r\n");
		buffer.append("\t\t\t}else{\r\n");
		buffer.append("\t\t\t\tthrow new Exception(\"No se generaron resultados (\"+cstmt.getInt(2)+\" : \"+cstmt.getString(3)+\")\");\r\n");
		buffer.append("\t\t\t}\r\n");
		
		buffer.append("\t\t\tMap<String, Map> mapTabla = new LinkedHashMap<String, Map>();\r\n");
		buffer.append("\t\t\tif (catalogos != null){\r\n");
		buffer.append("\t\t\tfor (Catalogo catalogo : catalogos){\r\n");
		buffer.append("\t\t\t\tif (mapTabla.containsKey(catalogo.getTabla())){\r\n");
		buffer.append("\t\t\t\t\tmapTabla.get(catalogo.getTabla()).put(catalogo.getCodigoAtributo(), catalogo.getValor1());\r\n");
		buffer.append("\t\t\t\t}else{\r\n");
		buffer.append("\t\t\t\t\tMap<String, String> mapElementoNuevo = new LinkedHashMap<String, String>();\r\n");
		buffer.append("\t\t\t\t\tmapElementoNuevo.put(catalogo.getCodigoAtributo(), catalogo.getValor1());\r\n");
		buffer.append("\t\t\t\t\tmapTabla.put(catalogo.getTabla(),mapElementoNuevo);\r\n");
		buffer.append("\t\t\t\t}\r\n");
		buffer.append("\t\t\t}\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\treturn mapTabla;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoDaoImplRegistrarSolicitud(Proceso proceso, Connection connection) throws Exception{
		StringBuffer buffer = new StringBuffer();
		
		Map<String, Atributo> map = new HashMap<String, Atributo>();
		StringBuffer parametros = new StringBuffer();
		StringBuffer numeroAtributosFK = new StringBuffer();
		
		int y = 0;
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {
			String paquete = proceso.getAtributosEntrada().get(x).getClase().getJavaPaquete();
			String clase = proceso.getAtributosEntrada().get(x).getClase().getJavaClase();
			
			if(map.containsKey(paquete+clase)==false){
				map.put(paquete+clase, proceso.getAtributosEntrada().get(x));
				if(y==0){
					parametros.append(clase+" "+clase.toLowerCase());
				}else{
					parametros.append(", "+clase+" "+clase.toLowerCase()+"  ");
				}
				
				y++;
			}
		}
		
		for (int i=0;i<proceso.getAtributosEntrada().size();i++){
			if(proceso.getAtributosEntrada().get(i).isFlgExisteCampoSQL()){
				if(numeroAtributosFK.length()==0){
					numeroAtributosFK.append("?");
				}else{
					numeroAtributosFK.append(",?");
				}
			}
		}
		
	 	buffer.append("\tpublic void registarSolicitud("+ parametros.toString()+") throws Exception{\r\n");
	 	
	 	buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+proceso.getAtributosEntrada().get(0).getCampoSQLProceso().getTabla().getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_REGISTRO("+numeroAtributosFK.toString()+")}\");\r\n");
		
	 	buffer.append(Validaciones.obtenerDaoCallableStatement(proceso.getAtributosEntrada(), connection));
	 	
		buffer.append("\t\tcstmt.execute();\r\n");
		buffer.append("\t\tcstmt.close();\r\n");
		buffer.append("\t\tgetConnection().close();\r\n");
		
		buffer.append("\t} \r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoDaoImplSecuencial(Proyecto proyecto, Proceso proceso, Connection conn){
		StringBuffer buffer = new StringBuffer();
		Tabla tabla = proyecto.getTablas().get(0);
		for (int x = 0; x < proceso.getAtributosEntrada().size(); x++) {
			if(proceso.getAtributosEntrada().get(x).isSqlFlgAutogenerado()){
				buffer.append("\tpublic "+proceso.getAtributosEntrada().get(x).getTipo()+" getSecuencial"+proceso.getAtributosEntrada().get(x).getNombre()+"() throws Exception{\r\n");
				
				buffer.append("\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call "+tabla.getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_SECUENCIA(?)}\");\r\n");
				buffer.append("\t\tcstmt.registerOutParameter(1, Types.BIGINT);\r\n");
				buffer.append("\t\tcstmt.execute();\r\n");
				buffer.append("\t\tlong codigo = cstmt.getLong(\"PO_"+proceso.getAtributosEntrada().get(x).getCampoSQLProceso().getNombre()+"\");\r\n");
				buffer.append("\t\tcstmt.close();\r\n");
				buffer.append("\t\tgetConnection().close();\r\n");
				buffer.append("\t\treturn codigo;\r\n");
				buffer.append("\t}\r\n\r\n");
			}
		}
		return buffer.toString();
	}
	
	private String contenidoBuscarSolicitud(Proceso proceso){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic GrillaLista buscarSolicitud(int skip, int cantidad, String tipoSolicitud, String filtro, String fechaDesde, String fechaHasta, String ordenFecha, int cargarTotal) throws Exception {\r\n");
		buffer.append("\t\treturn null;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String contenidoCabeceraBnd(Proyecto proyecto, List<Proceso> procesos, Connection connection) throws Exception{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n");
		buffer.append("<ejb-jar-bnd xmlns=\"http://websphere.ibm.com/xml/ns/javaee\"\r\n");
		buffer.append("\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n");
		buffer.append("\t\txsi:schemaLocation=\"http://websphere.ibm.com/xml/ns/javaee http://websphere.ibm.com/xml/ns/javaee/ibm-ejb-jar-bnd_1_0.xsd\"\r\n");
		buffer.append("\t\tversion=\"1.0\">\r\n\r\n");
		if (procesos != null){
			for (int i = 0; i < procesos.size(); i++) {
				Proceso proceso = procesos.get(i);
				buffer.append("\t\t<session name=\""+ proceso.getClase() +"DaoImpl\">\r\n");
				buffer.append("\t\t\t<resource-ref name=\""+proceso.getDatasource()+"\" binding-name=\""+proceso.getDatasource()+"\"></resource-ref>\r\n");
				buffer.append("\t\t</session>\r\n\r\n");
				List<Tarea> tareas = proyecto.getTareas();
				for (int x = 0; x < tareas.size(); x++) {
					Tarea tarea = tareas.get(x);
					buffer.append("\t\t<session name=\""+ tarea.getClase() +"DaoImpl\">\r\n");
					buffer.append("\t\t\t<resource-ref name=\""+tarea.getDataSource()+"\" binding-name=\""+tarea.getDataSource()+"\"></resource-ref>\r\n");
					buffer.append("\t\t</session>\r\n\r\n");
				}
			}
			buffer.append("\t</ejb-jar-bnd>\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	private String contenidoServletProcesoController(List<Proceso> procesos, Proyecto proyecto){
		StringBuffer buffer = new StringBuffer();
		
		if (procesos != null){
			buffer.append("package "+proyecto.getJavaNombrePaqueteControlador()+";\r\n\r\n");
			buffer.append("import java.util.HashMap;\r\n");
			buffer.append("import java.util.Map;\r\n\r\n");
			buffer.append("import pe.com.financiero.bpm.core.controller.ProcesoController;\r\n\r\n");
			buffer.append("public class "+proyecto.getJavaPreSufijoControlador()+"ProcesoController extends ProcesoController{\r\n\r\n");
			buffer.append("\tprivate static final long serialVersionUID = 1L;\r\n\r\n");
			buffer.append("\tpublic Map<String, String> cargarLookupMap() {\r\n");
			buffer.append("\t\tMap<String, String> lookMap = new HashMap<String, String>();\r\n");
			for (int i = 0; i < procesos.size(); i++) {
				Proceso proceso = procesos.get(i);
				buffer.append("\t\tlookMap.put(\""+proceso.getAleasSpring()+"\", \"ejblocal:"+proceso.getPaquete()+"."+proceso.getClase()+"\");\r\n");
			}
			buffer.append("\t\treturn lookMap;\r\n");
			buffer.append("\t}\r\n");
			
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
		}
	
		return buffer.toString();
	}
	
	private String registrarHistorialDocumento(Proceso proceso) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic Object registrarHistorialDocumento(HttpServletRequest request, java.util.Map<String, String> mapRequest, java.util.Map mapInputStream)  throws Exception{\r\n");
		buffer.append("\t\tUsuario usuario =  obtenerUsuario(request);\r\n");
		buffer.append("\t\tHistoricoDocumento historicoDocumento = new HistoricoDocumento();\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoHistoricoDocumento("+proceso.getClase().toLowerCase()+"Dao.obtenerSecuencialDocumento());\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoDocumento(Integer.parseInt(mapRequest.get(\"codigoDocumento\")));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoTipoDocumento(Integer.parseInt(mapRequest.get(\"codigoTipoDocumento\")));\r\n");
		buffer.append("\t\thistoricoDocumento.setPiid(mapRequest.get(\"piid\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoTarea(mapRequest.get(\"codigoTarea\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setDescripcionTarea(mapRequest.get(\"descripcionTarea\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setComentario(mapRequest.get(\"comentario\"));\r\n");
		buffer.append("\t\tif(mapRequest.get(\"adicional\")!=null){\r\n");
		buffer.append("\t\t	historicoDocumento.setAdicional(Boolean.parseBoolean(mapRequest.get(\"adicional\")));\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t\thistoricoDocumento.setSoloLectura(Boolean.parseBoolean(mapRequest.get(\"soloLectura\")));\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreUsuario(usuario.getNombre());\r\n");
		buffer.append("\t\thistoricoDocumento.setUsuario(usuario.getCodigo());\r\n");
		buffer.append("\t\thistoricoDocumento.setFecha(new Timestamp(new java.util.Date().getTime()));\r\n");
		buffer.append("\t\thistoricoDocumento = cargarHistoricoDocumento(historicoDocumento, mapRequest);\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreDocumento(ArchivoUtil.guardarArchivo((java.io.InputStream) mapInputStream.get(mapRequest.get(\"nombreObjeto\")), mapRequest.get(\"nombreOriginal\"), historicoDocumento.getRutaDocumento(), historicoDocumento.getNombreDocumento()));\r\n");
		buffer.append("\t\t"+proceso.getClase().toLowerCase()+"Dao.registrarHistorialDocumento(historicoDocumento);\r\n");
		buffer.append("\t\treturn historicoDocumento;\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();
	}
	
	private String cargarHistoricoDocumento(Proceso proceso) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\tpublic abstract HistoricoDocumento cargarHistoricoDocumento(HistoricoDocumento historicoDocumento,java.util.Map<String, String> mapRequest)throws Exception;\r\n\r\n");
		
		buffer.append("\tpublic abstract HistoricoDocumento cargarHistoricoDocumentoEliminar(HistoricoDocumento historicoDocumento, HttpServletRequest request)throws Exception;\r\n\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoDaoImplRegistrarHistorialDocumento(Proyecto proyecto) throws Exception {
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic void registrarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception {\r\n");
		buffer.append("\t\tCallableStatement cstmt = null;\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tcstmt = getConnection().prepareCall(\"{call " + tabla.getEsquema() + ".SP_PORTAL_HIS_DOCUMENTO_REGISTRO(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setLong(1, historicoDocumento.getCodigoHistoricoDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setInt(2, historicoDocumento.getCodigoDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(3, historicoDocumento.getPiid());\r\n");
		buffer.append("\t\t\tcstmt.setString(4, historicoDocumento.getCodigoTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(5, historicoDocumento.getDescripcionTarea());\r\n");
		buffer.append("\t\t\tcstmt.setInt(6, historicoDocumento.getCodigoTipoDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(7, historicoDocumento.getRutaDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(8, historicoDocumento.getNombreDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(9, historicoDocumento.getComentario());\r\n");
		buffer.append("\t\t\tcstmt.setString(10, historicoDocumento.getUsuario());\r\n");
		buffer.append("\t\t\tcstmt.setString(11, historicoDocumento.getNombreUsuario());\r\n");
		buffer.append("\t\t\tcstmt.setBoolean(12, historicoDocumento.isAdicional());\r\n");
		buffer.append("\t\t\tcstmt.setBoolean(13, historicoDocumento.isSoloLectura());\r\n");
		buffer.append("\t\t\tcstmt.setTimestamp(14, historicoDocumento.getFecha());\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif (cstmt != null){          	cstmt.close();          }\r\n");
		buffer.append("\t\t\tif (getConnection() != null){	getConnection().close();}\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("\tpublic void actualizarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception {\r\n");
		buffer.append("\t\tCallableStatement cstmt = null;\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tcstmt = getConnection().prepareCall(\"{call " + tabla.getEsquema() + ".SP_PORTAL_HIS_DOCUMENTO_ACTUALIZAR(?,?,?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setInt(1, historicoDocumento.getCodigoDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(2, historicoDocumento.getPiid());\r\n");
		buffer.append("\t\t\tcstmt.setString(3, historicoDocumento.getCodigoTarea());\r\n");
		buffer.append("\t\t\tcstmt.setString(4, historicoDocumento.getRutaDocumento());\r\n");
		buffer.append("\t\t\tcstmt.setString(5, historicoDocumento.getNombreDocumento());\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif (cstmt != null){          	cstmt.close();          }\r\n");
		buffer.append("\t\t\tif (getConnection() != null){	getConnection().close();}\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n\r\n");
		return buffer.toString();	
	}
	
	private String contenidoDaoImplSecuencialDocumento(Proyecto proyecto, Proceso proceso){
		StringBuffer buffer = new StringBuffer();
		Tabla tabla = proyecto.getTablas().get(0);
		buffer.append("\tpublic Long obtenerSecuencialDocumento() throws Exception{\r\n");
		
		buffer.append("\t\tCallableStatement cstmt = null;\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tcstmt = getConnection().prepareCall(\"{call "+tabla.getEsquema()+".SP_PORTAL_PRO_"+proceso.getClase().toUpperCase()+"_SECUENCIA_DOCUMENTO(?)}\");\r\n");
		buffer.append("\t\t\tcstmt.registerOutParameter(1, Types.BIGINT);\r\n");
		buffer.append("\t\t\tcstmt.execute();\r\n");
		buffer.append("\t\t\treturn  cstmt.getLong(\"PO_COD_DOCUMENTO\");\r\n");
		buffer.append("\t\t}catch(SQLException s){\r\n");
		buffer.append("\t\t\tthrow new Exception(s);\r\n");
		buffer.append("\t\t}finally{\r\n");
		buffer.append("\t\t\tif (cstmt != null){          	cstmt.close();          }\r\n");
		buffer.append("\t\t\tif (getConnection() != null){	getConnection().close();}\r\n");
		buffer.append("\t\t}\r\n");
		buffer.append("\t}\r\n");
		
		return buffer.toString();
	}
	
	private String contenidoDaoImplEliminarHistorialDocumento(Proyecto proyecto) throws Exception {
		Tabla tabla = proyecto.getTablas().get(0);
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic void eliminarHistorialDocumento(HistoricoDocumento historicoDocumento) throws Exception {\r\n");
		buffer.append("\t\ttry{\r\n");
		buffer.append("\t\t\tCallableStatement cstmt = getConnection().prepareCall(\"{call " + tabla.getEsquema() + ".SP_PORTAL_HIS_DOCUMENTO_ELIMINAR(?,?,?)}\");\r\n");
		buffer.append("\t\t\tcstmt.setLong(1, historicoDocumento.getCodigoHistoricoDocumento());\r\n");
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

	private String eliminarHistorialDocumento(Proceso proceso) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic Object eliminarHistorialDocumento(HttpServletRequest request) throws Exception{\r\n");
		buffer.append("\t\tHistoricoDocumento historicoDocumento = new HistoricoDocumento();\r\n");
		buffer.append("\t\thistoricoDocumento.setPiid(request.getParameter(\"piid\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoTarea(request.getParameter(\"codigoTarea\"));\r\n");
		buffer.append("\t\thistoricoDocumento.setCodigoHistoricoDocumento(Long.parseLong(request.getParameter(\"codigoHistoricoDocumento\")));\r\n");
		buffer.append("\t\thistoricoDocumento = cargarHistoricoDocumentoEliminar(historicoDocumento,request);\r\n");
		buffer.append("\t\thistoricoDocumento.setNombreDocumento(request.getParameter(\"nombreDocumento\"));\r\n");
		buffer.append("\t\tArchivoUtil.eliminarArchivo(historicoDocumento.getRutaDocumento(), historicoDocumento.getNombreDocumento());\r\n");
		buffer.append("\t\t"+proceso.getClase().toLowerCase()+"Dao.eliminarHistorialDocumento(historicoDocumento);\r\n");
		buffer.append("\t\treturn historicoDocumento;\r\n");
		buffer.append("\t}\r\n");
		return buffer.toString();
	}
}
