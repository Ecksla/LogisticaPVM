package com.furb.entrega;

import jpvm.jpvmBuffer;
import jpvm.jpvmEnvironment;
import jpvm.jpvmException;
import jpvm.jpvmMessage;
import jpvm.jpvmTaskId;

import com.furb.pedido.Pedido;
import com.furb.produto.Produto;
import com.furb.utils.SerializationUtils;
import com.furb.veiculo.Veiculo;

public class Entrega {
	static int num_workers = 1;

	public static void main(String args[]) {
		try {
			// inicia o jpvm...
			jpvmEnvironment jpvm = new jpvmEnvironment();

			// pega o id do meu pai...
			jpvmTaskId parent = jpvm.pvm_parent();

			jpvmMessage message = jpvm.pvm_recv();
			int[] tags = message.messageTagArray;
			byte[] byteArray = new byte[tags[2]];
			message.buffer.unpack(byteArray, tags[2], 1);

			// envia mensagem para meu pai...
			jpvmBuffer buf = new jpvmBuffer();
			// buf.pack("Hello from jpvm task, id: " +
			// jpvm.pvm_mytid().toString());

			String dadosEntrega = efetuarEntrega((Veiculo) SerializationUtils.ByteArrayToObject(byteArray));
			buf.pack(dadosEntrega);

			jpvm.pvm_send(buf, parent, 12345);

			// sai do jpvm
			jpvm.pvm_exit();
		} catch (jpvmException jpe) {
			System.out.println("Error - jpvm exception");
		}
	}

	public static String efetuarEntrega(Veiculo veiculo) {
		StringBuilder sb = new StringBuilder();

		sb.append(addQuebraLinha("\nREALIZANDO ENTREGA DOS PEDIDO DO VEÍCULO: " + veiculo.getPlaca()));
		int qtdPedidos = veiculo.getListaPedidos().size();
		for (Pedido pedido : veiculo.getListaPedidos()) {
			sb.append(addQuebraLinha("PEDIDO: " + pedido.getCodigo()));
			for (Produto produto : pedido.getListaProdutos()) {
				sb.append(addQuebraLinha("EFETUANDO ENTREGA"));
				sb.append(addQuebraLinha("PRODUTO: " + produto.getCodigo() + " - " + produto.getDescricao()));
				sb.append(addQuebraLinha("ENTREGA EFETUADA"));
			}
		}
		sb.append(addQuebraLinha("QUANTIDADE DE PEDIDOS ENTREGUES: " + qtdPedidos));

		return sb.toString();
	}

	private static String addQuebraLinha(String str) {
		return str + "\n";
	}
}
