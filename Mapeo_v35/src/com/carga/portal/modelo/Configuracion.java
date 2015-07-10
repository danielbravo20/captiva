package com.carga.portal.modelo;

import java.io.File;

public class Configuracion {

	private File directorioWorkspace;
	private File directorioLib;
	private File directorioEJBCliente;
	private File directorioEJB;
	private File directorioEJBExt;
	private File directorioWEB;
	private File directorioWEBContent;
	private File directorioSQL;
	private String usuarioCreacion;
	public File getDirectorioWorkspace() {
		return directorioWorkspace;
	}
	public void setDirectorioWorkspace(File directorioWorkspace) {
		this.directorioWorkspace = directorioWorkspace;
	}
	public File getDirectorioLib() {
		return directorioLib;
	}
	public void setDirectorioLib(File directorioLib) {
		this.directorioLib = directorioLib;
	}
	public File getDirectorioEJBCliente() {
		return directorioEJBCliente;
	}
	public void setDirectorioEJBCliente(File directorioEJBCliente) {
		this.directorioEJBCliente = directorioEJBCliente;
	}
	public File getDirectorioEJB() {
		return directorioEJB;
	}
	public void setDirectorioEJB(File directorioEJB) {
		this.directorioEJB = directorioEJB;
	}
	public File getDirectorioWEB() {
		return directorioWEB;
	}
	public void setDirectorioWEB(File directorioWEB) {
		this.directorioWEB = directorioWEB;
	}
	public File getDirectorioWEBContent() {
		return directorioWEBContent;
	}
	public void setDirectorioWEBContent(File directorioWEBContent) {
		this.directorioWEBContent = directorioWEBContent;
	}
	public File getDirectorioSQL() {
		return directorioSQL;
	}
	public void setDirectorioSQL(File directorioSQL) {
		this.directorioSQL = directorioSQL;
	}
	public String getUsuarioCreacion() {
		return usuarioCreacion;
	}
	public void setUsuarioCreacion(String usuarioCreacion) {
		this.usuarioCreacion = usuarioCreacion;
	}
	public File getDirectorioEJBExt() {
		return directorioEJBExt;
	}
	public void setDirectorioEJBExt(File directorioEJBExt) {
		this.directorioEJBExt = directorioEJBExt;
	}
		
	
}
