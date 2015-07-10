package com.carga.portal.modelo;

import java.util.List;

public class TablaConsulta extends Tabla{

	private int codigoConsulta;
	public boolean flgFK;
	public boolean flgUnoMuchos;
	public List<CampoSQLConsulta> campoSQLConsultas;
	public List<AtributoConsulta> atributosConsulta;
	
	public boolean isFlgFK() {
		return flgFK;
	}
	public void setFlgFK(boolean flgFK) {
		this.flgFK = flgFK;
	}
	public boolean isFlgUnoMuchos() {
		return flgUnoMuchos;
	}
	public void setFlgUnoMuchos(boolean flgUnoMuchos) {
		this.flgUnoMuchos = flgUnoMuchos;
	}
	public List<CampoSQLConsulta> getCampoSQLConsultas() {
		return campoSQLConsultas;
	}
	public void setCampoSQLConsultas(List<CampoSQLConsulta> campoSQLConsultas) {
		this.campoSQLConsultas = campoSQLConsultas;
	}
	public void setCodigoConsulta(int codigoConsulta) {
		this.codigoConsulta = codigoConsulta;
	}
	public int getCodigoConsulta() {
		return codigoConsulta;
	}
	public List<AtributoConsulta> getAtributosConsulta() {
		return atributosConsulta;
	}
	public void setAtributosConsulta(List<AtributoConsulta> atributosConsulta) {
		this.atributosConsulta = atributosConsulta;
	}
	
}
