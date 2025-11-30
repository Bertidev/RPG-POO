import java.util.Scanner;
import java.util.Random;

/**
 * Classe principal que gerencia o fluxo do jogo (Game Loop).
 * Contém a história, a criação de personagem e os menus.
 */
public class Jogo {

    // Variáveis globais do sistema
    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();
    private static Personagem jogador; // O herói controlado pelo usuário

    public static void main(String[] args) {
        imprimirCabecalho();
        criarPersonagem();
        iniciarHistoria();
    }

    // --- 1. SISTEMA DE CRIAÇÃO DE PERSONAGEM ---
    
    private static void criarPersonagem() {
        System.out.println("\n--- CRIAÇÃO DE PERSONAGEM ---");
        System.out.print("Digite o nome do seu herói: ");
        String nome = scanner.nextLine();

        System.out.println("\nEscolha sua classe:");
        System.out.println("1. Templário (Guerreiro) - HP Alto, Defesa Alta");
        System.out.println("2. Exorcista (Mago) - Ataque Mágico Altíssimo, Frágil");
        System.out.println("3. Inquisidor (Arqueiro) - Equilibrado, Ataque à Distância");
        
        int escolha = lerOpcao(3);

        switch (escolha) {
            case 1:
                jogador = new Guerreiro(nome);
                break;
            case 2:
                jogador = new Mago(nome);
                break;
            case 3:
                jogador = new Arqueiro(nome);
                break;
        }

        System.out.println("\nVocê escolheu a classe de " + jogador.getClass().getSimpleName() + "!");
        System.out.println("Seus status iniciais:");
        System.out.println("HP: " + jogador.getPontosVida() + " | ATK: " + jogador.getAtaque() + " | DEF: " + jogador.getDefesa());
        esperarEnter();
    }

    // --- 2. FLUXO DA HISTÓRIA ("TABULEIRO") ---

    private static void iniciarHistoria() {
        // CENA 1: O VILAREJO
        limparTela();
        System.out.println("=== CENA 1: VILAREJO CREPÚSCULO ===");
        System.out.println("Você chega ao vilarejo sob uma chuva torrencial.");
        System.out.println("O líder local se aproxima: 'Por favor, " + jogador.getNome() + ", nos ajude!'");
        System.out.println("'Uma escuridão emanada da Cripta Antiga está matando nosso gado.'");
        System.out.println("'Tome isto para ajudar em sua jornada.'");
        
        // O líder dá um item ao jogador
        Item pocao = new Item("Poção de Cura", "Cura 30 HP", "cura", 2);
        jogador.getInventario().adicionar(pocao);
        
        esperarEnter();

        // CENA 2: A CRIPTA (DECISÃO)
        limparTela();
        System.out.println("=== CENA 2: A ENTRADA DA CRIPTA ===");
        System.out.println("Você está diante de dois corredores escuros.");
        System.out.println("Da esquerda, você ouve sons de pequenos passos.");
        System.out.println("Da direita, sente um cheiro podre de enxofre.");
        
        System.out.println("\nO que você faz?");
        System.out.println("1. Ir pela Esquerda");
        System.out.println("2. Ir pela Direita");
        
        int escolhaCaminho = lerOpcao(2);

        if (escolhaCaminho == 1) {
            caminhoEsquerda();
        } else {
            caminhoDireita();
        }

        // Se o jogador sobreviver ao caminho, vai para o Chefe
        if (jogador.estaVivo()) {
            cenaChefe();
        } else {
            gameOver();
        }
    }

    private static void caminhoEsquerda() {
        System.out.println("\n--- CORREDOR DA ESQUERDA ---");
        System.out.println("Você encontra um pequeno Diabrete mexendo em um baú!");
        
        Inimigo diabrete = new Inimigo("Diabrete", 40, 8, 5, 1);
        // Adiciona um item extra ao inimigo
        diabrete.getInventario().adicionar(new Item("Mana Potion", "Recupera MP", "mana", 1));
        
        // Inicia o combate (método definido nas subclasses do jogador)
        batalhar(diabrete); 
        
        if (jogador.estaVivo()) {
            System.out.println("\nNo baú que o Diabrete protegia, você encontra um Amuleto!");
            jogador.getInventario().adicionar(new Item("Amuleto Sagrado", "Item de missão", "quest", 1));
            esperarEnter();
        }
    }

