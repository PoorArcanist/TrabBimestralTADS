package br.univel;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.postgresql.util.PSQLException;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.*;


public class Principal extends JFrame {

	private JPanel contentPane;
	private JTable table = new JTable();
	JTextField jtext[];
	JLabel jlab[];
	Connection connection = null;
	Tabela t = new Tabela();
	int QtdFields;
	
	Pessoa p = new Pessoa();
	Class<?> cz = p.getClass();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principal frame = new Principal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Principal() throws SQLException {
		JOptionPane.showMessageDialog(null, "'Instruções'\nPara inserir preencha os campos e clique em Salvar, campos em branco serão definidos como NULL\n"
				+ "Para buscar preencha quantos campos quiser e clique em buscar, se nenhum campo for preenchido, será apresentado todos os valores do banco\n"
				+ "Para excluir preencha quantos campos quiser filtrar e clique em excluir");
		Pessoa p = new Pessoa();
		Class<?> cz = p.getClass();
		QtdFields = cz.getDeclaredFields().length;
		
		CriarTela();	
		CriarLabelText();
		CriarTable();
		CriarButton();
		GetConnection();
		AtualizarTabela();
		
		
	}

	private void AtualizarTabela() throws SQLException {
		String SQL = "SELECT * FROM " + cz.getAnnotation(TabelaA.class).value();
		PreparedStatement ps = connection.prepareStatement(SQL);
		int a = 0;
		int x = 1 ;
		int aux = 0;
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
		} catch (Exception e) {
			int opcao = Integer.parseInt(JOptionPane.showInputDialog("A tabela " + cz.getAnnotation(TabelaA.class).value() + ""
					+ " não existe no seu postgre! Deseja criá-la? \n1-Sim\n0-Não"));
			if(opcao==1){
				CriarTableBanco();
				rs = ps.executeQuery();
			}
		}
		while(rs.next())
			aux++;
		ResultSet rb = ps.executeQuery();
		Object[][] ob = new Object[aux][QtdFields];
		aux = 0;
		while(rb.next()){
			x = 1;
			aux++;
			while(x<=QtdFields){
				ob[a][x-1] = rb.getObject(x);
				x++;
			}
			a++;
		}
		t.setLinhas(aux);
		t.setValores(ob);
		t.fireTableDataChanged();
		
			
		
		
	}

	private void GetConnection() throws SQLException {
		String url ="jdbc:postgresql://localhost:5432/postgres";
		String user = "postgres";
		String password = "123";

		connection = DriverManager.getConnection(url, user, password);
		
		
		
	}

	private void CriarButton() {
		JButton btnNewButton = new JButton("Salvar");
		JButton btnNewButton2 = new JButton("Buscar");
		JButton btnNewButton3 = new JButton("Excluir");
		JButton btnNewButton4 = new JButton("Criar tabela no banco");
		JButton btnNewButton5 = new JButton("'Dropar' tabela do banco");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.weightx = QtdFields;
		gbc_btnNewButton.gridy = 2;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		gbc_btnNewButton.gridy = 3;
		contentPane.add(btnNewButton2, gbc_btnNewButton);
		gbc_btnNewButton.gridy = 4;
		contentPane.add(btnNewButton3, gbc_btnNewButton);
		gbc_btnNewButton.gridy = 3;
		gbc_btnNewButton.gridx = 1;
		contentPane.add(btnNewButton4, gbc_btnNewButton);
		gbc_btnNewButton.gridx = 2;
		contentPane.add(btnNewButton5, gbc_btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Salvar();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Buscar();
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "Tabela " + cz.getAnnotation(TabelaA.class).value() + " não existe!");
				}
			}
		});
		btnNewButton3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Excluir();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnNewButton4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					CriarTableBanco();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnNewButton5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DroparTabela();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
	}

	protected void DroparTabela() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE " + cz.getAnnotation(TabelaA.class).value());
		System.out.println(sb.toString());
		
		String sql = sb.toString();
		PreparedStatement sp = connection.prepareStatement(sql);
		sp.executeUpdate();
		
	}

	protected void CriarTableBanco() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + cz.getAnnotation(TabelaA.class).value());
		sb.append("(");
		int i =0;
		for(Field f:cz.getDeclaredFields()){
			if(i!=0){
				sb.append(",");
			}
			sb.append(jlab[i].getText());
			if(f.getGenericType().getTypeName().endsWith("String"))
				sb.append(" VARCHAR(200)");
			else if(jlab[i].getText().equals("id"))
				sb.append(" SERIAL");
			else
				sb.append(" int");
			i++;
		}
		//declarando as chaves primarias
		sb.append(",PRIMARY KEY(");
		Field[] atributos = cz.getDeclaredFields();
		for(int j = 0, aux = 0;j < QtdFields; j++){
			Field field = atributos[j];
			if(field.isAnnotationPresent(Coluna.class)){
				Coluna anotacaoColuna = field.getAnnotation(Coluna.class);
				if(anotacaoColuna.pk()){
					if(aux > 0){
						sb.append(", ");
					}
					if(anotacaoColuna.nome().isEmpty()){
						sb.append(field.getName());
					}
					else{
						sb.append(anotacaoColuna.nome());
					}
					aux++;
				}
			}
		}
		sb.append(")");
		
		sb.append(")");
		System.out.println(sb);
		String sql = sb.toString();
		PreparedStatement ps = connection.prepareStatement(sql);
		try {
			ps.executeUpdate();
		} catch (PSQLException e) {
			JOptionPane.showMessageDialog(null, "Tabela " + cz.getAnnotation(TabelaA.class).value() + " ja existe!");
		}

		
	}

	protected void Excluir() throws SQLException {
		Object[] o = new Object[QtdFields];
		StringBuilder par = new StringBuilder();
		par.append("DELETE FROM ");
		par.append(cz.getAnnotation(TabelaA.class).value()+ " WHERE ");
		for(int i =0; i<o.length; i++){
			o[i] = jtext[i].getText();
		}
		int aux = 0;
		for(Field f: cz.getDeclaredFields()){
			if(aux != 0 && !o[aux-1].equals("") && !o[aux].equals(""))
				par.append(" and ");
			if(!o[aux].equals("")){
				if(!f.getGenericType().getTypeName().endsWith("String"))
					par.append(jlab[aux].getText() + "=" + o[aux]);
				else
					par.append(jlab[aux].getText() + "=" +"'" +  o[aux] + "'");
			}
			aux++;
		}
		System.out.println(par);
		String sql = par.toString();
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.executeUpdate();
		AtualizarTabela();
		for(int i =0; i<jtext.length; i++){
			jtext[i].setText("");
		}
		
	}

	protected void Buscar() throws SQLException {
		Object[] o = new Object[QtdFields];
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM " + cz.getAnnotation(TabelaA.class).value());
		
		for(int i =0; i<o.length; i++){
			o[i] = jtext[i].getText();
		}
		int aux = 0;
		int verif = 0;
		while(aux<o.length){
			if(!o[aux].equals(""))
				verif = 1;
			aux++;
		}
		if(verif==0){
			AtualizarTabela();
		}
		else{
			sb.append(" WHERE ");
			aux = 0;
			for(Field f: cz.getDeclaredFields()){
				if(aux != 0 && !o[aux-1].equals("") && !o[aux].equals(""))
					sb.append(" and ");
				if(!o[aux].equals("")){
					if(!f.getGenericType().getTypeName().endsWith("String"))
						sb.append(jlab[aux].getText() + "=" + o[aux]);
					else
						sb.append(jlab[aux].getText() + "=" +"'" +  o[aux] + "'");
				}
				aux++;
			}
			String sql = sb.toString();
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int a = 0;
			int x = 1 ;
			int aux2 = 0;
			while(rs.next())
				aux2++;
			ResultSet rb = ps.executeQuery();
			Object[][] ob = new Object[aux2][QtdFields];
			aux2 = 0;
			while(rb.next()){
				x = 1;
				aux2++;
				while(x<=QtdFields){
					ob[a][x-1] = rb.getObject(x);
					x++;
				}
				a++;
			}
			t.setLinhas(aux2);
			t.setValores(ob);
			t.fireTableDataChanged();
			for(int i =0; i<jtext.length; i++){
				jtext[i].setText("");
			}
		}
		
		
	}

	protected void Salvar() throws SQLException {
		Object[] o = new Object[QtdFields];
		for(int i =0; i<o.length; i++){
			o[i] = jtext[i].getText();
			if( i!= 0 && o[i].equals(""))
				o[i] = "NULL";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(cz.getAnnotation(TabelaA.class).value());
		sb.append("(");
		int aux = 0;
		Object[] obj2 = new Object[QtdFields];
		for(Field f: cz.getDeclaredFields()){
			if(aux==0 && o[0].equals(""));
			else if(aux!=QtdFields -1)
				sb.append(f.getName() + ",");
			else
				sb.append(f.getName() + ")");
			obj2[aux] = f.getGenericType().getTypeName();
			aux++;
		}
		sb.append(" VALUES(");
		aux =0;
		for (int i = 0; i < o.length; i++) {
			if(aux==0 && o[0].equals(""));
			else if(obj2[aux].equals("int")&& aux!=QtdFields -1){
				sb.append(o[aux] + ",");
			}
			else if(aux!=QtdFields -1){
				sb.append("'" + o[aux] + "',");
			}
			else
				sb.append(o[aux] + ")");
			aux++;
		}
		System.out.println(sb);
		String sql = sb.toString();
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.executeUpdate();
		AtualizarTabela();
		for(int i =0; i<jtext.length; i++){
			jtext[i].setText("");
		}

		
	}

	private void CriarTable() {
		t.setColunas(QtdFields);
		table.setModel(t);
		table.setBackground(Color.white);
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.gridwidth = QtdFields;
		gbc_table.insets = new Insets(0, 0, 0, 5);
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.gridx = 0;
		gbc_table.gridy = 5;
		JScrollPane js = new JScrollPane(table);
		contentPane.add(js,gbc_table);
		
		
	}

	private void CriarTela() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, QtdFields * 152, 610);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		double vet[] = new double[QtdFields +1];
		
		for(int i =0; i<vet.length;i++){
			if(i == vet.length-1)
				vet[i] = Double.MIN_VALUE;
			
			else
				vet[i] = 1.0;
		}
		gbl_contentPane.columnWeights = vet;
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		contentPane.setBackground(Color.darkGray);
	}

	private void CriarLabelText() {
		
		int cont = 0;
		jlab = new JLabel[QtdFields];
		jtext = new JTextField[QtdFields];

		GridBagConstraints gbag = new GridBagConstraints();
		
		for(Field f : cz.getDeclaredFields()){
			f.setAccessible(true);
			jlab[cont] = new JLabel(f.getAnnotation(Coluna.class).nome());
			if(f.getAnnotation(Coluna.class).nome().isEmpty())
				jlab[cont].setText(f.getName());
			jlab[cont].setForeground(Color.white);
			gbag = new GridBagConstraints();
			gbag.insets = new Insets(0, 0, 5, 5);
			gbag.gridx = cont;
			gbag.gridy = 0;
			contentPane.add(jlab[cont],gbag);
			jtext[cont] = new JTextField();
			gbag.fill = GridBagConstraints.HORIZONTAL;
			gbag.gridy = 1;
			contentPane.add(jtext[cont], gbag);
			jtext[cont].setColumns(10);
			
			
			cont++;
		}
	}

}
