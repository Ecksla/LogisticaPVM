package com.furb.regiao;

import java.awt.BorderLayout;
import java.util.Scanner;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import jomp.runtime.OMP;
import jpvm.jpvmBuffer;
import jpvm.jpvmEnvironment;
import jpvm.jpvmException;
import jpvm.jpvmMessage;
import jpvm.jpvmTaskId;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;

import com.furb.pedido.Pedido;
import com.furb.pedido.PedidoUtil;
import com.furb.utils.Mock;
import com.furb.utils.RegiaoUtil;
import com.furb.utils.SerializationUtils;

public class CriarRegiao_Master {
	static int num_algorithms = EnAlgoritimo.values().length;

	private static Mock mock;
	private static Scanner scanner;

	public static Dataset[] FormarRegioes(int numRegioes, Pedido[] pedidos) {
		int opcao = 0;
		boolean sair = false;
		scanner = new Scanner(System.in);
		mock = new Mock();
		Dataset[] dsFinal = null;
		try {
			// inicia o jpvm...
			jpvmEnvironment jpvm = new jpvmEnvironment();

			// distribui o trabalho...
			jpvmTaskId tids[] = new jpvmTaskId[num_algorithms];
			jpvm.pvm_spawn("com.furb.regiao.FormarRegioes_Slave",
					num_algorithms, tids);

			for (int i = 0; i < EnAlgoritimo.values().length; i++) {
				jpvmBuffer buf = new jpvmBuffer();
				// byte[] arrayByte = RegiaoUtil.PedidosToArrayByte(pedidos);
				byte[] arrayByte = SerializationUtils
						.ObjectArrayToByteArray(pedidos);
				int asdf = arrayByte.length;
				buf.pack(arrayByte, arrayByte.length, 1);

				int[] tags = new int[3];
				tags[0] = EnAlgoritimo.GetByIndex(i).GetCodigo();
				tags[1] = numRegioes;
				tags[2] = arrayByte.length;

				jpvm.pvm_send(buf, tids[i], tags);
			}

			JFreeChart chartKMeans;
			JFreeChart chartWeka;

			Dataset[] dsKMeans = null;
			Dataset[] dsWeka = null;

			for (int i = 0; i < tids.length; i++) {
				// recebe uma mensagem...
				jpvmMessage message = jpvm.pvm_recv();
				String msg = message.buffer.upkstr();
				byte[] byteArray = null;

				message.buffer.unpack(byteArray, 0, 1);

				switch (EnAlgoritimo.GetByIndex(i)) {
				case KMeans:
					dsKMeans = (Dataset[]) SerializationUtils
							.ByteArrayToObject(byteArray);
					break;
				case Weka:
					dsWeka = (Dataset[]) SerializationUtils
							.ByteArrayToObject(byteArray);

					break;
				default:
					dsKMeans = (Dataset[]) SerializationUtils
							.ByteArrayToObject(byteArray);
					chartKMeans = ChartFactory.createScatterPlot(
							"KMeans Clustering", "X", "Y",
							RegiaoUtil.DataSetToXYSeriesCollection(dsKMeans));

					break;
				}
			}

			chartWeka = ChartFactory.createScatterPlot("Weka Clustering", "X",
					"Y", RegiaoUtil.DataSetToXYSeriesCollection(dsWeka));

			chartKMeans = ChartFactory.createScatterPlot("KMeans Clustering",
					"X", "Y", RegiaoUtil.DataSetToXYSeriesCollection(dsKMeans));

			JFrame frame = new JFrame("Chart");
			frame.getContentPane().add(new ChartPanel(chartKMeans),
					BorderLayout.WEST);
			frame.getContentPane().add(new ChartPanel(chartWeka),
					BorderLayout.EAST);
			frame.pack();
			frame.setVisible(true);

			// sai do jpvm
			jpvm.pvm_exit();

			System.out
					.println("\nQual região você deseja salvar? 1 - KMeans Clustering / 2 - Weka Clustering");
			try {
				opcao = Integer.parseInt(scanner.next());
			} catch (NumberFormatException e) {
				opcao = 1;
			}

			switch (opcao) {
			case 1:
				dsFinal = dsKMeans;
				break;
			case 2:
				dsFinal = dsWeka;
				break;
			default:
				dsFinal = dsKMeans;
				break;
			}

		} catch (jpvmException jpe) {
			System.out.println("Error - jpvm exception");
		}

		return dsFinal;
	}

	public Regiao[] CriarRegioes(Dataset[] ds, Pedido[] pedidos) {
		Regiao[] regioes = new Regiao[ds.length];
		Regiao tempReg;
		Pedido tempPed;

		for (int i = 0; i < ds.length; i++) {
			tempReg = new Regiao("Região " + (i + 1));

			DefaultDataset defaultDataset = ((DefaultDataset) ds[i]);

			for (int j = 0; j < defaultDataset.size(); j++) {
				DenseInstance denseInst = (DenseInstance) defaultDataset
						.elementAt(j);
				tempPed = PedidoUtil.findPedido(
						Integer.parseInt(denseInst.classValue().toString()),
						pedidos);

				if (tempPed != null) {
					tempReg.addPedido(tempPed);
				}
			}
			
			regioes[i] = tempReg;
		}
		
		return regioes;
	}
}
