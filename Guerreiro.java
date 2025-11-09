//guerreiro, HP e defesa altas, ataque baixo
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
    public void batalhar(Inimigo inimigo) {
        System.out.println("--- BATALHA INICIADA ---");
        System.out.println(this.nome + " (HP: " + this.pontosVida + ") vs. " + inimigo.getNome() + " (HP: " + inimigo.getPontosVida() + ")");
        System.out.println("------------------------");

        //loop de combate continua enquanto ambos estiverem vivos
        while (this.estaVivo() && inimigo.estaVivo()) {

            //TURNO DO JOGADOR
            //vou adicionar o menu para escolher entre atacar item e fugir
            
            //por enquanto apenas atacar
            System.out.println("\n--- Turno do Jogador ---");
            this.atacar(inimigo);
            
            //verificando se o inimigo morreu depois do ataque
            if (!inimigo.estaVivo()) {
                System.out.println(inimigo.getNome() + " foi derrotado!");
                break; // Sai do loop
            }

            //TURNO DO INIMIGO
            System.out.println("\n--- Turno do Inimigo ---");
            inimigo.atacar(this); 

            //verificando se o jogador morreu depois do ataque
            if (!this.estaVivo()) {
                System.out.println(this.nome + " foi derrotado! FIM DE JOGO.");
                break; //sai do loop
            }
            
            //mostra a vida no final da rodada
            System.out.println("\n--- Fim da Rodada ---");
            System.out.println("HP de " + this.nome + ": " + this.pontosVida);
            System.out.println("HP de " + inimigo.getNome() + ": " + inimigo.getPontosVida());
            System.out.println("---------------------");
            
        }

        System.out.println("--- BATALHA ENCERRADA ---");
    }
}