package br.univel;

import java.lang.reflect.Field;

import javax.swing.table.AbstractTableModel;


public class Tabela extends AbstractTableModel {
	private int colunas;
	private int linhas;
	private Object valor;
	
	Pessoa p = new Pessoa();
	Class<?> cz = p.getClass();
	Field f[] = cz.getDeclaredFields();
	Object valores[][];
	
	
	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return f[column].getName();
	}
	@Override
	public int getColumnCount() {
		return getColunas();
		
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return getLinhas();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return getValores(arg0,arg1);
	}

	public int getLinhas() {
		return linhas;
	}

	public void setLinhas(int linhas) {
		this.linhas = linhas;
	}

	public int getColunas() {
		return colunas;
	}

	public void setColunas(int colunas) {
		this.colunas = colunas;
	}
	public void setValores(Object[][] ob) {
		
		this.valores = ob;
	}
	public Object getValores(int a, int b){
		
		return valores[a][b];
	}



}
