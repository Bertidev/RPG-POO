import java.util.Random;
import java.util.Scanner;

// classe concreta que representa o personagem mago do jogador
public class Mago extends Personagem {

    // construtor principal
    // cria um mago padrao com atributos especificos
    public Mago(String nome) {
        super(
            nome,   
            80,     // vida baixa
            18,     // ataque magico alto
            8,      // defesa baixa
            1       // nivel inicial
        );
        
        // adiciona itens iniciais especificos do mago (removido por enquanto)
        // Item grimorio = new Item("Grimório Simples", "Contém encantamentos básicos", "magia", 1);
        // this.inventario.adicionar(grimorio);
    }

    // construtor de copia
    // cria uma copia do mago original -- usado para mecanica de save point
    public Mago(Mago outroMago) {
        // copia atributos chamando o construtor da classe mae
        super(
            outroMago.nome,
            outroMago.pontosVida,
            outroMago.ataque,
            outroMago.defesa,
            outroMago.nivel
        );
        
        // clona o inventario inteiro do outro mago
        this.inventario = outroMago.inventario.clone();
    }

    // metodo de combate -- habilidade especial do mago
    @Override
    public boolean usarHabilidadeEspecial(Inimigo inimigo) {
        int custo = 45;   // mp necessario para usar a habilidade

        // verifica se o jogador tem mana suficiente
        if (this.mp >= custo) {
            this.gastarMana(custo); // desconta mp
            System.out.println("\n> SKILL: EXORCISMO SUPREMO!");

            // calculo de dano: ataque * 1.5 + rolagem de d20
            int danoSkill = (int)(this.getAtaque() * 1.5) + new java.util.Random().nextInt(20);
            
            System.out.println("Um raio de luz pura atinge o inimigo causando " + danoSkill + " de dano!");

            inimigo.receberDano(danoSkill);
            return true; // turno foi gasto com sucesso
        } else {
            System.out.println("> Mana insuficiente! (Precisa de " + custo + ")");
            return false; // sem mana -> nao perde o turno
        }
    }

    // retorna o nome da habilidade para exibicao no menu de combate
    @Override
    public String getDescricaoHabilidade() {
        return "Exorcismo Supremo (45 MP)";
    }

