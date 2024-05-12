package g03mysimpleirtool.controller;

import g03mysimpleirtool.G03MySimpleIRTool;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_ABOUT_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_ABOUT_VIEW;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_DOCUMENT_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_FOLDER_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_HELP_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_HELP_VIEW;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_NAME;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_PREFS_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_PREFS_VIEW;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_RESULT_FRAGMENT;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_TREEITEM_FRAGMENT;
import g03mysimpleirtool.model.Dictionary;
import g03mysimpleirtool.model.Status;
import g03mysimpleirtool.model.TFDocumentModel;
import g03mysimpleirtool.service.IndexingService;
import g03mysimpleirtool.service.PreProcessingService;
import g03mysimpleirtool.service.QueryService;
import static g03mysimpleirtool.util.Dialogs.askYesNo;
import static g03mysimpleirtool.util.Dialogs.chooseDirectory;
import static g03mysimpleirtool.util.Dialogs.revealInFolder;
import static g03mysimpleirtool.util.Dialogs.showDocumentViewer;
import static g03mysimpleirtool.util.Dialogs.showError;
import static g03mysimpleirtool.util.Dialogs.showInformation;
import static g03mysimpleirtool.util.Dialogs.showStage;
import static g03mysimpleirtool.util.Dialogs.showStatsViewer;
import static g03mysimpleirtool.util.Statistics.calculateSummaryStatistics;
import static g03mysimpleirtool.util.Statistics.calculateWordFrequencies;
import static g03mysimpleirtool.util.Statistics.findLeastFrequentWord;
import static g03mysimpleirtool.util.Statistics.findMostFrequentWord;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.DoubleSummaryStatistics;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 * La classe {@code G03MySimpleIRToolController} rappresenta il controller MVC
 * dell'applicazione JavaFX. Fornisce i metodi handler per la gestione degli
 * eventi di interfaccia grafica.
 */
public class G03MySimpleIRToolController implements Initializable {

    @FXML
    private MenuItem miApriCartella;

    @FXML
    private MenuItem miChiudiCartella;

    @FXML
    private MenuItem miAnnulla;

    @FXML
    private MenuItem miRipeti;

    @FXML
    private MenuItem miTaglia;

    @FXML
    private MenuItem miCopia;

    @FXML
    private MenuItem miIncolla;

    @FXML
    private MenuItem miElimina;

    @FXML
    private MenuItem miDeselezionaTutto;

    @FXML
    private MenuItem miEseguiQuery;

    @FXML
    private Button btnApriCartella;

    @FXML
    private Button btnChiudiCartella;

    @FXML
    private Button btnEseguiQuery;

    @FXML
    private TreeView<Path> treeView;

    @FXML
    private TableView<Pair<String, String>> tblStatistiche;

    @FXML
    private TableColumn<Pair<String, String>, String> tcProprieta;

    @FXML
    private TableColumn<Pair<String, String>, String> tcValore;

    @FXML
    private TextArea txtQuery;

    @FXML
    private ListView<TFDocumentModel> lstResults;

    @FXML
    private Label lblStatus;

    @FXML
    private ProgressBar pbStatus;

    /**
     * Definisce lo {@code Stage} principale.
     */
    private Stage mainStage;

    /**
     * Definisce la {@code Clipboard} per le operazioni di modifica.
     */
    private Clipboard clipboard;

    /**
     * Indica se la {@code Clipboard} contiene una stringa di caratteri.
     */
    private BooleanProperty clipboardHasString;

    /**
     * Definisce la lista osservabile contenente le statistiche collection-wide
     * per il folder corrente.
     */
    private ObservableList<Pair<String, String>> currentStats;

    /**
     * Definisce il task attualmente in esecuzione.
     */
    private ObjectProperty<Service<?>> currentTask;

    /**
     * Definisce lo stato attuale dell'applicazione.
     */
    private ObjectProperty<Status> currentStatus;

