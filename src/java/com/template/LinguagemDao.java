package com.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinguagemDao {

    // Conexão com o banco
    private Connection conn;

    //cadastro
    public void cadastrarLinguagem(LinguagemDTO objLinguagem) {
        String sql = "INSERT INTO linguagens (nome, criador, tipo, ano_criacao) VALUES (?, ?, ?, ?)";
        conn = new ConexaoBD().conectarBD(); // Abre a conexão

        try {
            // Prepara o comando de forma segura (evita invasões)
            PreparedStatement pstm = conn.prepareStatement(sql);

            // Substitui as (?) pelos dados da tela
            pstm.setString(1, objLinguagem.getNome());
            pstm.setString(2, objLinguagem.getCriador());
            pstm.setString(3, objLinguagem.getTipo());
            pstm.setInt(4, objLinguagem.getAnoCriacao());

            pstm.execute(); // Executa no banco
            pstm.close();   // Fecha para não travar a conexão

        } catch (SQLException e) {
            // Se der erro, mostra no console do IntelliJ
            System.out.println("Erro ao cadastrar linguagem: " + e.getMessage());
        }
    }

    // Lista todas as linguagens
    public List<LinguagemDTO> listarLinguagens() {
        // Traz todos os registros da tabela
        String sql = "SELECT * FROM linguagens";
        conn = new ConexaoBD().conectarBD();

        // Lista vazia para guardar os resultados do banco
        List<LinguagemDTO> lista = new ArrayList<>();

        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery(); // Executa e recebe os dados

            // Lê os resultados linha por linha
            while (rs.next()) {
                LinguagemDTO objLinguagem = new LinguagemDTO();

                // Preenche o objeto
                objLinguagem.setId(rs.getInt("id_linguagem"));
                objLinguagem.setNome(rs.getString("nome"));
                objLinguagem.setCriador(rs.getString("criador"));
                objLinguagem.setTipo(rs.getString("tipo"));
                objLinguagem.setAnoCriacao(rs.getInt("ano_criacao"));

                lista.add(objLinguagem); // Adiciona na lista final
            }
            pstm.close();

        } catch (SQLException e) {
            System.out.println("Erro ao listar linguagens: " + e.getMessage());
        }

        return lista; // Devolve os dados prontos para a tela
    }

    // Exclusão dos dados
    public void excluirLinguagem(int idLinguagem) {
        // Apaga pelo ID
        String sql = "DELETE FROM linguagens WHERE id_linguagem = ?";
        conn = new ConexaoBD().conectarBD();

        try {
            PreparedStatement pstm = conn.prepareStatement(sql);

            // Passa o ID selecionado na tela para a (?)
            pstm.setInt(1, idLinguagem);

            pstm.execute(); // Deleta do banco
            pstm.close();

        } catch (Exception e) {
            System.out.println("Erro ao excluir linguagem: " + e.getMessage());
        }
    }

    // atualizar a linguagem
    public void atualizarLinguagem(LinguagemDTO objLinguagem) {
        // SQL de atualização. Atualiza apenas a linguagem que tiver o ID correspondente
        String sql = "UPDATE linguagens SET nome = ?, criador = ?, tipo = ?, ano_criacao = ? WHERE id_linguagem = ?";
        conn = new ConexaoBD().conectarBD();

        try {
            PreparedStatement pstm = conn.prepareStatement(sql);

            // Substitui as (?) pelos novos dados
            pstm.setString(1, objLinguagem.getNome());
            pstm.setString(2, objLinguagem.getCriador());
            pstm.setString(3, objLinguagem.getTipo());
            pstm.setInt(4, objLinguagem.getAnoCriacao());
            pstm.setInt(5, objLinguagem.getId()); // O ID usado no WHERE

            pstm.execute(); // Executa a alteração no banco
            pstm.close();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar linguagem: " + e.getMessage());
        }
    }
}