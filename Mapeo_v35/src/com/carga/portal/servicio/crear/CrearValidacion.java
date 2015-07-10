package com.carga.portal.servicio.crear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carga.mapeo.dao.impl.ClaseDaoImpl;
import com.carga.portal.modelo.Atributo;
import com.carga.portal.modelo.AtributoTarea;
import com.carga.portal.modelo.Clase;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Proyecto;

public class CrearValidacion {
	
	public void escribirClases(Proyecto proyecto, Configuracion configuracion, Connection conn) throws IOException{
		ClaseDaoImpl claseDaoImpl = new ClaseDaoImpl();
		crearPaquetes(configuracion, claseDaoImpl.obtenerPaquetes(proyecto, conn));
	}
	
	private void crearPaquetes(Configuracion configuracion, List<Clase> clases) throws IOException{
		for(int x=0;x<clases.size(); x++){
			Clase clase = clases.get(x);
			File directorio = new File(configuracion.getDirectorioEJB().getAbsoluteFile()+"\\"+(clase.getJavaPaquete()).replaceAll("\\.", "/"));
			if(directorio.exists()==false){
				directorio.mkdirs();
			}
			
			File archivo = new File(directorio.getAbsolutePath()+"\\",clases.get(x).getJavaClase()+".java");
			if(archivo.exists()){
				archivo.delete();
			}
			archivo.createNewFile();
			BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo,true), "ISO-8859-1"));
			bufferedWriter.write(definirContenido(clase));
			bufferedWriter.close();
		}
	}
	
	private String definirContenido(Clase clase){
		
		List<Clase> clases = definirMetodos(clase);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+clase.getJavaPaquete()+";\r\n");
		buffer.append("\r\n");
		for (int x = 0; x < clases.size(); x++) {
			clase = clases.get(x);
			buffer.append("import "+clase.getJavaPaquete()+"."+clase.getJavaClase()+";\r\n");
		}
		buffer.append("\r\n");
		buffer.append("public class "+clase.getJavaClase()+"Validacion {\r\n \r\n");
		
		for (int x = 0; x < clases.size(); x++) {
			
			clase = clases.get(x);
			buffer.append("\tpublic "+clase.getJavaClase()+" cargar"+clase.getJavaClase()+"(HttpServletRequest request, HttpServletResponse response){\r\n");
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			
			for (int i = 0; i < clase.getAtributos().size(); i++) {
				Atributo atributo = clase.getAtributos().get(i);
				buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(("+atributo.getTipo()+")request.getAttribute(\""+atributo.getWebNombre()+"\"));\r\n");
			}
			buffer.append("\t\treturn "+clase.getJavaClase().toLowerCase()+";\r\n");
			buffer.append("\t}\r\n\r\n");
		}
		
		buffer.append("}\r\n");
		return buffer.toString();
	}

	private String nombreVariable(String cadena){
		return cadena.substring(0, 1).toUpperCase() + cadena.substring(1);
	}
	
	private List<Clase> definirMetodos(Clase clase){
		Map<String, Clase> metodos = new HashMap<String, Clase>();
		List<Clase> clases = new ArrayList<Clase>();
		List<String> keys = new ArrayList<String>();
		
		for (int i = 0; i < clase.getAtributos().size(); i++) {
			AtributoTarea atributo = clase.getAtributos().get(i);
			String key = atributo.getClase().getJavaPaquete()+"."+atributo.getClase().getJavaClase();
			Clase claseTemporal = null;
			if(metodos.containsKey(key)){
				claseTemporal = metodos.get(key);
			}else{
				claseTemporal = new Clase();
				claseTemporal.setAtributos(new ArrayList<AtributoTarea>());
				keys.add(key);
			}
			claseTemporal.setJavaClase(atributo.getClase().getJavaClase());
			claseTemporal.setJavaPaquete(atributo.getClase().getJavaPaquete());
			claseTemporal.getAtributos().add(atributo);
			metodos.put(key, claseTemporal);
		}
		
		for (int i = 0; i < keys.size(); i++) {
			clases.add(metodos.get(keys.get(i)));
			
		}
		
		return clases;
	}
}