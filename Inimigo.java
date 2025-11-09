public class Inimigo extends Personagem {

    //CONSTRUTOR
    public Inimigo(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        //chama o construtor da superclasse (personagem) para configurar os atributos
        super(nome, pontosVida, ataque, defesa, nivel);

        prepararLoot();
    }

    //loot table dos inimigos
    private void prepararLoot() {
        //50% de chance de dropar uma pocao de cura
        if (Math.random() > 0.5) {
            Item loot = new Item("Poção de Cura", "Restaura 20 HP", "cura", 1);
            this.inventario.adicionar(loot);
        }
        
        //drop obrigatorio
        Item lootObrigatorio = new Item("Essência Demoníaca", "Um item para missões", "missao", 1);
        this.inventario.adicionar(lootObrigatorio);
    }
}