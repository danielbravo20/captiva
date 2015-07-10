package com.carga.portal.servicio.crear.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carga.mapeo.dao.impl.ProcesoDaoImpl;
import com.carga.portal.modelo.AtributoProceso;
import com.carga.portal.modelo.AtributoTarea;
import com.carga.portal.modelo.CampoSQL;
import com.carga.portal.modelo.Clase;
import com.carga.portal.modelo.ClaseProceso;

public class Validaciones {
	
	public static String escribirValidacionString(AtributoProceso atributo){
		StringBuffer buffer = new StringBuffer();
		if (atributo != null && atributo.getCampoSQLProceso()!=null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length()>"+atributo.getCampoSQLProceso().getLongitud()+"){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
				
		return buffer.toString();
	}
	
	public static String escribirValidacionString(AtributoTarea atributo){
		StringBuffer buffer = new StringBuffer();
		
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			if(atributo.getCampoSQLTarea()!=null){
				buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length()>"+atributo.getCampoSQLTarea().getLongitud()+"){\r\n");
				buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
				buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
				buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
				buffer.append("\t\t\treturn mensajeValidacion;\r\n");
				buffer.append("\t\t}\r\n\r\n");
			}
		}
		
		return buffer.toString();
	}
	
	public static String escribirValidacionLong(AtributoProceso atributo){
		StringBuffer buffer = new StringBuffer();
		
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\ttry {\r\n");
			buffer.append("\t\t\tInteger.parseInt("+atributo.getWebNombreParametro()+");\r\n");
			buffer.append("\t\t} catch (NumberFormatException nfe){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			/*
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length()>"+atributo.getCampoSQL().getLongitud()+"){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			*/
		}
		
		
		return buffer.toString();
	}
	
	public static String escribirValidacionLong(AtributoTarea atributo){
		StringBuffer buffer = new StringBuffer();
		
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombre()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\ttry {\r\n");
			buffer.append("\t\t\tInteger.parseInt("+atributo.getWebNombreParametro()+");\r\n");
			buffer.append("\t\t} catch (NumberFormatException nfe){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length()>"+atributo.getCampoSQLTarea().getLongitud()+"){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	public static String escribirValidacionInteger(AtributoProceso atributo){
		StringBuffer buffer = new StringBuffer();
		
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\ttry {\r\n");
			buffer.append("\t\t\tInteger.parseInt("+atributo.getWebNombreParametro()+");\r\n");
			buffer.append("\t\t} catch (NumberFormatException nfe){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	public static String escribirValidacionInteger(AtributoTarea atributo){
		StringBuffer buffer = new StringBuffer();
		
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\ttry {\r\n");
			buffer.append("\t\t\tInteger.parseInt("+atributo.getWebNombreParametro()+");\r\n");
			buffer.append("\t\t} catch (NumberFormatException nfe){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}	
	
	public static String escribirValidacionBoolean(AtributoProceso atributo){
		StringBuffer buffer = new StringBuffer();
		
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length()>1){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	public static String escribirValidacionBoolean(AtributoTarea atributo){
		StringBuffer buffer = new StringBuffer();
		
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length()>1 ){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	
	public static String escribirValidacionBigDecimal(AtributoProceso atributo){
		StringBuffer buffer = new StringBuffer();
		if (atributo != null && atributo.getCampoSQLProceso()!=null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\ttry{\r\n");
			buffer.append("\t\t\tjava.math.BigDecimal "+atributo.getWebNombreParametro()+"BD = new java.math.BigDecimal("+atributo.getWebNombreParametro()+".trim()); \r\n");
			buffer.append("\t\t\tif("+atributo.getWebNombreParametro()+"BD.precision()>"+atributo.getCampoSQLProceso().getLongitud()+"){\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombreParametro()+"\");\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t\t}else if("+atributo.getWebNombreParametro()+"BD.scale()>"+atributo.getCampoSQLProceso().getPrecision()+"){\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t\t}\r\n");
			buffer.append("\t\t} catch (NumberFormatException nfe){\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	public static String escribirValidacionBigDecimal(AtributoTarea atributo){
		StringBuffer buffer = new StringBuffer();
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+"==null || "+atributo.getWebNombreParametro()+".trim().length()==0){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\ttry{\r\n");
			buffer.append("\t\t\tjava.math.BigDecimal "+atributo.getWebNombreParametro()+"BD = new java.math.BigDecimal("+atributo.getWebNombreParametro()+".trim()); \r\n");
			
			buffer.append("\t\t\tif("+atributo.getWebNombreParametro()+"BD.precision()>"+atributo.getCampoSQLTarea().getLongitud()+"){\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t\t}else if("+atributo.getWebNombreParametro()+"BD.scale()>"+atributo.getCampoSQLTarea().getPrecision()+"){\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t\t}\r\n");
			buffer.append("\t\t} catch (NumberFormatException nfe){\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombreParametro()+"\");\r\n");
			buffer.append("\t\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	public static String escribirValidacionDate(AtributoProceso atributo){
		StringBuffer buffer = new StringBuffer();
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif ("+atributo.getWebNombreParametro()+" == null || "+atributo.getWebNombreParametro()+".trim().length() == 0 ){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n"); 
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\tjava.text.SimpleDateFormat "+atributo.getWebNombreParametro()+"SDF = new java.text.SimpleDateFormat(\""+atributo.getWebFormato()+"\");\r\n");  
			  
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length() != "+atributo.getWebNombreParametro()+"SDF.toPattern().length()){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\t"+atributo.getWebNombreParametro()+"SDF.setLenient(false);\r\n");
			  
			buffer.append("\t\ttry{\r\n");
			buffer.append("\t\t\t"+atributo.getWebNombreParametro()+"SDF.parse("+atributo.getWebNombreParametro()+".trim());\r\n"); 
			buffer.append("\t\t}catch (java.text.ParseException pe) { \r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n"); 
			buffer.append("\t\t}\r\n\r\n");
		}
		  
		return buffer.toString();
	}
	
	public static String escribirValidacionDate(AtributoTarea atributo){
		StringBuffer buffer = new StringBuffer();
		if (atributo != null){
			buffer.append("\t\tString "+atributo.getWebNombreParametro()+" = request.getParameter(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\tif ("+atributo.getWebNombreParametro()+" == null || "+atributo.getWebNombreParametro()+".trim().length() == 0 ){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n"); 
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\tjava.text.SimpleDateFormat "+atributo.getWebNombreParametro()+"SDF = new java.text.SimpleDateFormat(\""+atributo.getWebFormato()+"\");\r\n");  
			  
			buffer.append("\t\tif("+atributo.getWebNombreParametro()+".trim().length() != "+atributo.getWebNombreParametro()+"SDF.toPattern().length()){\r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n");
			buffer.append("\t\t}\r\n\r\n");
			
			buffer.append("\t\t"+atributo.getWebNombreParametro()+"SDF.setLenient(false);\r\n");
			  
			buffer.append("\t\ttry{\r\n");
			buffer.append("\t\t\t"+atributo.getWebNombreParametro()+"SDF.parse("+atributo.getWebNombreParametro()+".trim());\r\n"); 
			buffer.append("\t\t}catch (java.text.ParseException pe) { \r\n");
			buffer.append("\t\t\tmensajeValidacion.setConforme(false);\r\n");
			buffer.append("\t\t\tmensajeValidacion.setParametro(\""+atributo.getWebNombre()+"\");\r\n");
			buffer.append("\t\t\tmensajeValidacion.setMensaje(\""+atributo.getWebMensajeValidacion()+"\");\r\n");
			buffer.append("\t\t\treturn mensajeValidacion;\r\n"); 
			buffer.append("\t\t}\r\n\r\n");
			
		}
		 
		return buffer.toString();
	}
	
	public static String escribirCargaObjeto(List<AtributoProceso> atributos){
		StringBuffer buffer = new StringBuffer();
		List<ClaseProceso> clases = filtrarClases(atributos);
		
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			buffer.append("\tprivate "+clase.getJavaClase()+" cargar"+clase.getJavaClase()+"(HttpServletRequest request) throws Exception{\r\n");
			buffer.append("\t\t"+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			for (int j = 0; j < atributos.size(); j++) {
				AtributoProceso atributo = atributos.get(j);
				if(atributo.getClase().getJavaClase().equals(clase.getJavaClase())){
					if(atributo.isJavaTieneValorOmision()==false){
						if(atributo.getTipo().equals("String")){
							buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(request.getParameter(\""+atributo.getWebNombre()+"\"));\r\n");
						}else if(atributo.getTipo().equals("int") || atributo.getTipo().equals("Integer")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null){\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(Integer.parseInt(request.getParameter(\""+atributo.getWebNombre()+"\")));\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equals("boolean")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && ( request.getParameter(\""+atributo.getWebNombre()+"\").equals(\"1\") || request.getParameter(\""+atributo.getWebNombre()+"\").equalsIgnoreCase(\"true\"))) {\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(true);}\r\n");
							buffer.append("\t\telse{\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(false);\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equals("java.sql.Date")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").length() > 0  ){\r\n");
							buffer.append("\t\t\ttry{\r\n");
							buffer.append("\t\t\t\tjava.text.SimpleDateFormat "+atributo.getWebNombreParametro()+"SDF = new java.text.SimpleDateFormat(\""+atributo.getWebFormato()+"\");\r\n");
							buffer.append("\t\t\t\tjava.util.Date date"+nombreVariable(atributo.getNombre())+"= "+atributo.getWebNombreParametro()+"SDF.parse(request.getParameter(\""+atributo.getWebNombre()+"\"));\r\n");
							buffer.append("\t\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(new java.sql.Date(date"+nombreVariable(atributo.getNombre())+ ".getTime()));\r\n");
							buffer.append("\t\t\t}catch (java.text.ParseException pe) { \r\n");
							buffer.append("\t\t\t\tpe.printStackTrace();\r\n");
							buffer.append("\t\t\t}\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equals("java.math.BigDecimal")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null){\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(new java.math.BigDecimal(request.getParameter(\""+atributo.getWebNombre()+"\").trim()));\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equalsIgnoreCase("long")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null){\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(Long.parseLong(request.getParameter(\""+atributo.getWebNombre()+"\")));\r\n");
							buffer.append("\t\t}\r\n");
						}
					}else{
						buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"("+atributo.getWebValorOmision()+");\r\n");
					}
				}
			}
			
			buffer.append("\t\treturn "+clase.getJavaClase().toLowerCase()+";\r\n");
			buffer.append("\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}

	public static String escribirCompletarObjeto(String nombreMetodo, List<AtributoProceso> atributos){
		StringBuffer buffer = new StringBuffer();
		List<ClaseProceso> clases = filtrarClases(atributos);
		
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			buffer.append("\tprotected "+clase.getJavaClase()+" "+nombreMetodo+clase.getJavaClase()+"("+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+", HttpServletRequest request) throws Exception{\r\n");
			
			buffer.append("\t\tif("+clase.getJavaClase().toLowerCase()+"==null){\r\n");
			buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			buffer.append("\t\t}\r\n");
			
			for (int j = 0; j < atributos.size(); j++) {
				AtributoProceso atributo = atributos.get(j);
				if(atributo.getClase().getJavaClase().equals(clase.getJavaClase())){
					if(atributo.isJavaTieneValorOmision()==false){
						if(atributo.isWebFlgReferencia()){
							if(atributo.getTipo().equals("String")){
								buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(request.getParameter(\""+atributo.getWebNombre()+"\"));\r\n");
							}else if(atributo.getTipo().equals("int") || atributo.getTipo().equals("Integer")){
								buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").trim().length()>0){\r\n");
								buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(Integer.parseInt(request.getParameter(\""+atributo.getWebNombre()+"\")));\r\n");
								buffer.append("\t\t}\r\n");
							}else if(atributo.getTipo().equals("boolean")){
								buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && ( request.getParameter(\""+atributo.getWebNombre()+"\").equals(\"1\")) || request.getParameter(\""+atributo.getWebNombre()+"\").equalsIgnoreCase(\"on\")) {\r\n");
								buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(true);}\r\n");
								buffer.append("\t\telse{\r\n");
								buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(false);\r\n");
								buffer.append("\t\t}\r\n");
							}else if(atributo.getTipo().equals("java.sql.Date")){
								buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").length() > 0 ){\r\n");
								buffer.append("\t\t\ttry{\r\n");
								buffer.append("\t\t\t\tjava.text.SimpleDateFormat "+atributo.getWebNombre()+"SDF = new java.text.SimpleDateFormat(\""+atributo.getWebFormato()+"\");\r\n");
								buffer.append("\t\t\t\tjava.util.Date date"+nombreVariable(atributo.getNombre())+"= "+atributo.getWebNombre()+"SDF.parse(request.getParameter(\""+atributo.getWebNombre()+"\"));\r\n");
								buffer.append("\t\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(new java.sql.Date(date"+nombreVariable(atributo.getNombre())+ ".getTime()));\r\n");
					//			buffer.append("\t\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"("+atributo.getWebNombre()+"SDF.parse(request.getParameter(\""+atributo.getWebNombre()+"\")));\r\n");
								buffer.append("\t\t\t}catch (java.text.ParseException pe) { \r\n");
								buffer.append("\t\t\t\tpe.printStackTrace();\r\n");
								buffer.append("\t\t\t}\r\n");
								buffer.append("\t\t}\r\n");
							}else if(atributo.getTipo().equals("java.math.BigDecimal")){
								buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").trim().length()>0){\r\n");
								buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(new java.math.BigDecimal(request.getParameter(\""+atributo.getWebNombre()+"\").trim()));\r\n");
								buffer.append("\t\t}\r\n");
							}else if(atributo.getTipo().equalsIgnoreCase("long")){
								buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").trim().length()>0){\r\n");
								buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(Long.parseLong(request.getParameter(\""+atributo.getWebNombre()+"\")));\r\n");
								buffer.append("\t\t}\r\n");
							}
						}
					}else{
						buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"("+atributo.getWebValorOmision()+");\r\n");
					}
				}
			}
			
			buffer.append("\t\treturn "+clase.getJavaClase().toLowerCase()+";\r\n");
			buffer.append("\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	public static String escribirCompletarObjetoTarea(String nombreMetodo, List<AtributoTarea> atributos){
		StringBuffer buffer = new StringBuffer();
		List<Clase> clases = filtrarClasesTarea(atributos);
		
		for (int i = 0; i < clases.size(); i++) {
			Clase clase = clases.get(i);
			buffer.append("\tpublic "+clase.getJavaClase()+" "+nombreMetodo+clase.getJavaClase()+"("+clase.getJavaClase()+" "+clase.getJavaClase().toLowerCase()+", Usuario usuarioPortal, HttpServletRequest request) throws Exception{\r\n");
			
			buffer.append("\t\tif("+clase.getJavaClase().toLowerCase()+"==null){\r\n");
			buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+" = new "+clase.getJavaClase()+"();\r\n");
			buffer.append("\t\t}\r\n");
			
			for (int j = 0; j < atributos.size(); j++) {
				AtributoTarea atributo = atributos.get(j);
				if(atributo.getClase().getJavaClase().equals(clase.getJavaClase())){
					if(atributo.isJavaTieneValorOmision()==false){
						if(atributo.getTipo().equals("String")){
							buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(request.getParameter(\""+atributo.getWebNombre()+"\"));\r\n");
						}else if(atributo.getTipo().equals("int") || atributo.getTipo().equals("Integer")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").trim().length()>0){\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(Integer.parseInt(request.getParameter(\""+atributo.getWebNombre()+"\")));\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equals("boolean")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && ( request.getParameter(\""+atributo.getWebNombre()+"\").equals(\"1\")) || request.getParameter(\""+atributo.getWebNombre()+"\").equalsIgnoreCase(\"on\")) {\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(true);}\r\n");
							buffer.append("\t\telse{\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(false);\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equals("java.sql.Date")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").length() > 0 ){\r\n");
							buffer.append("\t\t\ttry{\r\n");
							buffer.append("\t\t\t\tjava.text.SimpleDateFormat "+atributo.getWebNombreParametro()+"SDF = new java.text.SimpleDateFormat(\""+atributo.getWebFormato()+"\");\r\n");
							buffer.append("\t\t\t\tjava.util.Date date"+nombreVariable(atributo.getNombre())+"= "+atributo.getWebNombreParametro()+"SDF.parse(request.getParameter(\""+atributo.getWebNombre()+"\"));\r\n");
							buffer.append("\t\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(new java.sql.Date(date"+nombreVariable(atributo.getNombre())+ ".getTime()));\r\n");
							buffer.append("\t\t\t}catch (java.text.ParseException pe) { \r\n");
							buffer.append("\t\t\t\tpe.printStackTrace();\r\n");
							buffer.append("\t\t\t}\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equals("java.math.BigDecimal")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").trim().length()>0){\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(new java.math.BigDecimal(request.getParameter(\""+atributo.getWebNombre()+"\").trim()));\r\n");
							buffer.append("\t\t}\r\n");
						}else if(atributo.getTipo().equalsIgnoreCase("long")){
							buffer.append("\t\tif(request.getParameter(\""+atributo.getWebNombre()+"\")!=null && request.getParameter(\""+atributo.getWebNombre()+"\").trim().length()>0){\r\n");
							buffer.append("\t\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"(Long.parseLong(request.getParameter(\""+atributo.getWebNombre()+"\")));\r\n");
							buffer.append("\t\t}\r\n");
						}
					}else{
						buffer.append("\t\t"+clase.getJavaClase().toLowerCase()+".set"+nombreVariable(atributo.getNombre())+"("+atributo.getWebValorOmision()+");\r\n");
					}
				}
			}
			
			buffer.append("\t\treturn "+clase.getJavaClase().toLowerCase()+";\r\n");
			buffer.append("\t}\r\n\r\n");
		}
		
		return buffer.toString();
	}
	
	public static List<ClaseProceso> filtrarClases(List<AtributoProceso> atributos){
		Map<String, ClaseProceso> map = new HashMap<String, ClaseProceso>();
		List<ClaseProceso> clases = new ArrayList<ClaseProceso>();
		List<String> nombreClases = new ArrayList<String>();
		for (int i = 0; i < atributos.size(); i++) {
			if(map.containsKey(atributos.get(i).getClase().getJavaClase())==false){
				ClaseProceso clase = new ClaseProceso();
				clase.setJavaClase(atributos.get(i).getClase().getJavaClase());
				clase.setBaseDatosEsquema(atributos.get(i).getClase().getBaseDatosEsquema());
				clase.setBaseDatosTabla(atributos.get(i).getClase().getBaseDatosTabla());
				clase.setInformacionAutor(atributos.get(i).getClase().getInformacionAutor());
				clase.setInformacionDescripcion(atributos.get(i).getClase().getInformacionDescripcion());
				clase.setJavaPaquete(atributos.get(i).getClase().getJavaPaquete());
				//clase.setProcesoObjeto(atributos.get(i).getClase().getProcesoObjeto());
				List<AtributoProceso> atributosClase = new ArrayList<AtributoProceso>();
				atributosClase.add(atributos.get(i));
				clase.setAtributosProceso(atributosClase);
				
				map.put(clase.getJavaClase(), clase);
				nombreClases.add(clase.getJavaClase());
			}else{
				((ClaseProceso)map.get(atributos.get(i).getClase().getJavaClase())).getAtributosProceso().add(atributos.get(i));
			}
		}
		
		for (int i = 0; i < nombreClases.size(); i++) {
			clases.add(map.get(nombreClases.get(i)));
		}
		
		return clases;
	}
	
	public static List<Clase> filtrarClasesTarea(List<AtributoTarea> atributos){
		Map<String, Clase> map = new HashMap<String, Clase>();
		List<Clase> clases = new ArrayList<Clase>();
		List<String> nombreClases = new ArrayList<String>();
		for (int i = 0; i < atributos.size(); i++) {
			if(map.containsKey(atributos.get(i).getClase().getJavaClase())==false){
				Clase clase = new Clase();
				clase.setJavaClase(atributos.get(i).getClase().getJavaClase());
				clase.setBaseDatosEsquema(atributos.get(i).getClase().getBaseDatosEsquema());
				clase.setBaseDatosTabla(atributos.get(i).getClase().getBaseDatosTabla());
				clase.setInformacionAutor(atributos.get(i).getClase().getInformacionAutor());
				clase.setInformacionDescripcion(atributos.get(i).getClase().getInformacionDescripcion());
				clase.setJavaPaquete(atributos.get(i).getClase().getJavaPaquete());
				//clase.setProcesoObjeto(atributos.get(i).getClase().getProcesoObjeto());
				List<AtributoTarea> atributosClase = new ArrayList<AtributoTarea>();
				atributosClase.add(atributos.get(i));
				clase.setAtributos(atributosClase);
				
				map.put(clase.getJavaClase(), clase);
				nombreClases.add(clase.getJavaClase());
			}else{
				((Clase)map.get(atributos.get(i).getClase().getJavaClase())).getAtributos().add(atributos.get(i));
			}
		}
		
		for (int i = 0; i < nombreClases.size(); i++) {
			clases.add(map.get(nombreClases.get(i)));
		}
		
		return clases;
	}
	
	public static String nombreVariable(String cadena){
		if(cadena!=null){
			return cadena.substring(0, 1).toUpperCase() + cadena.substring(1);
		}else{
			return null;
		}
	}
	
	public static String obtenerSQLIntro(CampoSQL atributo){
		String tablaTemp = atributo.getTabla().getNombre();
		String parametro = atributo.getNombre();
		String tipoparametro = atributo.getTipo();
		int longitud = atributo.getLongitud();
		int precision = atributo.getPrecision();
		
		StringBuffer buffer = new StringBuffer();
		if(tipoparametro.equalsIgnoreCase("VARCHAR")){
			buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")");	
	    }
		if(tipoparametro.equalsIgnoreCase("CHAR")){
		    buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+")");	
		}
	    if(tipoparametro.equalsIgnoreCase("INTEGER")){
	    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro));
	    }
	    if(tipoparametro.equalsIgnoreCase("DATE")){
	    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro));
	    }
	    if(tipoparametro.equalsIgnoreCase("DECIMAL")){
	    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro)+"("+longitud+","+precision+")");
	    }
		
	    if(tipoparametro.equalsIgnoreCase("BIGINT")){
	    	buffer.append("\t\t\t\tIN PI_" + tablaTemp + "_" + Cadena.convertirMayuscula(parametro) + "\t"+  Cadena.convertirMayuscula(tipoparametro));
	    }
	    return buffer.toString();
	}
	
	public static String obtenerDaoJdbcCallAddDeclaredParameter(CampoSQL atributo) throws Exception{
		
		String parametro	=	atributo.getNombre();
		String tabla		=	atributo.getTabla().getNombre();
		String tipoparametro=	atributo.getTipo();
		StringBuffer buffer = new StringBuffer();
		if(tipoparametro.equalsIgnoreCase("VARCHAR")){
			buffer.append("\t\tjdbcCall.addDeclaredParameter(new SqlParameter(\"PI_"+tabla+"_"+ parametro + "\", Types.VARCHAR)); \r\n");	
		}else if(tipoparametro.equalsIgnoreCase("INTEGER")){
			buffer.append("\t\tjdbcCall.addDeclaredParameter(new SqlParameter(\"PI_"+tabla+"_"+ parametro + "\", Types.INTEGER));\r\n");
		}else if(tipoparametro.equalsIgnoreCase("DATE")){
			buffer.append("\t\tjdbcCall.addDeclaredParameter(new SqlParameter(\"PI_"+tabla+"_"+ parametro + "\", Types.DATE));\r\n");
		}else if(tipoparametro.equalsIgnoreCase("DECIMAL")){
			buffer.append("\t\tjdbcCall.addDeclaredParameter(new SqlParameter(\"PI_"+tabla+"_"+ parametro + "\", Types.DECIMAL));\r\n");
		}else if(tipoparametro.equalsIgnoreCase("BIGINT")){
			buffer.append("\t\tjdbcCall.addDeclaredParameter(new SqlParameter(\"PI_"+tabla+"_"+ parametro + "\", Types.BIGINT));\r\n");
		}else if(tipoparametro.equalsIgnoreCase("CHAR")){
			buffer.append("\t\tjdbcCall.addDeclaredParameter(new SqlParameter(\"PI_"+tabla+"_"+ parametro + "\", Types.CHAR)); \r\n");
		}else{
			throw new Exception("EL tipo de dato "+tipoparametro+", no esta identificado...");
		}
		return buffer.toString();
	}

	public static String obtenerDaoCallableStatement(List<AtributoProceso> atributos, Connection connection) throws Exception{
		ProcesoDaoImpl procesoDaoImpl = new ProcesoDaoImpl();
		StringBuffer buffer = new StringBuffer();
		int indice = 0;
		for (int i=0;i<atributos.size();i++){
			AtributoProceso atributo = atributos.get(i);
			
			if(atributo.getCampoSQLProceso()!=null && atributo.getCampoSQLProceso().getTabla().getNombre()!=null && atributo.getCampoSQLProceso().getTabla().getNombre().trim().length()>0){
				
				String clase 			= 	atributo.getClase().getJavaClase();
				String nombreAtributo 	= 	atributo.getNombre();
				String tipoClase 		= 	atributo.getTipo();
				
				//String tablaTemp 		= atributo.getCampoSQLProceso().getTabla().getNombre();
				//String parametro 		= atributo.getCampoSQLProceso().getNombre();
				indice++; 
				
				if(atributo.getCampoSQLProceso().isFlgTieneFK()){
					AtributoProceso atributoReferencia = procesoDaoImpl.obtenerAtributo(atributo.getCampoSQLProceso().getFk().getCodigo(), connection);
					clase 			= 	atributoReferencia.getClase().getJavaClase();
					nombreAtributo 	= 	atributoReferencia.getNombre();
				}
				
				if("java.math.BigDecimal".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\tcstmt.setBigDecimal("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\tcstmt.setDate("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Timestamp".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\tcstmt.setTimestamp("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
					throw new Exception("El tipo de dato java.sql.Date no es un tipo valido, emplee java.sql.Date");
				}else if("boolean".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+clase.toLowerCase()+".is"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("Integer".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\tcstmt.setInt("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else{
					buffer.append("\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}
				
			}
		}
		return buffer.toString();
	}
	
	public static String obtenerDaoCallableStatementTarea(Clase clasePadre, List<AtributoTarea> atributos) throws Exception{
		StringBuffer buffer = new StringBuffer();
		int indice = 0;
		for (int i=0;i<atributos.size();i++){
			AtributoTarea atributo = atributos.get(i);
			
			if(atributo.getCampoSQLTarea()!=null && atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
				
				String clase 			= 	atributo.getClase().getJavaClase();
				String nombreAtributo 	= 	atributo.getNombre();
				String tipoClase 		= 	atributo.getTipo();
				indice++;
				
				//menejo de referencia PK
				if(atributo.getCampoSQLTarea().isFlgTieneFK()){
					AtributoTarea atributoReferencia = atributo.getCampoSQLTarea().getFk().getAtributoTarea();
					clase 			= 	atributoReferencia.getClase().getJavaClase();
					nombreAtributo 	= 	atributoReferencia.getNombre();
				}
				
				String preFijo = null;
				if(clase.equalsIgnoreCase(clasePadre.getJavaClase())){
					preFijo = clase.toLowerCase()+".get";
				}else{
					preFijo = clasePadre.getJavaClase().toLowerCase()+".get"+clase+"().get";
				}
				
				if("java.math.BigDecimal".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setBigDecimal("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setDate("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Timestamp".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setTimestamp("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
					throw new Exception("El tipo de dato java.sql.Date no es un tipo valido, emplee java.sql.Date");
				}else if("boolean".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+clase.toLowerCase()+".is"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("Integer".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setInt("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else{
					buffer.append("\t\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}
					
					
				
			}
		}
		return buffer.toString();
	}
	
	public static String obtenerDaoCallableStatementTareaConObjetoPadre(Clase clasePadre, List<AtributoTarea> atributos) throws Exception{
		StringBuffer buffer = new StringBuffer();
		int indice = 0;
		for (int i=0;i<atributos.size();i++){
			AtributoTarea atributo = atributos.get(i);
			
			if(atributo.getCampoSQLTarea()!=null && 
					atributo.getCampoSQLTarea().getTabla().getNombre()!=null && 
					atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0 && 
					atributo.getCampoSQLTarea().isTieneFuncion()==false){
				
				String clase 			= 	atributo.getClase().getJavaClase();
				String nombreAtributo 	= 	atributo.getNombre();
				String tipoClase 		= 	atributo.getTipo();
				indice++;
				
				//menejo de referencia PK
				if(atributo.getCampoSQLTarea().isFlgTieneFK()){
					AtributoTarea atributoReferencia = atributo.getCampoSQLTarea().getFk().getAtributoTarea();
					clase 			= 	atributoReferencia.getClase().getJavaClase();
					nombreAtributo 	= 	atributoReferencia.getNombre();
				}
				
				String preFijo = null;
				if(clase.equalsIgnoreCase(clasePadre.getJavaClase())){
					preFijo = clase.toLowerCase()+".get";
				}else{
					preFijo = clasePadre.getJavaClase().toLowerCase()+".get"+clase+"().get";
				}
				if("java.math.BigDecimal".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setBigDecimal("+indice+", "+ preFijo + nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setDate("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Timestamp".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setTimestamp("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
					throw new Exception("El tipo de dato java.sql.Date no es un tipo valido, emplee java.sql.Date");
				}else if("boolean".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+clasePadre.getJavaClase().toLowerCase()+".get"+clase+"().is"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else if("Integer".equalsIgnoreCase(tipoClase)){
					buffer.append("\t\t\tcstmt.setInt("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}else{
					buffer.append("\t\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+preFijo+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
				}
					
					
				
			}
		}
		return buffer.toString();
	}
	
	public static String obtenerDaoCallableStatementReferenciaFK(List<AtributoTarea> atributos) throws Exception{
		StringBuffer buffer = new StringBuffer();
		int indice = 0;
		for (int i=0;i<atributos.size();i++){
			AtributoTarea atributo = atributos.get(i).getCampoSQLTarea().getFk().getAtributoTarea();
			if(atributo.isFlgLista()==false){
				if(atributo.getCampoSQLTarea().getTabla().getNombre()!=null && atributo.getCampoSQLTarea().getTabla().getNombre().trim().length()>0){
					String clase 			= 	atributo.getClase().getJavaClase();
					String nombreAtributo 	= 	atributo.getNombre();
					String tipoClase 		= 	atributo.getTipo();
					indice++;
					if("java.math.BigDecimal".equalsIgnoreCase(tipoClase)){
						buffer.append("\t\tcstmt.setBigDecimal("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
					}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
						buffer.append("\t\tcstmt.setDate("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
					}else if("java.sql.Timestamp".equalsIgnoreCase(tipoClase)){
						buffer.append("\t\tcstmt.setTimestamp("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
					}else if("java.sql.Date".equalsIgnoreCase(tipoClase)){
						throw new Exception("El tipo de dato java.sql.Date no es un tipo valido, emplee java.sql.Date");
					}else if("boolean".equalsIgnoreCase(tipoClase)){
						buffer.append("\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+clase.toLowerCase()+".is"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
					}else{
						buffer.append("\t\tcstmt.set"+tipoClase.toUpperCase().charAt(0)+tipoClase.substring(1)+"("+indice+", "+clase.toLowerCase()+".get"+ nombreAtributo.toUpperCase().charAt(0)+ nombreAtributo.substring(1)  +"());\r\n");
					}
				}
			}
		}
		return buffer.toString();
	}
	/*
	public static List<AtributoTarea> obtenerPKs(AtributoTarea atributoBase){
		List<Atributo> atributoPKs = new ArrayList<Atributo>();
		for (int x = 0; x < CargaAtributoExcel.atributoList.size(); x++) {
			AtributoTarea atributo = CargaAtributoExcel.atributoList.get(x);
			if(atributo.isBaseDatosPK() && atributoBase.getBaseDatosTabla().equalsIgnoreCase(atributo.getBaseDatosTabla())){
				atributoPKs.add(clonar(atributo));
			}
		}
		return atributoPKs;
	}
	*/
	/*
	public static boolean isCampoPresentacion(Atributo atributoBusqueda, Tabla tablaResumen){
		boolean encontrado = false;
		for(Atributo atributoLista : tablaResumen.getAtributos() )
		{
			if(atributoLista.getInformacionCodigo().equals(atributoBusqueda.getInformacionCodigo()))
			{
				encontrado = true;
				break;
			}
		}
		if(!encontrado)
		{
			for(Tabla tabla : tablaResumen.getTablasFK() )
			{
				for(Atributo atributoLista : tabla.getAtributos())
				{
					if(atributoLista.getInformacionCodigo().equals(atributoBusqueda.getInformacionCodigo()))
					{
						encontrado = true;
						break;
					}	
				}
			}
		}
		
		
		return encontrado;
	}
	*/
	public static String obtenerClaseHijaPadre(Clase padre, Clase clase){
		if(clase.getJavaClase().equalsIgnoreCase(padre.getJavaClase())==false){
			return padre.getJavaClase().toLowerCase()+".get"+clase.getJavaClase()+"()"; 
		}else{
			return clase.getJavaClase().toLowerCase();
		}
	}
	
	public static String ponerClaseHijaPadre(Clase padre, Clase clase){
		if(clase.getJavaClase().equalsIgnoreCase(padre.getJavaClase())==false){
			return padre.getJavaClase().toLowerCase()+".set"+clase.getJavaClase(); 
		}else{
			return clase.getJavaClase().toLowerCase();
		}
	}

	//public static Atributo obtenerAtributo
	
}
