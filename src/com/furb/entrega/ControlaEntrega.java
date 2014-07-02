package com.furb.entrega;

/* hello.java
 *
 * A simple test of jpvm message passing.
 *
 * Adam J Ferrari
 * Sun 05-26-1996
 *
 * Copyright (C) 1996  Adam J Ferrari
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 675 Mass Ave, Cambridge,
 * MA 02139, USA.
 */

import java.util.List;

import jpvm.jpvmBuffer;
import jpvm.jpvmEnvironment;
import jpvm.jpvmException;
import jpvm.jpvmMessage;
import jpvm.jpvmTaskId;

import com.furb.utils.SerializationUtils;
import com.furb.veiculo.Veiculo;

public class ControlaEntrega {
	// private static int num_workers = 3;

	private List<Veiculo> listaVeiculos;

	public ControlaEntrega(List<Veiculo> listaVeiculos) {
		this.listaVeiculos = listaVeiculos;
	}

	public void executaEscravos() {

		try {
			// inicia o pvm...
			jpvmEnvironment jpvm = new jpvmEnvironment();

			// pega o meu id...
			jpvmTaskId mytid = jpvm.pvm_mytid();
			System.out.println("Task Id: " + mytid.toString());

			// distribui o trabalho...
			int qtdEscravos = this.getListaVeiculos().size();
			jpvmTaskId tids[] = new jpvmTaskId[qtdEscravos];
			jpvm.pvm_spawn(Entrega.class.getName(), qtdEscravos, tids);

			for (int i = 0; i < qtdEscravos; i++) {

				jpvmBuffer buf = new jpvmBuffer();

				byte[] arrayByte = SerializationUtils.ObjectToByteArray(this.getListaVeiculos().get(i));
				int[] tags = new int[3];
				// tags[0] = EnAlgoritimo.GetByIndex(i).GetCodigo();
				// tags[1] = numRegioes;
				 tags[2] = arrayByte.length;

				buf.pack(arrayByte, arrayByte.length, 1);
				jpvm.pvm_send(buf, tids[i], tags);
			}

			// recebe uma mensagem de cada processo...
			for (int i = 0; i < qtdEscravos; i++) {
				// recebe uma mensagem...
				jpvmMessage message = jpvm.pvm_recv();
				System.out.println("\nGot message tag " + message.messageTag + " from " + message.sourceTid.toString());

				// desempacota a mensagem...
				String str = message.buffer.upkstr();

				System.out.println("Received: " + str);
			}

			// sai da m�quina virtual...
			jpvm.pvm_exit();
		} catch (jpvmException jpe) {
			System.out.println("Error - jpvm exception");
		}

	}

	// public static void main(String args[]) {
	// try {
	//
	// System.out.println("Worker tasks: ");
	// int i;
	// for (i = 0; i < num_workers; i++) {
	// System.out.println("\t" + tids[i].toString());
	//
	// jpvmBuffer buf = new jpvmBuffer();
	// // byte[] arrayByte = RegiaoUtil.PedidosToArrayByte(pedidos);
	// // byte[] arrayByte =
	// // SerializationUtils.ObjectArrayToByteArray(pedidos);
	// // int asdf = arrayByte.length;
	// // buf.pack(arrayByte, arrayByte.length, 1);
	// //
	// int[] tags = new int[0];
	// // tags[0] = EnAlgoritimo.GetByIndex(i).GetCodigo();
	// // tags[1] = numRegioes;
	// // tags[2] = arrayByte.length;
	//
	// buf.pack("OLÁ MUNDO!");
	// jpvm.pvm_send(buf, tids[i], tags);
	// }
	//
	// // recebe uma mensagem de cada processo...
	// for (i = 0; i < num_workers; i++) {
	// // recebe uma mensagem...
	// jpvmMessage message = jpvm.pvm_recv();
	// System.out.println("\nGot message tag " + message.messageTag + " from " +
	// message.sourceTid.toString());
	//
	// // desempacota a mensagem...
	// String str = message.buffer.upkstr();
	//
	// System.out.println("Received: " + str);
	// }
	//
	// // sai da m�quina virtual...
	// jpvm.pvm_exit();
	// } catch (jpvmException jpe) {
	// System.out.println("Error - jpvm exception");
	// }
	// }

	public List<Veiculo> getListaVeiculos() {
		return listaVeiculos;
	}
};
