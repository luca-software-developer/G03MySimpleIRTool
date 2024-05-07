package g03mysimpleirtool.model;

import g03mysimpleirtool.G03MySimpleIRTool;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 * La classe {@code TFDocumentModel} rappresenta il modello vettoriale del
 * documento.
 */
public class VectorDocumentModel extends DocumentModel {

    private static final long serialVersionUID = 1L;

    /**
     * Definisce il vettore del titolo del documento.
     */
    private final Map<String, Double> titleVector;

    /**
     * Definisce il vettore del corpo del documento.
     */
    private final Map<String, Double> contentVector;

    /**
     * Costruttore.
     *
     * @param path Path del documento.
     * @param vector Vettore del documento.
     */
    public VectorDocumentModel(String path, Map<String, Double> vector) {
        super(path);
        this.titleVector = new HashMap<>();
        this.contentVector = vector;
    }

    /**
     * Costruttore.
     *
     * @param path Path del documento.
     * @param titleVector Vettore del titolo del documento.
     * @param contentVector Vettore del corpo del documento.
     */
    public VectorDocumentModel(String path, Map<String, Double> titleVector, Map<String, Double> contentVector) {
        super(path);
        this.titleVector = titleVector;
        this.contentVector = contentVector;
    }

    /**
     * Restituisce il modello con vettore esteso.
     *
     * @param dictionary Dizionario da utilizzare per l'estensione del vettore.
     * @return Restituisce il modello con vettore esteso.
     */
    public VectorDocumentModel toExtended(Dictionary dictionary) {
        VectorDocumentModel documentModel = new VectorDocumentModel(
                getPath().toString(), getTitleVector(), getContentVector());
        documentModel.extendVector(dictionary);
        return documentModel;
    }

    /**
     * Estende il vettore utilizzando il dizionario specificato.
     *
     * @param dictionary Dizionario da utilizzare.
     */
    public void extendVector(Dictionary dictionary) {
        final Set<String> bagOfWords = dictionary.getBagOfWords();
        bagOfWords.forEach(word -> getTitleVector().putIfAbsent(word, 0D));
        bagOfWords.forEach(word -> getContentVector().putIfAbsent(word, 0D));
    }

    /**
     * Restituisce il vettore del documento.
     *
     * @return Restituisce il vettore del documento.
     */
    public Map<String, Double> getVector() {
        return getVector(true);
    }

    /**
     * Restituisce il vettore del documento.
     *
     * @param weighted Indica se il contributo differenziato deve essere
     * applicato.
     * @return Restituisce il vettore del documento.
     */
    public Map<String, Double> getVector(boolean weighted) {
        Map<String, Double> vector = new HashMap<>();
        Map<String, Double> title = getTitleVector();
        Map<String, Double> content = getContentVector();

        //  Se l'utente ha configurato il contributo differenziato titolo/corpo...
        final Preferences preferences = Preferences.userNodeForPackage(G03MySimpleIRTool.class);
        if (weighted && preferences.getBoolean("contributoDifferenziato", false)) {

            //  Ottiene i pesi dalle preferenze dell'utente...
            final double titleWeight = preferences.getDouble("contributoTitolo", 0.50);
            final double contentWeight = preferences.getDouble("contributoCorpo", 0.50);

            //  ... e moltiplica i vettori relativi al titolo e al corpo per i
            //  relativi pesi.
            title = title.entrySet().stream()
                    .map(entry -> new Pair<>(entry.getKey(), entry.getValue() * titleWeight))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
            content = content.entrySet().stream()
                    .map(entry -> new Pair<>(entry.getKey(), entry.getValue() * contentWeight))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        }

        //  Quindi unisce i vettori, sommando i valori quando le chiavi coincidono.
        vector.putAll(title);
        content.forEach((key, value) -> {
            if (vector.containsKey(key)) {
                vector.put(key, vector.get(key) + value);
            } else {
                vector.put(key, value);
            }
        });

        return vector;
    }

    /**
     * Restituisce il vettore del titolo del documento.
     *
     * @return Restituisce il vettore del titolo del documento.
     */
    public Map<String, Double> getTitleVector() {
        return titleVector;
    }

    /**
     * Restituisce il vettore del corpo del documento.
     *
     * @return Restituisce il vettore del corpo del documento.
     */
    public Map<String, Double> getContentVector() {
        return contentVector;
    }

    /**
     * Restituisce la similarità di coseno.
     *
     * @param model Modello del documento da confrontare.
     * @return Restituisce la similarità di coseno.
     */
    public double computeSimilarity(VectorDocumentModel model) {
        return computeSimilarity(model, true, true);
    }

    /**
     * Restituisce la similarità di coseno.
     *
     * @param model Modello del documento da confrontare.
     * @param thisWeighted Indica se il contributo differenziato deve essere
     * applicato per il vettore corrente.
     * @return Restituisce la similarità di coseno.
     */
    public double computeSimilarity(VectorDocumentModel model, boolean thisWeighted) {
        return computeSimilarity(model, thisWeighted, true);
    }

    /**
     * Restituisce la similarità di coseno.
     *
     * @param model Modello del documento da confrontare.
     * @param thisWeighted Indica se il contributo differenziato deve essere
     * applicato per il vettore corrente.
     * @param otherWeighted Indica se il contributo differenziato deve essere
     * applicato per l'altro vettore.
     * @return Restituisce la similarità di coseno.
     */
    public double computeSimilarity(VectorDocumentModel model, boolean thisWeighted, boolean otherWeighted) {
        Map<String, Double> vector1 = getVector(thisWeighted);
        Map<String, Double> vector2 = model.getVector(otherWeighted);
        if (!vector1.keySet().equals(vector2.keySet())) {
            throw new IllegalArgumentException("I set di chiavi dei due vettori devono coincidere.");
        }
        Map<String, Double> merged = new HashMap<>(vector1);
        vector2.forEach((key, value) -> merged.merge(key, value, (v1, v2) -> v1 * v2));
        List<Double> values = merged.values().stream().collect(Collectors.toList());
        BigDecimal dot = new BigDecimal(values.stream().reduce(0D, (current, value) -> current + value));
        double modulo1 = Math.sqrt(vector1.values().stream().reduce(0D, (current, value) -> current + value * value));
        double modulo2 = Math.sqrt(vector2.values().stream().reduce(0D, (current, value) -> current + value * value));
        if (modulo1 * modulo2 == 0) {
            return 0;
        }
        dot = dot.divide(new BigDecimal(modulo1 * modulo2), MathContext.DECIMAL32.getPrecision(), RoundingMode.HALF_EVEN);
        return dot.doubleValue();
    }

    /**
     * Metodo di utilità che converte un vettore {@code Long}-valued in un
     * vettore {@code Double}-valued.
     *
     * @param longVector Vettore {@code Long}-valued.
     * @return Corrispondente vettore {@code Double}-valued.
     */
    public static Map<String, Double> convertDoubleVectorToLong(Map<String, Long> longVector) {
        return longVector.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().doubleValue()));
    }

    /**
     * Metodo di utilità che converte un vettore {@code Double}-valued in un
     * vettore {@code Long}-valued.
     *
     * @param doubleVector Vettore {@code Double}-valued.
     * @return Corrispondente vettore {@code Long}-valued.
     */
    public static Map<String, Long> convertLongVectorToDouble(Map<String, Double> doubleVector) {
        return doubleVector.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().longValue()));
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
