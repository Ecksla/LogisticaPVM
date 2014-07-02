package com.furb.frete;

import com.furb.pedido.Pedido;
import com.furb.produto.Produto;
import com.furb.utils.Mock;

public class CalcularFrete {
	public static float CalcularFrete(Pedido pedido){
		float result = 0;
			
		for (Produto produto : pedido.getListaProdutos()) {
			result += ((produto.getAltura() * produto.getLargura()) * 0.01) + (produto.getPeso() * 0.05);
		}	
		
		return result;
	}
}
