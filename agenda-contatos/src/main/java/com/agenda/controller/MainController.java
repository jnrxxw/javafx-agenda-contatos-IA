package com.agenda.controller;

import com.agenda.dao.ContatoDAO;
import com.agenda.model.Contato;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da tela principal.
 *
 * No padrão MVC:
 *   Model      → Contato.java (dados)
 *   View       → main-view.fxml + style.css (o que o usuário vê)
 *   Controller → este arquivo (o que acontece quando o usuário interage)
 *
 * Os campos anotados com @FXML são "ligados" automaticamente
 * aos elementos com o mesmo fx:id no arquivo .fxml.
 */
public class MainController implements Initializable {

    // ── Referências aos elementos visuais (injetados pelo JavaFX via @FXML) ───
    @FXML private TableView<Contato>           tabelaContatos;
    @FXML private TableColumn<Contato, String> colNome;
    @FXML private TableColumn<Contato, String> colTelefone;
    @FXML private TableColumn<Contato, String> colEmail;
    @FXML private TableColumn<Contato, String> colEndereco;
    @FXML private TextField                    campoBusca;
    @FXML private Button                       btnEditar;
    @FXML private Button                       btnExcluir;
    @FXML private Label                        labelStatus;

    // ── Objetos de apoio ──────────────────────────────────────────────────────
    private ContatoDAO               dao;
    private ObservableList<Contato>  listaObservavel; // lista "reativa" ligada à tabela

    // ── Inicialização ─────────────────────────────────────────────────────────

    /**
     * Executado automaticamente quando a tela é aberta.
     * É aqui que configuramos a tabela e carregamos os dados iniciais.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new ContatoDAO();
        configurarTabela();
        carregarContatos();
    }

    /** Liga cada coluna da tabela ao getter correspondente em Contato.java */
    private void configurarTabela() {
        // PropertyValueFactory("nome") chama automaticamente contato.getNome()
        colNome     .setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone .setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEmail    .setCellValueFactory(new PropertyValueFactory<>("email"));
        colEndereco .setCellValueFactory(new PropertyValueFactory<>("endereco"));

        // Mensagem exibida quando não há contatos na tabela
        tabelaContatos.setPlaceholder(
            new Label("Nenhum contato cadastrado. Clique em '+ Novo Contato' para começar!")
        );

        // Listener: habilita/desabilita botões conforme algo está selecionado ou não
        tabelaContatos.getSelectionModel().selectedItemProperty().addListener(
            (observable, anterior, selecionado) -> {
                boolean temSelecionado = selecionado != null;
                btnEditar .setDisable(!temSelecionado);
                btnExcluir.setDisable(!temSelecionado);
            }
        );
    }

    /** Busca todos os contatos no banco e exibe na tabela */
    private void carregarContatos() {
        List<Contato> lista = dao.listarTodos();
        listaObservavel = FXCollections.observableArrayList(lista);
        tabelaContatos.setItems(listaObservavel);
        atualizarContador();
    }

    // ── Ações do usuário ──────────────────────────────────────────────────────

    /**
     * Chamado a cada tecla pressionada no campo de busca.
     * Filtra os contatos em tempo real.
     */
    @FXML
    private void buscarContato() {
        String texto = campoBusca.getText().trim();

        List<Contato> resultado = texto.isEmpty()
                ? dao.listarTodos()           // sem filtro: mostra todos
                : dao.buscarPorNome(texto);   // com filtro: só os que batem

        listaObservavel = FXCollections.observableArrayList(resultado);
        tabelaContatos.setItems(listaObservavel);
        atualizarContador();
    }

    /** Abre o formulário para cadastrar um novo contato */
    @FXML
    private void abrirFormularioNovo() {
        // null = modo "novo contato" (campos em branco)
        Dialog<Contato> dialog = criarFormulario(null);

        dialog.showAndWait().ifPresent(novoContato -> {
            if (dao.salvar(novoContato)) {
                carregarContatos();
                exibirMensagem(Alert.AlertType.INFORMATION, "Contato salvo com sucesso! ✅");
            }
        });
    }

