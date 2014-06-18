package com.furb.frota;

import jpvm.jpvmBuffer;
import jpvm.jpvmEnvironment;
import jpvm.jpvmException;
import jpvm.jpvmMessage;
import jpvm.jpvmTaskId;

import com.furb.regiao.Regiao;
import com.furb.utils.EnPVMResult;
import com.furb.utils.Mock;
import com.furb.utils.SerializationUtils;

public class CalcularFrota_Master {
	private static Mock mock;
	
	public CalcularFrota_Master(){
		mock = new Mock();
	}
	
	public int CalcularFrota() {

		Regiao[] regioes = mock.GetRegioes();

		int numDiasUteis = 6;
		int periodoAtendimento = 2;

		int numFrota = 0;
		int num_workers = regioes.length;
		
		try {
			// inicia o pvm...
			jpvmEnvironment jpvm = new jpvmEnvironment();

			// pega o meu id...
			jpvmTaskId mytid = jpvm.pvm_mytid();
			System.out.println("Task Id: " + mytid.toString());

			// distribui o trabalho...
			jpvmTaskId tids[] = new jpvmTaskId[num_workers];
			jpvm.pvm_spawn("com.furb.frota.CalcularFrota_Slave", num_workers, tids);
			
			
			for (int i = 0; i < num_workers; i++) {
				jpvmBuffer buf = new jpvmBuffer();
				
				byte[] regiaoSerializada = SerializationUtils.ObjectToByteArray(regioes[i]);
				
				buf.pack(regiaoSerializada, regiaoSerializada.length, 1);

				int[] tags = new int[3];
				tags[0] = numDiasUteis;
				tags[1] = periodoAtendimento;
				tags[2] = regiaoSerializada.length;
				
				jpvm.pvm_send(buf, tids[i], tags);
			}
			
			for (int i=0;i<num_workers; i++) {
				// recebe uma mensagem...
				jpvmMessage message = jpvm.pvm_recv();
				EnPVMResult status = EnPVMResult.ObterPorCodigo(message.messageTag);
				
				if(status == EnPVMResult.Sucesso){
					// desempacota a mensagem...
					int frotaParcial = message.buffer.upkint();
	
					numFrota += frotaParcial;
				}
			}			
		} catch (jpvmException jpe) {
			System.out.println("Error - jpvm exception");
		}
		
		return numFrota;
	}
}
