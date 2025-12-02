import java.util.Random;
import java.util.Scanner;

// classe concreta que representa o arqueiro -- personagem focado em distancia e evasao
// possui uma habilidade especial baseada em invulnerabilidade temporaria
public class Arqueiro extends Personagem {

    // construtor principal
    // cria um arqueiro novo com status base pre-definidos
    public Arqueiro(String nome) {
        super(
                nome,   // nome vindo do jogador
                100,    // hp medio
                14,     // atk medio
                10,     // def baixa
                1       // nivel inicial
        );

        // itens iniciais poderiam ser adicionados aqui (mantido comentado)
        // Item besta = new Item("Besta Leve", "Uma besta confiavel para cacar demonios", "ataque", 1);
        // this.inventario.adicionar(besta);
    }

    // construtor de copia
    // necessario para sistema de save point
    // cria um novo arqueiro copiando exatamente outro existente
    public Arqueiro(Arqueiro outroArqueiro) {

        // copia atributos base chamando o construtor da classe-mae
        super(
                outroArqueiro.nome,
                outroArqueiro.pontosVida,
                outroArqueiro.ataque,
                outroArqueiro.defesa,
                outroArqueiro.nivel
        );

        // clona o inventario para manter os itens mas evitar referencia compartilhada
        this.inventario = outroArqueiro.inventario.clone();
    }

    // descricao da habilidade especial
    // texto mostrado no menu de batalha
    @Override
    public String getDescricaoHabilidade() {
        return "Passo Sombrio (70 MP)";
    }

    // habilidade especial do arqueiro
    // passo sombrio: torna o arqueiro invulneravel por 2 turnos e ainda da um ataque gratis
    @Override
    public boolean usarHabilidadeEspecial(Inimigo inimigo) {

        int custo = 70;

        // verifica se o arqueiro tem mana suficiente
        if (this.mp >= custo) {

            // consome a mana necessaria
            this.gastarMana(custo);

            // ativa o estado de invulnerabilidade por 2 turnos
            this.turnosInvulneravel = 2;

            System.out.println("\n> SKILL: PASSO SOMBRIO!");
            System.out.println("Você recua para as sombras e ataca de longe. O inimigo não consegue te alcançar!");

            // ataque bonus imediato enquanto esta seguro
            System.out.println("Você aproveita a distância e dispara!");
            this.atacar(inimigo);

            return true;

        } else {
            System.out.println("> Mana insuficiente! (Precisa de " + custo + ")");
            return false; // turno nao passa
        }
    }

