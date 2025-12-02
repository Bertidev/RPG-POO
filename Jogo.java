import java.util.Scanner;

/**
 * classe principal que gerencia o fluxo do jogo (game loop)
 * responsavel pela criacao do personagem, navegacao pelos cenarios
 * e chamadas ao sistema de batalha
 */
public class Jogo {

    // variaveis globais do sistema
    private static Scanner scanner = new Scanner(System.in);
    private static Personagem jogador; // o heroi controlado pelo usuario

    public static void main(String[] args) {
        imprimirCabecalho();
        criarPersonagem();
        iniciarHistoria();
    }

    // sistema de criacao de personagem

    /**
     * gerencia o processo de criacao do heroi:
     * - escolhe nome
     * - escolhe classe
     * - mostra status iniciais
     */
    private static void criarPersonagem() {
        System.out.println("\n--- CRIAÇÃO DE PERSONAGEM ---");
        System.out.print("Digite o nome do seu herói: ");
        String nome = scanner.nextLine();

        // selecao de classe
        System.out.println("\nEscolha sua classe:");
        System.out.println("1. Templário (Guerreiro) - HP Alto, Defesa Alta, Ataque Baixo");
        System.out.println("2. Exorcista (Mago) - HP Baixo, Defesa Baixa, Ataque Mágico Altíssimo");
        System.out.println("3. Inquisidor (Arqueiro) - HP Medio, Defesa Baixa, Ataque Medio");
        
        int escolha = lerOpcao(3);

        // criacao da classe escolhida
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

        // exibe status iniciais
        System.out.println("\nVocê escolheu a classe de " + jogador.getClass().getSimpleName() + "!");
        System.out.println("Seus status iniciais:");
        System.out.println("HP: " + jogador.getPontosVida() + " | ATK: " + jogador.getAtaque() + " | DEF: " + jogador.getDefesa());
        esperarEnter();
    }

    // fluxo da historia principal

    /**
     * este metodo conduz o jogador pelos cenarios principais
     * cada cena representa um "capitulo" da aventura
     */
    private static void iniciarHistoria() {

        // cena 1: o vilarejo
        limparTela();
        System.out.println("=== CENA 1: VILAREJO CREPÚSCULO ===");
        System.out.println("Você chega ao vilarejo sob uma chuva torrencial.");
        System.out.println("O líder local se aproxima: 'Por favor, " + jogador.getNome() + ", nos ajude!'");
        System.out.println("'Uma escuridão emanada da Cripta Antiga está matando nosso gado.'");
        System.out.println("'Tome isto para ajudar em sua jornada.'");

        // da uma pocao ao jogador
        Item pocao = new Item("Poção de Cura", "Cura 30 HP", "cura", 2);
        jogador.getInventario().adicionar(pocao, false); // mostra mensagem
        
        esperarEnter();

        // cena 2: entrada da cripta
        limparTela();
        System.out.println("=== CENA 2: A ENTRADA DA CRIPTA ===");
        System.out.println("Você está diante de dois corredores escuros.");
        System.out.println("Da esquerda, você ouve pequenos passos.");
        System.out.println("Da direita, sente um cheiro forte de enxofre.");

        // escolha do caminho
        System.out.println("\nO que você faz?");
        System.out.println("1. Ir pela Esquerda");
        System.out.println("2. Ir pela Direita");

        int escolhaCaminho = lerOpcao(2);

        if (escolhaCaminho == 1) {
            caminhoEsquerda();
        } else {
            caminhoDireita();
        }

        // apos o caminho, verifica se o jogador sobreviveu para enfrentar o chefe
        if (jogador.estaVivo()) {
            cenaChefe();
        } else {
            gameOver();
        }
    }

    /**
     * caminho alternativo 1 da historia
     * o jogador enfrenta um diabrete e pode encontrar um item especial no bau
     */
    private static void caminhoEsquerda() {
        System.out.println("\n--- CORREDOR DA ESQUERDA ---");
        System.out.println("Você encontra um pequeno Diabrete mexendo em um baú!");
        
        Inimigo diabrete = new Inimigo("Diabrete", 40, 8, 5, 1);

        // adiciona itens ao inventario do diabrete (loot silencioso)
        diabrete.getInventario().adicionar(new Item("Mana Potion", "Recupera MP", "mana", 1), true);
        diabrete.getInventario().adicionar(new Item("Poção de Cura", "Cura 30 HP", "cura", 1), true);

        // combate
        batalhar(diabrete);
        
        // se vencer, ganha item do bau
        if (jogador.estaVivo()) {
            System.out.println("\nNo baú que o Diabrete protegia, você encontra um Amuleto!");
            jogador.getInventario().adicionar(new Item("Amuleto Sagrado", "Aumenta DEF (3 rodadas)", "buff_def", 1), false);
            esperarEnter();
        }
    }

