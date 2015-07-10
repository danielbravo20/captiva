package com.carga.portal.servicio.crear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.List;

import com.carga.mapeo.dao.impl.ClaseDaoImpl;
import com.carga.portal.modelo.Atributo;
import com.carga.portal.modelo.Clase;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Proyecto;

public class CrearBean{
	
	public void crear(Proyecto proyecto, Configuracion configuracion, Connection conn) throws IOException{
		crearPaquetes(configuracion, proyecto, conn);
	}
	
	private void crearPaquetes(Configuracion configuracion,Proyecto proyecto, Connection conn) throws IOException{
		List<Clase> clases = new ClaseDaoImpl().obtenerPaquetes(proyecto,conn);
		for(int x=0;x<clases.size(); x++){
			Clase clase = clases.get(x);
			clase.setAtributos(new ClaseDaoImpl().obtenerAtributos(clase,conn));
			
			File directorio = new File(configuracion.getDirectorioWorkspace().getAbsolutePath()+"\\"+proyecto.getJavaNombreProyectoLibreria()+"\\generado\\"+(clase.getJavaPaquete()).replaceAll("\\.", "/"));
			if(directorio.exists()==false){
				directorio.mkdirs();
			}
			
			if(clase.getAtributos()!=null){
				
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
	}

	private String definirContenido(Clase clase){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+clase.getJavaPaquete()+";\r\n");
		buffer.append("\r\n");
		
		for (int i = 0; i < clase.getAtributos().size(); i++) {
			Atributo atributo = clase.getAtributos().get(i);
			if(atributo.isFlgLista()){
				buffer.append("import java.util.List;\r\n\r\n");
				break;
			}
		}
		buffer.append("import pe.com.financiero.bpm.core.modelo.ModeloBean;\r\n\r\n");
		
		buffer.append("/**\n * "+clase.getInformacionDescripcion()+"\n * @author "+clase.getInformacionAutor()+"\n * \n */\n");
		buffer.append("public class "+clase.getJavaClase()+" extends ModeloBean{\r\n \r\n");
		
		buffer.append("\tprivate static final long serialVersionUID = 1L;\r\n\r\n");
		
		for (int i = 0; i < clase.getAtributos().size(); i++) {
			Atributo campo = clase.getAtributos().get(i);
			if(campo.isFlgLista()){
				buffer.append("\t/**\n \t * "+campo.getInformacionDescripcion()+"\n\t */\n");
				buffer.append("\tprivate List<"+campo.getTipo()+"> "+campo.getNombre()+";\r\n");
			}else{
				buffer.append("\t/**\n \t * "+campo.getInformacionDescripcion()+"\n\t */\n");
				buffer.append("\tprivate "+campo.getTipo()+" "+campo.getNombre()+";\r\n");
			}
		}
		
		buffer.append("\r\n");
		for (int i = 0; i < clase.getAtributos().size(); i++) {
			Atributo campo = clase.getAtributos().get(i);
			if(campo.isFlgLista()){
				if(campo.getTipo().equalsIgnoreCase("boolean")==false){
					buffer.append("\tpublic List<"+campo.getTipo()+"> get"+(campo.getNombre().substring(0, 1)).toUpperCase() + campo.getNombre().substring(1) +"(){\r\n");
					buffer.append("\t\treturn "+campo.getNombre()+";\r\n");
					buffer.append("\t}\r\n\r\n");
				}else{
					buffer.append("\tpublic List<"+campo.getTipo()+">() is"+(campo.getNombre().substring(0, 1)).toUpperCase() + campo.getNombre().substring(1) +"(){\r\n");
					buffer.append("\t\treturn "+campo.getNombre()+";\r\n");
					buffer.append("\t}\r\n\r\n");
				}
				buffer.append("\tpublic void set"+(campo.getNombre().substring(0, 1)).toUpperCase() + campo.getNombre().substring(1)+"(List<"+campo.getTipo()+"> "+campo.getNombre()+") {\r\n");
				buffer.append("\t\tthis."+campo.getNombre()+" = "+campo.getNombre()+";\r\n");
				buffer.append("\t}\r\n\r\n");
			}else{
				if(campo.getTipo().equalsIgnoreCase("boolean")==false){
					buffer.append("\tpublic "+campo.getTipo()+" get"+(campo.getNombre().substring(0, 1)).toUpperCase() + campo.getNombre().substring(1) +"(){\r\n");
					buffer.append("\t\treturn "+campo.getNombre()+";\r\n");
					buffer.append("\t}\r\n\r\n");
				}else{
					buffer.append("\tpublic "+campo.getTipo()+" is"+(campo.getNombre().substring(0, 1)).toUpperCase() + campo.getNombre().substring(1) +"(){\r\n");
					buffer.append("\t\treturn "+campo.getNombre()+";\r\n");
					buffer.append("\t}\r\n\r\n");
				}
				buffer.append("\tpublic void set"+(campo.getNombre().substring(0, 1)).toUpperCase() + campo.getNombre().substring(1)+"("+campo.getTipo()+" "+campo.getNombre()+") {\r\n");
				buffer.append("\t\tthis."+campo.getNombre()+" = "+campo.getNombre()+";\r\n");
				buffer.append("\t}\r\n\r\n");
			
			}
			
		}
		
		buffer.append("}");
		return buffer.toString();
	}
}
	