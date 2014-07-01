import java.awt.BorderLayout;
import java.util.Scanner;

import javax.swing.JFrame;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.furb.frota.CalcularFrota_Master;
import com.furb.pedido.Pedido;
import com.furb.pedido.PedidoUtil;
import com.furb.regiao.CriarRegiao_Master;
import com.furb.regiao.Regiao;
import com.furb.utils.Mock;
import com.furb.utils.RegiaoUtil;

public class Main {
	private static final String CALCULAR_FROTA_NECESSARIA = "1 - Calcular Frota Necessária";
	private static final String CRIAR_REGIOES = "2 - Criar Regiões";
	private static final String CALCULAR_FRETE = "3 - Calcular o Frete do Pedido ";
	private static final String EFETUAR_ENTREGA = "4 - Efetuar entrega ";
	private static final String ESCOLHA_UMA_OPCAO = "Escolha uma opção: ";
	private static Mock mock;
	private static Scanner scanner;
	

	public static void main(String[] args) {
		int opcao = 0;
		boolean sair = false;
		scanner = new Scanner(System.in);
		mock = new Mock();

		System.out.println(ESCOLHA_UMA_OPCAO);
		System.out.println(CALCULAR_FROTA_NECESSARIA);
		System.out.println(CRIAR_REGIOES);
		System.out.println(CALCULAR_FRETE);
		System.out.println(EFETUAR_ENTREGA);
		System.out.println("666 - Sair");

		while (!sair) {
			try {
				opcao = Integer.parseInt(scanner.next());
				switch (opcao) {
				case 1:
					CalcularFrota();
					break;
				case 2:
					System.out.println("\nQuantas regiões você deseja criar?");
					opcao = Integer.parseInt(scanner.next());
					CriarRegioes(opcao);
					break;
				case 3:
					break;
				case 4:
					//new OpcoesEntrada();
					break;
				case 666:
					sair = true;
				default:
					break;
				}

				System.out.println("\n" + ESCOLHA_UMA_OPCAO);
				System.out.println(CALCULAR_FROTA_NECESSARIA);
				System.out.println(CRIAR_REGIOES);
				System.out.println(CALCULAR_FRETE);
				System.out.println(EFETUAR_ENTREGA);
				System.out.println("666 - Sair");

			} catch (NumberFormatException nfe) {
				System.out.println("\nOpcao invalida! Tente novamente.");
			}
		}
	}

	private static void CalcularFrota() {

		CalcularFrota_Master cfm = new CalcularFrota_Master();
		int numFrota = cfm.CalcularFrota();
		
		System.out.println("Frota necessária = " + numFrota);
	}

	private static void CriarRegioes(int numRegioes) {
		Pedido[] pedidos = mock.GetPedidos(70);
		CriarRegiao_Master criarRegMaster = new CriarRegiao_Master();
		Dataset[] dsFinal = criarRegMaster.FormarRegioes(numRegioes, pedidos);
		
		Regiao[] regioes = criarRegMaster.CriarRegioes(dsFinal, pedidos);
		
		mock.SetRegioes(regioes);
	}
	
//	private static void ImprimirRegioes(Regiao[] regioes){		
//		for (int i = 0; i < regioes.length; i++) {
//			System.out.println("");
//			System.out.print(regioes[i].getId());
//			for (int j = 0; j < regioes[i].getPedidos().size(); j++) {
//				System.out
//						.print(" -> ("
//								+ regioes[i].getPedidos().get(j)
//										.getCoordenadaX()
//								+ ", "
//								+ regioes[i].getPedidos().get(j)
//										.getCoordenadaY() + ")");
//			}
//		}
//	}
//
//	private static Regiao[] CriarRegioes(Dataset[] ds, Pedido[] pedidos) {
//		Regiao[] regioes = new Regiao[ds.length];
//		Regiao tempReg;
//		Pedido tempPed;
//
//		OMP.setNumThreads(regioes.length);
//
//		int j = 0;
//
//		for (int i = 0; i < ds.length; i++) {
//			tempReg = new Regiao("Região " + (i + 1));
//
//			DefaultDataset defaultDataset = ((DefaultDataset) ds[i]);
//
//			//omp parallel private(tempPed)
//			{
//				//omp for
//				for (j = 0; j < defaultDataset.size(); j++) {
//					DenseInstance denseInst = (DenseInstance) defaultDataset
//							.elementAt(j);
//					tempPed = PedidoUtil
//							.findPedido(Integer.parseInt(denseInst.classValue()
//									.toString()), pedidos);
//
//					if (tempPed != null) {
//						//omp critical
//						{
//							tempReg.addPedido(tempPed);
//						}
//					}
//				}
//			}
//
//			regioes[i] = tempReg;
//		}
//
//		return regioes;
//	}
}
