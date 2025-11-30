import java.util.Scanner;

/**
 * Classe principal que gerencia o fluxo do jogo (Game Loop).
 * Contém a história, a criação de personagem e os menus.
 */
public class Jogo {

    // Variáveis globais do sistema
    private static Scanner scanner = new Scanner(System.in);
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
        System.out.println("1. Templário (Guerreiro) - HP Alto, Defesa Alta, Ataque Baixo");
        System.out.println("2. Exorcista (Mago) - HP Baixo, Defesa Baixa, Ataque Mágico Altíssimo");
        System.out.println("3. Inquisidor (Arqueiro) - HP Medio, Defesa Baixa, Ataque Medio");
        
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
        jogador.getInventario().adicionar(pocao, false);
        
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
    
        // AQUI ESTÁ A MUDANÇA: note o ', true' no final.
        // Isso diz ao inventário: "Adicione isso, mas fique quieto!"
        diabrete.getInventario().adicionar(new Item("Mana Potion", "Recupera MP", "mana", 1), true);

        // Opcional: Adicionar Poção de Cura ao Diabrete também (para o jogador saquear)
        diabrete.getInventario().adicionar(new Item("Poção de Cura", "Cura 30 HP", "cura", 1), true);
 
        batalhar(diabrete); 
        
        if (jogador.estaVivo()) {
            System.out.println("\nNo baú que o Diabrete protegia, você encontra um Amuleto!");
            jogador.getInventario().adicionar(new Item("Amuleto Sagrado", "Aumenta DEF (3 rodadas)", "buff_def", 1), false);
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
        System.out.println("Você sente uma energia estranha... O local parece um ponto de convergência.");
        
        // --- SAVE POINT (CHECKPOINT) ---
        System.out.println(" > JOGO SALVO: Um checkpoint foi criado antes da batalha final.");
        Personagem saveState = clonarPersonagem(jogador); // Cria o backup
        
        boolean loopBatalha = true;

        while (loopBatalha) {
            System.out.println("\nUm demônio maior, um Malignus, está abrindo um portal!");
            System.out.println("CHEFE: 'Você chegou tarde, mortal!'");
            
            // Recriamos o chefe a cada tentativa para ele voltar com HP cheio
            Inimigo chefe = new Inimigo("Malikis, o Devastador", 120, 12, 10, 2);
            // Loot do chefe (Silencioso)
            chefe.getInventario().adicionar(new Item("Chifre de Malikis", "Troféu", "trofeu", 1), true);
            
            // Inicia o combate
            batalhar(chefe);
            
            // --- VERIFICAÇÃO PÓS-BATALHA ---
            
            if (jogador.estaVivo() && !chefe.estaVivo()) {
                // VITÓRIA
                System.out.println("\n*** VITÓRIA! ***");
                System.out.println("Com a morte do demônio, o portal se fecha.");
                System.out.println("O vilarejo está salvo graças a você, " + jogador.getNome() + ".");
                System.out.println("FIM DE JOGO.");
                loopBatalha = false; // Sai do loop e encerra o jogo
                
            } else if (jogador.estaVivo() && chefe.estaVivo()) {
                // FUGIU
                System.out.println("\n*** FUGA! ***");
                System.out.println("Você corre para fora da cripta, salvando sua própria pele.");
                System.out.println("O Vilarejo Crepúsculo foi consumido pelas trevas.");
                System.out.println("FIM DE JOGO (Final Ruim).");
                loopBatalha = false;
                
            } else {
                // DERROTA (JOGADOR MORREU)
                System.out.println("\n--- VOCÊ CAIU EM COMBATE ---");
                System.out.println("A escuridão começa a tomar conta de sua visão...");
                System.out.println("Deseja usar o Save Point e tentar novamente?");
                System.out.println("[1] Sim, eu não vou desistir!");
                System.out.println("[2] Não, aceito meu destino.");
                System.out.print(">> ");
                
                String opcao = scanner.nextLine();
                
                if (opcao.equals("1")) {
                    System.out.println("\n... Uma luz brilha e o tempo parece voltar ...");
                    // RESTAURA O SAVE
                    // Importante: Clonamos o saveState de novo, para ter tentativas infinitas
                    jogador = clonarPersonagem(saveState); 
                    System.out.println("Você está de volta à entrada do Altar, com seus itens e vida restaurados!");
                    esperarEnter();
                    // O loop continua e a batalha reinicia
                } else {
                    gameOver();
                    loopBatalha = false;
                }
            }
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
        jogador.batalhar(inimigo, scanner); 
        
        // CORREÇÃO: Só faz o saque se o jogador estiver vivo E o inimigo estiver morto
        if (jogador.estaVivo() && !inimigo.estaVivo()) {
            System.out.println("\nVocê vasculha os restos de " + inimigo.getNome() + "...");
            System.out.println("Você encontrou itens úteis!"); 
            
            // Pega o Loot
            jogador.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());
            
            // --- NOVO: LEVEL UP ---
            System.out.println("A experiência de combate te fortalece...");
            jogador.subirNivel(); // Chama o método que criamos!
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
        System.out.println("Criado por: Lucas Berti");
        esperarEnter();
    }
    
    private static void gameOver() {
        System.out.println("\n=== GAME OVER ===");
        System.out.println("Seu herói caiu em combate. O vilarejo foi consumido pelas trevas.");
    }

    /**
     * Método auxiliar para criar uma cópia exata do personagem (Save Point).
     * Verifica qual é a classe do herói e chama o construtor de cópia correspondente.
     */
    private static Personagem clonarPersonagem(Personagem original) {
        if (original instanceof Guerreiro) {
            return new Guerreiro((Guerreiro) original);
        } else if (original instanceof Mago) {
            return new Mago((Mago) original);
        } else if (original instanceof Arqueiro) {
            return new Arqueiro((Arqueiro) original);
        }
        return null; // Não deve acontecer
    }
}