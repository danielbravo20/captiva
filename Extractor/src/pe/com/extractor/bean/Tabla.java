package pe.com.extractor.bean;

import java.util.List;

public class Tabla {

	private String esquema;
	private String nombre;
	private List<Columna> columnas;
	
	public String getEsquema() {
		return esquema;
	}
	public void setEsquema(String esquema) {
		this.esquema = esquema;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<Columna> getColumnas() {
		return columnas;
	}
	public void setColumnas(List<Columna> columnas) {
		this.columnas = columnas;
	}
	
}