    // loop de combate do mago contra um inimigo
    @Override
    public void batalhar(Inimigo inimigo, Scanner scanner) {
        Random dado = new Random();

        System.out.println("========================================");
        System.out.println("   COMBATE INICIADO: " + this.getNome() + " vs " + inimigo.getNome());
        System.out.println("========================================");

        // loop roda enquanto ambos estiverem vivos
        while (this.estaVivo() && inimigo.estaVivo()) {
            
            // mostrar status atualizado do combate
            System.out.println("\n----------------------------------------");

            // monta string com buffs ativos (ataque/defesa)
            String statusBuffs = "";
            if (this.turnosBuffAtaque > 0) statusBuffs += " [ATK UP: " + this.turnosBuffAtaque + "t]";
            if (this.turnosBuffDefesa > 0) statusBuffs += " [DEF UP: " + this.turnosBuffDefesa + "t]";
            
            System.out.println(this.getNome().toUpperCase() + statusBuffs);
            System.out.println("HP: " + this.getPontosVida() + " | MP: " + this.getMp()); 
            
            // exibe valores ja modificados pelos buffs temporarios
            System.out.println("ATK: " + this.getAtaque() + " | DEF: " + this.getDefesa());
            
            System.out.println("\nVS");
            
            System.out.println("\n" + inimigo.getNome().toUpperCase());
            System.out.println("HP: " + inimigo.getPontosVida() + " | ATK: " + inimigo.getAtaque() + " | DEF: " + inimigo.getDefesa());
            System.out.println("----------------------------------------");
            
            // menu de acoes do jogador
            System.out.println("Sua vez! Escolha uma ação:");
            System.out.println("[1] Atacar");
            System.out.println("[2] " + this.getDescricaoHabilidade());
            System.out.println("[3] Usar Poção de Cura (Atalho)");
            System.out.println("[4] Usar Item do Inventário"); // acesso ao inventario completo
            System.out.println("[5] Tentar Fugir");
            System.out.print(">> ");

            String escolha = scanner.nextLine();
            boolean turnoPassou = false; // indica se o jogador gastou o turno

            // opcao 1: ataque normal
            if (escolha.equals("1")) {
                System.out.println("\n> Você ergue seu Grimório e dispara um raio de energia!");               
                this.atacar(inimigo);
                turnoPassou = true;

            // opcao 2: habilidade especial
            } else if (escolha.equals("2")) {

                boolean usou = this.usarHabilidadeEspecial(inimigo);

                // so perde o turno se a skill foi executada
                if (usou) turnoPassou = true;

            // opcao 3: atalho de pocao de cura
            } else if (escolha.equals("3")) {

                if (this.getInventario().temItem("Poção de Cura")) {
                    this.getInventario().remover("Poção de Cura");
                    this.curar(30);
                    System.out.println("> Você bebeu uma Poção de Cura.");
                    turnoPassou = true;
                } else {
                    System.out.println("\n> Sem poções no atalho!");
                }

            // opcao 4: abrir inventario
            } else if (escolha.equals("4")) {

                System.out.println("\n--- SEU INVENTÁRIO ---");

                // pega lista de itens numerada
                java.util.ArrayList<Item> lista = this.getInventario().getListaItens();
                
                if (lista.isEmpty()) {
                    System.out.println("(Vazio)");
                } else {

                    // exibe cada item + quantidade + descricao
                    for (int i = 0; i < lista.size(); i++) {
                        Item item = lista.get(i);
                        System.out.println("[" + (i + 1) + "] " + item.getNome() + " (" + item.getQuantidade() + "x) - " + item.getDescricao());
                    }

                    System.out.println("[0] Cancelar");
                    System.out.print("Escolha o item: ");
                    
                    try {
                        int index = Integer.parseInt(scanner.nextLine()) - 1;

                        // se index valido -> usar item
                        if (index >= 0 && index < lista.size()) {
                            Item itemEscolhido = lista.get(index);

                            // retorna true se o item foi usado com sucesso
                            turnoPassou = this.usarItemDoInventario(itemEscolhido, inimigo);

                        } else {
                            System.out.println("> Cancelado.");
                        }

                    } catch (Exception e) {
                        System.out.println("> Opção inválida.");
                    }
                }

            // opcao 5: tentar fugir
            } else if (escolha.equals("5")) {

                System.out.println("\n> Você tenta correr...");
                int rolagemFuga = dado.nextInt(6) + 1;

                if (rolagemFuga >= 3) {
                    System.out.println("> SUCESSO! Você escapou.");
                    return; // fuga encerrada
                } else {
                    System.out.println("> FALHA! O inimigo bloqueou você!");
                    turnoPassou = true; // falha ainda gasta turno
                }

            } else {
                System.out.println("Opção inválida.");
            }

            // checa se o inimigo foi derrotado
            if (!inimigo.estaVivo()) {
                System.out.println("\n****************************************");
                System.out.println("   VITÓRIA! O inimigo foi derrotado!");
                System.out.println("****************************************");
                System.out.println("Você saqueia o corpo do inimigo...");

                // copia loot do inventario do inimigo
                this.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());
                break;
            }

            // turno do inimigo
            if (turnoPassou && inimigo.estaVivo()) {
                System.out.println("\n> Vez do " + inimigo.getNome() + "...");
                try { Thread.sleep(1000); } catch (Exception e) {} 
                inimigo.atacar(this);
            }
            
            // checa se o jogador morreu
            if (!this.estaVivo()) {
                System.out.println("\nVOCÊ MORREU! O destino de Haled está selado...");
                break;
            }

            // atualiza decremento dos buffs temporarios
            this.atualizarBuffs();
        }
    }
}