import java.util.Scanner;

public class Inimigo extends Personagem {

    public Inimigo(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        prepararLoot();
    }

    private void prepararLoot() {
        if (Math.random() < 0.5) {
            Item loot = new Item("Poção de Cura", "Restaura 30 HP", "cura", 1);
            // CORREÇÃO AQUI: Adicionar ', true' para ser silencioso
            this.inventario.adicionar(loot, true); 
        }
        
        // CORREÇÃO AQUI: Adicionar ', true' também
        Item lootObrigatorio = new Item("Essência Demoníaca", "Aumenta ATK (2 rodadas)", "buff_atk", 1);
        this.inventario.adicionar(lootObrigatorio, true);
    }

    @Override
    public boolean usarHabilidadeEspecial(Inimigo inimigo) {
        return false; // Inimigos não usam mana neste sistema simples
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Ataque Especial"; // Inimigo não tem menu, mas precisa ter o método
    }

    @Override
    public void batalhar(Inimigo inimigo, Scanner scanner) {
        System.out.println("O inimigo ruge, mas não inicia batalhas.");
    }
}