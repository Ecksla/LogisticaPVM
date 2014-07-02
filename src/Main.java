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
					System.out.println("\nInforme a quantidade de pedidos?");
					int pedidos = Integer.parseInt(scanner.next());
					System.out.println("\nQuantas regiões você deseja criar?");
					opcao = Integer.parseInt(scanner.next());
					CriarRegioes(opcao, pedidos);
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

	private static void CriarRegioes(int numRegioes, int numPedidos) {
		Pedido[] pedidos = mock.GetPedidos(numPedidos);
		CriarRegiao_Master criarRegMaster = new CriarRegiao_Master();
		Dataset[] dsFinal = criarRegMaster.FormarRegioes(numRegioes, pedidos);
		
		Regiao[] regioes = RegiaoUtil.CriarRegioes(dsFinal, pedidos);
		
		mock.SetRegioes(regioes);
	}	
}


