package com.carga.portal.plantilla;

import com.carga.portal.modelo.Proceso;

public class Plantillas {

	public static String contenidoBeanHistorial(Proceso proceso){
	
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+proceso.getPaquete()+".util;\r\n\r\n");
		buffer.append("public class Historial {\r\n\r\n");
		buffer.append("\tprivate String tarea;\r\n");
		buffer.append("\tprivate String responsable;\r\n");
		buffer.append("\tprivate String comentario;\r\n");
		buffer.append("\tprivate String estado;\r\n");
		buffer.append("\tprivate String fechaCreacion;\r\n");
		buffer.append("\tprivate String fechaInicio;\r\n");
		buffer.append("\tprivate String fechaTermino;\r\n\r\n");
		buffer.append("\tpublic String getTarea() {\r\n");
		buffer.append("\t\treturn tarea;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setTarea(String tarea) {\r\n");
		buffer.append("\t\tthis.tarea = tarea;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic String getResponsable() {\r\n");
		buffer.append("\t\treturn responsable;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setResponsable(String responsable) {\r\n");
		buffer.append("\t\tthis.responsable = responsable;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic String getComentario() {\r\n");
		buffer.append("\t\treturn comentario;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setComentario(String comentario) {\r\n");
		buffer.append("\t\tthis.comentario = comentario;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic String getEstado() {\r\n");
		buffer.append("\t\treturn estado;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setEstado(String estado) {\r\n");
		buffer.append("\t\tthis.estado = estado;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic String getFechaCreacion() {\r\n");
		buffer.append("\t\treturn fechaCreacion;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setFechaCreacion(String fechaCreacion) {\r\n");
		buffer.append("\t\tthis.fechaCreacion = fechaCreacion;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic String getFechaInicio() {\r\n");
		buffer.append("\t\treturn fechaInicio;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setFechaInicio(String fechaInicio) {\r\n");
		buffer.append("\t\tthis.fechaInicio = fechaInicio;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic String getFechaTermino() {\r\n");
		buffer.append("\t\treturn fechaTermino;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setFechaTermino(String fechaTermino) {\r\n");
		buffer.append("\t\tthis.fechaTermino = fechaTermino;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("}");
		
		return buffer.toString();
	}
	/*
	public static String contenidoBeanResumen(Proceso proceso){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package "+proceso.getPaquete()+".util;\r\n");
		buffer.append("\r\n");
		buffer.append("import java.util.List;\r\n\r\n");
		buffer.append("public class ResumenProceso {\r\n\r\n");
		
		for (int i = 0; i < proceso.getAtributosResumen().size(); i++) {
			Atributo campo = proceso.getAtributosResumen().get(i); 
			buffer.append("\tprivate "+campo.getJavaTipo()+" "+campo.getJavaAtributo()+";\r\n");
		}
		
		buffer.append("\tprivate List<Historial> historial;\r\n");
		
		buffer.append("\r\n");
		for (int i = 0; i < proceso.getAtributosResumen().size(); i++) {
			Atributo campo = proceso.getAtributosResumen().get(i);
			
			if(campo.getJavaTipo().equalsIgnoreCase("boolean")==false){
				buffer.append("\tpublic "+campo.getJavaTipo()+" get"+(campo.getJavaAtributo().substring(0, 1)).toUpperCase() + campo.getJavaAtributo().substring(1) +"(){\r\n");
				buffer.append("\t\treturn "+campo.getJavaAtributo()+";\r\n");
				buffer.append("\t}\r\n\r\n");
			}else{
				buffer.append("\tpublic "+campo.getJavaTipo()+" is"+(campo.getJavaAtributo().substring(0, 1)).toUpperCase() + campo.getJavaAtributo().substring(1) +"(){\r\n");
				buffer.append("\t\treturn "+campo.getJavaAtributo()+";\r\n");
				buffer.append("\t}\r\n\r\n");
			}
			
			buffer.append("\tpublic void set"+(campo.getJavaAtributo().substring(0, 1)).toUpperCase() + campo.getJavaAtributo().substring(1)+"("+campo.getJavaTipo()+" "+campo.getJavaAtributo()+") {\r\n");
			buffer.append("\t\tthis."+campo.getJavaAtributo()+" = "+campo.getJavaAtributo()+";\r\n");
			buffer.append("\t}\r\n\r\n");
			
		}
		
		buffer.append("\tpublic List<Historial> getHistorial() {\r\n");
		buffer.append("\t\treturn historial;\r\n");
		buffer.append("\t}\r\n\r\n");
		buffer.append("\tpublic void setHistorial(List<Historial> historial) {\r\n");
		buffer.append("\t\tthis.historial = historial;\r\n");
		buffer.append("\t}\r\n\r\n");
		
		buffer.append("}");
		return buffer.toString();
	}
	*/
}
