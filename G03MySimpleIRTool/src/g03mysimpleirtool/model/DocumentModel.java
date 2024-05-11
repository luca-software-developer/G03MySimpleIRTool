package g03mysimpleirtool.model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * La classe {@code TFDocumentModel} rappresenta il modello del documento.
 */
public class DocumentModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Definisce il path del documento.
     */
    private final String path;

    /**
     * Definisce il risultato della funzione di hash SHA-256 calcolata sul file.
     */
    private final String hash;

    /**
     * Costruttore.
     *
     * @param path Path del documento.
     */
    public DocumentModel(String path) {
        this.path = path;
        this.hash = computeDocumentHash(path);
    }

    /**
     * Verifica se il modello è aggiornato confrontando gli hash.
     *
     * @return Restituisce {@code true} se il modello è aggiornato,
     * {@code false} altrimenti.
     */
    public boolean isUpToDate() {
        return computeDocumentHash(path).equalsIgnoreCase(hash);
    }

    /**
     * Restituisce l'hash SHA-256 del documento specificato.
     *
     * @param path Path del documento a cui applicare la funzione di hash.
     * @return Restituisce l'hash SHA-256 del documento specificato oppure
     * {@code null} in caso di errore.
     */
    private String computeDocumentHash(String path) {
        if (path == null) {
            return null;
        }
        StringBuilder digest = new StringBuilder(64);
        try {
            for (byte item : MessageDigest.getInstance("SHA-256")
                    .digest(Files.readAllBytes(Paths.get(path)))) {
                digest.append(String.format("%02x", item));
            }
            return digest.toString();
        } catch (NoSuchAlgorithmException | IOException ex) {
            return null;
        }
    }

    /**
     * Restituisce il {@code Path} del documento.
     *
     * @return Restituisce il {@code Path} del documento.
     */
    public Path getPath() {
        return path == null ? null : Paths.get(path);
    }

    /**
     * Restituisce l'hash SHA-256 del documento.
     *
     * @return Restituisce l'hash SHA-256 del documento.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Restituisce il modello rappresentato come stringa.
     *
     * @return Restituisce il modello rappresentato come stringa.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " { " + "path: " + getPath()
                + ", hash: " + getHash() + " }";
    }

}
