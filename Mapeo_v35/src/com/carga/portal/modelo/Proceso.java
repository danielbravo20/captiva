package com.carga.portal.modelo;

import java.util.List;

public class Proceso {
	
	private int codigo;
	private int codigoProyecto;
	private String proyecto;
	private String paquete;
	private String clase;
	private String nombrePlantilla;
	private int actividadInicio;
	private List<AtributoProceso> atributosEntrada;
	private List<CampoSQLProceso> camposSQLProceso;
	private String claseDao;
	private String datasource;
	private String aleasSpring;
	private String sufijoBanca;
	private String sufijoProducto;
	private String sufijoProceso;
	private Consulta consultaResumen;
	private Consulta consultaDetalle;
	private Tabla tablaResumen;
	private Tabla tablaDetalle;
	private String nombre;
	private List<Tarea> tareas;
	private List<AtributoProceso> objetosDOEntreda;
	private List<AtributoProceso> objetosDOsinEntreda;
	private String nombreSecuenciaDocumentos;
	
	public String getProyecto() {
		return proyecto;
	}
	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public Tabla getTablaDetalle() {
		return tablaDetalle;
	}
	public void setTablaDetalle(Tabla tablaDetalle) {
		this.tablaDetalle = tablaDetalle;
	}
	
	public String getClaseDao() {
		return claseDao;
	}
	public void setClaseDao(String claseDao) {
		this.claseDao = claseDao;
	}
	public String getPaqueteDao() {
		return paqueteDao;
	}
	public void setPaqueteDao(String paqueteDao) {
		this.paqueteDao = paqueteDao;
	}
	private String paqueteDao;
	
	public String getNombrePlantilla() {
		return nombrePlantilla;
	}
	public void setNombrePlantilla(String nombrePlantilla) {
		this.nombrePlantilla = nombrePlantilla;
	}
	
	public String getPaquete() {
		return paquete;
	}
	public void setPaquete(String paquete) {
		this.paquete = paquete;
	}
	public String getClase() {
		return clase;
	}
	public void setClase(String clase) {
		this.clase = clase;
	}
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public String getAleasSpring() {
		return aleasSpring;
	}
	public void setAleasSpring(String aleasSpring) {
		this.aleasSpring = aleasSpring;
	}
	public String getSufijoBanca() {
		return sufijoBanca;
	}
	public void setSufijoBanca(String sufijoBanca) {
		this.sufijoBanca = sufijoBanca;
	}
	public String getSufijoProducto() {
		return sufijoProducto;
	}
	public void setSufijoProducto(String sufijoProducto) {
		this.sufijoProducto = sufijoProducto;
	}
	public String getSufijoProceso() {
		return sufijoProceso;
	}
	public void setSufijoProceso(String sufijoProceso) {
		this.sufijoProceso = sufijoProceso;
	}
	public Tabla getTablaResumen() {
		return tablaResumen;
	}
	public void setTablaResumen(Tabla tablaResumen) {
		this.tablaResumen = tablaResumen;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public int getCodigoProyecto() {
		return codigoProyecto;
	}
	public void setCodigoProyecto(int codigoProyecto) {
		this.codigoProyecto = codigoProyecto;
	}
	public int getActividadInicio() {
		return actividadInicio;
	}
	public void setActividadInicio(int actividadInicio) {
		this.actividadInicio = actividadInicio;
	}
	public List<Tarea> getTareas() {
		return tareas;
	}
	public void setTareas(List<Tarea> tareas) {
		this.tareas = tareas;
	}
	public List<AtributoProceso> getAtributosEntrada() {
		return atributosEntrada;
	}
	public void setAtributosEntrada(List<AtributoProceso> atributosEntrada) {
		this.atributosEntrada = atributosEntrada;
	}
	public List<CampoSQLProceso> getCamposSQLProceso() {
		return camposSQLProceso;
	}
	public void setCamposSQLProceso(List<CampoSQLProceso> camposSQLProceso) {
		this.camposSQLProceso = camposSQLProceso;
	}
	public Consulta getConsultaResumen() {
		return consultaResumen;
	}
	public void setConsultaResumen(Consulta consultaResumen) {
		this.consultaResumen = consultaResumen;
	}
	public Consulta getConsultaDetalle() {
		return consultaDetalle;
	}
	public void setConsultaDetalle(Consulta consultaDetalle) {
		this.consultaDetalle = consultaDetalle;
	}
	public List<AtributoProceso> getObjetosDOEntreda() {
		return objetosDOEntreda;
	}
	public void setObjetosDOEntreda(List<AtributoProceso> objetosDOEntreda) {
		this.objetosDOEntreda = objetosDOEntreda;
	}
	public List<AtributoProceso> getObjetosDOsinEntreda() {
		return objetosDOsinEntreda;
	}
	public void setObjetosDOsinEntreda(List<AtributoProceso> objetosDOsinEntreda) {
		this.objetosDOsinEntreda = objetosDOsinEntreda;
	}
	public String getNombreSecuenciaDocumentos() {
		return nombreSecuenciaDocumentos;
	}
	public void setNombreSecuenciaDocumentos(String nombreSecuenciaDocumentos) {
		this.nombreSecuenciaDocumentos = nombreSecuenciaDocumentos;
	}
	
	
}
