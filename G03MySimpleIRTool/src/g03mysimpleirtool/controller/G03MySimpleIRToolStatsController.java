package g03mysimpleirtool.controller;

import g03mysimpleirtool.model.Dictionary;
import g03mysimpleirtool.model.TFDocumentModel;
import java.net.URL;
import java.util.DoubleSummaryStatistics;
import java.util.Locale;
import java.util.Map;
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
        final DoubleSummaryStatistics statistics = model.getVector(false).entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.summarizingDouble(Map.Entry::getValue));
        final String mostFrequentWord = model.getVector(false).entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get().getKey();
        final String leastFrequentWord = model.getVector(false).entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .min((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get().getKey();
        final long maxFrequency = (long) statistics.getMax();
        final long minFrequency = (long) statistics.getMin();
        final double avgFrequency = statistics.getAverage();
        final long distinctWords = model.getVector(false).entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .count();
        final long totalWords = model.getVector(false).entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.summingDouble(entry -> entry.getValue())).longValue();
        stats.clear();
        stats.add(new Pair<>("Parola più frequente",
                mostFrequentWord.substring(0, 1).toUpperCase()
                + mostFrequentWord.substring(1)));
        stats.add(new Pair<>("Parola meno frequente",
                leastFrequentWord.substring(0, 1).toUpperCase()
                + leastFrequentWord.substring(1)));
        stats.add(new Pair<>("Frequenza massima",
                Long.toString(maxFrequency)));
        stats.add(new Pair<>("Frequenza media",
                String.format(Locale.US, "%.2f", avgFrequency)));
        stats.add(new Pair<>("Frequenza minima",
                Long.toString(minFrequency)));
        stats.add(new Pair<>("Numero di parole distinte",
                Long.toString(distinctWords)));
        stats.add(new Pair<>("Numero di parole totali",
                Long.toString(totalWords)));
        stats.add(new Pair<>("Dimensione del dizionario",
                Integer.toString(new Dictionary(model).getBagOfWords().size())));
    }

}
