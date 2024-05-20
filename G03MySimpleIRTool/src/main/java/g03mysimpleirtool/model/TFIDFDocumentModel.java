package g03mysimpleirtool.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 * La classe {@code TFIDFDocumentModel} rappresenta il modello TF-IDF (Term
 * Frequency - Inverse Document Frequency) del documento.
 */
public class TFIDFDocumentModel extends VectorDocumentModel {

    private static final long serialVersionUID = 1L;

    /**
     * Costruttore.
     *
     * @param path Path del documento.
     * @param vector Vettore del documento.
     */
    public TFIDFDocumentModel(String path, Map<String, Double> vector) {
        super(path, vector);
    }

    /**
     * Costruttore.
     *
     * @param path Path del documento.
     * @param titleVector Vettore del titolo del documento.
     * @param contentVector Vettore del corpo del documento.
     */
    public TFIDFDocumentModel(String path, Map<String, Double> titleVector, Map<String, Double> contentVector) {
        super(path, titleVector, contentVector);
    }

    /**
     * Restituisce il modello con vettore esteso.
     *
     * @param dictionary Dizionario da utilizzare per l'estensione del vettore.
     * @return Restituisce il modello con vettore esteso.
     */
    @Override
    public TFIDFDocumentModel toExtended(Dictionary dictionary) {
        TFIDFDocumentModel documentModel = new TFIDFDocumentModel(getPath().toString(),
                getTitleVector(), getContentVector());
        documentModel.extendVector(dictionary);
        return documentModel;
    }

    /**
     * Restituisce il modello tf-idf dato il modello tf e il set di modelli tf
     * della collezione di documenti.
     *
     * @param tfModel Modello tf del documento.
     * @param collection Set di modelli tf della collezione di documenti.
     * @return Restituisce il modello tf-idf.
     */
    public static TFIDFDocumentModel fromCollection(TFDocumentModel tfModel, Set<TFDocumentModel> collection) {

        //  Ottiene il numero di documenti e le frequenze di ogni parola nel
        //  vettore corrente all'interno della collezione.
        final int N = collection.size();
        final Map<String, Long> df = new HashMap<>();
        tfModel.getVector().keySet().forEach(term -> {
            final AtomicLong documentsContainingTerm = new AtomicLong();
            collection.forEach(model -> {
                if (model.getTitleVector().keySet().contains(term)
                        && model.getTitleVector().get(term) > 0
                        || model.getContentVector().keySet().contains(term)
                        && model.getContentVector().get(term) > 0) {
                    documentsContainingTerm.incrementAndGet();
                }
            });
            df.put(term, documentsContainingTerm.longValue());
        });

        //  Calcola w_{i,d} = tf_{i,d} log(N / df_i) per ciascun termine nel
        //  vettore del titolo e del corpo del documento.
        final Map<String, Double> titleVector = tfModel.getTitleVector().entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(),
                entry.getValue() * Math.log((double) N / df.get(entry.getKey()))))
                .collect(Collectors.toMap(Pair::getKey, pair -> pair.getValue()));
        final Map<String, Double> contentVector = tfModel.getContentVector().entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(),
                entry.getValue() * Math.log((double) N / df.get(entry.getKey()))))
                .collect(Collectors.toMap(Pair::getKey, pair -> pair.getValue()));

        return new TFIDFDocumentModel(tfModel.getPath() == null
                ? null : tfModel.getPath().toString(), titleVector, contentVector);
    }

}
