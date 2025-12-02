import java.util.Objects;

// classe que representa qualquer item utilizavel no jogo
// itens podem ser consumiveis, buffs, etc
public class Item implements Comparable<Item> {

    // atributos
    private String nome;        // nome do item (criterio principal para comparacao e igualdade)
    private String descricao;   // texto explicando o que o item faz
    private String efeito;      // usado pelo sistema para identificar o tipo (ex: "cura", "buff_atk")
    private int quantidade;     // numero de unidades desse item

    // construtor
    // inicializa todos os campos do item
    public Item(String nome, String descricao, String efeito, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.efeito = efeito;
        this.quantidade = quantidade;
    }

    // metodos obrigatorios

    // sobrescrita de equals para verificar se dois itens sao considerados "iguais"
    // a comparacao e feita somente pelo nome, permitindo stack (acumular quantidades)
    @Override
    public boolean equals(Object o) {

        // se e o mesmo objeto em memoria -> iguais
        if (this == o) return true;

        // se o outro e null ou de outra classe -> diferentes
        if (o == null || getClass() != o.getClass()) return false;

        // converte o Object para Item
        Item item = (Item) o;

        // igualdade baseada exclusivamente no nome
        return nome.equals(item.nome);
    }

    // como equals utiliza apenas "nome", o hashCode tambem deve utilizar nome
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    // define a ordenacao natural dos itens
    // aqui ordenamos alfabeticamente pelo nome
    @Override
    public int compareTo(Item outroItem) {
        return this.nome.compareTo(outroItem.getNome());
    }

    // metodos opcionais

    // reduz a quantidade do item apos o uso
    public void usarItem() {
        if (this.quantidade > 0) {

            this.quantidade--; // consome uma unidade

            System.out.println(" > Você usou " + this.nome + ". Restam: " + this.quantidade);

        } else {

            // caso o jogador tente usar algo que acabou
            System.out.println(" > Você não tem mais " + this.nome + " para usar.");
        }
    }

    // adiciona mais unidades deste item ao inventario
    public void adicionarQuantidade(int quantidadeParaAdicionar) {
        this.quantidade += quantidadeParaAdicionar;
    }

    // representacao textual do item para exibicao no inventario
    @Override
    public String toString() {
        return String.format("[%dx] %s (%s)", this.quantidade, this.nome, this.descricao);
    }

    // getters

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getEfeito() {
        return efeito;
    }

    public int getQuantidade() {
        return quantidade;
    }
}