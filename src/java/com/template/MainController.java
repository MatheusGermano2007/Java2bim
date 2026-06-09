package com.template;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.dao.LinguagemDAO;
import model.dto.LinguagemDTO;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Componentes do FXML exatos como estão no seu layout
    @FXML private Label lblTotal;
    @FXML private TableView<LinguagemDTO> tableView;
    @FXML private TableColumn<LinguagemDTO, String> colNome;
    @FXML private TableColumn<LinguagemDTO, String> colCriador;
    @FXML private TableColumn<LinguagemDTO, String> colTipo;
    @FXML private TableColumn<LinguagemDTO, String> colAno;

    @FXML private TextField txtSearch;
    @FXML private TextField txtNome;
    @FXML private TextField txtCriador;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtAno;

    @FXML private Button btnSave;
    @FXML private Button btnClear;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    @FXML private Label lblMensagem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configura as colunas da Tabela
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCriador.setCellValueFactory(new PropertyValueFactory<>("criador"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoCriacao"));

        recarregarTabela(); // Carrega os dados do banco

        // 2. Configura a ação de clique na tabela (Habilita Atualizar e Excluir)
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, selecaoAntiga, novaSelecao) -> {
            boolean temSelecao = (novaSelecao != null);
            btnUpdate.setDisable(!temSelecao);
            btnDelete.setDisable(!temSelecao);

            if (temSelecao) {
                txtNome.setText(novaSelecao.getNome());
                txtCriador.setText(novaSelecao.getCriador());
                comboTipo.setValue(novaSelecao.getTipo());
                txtAno.setText(String.valueOf(novaSelecao.getAnoCriacao()));
            }
        });

        // 3. Preenche as opções do ComboBox (Tipo)
        comboTipo.getItems().addAll("Orientado a Objetos", "Funcional", "Backend", "Frontend");

        // 4. Configura os bloqueios dos botões Salvar e Limpar
        if (btnSave != null) btnSave.setDisable(true);
        if (btnClear != null) btnClear.setDisable(true);

        javafx.beans.value.ChangeListener<String> validador = (obs, antigo, novo) -> {
            boolean algumCampoVazio = txtNome.getText().trim().isEmpty() ||
                    txtCriador.getText().trim().isEmpty() ||
                    txtAno.getText().trim().isEmpty();

            boolean tudoVazio = txtNome.getText().trim().isEmpty() &&
                    txtCriador.getText().trim().isEmpty() &&
                    txtAno.getText().trim().isEmpty();

            if (btnSave != null) btnSave.setDisable(algumCampoVazio);
            if (btnClear != null) btnClear.setDisable(tudoVazio);
        };

        // Adiciona a validação aos campos de texto
        txtNome.textProperty().addListener(validador);
        txtCriador.textProperty().addListener(validador);
        txtAno.textProperty().addListener(validador);

        // 5. Configura a barra de pesquisa (Filtro em tempo real)
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, antigo, novo) -> {
                recarregarTabela();
            });
        }
    }
    // UX 3: Método para exibir mensagens dinâmicas de feedback na barra inferior
    private void mostrarMensagem(String texto, boolean isSucesso) {
        lblMensagem.setText(texto);
        lblMensagem.getStyleClass().removeAll("msg-sucesso", "msg-erro");
        if (isSucesso) {
            lblMensagem.getStyleClass().add("msg-sucesso");
            lblMensagem.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;"); // Força a cor verde
        } else {
            lblMensagem.getStyleClass().add("msg-erro");
            lblMensagem.setStyle("-fx-text-fill: #ff4c4c; -fx-font-weight: bold;"); // Força a cor vermelha
        }
    }

    @FXML
    private void acaoSalvar() {
        // 1. PRIMEIRO: Criamos a variável 'dto' e preenchemos ela com os dados da tela
        LinguagemDTO dto = new LinguagemDTO();
        dto.setNome(txtNome.getText());
        dto.setCriador(txtCriador.getText());
        dto.setTipo(comboTipo.getValue());

        // Lembra da conversão para número que fizemos lá atrás? Ela entra aqui!
        try {
            dto.setAnoCriacao(Integer.parseInt(txtAno.getText()));
        } catch (NumberFormatException e) {
            mostrarMensagem("Erro: O ano deve ser apenas números!", false);
            return; // Para a execução se o cara digitar letras no ano
        }

        // 2. SEGUNDO: Agora sim mandamos salvar no banco de dados!
        try {
            LinguagemDAO dao = new LinguagemDAO();

            // Agora o Java sabe quem é o 'dto', pois criamos ele ali em cima!
            dao.cadastrarLinguagem(dto);

            recarregarTabela(); // Atualiza a tabela na mesma hora
            limparCampos();     // Limpa a tela

            mostrarMensagem("Linguagem salva com sucesso!", true);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensagem("Erro ao salvar: " + e.getMessage(), false);
        }
    }

    @FXML
    private void acaoExcluir() {
        LinguagemDTO selecionada = tableView.getSelectionModel().getSelectedItem();

        if (selecionada != null) {
            // 1. Cria a caixinha de confirmação
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmação de Exclusão");
            alerta.setHeaderText("Você está prestes a excluir: " + selecionada.getNome());
            alerta.setContentText("Tem certeza que deseja apagar esta linguagem permanentemente?");

            // 2. Mostra a caixinha e verifica qual botão foi clicado
            java.util.Optional<javafx.scene.control.ButtonType> resultado = alerta.showAndWait();

            if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK) {
                // Se clicou em OK, executa a exclusão
                try {
                    LinguagemDAO dao = new LinguagemDAO();
                    // AJUSTE AQUI: Use o nome do seu método de excluir no DAO (ex: deletar, excluir)
                    dao.excluirLinguagem(selecionada.getId());

                    recarregarTabela();
                    limparCampos();
                    mostrarMensagem("Linguagem excluída!", true);
                } catch (Exception e) {
                    mostrarMensagem("Erro ao excluir: " + e.getMessage(), false);
                }
            }
        }
    }

    @FXML
    public void acaoAtualizar() {
        LinguagemDTO selecionada = tableView.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            // Lógica de atualização aqui
            mostrarMensagem("Linguagem atualizada com sucesso!", true);
            tableView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void limparCampos() {
        txtNome.clear();
        txtCriador.clear();
        comboTipo.getSelectionModel().clearSelection();
        txtAno.clear();
        txtNome.requestFocus(); // Volta o ponteiro do teclado para o primeiro campo
        mostrarMensagem("Campos limpos. Sistema pronto.", true);
        lblMensagem.setStyle("-fx-text-fill: #888;"); // Volta para cor neutra
    }

    private void recarregarTabela() {
        try {
            LinguagemDAO dao = new LinguagemDAO();
            List<LinguagemDTO> listaBanco = dao.listarLinguagens();

            // LÓGICA DO FILTRO
            String busca = txtSearch.getText().toLowerCase(); // Pega o texto e deixa minúsculo
            List<LinguagemDTO> listaFiltrada = new java.util.ArrayList<>();

            for (LinguagemDTO lang : listaBanco) {
                // Se o nome ou criador contiverem a letra digitada, adiciona na lista
                if (lang.getNome().toLowerCase().contains(busca) ||
                        lang.getCriador().toLowerCase().contains(busca)) {
                    listaFiltrada.add(lang);
                }
            }

            tableView.getItems().setAll(listaFiltrada); // Joga só os filtrados na tabela

            if (lblTotal != null) {
                lblTotal.setText("Total cadastrado: " + listaFiltrada.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}