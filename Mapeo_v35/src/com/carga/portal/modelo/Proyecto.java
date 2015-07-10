package com.carga.portal.modelo;

import java.util.List;

public class Proyecto {
	private int codigoProyecto;
	private String nombre;
	private String javaNombreProyectoLibreria;
	private String javaNombreProyectoEJB;
	private String javaNombreProyectoEJBExtension;
	private String javaNombreProyectoEJBCliente;
	private String javaNombreProyectoWeb;
	private String javaNombrePaquete;
	private String javaNombrePaqueteControlador;
	private String javaPreSufijoControlador;
	private List<Tabla> tablas;
	private List<Consulta> consultas;
	private List<Proceso> procesos;
	private List<Tarea> tareas;
	private List<UnidadNegocio> unidadesNegocio;
	private List<Producto> productos;
	private List<PerfilModulo> perfilesxModulo;
	private List<Perfil> perfiles;
	private List<Catalogo> catalogos;
	
	public int getCodigoProyecto() {
		return codigoProyecto;
	}
	public void setCodigoProyecto(int codigoProyecto) {
		this.codigoProyecto = codigoProyecto;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getJavaNombreProyectoLibreria() {
		return javaNombreProyectoLibreria;
	}
	public void setJavaNombreProyectoLibreria(String javaNombreProyectoLibreria) {
		this.javaNombreProyectoLibreria = javaNombreProyectoLibreria;
	}
	public String getJavaNombreProyectoEJB() {
		return javaNombreProyectoEJB;
	}
	public void setJavaNombreProyectoEJB(String javaNombreProyectoEJB) {
		this.javaNombreProyectoEJB = javaNombreProyectoEJB;
	}
	public String getJavaNombreProyectoEJBCliente() {
		return javaNombreProyectoEJBCliente;
	}
	public void setJavaNombreProyectoEJBCliente(String javaNombreProyectoEJBCliente) {
		this.javaNombreProyectoEJBCliente = javaNombreProyectoEJBCliente;
	}
	public String getJavaNombreProyectoWeb() {
		return javaNombreProyectoWeb;
	}
	public void setJavaNombreProyectoWeb(String javaNombreProyectoWeb) {
		this.javaNombreProyectoWeb = javaNombreProyectoWeb;
	}
	public String getJavaNombrePaquete() {
		return javaNombrePaquete;
	}
	public void setJavaNombrePaquete(String javaNombrePaquete) {
		this.javaNombrePaquete = javaNombrePaquete;
	}
	public String getJavaNombrePaqueteControlador() {
		return javaNombrePaqueteControlador;
	}
	public void setJavaNombrePaqueteControlador(String javaNombrePaqueteControlador) {
		this.javaNombrePaqueteControlador = javaNombrePaqueteControlador;
	}
	public String getJavaPreSufijoControlador() {
		return javaPreSufijoControlador;
	}
	public void setJavaPreSufijoControlador(String javaPreSufijoControlador) {
		this.javaPreSufijoControlador = javaPreSufijoControlador;
	}
	public List<Tabla> getTablas() {
		return tablas;
	}
	public void setTablas(List<Tabla> tablas) {
		this.tablas = tablas;
	}
	public List<Consulta> getConsultas() {
		return consultas;
	}
	public void setConsultas(List<Consulta> consultas) {
		this.consultas = consultas;
	}
	public List<Proceso> getProcesos() {
		return procesos;
	}
	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}
	public List<Tarea> getTareas() {
		return tareas;
	}
	public void setTareas(List<Tarea> tareas) {
		this.tareas = tareas;
	}
	public List<UnidadNegocio> getUnidadesNegocio() {
		return unidadesNegocio;
	}
	public void setUnidadesNegocio(List<UnidadNegocio> unidadesNegocio) {
		this.unidadesNegocio = unidadesNegocio;
	}
	public List<Producto> getProductos() {
		return productos;
	}
	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}
	public List<PerfilModulo> getPerfilesxModulo() {
		return perfilesxModulo;
	}
	public void setPerfilesxModulo(List<PerfilModulo> perfilesxModulo) {
		this.perfilesxModulo = perfilesxModulo;
	}
	public List<Perfil> getPerfiles() {
		return perfiles;
	}
	public void setPerfiles(List<Perfil> perfiles) {
		this.perfiles = perfiles;
	}
	public List<Catalogo> getCatalogos() {
		return catalogos;
	}
	public void setCatalogos(List<Catalogo> catalogos) {
		this.catalogos = catalogos;
	}
	public String getJavaNombreProyectoEJBExtension() {
		return javaNombreProyectoEJBExtension;
	}
	public void setJavaNombreProyectoEJBExtension(
			String javaNombreProyectoEJBExtension) {
		this.javaNombreProyectoEJBExtension = javaNombreProyectoEJBExtension;
	}

}
