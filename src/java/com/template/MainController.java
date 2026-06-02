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

/*
 * O Controller é o "cérebro" da tela. Ele faz a ponte entre o visual (arquivo FXML)
 * e as regras de negócio / banco de dados (DAO e DTO).
 */
public class MainController {

    // ELEMENTOS DA TELA
    // A anotação @FXML avisa ao Java que essas variáveis estão ligadas a componentes visuais criados no Scene Builder.

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

    // DEPENDÊNCIAS E ESTADO
    // Instancia o DAO para conseguirmos usar os comandos de INSERT, UPDATE, DELETE e SELECT.
    private final LinguagemDAO linguagemDao = new LinguagemDAO();

    // ObservableList é uma lista especial do JavaFX. Se você adicionar ou remover algo dela, a tabela na tela é atualizada automaticamente em tempo real
    private final ObservableList<LinguagemDTO> listaLinguagens = FXCollections.observableArrayList();

    // INICIALIZAÇÃO
    // O método initialize() O JavaFX chama ele sozinho assim que a tela termina de carregar.
    @FXML
    public void initialize() {
        configurarComboBox();  // Coloca os itens na caixinha de seleção
        configurarColunas();   // Ensina as colunas a lerem os dados
        configurarEventos();   // Liga os botões às suas funções
        configurarPesquisa();  // Prepara a barra de pesquisa
        carregarTabela();      // Puxa os dados do banco na hora que abre o programa
    }

    //  Métodos de Configuração

    private void configurarComboBox() {
        // Criamos uma lista de Strings com as opções que queremos oferecer
        ObservableList<String> tipos = FXCollections.observableArrayList(
                "Orientado a Objetos",
                "Multiparadigma",
                "Funcional",
                "Baixo Nível/Sistemas",
                "Scripting"
        );
        // Jogamos essa lista dentro do ComboBox
        comboTipo.setItems(tipos);
    }

    private void configurarColunas() {
        // O PropertyValueFactory procura métodos "get" no DTO.
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCriador.setCellValueFactory(new PropertyValueFactory<>("criador"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoCriacao"));
    }

    private void configurarEventos() {
        // event -> Significa "Quando o botão for clicado, execute este método"
        btnSave.setOnAction(event -> salvarLinguagem());
        btnClear.setOnAction(event -> limparCampos());
        btnDelete.setOnAction(event -> excluirLinguagem());
        btnUpdate.setOnAction(event -> atualizarLinguagem());

        // Esse Listener fica observando a tabela. Sempre que o usuário clicar em uma linha(novaselecao) ele pega os dados dessa linha e joga nas caixas de texto para edição.
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, velhaSelecao, novaSelecao) -> {
            if (novaSelecao != null) { // Só preenche se o usuário realmente clicou em algo válido
                txtNome.setText(novaSelecao.getNome());
                txtCriador.setText(novaSelecao.getCriador());
                comboTipo.setValue(novaSelecao.getTipo());
                txtAno.setText(String.valueOf(novaSelecao.getAnoCriacao())); // Converte int pra String
            }
        });
    }

    // CRUD

    private void salvarLinguagem() {
        // Antes de salvar, chama a validação. Se estiver faltando dado, trava e avisa.
        if (!isCamposValidos()) {
            mostrarAlerta("Erro", "Por favor, preencha todos os campos corretamente antes de salvar!");
            return; // O 'return' cancela a continuação do método
        }

        try {
            // Cria um objeto em branco e vai "setando" os valores que o usuário digitou na tela
            LinguagemDTO novaLinguagem = new LinguagemDTO();

            // O .trim() corta os espaços em branco no começo e fim do texto. Ex: " Java " vira "Java"
            novaLinguagem.setNome(txtNome.getText().trim());
            novaLinguagem.setCriador(txtCriador.getText().trim());
            novaLinguagem.setTipo(comboTipo.getValue());
            novaLinguagem.setAnoCriacao(Integer.parseInt(txtAno.getText().trim())); // Transforma o texto do ano em Número Inteiro

            // Manda o objeto pronto pro DAO fazer o INSERT no banco de dados
            linguagemDao.cadastrarLinguagem(novaLinguagem);

            limparCampos();
            carregarTabela();
            mostrarAlerta("Sucesso", "Linguagem cadastrada com sucesso!");

        } catch (NumberFormatException e) {
            // Se o usuário digitou letras no campo "Ano", o Integer.parseInt() vai dar erro.
            // O bloco catch captura esse erro pra tela não travar e avisa o usuário.
            mostrarAlerta("Erro de Formato", "O campo 'Ano Criação' deve conter apenas números.");
        }
    }

