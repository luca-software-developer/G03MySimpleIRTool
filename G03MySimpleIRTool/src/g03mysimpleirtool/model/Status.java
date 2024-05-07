package g03mysimpleirtool.model;

/**
 * L'enumerazione {@code Status} rappresenta lo stato attuale dell'applicazione.
 */
public enum Status {

    /**
     * Rappresenta lo stato di pronto.
     */
    READY("Pronto"),
    /**
     * Rappresenta la fase di indicizzazione della directory.
     */
    INDEXING("Indicizzazione in corso"),
    /**
     * Rappresenta la fase di pre-elaborazione.
     */
    PREPROCESSING("Pre-elaborazione in corso"),
    /**
     * Rappresenta la fase di ricerca.
     */
    SEARCHING("Ricerca in corso");

    /**
     * Definisce il messaggio relativo allo stato.
     */
    private final String message;

    /**
     * Costruttore.
     *
     * @param message Messaggio relativo allo stato.
     */
    private Status(String message) {
        this.message = message;
    }

    /**
     * Restituisce il messaggio relativo allo stato.
     *
     * @return Restituisce il messaggio relativo allo stato.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Restituisce lo stato rappresentato come stringa.
     *
     * @return Restituisce il messaggio relativo allo stato.
     */
    @Override
    public String toString() {
        return message;
    }

}
