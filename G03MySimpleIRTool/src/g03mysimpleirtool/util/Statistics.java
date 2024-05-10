package g03mysimpleirtool.util;

import g03mysimpleirtool.model.TFDocumentModel;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * La classe {@code Statistics} fornisce metodi di utilità per il calcolo delle
 * statistiche.
 */
public class Statistics {

    /**
     * Costruttore.
     */
    private Statistics() {
    }

    /**
     * Calcola le frequenze di ciascuna parola all'interno di uno o più modelli.
     *
     * @param models Modelli forniti (varargs di TFDocumentModel).
     * @return Restituisce le frequenze di ciascuna parola all'interno di uno o
     * più modelli.
     */
    public static Map<String, Long> calculateWordFrequencies(TFDocumentModel... models) {
        final Map<String, Long> wordFrequencies = new HashMap<>();
        Stream.of(models)
                .distinct()
                .flatMap(model -> model.getVector(false).entrySet().stream())
                .forEach(entry -> {
                    wordFrequencies.put(entry.getKey(),
                            wordFrequencies.getOrDefault(entry.getKey(), 0L)
                            + entry.getValue().longValue());
                });
        return wordFrequencies;
    }

    /**
     * Calcola le frequenze di ciascuna parola all'interno di tutti i documenti
     * della collezione.
     *
     * @param current Set di modelli corrente.
     * @return Restituisce le frequenze di ciascuna parola all'interno di tutti
     * i documenti della collezione.
     */
    public static Map<String, Long> calculateWordFrequencies(Set<TFDocumentModel> current) {
        final Map<String, Long> wordFrequencies = new HashMap<>();
        current.stream()
                .flatMap(model -> model.getVector(false).entrySet().stream())
                .forEach(entry -> {
                    wordFrequencies.put(entry.getKey(),
                            wordFrequencies.getOrDefault(entry.getKey(), 0L)
                            + entry.getValue().longValue());
                });
        return wordFrequencies;
    }

    /**
     * Restituisce l'oggetto {@code DoubleSummaryStatistics} per il vettore TF
     * (Text Frequency) fornito.
     *
     * @param wordFrequencies Vettore TF (Text Frequency).
     * @return Restituisce l'oggetto {@code DoubleSummaryStatistics} per il
     * vettore TF (Text Frequency) fornito.
     */
    public static DoubleSummaryStatistics calculateSummaryStatistics(Map<String, Long> wordFrequencies) {
        return wordFrequencies.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.summarizingDouble(Map.Entry::getValue));
    }

    /**
     * Restituisce la parola più frequente nel vettore TF (Text Frequency)
     * fornito. Se la parola non viene trovata restituisce una stringa vuota.
     *
     * @param wordFrequencies Vettore TF (Text Frequency).
     * @return Restituisce la parola più frequente nel vettore TF (Text
     * Frequency) fornito.
     */
    public static String findMostFrequentWord(Map<String, Long> wordFrequencies) {
        Optional<Entry<String, Long>> mostFrequent = wordFrequencies.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()));
        if (mostFrequent.isPresent()) {
            return mostFrequent.get().getKey();
        }
        return "";
    }

    /**
     * Restituisce la parola meno frequente nel vettore TF (Text Frequency)
     * fornito. Se la parola non viene trovata restituisce una stringa vuota.
     *
     * @param wordFrequencies Vettore TF (Text Frequency).
     * @return Restituisce la parola meno frequente nel vettore TF (Text
     * Frequency) fornito.
     */
    public static String findLeastFrequentWord(Map<String, Long> wordFrequencies) {
        Optional<Entry<String, Long>> leastFrequent = wordFrequencies.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .min((e1, e2) -> e1.getValue().compareTo(e2.getValue()));
        if (leastFrequent.isPresent()) {
            return leastFrequent.get().getKey();
        }
        return "";
    }

}
