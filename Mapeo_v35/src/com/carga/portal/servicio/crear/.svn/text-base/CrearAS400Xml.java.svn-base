package com.carga.portal.servicio.crear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.List;

import com.carga.mapeo.dao.impl.AS400DaoImpl;
import com.carga.portal.modelo.AS400In;
import com.carga.portal.modelo.AS400InLista;
import com.carga.portal.modelo.AS400Out;
import com.carga.portal.modelo.AS400OutLista;
import com.carga.portal.modelo.AS400Programa;
import com.carga.portal.modelo.Configuracion;
import com.carga.portal.modelo.Proyecto;

public class CrearAS400Xml {

	public static final String SP_CARACTER_CIERRE = "@";
	
	public void crear(Proyecto proyecto, Configuracion configuracion, Connection conn) throws Exception{
		
		/**************************************************************************************************************/
		/** creamos los directorios */
		File directorioXML = new File(configuracion.getDirectorioSQL().getAbsolutePath()+"\\AS400\\xml");
		if(directorioXML.exists()==false){
			directorioXML.mkdirs();
		}
		List<AS400Programa> as400Programas = new AS400DaoImpl().obtenerAS400Programa(proyecto, conn);
		for (int x = 0; x < as400Programas.size(); x++) {
			AS400Programa as400programa = as400Programas.get(x);
			String archivoXML1 = directorioXML.getAbsolutePath()+"\\";
			String archivoXML2 = as400programa.getCodigoPrograma().trim()+".xml";
			
			File archivo = new File(archivoXML1,archivoXML2);
			if(archivo.exists()){
				archivo.delete();
			}
			archivo.createNewFile();
			
			BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo,true), "ISO-8859-1"));
			bufferedWriter.write(contenidoXML(as400programa));
			bufferedWriter.close();
		}
		
	}
	
	public static String contenidoXML(AS400Programa as400programa) throws Exception{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		buffer.append("<program xmlns=\"http://www.financiero.com.pe/programs\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n");
		buffer.append("\txsi:schemaLocation=\"http://www.financiero.com.pe/programs ../bindings/as400program_binding.xsd \">)\r\n");
		buffer.append("\t<name>"+as400programa.getCodigoPrograma().trim()+"</name>\r\n");
		buffer.append("\t<description>"+as400programa.getNombre().trim()+"</description>\r\n");
		buffer.append("\t<input plenLength=\"5\" pdataLength=\"4096\">\r\n");
		buffer.append("\t\t<pdata>\r\n");
		buffer.append("\t\t\t<head length=\"82\">\r\n");
		buffer.append("\t\t\t\t<parameters>\r\n");
		
		buffer.append("\t\t\t\t\t<parameter name=\"IHPLAZA\" startIndex=\"1\" length=\"3\" javaType=\"java.lang.Integer\" defaultValue=\"320\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHLOGTX\" startIndex=\"4\" length=\"5\" javaType=\"java.lang.Integer\" defaultValue=\"00000\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHAGUSR\" startIndex=\"9\" length=\"3\" javaType=\"java.lang.Integer\" defaultValue=\"022\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHSUCUR\" startIndex=\"12\" length=\"2\" javaType=\"java.lang.Integer\" defaultValue=\"22\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHUNOPC\" startIndex=\"14\" length=\"3\" javaType=\"java.lang.Integer\" defaultValue=\"622\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHESTAC\" startIndex=\"17\" length=\"10\" javaType=\"java.lang.String\" defaultValue=\"BPM\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHTRANS\" startIndex=\"27\" length=\"4\" javaType=\"java.lang.Integer\" defaultValue=\""+as400programa.getTransaccion().trim()+"\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHHORA\" startIndex=\"31\" length=\"6\" javaType=\"java.util.Date\" dateFormat=\"HHmmss\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHUSUAR\" startIndex=\"37\" length=\"10\" javaType=\"java.lang.String\" defaultValue=\""+as400programa.getUsuario().trim()+"\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHSUPER\" startIndex=\"47\" length=\"10\" javaType=\"java.lang.String\" defaultValue=\" \" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHLOGAP\" startIndex=\"57\" length=\"5\" javaType=\"java.lang.Integer\" defaultValue=\"00000\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHVALID\" startIndex=\"62\" length=\"1\" javaType=\"java.lang.Integer\" defaultValue=\" \" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHFLAG1\" startIndex=\"63\" length=\"1\" javaType=\"java.lang.Integer\" defaultValue=\"0\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHFLAG2\" startIndex=\"64\" length=\"1\" javaType=\"java.lang.Integer\" defaultValue=\"2\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHFLAG3\" startIndex=\"65\" length=\"1\" javaType=\"java.lang.Integer\" defaultValue=\"0\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHFLAG4\" startIndex=\"66\" length=\"1\" javaType=\"java.lang.Integer\" defaultValue=\"0\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHFLAG5\" startIndex=\"67\" length=\"1\" javaType=\"java.lang.Integer\" defaultValue=\"0\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHTPFMT\" startIndex=\"68\" length=\"1\" javaType=\"java.lang.String\" defaultValue=\"A\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHFECHA\" startIndex=\"69\" length=\"6\" javaType=\"java.util.Date\" dateFormat=\"ddMMyy\" />\r\n");
		buffer.append("\t\t\t\t\t<parameter name=\"IHPROGR\" startIndex=\"75\" length=\"8\" javaType=\"java.lang.String\" defaultValue=\""+as400programa.getCodigoPrograma().trim()+"\" />\r\n");
		buffer.append("\t\t\t\t</parameters>\r\n");
		buffer.append("\t\t\t</head>\r\n");
		buffer.append("\t\t\t<data length=\"4014\">\r\n");
		buffer.append("\t\t\t\t<parameters>\r\n");
		
		List<AS400In> as400ins = as400programa.getAs400ins();
		for (int y = 0; y < as400ins.size(); y++) {
			AS400In as400in = as400ins.get(y);
			
			String defaulValue = "";
			if(as400in.getValorDefecto()!=null){
				defaulValue = "defaultValue=\""+as400in.getValorDefecto()+"\" ";
			}
			
			buffer.append("\t\t\t\t\t<!-- "+as400in.getDescripcion()+" -->\r\n");
			if(as400in.getTipoDato().equalsIgnoreCase("java.lang.String")){
				buffer.append("\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.lang.String\" "+defaulValue+"/>\r\n");
			}else if(as400in.getTipoDato().equalsIgnoreCase("java.math.BigDecimal")){
				buffer.append("\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.math.BigDecimal\" scale=\""+as400in.getEscala()+"\" "+defaulValue+"/>\r\n");
			}else if(as400in.getTipoDato().equalsIgnoreCase("java.util.Date")){
				buffer.append("\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.util.Date\" dateFormat=\""+as400in.getFormatoDate()+"\" "+defaulValue+"/>\r\n");
			}else if(as400in.getTipoDato().equalsIgnoreCase("java.lang.Integer")){
				buffer.append("\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.lang.Integer\" "+defaulValue+"/>\r\n");
			}else{
				throw new Exception("El tipo de dato "+as400in.getTipoDato()+" del atributo de entrada "+as400in.getCodigoAtributo()+", no es valido");
			}
		}
		
		
		List<AS400InLista> as400inListas = as400programa.getAs400inLista();
		for (int x = 0; x < as400inListas.size(); x++) {
			AS400InLista as400inLista = as400inListas.get(x);
			
			buffer.append("\t\t\t\t\t<parameterSection name=\""+as400inLista.getNombre()+"\" times=\""+as400inLista.getTimes()+"\" startIndex=\""+as400inLista.getInicio()+"\" length=\""+as400inLista.getLongitud()+"\">\r\n");
            
			as400ins = as400inLista.getAs400ins();
			for (int y = 0; y < as400ins.size(); y++) {
				AS400In as400in = as400ins.get(y);
				
				String defaulValue = "";
				if(as400in.getValorDefecto()!=null && as400in.getValorDefecto().length()>0){
					defaulValue = "defaultValue=\""+as400in.getValorDefecto()+"\" ";
				}
				
				buffer.append("\t\t\t\t\t\t<!-- "+as400in.getDescripcion()+" -->\r\n");
				if(as400in.getTipoDato().equalsIgnoreCase("java.lang.String")){
					buffer.append("\t\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.lang.String\" "+defaulValue+"/>\r\n");
				}else if(as400in.getTipoDato().equalsIgnoreCase("java.math.BigDecimal")){
					buffer.append("\t\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.math.BigDecimal\" scale=\""+as400in.getEscala()+"\" "+defaulValue+"/>\r\n");
				}else if(as400in.getTipoDato().equalsIgnoreCase("java.util.Date")){
					buffer.append("\t\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.util.Date\" dateFormat=\""+as400in.getFormatoDate()+"\" "+defaulValue+"/>\r\n");
				}else if(as400in.getTipoDato().equalsIgnoreCase("java.lang.Integer")){
					buffer.append("\t\t\t\t\t\t<parameter name=\""+as400in.getCodigoAtributo().trim()+"\" startIndex=\""+as400in.getInicio()+"\" length=\""+as400in.getLongitud()+"\" javaType=\"java.lang.Integer\" "+defaulValue+"/>\r\n");
				}else{
					throw new Exception("El tipo de dato "+as400in.getTipoDato()+" del atributo de entrada "+as400in.getCodigoAtributo()+", no es valido");
				}
			}
			
            buffer.append("\t\t\t\t\t</parameterSection>\r\n");
			
		}
		
	    buffer.append("\t\t\t\t</parameters>\r\n");
	    buffer.append("\t\t\t</data>\r\n");
	    buffer.append("\t\t</pdata>\r\n");
	    buffer.append("\t</input>\r\n");
	    

		/**
		 * ====================================================================================================================
		 */
		
	    buffer.append("\t<output plenLength=\"5\" pdataLength=\"4096\">\r\n");
	    buffer.append("\t\t<pdata>\r\n");
	    buffer.append("\t\t\t<head length=\"36\">\r\n");
	    buffer.append("\t\t\t\t<parameters>\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHFMTAP\" startIndex=\"1\" length=\"1\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHSTATU\" startIndex=\"2\" length=\"1\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHLOGTX\" startIndex=\"3\" length=\"5\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHESTAC\" startIndex=\"8\" length=\"10\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHCOMSG\" startIndex=\"18\" length=\"4\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHLOGAP\" startIndex=\"22\" length=\"5\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHHORA\" startIndex=\"27\" length=\"6\" javaType=\"java.util.Date\" dateFormat=\"HHmmss\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHLCDPR\" startIndex=\"33\" length=\"1\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHFMTPR\" startIndex=\"34\" length=\"1\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t\t<parameter name=\"OHFINFM\" startIndex=\"35\" length=\"2\" javaType=\"java.lang.String\" />\r\n");
	    buffer.append("\t\t\t\t</parameters>\r\n");
	    buffer.append("\t\t\t</head>\r\n");
	    buffer.append("\t\t\t<data length=\"4060\">\r\n");
	    buffer.append("\t\t\t\t<parameters>\r\n");
	    
	    if(true){
		    List<AS400Out> as400outs = as400programa.getAs400outs();
			for (int y = 0; y < as400outs.size(); y++) {
				AS400Out as400out = as400outs.get(y);
				
				String defaulValue = "";
				if(as400out.getValorDefecto()!=null && as400out.getValorDefecto().length()>0){
					defaulValue = "defaultValue=\""+as400out.getValorDefecto()+"\" ";
				}
					
				buffer.append("\t\t\t\t\t<!-- "+as400out.getDescripcion()+" -->\r\n");
				if(as400out.getTipoDato().equalsIgnoreCase("java.lang.String")){
					buffer.append("\t\t\t\t\t<parameter name=\""+as400out.getCodigoAtributo().trim()+"\" startIndex=\""+as400out.getInicio()+"\" length=\""+as400out.getLongitud()+"\" javaType=\"java.lang.String\" "+defaulValue+"/>\r\n");
				}else if(as400out.getTipoDato().equalsIgnoreCase("java.math.BigDecimal")){
					buffer.append("\t\t\t\t\t<parameter name=\""+as400out.getCodigoAtributo().trim()+"\" startIndex=\""+as400out.getInicio()+"\" length=\""+as400out.getLongitud()+"\" javaType=\"java.math.BigDecimal\" scale=\""+as400out.getEscala()+"\" "+defaulValue+"/>\r\n");
				}else if(as400out.getTipoDato().equalsIgnoreCase("java.util.Date")){
					buffer.append("\t\t\t\t\t<parameter name=\""+as400out.getCodigoAtributo().trim()+"\" startIndex=\""+as400out.getInicio()+"\" length=\""+as400out.getLongitud()+"\" javaType=\"java.util.Date\" dateFormat=\""+as400out.getFormatoDate()+"\" "+defaulValue+"/>\r\n");
				}else if(as400out.getTipoDato().equalsIgnoreCase("java.lang.Integer")){
					buffer.append("\t\t\t\t\t<parameter name=\""+as400out.getCodigoAtributo().trim()+"\" startIndex=\""+as400out.getInicio()+"\" length=\""+as400out.getLongitud()+"\" javaType=\"java.lang.Integer\" "+defaulValue+"/>\r\n");
				}else if (as400out.getTipoDato().equalsIgnoreCase("java.util.List")){
					if (as400out.getLongitudLista() != null && !"".equals(as400out.getLongitudLista())){
						List<AS400OutLista> as400outListas = as400programa.getAs400outLista();
						for (int x = 0; x < as400outListas.size(); x++) {
							AS400OutLista as400outLista = as400outListas.get(x);
							
							buffer.append("\t\t\t\t\t<parameterSection name=\""+as400out.getCodigoAtributo()+"\" times=\""+as400outLista.getTimes()+"\" startIndex=\""+as400out.getInicio()+"\" length=\""+as400outLista.getLongitud()+"\">\r\n");
				            
							List<AS400Out> as400outsList = as400outLista.getAs400outs();
							for (int z = 0; z < as400outsList.size(); z++) {
								AS400Out as400out2 = as400outsList.get(z);
								
								defaulValue = "";
								if(as400out2.getValorDefecto()!=null && as400out2.getValorDefecto().length()>0){
									defaulValue = "defaultValue=\""+as400out2.getValorDefecto()+"\" ";
								}
								
								buffer.append("\t\t\t\t\t\t<!-- "+as400out2.getDescripcion()+" -->\r\n");
								if(as400out2.getTipoDato().equalsIgnoreCase("java.lang.String")){
									buffer.append("\t\t\t\t\t\t<parameter name=\""+as400out2.getCodigoAtributo().trim()+"\" startIndex=\""+as400out2.getInicio()+"\" length=\""+as400out2.getLongitud()+"\" javaType=\"java.lang.String\" "+defaulValue+"/>\r\n");
								}else if(as400out2.getTipoDato().equalsIgnoreCase("java.math.BigDecimal")){
									buffer.append("\t\t\t\t\t\t<parameter name=\""+as400out2.getCodigoAtributo().trim()+"\" startIndex=\""+as400out2.getInicio()+"\" length=\""+as400out2.getLongitud()+"\" javaType=\"java.math.BigDecimal\" scale=\""+as400out2.getEscala()+"\" "+defaulValue+"/>\r\n");
								}else if(as400out2.getTipoDato().equalsIgnoreCase("java.util.Date")){
									buffer.append("\t\t\t\t\t\t<parameter name=\""+as400out2.getCodigoAtributo().trim()+"\" startIndex=\""+as400out2.getInicio()+"\" length=\""+as400out2.getLongitud()+"\" javaType=\"java.util.Date\" dateFormat=\""+as400out2.getFormatoDate()+"\" "+defaulValue+"/>\r\n");
								}else if(as400out.getTipoDato().equalsIgnoreCase("java.lang.Integer")){
									buffer.append("\t\t\t\t\t\t<parameter name=\""+as400out2.getCodigoAtributo().trim()+"\" startIndex=\""+as400out2.getInicio()+"\" length=\""+as400out2.getLongitud()+"\" javaType=\"java.lang.Integer\" "+defaulValue+"/>\r\n");
								}else{
									throw new Exception("El tipo de dato "+as400out2.getTipoDato()+" del atributo de entrada "+as400out2.getCodigoAtributo()+", no es valido");
								}
							}
							
				            buffer.append("\t\t\t\t\t</parameterSection>\r\n");
							
						}
					}
				}
				else{
					throw new Exception("El tipo de dato "+as400out.getTipoDato()+" del atributo de entrada "+as400out.getCodigoAtributo()+", no es valido");
				}
			}
	    }
		
	    buffer.append("\t\t\t\t</parameters>\r\n");
	    buffer.append("\t\t\t</data>\r\n");
	    buffer.append("\t\t</pdata>\r\n");
	    buffer.append("\t</output>\r\n");
	    buffer.append("</program>\r\n");
	    
	    
		return buffer.toString();
	}
	
}