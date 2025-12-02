import java.util.Random;
import java.util.Scanner;

public abstract class Personagem {

    // ATRIBUTOS
    // protected permite que as classes filhas acessem esses atributos
    protected String nome;
    protected int pontosVida;
    protected int ataque;
    protected int defesa;
    protected int nivel;
    protected Inventario inventario;

    // atributos usados para controlar a duracao de buffs temporarios
    protected int turnosBuffAtaque = 0;
    protected int turnosBuffDefesa = 0;

    protected int mp; // mana atual do personagem
    protected int maxMp = 100; // limite de mana

    protected int turnosSkillGuerreiro = 0; // buff que soma DEF ao ATK
    protected int turnosInvulneravel = 0;   // personagem ignora dano enquanto > 0

    // objeto usado para rolar um dado (valores de 1 a 20)
    private static final Random dado = new Random();

    // CONSTRUTOR
    public Personagem(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        this.nome = nome;
        this.pontosVida = pontosVida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        this.mp = maxMp;

        // todo personagem inicia com um inventario
        this.inventario = new Inventario();
    }

    // METODOS DE COMBATE
    // esse metodo executa a logica de ataque: rolagem de dado + comparacao com defesa
    public int atacar(Personagem alvo) {
        int dadoRolado = dado.nextInt(20) + 1;
        int meuAtaqueAtual = this.getAtaque(); // pega ataque ja considerando buffs
        int forcaAtaque = meuAtaqueAtual + dadoRolado;

        // debug visual para o jogador ver a conta
        System.out.println(" > " + this.nome + " atacou! (Dado: " + dadoRolado + " + Atq: " 
            + meuAtaqueAtual + " = " + forcaAtaque + ")");

        // verifica se o ataque supera a defesa do alvo
        if (forcaAtaque > alvo.getDefesa()) {
            int dano = forcaAtaque - alvo.getDefesa();
            alvo.receberDano(dano);
            System.out.println(" > ACERTOU! Dano causado: " + dano);
            return dano;
        } else {
            System.out.println(" > ERROU! (Defesa do inimigo era " + alvo.getDefesa() + ")");
            return 0;
        }
    }

    // recupera pontos de vida
    public void curar(int quantidadeCura) {
        this.pontosVida += quantidadeCura;
        System.out.println(" > " + this.nome + " recuperou " + quantidadeCura + " HP! (Vida atual: " + this.pontosVida + ")");
    }

    // reduz mana ao usar habilidades
    public void gastarMana(int custo) {
        this.mp -= custo;
        if (this.mp < 0)
            this.mp = 0;
    }

    // recupera mana sem passar do limite
    public void recuperarMana(int quantidade) {
        this.mp += quantidade;
        if (this.mp > maxMp)
            this.mp = maxMp;
        System.out.println(" > " + this.nome + " recuperou " + quantidade + " MP.");
    }

    // aplica o efeito do item no usuario ou no alvo
    public boolean usarItemDoInventario(Item item, Personagem alvo) {

        String efeito = item.getEfeito();

        System.out.println(" > Voce usou " + item.getNome() + "!");

        if (efeito.equalsIgnoreCase("cura")) {
            this.curar(30);
            this.inventario.remover(item.getNome());
            return true;

        } else if (efeito.equalsIgnoreCase("ataque")) {
            // item ofensivo
            int danoItem = 25;
            System.out.println(" > O item explode no inimigo causando " + danoItem + " de dano!");
            alvo.receberDano(danoItem);
            this.inventario.remover(item.getNome());
            return true;

        } else if (efeito.equalsIgnoreCase("buff_atk")) {
            // buff de ataque por turnos
            System.out.println(" > Voce sente uma furia sombria! (Ataque +10% por 2 turnos)");
            this.turnosBuffAtaque = 2;
            this.inventario.remover(item.getNome());
            return true;

        } else if (efeito.equalsIgnoreCase("buff_def")) {
            // buff de defesa por turnos
            System.out.println(" > Uma luz te protege! (Defesa +5 por 3 turnos)");
            this.turnosBuffDefesa = 3;
            this.inventario.remover(item.getNome());
            return true;

        } else if (efeito.equalsIgnoreCase("mana")) {
            // item que recupera MP
            this.recuperarMana(50);
            this.inventario.remover(item.getNome());
            return true;

        } else {
            System.out.println(" > Este item nao tem efeito em batalha.");
            return false;
        }
    }

