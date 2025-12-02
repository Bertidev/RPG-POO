import java.util.Map;
import java.util.TreeMap;

// classe responsavel por armazenar, organizar e manipular os itens do personagem
// usa treemap para manter os itens ordenados alfabeticamente
public class Inventario implements Cloneable { 

    // atributo
    // chave = nome do item | valor = objeto item
    private Map<String, Item> itens;

    // construtor
    // inicia o inventario com um treemap (ordem alfabetica automatica)
    public Inventario() {
        this.itens = new TreeMap<>();
    }

    // metodos obrigatorios

    // adiciona um item ao inventario
    // se o item ja existe, soma quantidades; caso contrario, cria a entrada
    // parametro 'silencioso' controla se deve ou nao imprimir mensagem
    public void adicionar(Item itemParaAdicionar, boolean silencioso) {
        String nomeDoItem = itemParaAdicionar.getNome();

        // caso o item ja exista no inventario -> acumula quantidade
        if (this.itens.containsKey(nomeDoItem)) {

            Item itemExistente = this.itens.get(nomeDoItem);
            itemExistente.adicionarQuantidade(itemParaAdicionar.getQuantidade());

            // exibe mensagem apenas se nao estiver silencioso
            if (!silencioso) { 
                System.out.println(" > (Inventário) " + itemParaAdicionar.getQuantidade() +
                                   "x '" + nomeDoItem + "' adicionado(s).");
            }

        } else {
            // novo item -> adiciona ao mapa
            this.itens.put(nomeDoItem, itemParaAdicionar);

            if (!silencioso) {
                System.out.println(" > (Inventário) Novo item adicionado: " + itemParaAdicionar.getNome());
            }
        }
    }

    // diminui a quantidade de um item quando usado
    // se chegar a 0, remove o item completamente do inventario
    public void remover(String nomeDoItem) {

        // caso o jogador tente usar algo que nao possui
        if (!this.itens.containsKey(nomeDoItem)) {
            System.out.println(" > (Inventário) Você não possui o item '" + nomeDoItem + "'.");
            return;
        }

        Item item = this.itens.get(nomeDoItem);

        // o proprio metodo usaritem() ja reduz a quantidade
        item.usarItem();

        // se a quantidade zerar -> remove do mapa
        if (item.getQuantidade() <= 0) {
            this.itens.remove(nomeDoItem);
            System.out.println(" > (Inventário) '" + nomeDoItem + "' acabou.");
        }
    }

    // exibe todos os itens do inventario em ordem alfabetica
    public void listar() {
        if (this.itens.isEmpty()) {
            System.out.println("--- INVENTÁRIO (Vazio) ---");
            return;
        }

        System.out.println("--- INVENTÁRIO ---");

        // treemap garante ordem alfabetica automaticamente
        for (Item item : this.itens.values()) {
            System.out.println(item.toString());
        }

        System.out.println("--------------------");
    }

    // cria uma copia profunda do inventario (deep copy)
    // essencial para copiar loot de inimigos sem compartilhar referencias
    @Override
    public Inventario clone() {
        Inventario inventarioCopiado = new Inventario();

        // percorre todos os itens do inventario atual
        for (Item itemOriginal : this.itens.values()) {

            // cria um novo item com os mesmos atributos (clone de verdade)
            Item itemCopiado = new Item(
                itemOriginal.getNome(),
                itemOriginal.getDescricao(),
                itemOriginal.getEfeito(),
                itemOriginal.getQuantidade()
            );

            // adiciona ao novo inventario em modo silencioso
            inventarioCopiado.adicionar(itemCopiado, true);
        }

        return inventarioCopiado;
    }

    // adiciona todos os itens do inventario do inimigo ao jogador apos a batalha
    // aqui silencioso = false para mostrar mensagem de loot
    public void adicionarItensDoInimigo(Inventario inventarioInimigo) {
        for (Item itemDoInimigo : inventarioInimigo.itens.values()) {
            this.adicionar(itemDoInimigo, false);
        }
    }

    // verifica rapidamente se possui um item especifico (ex: verificar pocao)
    public boolean temItem(String nome) {
        return this.itens.containsKey(nome);
    }

    // retorna a lista de itens como arraylist (mais facil para menus)
    public java.util.ArrayList<Item> getListaItens() {
        return new java.util.ArrayList<>(this.itens.values());
    }

    // getters
    // retorna o item pelo nome, util para quando o jogador seleciona algo pelo menu
    public Item getItem(String nomeDoItem) {
        return this.itens.get(nomeDoItem);
    }
}