    /**
     * Definisce il progresso dell'operazione attualmente in esecuzione.
     */
    private DoubleProperty currentProgress;

    /**
     * Definisce la file attualmente aperta.
     */
    private ObjectProperty<File> currentDirectory;

    /**
     * Definisce il {@code Set} di modelli TF (Text Frequency) per la collezione
     * di documenti corrente.
     */
    private ObjectProperty<Set<TFDocumentModel>> currentModels;

    /**
     * Inizializza il controller.
     *
     * @param url URL utilizzata per risolvere i percorsi relativi dell'oggetto
     * radice, o null se la posizione non è nota.
     * @param rb Risorse utilizzate per localizzare l'oggetto radice, o null se
     * l'oggetto radice non è stato localizzato.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clipboard = Clipboard.getSystemClipboard();
        clipboardHasString = new SimpleBooleanProperty(clipboard.hasString());
        currentStats = FXCollections.observableArrayList();
        tcProprieta.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getKey()));
        tcValore.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getValue()));
        tblStatistiche.setItems(currentStats);
        currentTask = new SimpleObjectProperty<>();
        currentStatus = new SimpleObjectProperty<>(Status.READY);
        currentProgress = new SimpleDoubleProperty();
        currentDirectory = new SimpleObjectProperty<>();
        currentModels = new SimpleObjectProperty<>();
        initBindings();
        initTreeView();
        initResultsView();
        Platform.runLater(() -> {
            mainStage = (Stage) txtQuery.getScene().getWindow();
            mainStage.setOnCloseRequest(this::onCloseRequest);
            reopenLastFolder();
        });
    }

    /**
     * Handler per la richiesta di chiusura dell'applicazione.
     *
     * @param event Evento JavaFX.
     */
    private void onCloseRequest(WindowEvent event) {
        if (currentTask.get() != null) {
            if (askYesNo("Uscita dal programma",
                    "Interrompere il task in esecuzione?")) {
                currentTask.get().cancel();
                esci(null);
            } else {
                event.consume();
            }
        } else {
            esci(null);
        }
    }

    /**
     * Inizializza i binding.
     */
    private void initBindings() {
        initMenuBindings();
        btnApriCartella.disableProperty().bind(currentStatus.isNotEqualTo(Status.READY));
        btnChiudiCartella.disableProperty().bind(currentDirectory.isNull()
                .or(currentStatus.isNotEqualTo(Status.READY)));
        btnEseguiQuery.disableProperty().bind(currentDirectory.isNull()
                .or(currentModels.isNull())
                .or(Bindings.createBooleanBinding(() -> txtQuery.getText().trim().isEmpty(),
                        txtQuery.textProperty()))
                .or(currentStatus.isNotEqualTo(Status.READY)));
        txtQuery.disableProperty().bind(currentDirectory.isNull()
                .or(currentModels.isNull()));
        lblStatus.textProperty().bind(Bindings.createStringBinding(
                () -> currentStatus.get() == Status.READY
                ? Status.READY.getMessage()
                : String.format("%s (%s%%)", currentStatus.get().getMessage(),
                        Math.max(0, Math.min(100, Math.round(100 * currentProgress.get())))),
                currentStatus, currentProgress
        ));
        pbStatus.progressProperty().bind(currentProgress);
    }

