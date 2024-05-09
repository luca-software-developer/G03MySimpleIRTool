package g03mysimpleirtool.controller;

import g03mysimpleirtool.G03MySimpleIRTool;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_CACHE_FILE;
import g03mysimpleirtool.model.Model;
import static g03mysimpleirtool.util.Dialogs.chooseFile;
import static g03mysimpleirtool.util.Dialogs.showError;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * La classe {@code G03MySimpleIRToolPreferencesController} rappresenta il
 * controller MVC della view di preferenze dell'applicazione. Fornisce i metodi
 * handler per la gestione degli eventi di interfaccia grafica.
 */
public class G03MySimpleIRToolPreferencesController implements Initializable {

    @FXML
    private CheckBox chkContributo;

    @FXML
    private VBox vbContributo;

    @FXML
    private Slider sldContributoTitolo;

    @FXML
    private Label lblContributoTitolo;

    @FXML
    private Slider sldContributoCorpo;

    @FXML
    private Label lblContributoCorpo;

    @FXML
    private CheckBox chkStopwords;

    @FXML
    private VBox vbStopwords;

    @FXML
    private TextField txtStopwords;

    @FXML
    private ChoiceBox<String> cbModello;

    /**
     * Definisce lo {@code Stage} del gestore delle preferenze.
     */
    private Stage stage;

    /**
     * Definisce l'oggetto {@code Preferences} per la gestione e la persistenza
     * delle preferenze dell'utente.
     */
    private Preferences preferences;

    /**
     * Indica se c'è stata una modifica alle preferenze che richiede un
     * ricalcolo del modello.
     */
    private BooleanProperty changed;

    /**
     * Definisce il controller della finestra principale.
     */
    private final G03MySimpleIRToolController mainController;

    /**
     * Costruttore.
     *
     * @param mainController Controller della finestra principale.
     */
    public G03MySimpleIRToolPreferencesController(G03MySimpleIRToolController mainController) {
        this.mainController = mainController;
    }

    /**
     * Restituisce il controller della finestra principale.
     *
     * @return Restituisce il controller della finestra principale.
     */
    public G03MySimpleIRToolController getMainController() {
        return mainController;
    }

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
        preferences = Preferences.userNodeForPackage(G03MySimpleIRTool.class);
        changed = new SimpleBooleanProperty(false);
        initBindings();
        initControls();
        Platform.runLater(() -> {
            stage = (Stage) vbContributo.getScene().getWindow();
            stage.setOnCloseRequest(this::onCloseRequest);
        });
    }

    /**
     * Handler per la richiesta di chiusura della finestra.
     *
     * @param event Evento JavaFX.
     */
    private void onCloseRequest(WindowEvent event) {
        if (changed.get()) {
            rerunPreProcessing();
        }
    }

    /**
     * Inizializza i controlli dell'interfaccia.
     */
    private void initControls() {
        chkContributo.setSelected(preferences.getBoolean("contributoDifferenziato", false));
        sldContributoTitolo.setValue(preferences.getDouble("contributoTitolo", 0.70));
        sldContributoCorpo.setValue(preferences.getDouble("contributoCorpo", 0.30));
        String stopwordsPath;
        chkStopwords.setSelected(preferences.getBoolean("filtraggioStopwords", false));
        if ((stopwordsPath = preferences.get("stopwordsPath", null)) != null) {
            txtStopwords.setText(new File(stopwordsPath).getName());
        }
        cbModello.getItems().setAll(Model.TF.toString(), Model.TFIDF.toString());
        cbModello.setValue(preferences.get("modello", Model.TF.toString()));
    }

    /**
     * Inizializza i binding.
     */
    private void initBindings() {
        chkContributo.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            vbContributo.setDisable(!newValue);
            changed.set(true);
            preferences.putBoolean("contributoDifferenziato", newValue);
        });
        lblContributoTitolo.textProperty().bind(sldContributoTitolo.valueProperty().asString("%.2f"));
        lblContributoCorpo.textProperty().bind(sldContributoCorpo.valueProperty().asString("%.2f"));
        sldContributoTitolo.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            sldContributoCorpo.valueProperty().setValue(1 - newValue.doubleValue());
            changed.set(true);
            preferences.putDouble("contributoTitolo", newValue.doubleValue());
        });
        sldContributoCorpo.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            sldContributoTitolo.valueProperty().setValue(1 - newValue.doubleValue());
            changed.set(true);
            preferences.putDouble("contributoCorpo", newValue.doubleValue());
        });
        chkStopwords.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            vbStopwords.setDisable(!newValue);
            changed.set(true);
            preferences.putBoolean("filtraggioStopwords", newValue);
        });
    }

    /**
     * Elimina il file di cache e riesegue la pre-elaborazione.
     */
    private void rerunPreProcessing() {
        try {
            Files.deleteIfExists(Paths.get(APP_CACHE_FILE));
        } catch (IOException ex) {
            showError("Errore durante l'eliminazione del file di cache.");
        }
        mainController.performPreProcessing();
    }

    /**
     * Gestisce l'evento di click sul {@code Button} [...].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void sfogliaFileStopwords(ActionEvent actionEvent) {
        File stopwordsFile = chooseFile(stage, new ExtensionFilter("File di testo (*.txt)", "*.txt"));
        if (stopwordsFile != null) {
            txtStopwords.setText(stopwordsFile.getName());
            preferences.put("stopwordsPath", stopwordsFile.getAbsolutePath());
        }
    }

    /**
     * Gestisce l'evento di scelta sul {@code ChoiceBox} relativo al modello.
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void scegliModello(ActionEvent actionEvent) {
        preferences.put("modello", cbModello.getValue());
    }

}
