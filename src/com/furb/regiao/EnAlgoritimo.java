package com.furb.regiao;

public enum EnAlgoritimo {
	KMeans(1),
	Weka(2);
	
	private int codigo;
	
	EnAlgoritimo(int cod){
		this.codigo = cod;
	}
	
	public int GetCodigo(){
		return this.codigo;
	}	
	
	public static EnAlgoritimo GetByIndex(int index){
		switch (index) {
		case 0:
			return KMeans;
		case 1:
			return Weka;
		default:
			return KMeans;
		}
	}
	
	public static EnAlgoritimo ObterPorCodigo(int cod){
		EnAlgoritimo result = null;
		for (int i = 0; i < EnAlgoritimo.values().length; i++) {
			if(EnAlgoritimo.values()[1].GetCodigo() == cod){
				result = EnAlgoritimo.values()[1];
				break;
			}
		}
		
		return result;
	}
}
