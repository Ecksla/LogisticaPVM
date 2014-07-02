package com.furb.regiao;

import java.awt.BorderLayout;
import java.util.Scanner;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

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
				byte[] arrayByte = SerializationUtils
						.ObjectArrayToByteArray(pedidos);
				
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
			
			int[][] valorFreteRegioes = new int[2][numRegioes];
			for (int i = 0; i < tids.length; i++) {
				// recebe uma mensagem...
				jpvmMessage message = jpvm.pvm_recv();
				
				int[] tagsfrete = message.messageTagArray;
				
				byte[] byteArray = new byte[tagsfrete[0]];

				message.buffer.unpack(byteArray, byteArray.length, 1);
				
				for (int j = 0; j < valorFreteRegioes[i].length; j++) {
					valorFreteRegioes[i][j] = tagsfrete[j+1];
				}
				
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
					break;
				}
			}

			chartWeka = ChartFactory.createScatterPlot("Weka Clustering", "X",
					"Y", RegiaoUtil.DataSetToXYSeriesCollection(dsWeka, valorFreteRegioes[0]));

			chartKMeans = ChartFactory.createScatterPlot("KMeans Clustering",
					"X", "Y", RegiaoUtil.DataSetToXYSeriesCollection(dsKMeans, valorFreteRegioes[1]));

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

	
}
