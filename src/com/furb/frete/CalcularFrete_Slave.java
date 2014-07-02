package com.furb.frete;

import com.furb.utils.*;
import jpvm.*;

public class CalcularFrete_Slave
{
	 static int num_workers = 1;
	    private static Mock mock;
	    
    public static void main(String args[])
    {
        try
        {
            jpvmEnvironment jpvm = new jpvmEnvironment();
            jpvmTaskId parent = jpvm.pvm_parent();
            jpvmMessage message = jpvm.pvm_recv();
            try
            {
                int tag = message.messageTag;
                byte byteArray[] = new byte[tag];
                message.buffer.unpack(byteArray, tag, 1);
                com.furb.pedido.Pedido pedidos[] = (com.furb.pedido.Pedido[])SerializationUtils.ByteArrayToArrayObject(byteArray);
                float frete = 0.0F;
                com.furb.pedido.Pedido apedido[];
                int j = (apedido = pedidos).length;
                for(int i = 0; i < j; i++)
                {
                    com.furb.pedido.Pedido pedido = apedido[i];
                    frete += CalcularFrete.CalcularFrete(pedido);
                }

                jpvmBuffer buf = new jpvmBuffer();
                buf.pack(frete);
                jpvm.pvm_send(buf, parent, EnPVMResult.Sucesso.GetCodigo());
                jpvm.pvm_exit();
            }
            catch(Exception e)
            {
                jpvmBuffer buf = new jpvmBuffer();
                buf.pack(e.getMessage());
                jpvm.pvm_send(buf, parent, EnPVMResult.Erro.GetCodigo());
            }
        }
        catch(jpvmException jpvmexception) { }
    }
}