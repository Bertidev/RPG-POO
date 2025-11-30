//guerreiro, HP e defesa altas, ataque baixo

import java.util.Random;
import java.util.Scanner;

public class Guerreiro extends Personagem {

    //CONSTRUTOR PRINCIPAL
    public Guerreiro(String nome) {
        super(
            nome,  
            150,    //alto
            10,     //medio
            15,     //alta
            1      
        );
        
        //adicionando itens iniciais especificos do guerreiro
        Item espada = new Item("Espada Longa", "Uma espada básica de ferro", "ataque", 1);
        this.inventario.adicionar(espada);
    }

     //CONSTRUTOR DE COPIA
    //cria uma copia exata de outro guerreiro, requisito para save point
    public Guerreiro(Guerreiro outroGuerreiro) {
        //copia os atributos base chamando o construtor super
        super(
            outroGuerreiro.nome,
            outroGuerreiro.pontosVida,
            outroGuerreiro.ataque,
            outroGuerreiro.defesa,
            outroGuerreiro.nivel
        );
        
        //clona o inventario
        this.inventario = outroGuerreiro.inventario.clone();
    }

    //METODO DE COMBATE
    // Importante: Verifique se importou Scanner e Random no topo do arquivo
    // import java.util.Scanner;
    // import java.util.Random;

    @Override
    public void batalhar(Inimigo inimigo) {
        Scanner scanner = new Scanner(System.in);
        Random dado = new Random();

        System.out.println("========================================");
        System.out.println("   COMBATE INICIADO: " + this.getNome() + " vs " + inimigo.getNome());
        System.out.println("========================================");

        // Loop do combate
        while (this.estaVivo() && inimigo.estaVivo()) {
            
            // --- MOSTRAR STATUS ---
            System.out.println("\n----------------------------------------");
            System.out.println(this.getNome() + " HP: " + this.getPontosVida());
            System.out.println(inimigo.getNome() + " HP: " + inimigo.getPontosVida());
            System.out.println("----------------------------------------");
            System.out.println("Sua vez! Escolha uma ação:");
            System.out.println("[1] Atacar");
            System.out.println("[2] Usar Poção de Cura");
            System.out.println("[3] Tentar Fugir");
            System.out.print(">> ");

            String escolha = scanner.nextLine();

            // --- TURNO DO JOGADOR ---
            boolean turnoPassou = true; // Controla se o inimigo ataca depois

            if (escolha.equals("1")) {
                // OPÇÃO 1: ATACAR (Regra: d20 + Ataque vs Defesa)
                System.out.println("\n> Você ataca com sua arma!");
                this.atacar(inimigo);

            } else if (escolha.equals("2")) {
                // OPÇÃO 2: USAR ITEM
                if (this.getInventario().temItem("Poção de Cura")) {
                    this.getInventario().remover("Poção de Cura");
                    this.curar(30); // Valor fixo de cura para simplificar
                    System.out.println("> Você bebeu uma Poção de Cura.");
                } else {
                    System.out.println("\n> Você revira a bolsa, mas não tem Poções!");
                    // Não perde o turno se errar o item
                    turnoPassou = false; 
                }

            } else if (escolha.equals("3")) {
                // OPÇÃO 3: FUGIR (Regra: d6. 1-2 falha, 3-6 sucesso)
                System.out.println("\n> Você tenta correr...");
                int rolagemFuga = dado.nextInt(6) + 1;
                
                if (rolagemFuga >= 3) {
                    System.out.println("> SUCESSO! (Dado: " + rolagemFuga + ") Você escapou da batalha.");
                    return; // Encerra o método batalhar imediatamente
                } else {
                    System.out.println("> FALHA! (Dado: " + rolagemFuga + ") O inimigo bloqueou sua passagem!");
                    // Turno passa e o jogador apanha
                }

            } else {
                System.out.println("Opção inválida.");
                turnoPassou = false;
            }

            // Verifica se inimigo morreu antes de ele atacar
            if (!inimigo.estaVivo()) {
                System.out.println("\n****************************************");
                System.out.println("   VITÓRIA! O inimigo foi derrotado!");
                System.out.println("****************************************");
                
                // Lógica de Loot (Saque)
                System.out.println("Você saqueia o corpo do inimigo...");
                // Clona o inventário do inimigo para evitar bugs de referência
                this.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());
                break;
            }

            // --- TURNO DO INIMIGO ---
            if (turnoPassou) {
                System.out.println("\n> Vez do " + inimigo.getNome() + "...");
                // Pausa dramática pequena (opcional, pode remover se der erro)
                try { Thread.sleep(1000); } catch (Exception e) {} 
                
                inimigo.atacar(this);
            }
            
            // Verifica se jogador morreu
            if (!this.estaVivo()) {
                System.out.println("\nVOCÊ MORREU! O destino de Haled está selado...");
                break;
            }
        }
    }
}