    /**
     * caminho alternativo 2 da historia
     * o jogador e emboscado por duas criaturas seguidas
     */
    private static void caminhoDireita() {
        System.out.println("\n--- CORREDOR DA DIREITA ---");
        System.out.println("Você cai em uma emboscada! Duas Larvas Infernais atacam.");
        
        // primeira batalha
        Inimigo larva1 = new Inimigo("Larva Infernal A", 25, 5, 2, 1);
        batalhar(larva1);

        // se sobreviver, segunda batalha
        if (jogador.estaVivo()) {
            System.out.println("A segunda larva avança!");
            Inimigo larva2 = new Inimigo("Larva Infernal B", 25, 5, 2, 1);
            batalhar(larva2);
        }
        esperarEnter();
    }

    /**
     * cena final antes da batalha contra o chefe
     * contem um sistema completo de save point, permitindo varias tentativas
     */
    private static void cenaChefe() {
        limparTela();
        System.out.println("=== CENA 3: O ALTAR PROFANO ===");
        System.out.println("Você chega ao coração da cripta.");
        System.out.println("Você sente uma energia estranha no local.");

        // criacao do save point
        System.out.println(" > JOGO SALVO: Um checkpoint foi criado antes da batalha final.");
        Personagem saveState = clonarPersonagem(jogador);

        boolean loopBatalha = true;

        while (loopBatalha) {
            System.out.println("\nUm demônio maior, um Malignus, está abrindo um portal!");
            System.out.println("CHEFE: 'Você chegou tarde, mortal!'");

            // chefe e recriado a cada tentativa (vida cheia)
            Inimigo chefe = new Inimigo("Malignus, o Devastador", 120, 12, 10, 2);

            // loot silencioso
            chefe.getInventario().adicionar(new Item("Chifre de Malignus", "Troféu", "trofeu", 1), true);

            // batalha final
            batalhar(chefe);

            // verificacoes pos-batalha
            if (jogador.estaVivo() && !chefe.estaVivo()) {
                // vitoria
                System.out.println("\n*** VITÓRIA! ***");
                System.out.println("O portal se fecha e o vilarejo está salvo!");
                System.out.println("FIM DE JOGO.");
                loopBatalha = false;

            } else if (jogador.estaVivo() && chefe.estaVivo()) {
                // jogador fugiu
                System.out.println("\n*** FUGA! ***");
                System.out.println("Você abandona o combate e o vilarejo é destruído.");
                System.out.println("FIM DE JOGO (Final Ruim).");
                loopBatalha = false;

            } else {
                // jogador morreu (derrota)
                System.out.println("\n--- VOCÊ CAIU EM COMBATE ---");
                System.out.println("Deseja tentar novamente usando o Save Point?");
                System.out.println("[1] Sim");
                System.out.println("[2] Não");

                String opcao = scanner.nextLine();

                if (opcao.equals("1")) {
                    System.out.println("\n... O tempo volta ...");
                    // restaura o jogador com tudo que tinha no save
                    jogador = clonarPersonagem(saveState);
                    System.out.println("Você retorna ao início do altar!");
                    esperarEnter();

                } else {
                    gameOver();
                    loopBatalha = false;
                }
            }
        }
    }

    // sistema de combate (interface com as classes)

    /**
     * invoca o sistema de batalha presente nas subclasses do jogador
     * este metodo lida com o pos-combate: loot e level up
     */
    private static void batalhar(Inimigo inimigo) {

        // o combate real acontece dentro das classes do jogador (polimorfismo)
        jogador.batalhar(inimigo, scanner); 
        
        // caso vencido, realiza saque + level up
        if (jogador.estaVivo() && !inimigo.estaVivo()) {
            System.out.println("\nVocê vasculha os restos de " + inimigo.getNome() + "...");
            jogador.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());

            // level up automatico ao vencer
            System.out.println("A experiência te fortalece...");
            jogador.subirNivel();
        }
    }

    // metodos auxiliares (tela, input e save)

    /**
     * le uma opcao numerica do jogador, garantindo que esteja dentro do limite
     */
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

    /**
     * pausa a tela ate o jogador pressionar enter
     */
    private static void esperarEnter() {
        System.out.println("\n(Pressione ENTER para continuar...)");
        scanner.nextLine();
    }

    /**
     * simula limpar a tela imprimindo varias linhas em branco
     */
    private static void limparTela() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * exibe o cabecalho inicial antes de comecar o jogo
     */
    private static void imprimirCabecalho() {
        System.out.println("################################");
        System.out.println("#      A MANCHA DE HALED       #");
        System.out.println("#      RPG de Texto em Java    #");
        System.out.println("################################");
        System.out.println("Criado por: Lucas Berti");
        esperarEnter();
    }

    /**
     * mensagem de fim de jogo padrao
     */
    private static void gameOver() {
        System.out.println("\n=== GAME OVER ===");
        System.out.println("Seu herói caiu em combate. O vilarejo foi consumido pelas trevas.");
    }

    /**
     * cria uma copia exata do personagem para o save point
     * a instancia correta e recriada com base na classe original
     */
    private static Personagem clonarPersonagem(Personagem original) {
        if (original instanceof Guerreiro) {
            return new Guerreiro((Guerreiro) original);
        } else if (original instanceof Mago) {
            return new Mago((Mago) original);
        } else if (original instanceof Arqueiro) {
            return new Arqueiro((Arqueiro) original);
        }
        return null; // nao deve acontecer
    }
}