    // metodo abstrato que sera implementado por classes filhas
    public abstract void batalhar(Inimigo inimigo, Scanner scanner);

    // metodo abstrato para habilidades especiais
    public abstract boolean usarHabilidadeEspecial(Inimigo inimigo);

    // descricao da habilidade especial
    public abstract String getDescricaoHabilidade();

    // reduz vida quando recebe dano (a nao ser que esteja invulneravel)
    public void receberDano(int dano) {

        // se estiver invulneravel, ignora dano
        if (this.turnosInvulneravel > 0) {
            System.out.println(" > " + this.nome + " esquivou completamente do ataque! (Dano 0)");
            return;
        }

        this.pontosVida -= dano;
        if (this.pontosVida < 0)
            this.pontosVida = 0;
    }

    // verifica se o personagem ainda esta vivo
    public boolean estaVivo() {
        return this.pontosVida > 0;
    }

    // chamado no fim de cada turno para reduzir duracao de efeitos temporarios
    public void atualizarBuffs() {

        // reduz buff de ataque
        if (this.turnosBuffAtaque > 0) {
            this.turnosBuffAtaque--;
            if (this.turnosBuffAtaque == 0) {
                System.out.println(" > O efeito da Essencia Demoniana acabou.");
            }
        }

        // reduz buff de defesa
        if (this.turnosBuffDefesa > 0) {
            this.turnosBuffDefesa--;
            if (this.turnosBuffDefesa == 0) {
                System.out.println(" > O efeito do Amuleto Sagrado acabou.");
            }
        }

        // regenera mana automaticamente a cada turno
        if (this.mp < this.maxMp) {
            this.mp += 5;
            if (this.mp > this.maxMp)
                this.mp = this.maxMp;
        }

        // reduz contadores das habilidades especiais
        if (this.turnosSkillGuerreiro > 0)
            this.turnosSkillGuerreiro--;

        if (this.turnosInvulneravel > 0)
            this.turnosInvulneravel--;
    }

    // aumenta nivel e recalcula atributos
    public void subirNivel() {

        this.nivel++;

        // calculo dos aumentos
        int aumentoVida = (int) (this.pontosVida * 0.1);
        int aumentoAtaque = (int) (this.ataque * 0.2);

        this.pontosVida += aumentoVida;
        this.ataque += aumentoAtaque;

        // feedback visual
        System.out.println("\n----------------------------------------");
        System.out.println("           LEVEL UP! (Nivel " + this.nivel + ")");
        System.out.println("----------------------------------------");
        System.out.println(" > Vida maxima aumentou: +" + aumentoVida + " (Total: " + this.pontosVida + ")");
        System.out.println(" > Ataque base aumentou: +" + aumentoAtaque + " (Total: " + this.ataque + ")");
        System.out.println("----------------------------------------\n");
    }

    // GETTERS
    public String getNome() {
        return nome;
    }

    public int getMp() {
        return mp;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public int getAtaque() {

        int ataqueFinal = this.ataque;

        // buff antigo de ataque (essencia)
        if (this.turnosBuffAtaque > 0) {
            ataqueFinal = (int) (ataqueFinal * 1.1);
        }

        // buff do guerreiro (soma defesa ao ataque)
        if (this.turnosSkillGuerreiro > 0) {
            ataqueFinal += this.defesa;
        }

        return ataqueFinal;
    }

    public int getDefesa() {
        // buff de defesa adiciona +5
        if (this.turnosBuffDefesa > 0) {
            return this.defesa + 5;
        }
        return this.defesa;
    }

    public int getNivel() {
        return nivel;
    }

    public Inventario getInventario() {
        return inventario;
    }
}
