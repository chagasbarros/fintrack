package com.mycompany.fintrack.app;
import java.util.Scanner;
import com.mycompany.fintrack.controller.FinTracker;
import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.exceptions.EntradaInvalidaException;
import com.mycompany.fintrack.utils.Formatador;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.InputMismatchException;
import java.util.List;


public class Fintrack {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT); // rejeita datas como 31/02

    public static void main(String[] args) {

        FinTracker fintracker = new FinTracker();
        Scanner sc = new Scanner(System.in);
        int opcao = 0;

        while(opcao != 5){
            try {
                System.out.println("=====FINTRACK - SEU CONTROLE FINANCEIRO =====");
                System.out.println("1. Adicionar nova transacao");
                System.out.println("2. Listar Transacoes");
                System.out.println("3. Mostrar saldo atual");
                System.out.println("4. Remover transacao");
                System.out.println("5. Sair");
                System.out.print("Escolha uma opcao: ");
                opcao = sc.nextInt();
                sc.nextLine();

                if (opcao < 1 || opcao > 5){
                    throw new EntradaInvalidaException("A opcao deve estar entre 1 e 5.");
                }


           switch(opcao){

            case 1 -> {
                System.out.println("ADICIONANDO NOVA TRANSACAO!!!");
                System.out.println("Digite a Descricao: ");
                String descricao = sc.nextLine();

                System.out.println("Digite Valor: ");
                double valor = sc.nextDouble();
                sc.nextLine();

                System.out.println("RECEITA OU DESPESA ");
                String tipoDigitado = sc.nextLine().trim().toUpperCase();
                TipoTransacao tipo;
                try {
                    tipo = TipoTransacao.valueOf(tipoDigitado);
                } catch (IllegalArgumentException e) {
                    throw new EntradaInvalidaException("Tipo de transacao invalido. Use RECEITA ou DESPESA.");
                }

                System.out.println("Data (dd/MM/aaaa) ou Enter para hoje: ");
                String dataDigitada = sc.nextLine().trim();
                LocalDate data = dataDigitada.isEmpty()
                        ? LocalDate.now()
                        : LocalDate.parse(dataDigitada, FORMATO_DATA);

                Transacao transacao = new Transacao(descricao, valor, tipo, data);
                fintracker.adicionarTransacao(transacao);
                System.out.println("TRANSACAO ADICIONADA COM SUCESSO!!!");

            }

            case 2 -> {
                List<Transacao> transacoes = fintracker.listarTransacoes();
                if (transacoes.isEmpty()) {
                    System.out.println("Nenhuma transacao cadastrada.");
                } else {
                    System.out.println("Transacoes listadas");
                    for (int i = 0; i < transacoes.size(); i++) {
                        System.out.println((i + 1) + " - " + transacoes.get(i));
                    }
                }
            }

            case 3 -> {
                System.out.println("Saldo atual: " + Formatador.formatarMoeda(fintracker.calcularSaldoTotal()));
            }

            case 4 -> {
                System.out.println("Digite o indice para remocao: ");
                int indice = sc.nextInt();
                sc.nextLine();
                fintracker.removerTransacao(indice);

                System.out.println("Transacao removida!");
            }

            case 5 -> System.out.println("Saindo do Sistema...");
        }

            }catch(InputMismatchException e){
                System.out.println("Erro: digite apenas numeros. ");
                sc.nextLine();

            }catch(DateTimeParseException e){
                System.out.println("Data invalida. Use o formato dd/MM/aaaa.");

            }catch(EntradaInvalidaException | IllegalArgumentException | IndexOutOfBoundsException e){
                System.out.println(e.getMessage());

            }



        }
    }


}
