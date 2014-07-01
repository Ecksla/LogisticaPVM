package com.furb.regiao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import jpvm.*;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClusterer;
import weka.clusterers.XMeans;
import weka.gui.SetInstancesPanel;

import com.furb.pedido.Pedido;
import com.furb.utils.EnPVMResult;
import com.furb.utils.Mock;
import com.furb.utils.RegiaoUtil;
import com.furb.utils.SerializationUtils;

class FormarRegioes_Slave {
	static int num_workers = 1;
	private static Mock mock;

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

			jpvmBuffer buf1 = new jpvmBuffer();
			buf1.pack("byteArray: " + byteArray.length + ",tags[1]: " + tags[2]);

			Dataset[] clusters = null;

			Pedido[] pedidos = SerializationUtils
					.ByteArrayToArrayObject(byteArray);

			byte[] pedidosBytes = RegiaoUtil.PedidosToArrayByte(pedidos);

			InputStream inPedidos = new ByteArrayInputStream(pedidosBytes);

			EnAlgoritimo algoritmo = EnAlgoritimo.ObterPorCodigo(tags[0]);

			switch (algoritmo) {
			case KMeans:
				clusters = RegiaoUtil.CriarRegiaoKMeans(inPedidos, tags[1]);
				break;
			case Weka:
				clusters = RegiaoUtil.CriarRegiaoWekaClusterer(inPedidos,
						tags[1]);
				break;
			default:
				clusters = RegiaoUtil.CriarRegiaoKMeans(inPedidos, tags[1]);
				break;
			}
			
			byte[] ClustersByteArray = SerializationUtils
					.ObjectToByteArray(clusters);
			
			// envia mensagem para meu pai...
			jpvmBuffer buf = new jpvmBuffer();

			buf.pack(ClustersByteArray, ClustersByteArray.length, 1);

			jpvm.pvm_send(buf, parent, ClustersByteArray.length);

			// sai do jpvm
			jpvm.pvm_exit();
		} catch (Throwable thr) {			
			StringWriter errors = new StringWriter();
			thr.printStackTrace(new PrintWriter(errors));
			JOptionPane.showMessageDialog(null, errors.toString());
			//System.out.println("Error - jpvm exception");
		}
	}
};
