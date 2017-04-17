package br.univel;

@TabelaA("Table_Pessoa")
public class Pessoa {
	
	@Coluna(pk=true)
	private int id;
	
	@Coluna(nome="Nome")
	private String nome;
	
	@Coluna(nome="Sobrenome")
	private String sobrenome;
	
	@Coluna(nome="Idade")
	private int idade;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}
	
	
}
