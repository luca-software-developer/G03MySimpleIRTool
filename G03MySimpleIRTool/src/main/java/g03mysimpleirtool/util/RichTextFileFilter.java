package g03mysimpleirtool.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * La classe {@code RichTextFileFilter} rappresenta un filtro che seleziona solo
 * i file di testo a formattazione complessa con estensione .rtf.
 */
public class RichTextFileFilter implements Predicate<Path> {

    /**
     * Effettua la valutazione del predicato dato il {@code Path} del file.
     *
     * @param path {@code Path} del file.
     * @return {@code true} se la condizione è soddisfatta, {@code false}
     * altrimenti.
     */
    @Override
    public boolean test(Path path) {
        return Files.isRegularFile(path)
                && path.getFileName().toString().endsWith(".rtf");
    }

}
