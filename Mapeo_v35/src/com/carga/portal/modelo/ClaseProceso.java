package com.carga.portal.modelo;

import java.util.List;

public class ClaseProceso extends Clase{
	
	private int codigo;
	private String informacionDescripcion;
	private String informacionAutor;
	private String 	javaPaquete;
	private String 	javaClase;
	private String 	procesoObjeto;
	private List<AtributoProceso> atributosProceso;
	private String 	baseDatosEsquema;
	private String 	baseDatosTabla;
	
	public String getInformacionDescripcion() {
		return informacionDescripcion;
	}
	public void setInformacionDescripcion(String informacionDescripcion) {
		this.informacionDescripcion = informacionDescripcion;
	}
	public String getInformacionAutor() {
		return informacionAutor;
	}
	public void setInformacionAutor(String informacionAutor) {
		this.informacionAutor = informacionAutor;
	}
	public String getJavaPaquete() {
		return javaPaquete;
	}
	public void setJavaPaquete(String javaPaquete) {
		this.javaPaquete = javaPaquete;
	}
	public String getJavaClase() {
		return javaClase;
	}
	public void setJavaClase(String javaClase) {
		this.javaClase = javaClase;
	}
	public String getBaseDatosEsquema() {
		return baseDatosEsquema;
	}
	public void setBaseDatosEsquema(String baseDatosEsquema) {
		this.baseDatosEsquema = baseDatosEsquema;
	}
	public String getBaseDatosTabla() {
		return baseDatosTabla;
	}
	public void setBaseDatosTabla(String baseDatosTabla) {
		this.baseDatosTabla = baseDatosTabla;
	}
	public String getProcesoObjeto() {
		return procesoObjeto;
	}
	public void setProcesoObjeto(String procesoObjeto) {
		this.procesoObjeto = procesoObjeto;
	}
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public List<AtributoProceso> getAtributosProceso() {
		return atributosProceso;
	}
	public void setAtributosProceso(List<AtributoProceso> atributosProceso) {
		this.atributosProceso = atributosProceso;
	}

}
