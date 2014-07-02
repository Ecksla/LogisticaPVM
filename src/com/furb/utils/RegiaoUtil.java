package com.furb.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClusterer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import weka.clusterers.XMeans;

import com.furb.pedido.Pedido;
import com.furb.pedido.PedidoUtil;
import com.furb.regiao.Regiao;

public class RegiaoUtil {
	
	public static XYSeriesCollection DataSetToXYSeriesCollection(
			Dataset[] dataset, int[] frete) {
		XYSeriesCollection result = new XYSeriesCollection();
		for (int i = 0; i < dataset.length; i++) {
			XYSeries series = new XYSeries("Reg." + (i + 1) + "(R$" + frete[i]);

			DefaultDataset defaultDataset = ((DefaultDataset) dataset[i]);
			for (int j = 0; j < defaultDataset.size(); j++) {
				DenseInstance denseInst = (DenseInstance) defaultDataset
						.elementAt(j);
				series.add(denseInst.value(0), denseInst.value(1));
			}

			result.addSeries(series);
		}

		return result;
	}	
	
	public static Dataset[] CriarRegiaoKMeans(InputStream pedidos, int numClusters) {

		Reader reader = new InputStreamReader(pedidos);

		Dataset data = FileHandler.load(reader, 0, ";");
						
		Clusterer km = new KMeans(numClusters);
		
		Dataset[] clustersKM = km.cluster(data);
		
		return clustersKM;
	}
	
	public static byte[] PedidosToArrayByte(Pedido[] pedidos) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (Pedido pedido : pedidos) {
			try {
				baos.write(MessageFormat.format("{0};{1};{2}\n",
						pedido.getCodigo(), pedido.getCoordenadaX(),
						pedido.getCoordenadaY()).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return baos.toByteArray();
	}

	public static Regiao[] CriarRegioes(Dataset[] ds, Pedido[] pedidos) {
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
	
	public static Dataset[] CriarRegiaoWekaClusterer(InputStream pedidos,
			int numClusters) {

		Reader reader = new InputStreamReader(pedidos);

		Dataset data = FileHandler.load(reader, 0, ";");

		XMeans xm = new XMeans();

		xm.setMinNumClusters(numClusters);
		xm.setMaxNumClusters(numClusters);

		Clusterer jmlxm = new WekaClusterer(xm);

		Dataset[] clustersXM = jmlxm.cluster(data);

		return clustersXM;
	}	
	

}
