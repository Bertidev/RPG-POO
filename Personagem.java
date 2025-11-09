import java.util.Random;

public abstract class Personagem {

    //ATRIBUTOS
    //'protected' permite que as classes filhas acessem esses atributos
    protected String nome;        
    protected int pontosVida;      
    protected int ataque;        
    protected int defesa;        
    protected int nivel;         
    protected Inventario inventario; 

    //objeto para simular a rolagem de dados
    private static final Random dado = new Random();

    //CONSTRUTOR
    public Personagem(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        this.nome = nome;
        this.pontosVida = pontosVida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        
        //todo personagem é inicializado com um inventario
        this.inventario = new Inventario(); 
    }

    //METODOS DE COMBATE
    //esse metodo tem a logica de rolagem de dado e a parte de ataque vs defesa
    public int atacar(Personagem alvo) {
        //uso um d20 -> nextInt(20) gera 0-19, então somamos 1.
        int rolagemDado = dado.nextInt(20) + 1;
        int forcaAtaque = this.ataque + rolagemDado;

        System.out.println(" > " + this.nome + " rolou " + rolagemDado + " (Força total: " + forcaAtaque + ")");

        //verificando se o ataque superou a defesa do alvo
        if (forcaAtaque > alvo.getDefesa()) {
            int dano = forcaAtaque - alvo.getDefesa();
            alvo.receberDano(dano);
            System.out.println(" > " + this.nome + " ACERTOU " + alvo.getNome() + " e causou " + dano + " de dano!");
            return dano;
        } else {
            System.out.println(" > " + this.nome + " ERROU o ataque contra " + alvo.getNome() + ".");
            return 0;
        }
    }

    //metodo para subtrair da vida de um personagem quando ele recebe dano
    public void receberDano(int dano) {
        this.pontosVida -= dano;
        //garantia de que os pontos de vida nao ficarao negativos
        if (this.pontosVida < 0) {
            this.pontosVida = 0;
        }
    }

    //metodo que verifica se o personagem ainda esta vivo
    public boolean estaVivo() {
        return this.pontosVida > 0;
    }

    //GETTERS
    public String getNome() {
        return nome;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public int getNivel() {
        return nivel;
    }

    public Inventario getInventario() {
        return inventario;
    }
}