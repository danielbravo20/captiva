package com.carga.portal.modelo;

public class Producto {
	
	private String sufijoProducto;
	private String descripcion;
	private String url;
	private UnidadNegocio un;
	
	public String getSufijoProducto() {
		return sufijoProducto;
	}
	public void setSufijoProducto(String sufijoProducto) {
		this.sufijoProducto = sufijoProducto;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public UnidadNegocio getUn() {
		return un;
	}
	public void setUn(UnidadNegocio un) {
		this.un = un;
	}
	
}