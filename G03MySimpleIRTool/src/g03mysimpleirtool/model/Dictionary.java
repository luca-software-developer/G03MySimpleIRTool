package g03mysimpleirtool.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * La classe {@code Dictionary} rappresenta un dizionario.
 */
public class Dictionary {

    /**
     * Definisce il {@code Set} di parole che costituiscono il dizionario.
     */
    private final Set<String> dictionary;

    /**
     * Costruttore.
     *
     * @param model Modello del documento da cui estrarre il dizionario.
     */
    public Dictionary(TFDocumentModel model) {
        this(new HashSet<>(Arrays.asList(model)));
    }

    /**
     * Costruttore.
     *
     * @param models Modelli dei documenti da cui estrarre il dizionario.
     */
    public Dictionary(Set<TFDocumentModel> models) {
        dictionary = models.stream()
                .flatMap(model -> Stream.of(model.getVector().keySet()))
                .flatMap(bag -> bag.stream())
                .collect(Collectors.toSet());
    }

    /**
     * Restituisce il {@code Set} di parole.
     *
     * @return Restituisce il {@code Set} di parole.
     */
    public Set<String> getBagOfWords() {
        return new HashSet<>(dictionary);
    }

    /**
     * Restituisce il dizionario rappresentato come stringa.
     *
     * @return Restituisce il dizionario rappresentato come stringa.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " { " + String.join("",
                Stream.of(getClass().getDeclaredFields())
                        .map(field -> {
                            try {
                                return field.getName() + ": " + field.get(this) + ' ';
                            } catch (IllegalArgumentException | IllegalAccessException ex) {
                                return "";
                            }
                        })
                        .collect(Collectors.toList())
        ) + '}';
    }

}
