import java.util.Map;
import java.util.TreeMap;

public class Inventario implements Cloneable { 

    //ATRIBUTO
    //string é o nome do item
    private Map<String, Item> itens;

    //CONSTRUTOR
    public Inventario() {
        this.itens = new TreeMap<>();
    }

    //METODOS OBRIGATORIOS
    //adiciona um item ao inventario, se ja existe um dele, aumenta a quantidade 
    public void adicionar(Item itemParaAdicionar) {
        String nomeDoItem = itemParaAdicionar.getNome();

        //verificando se o item ja existe no mapa
        if (this.itens.containsKey(nomeDoItem)) {
            //se sim aumenta a quantidade
            Item itemExistente = this.itens.get(nomeDoItem);

            itemExistente.adicionarQuantidade(itemParaAdicionar.getQuantidade());
            System.out.println(" > (Inventário) " + itemParaAdicionar.getQuantidade() + "x '" + nomeDoItem + "' adicionado(s). Total agora: " + itemExistente.getQuantidade());
        } else {
            //se nao adiciona ao inventario
            this.itens.put(nomeDoItem, itemParaAdicionar);
            System.out.println(" > (Inventário) Novo item adicionado: " + itemParaAdicionar.getNome());
        }
    }

    //diminui a quantidade de um item ao usa-lo, se chegar a 0 remove o item do inventario
    public void remover(String nomeDoItem) {
        if (!this.itens.containsKey(nomeDoItem)) {
            System.out.println(" > (Inventário) Você não possui o item '" + nomeDoItem + "'.");
            return;
        }

        Item item = this.itens.get(nomeDoItem);
        item.usarItem(); //metodo ja diminui a quantidade

        //se a quantidade for 0 remove o item do mapa
        if (item.getQuantidade() <= 0) {
            this.itens.remove(nomeDoItem);
            System.out.println(" > (Inventário) '" + nomeDoItem + "' acabou.");
        }
    }

    //lista todos os itens do inventario
    public void listar() {
        if (this.itens.isEmpty()) {
            System.out.println("--- INVENTÁRIO (Vazio) ---");
            return;
        }

        System.out.println("--- INVENTÁRIO ---");
        for (Item item : this.itens.values()) {
            System.out.println(item.toString());
        }
        System.out.println("--------------------");
    }

    //cria uma copia do inventario, essencial para fazer loot de inimigos
    @Override
    public Inventario clone() {
        Inventario inventarioCopiado = new Inventario();

        //todos os items do inventorio
        for (Item itemOriginal : this.itens.values()) {
            //cria um novo objeto sendo uma copia exata
            Item itemCopiado = new Item(
                itemOriginal.getNome(),
                itemOriginal.getDescricao(),
                itemOriginal.getEfeito(),
                itemOriginal.getQuantidade()
            );
            //adiciona a copia ao novo inventario
            inventarioCopiado.adicionar(itemCopiado);
        }
        return inventarioCopiado;
    }

    public void adicionarItensDoInimigo(Inventario inventarioInimigo) {
        // Itera sobre os itens do inimigo (usando values() do TreeMap)
        for (Item itemDoInimigo : inventarioInimigo.itens.values()) {
            // Usa o método adicionar() que já criamos, pois ele
            // já sabe lidar com quantidades somadas!
            this.adicionar(itemDoInimigo);
        }
    }
    
    // Precisamos também de um método para verificar se tem poção
    public boolean temItem(String nome) {
        return this.itens.containsKey(nome);
    }

    //GETTERS
    //getter para jogo poder ver as informacoes do item
    public Item getItem(String nomeDoItem) {
        return this.itens.get(nomeDoItem);
    }
}