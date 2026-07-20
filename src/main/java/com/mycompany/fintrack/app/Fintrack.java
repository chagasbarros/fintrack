package com.mycompany.fintrack.app;
import java.util.Scanner;
import com.mycompany.fintrack.controller.FinTracker;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.exceptions.EntradaInvalidaException;
import java.util.InputMismatchException;


public class Fintrack {

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
                if(valor <= 0){
                    throw new EntradaInvalidaException("O valor deve ser maior que zero.");
                }
                
                System.out.println("RECEITA OU DESPESA ");
                String tipo = sc.next().trim().toUpperCase();
                
                if(!tipo.equals("RECEITA") && !tipo.equals("DESPESA")){
                    System.out.println("Tipo de transação inválido");
                    return;
                }
                
                Transacao transacao = new Transacao(descricao, valor, tipo);
                fintracker.adicionarTransacao(transacao);
                System.out.println("TRANSACAO ADICIONADA COM SUCESSO!!!");
            
            }
            
            case 2 -> {
                System.out.println("Transacoes listadas");
                fintracker.listarTransacoes();
            }
            
            case 3 -> {
                System.out.println("Saldo atual: " + fintracker.calcularSaldoTotal());
            }
            
            case 4 -> {
                System.out.println("Digite o indice para remoção: ");
                int indice = sc.nextInt();
                fintracker.removerTransacao(indice);
                
                System.out.println("Transacao removida!");
            }
            
            case 5 -> System.out.println("Saindo do Sistema...");
            
            default -> System.out.println("Opcao Invalida");
        }
           
            }catch(InputMismatchException e){
                System.out.println("Erro: digite apenas números. ");
                sc.nextLine();
            
            
            }catch(EntradaInvalidaException e){
                System.out.println(e.getMessage());
                
            }
            
           
           
        }
    }
    
    
}