    /**
     * Inizializza i binding relativi ai menu.
     */
    private void initMenuBindings() {
        miApriCartella.disableProperty().bind(currentStatus.isNotEqualTo(Status.READY));
        miChiudiCartella.disableProperty().bind(currentDirectory.isNull()
                .or(currentStatus.isNotEqualTo(Status.READY)));
        miAnnulla.disableProperty().bind(txtQuery.undoableProperty().not());
        miRipeti.disableProperty().bind(txtQuery.redoableProperty().not());
        miTaglia.disableProperty().bind(txtQuery.selectedTextProperty().isEmpty());
        miCopia.disableProperty().bind(txtQuery.selectedTextProperty().isEmpty());
        miIncolla.disableProperty().bind(clipboardHasString.not());
        miElimina.disableProperty().bind(txtQuery.selectedTextProperty().isEmpty());
        miDeselezionaTutto.disableProperty().bind(txtQuery.selectedTextProperty().isEmpty());
        miEseguiQuery.disableProperty().bind(currentDirectory.isNull()
                .or(currentModels.isNull())
                .or(Bindings.createBooleanBinding(() -> txtQuery.getText().trim().isEmpty(),
                        txtQuery.textProperty()))
                .or(currentStatus.isNotEqualTo(Status.READY)));
    }

    /**
     * Inizializza la visualizzazione della file corrente.
     */
    private void initTreeView() {
        treeView.setCellFactory((TreeView<Path> param) -> new DirectoryItemTreeCellImpl());
    }

    /**
     * Inizializza la visualizzazione della lista dei risultati.
     */
    private void initResultsView() {
        lstResults.setCellFactory((ListView<TFDocumentModel> param) -> new ResultItemListCellImpl());
    }

    /**
     * Riapre l'ultimo folder su cui si stava lavorando, se presente.
     */
    private void reopenLastFolder() {
        final Preferences preferences = Preferences.userNodeForPackage(G03MySimpleIRTool.class);
        String lastFolderPath = preferences.get("path", null);
        if (lastFolderPath != null) {
            File lastFolder = new File(lastFolderPath);
            if (Files.exists(Paths.get(lastFolder.toURI()))) {
                currentDirectory.set(new File(lastFolderPath));
                mainStage.setTitle(currentDirectory.get().getAbsolutePath()
                        + " — " + APP_NAME);
                try {
                    performIndexing(this::performPreProcessing);
                } catch (IOException ex) {
                    showError("Errore durante l'apertura della cartella.");
                }
            }
        }
    }

    /**
     * La classe {@code DirectoryItemTreeCellImpl} specializza
     * {@code TreeCell<Path>} e ridefinisce l'aspetto dei {@code TreeItem}.
     */
    private class DirectoryItemTreeCellImpl extends TreeCell<Path> {

        /**
         * Definisce l'icona delle entry che corrispondono a folder.
         */
        private final Image folderIcon;

        /**
         * Definisce l'icona delle entry che corrispondono a documenti.
         */
        private final Image documentIcon;

        /**
         * Definisce il menu contestuale per i documenti.
         */
        private final ContextMenu contextMenu;

        /**
         * Costruttore.
         */
        DirectoryItemTreeCellImpl() {
            folderIcon = new Image(
                    G03MySimpleIRTool.class.getResourceAsStream(APP_FOLDER_ICON));
            documentIcon = new Image(
                    G03MySimpleIRTool.class.getResourceAsStream(APP_DOCUMENT_ICON));
            contextMenu = new ContextMenu();
            initContextMenu();
        }

        /**
         * Inizializza il menu contestuale.
         */
        private void initContextMenu() {
            final MenuItem miOpen = new MenuItem("Visualizza documento");
            final MenuItem miStats = new MenuItem("Visualizza statistiche");
            final MenuItem miReveal = new MenuItem("Mostra nella cartella");
            contextMenu.getItems().setAll(miOpen, miStats, miReveal);
            miOpen.setOnAction(event -> showDocumentViewer(getItem()));
            miStats.setOnAction(event -> showStatsViewer(getItem()));
            miReveal.setOnAction(event -> revealInFolder(getItem()));
        }