    /** Abre o formulário preenchido com os dados do contato selecionado */
    @FXML
    private void editarContato() {
        Contato selecionado = tabelaContatos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        // Passamos o contato selecionado para pré-preencher o formulário
        Dialog<Contato> dialog = criarFormulario(selecionado);

        dialog.showAndWait().ifPresent(contatoEditado -> {
            contatoEditado.setId(selecionado.getId()); // mantém o mesmo ID
            if (dao.atualizar(contatoEditado)) {
                carregarContatos();
                exibirMensagem(Alert.AlertType.INFORMATION, "Contato atualizado com sucesso! ✅");
            }
        });
    }

    /** Pede confirmação e exclui o contato selecionado */
    @FXML
    private void excluirContato() {
        Contato selecionado = tabelaContatos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        // Caixa de confirmação antes de excluir
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Confirmar exclusão");
        confirmar.setHeaderText("Excluir \"" + selecionado.getNome() + "\"?");
        confirmar.setContentText("Esta ação não pode ser desfeita.");

        Optional<ButtonType> resposta = confirmar.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            if (dao.excluir(selecionado.getId())) {
                carregarContatos();
                exibirMensagem(Alert.AlertType.INFORMATION, "Contato excluído.");
            }
        }
    }

    // ── Auxiliares ────────────────────────────────────────────────────────────

    /**
     * Cria o formulário de diálogo (funciona tanto para novo quanto para edição).
     *
     * @param contato  null → formulário em branco (novo contato)
     *                 objeto → formulário pré-preenchido (edição)
     */
    private Dialog<Contato> criarFormulario(Contato contato) {
        boolean modoEdicao = contato != null;

        Dialog<Contato> dialog = new Dialog<>();
        dialog.setTitle(modoEdicao ? "Editar Contato" : "Novo Contato");
        dialog.setHeaderText(modoEdicao
                ? "Edite os dados do contato"
                : "Preencha os dados do novo contato");

        // Botões do diálogo
        ButtonType btnSalvar   = new ButtonType("Salvar",   ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, btnCancelar);

        // Campos de entrada
        TextField campoNome     = new TextField();
        TextField campoTelefone = new TextField();
        TextField campoEmail    = new TextField();
        TextField campoEndereco = new TextField();

        campoNome     .setPromptText("Nome completo *");
        campoTelefone .setPromptText("(xx) xxxxx-xxxx");
        campoEmail    .setPromptText("email@exemplo.com");
        campoEndereco .setPromptText("Rua, número, bairro, cidade...");
        campoNome     .setPrefWidth(280);

        // Se for edição, preenche os campos com os dados atuais
        if (modoEdicao) {
            campoNome     .setText(contato.getNome());
            campoTelefone .setText(contato.getTelefone());
            campoEmail    .setText(contato.getEmail());
            campoEndereco .setText(contato.getEndereco());
        }

        // Layout do formulário em grade (rótulo | campo)
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 24, 10, 24));

        grid.add(new Label("Nome *"),   0, 0);  grid.add(campoNome,     1, 0);
        grid.add(new Label("Telefone"), 0, 1);  grid.add(campoTelefone, 1, 1);
        grid.add(new Label("E-mail"),   0, 2);  grid.add(campoEmail,    1, 2);
        grid.add(new Label("Endereço"), 0, 3);  grid.add(campoEndereco, 1, 3);

        dialog.getDialogPane().setContent(grid);
        campoNome.requestFocus(); // coloca o cursor no primeiro campo

        // Define o que retornar quando o usuário clica em "Salvar"
        dialog.setResultConverter(botaoClicado -> {
            if (botaoClicado == btnSalvar) {
                String nome = campoNome.getText().trim();

                // Validação: nome é obrigatório
                if (nome.isEmpty()) {
                    exibirMensagem(Alert.AlertType.WARNING, "O nome é obrigatório!");
                    return null; // não fecha o diálogo
                }

                return new Contato(
                    nome,
                    campoTelefone .getText().trim(),
                    campoEmail    .getText().trim(),
                    campoEndereco .getText().trim()
                );
            }
            return null; // cancelou
        });

        return dialog;
    }

    /** Atualiza o contador de contatos exibido no rodapé */
    private void atualizarContador() {
        int total = listaObservavel != null ? listaObservavel.size() : 0;
        labelStatus.setText(switch (total) {
            case 0  -> "Nenhum contato";
            case 1  -> "1 contato";
            default -> total + " contatos";
        });
    }

    /** Exibe uma caixa de diálogo simples com uma mensagem */
    private void exibirMensagem(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Agenda de Contatos");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
