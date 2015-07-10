package com.carga.portal.servicio.crear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.List;
import com.carga.portal.modelo.AtributoTarea;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Proceso;
import com.carga.portal.modelo.Proyecto;
import com.carga.portal.modelo.Tarea;

public class CrearWebValidacion {
	
	public void crear(Proyecto proyecto, Configuracion configuracion, Connection connection) throws Exception{
		crearBase(proyecto, proyecto.getTareas(), configuracion, connection);
	}
	
	private void crearBase(Proyecto proyecto, List<Tarea> tareas, Configuracion configuracion, Connection connection) throws Exception{
		
		
		File directorioWebContent = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoWeb()+"\\WebContent\\");
		if(directorioWebContent.exists()==false){
			directorioWebContent.mkdirs();
		}
		
		for(int x=0;x<proyecto.getProcesos().size(); x++){
			Proceso proceso = proyecto.getProcesos().get(x);
			
			File directorioWebProceso = new File(directorioWebContent.getAbsolutePath()+"\\"+proceso.getSufijoBanca()+"\\"+proceso.getClase()+"\\preControlador\\tareas");
			if(directorioWebProceso.exists()==false){
				directorioWebProceso.mkdirs();
			}
			
			for(int y=0;y<proceso.getTareas().size(); y++){
				Tarea tarea = proceso.getTareas().get(y);
				
				File archivoValidacion = new File(directorioWebProceso.getAbsolutePath()+"\\"+tarea.getWebNombreConfiguracion()+"_reglas.js");
				if(archivoValidacion.exists()){
					archivoValidacion.delete();
				}
				archivoValidacion.createNewFile();
				BufferedWriter bwProcesoController =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoValidacion,true), "ISO-8859-1"));
				bwProcesoController.write(contenidoArchivoValidacion(proyecto, tarea, connection));
				bwProcesoController.close();
				
			}
		}
	}
	
	private String contenidoArchivoValidacion(Proyecto proyecto, Tarea tarea, Connection connection) throws Exception{
		StringBuffer contenido = new StringBuffer();
		
		contenido.append("portal.registerCtrl('tareaCargada_reglas', function($scope) {\r\n");
		contenido.append("\t$scope.reglas = {\r\n");
		
		boolean inicio = false;
		
		if(tarea.getAtributosCompletarValidacionWeb()!=null){
			for (int i = 0; i < tarea.getAtributosCompletarValidacionWeb().size(); i++) {
				AtributoTarea atributoTarea = tarea.getAtributosCompletarValidacionWeb().get(i);
				
				if(atributoTarea.isRequiereValidacion() && atributoTarea.getWebFormatoValidacion()!=null){
					if(inicio){
						contenido.append("\t\t},\r\n");
					}else{
						inicio = true;
					}
					contenido.append("\t\t\""+atributoTarea.getWebNombre()+"\" : {\r\n");
					if(atributoTarea.getInformacionNombre()==null){
						contenido.append("\t\t\tnombre : \""+atributoTarea.getWebNombre()+"\",\r\n");
					}else{
						contenido.append("\t\t\tnombre : \""+atributoTarea.getInformacionNombre()+"\",\r\n");
					}
					contenido.append("\t\t\tvalidacion : ["+atributoTarea.getWebFormatoValidacion()+"],\r\n");
					contenido.append("\t\t\ttab : "+atributoTarea.getWebTabCampo()+"\r\n");
				}
			}
			contenido.append("\t\t}\r\n");
		}
		
		contenido.append("\t};\r\n");
		contenido.append("});\r\n");
		
		return contenido.toString();
	}
	
}
