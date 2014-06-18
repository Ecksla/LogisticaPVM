package com.furb.frota;

import com.furb.regiao.Regiao;
import com.furb.utils.EnPVMResult;
import com.furb.utils.SerializationUtils;

import jpvm.jpvmBuffer;
import jpvm.jpvmEnvironment;
import jpvm.jpvmException;
import jpvm.jpvmMessage;
import jpvm.jpvmTaskId;

public class CalcularFrota_Slave {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// inicia o jpvm...
			jpvmEnvironment jpvm = new jpvmEnvironment();

			// pega o id do meu pai...
			jpvmTaskId parent = jpvm.pvm_parent();

			jpvmMessage message = jpvm.pvm_recv();
			try {
				int[] tags = message.messageTagArray;

				byte[] regiaoSerializada = new byte[tags[2]];

				message.buffer.unpack(regiaoSerializada, tags[2], 1);

				Regiao regiao = (Regiao) SerializationUtils
						.ByteArrayToObject(regiaoSerializada);

				int result = regiao
						.CalcularNumFrotaNecessaria(tags[0], tags[1]);

				// envia mensagem para meu pai...
				jpvmBuffer buf = new jpvmBuffer();
				buf.pack(result);
				jpvm.pvm_send(buf, parent, EnPVMResult.Sucesso.GetCodigo());
			} catch (Exception e) {
				jpvmBuffer buf = new jpvmBuffer();
				buf.pack(e.getMessage());
				jpvm.pvm_send(buf, parent, EnPVMResult.Erro.GetCodigo());
			}

			// sai do jpvm
			jpvm.pvm_exit();
		} catch (jpvmException jpe) {
		}

	}
}