    // sistema principal de combate do arqueiro
    // controla todas as acoes do jogador e do inimigo
    @Override
    public void batalhar(Inimigo inimigo, Scanner scanner) {

        Random dado = new Random();

        // tela inicial do combate
        System.out.println("========================================");
        System.out.println("   COMBATE INICIADO: " + this.getNome() + " vs " + inimigo.getNome());
        System.out.println("========================================");

        // loop da batalha (continua ate alguem morrer)
        while (this.estaVivo() && inimigo.estaVivo()) {

            // mostrar status atualizado
            // com hp, mp, buffs ativos e atributos atuais
            System.out.println("\n----------------------------------------");

            // monta texto informando buffs ativos
            String statusBuffs = "";
            if (this.turnosBuffAtaque > 0)
                statusBuffs += " [ATK UP: " + this.turnosBuffAtaque + "t]";
            if (this.turnosBuffDefesa > 0)
                statusBuffs += " [DEF UP: " + this.turnosBuffDefesa + "t]";

            // exibicao do jogador
            System.out.println(this.getNome().toUpperCase() + statusBuffs);
            System.out.println("HP: " + this.getPontosVida() + " | MP: " + this.getMp());
            System.out.println("ATK: " + this.getAtaque() + " | DEF: " + this.getDefesa());

            // exibicao do inimigo
            System.out.println("\nVS\n");
            System.out.println(inimigo.getNome().toUpperCase());
            System.out.println("HP: " + inimigo.getPontosVida() +
                               " | ATK: " + inimigo.getAtaque() +
                               " | DEF: " + inimigo.getDefesa());
            System.out.println("----------------------------------------");

            // menu do turno do jogador
            System.out.println("Sua vez! Escolha uma ação:");
            System.out.println("[1] Atacar");
            System.out.println("[2] " + this.getDescricaoHabilidade());
            System.out.println("[3] Usar Poção de Cura (Atalho)");
            System.out.println("[4] Usar Item do Inventário");
            System.out.println("[5] Tentar Fugir");
            System.out.print(">> ");

            String escolha = scanner.nextLine();
            boolean turnoPassou = false; // controla se o inimigo vai jogar depois

            // acoes do jogador
            if (escolha.equals("1")) {

                // ataque basico
                System.out.println("\n> Você mira com sua Besta Leve e atira um virote!");
                this.atacar(inimigo);
                turnoPassou = true;

            } else if (escolha.equals("2")) {

                // habilidade especial
                boolean usouSkill = this.usarHabilidadeEspecial(inimigo);
                turnoPassou = usouSkill;

            } else if (escolha.equals("3")) {

                // atalho rapido de pocao
                if (this.getInventario().temItem("Poção de Cura")) {
                    this.getInventario().remover("Poção de Cura");
                    this.curar(30);
                    System.out.println("> Você bebeu uma Poção de Cura.");
                    turnoPassou = true;
                } else {
                    System.out.println("\n> Sem poções no atalho!");
                }

            } else if (escolha.equals("4")) {

                // menu de inventario
                // lista itens e permite usar 1 deles
                System.out.println("\n--- SEU INVENTÁRIO ---");

                java.util.ArrayList<Item> lista = this.getInventario().getListaItens();

                if (lista.isEmpty()) {
                    System.out.println("(Vazio)");
                } else {

                    // mostra itens numerados
                    for (int i = 0; i < lista.size(); i++) {
                        Item item = lista.get(i);
                        System.out.println("[" + (i + 1) + "] " +
                                item.getNome() + " (" + item.getQuantidade() + "x) - " +
                                item.getDescricao());
                    }

                    System.out.println("[0] Cancelar");
                    System.out.print("Escolha o item: ");

                    try {
                        int index = Integer.parseInt(scanner.nextLine()) - 1;

                        if (index >= 0 && index < lista.size()) {
                            Item itemEscolhido = lista.get(index);

                            // usarItemDoInventario() retorna se o turno deve passar
                            turnoPassou = this.usarItemDoInventario(itemEscolhido, inimigo);
                        } else {
                            System.out.println("> Cancelado.");
                        }

                    } catch (Exception e) {
                        System.out.println("> Opção inválida.");
                    }
                }

            } else if (escolha.equals("5")) {

                // fuga
                // sucesso em rolagem 3,4,5 ou 6
                System.out.println("\n> Você tenta correr...");
                int rolagem = dado.nextInt(6) + 1;

                if (rolagem >= 3) {
                    System.out.println("> SUCESSO! Você escapou.");
                    return;
                } else {
                    System.out.println("> FALHA! O inimigo bloqueou você!");
                    turnoPassou = true;
                }

            } else {
                System.out.println("Opção inválida.");
            }

            // checagem de vitoria do jogador
            if (!inimigo.estaVivo()) {
                System.out.println("\n****************************************");
                System.out.println("   VITÓRIA! O inimigo foi derrotado!");
                System.out.println("****************************************");
                System.out.println("Você saqueia o corpo do inimigo...");
                this.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());
                break;
            }

            // turno do inimigo
            // so ocorre se o jogador realmente fez uma acao valida
            if (turnoPassou && inimigo.estaVivo()) {
                System.out.println("\n> Vez do " + inimigo.getNome() + "...");
                try { Thread.sleep(1000); } catch (Exception e) {}
                inimigo.atacar(this);
            }

            // checagem de derrota do jogador
            if (!this.estaVivo()) {
                System.out.println("\nVOCÊ MORREU! O destino de Haled está selado...");
                break;
            }

            // atualiza buffs (reduz turnos de atk up, def up, ou invulnerabilidade)
            this.atualizarBuffs();
        }
    }
}