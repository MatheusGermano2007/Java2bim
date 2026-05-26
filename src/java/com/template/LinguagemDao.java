package com.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinguagemDao {

    private Connection conn;


    public void cadastrarLinguagem(LinguagemDTO objLinguagem) {
        String sql = "INSERT INTO linguagens (nome, criador, tipo, ano_criacao) VALUES (?, ?, ?, ?)";
        conn = new ConexaoBD().conectarBD();

        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, objLinguagem.getNome());
            pstm.setString(2, objLinguagem.getCriador());
            pstm.setString(3, objLinguagem.getTipo());
            pstm.setInt(4, objLinguagem.getAnoCriacao());

            pstm.execute();
            pstm.close();

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar linguagem: " + e.getMessage());
        }
    }


    public List<LinguagemDTO> listarLinguagens() {
        String sql = "SELECT * FROM linguagens";
        conn = new ConexaoBD().conectarBD();
        List<LinguagemDTO> lista = new ArrayList<>();

        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                LinguagemDTO objLinguagem = new LinguagemDTO();
                objLinguagem.setId(rs.getInt("id_linguagem"));
                objLinguagem.setNome(rs.getString("nome"));
                objLinguagem.setCriador(rs.getString("criador"));
                objLinguagem.setTipo(rs.getString("tipo"));
                objLinguagem.setAnoCriacao(rs.getInt("ano_criacao"));

                lista.add(objLinguagem);
            }
            pstm.close();

        } catch (SQLException e) {
            System.out.println("Erro ao listar linguagens: " + e.getMessage());
        }
        return lista;
    }


    public void excluirLinguagem(int id) {
        String sql = "DELETE FROM linguagens WHERE id = ?";
        conn = new ConexaoBD().conectarBD();

        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id);

            pstm.execute();
            pstm.close();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir linguagem: " + e.getMessage());
        }
    }
}
