package g03mysimpleirtool.model;

import g03mysimpleirtool.G03MySimpleIRTool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.prefs.Preferences;
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
        final Map<String, Long> vector = Stream.of(removeStopwords(sanitizeText(query))
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
        Optional<String> firstLine = getLineStream(path).findFirst();
        if (firstLine.isPresent() && !firstLine.get().trim().isEmpty()) {
            return Stream.of(removeStopwords(sanitizeText(firstLine.get().toLowerCase().trim())).split("\\s+"))
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
        return getLineStream(path)
                .skip(1)
                .map(line -> sanitizeText(line))
                .map(line -> removeStopwords(line))
                .flatMap(line -> Stream.of(line.toLowerCase().trim().split("\\s+")))
                .collect(Collectors.groupingBy(String::toString, Collectors.counting()));
    }

    /**
     * Effettua la rimozione delle stopwords, se abilitata e configurata.
     *
     * @param text Testo fornito in input.
     * @return Restituisce il risultato della rimozione oppure {@code null} in
     * caso di errore.
     */
    private static String removeStopwords(String text) {
        Preferences preferences = Preferences.userNodeForPackage(G03MySimpleIRTool.class);
        if (preferences.getBoolean("filtraggioStopwords", false)) {
            String stopwordsPath;
            if ((stopwordsPath = preferences.get("stopwordsPath", null)) != null) {
                try {
                    Set<String> stopwords = getLineStream(stopwordsPath)
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .filter(line -> !line.isEmpty())
                            .collect(Collectors.toSet());
                    return String.join(" ", Stream.of(text
                            .toLowerCase().trim().split("\\s+"))
                            .filter(word -> !stopwords.contains(word))
                            .collect(Collectors.toList()));
                } catch (IOException ex) {
                    return null;
                }
            }
        }
        return text;
    }

    /**
     * Effettua la rimozione della punteggiatura dal testo.
     *
     * @param text Testo fornito in input.
     * @return Restituisce il risultato della rimozione.
     */
    private static String sanitizeText(String text) {
        final String symbols = ".,;_\\-\\(\\)\"'?!\\/";
        return String.join(" ", Stream.of(text.split("\\s+"))
                .map(token -> token
                .replaceAll("^[" + symbols + "]+", "")
                .replaceAll("[" + symbols + "]+$", ""))
                .filter(token -> !Arrays.asList(symbols.split("")).contains(token))
                .collect(Collectors.toList())
        );
    }

    /**
     * Restituisce il contenuto del documento specificato come uno
     * {@code Stream} di linee, saltando le linee vuote.
     *
     * @param path Path del documento.
     * @return Restituisce il contenuto del documento specificato come uno
     * {@code Stream} di linee, saltando le linee vuote.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    private static Stream<String> getLineStream(String path) throws IOException {
        return Files.readAllLines(Paths.get(path)).stream()
                .filter(line -> !line.trim().isEmpty());
    }

    /**
     * Restituisce il modello rappresentato come stringa.
     *
     * @return Restituisce il modello rappresentato come stringa.
     */
    @Override
    public String toString() {
        return "TFDocumentModel{" + "path=" + getPath() + ", hash=" + getHash() + ", titleVector=" + getTitleVector() + ", contentVector=" + getContentVector() + '}';
    }

}
