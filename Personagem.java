import java.util.Random;
import java.util.Scanner;

public abstract class Personagem {

    // ATRIBUTOS
    // 'protected' permite que as classes filhas acessem esses atributos
    protected String nome;
    protected int pontosVida;
    protected int ataque;
    protected int defesa;
    protected int nivel;
    protected Inventario inventario;

    // Novos atributos para controlar a duração dos buffs
    protected int turnosBuffAtaque = 0;
    protected int turnosBuffDefesa = 0;

    protected int mp; // Mana atual
    protected int maxMp = 100;

    protected int turnosSkillGuerreiro = 0; // Buff de ATK + DEF
    protected int turnosInvulneravel = 0;

    // objeto para simular a rolagem de dados
    private static final Random dado = new Random();

    // CONSTRUTOR
    public Personagem(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        this.nome = nome;
        this.pontosVida = pontosVida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        this.mp = maxMp;

        // todo personagem é inicializado com um inventario
        this.inventario = new Inventario();
    }

    // METODOS DE COMBATE
    // esse metodo tem a logica de rolagem de dado e a parte de ataque vs defesa
    public int atacar(Personagem alvo) {
        int dadoRolado = dado.nextInt(20) + 1;
        int meuAtaqueAtual = this.getAtaque(); // Pega o ataque JÁ com buff
        int forcaAtaque = meuAtaqueAtual + dadoRolado;

        // Mensagem de Debug visual para o jogador entender a conta
        System.out.println(" > " + this.nome + " atacou! (Dado: " + dadoRolado + " + Atq: " + meuAtaqueAtual + " = "
                + forcaAtaque + ")");

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

    public void curar(int quantidadeCura) {
        this.pontosVida += quantidadeCura;
        System.out.println(
                " > " + this.nome + " recuperou " + quantidadeCura + " HP! (Vida atual: " + this.pontosVida + ")");
    }

    public void gastarMana(int custo) {
        this.mp -= custo;
        if (this.mp < 0)
            this.mp = 0;
    }

    public void recuperarMana(int quantidade) {
        this.mp += quantidade;
        if (this.mp > maxMp)
            this.mp = maxMp;
        System.out.println(" > " + this.nome + " recuperou " + quantidade + " MP.");
    }

    public boolean usarItemDoInventario(Item item, Personagem alvo) {
        String efeito = item.getEfeito();

        System.out.println(" > Você usou " + item.getNome() + "!");

        if (efeito.equalsIgnoreCase("cura")) {
            this.curar(30);
            this.inventario.remover(item.getNome());
            return true;

        } else if (efeito.equalsIgnoreCase("ataque")) {
            int danoItem = 25;
            System.out.println(" > O item explode no inimigo causando " + danoItem + " de dano!");
            alvo.receberDano(danoItem);
            this.inventario.remover(item.getNome());
            return true;

            // --- NOVOS EFEITOS ---

        } else if (efeito.equalsIgnoreCase("buff_atk")) { // Essência Demoníaca
            System.out.println(" > Você sente uma fúria sombria! (Ataque +10% por 2 turnos)");
            this.turnosBuffAtaque = 2; // Define a duração
            this.inventario.remover(item.getNome());
            return true;

        } else if (efeito.equalsIgnoreCase("buff_def")) { // Amuleto Sagrado
            System.out.println(" > Uma luz divina te protege! (Defesa +5 por 3 turnos)");
            this.turnosBuffDefesa = 3; // Define a duração
            this.inventario.remover(item.getNome());
            return true;

            // ---------------------

        } else if (efeito.equalsIgnoreCase("mana")) {
            // AGORA FUNCIONA!
            this.recuperarMana(50); // Recupera 50 MP
            this.inventario.remover(item.getNome());
            return true;
        } else {
            System.out.println(" > Este item não tem efeito em batalha.");
            return false;
        }
    }

    // metodo abstrato
    public abstract void batalhar(Inimigo inimigo, Scanner scanner);

    // Obriga as classes a criarem sua habilidade específica
    public abstract boolean usarHabilidadeEspecial(Inimigo inimigo);

    public abstract String getDescricaoHabilidade();

    // metodo para subtrair da vida de um personagem quando ele recebe dano
    public void receberDano(int dano) {
        // Se estiver invulnerável (Skill do Arqueiro), ignora o dano
        if (this.turnosInvulneravel > 0) {
            System.out.println(" > " + this.nome + " esquivou completamente do ataque! (Dano 0)");
            return;
        }

        this.pontosVida -= dano;
        if (this.pontosVida < 0)
            this.pontosVida = 0;
    }

    // metodo que verifica se o personagem ainda esta vivo
    public boolean estaVivo() {
        return this.pontosVida > 0;
    }

    /**
     * Chamado a cada final de rodada para diminuir a duração dos buffs.
     */
    public void atualizarBuffs() {
        if (this.turnosBuffAtaque > 0) {
            this.turnosBuffAtaque--;
            if (this.turnosBuffAtaque == 0) {
                System.out.println(" > O efeito da Essência Demoníaca acabou.");
            }
        }

        if (this.turnosBuffDefesa > 0) {
            this.turnosBuffDefesa--;
            if (this.turnosBuffDefesa == 0) {
                System.out.println(" > O efeito do Amuleto Sagrado acabou.");
            }
        }

        // Lógica de Regeneração de Mana (5 por turno)
        if (this.mp < this.maxMp) {
            this.mp += 5;
            if (this.mp > this.maxMp)
                this.mp = this.maxMp;
        }

        // Diminui contadores das skills novas
        if (this.turnosSkillGuerreiro > 0)
            this.turnosSkillGuerreiro--;
        if (this.turnosInvulneravel > 0)
            this.turnosInvulneravel--;
    }

    /**
     * Aumenta o nível e os atributos do personagem.
     * Regra: +10% de Vida e +20% de Ataque.
     */
    public void subirNivel() {
        this.nivel++;

        // Calcula os aumentos (Cast para int para arredondar)
        int aumentoVida = (int) (this.pontosVida * 0.1); // 10%
        int aumentoAtaque = (int) (this.ataque * 0.2); // 20%

        // Aplica os aumentos
        this.pontosVida += aumentoVida;
        this.ataque += aumentoAtaque;

        // Feedback Visual
        System.out.println("\n----------------------------------------");
        System.out.println("       ✨ LEVEL UP! (NÍVEL " + this.nivel + ") ✨");
        System.out.println("----------------------------------------");
        System.out.println(" > Vida máxima aumentou: +" + aumentoVida + " (Total: " + this.pontosVida + ")");
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

        // Buff antigo da Essência (+10%)
        if (this.turnosBuffAtaque > 0) {
            ataqueFinal = (int) (ataqueFinal * 1.1);
        }

        // NOVO: Skill do Guerreiro (Soma a Defesa no Ataque)
        if (this.turnosSkillGuerreiro > 0) {
            ataqueFinal += this.defesa;
        }

        return ataqueFinal;
    }

    public int getDefesa() {
        if (this.turnosBuffDefesa > 0) {
            // Se tiver buff, soma +5 fixo
            return this.defesa + 5;
        }
        return this.defesa; // Se não, retorna normal
    }

    public int getNivel() {
        return nivel;
    }

    public Inventario getInventario() {
        return inventario;
    }
}