    private void atualizarLinguagem() {
        // Descobre quem é a linguagem que está marcada na tabela neste exato momento
        LinguagemDTO linguagemSelecionada = tableView.getSelectionModel().getSelectedItem();

        if (linguagemSelecionada == null) {
            mostrarAlerta("Aviso", "Selecione uma linguagem na tabela para atualizar.");
            return;
        }

        if (!isCamposValidos()) {
            mostrarAlerta("Erro", "Por favor, preencha todos os campos corretamente antes de atualizar!");
            return;
        }

        try {
            // Modifica o objeto com as novas informações da tela
            linguagemSelecionada.setNome(txtNome.getText().trim());
            linguagemSelecionada.setCriador(txtCriador.getText().trim());
            linguagemSelecionada.setTipo(comboTipo.getValue());
            linguagemSelecionada.setAnoCriacao(Integer.parseInt(txtAno.getText().trim()));

            // Manda o objeto atualizado pro DAO fazer o UPDATE no banco
            linguagemDao.atualizarLinguagem(linguagemSelecionada);

            limparCampos();
            carregarTabela();
            mostrarAlerta("Sucesso", "Linguagem atualizada com sucesso!");

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "O campo 'Ano Criação' deve conter apenas números.");
        }
    }

    private void excluirLinguagem() {
        // Descobre quem foi clicado
        LinguagemDTO linguagemSelecionada = tableView.getSelectionModel().getSelectedItem();

        if (linguagemSelecionada != null) {

            linguagemDao.excluirLinguagem(linguagemSelecionada.getId());
            carregarTabela();
            mostrarAlerta("Sucesso", "Linguagem excluída com sucesso!");
        } else {

            mostrarAlerta("Aviso", "Selecione uma linguagem na tabela para excluir.");
        }
    }

    // metodo de apoio
    private void carregarTabela() {
        // O DAO faz um SELECT * no banco e devolve uma lista Java comum
        List<LinguagemDTO> listaBanco = linguagemDao.listarLinguagens();
        // O setAll substitui tudo que estava na tabela pela lista fresca que veio do banco
        listaLinguagens.setAll(listaBanco);
    }

    private void configurarPesquisa() {
        // Envolve a nossa lista em um "Filtro" (FilteredList)
        FilteredList<LinguagemDTO> dadosFiltrados = new FilteredList<>(listaLinguagens, p -> true);

        // Fica espionando a caixa de pesquisa. Se o texto mudar (newValue), roda a lógica:
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            dadosFiltrados.setPredicate(linguagem -> {
                // Se a caixa de pesquisa ficar vazia, mostra todo mundo
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                // Deixa o texto da pesquisa todo minúsculo para comparar sem problemas de Maiúscula/Minúscula
                String filtro = newValue.toLowerCase().trim();

                // Verifica se a palavra digitada está contida no Nome OU no Criador OU no Tipo.
                return linguagem.getNome().toLowerCase().contains(filtro)
                        || linguagem.getCriador().toLowerCase().contains(filtro)
                        || linguagem.getTipo().toLowerCase().contains(filtro);
            });
        });

        // Envolve a lista filtrada num Ordenador para as colunas conseguirem
        SortedList<LinguagemDTO> dadosOrdenados = new SortedList<>(dadosFiltrados);
        dadosOrdenados.comparatorProperty().bind(tableView.comparatorProperty());

        // joga o pacote completo (lista + filtro + ordem) na tabela visual
        tableView.setItems(dadosOrdenados);
    }

    private void limparCampos() {
        // Esvazia as caixas de texto
        txtNome.clear();
        txtCriador.clear();
        comboTipo.getSelectionModel().clearSelection(); // "Desseleciona" o item da lista
        txtAno.clear();
    }

    // Verifica se todos os campos estão preenchidos. Retorna true (verdadeiro) se estiver tudo ok, e false (falso) se algo estiver vazio.
    private boolean isCamposValidos() {
        // analisa se não é nulo ou se é vazio
        return txtNome.getText() != null && !txtNome.getText().trim().isEmpty() &&
                txtCriador.getText() != null && !txtCriador.getText().trim().isEmpty() &&
                comboTipo.getValue() != null &&
                txtAno.getText() != null && !txtAno.getText().trim().isEmpty();
    }

    // Facilita a criação das janelas de aviso
    // Em vez de escrever 5 linhas toda hora, escrevemos só chamar essa função passando o Título e a Mensagem.
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null); // Remove o cabeçalho padrão pra ficar mais limpo
        alerta.setContentText(mensagem);
        alerta.showAndWait(); // Mostra a tela e "congela" o fundo até o usuário clicar em OK
    }
}