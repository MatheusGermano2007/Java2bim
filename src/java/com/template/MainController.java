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

    // ELEMENTOS DA TELA
    // O @FXML liga as caixinhas e botões lá do Scene Builder com o nosso código
    @FXML private TextField txtNome;
    @FXML private TextField txtCriador;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtAno;
    @FXML private TextField txtSearch;

    @FXML private Button btnSave;
    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    @FXML private TableView<LinguagemDTO> tableView;
    @FXML private TableColumn<LinguagemDTO, String> colNome;
    @FXML private TableColumn<LinguagemDTO, String> colCriador;
    @FXML private TableColumn<LinguagemDTO, String> colTipo;
    @FXML private TableColumn<LinguagemDTO, Integer> colAno;

    // Ferramenta que conversa com o banco de dados
    private LinguagemDao linguagemDao = new LinguagemDao();

    // A lista oficial que vai segurar os dados para a tabela mostrar
    private ObservableList<LinguagemDTO> listaLinguagens = FXCollections.observableArrayList();

    // INICIALIZAÇÃO
    // Esse método roda sozinho assim que a tela abre
    @FXML
    public void initialize() {
        // Coloca as opções dentro da caixinha de Tipos
        ObservableList<String> tipos = FXCollections.observableArrayList(
                "Orientado a Objetos",
                "Multiparadigma",
                "Funcional",
                "Baixo Nível/Sistemas",
                "Scripting"
        );
        comboTipo.setItems(tipos);

        // Ensina cada coluna da tabela de onde ela deve puxar os dados do DTO
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCriador.setCellValueFactory(new PropertyValueFactory<>("criador"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoCriacao"));

        // Puxa os dados do banco e liga a pesquisa
        carregarTabela();
        configurarPesquisa();

        // Diz o que cada botão vai fazer quando for clicado
        btnSave.setOnAction(event -> salvarLinguagem());
        btnClear.setOnAction(event -> limparCampos());
        btnDelete.setOnAction(event -> excluirLinguagem());
        // Preenche os campos de texto ao clicar em uma linha da tabela
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, velhaSelecao, novaSelecao) -> {
            if (novaSelecao != null) {
                txtNome.setText(novaSelecao.getNome());
                txtCriador.setText(novaSelecao.getCriador());
                comboTipo.setValue(novaSelecao.getTipo());
                txtAno.setText(String.valueOf(novaSelecao.getAnoCriacao()));
            }
        });

        // Diz o que o botão de atualizar vai fazer
        btnUpdate.setOnAction(event -> atualizarLinguagem());
    }

    // AÇÕES DOS BOTÕES

    private void salvarLinguagem() {
        // Trava se o usuário esquecer de preencher algo
        if (txtNome.getText().isEmpty() || txtCriador.getText().isEmpty() || comboTipo.getValue() == null || txtAno.getText().isEmpty()) {
            mostrarAlerta("Erro", "Por favor, preencha todos os campos antes de salvar!");
            return;
        }

        try {
            // Cria uma "caixa" (DTO) e enfia os dados da tela lá dentro
            LinguagemDTO novaLinguagem = new LinguagemDTO();
            novaLinguagem.setNome(txtNome.getText());
            novaLinguagem.setCriador(txtCriador.getText());
            novaLinguagem.setTipo(comboTipo.getValue());
            novaLinguagem.setAnoCriacao(Integer.parseInt(txtAno.getText()));

            // Manda a caixa pro DAO salvar no banco
            linguagemDao.cadastrarLinguagem(novaLinguagem);

            // Deixa tudo limpo e atualiza a tabela pra mostrar o item novo
            limparCampos();
            carregarTabela();
            mostrarAlerta("Sucesso", "Linguagem cadastrada com sucesso!");

        } catch (NumberFormatException e) {
            // Se o cara digitar letras no ano, o parseInt() surta e cai aqui
            mostrarAlerta("Erro de Formato", "O campo 'Ano Criação' deve conter apenas números.");
        }
    }

    private void excluirLinguagem() {
        // Pega exatamente a linha que o usuário clicou na tabela
        LinguagemDTO linguagemSelecionada = tableView.getSelectionModel().getSelectedItem();

        if (linguagemSelecionada != null) {
            // Manda o ID dessa linha pro DAO apagar lá no banco
            linguagemDao.excluirLinguagem(linguagemSelecionada.getId());

            carregarTabela(); // Recarrega a tela sem o item apagado
            mostrarAlerta("Sucesso", "Linguagem excluída com sucesso!");
        } else {
            mostrarAlerta("Aviso", "Selecione uma linguagem na tabela para excluir.");
        }
    }

    // MÉTODOS DE APOIO

    private void carregarTabela() {
        // Pede os dados atualizados pro banco e joga na lista da tabela
        List<LinguagemDTO> listaBanco = linguagemDao.listarLinguagens();
        listaLinguagens.setAll(listaBanco);
    }

    private void configurarPesquisa() {
        // Cria uma lista especial que aceita filtros
        FilteredList<LinguagemDTO> dadosFiltrados = new FilteredList<>(listaLinguagens, p -> true);

        // Fica de olho na barra de pesquisa. Se digitar algo, roda esse código:
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            dadosFiltrados.setPredicate(linguagem -> {
                // Se apagar tudo, mostra todas as linguagens de novo
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Joga tudo pra minúsculo pra facilitar a busca (ignora Maiúsculas)
                String filtroMinusculo = newValue.toLowerCase();

                // Procura a palavra no Nome, Criador ou Tipo
                if (linguagem.getNome().toLowerCase().contains(filtroMinusculo)) {
                    return true;
                } else if (linguagem.getCriador().toLowerCase().contains(filtroMinusculo)) {
                    return true;
                } else if (linguagem.getTipo().toLowerCase().contains(filtroMinusculo)) {
                    return true;
                }

                return false; // Se não achar nada, esconde a linha
            });
        });

        // Liga a lista filtrada na tabela de um jeito que ainda dê pra ordenar
        SortedList<LinguagemDTO> dadosOrdenados = new SortedList<>(dadosFiltrados);
        dadosOrdenados.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(dadosOrdenados);
    }

    private void limparCampos() {
        // Apaga as caixas de texto para a proxima filtragem
        txtNome.clear();
        txtCriador.clear();
        comboTipo.getSelectionModel().clearSelection();
        txtAno.clear();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        // Cria aqueles pop-ups (janelinhas) de aviso na tela
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
    private void atualizarLinguagem() {
        // Pega a linha que está selecionada na tabela
        LinguagemDTO linguagemSelecionada = tableView.getSelectionModel().getSelectedItem();

        if (linguagemSelecionada == null) {
            mostrarAlerta("Aviso", "Selecione uma linguagem na tabela para atualizar.");
            return;
        }

        try {
            // Atualiza o DTO com o que está digitado nas caixinhas agora
            linguagemSelecionada.setNome(txtNome.getText());
            linguagemSelecionada.setCriador(txtCriador.getText());
            linguagemSelecionada.setTipo(comboTipo.getValue());
            linguagemSelecionada.setAnoCriacao(Integer.parseInt(txtAno.getText()));

            // Manda pro banco salvar a alteração
            linguagemDao.atualizarLinguagem(linguagemSelecionada);

            limparCampos();
            carregarTabela(); // Atualiza a tela
            mostrarAlerta("Sucesso", "Linguagem atualizada com sucesso!");

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "O campo 'Ano Criação' deve conter apenas números.");
        }
    }
}