package main;

import java.util.Scanner;

import net.sf.javaml.core.Dataset;

import com.furb.entrega.ui.OpcoesEntrada;
import com.furb.frota.CalcularFrota_Master;
import com.furb.pedido.Pedido;
import com.furb.regiao.CriarRegiao_Master;
import com.furb.regiao.Regiao;
import com.furb.utils.Mock;
import com.furb.utils.RegiaoUtil;

public class Main {
	private static final String SAIR = "666 - Sair";
	private static final String FROTA_NECESSARIA = "Frota necessária";
	private static final String OPCAO_INVALIDA = "Opcao inválida! Tente novamente.";
	private static final String QTD_REGI0ES_CRIAR = "Quantas regiões você deseja criar?";
	private static final String CALCULAR_FROTA_NECESSARIA = "1 - Calcular Frota Necessária";
	private static final String CRIAR_REGIOES = "2 - Criar Regiões";
	private static final String CALCULAR_FRETE = "3 - Calcular o Frete do Pedido ";
	private static final String EFETUAR_ENTREGA = "4 - Efetuar entrega ";
	private static final String ESCOLHA_UMA_OPCAO = "Escolha uma opção: ";
	private static final String QUEBRA_DE_LINHA = "\n";
	private static Mock mock;
	private static Scanner scanner;
	private static StringBuilder sb;

	static {
		sb = new StringBuilder();
		sb.append(ESCOLHA_UMA_OPCAO);
		sb.append(QUEBRA_DE_LINHA);
		sb.append(CALCULAR_FROTA_NECESSARIA);
		sb.append(QUEBRA_DE_LINHA);
		sb.append(CRIAR_REGIOES);
		sb.append(QUEBRA_DE_LINHA);
		sb.append(CALCULAR_FRETE);
		sb.append(QUEBRA_DE_LINHA);
		sb.append(EFETUAR_ENTREGA);
		sb.append(QUEBRA_DE_LINHA);
		sb.append(SAIR);
		sb.append(QUEBRA_DE_LINHA);
	}

	public static void main(String[] args) {
		int opcao = 0;
		boolean sair = false;
		scanner = new Scanner(System.in);
		mock = new Mock();

		System.out.println(Main.getTxtIni());

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
					System.out.println("\n" + QTD_REGI0ES_CRIAR);
					opcao = Integer.parseInt(scanner.next());
					CriarRegioes(opcao, pedidos);
					break;
				case 3:
					break;
				case 4:
					 new OpcoesEntrada();
					break;
				case 666:
					sair = true;
					break;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("\n" + OPCAO_INVALIDA);
			}
		}
	}

	private static void CalcularFrota() {

		CalcularFrota_Master cfm = new CalcularFrota_Master();
		int numFrota = cfm.CalcularFrota();

		System.out.println(FROTA_NECESSARIA + " = " + numFrota);
		System.out.println("\n" + Main.getTxtIni());
	}

	private static void CriarRegioes(int numRegioes, int numPedidos) {
		Pedido[] pedidos = Mock.GetPedidos(numPedidos);
		Dataset[] dsFinal = CriarRegiao_Master.FormarRegioes(numRegioes, pedidos);

		Regiao[] regioes = RegiaoUtil.CriarRegioes(dsFinal, pedidos);

		mock.SetRegioes(regioes);
		System.out.println("\n" + Main.getTxtIni());
	}

	/**
	 * Cabeçalho de inicialização.
	 * 
	 * @return Texto apresentado para o usuário.
	 */
	public static String getTxtIni() {
		return sb.toString();
	}
}
