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
        //Item espada = new Item("Espada Longa", "Uma espada básica de ferro", "ataque", 1);
        //this.inventario.adicionar(espada);
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
    @Override
    public boolean usarHabilidadeEspecial(Inimigo inimigo) {
        int custo = 60;
        if (this.mp >= custo) {
            this.gastarMana(custo);
            this.turnosSkillGuerreiro = 2; // Dura 2 turnos
            System.out.println("\n> SKILL: ÉGIDE OFENSIVA!");
            System.out.println("Você canaliza sua fé na armadura. Sua DEFESA é somada ao seu ATAQUE!");
            return true;
        } else {
            System.out.println("> Mana insuficiente! (Precisa de " + custo + ")");
            return false;
        }
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Égide Ofensiva (60 MP)";
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
            if (this.turnosBuffAtaque > 0) statusBuffs += " [ATK UP: " + this.turnosBuffAtaque + "t]";
            if (this.turnosBuffDefesa > 0) statusBuffs += " [DEF UP: " + this.turnosBuffDefesa + "t]";
            
            System.out.println(this.getNome().toUpperCase() + statusBuffs);
            System.out.println("HP: " + this.getPontosVida() + " | MP: " + this.getMp()); 
            // Aqui mostramos o Ataque/Defesa atuais (já calculados com o buff)
            System.out.println("ATK: " + this.getAtaque() + " | DEF: " + this.getDefesa());
            
            System.out.println("\nVS");
            
            System.out.println("\n" + inimigo.getNome().toUpperCase());
            System.out.println("HP: " + inimigo.getPontosVida() + " | ATK: " + inimigo.getAtaque() + " | DEF: " + inimigo.getDefesa());
            System.out.println("----------------------------------------");
            
            System.out.println("Sua vez! Escolha uma ação:");
            System.out.println("[1] Atacar");
            System.out.println("[2] " + this.getDescricaoHabilidade());
            System.out.println("[3] Usar Poção de Cura (Atalho)");
            System.out.println("[4] Usar Item do Inventário"); // NOVA OPÇÃO
            System.out.println("[5] Tentar Fugir");
            System.out.print(">> ");

            String escolha = scanner.nextLine();
            boolean turnoPassou = false; // Começa falso, só vira true se fizer uma ação válida

            if (escolha.equals("1")) {
                // ATACAR
                System.out.println("\n> Você empunha sua Espada Longa e desfere um golpe pesado!");                
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
                        System.out.println("[" + (i + 1) + "] " + item.getNome() + " (" + item.getQuantidade() + "x) - " + item.getDescricao());
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
                try { Thread.sleep(1000); } catch (Exception e) {} 
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