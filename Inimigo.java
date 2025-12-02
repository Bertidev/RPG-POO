import java.util.Scanner;

// classe que representa qualquer inimigo generico do jogo
// herda de Personagem e reutiliza toda a logica base
public class Inimigo extends Personagem {

    // construtor do inimigo
    // aqui configuramos stats iniciais e ja preparamos o loot
    public Inimigo(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        prepararLoot(); // inimigo sempre cria os itens que podera dropar ao morrer
    }

    // metodo responsavel por gerar os itens do inventario do inimigo (loot)
    private void prepararLoot() {

        // 50% de chance de vir uma pocao de cura
        if (Math.random() < 0.5) {
            Item loot = new Item("Poção de Cura", "Restaura 30 HP", "cura", 1);

            // adiciona ao inventario silenciosamente
            // o 'true' impede que apareca mensagem ao jogador
            this.inventario.adicionar(loot, true);
        }
        
        // todo inimigo sempre carrega uma Essencia Demoniaca
        Item lootObrigatorio = new Item("Essência Demoníaca",
                                        "Aumenta ATK (2 rodadas)",
                                        "buff_atk",
                                        1);

        // tambem adicionada silenciosamente
        this.inventario.adicionar(lootObrigatorio, true);
    }

    // inimigos nao usam mana nem habilidades especiais nesse sistema simplificado
    @Override
    public boolean usarHabilidadeEspecial(Inimigo inimigo) {
        return false;
    }

    // descricao generica para cumprir contrato da classe base
    @Override
    public String getDescricaoHabilidade() {
        return "Ataque Especial";
    }

    // inimigos nao iniciam batalhas por conta propria
    @Override
    public void batalhar(Inimigo inimigo, Scanner scanner) {
        System.out.println("O inimigo ruge, mas não inicia batalhas.");
    }
}