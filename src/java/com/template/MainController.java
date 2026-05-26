package com.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MainController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCriador;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtAno;
    @FXML private TextField txtSearch;

    @FXML private Button btnSave;
    @FXML private Button btnClear;
    @FXML private Button btnDelete;

    @FXML private TableView<LinguagemDTO> tableView;
    @FXML private TableColumn<LinguagemDTO, String> colNome;
    @FXML private TableColumn<LinguagemDTO, String> colCriador;
    @FXML private TableColumn<LinguagemDTO, String> colTipo;
    @FXML private TableColumn<LinguagemDTO, Integer> colAno;

    private LinguagemDao linguagemDao = new LinguagemDao();

    private ObservableList<LinguagemDTO> listaLinguagens = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ObservableList<String> tipos = FXCollections.observableArrayList(
                "Orientado a Objetos",
                "Multiparadigma",
                "Funcional",
                "Baixo Nível/Sistemas",
                "Scripting"
        );
        comboTipo.setItems(tipos);

        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCriador.setCellValueFactory(new PropertyValueFactory<>("criador"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoCriacao"));

        carregarTabela();
        configurarPesquisa();

        btnSave.setOnAction(event -> salvarLinguagem());
        btnClear.setOnAction(event -> limparCampos());
        btnDelete.setOnAction(event -> excluirLinguagem());
    }

    private void salvarLinguagem() {
        if (txtNome.getText().isEmpty() || txtCriador.getText().isEmpty() || comboTipo.getValue() == null || txtAno.getText().isEmpty()) {
            mostrarAlerta("Erro", "Por favor, preencha todos os campos antes de salvar!");
            return;
        }

        try {
            LinguagemDTO novaLinguagem = new LinguagemDTO();
            novaLinguagem.setNome(txtNome.getText());
            novaLinguagem.setCriador(txtCriador.getText());
            novaLinguagem.setTipo(comboTipo.getValue());
            novaLinguagem.setAnoCriacao(Integer.parseInt(txtAno.getText()));

            linguagemDao.cadastrarLinguagem(novaLinguagem);

            limparCampos();
            carregarTabela();
            mostrarAlerta("Sucesso", "Linguagem cadastrada com sucesso!");

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "O campo 'Ano Criação' deve conter apenas números.");
        }
    }

    private void excluirLinguagem() {
        LinguagemDTO linguagemSelecionada = tableView.getSelectionModel().getSelectedItem();

        if (linguagemSelecionada != null) {
            linguagemDao.excluirLinguagem(linguagemSelecionada.getId());
            carregarTabela();
            mostrarAlerta("Sucesso", "Linguagem excluída com sucesso!");
        } else {
            mostrarAlerta("Aviso", "Selecione uma linguagem na tabela para excluir.");
        }
    }

    private void carregarTabela() {
        List<LinguagemDTO> listaBanco = linguagemDao.listarLinguagens();
        listaLinguagens.setAll(listaBanco);
    }

    private void configurarPesquisa() {
        FilteredList<LinguagemDTO> dadosFiltrados = new FilteredList<>(listaLinguagens, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            dadosFiltrados.setPredicate(linguagem -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String filtroMinusculo = newValue.toLowerCase();

                if (linguagem.getNome().toLowerCase().contains(filtroMinusculo)) {
                    return true;
                } else if (linguagem.getCriador().toLowerCase().contains(filtroMinusculo)) {
                    return true;
                } else if (linguagem.getTipo().toLowerCase().contains(filtroMinusculo)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<LinguagemDTO> dadosOrdenados = new SortedList<>(dadosFiltrados);
        dadosOrdenados.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(dadosOrdenados);
    }

    private void limparCampos() {
        txtNome.clear();
        txtCriador.clear();
        comboTipo.getSelectionModel().clearSelection();
        txtAno.clear();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}