        /**
         * Ridefinisce la visualizzazione della {@code TreeCell<Path>}.
         *
         * @param item Il nuovo elemento per la cella.
         * @param empty Se questa cella rappresenta o meno i dati dell'elenco.
         * Se è vuota, non rappresenta alcun dato del dominio, ma è una cella
         * utilizzata per rendere una riga "vuota".
         */
        @Override
        protected void updateItem(Path item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                try {
                    final HBox entry = FXMLLoader.load(
                            G03MySimpleIRTool.class.getResource(APP_TREEITEM_FRAGMENT));
                    final ImageView icon = (ImageView) entry.lookup("#icon");
                    icon.setImage(Files.isDirectory(item) ? folderIcon : documentIcon);
                    final Label name = (Label) entry.lookup("#name");
                    name.setText(item.getFileName().toString());
                    if (Files.isRegularFile(item)) {
                        entry.setOnMouseClicked(event -> {
                            if (event.getButton().equals(MouseButton.PRIMARY)
                                    && event.getClickCount() == 2) {
                                showDocumentViewer(item);
                            }
                        });
                        entry.setOnContextMenuRequested(event -> {
                            contextMenu.show(entry.getScene().getWindow(),
                                    event.getScreenX(), event.getScreenY());
                        });
                    }
                    setGraphic(entry);
                } catch (IOException ex) {
                    showError("Errore durante la visualizzazione della directory.");
                }
            }
        }

    }

    /**
     * La classe {@code ResultItemListCellImpl} specializza
     * {@code ListCell<TFDocumentModel>} e ridefinisce l'aspetto dei risultati.
     */
    private class ResultItemListCellImpl extends ListCell<TFDocumentModel> {

        /**
         * Ridefinisce la visualizzazione della
         * {@code ListCell<TFDocumentModel>}.
         *
         * @param item Il nuovo elemento per la cella.
         * @param empty Se questa cella rappresenta o meno i dati dell'elenco.
         * Se è vuota, non rappresenta alcun dato del dominio, ma è una cella
         * utilizzata per rendere una riga "vuota".
         */
        @Override
        protected void updateItem(TFDocumentModel item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                try {
                    final FXMLLoader loader = new FXMLLoader(G03MySimpleIRTool.class.getResource(APP_RESULT_FRAGMENT));
                    loader.setController(new G03MySimpleIRToolResultController(item));
                    setGraphic(loader.load());
                } catch (IOException ex) {
                    showError("Errore durante la visualizzazione del risultato.");
                }
            }
        }

    }

    /**
     * Esegue l'indicizzazione.
     *
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    private void performIndexing() throws IOException {
        performIndexing(null);
    }

    /**
     * Esegue l'indicizzazione.
     *
     * @param onFinished Callback eseguita al termine dell'operazione.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    public void performIndexing(Runnable onFinished) throws IOException {
        if (currentDirectory.get() == null) {
            if (!treeView.rootProperty().isBound()) {
                treeView.setRoot(null);
            }
            return;
        }
        currentStatus.set(Status.INDEXING);
        final Path current = Paths.get(currentDirectory.get().toURI());
        IndexingService indexingService = new IndexingService(current);
        currentProgress.bind(indexingService.progressProperty());
        treeView.rootProperty().bind(indexingService.valueProperty());
        indexingService.start();
        currentTask.set(indexingService);
        indexingService.setOnSucceeded(event -> {
            treeView.rootProperty().unbind();
            currentProgress.unbind();
            currentTask.set(null);
            currentProgress.set(0);
            currentStatus.set(Status.READY);
            if (onFinished != null) {
                onFinished.run();
            }
        });
    }

    /**
     * Esegue la pre-elaborazione.
     */
    public void performPreProcessing() {
        if (currentDirectory.get() == null) {
            return;
        }
        currentStatus.set(Status.PREPROCESSING);
        String current = currentDirectory.get().getAbsolutePath();
        PreProcessingService preprocessingService = new PreProcessingService(current);
        currentModels.bind(preprocessingService.valueProperty());
        currentProgress.bind(preprocessingService.progressProperty());
        preprocessingService.start();
        currentTask.set(preprocessingService);
        preprocessingService.setOnSucceeded(event -> {
            currentModels.unbind();
            currentProgress.unbind();
            currentTask.set(null);
            currentProgress.set(0);
            currentStatus.set(Status.READY);
            updateCollectionStats();
        });
    }

    /**
     * Effettua l'aggiornamento delle statistiche sulla collezione.
     */
    private void updateCollectionStats() {
        if (currentModels.get() == null) {
            return;
        }
        final Set<TFDocumentModel> current = currentModels.get();
        final Map<String, Long> wordFrequencies = calculateWordFrequencies(current);
        final DoubleSummaryStatistics statistics = calculateSummaryStatistics(wordFrequencies);
        final UnaryOperator<String> capitalize = s -> s.isEmpty()
                ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
        currentStats.clear();
        addToStatistics("Parola più frequente", capitalize.apply(findMostFrequentWord(wordFrequencies)));
        addToStatistics("Parola meno frequente", capitalize.apply(findLeastFrequentWord(wordFrequencies)));
        addToStatistics("Frequenza massima", Long.toString(statistics.getMax() == Double.NEGATIVE_INFINITY ? 0 : (long) statistics.getMax()));
        addToStatistics("Frequenza media", String.format(Locale.US, "%.2f", statistics.getAverage()));
        addToStatistics("Frequenza minima", Long.toString(statistics.getMin() == Double.POSITIVE_INFINITY ? 0 : (long) statistics.getMin()));
        addToStatistics("Dimensione del dizionario", Integer.toString(new Dictionary(current).getBagOfWords().size()));
        addToStatistics("Numero di documenti", Integer.toString(current.size()));
    }

    /**
     * Aggiunge la coppia proprietà-valore alle statistiche per la collezione
     * corrente.
     *
     * @param property Nome della proprietà.
     * @param value Valore della proprietà.
     */
    private void addToStatistics(String property, String value) {
        currentStats.add(new Pair<>(property, value));
    }

    /**
     * Gestisce l'evento di trascinamento di un folder nell'area di
     * visualizzazione della file ({@code TreeView}).
     *
     * @param dragEvent Evento JavaFX di trascinamento.
     */
    @FXML
    private void trascinaFolder(DragEvent dragEvent) {
        final Dragboard dragboard = dragEvent.getDragboard();
        if (dragEvent.getGestureSource() != treeView
                && dragboard.hasFiles()) {
            if (dragboard.getFiles().size() == 1
                    && dragboard.getFiles().get(0).isDirectory()) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            } else {
                dragEvent.acceptTransferModes(TransferMode.NONE);
            }
        }
        dragEvent.consume();
    }

    /**
     * Gestisce l'evento di rilascio di un folder nell'area di visualizzazione
     * della file ({@code TreeView}).
     *
     * @param dragEvent Evento JavaFX di trascinamento.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    @FXML
    private void rilasciaFolder(DragEvent dragEvent) throws IOException {
        final Dragboard dragboard = dragEvent.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            success = true;
            File file = dragboard.getFiles().get(0);
            if (file.isDirectory()) {
                currentDirectory.set(file);
                mainStage.setTitle(currentDirectory.get().getAbsolutePath()
                        + " — " + APP_NAME);
                performIndexing(this::performPreProcessing);
            }
        }
        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Apri cartella...].
     *
     * @param actionEvent Evento JavaFX.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    @FXML
    private void apriCartella(ActionEvent actionEvent) throws IOException {
        if (currentDirectory.get() != null) {
            if (askYesNo("Apri cartella", "Chiudere la cartella corrente?")) {
                chiudiCartella(actionEvent);
                currentDirectory.set(chooseDirectory(mainStage));
            }
        } else {
            currentDirectory.set(chooseDirectory(mainStage));
        }
        if (currentDirectory.get() != null) {
            mainStage.setTitle(currentDirectory.get().getAbsolutePath() + " — " + APP_NAME);
        }
        performIndexing(this::performPreProcessing);
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Chiudi cartella].
     *
     * @param actionEvent Evento JavaFX.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    @FXML
    private void chiudiCartella(ActionEvent actionEvent) throws IOException {
        currentDirectory.set(null);
        currentStats.clear();
        if (!currentModels.isBound()) {
            currentModels.set(null);
        }
        txtQuery.clear();
        lstResults.getItems().clear();
        mainStage.setTitle(APP_NAME);
        performIndexing();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [(Esegui) Query].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void eseguiQuery(ActionEvent actionEvent) {
        currentStatus.set(Status.SEARCHING);
        QueryService queryService = new QueryService(txtQuery.getText(), currentModels.get());
        lstResults.itemsProperty().bind(queryService.valueProperty());
        currentProgress.bind(queryService.progressProperty());
        queryService.start();
        currentTask.set(queryService);
        queryService.setOnSucceeded(event -> {
            lstResults.itemsProperty().unbind();
            currentProgress.unbind();
            currentTask.set(null);
            currentProgress.set(0);
            currentStatus.set(Status.READY);
            if (lstResults.getItems().isEmpty()) {
                showInformation("La ricerca non ha prodotto risultati in nessun documento.");
            }
        });
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Annulla].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void annulla(ActionEvent actionEvent) {
        txtQuery.undo();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Ripeti].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void ripeti(ActionEvent actionEvent) {
        txtQuery.redo();
    }

    /**
     * Aggiorna lo stato attuale della {@code Clipboard}.
     */
    private void updateClipboardState() {
        clipboardHasString.set(clipboard.hasString());
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Taglia].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void taglia(ActionEvent actionEvent) {
        txtQuery.cut();
        updateClipboardState();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Copia].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void copia(ActionEvent actionEvent) {
        txtQuery.copy();
        updateClipboardState();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Incolla].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void incolla(ActionEvent actionEvent) {
        txtQuery.paste();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Elimina].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void elimina(ActionEvent actionEvent) {
        txtQuery.deleteText(txtQuery.getSelection());
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Seleziona tutto].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void selezionaTutto(ActionEvent actionEvent) {
        txtQuery.selectAll();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Deseleziona tutto].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void deselezionaTutto(ActionEvent actionEvent) {
        txtQuery.deselect();
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Preferenze].
     *
     * @param actionEvent Evento JavaFX.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    @FXML
    private void preferenze(ActionEvent actionEvent) throws IOException {
        showStage(APP_PREFS_VIEW, "Preferenze di " + APP_NAME, APP_PREFS_ICON,
                false, new G03MySimpleIRToolPreferencesController(this));
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Guida utente].
     *
     * @param actionEvent Evento JavaFX.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    @FXML
    private void guidaUtente(ActionEvent actionEvent) throws IOException {
        showStage(APP_HELP_VIEW, "Guida utente di " + APP_NAME, APP_HELP_ICON);
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Informazioni su].
     *
     * @param actionEvent Evento JavaFX.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    @FXML
    private void informazioniSu(ActionEvent actionEvent) throws IOException {
        showStage(APP_ABOUT_VIEW, "Informazioni su " + APP_NAME, APP_ABOUT_ICON);
    }

    /**
     * Gestisce l'evento di click sul {@code MenuItem} [Esci].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void esci(ActionEvent actionEvent) {
        final Preferences preferences = Preferences.userNodeForPackage(G03MySimpleIRTool.class);
        preferences.putDouble("x", mainStage.getX());
        preferences.putDouble("y", mainStage.getY());
        preferences.putDouble("width", mainStage.getWidth());
        preferences.putDouble("height", mainStage.getHeight());
        preferences.putBoolean("maximized", mainStage.isMaximized());
        if (currentDirectory.get() != null) {
            preferences.put("path", currentDirectory.get().getAbsolutePath());
            try {
                chiudiCartella(actionEvent);
            } catch (IOException ex) {
                showError("Errore durante la chiusura della cartella.");
            }
        } else {
            preferences.remove("path");
        }
        Platform.exit();
    }

}
