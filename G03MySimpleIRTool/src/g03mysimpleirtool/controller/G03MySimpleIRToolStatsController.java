package g03mysimpleirtool.controller;

import g03mysimpleirtool.model.Dictionary;
import g03mysimpleirtool.model.TFDocumentModel;
import static g03mysimpleirtool.util.Statistics.calculateSummaryStatistics;
import static g03mysimpleirtool.util.Statistics.calculateWordFrequencies;
import static g03mysimpleirtool.util.Statistics.findLeastFrequentWord;
import static g03mysimpleirtool.util.Statistics.findMostFrequentWord;
import java.net.URL;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.UnaryOperator;
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
    private ObservableList<Pair<String, String>> statistics;

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
        statistics = FXCollections.observableArrayList();
        tcProprieta.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getKey()));
        tcValore.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getValue()));
        tblStatistiche.setItems(statistics);
        computeStatistics();
    }

    /**
     * Effettua il calcolo delle statistiche sul documento.
     */
    private void computeStatistics() {
        final Set<TFDocumentModel> current = Collections.singleton(model);
        final Map<String, Long> wordFrequencies = calculateWordFrequencies(current);
        final DoubleSummaryStatistics stats = calculateSummaryStatistics(wordFrequencies);
        final UnaryOperator<String> capitalize = s -> s.isEmpty()
                ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
        statistics.clear();
        addToStatistics("Parola più frequente", capitalize.apply(findMostFrequentWord(wordFrequencies)));
        addToStatistics("Parola meno frequente", capitalize.apply(findLeastFrequentWord(wordFrequencies)));
        addToStatistics("Frequenza massima", Long.toString(stats.getMax() == Double.NEGATIVE_INFINITY ? 0 : (long) stats.getMax()));
        addToStatistics("Frequenza media", String.format(Locale.US, "%.2f", stats.getAverage()));
        addToStatistics("Frequenza minima", Long.toString(stats.getMin() == Double.POSITIVE_INFINITY ? 0 : (long) stats.getMin()));
        addToStatistics("Dimensione del dizionario", Integer.toString(new Dictionary(current).getBagOfWords().size()));
    }

    /**
     * Aggiunge la coppia proprietà-valore alle statistiche per il documento
     * corrente.
     *
     * @param property Nome della proprietà.
     * @param value Valore della proprietà.
     */
    private void addToStatistics(String property, String value) {
        statistics.add(new Pair<>(property, value));
    }

}
