package g03mysimpleirtool.service;

import g03mysimpleirtool.G03MySimpleIRTool;
import g03mysimpleirtool.model.Dictionary;
import g03mysimpleirtool.model.Model;
import g03mysimpleirtool.model.TFDocumentModel;
import g03mysimpleirtool.model.TFIDFDocumentModel;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

/**
 * La classe {@code QueryService} rappresenta il servizio di ricerca dei
 * documenti.
 */
public class QueryService extends Service<ObservableList<TFDocumentModel>> {

    /**
     * Definisce la query.
     */
    private final String query;

    /**
     * Definisce l'insieme di modelli dei documenti della collezione.
     */
    private final Set<TFDocumentModel> models;

    /**
     * Costruttore.
     *
     * @param query Query fornita dall'utente.
     * @param models Modelli dei documenti della collezione.
     */
    public QueryService(String query, Set<TFDocumentModel> models) {
        this.query = query;
        this.models = models;
    }

    /**
     * Restituisce la query fornita dall'utente.
     *
     * @return Restituisce la query fornita dall'utente.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Restituisce i modelli dei documenti della collezione.
     *
     * @return Restituisce i modelli dei documenti della collezione.
     */
    public Set<TFDocumentModel> getModels() {
        return new HashSet<>(models);
    }

    /**
     * Invocato dopo l'avvio del servizio sul thread dell'applicazione JavaFX.
     *
     * @return Restituisce il {@code Task}.
     */
    @Override
    protected Task<ObservableList<TFDocumentModel>> createTask() {
        return new Task<ObservableList<TFDocumentModel>>() {
            /**
             * Invocato quando il task viene eseguito.
             *
             * @return Restituisce il risultato del task, se presente.
             * @throws Exception Sollevata in caso di eccezione non gestita
             * durante l'operazione in background.
             */
            @Override
            protected ObservableList<TFDocumentModel> call() throws Exception {
                int countModels = 0;
                final int totalModels = models.size();

                //  Ottiene il dizionario, il modello della query
                //  e le preferenze dell'utente.
                final Dictionary dictionary = new Dictionary(models);
                final ObservableList<TFDocumentModel> result = FXCollections.observableArrayList();
                final Preferences preferences = Preferences.userNodeForPackage(G03MySimpleIRTool.class);
                TFDocumentModel queryModel = TFDocumentModel.fromQuery(query, dictionary);
                queryModel.extendVector(dictionary);

                updateProgress(countModels, totalModels);

                //  Se è selezionato il modello alternativo tf-idf...
                if (preferences.get("modello", null).equals(Model.TFIDF.toString())) {
                    //  Converte il modello tf della query nel corrispondente
                    //  tf-idf per la collezione corrente.
                    TFIDFDocumentModel tfidfModel = TFIDFDocumentModel
                            .fromCollection(queryModel, models);

                    //  Calcola la similarità con i modelli tf-idf calcolati
                    //  sulla collezione.
                    models.stream()
                            .map(model
                                    -> new Pair<>(model, tfidfModel.computeSimilarity(
                                    TFIDFDocumentModel.fromCollection(model, models), false)))
                            .filter(similarity -> similarity.getValue() > 0)
                            .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                            .forEach(similarity -> {
                                updateProgress(countModels, totalModels);
                                result.add(similarity.getKey());
                            });
                } else {
                    //  Calcola la similarità con i modelli tf della collezione.
                    models.stream()
                            .map(model -> new Pair<>(model, queryModel.computeSimilarity(model, false)))
                            .filter(similarity -> similarity.getValue() > 0)
                            .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                            .forEach(similarity -> {
                                updateProgress(countModels, totalModels);
                                result.add(similarity.getKey());
                            });
                }

                return result;
            }
        };
    }

}
