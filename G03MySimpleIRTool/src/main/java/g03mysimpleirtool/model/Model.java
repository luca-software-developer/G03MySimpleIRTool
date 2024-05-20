package g03mysimpleirtool.model;

/**
 * L'enumerazione {@code Model} rappresenta il modello IR selezionato.
 */
public enum Model {

    /**
     * Rappresenta il modello di default.
     */
    TF("tf (Default)"),
    /**
     * Rappresenta il modello tf-idf.
     */
    TFIDF("tf-idf");

    /**
     * Definisce il nome del modello.
     */
    private final String name;

    /**
     * Costruttore.
     *
     * @param name Nome del modello.
     */
    private Model(String name) {
        this.name = name;
    }

    /**
     * Restituisce il nome del modello.
     *
     * @return Restituisce il nome del modello.
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce il nome del modello.
     *
     * @return Restituisce il nome del modello.
     */
    @Override
    public String toString() {
        return name;
    }

}
