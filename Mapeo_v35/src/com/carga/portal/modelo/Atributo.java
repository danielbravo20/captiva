package com.carga.portal.modelo;

public class Atributo {
	
	private int 	codigo;
	private int 	codigoClase;
	private String 	nombre;
	private String 	tipo;
	private boolean	flgLista;
	private String 	webNombre;
	private String 	webFormato;
	private String 	informacionNombre;
	private String 	informacionDescripcion;
	private String 	informacionAutor;
	private String  webFormatoValidacion;
	private Clase   clase;
	private CampoSQL campoSQL;
	
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public int getCodigoClase() {
		return codigoClase;
	}
	public void setCodigoClase(int codigoClase) {
		this.codigoClase = codigoClase;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public boolean isFlgLista() {
		return flgLista;
	}
	public void setFlgLista(boolean flgLista) {
		this.flgLista = flgLista;
	}
	public String getWebNombre() {
		return webNombre;
	}
	public void setWebNombre(String webNombre) {
		this.webNombre = webNombre;
	}
	public String getWebFormato() {
		return webFormato;
	}
	public void setWebFormato(String webFormato) {
		this.webFormato = webFormato;
	}
	public String getInformacionNombre() {
		return informacionNombre;
	}
	public void setInformacionNombre(String informacionNombre) {
		this.informacionNombre = informacionNombre;
	}
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
	public Clase getClase() {
		return clase;
	}
	public void setClase(Clase clase) {
		this.clase = clase;
	}
	public CampoSQL getCampoSQL() {
		return campoSQL;
	}
	public void setCampoSQL(CampoSQL campoSQL) {
		this.campoSQL = campoSQL;
	}
	public String getWebNombreParametro() {
		if(this.getWebNombre()!=null){
			return this.getWebNombre().replace('.', '_');
		}else{
			return null;
		}
	}
	public String getWebFormatoValidacion() {
		return webFormatoValidacion;
	}
	public void setWebFormatoValidacion(String webFormatoValidacion) {
		this.webFormatoValidacion = webFormatoValidacion;
	}
	
}
