public class Mago extends Personagem {

    //CONSTRUTOR PRINCIPAL
    public Mago(String nome) {
        super(
            nome,   
            80,     //baixo
            18,     //alto
            8,      //baixo
            1      
        );
        
        //adiciona itens iniciais especificos do mago
        Item grimorio = new Item("Grimório Simples", "Contém encantamentos básicos", "magia", 1);
        this.inventario.adicionar(grimorio);
    }

    //CONSTRUTOR DE COPIA
    //cria uma copia exata de outro mago, requisito para save point
    public Mago(Mago outroMago) {
        //copia os atributos base chamando o construtor super
        super(
            outroMago.nome,
            outroMago.pontosVida,
            outroMago.ataque,
            outroMago.defesa,
            outroMago.nivel
        );
        
        //clona o inventario
        this.inventario = outroMago.inventario.clone();
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