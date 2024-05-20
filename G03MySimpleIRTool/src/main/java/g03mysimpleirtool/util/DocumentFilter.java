package g03mysimpleirtool.util;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * La classe {@code DocumentFilter} rappresenta un filtro che seleziona solo i
 * tipi di file riconosciuti come documenti.
 */
public class DocumentFilter implements Predicate<Path> {

    /**
     * Effettua la valutazione del predicato dato il {@code Path} del file.
     *
     * @param path {@code Path} del file.
     * @return {@code true} se la condizione è soddisfatta, {@code false}
     * altrimenti.
     */
    @Override
    public boolean test(Path path) {
        return new TextFileFilter().test(path)
                || new RichTextFileFilter().test(path);
    }

}
