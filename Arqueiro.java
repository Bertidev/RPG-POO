import java.util.Random;
import java.util.Scanner;

public class Arqueiro extends Personagem {

    // CONSTRUTOR PRINCIPAL
    public Arqueiro(String nome) {
        super(
                nome, // nome
                100, // medio
                14, // medio
                10, // baixa
                1);

        // adiciona itens iniciais especificos do arqueiro
        // Item besta = new Item("Besta Leve", "Uma besta confiável para caçar
        // demônios", "ataque", 1);
        // this.inventario.adicionar(besta);
    }

    // CONSTRUTOR DE COPIA
    // cria uma copia exata de outro arqueiro, requisito para save point
    public Arqueiro(Arqueiro outroArqueiro) {
        // copia os atributos base chamando o construtor super
        super(
                outroArqueiro.nome,
                outroArqueiro.pontosVida,
                outroArqueiro.ataque,
                outroArqueiro.defesa,
                outroArqueiro.nivel);

        // clona o inventario
        this.inventario = outroArqueiro.inventario.clone();
    }

    // METODO DE COMBATE
    @Override
    public String getDescricaoHabilidade() {
        return "Égide Ofensiva (60 MP)";
    }

    @Override
    public boolean usarHabilidadeEspecial(Inimigo inimigo) {
        int custo = 70;
        if (this.mp >= custo) {
            this.gastarMana(custo);
            this.turnosInvulneravel = 2;
            System.out.println("\n> SKILL: PASSO SOMBRIO!");
            System.out.println("Você recua para as sombras e ataca de longe. O inimigo não consegue te alcançar!");

            // Opcional: Já dá um ataque grátis junto
            System.out.println("Você aproveita a distância e dispara!");
            this.atacar(inimigo);

            return true;
        } else {
            System.out.println("> Mana insuficiente! (Precisa de " + custo + ")");
            return false;
        }
    }

    @Override
    public void batalhar(Inimigo inimigo, Scanner scanner) {
        Random dado = new Random();

        System.out.println("========================================");
        System.out.println("   COMBATE INICIADO: " + this.getNome() + " vs " + inimigo.getNome());
        System.out.println("========================================");

        while (this.estaVivo() && inimigo.estaVivo()) {

            // --- MOSTRAR STATUS (ATUALIZADO) ---
            System.out.println("\n----------------------------------------");

            // Lógica para montar a string de status do Jogador
            String statusBuffs = "";
            if (this.turnosBuffAtaque > 0)
                statusBuffs += " [ATK UP: " + this.turnosBuffAtaque + "t]";
            if (this.turnosBuffDefesa > 0)
                statusBuffs += " [DEF UP: " + this.turnosBuffDefesa + "t]";

            System.out.println(this.getNome().toUpperCase() + statusBuffs);
            System.out.println("HP: " + this.getPontosVida() + " | MP: " + this.getMp());
            // Aqui mostramos o Ataque/Defesa atuais (já calculados com o buff)
            System.out.println("ATK: " + this.getAtaque() + " | DEF: " + this.getDefesa());

            System.out.println("\nVS");

            System.out.println("\n" + inimigo.getNome().toUpperCase());
            System.out.println("HP: " + inimigo.getPontosVida() + " | ATK: " + inimigo.getAtaque() + " | DEF: "
                    + inimigo.getDefesa());
            System.out.println("----------------------------------------");

            System.out.println("Sua vez! Escolha uma ação:");
            System.out.println("[1] Atacar");
            System.out.println("[2] " + this.getDescricaoHabilidade());
            System.out.println("[3] Usar Poção de Cura (Atalho)");
            System.out.println("[4] Usar Item do Inventário");
            System.out.println("[5] Tentar Fugir");
            System.out.print(">> ");

            String escolha = scanner.nextLine();
            boolean turnoPassou = false; // Começa falso, só vira true se fizer uma ação válida

            if (escolha.equals("1")) {
                // ATACAR
                System.out.println("\n> Você mira com sua Besta Leve e atira um virote!");
                this.atacar(inimigo);
                turnoPassou = true;

            } else if (escolha.equals("2")) {
                // HABILIDADE ESPECIAL
                // Chama o método abstrato que implementamos em cada classe
                boolean usou = this.usarHabilidadeEspecial(inimigo);
                if (usou) {
                    turnoPassou = true;
                } else {
                    turnoPassou = false; // Se não tinha mana, não perde a vez
                }

            } else if (escolha.equals("3")) {
                // ATALHO POÇÃO
                if (this.getInventario().temItem("Poção de Cura")) {
                    this.getInventario().remover("Poção de Cura");
                    this.curar(30);
                    System.out.println("> Você bebeu uma Poção de Cura.");
                    turnoPassou = true;
                } else {
                    System.out.println("\n> Sem poções no atalho!");
                }

            } else if (escolha.equals("4")) {
                // --- NOVA LÓGICA: LISTAR INVENTÁRIO ---
                System.out.println("\n--- SEU INVENTÁRIO ---");
                // Pega a lista numerada
                java.util.ArrayList<Item> lista = this.getInventario().getListaItens();

                if (lista.isEmpty()) {
                    System.out.println("(Vazio)");
                } else {
                    // Loop para mostrar: 1. Nome (Qtd) - Descrição
                    for (int i = 0; i < lista.size(); i++) {
                        Item item = lista.get(i);
                        System.out.println("[" + (i + 1) + "] " + item.getNome() + " (" + item.getQuantidade() + "x) - "
                                + item.getDescricao());
                    }
                    System.out.println("[0] Cancelar");
                    System.out.print("Escolha o item: ");

                    try {
                        int index = Integer.parseInt(scanner.nextLine()) - 1;
                        if (index >= 0 && index < lista.size()) {
                            Item itemEscolhido = lista.get(index);
                            // Chama o método que criamos no Passo 2
                            // Se o método retornar true, o turno passa. Se false (item inútil), não passa.
                            turnoPassou = this.usarItemDoInventario(itemEscolhido, inimigo);
                        } else {
                            System.out.println("> Cancelado.");
                        }
                    } catch (Exception e) {
                        System.out.println("> Opção inválida.");
                    }
                }

            } else if (escolha.equals("5")) {
                // FUGIR
                System.out.println("\n> Você tenta correr...");
                int rolagemFuga = dado.nextInt(6) + 1;
                if (rolagemFuga >= 3) {
                    System.out.println("> SUCESSO! Você escapou.");
                    return;
                } else {
                    System.out.println("> FALHA! O inimigo bloqueou você!");
                    turnoPassou = true;
                }

            } else {
                System.out.println("Opção inválida.");
            }

            // CHECAGEM DE VITÓRIA
            if (!inimigo.estaVivo()) {
                System.out.println("\n****************************************");
                System.out.println("   VITÓRIA! O inimigo foi derrotado!");
                System.out.println("****************************************");
                System.out.println("Você saqueia o corpo do inimigo...");
                this.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());
                break;
            }

            // TURNO DO INIMIGO
            if (turnoPassou && inimigo.estaVivo()) {
                System.out.println("\n> Vez do " + inimigo.getNome() + "...");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                inimigo.atacar(this);
            }

            // CHECAGEM DE DERROTA
            if (!this.estaVivo()) {
                System.out.println("\nVOCÊ MORREU! O destino de Haled está selado...");
                break;
            }

            this.atualizarBuffs();
        }
    }
}