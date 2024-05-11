package g03mysimpleirtool.model;

import g03mysimpleirtool.util.TextProcessing;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * La classe {@code TFDocumentModel} rappresenta il modello TF (Term Frequency)
 * del documento.
 */
public class TFDocumentModel extends VectorDocumentModel {

    private static final long serialVersionUID = 1L;

    /**
     * Costruttore.
     *
     * @param path Path del documento.
     * @param vector Vettore del documento.
     */
    public TFDocumentModel(String path, Map<String, Long> vector) {
        super(path, convertDoubleVectorToLong(vector));
    }

    /**
     * Costruttore.
     *
     * @param path Path del documento.
     * @param titleVector Vettore del titolo del documento.
     * @param contentVector Vettore del corpo del documento.
     */
    public TFDocumentModel(String path, Map<String, Long> titleVector, Map<String, Long> contentVector) {
        super(path, convertDoubleVectorToLong(titleVector),
                convertDoubleVectorToLong(contentVector));
    }

    /**
     * Restituisce il modello con vettore esteso.
     *
     * @param dictionary Dizionario da utilizzare per l'estensione del vettore.
     * @return Restituisce il modello con vettore esteso.
     */
    @Override
    public TFDocumentModel toExtended(Dictionary dictionary) {
        TFDocumentModel documentModel = new TFDocumentModel(getPath().toString(),
                convertLongVectorToDouble(getTitleVector()),
                convertLongVectorToDouble(getContentVector()));
        documentModel.extendVector(dictionary);
        return documentModel;
    }

    /**
     * Restituisce il modello del documento specificato.
     *
     * @param path Path del documento.
     * @return Restituisce il modello del documento specificato.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    public static TFDocumentModel fromPath(String path) throws IOException {
        return new TFDocumentModel(path, computeTitleVector(path), computeContentVector(path));
    }

    /**
     * Restituisce il modello della query specificata.
     *
     * @param query Stringa contenente la query.
     * @param dictionary Dizionario delle parole.
     * @return Restituisce il modello del documento specificato.
     */
    public static TFDocumentModel fromQuery(String query, Dictionary dictionary) {
        final Map<String, Long> vector = Stream.of(
                TextProcessing.removeStopwords(
                        TextProcessing.sanitizeText(query))
                        .toLowerCase().trim().split("\\s+"))
                .filter(word -> dictionary.getBagOfWords().contains(word))
                .collect(Collectors.groupingBy(String::toString, Collectors.counting()));
        return new TFDocumentModel(null, vector);
    }

    /**
     * Restituisce il vettore del titolo del documento specificato.
     *
     * @param path Path del documento.
     * @return Restituisce il vettore del titolo del documento specificato.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    private static Map<String, Long> computeTitleVector(String path) throws IOException {
        Optional<String> firstLine = TextProcessing.getLineStream(path).findFirst();
        if (firstLine.isPresent() && !firstLine.get().trim().isEmpty()) {
            return Stream.of(TextProcessing.removeStopwords(TextProcessing.sanitizeText(
                    firstLine.get().toLowerCase().trim())).split("\\s+"))
                    .collect(Collectors.groupingBy(String::toString, Collectors.counting()));
        }
        return new HashMap<>();
    }

    /**
     * Restituisce il vettore del corpo del documento specificato.
     *
     * @param path Path del documento.
     * @return Restituisce il vettore del corpo del documento specificato.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    private static Map<String, Long> computeContentVector(String path) throws IOException {
        return TextProcessing.getLineStream(path)
                .skip(1)
                .map(line -> TextProcessing.sanitizeText(line))
                .map(line -> TextProcessing.removeStopwords(line))
                .flatMap(line -> Stream.of(line.toLowerCase().trim().split("\\s+")))
                .collect(Collectors.groupingBy(String::toString, Collectors.counting()));
    }

}
