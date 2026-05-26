package com.template;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    public Connection conectarBD() {
        Connection conn = null;
        try {
            String url = "jdbc:postgresql://localhost:5432/DB_linguagenscrud?user=postgres&password=postgres";

            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
        return conn;
    }
}

