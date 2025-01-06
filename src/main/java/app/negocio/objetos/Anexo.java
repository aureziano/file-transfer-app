package app.negocio.objetos;

// Classe Objeto - Anexo
public class Anexo {
    private String nome;
    private byte[] conteudo;
    private String caminho;

    // Construtor
    public Anexo(String nome, byte[] conteudo, String caminho) {
        this.nome = nome;
        this.conteudo = conteudo;
        this.caminho = caminho;
    }

    // Getters e Setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
}