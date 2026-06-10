public enum TreeType {
    BST("Binária Comum"),
    AVL("AVL"),
    RED_BLACK("Red-Black");

    private final String descricao;

    TreeType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
