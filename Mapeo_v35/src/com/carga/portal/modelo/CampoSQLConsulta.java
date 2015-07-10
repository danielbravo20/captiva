package com.carga.portal.modelo;

public class CampoSQLConsulta extends CampoSQL{

	public boolean flgCondicion;
	public boolean flgVisible;

	public boolean isFlgCondicion() {
		return flgCondicion;
	}

	public void setFlgCondicion(boolean flgCondicion) {
		this.flgCondicion = flgCondicion;
	}

	public boolean isFlgVisible() {
		return flgVisible;
	}

	public void setFlgVisible(boolean flgVisible) {
		this.flgVisible = flgVisible;
	}
	
}
