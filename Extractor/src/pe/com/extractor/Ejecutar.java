package pe.com.extractor;

import pe.com.extractor.service.ExtractorService;

public class Ejecutar {

	public static void main(String[] args) {
		ExtractorService extractorService = new ExtractorService();
		extractorService.escribirRegistros("BFP_CARTA_FIANZA");
	}
	
}
