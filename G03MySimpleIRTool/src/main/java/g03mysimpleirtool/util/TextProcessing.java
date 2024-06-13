package g03mysimpleirtool.util;

import g03mysimpleirtool.G03MySimpleIRTool;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * La classe {@code TextProcessing} fornisce metodi di utilità per
 * l'elaborazione del testo.
 */
public class TextProcessing {

    /**
     * Costruttore.
     */
    public TextProcessing() {
    }

    /**
     * Effettua la rimozione delle stopwords, se abilitata e configurata.
     *
     * @param text Testo fornito in input.
     * @return Restituisce il risultato della rimozione oppure {@code null} in
     * caso di errore.
     */
    public static String removeStopwords(String text) {
        final Preferences preferences = Preferences.userNodeForPackage(G03MySimpleIRTool.class);
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
    public static String sanitizeText(String text) {
        final String allowed = "a-zA-Z0-9";
        return String.join(" ", Stream.of(text.split(",|\\s+"))
                .map(token -> token
                .replaceAll("^[^" + allowed + "]+", "")
                .replaceAll("[^" + allowed + "]+$", ""))
                .filter(token -> !Arrays.asList(allowed.split("")).contains(token))
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
    public static Stream<String> getLineStream(String path) throws IOException {
        if (path.endsWith(".rtf")) {
            final RTFEditorKit rtfEditorKit = new RTFEditorKit();
            final Document document = rtfEditorKit.createDefaultDocument();
            try {
                rtfEditorKit.read(new ByteArrayInputStream(
                        Files.readAllBytes(Paths.get(path))), document, 0);
                return Stream.of(document.getText(0, document.getLength())
                        .split("\n"))
                        .filter(line -> !line.trim().isEmpty());
            } catch (BadLocationException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else if (path.endsWith(".txt")) {
            return Files.readAllLines(Paths.get(path)).stream()
                    .filter(line -> !line.trim().isEmpty());
        } else {
            throw new IllegalArgumentException("Tipo di documento non supportato.");
        }
    }

}
