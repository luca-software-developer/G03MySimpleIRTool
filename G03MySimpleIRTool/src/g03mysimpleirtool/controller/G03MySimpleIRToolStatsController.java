package g03mysimpleirtool.controller;

import g03mysimpleirtool.model.TFDocumentModel;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Pair;

/**
 * La classe {@code G03MySimpleIRToolStatsController} rappresenta il controller
 * MVC del visualizzatore di statistiche dell'applicazione. Fornisce i metodi
 * handler per la gestione degli eventi di interfaccia grafica.
 */
public class G03MySimpleIRToolStatsController implements Initializable {

    @FXML
    private TableView<Pair<String, String>> tblStatistiche;

    @FXML
    private TableColumn<Pair<String, String>, String> tcProprieta;

    @FXML
    private TableColumn<Pair<String, String>, String> tcValore;

    /**
     * Definisce il modello di documento su cui calcolare le statistiche.
     */
    private final TFDocumentModel model;

    /**
     * Definisce la lista osservabile di statistiche sul documento.
     */
    private ObservableList<Pair<String, String>> stats;

    /**
     * Costruttore.
     *
     * @param model Modello di documento su cui calcolare le statistiche.
     */
    public G03MySimpleIRToolStatsController(TFDocumentModel model) {
        this.model = model;
    }

    /**
     * Restituisce il modello di documento.
     *
     * @return Restituisce il modello di documento.
     */
    public TFDocumentModel getModel() {
        return model;
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
        stats = FXCollections.observableArrayList();
        tcProprieta.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getKey()));
        tcValore.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getValue()));
        tblStatistiche.setItems(stats);
        computeStats();
    }

    /**
     * Effettua il calcolo delle statistiche sul documento.
     */
    private void computeStats() {
        final String mostFrequentWord = model.getVector().entrySet().stream()
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get().getKey();
        stats.add(new Pair<>("Parola più frequente",
                mostFrequentWord.substring(0, 1).toUpperCase()
                + mostFrequentWord.substring(1)));
        final long distinctWords = model.getVector().entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .count();
        stats.add(new Pair<>("Numero di parole distinte",
                Long.toString(distinctWords)));
        final long totalWords = model.getVector().entrySet().stream()
                .collect(Collectors.summingDouble(entry -> entry.getValue())).longValue();
        stats.add(new Pair<>("Numero di parole totali",
                Long.toString(totalWords)));
    }

}
