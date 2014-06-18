package com.furb.utils;

import com.furb.regiao.EnAlgoritimo;

public enum EnPVMResult {
	Sucesso(1),
	Erro(2);
	
	private int codigo;
	
	EnPVMResult(int cod){
		this.codigo = cod;
	}
	
	public int GetCodigo(){
		return this.codigo;
	}	
	
	public static EnPVMResult ObterPorCodigo(int cod){
		EnPVMResult result = null;
		for (int i = 0; i < EnAlgoritimo.values().length; i++) {
			if(EnPVMResult.values()[i].GetCodigo() == cod){
				result = EnPVMResult.values()[i];
				break;
			}
		}
		
		return result;
	}
}
