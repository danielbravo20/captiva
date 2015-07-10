package com.carga.portal.modelo;

import java.util.ArrayList;
import java.util.List;

public class AS400Programa {

	private String codigoPrograma;
	private String nombre;
	private String usuario;
	private String transaccion;
	private String baseNameSpace;
	private String pathRequestIn;
	private String pathRequestOut;
	private String pathResponseIn;
	private String pathResponseOut;
	private List<AS400In> as400ins;
	private List<AS400Out> as400outs;
	private List<AS400InLista> as400inLista;
	private List<AS400OutLista> as400outLista;
	
	public String getCodigoPrograma() {
		return codigoPrograma;
	}
	public void setCodigoPrograma(String codigoPrograma) {
		this.codigoPrograma = codigoPrograma;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
	}
	public List<AS400In> getAs400ins() {
		if (as400ins == null){
			as400ins = new ArrayList<AS400In>();
		}
		return as400ins;
	}
	public void setAs400ins(List<AS400In> as400ins) {
		this.as400ins = as400ins;
	}
	public List<AS400Out> getAs400outs() {
		if (as400outs == null){
			as400outs = new ArrayList<AS400Out>();
		}
		return as400outs;
	}
	public void setAs400outs(List<AS400Out> as400outs) {
		this.as400outs = as400outs;
	}
	public String getBaseNameSpace() {
		return baseNameSpace;
	}
	public void setBaseNameSpace(String baseNameSpace) {
		this.baseNameSpace = baseNameSpace;
	}
	public String getPathResponseIn() {
		return pathResponseIn;
	}
	public void setPathResponseIn(String pathResponseIn) {
		this.pathResponseIn = pathResponseIn;
	}
	public String getPathResponseOut() {
		return pathResponseOut;
	}
	public void setPathResponseOut(String pathResponseOut) {
		this.pathResponseOut = pathResponseOut;
	}
	public String getPathRequestIn() {
		return pathRequestIn;
	}
	public void setPathRequestIn(String pathRequestIn) {
		this.pathRequestIn = pathRequestIn;
	}
	public String getPathRequestOut() {
		return pathRequestOut;
	}
	public void setPathRequestOut(String pathRequestOut) {
		this.pathRequestOut = pathRequestOut;
	}
	public List<AS400InLista> getAs400inLista() {
		if (as400inLista == null){
			as400inLista = new ArrayList<AS400InLista>();
		}
		return as400inLista;
	}
	public void setAs400inLista(List<AS400InLista> as400inLista) {
		this.as400inLista = as400inLista;
	}
	public List<AS400OutLista> getAs400outLista() {
		if (as400outLista == null){
			as400outLista = new ArrayList<AS400OutLista>();
		}
		return as400outLista;
	}
	public void setAs400outLista(List<AS400OutLista> as400outLista) {
		this.as400outLista = as400outLista;
	}

}