    private static void caminhoDireita() {
        System.out.println("\n--- CORREDOR DA DIREITA ---");
        System.out.println("Você cai em uma emboscada! Duas Larvas Infernais atacam.");
        
        // Batalha 1
        Inimigo larva1 = new Inimigo("Larva Infernal A", 25, 5, 2, 1);
        batalhar(larva1);

        // Se sobreviver, Batalha 2
        if (jogador.estaVivo()) {
            System.out.println("A segunda larva avança!");
            Inimigo larva2 = new Inimigo("Larva Infernal B", 25, 5, 2, 1);
            batalhar(larva2);
        }
        esperarEnter();
    }

    private static void cenaChefe() {
        limparTela();
        System.out.println("=== CENA 3: O ALTAR PROFANO ===");
        System.out.println("Você chega ao coração da cripta.");
        System.out.println("Um demônio maior, um Malikis, está abrindo um portal!");
        System.out.println("CHEFE: 'Você chegou tarde, mortal!'");
        
        Inimigo chefe = new Inimigo("Malikis, o Devastador", 120, 12, 10, 2);
        
        // Inicia a batalha
        batalhar(chefe);
        
        // --- LÓGICA DE FINALIZAÇÃO CORRIGIDA ---
        
        if (jogador.estaVivo() && !chefe.estaVivo()) {
            // CASO 1: VITÓRIA (Jogador vivo, Chefe morto)
            System.out.println("\n*** VITÓRIA! ***");
            System.out.println("Com a morte do demônio, o portal se fecha.");
            System.out.println("O vilarejo está salvo graças a você, " + jogador.getNome() + ".");
            System.out.println("FIM DE JOGO.");
            
        } else if (jogador.estaVivo() && chefe.estaVivo()) {
            // CASO 2: FUGA (Jogador vivo, Chefe vivo)
            System.out.println("\n*** FUGA! ***");
            System.out.println("Você corre para fora da cripta, salvando sua própria pele.");
            System.out.println("Mas, atrás de você, o portal se abre completamente.");
            System.out.println("O Vilarejo Crepúsculo foi consumido pelas trevas.");
            System.out.println("FIM DE JOGO (Final Ruim).");
            
        } else {
            // CASO 3: DERROTA (Jogador morto)
            gameOver();
        }
    }

    // --- 3. SISTEMA DE COMBATE E MENUS ---

    /**
     * Gerencia o menu de combate. Este método chama o 'batalhar' específico
     * da classe do jogador (Guerreiro, Mago, etc), mas aqui poderíamos
     * ter lógica extra se necessário.
     * * Para simplificar e cumprir o requisito de polimorfismo, vamos chamar
     * o método batalhar() diretamente nas instâncias no fluxo acima.
     * Mas precisamos de um método auxiliar para gerenciar as escolhas DO TURNO.
     */
    private static void batalhar(Inimigo inimigo) {
        // Chama o combate real nas classes (Guerreiro/Mago/Arqueiro)
        jogador.batalhar(inimigo); 
        
        // CORREÇÃO: Só faz o saque se o jogador estiver vivo E o inimigo estiver morto
        if (jogador.estaVivo() && !inimigo.estaVivo()) {
            System.out.println("\nVocê vasculha os restos de " + inimigo.getNome() + "...");
            System.out.println("Você encontrou itens úteis!"); 
            // Aqui transferimos o loot real
            jogador.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());
        } 
        // Se o jogador fugiu (ambos vivos), não acontece nada aqui.
    }
    // --- 4. MÉTODOS AUXILIARES (TELA E INPUT) ---

    private static int lerOpcao(int max) {
        int opcao = 0;
        do {
            System.out.print("Escolha: ");
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
            if (opcao < 1 || opcao > max) {
                System.out.println("Opção inválida.");
            }
        } while (opcao < 1 || opcao > max);
        return opcao;
    }

    private static void esperarEnter() {
        System.out.println("\n(Pressione ENTER para continuar...)");
        scanner.nextLine();
    }

    private static void limparTela() {
        // Simula limpeza de tela pulando linhas
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    private static void imprimirCabecalho() {
        System.out.println("################################");
        System.out.println("#      A MANCHA DE HALED       #");
        System.out.println("#      RPG de Texto em Java    #");
        System.out.println("################################");
        System.out.println("Criado por: [Seu Nome]");
        esperarEnter();
    }
    
    private static void gameOver() {
        System.out.println("\n=== GAME OVER ===");
        System.out.println("Seu herói caiu em combate. O vilarejo foi consumido pelas trevas.");
    }
}