import java.util.Objects;

public class Item implements Comparable<Item> {

    //ATRIBUTOS
    private String nome;        // [cite: 163]
    private String descricao;   // [cite: 164]
    private String efeito;      // [cite: 166]
    private int quantidade;     // [cite: 167]

    //CONSTRUTOR
    public Item(String nome, String descricao, String efeito, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.efeito = efeito;
        this.quantidade = quantidade;
    }

    //METODOS OBRIGATORIOS
    //sobrescrevendo equals para verificar se os items tem o mesmo nome
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        // A igualdade é definida apenas pelo NOME.
        return nome.equals(item.nome); 
    }

    //como equals usa nome, hashcode tambem deve usar
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    //ordenando os items em ordem alfabetica
    @Override
    public int compareTo(Item outroItem) {
        return this.nome.compareTo(outroItem.getNome());
    }

    //METODOS OPCIONAIS
    //decrementa um item depois de usa-lo
    public void usarItem() {
        if (this.quantidade > 0) {
            this.quantidade--;
            System.out.println(" > Você usou " + this.nome + ". Restam: " + this.quantidade);
        } else {
            System.out.println(" > Você não tem mais " + this.nome + " para usar.");
        }
    }

    //adiciona item 
    public void adicionarQuantidade(int quantidadeParaAdicionar) {
        this.quantidade += quantidadeParaAdicionar;
    }

    //retorna uma string com os items
    @Override
    public String toString() {
        return String.format("[%dx] %s (%s)", this.quantidade, this.nome, this.descricao);
    }

    // GETTERS
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