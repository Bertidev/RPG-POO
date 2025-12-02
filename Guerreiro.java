// classe especifica do guerreiro
// foco: alta vida e alta defesa, porem ataque base mais baixo que outras classes

import java.util.Random;
import java.util.Scanner;

public class Guerreiro extends Personagem {

    // construtor principal
    // inicializa um guerreiro padrao com atributos altos de vida e defesa
    public Guerreiro(String nome) {
        super(
            nome,  
            150,    // hp alto -- classe tanque
            10,     // ataque medio/baixo
            15,     // defesa alta
            1       // nivel inicial
        );
        
        // itens iniciais (comentados pois talvez ative futuramente)
        // Item espada = new Item("Espada Longa", "Uma espada básica de ferro", "ataque", 1);
        // this.inventario.adicionar(espada);
    }

    // construtor de copia
    // cria um novo guerreiro duplicando todos os atributos de outro -- usado para save point
    public Guerreiro(Guerreiro outroGuerreiro) {
        super(
            outroGuerreiro.nome,
            outroGuerreiro.pontosVida,
            outroGuerreiro.ataque,
            outroGuerreiro.defesa,
            outroGuerreiro.nivel
        );
        
        // clona o inventario inteiro
        this.inventario = outroGuerreiro.inventario.clone();
    }

    // habilidade especial do guerreiro
    // egide ofensiva -- converte a def em atk temporariamente (buff de 2 turnos)
    @Override
    public boolean usarHabilidadeEspecial(Inimigo inimigo) {
        int custo = 60;

        // verifica mana suficiente
        if (this.mp >= custo) {
            this.gastarMana(custo);

            // habilidade ativa por 2 turnos
            this.turnosSkillGuerreiro = 2;

            System.out.println("\n> SKILL: ÉGIDE OFENSIVA!");
            System.out.println("Você canaliza sua fé na armadura. Sua DEFESA é somada ao seu ATAQUE!");

            return true;

        } else {
            System.out.println("> Mana insuficiente! (Precisa de " + custo + ")");
            return false;
        }
    }

    // descricao usada no menu da batalha
    @Override
    public String getDescricaoHabilidade() {
        return "Égide Ofensiva (60 MP)";
    }

    // sistema de combate do guerreiro
    @Override
    public void batalhar(Inimigo inimigo, Scanner scanner) {

        Random dado = new Random();

        System.out.println("========================================");
        System.out.println("   COMBATE INICIADO: " + this.getNome() + " vs " + inimigo.getNome());
        System.out.println("========================================");

        // laco principal da batalha -- continua enquanto ambos estiverem vivos
        while (this.estaVivo() && inimigo.estaVivo()) {
            
            // mostrar status do turno
            System.out.println("\n----------------------------------------");

            // mostra estado dos buffs ativos, se existirem
            String statusBuffs = "";
            if (this.turnosBuffAtaque > 0) statusBuffs += " [ATK UP: " + this.turnosBuffAtaque + "t]";
            if (this.turnosBuffDefesa > 0) statusBuffs += " [DEF UP: " + this.turnosBuffDefesa + "t]";
            
            // status do jogador
            System.out.println(this.getNome().toUpperCase() + statusBuffs);
            System.out.println("HP: " + this.getPontosVida() + " | MP: " + this.getMp());
            System.out.println("ATK: " + this.getAtaque() + " | DEF: " + this.getDefesa());

            System.out.println("\nVS\n");

            // status do inimigo
            System.out.println(inimigo.getNome().toUpperCase());
            System.out.println("HP: " + inimigo.getPontosVida()
                + " | ATK: " + inimigo.getAtaque()
                + " | DEF: " + inimigo.getDefesa());
            System.out.println("----------------------------------------");

            // menu principal
            System.out.println("Sua vez! Escolha uma ação:");
            System.out.println("[1] Atacar");
            System.out.println("[2] " + this.getDescricaoHabilidade());
            System.out.println("[3] Usar Poção de Cura (Atalho)");
            System.out.println("[4] Usar Item do Inventário");
            System.out.println("[5] Tentar Fugir");
            System.out.print(">> ");

            String escolha = scanner.nextLine();
            boolean turnoPassou = false; // so vira true quando o jogador faz uma acao valida

            // acoes do jogador

            if (escolha.equals("1")) {
                // ataque basico do guerreiro
                System.out.println("\n> Você empunha sua Espada Longa e desfere um golpe pesado!");                
                this.atacar(inimigo);
                turnoPassou = true;

            } else if (escolha.equals("2")) {
                // habilidade especial
                boolean usou = this.usarHabilidadeEspecial(inimigo);
                if (usou) turnoPassou = true;

            } else if (escolha.equals("3")) {
                // atalho para usar pocao de cura rapidamente
                if (this.getInventario().temItem("Poção de Cura")) {
                    this.getInventario().remover("Poção de Cura");
                    this.curar(30);
                    System.out.println("> Você bebeu uma Poção de Cura.");
                    turnoPassou = true;
                } else {
                    System.out.println("\n> Sem poções no atalho!");
                }

            } else if (escolha.equals("4")) {
                // interacao completa com itens do inventario
                System.out.println("\n--- SEU INVENTÁRIO ---");

                java.util.ArrayList<Item> lista = this.getInventario().getListaItens();

                if (lista.isEmpty()) {
                    System.out.println("(Vazio)");
                } else {
                    // lista todos os itens com indice
                    for (int i = 0; i < lista.size(); i++) {
                        Item item = lista.get(i);
                        System.out.println("[" + (i + 1) + "] " + item.getNome() 
                            + " (" + item.getQuantidade() + "x) - " + item.getDescricao());
                    }

                    System.out.println("[0] Cancelar");
                    System.out.print("Escolha o item: ");

                    try {
                        int index = Integer.parseInt(scanner.nextLine()) - 1;

                        if (index >= 0 && index < lista.size()) {
                            Item itemEscolhido = lista.get(index);
                            // usar item retorna true -> turno passa
                            // retorna false -> item nao interfere, turno nao passa
                            turnoPassou = this.usarItemDoInventario(itemEscolhido, inimigo);
                        } else {
                            System.out.println("> Cancelado.");
                        }

                    } catch (Exception e) {
                        System.out.println("> Opção inválida.");
                    }
                }

            } else if (escolha.equals("5")) {
                // tentativa de fuga -- rolagem simples de dado
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

            // checagem de vitoria
            if (!inimigo.estaVivo()) {
                System.out.println("\n****************************************");
                System.out.println("   VITÓRIA! O inimigo foi derrotado!");
                System.out.println("****************************************");
                System.out.println("Você saqueia o corpo do inimigo...");
                this.getInventario().adicionarItensDoInimigo(inimigo.getInventario().clone());
                break;
            }

            // turno do inimigo
            if (turnoPassou && inimigo.estaVivo()) {
                System.out.println("\n> Vez do " + inimigo.getNome() + "...");

                try { Thread.sleep(1000); } 
                catch (Exception e) {}

                inimigo.atacar(this);
            }
            
            // checagem de derrota
            if (!this.estaVivo()) {
                System.out.println("\nVOCÊ MORREU! O destino de Haled está selado...");
                break;
            }

            // atualiza buffs ativos (reduz duracao dos turnos)
            this.atualizarBuffs();
        }
    }
}