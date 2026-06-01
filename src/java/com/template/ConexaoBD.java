package com.template;

import java.sql.Connection;    // Objeto que mantém a conexão aberta com o banco
import java.sql.DriverManager; // Gerenciador que faz a ligação usando a URL e a senha
import java.sql.SQLException;

public class ConexaoBD {

    // Método responsável por abrir a porta do PostgreSQL
    public Connection conectarBD() {
        Connection conn = null;

        try {

            String url = "jdbc:postgresql://localhost:5432/DB_linguagenscrud?user=postgres&password=postgres";

            // Realiza a conexão oficial usando os dados da URL acima
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            // Se der erro (ex: banco desligado, senha ou nome errado), mostra no console
            System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }

        // Devolve a conexão pronta (ou nula, se falhou) para o DAO usar
        return conn;
    }
}