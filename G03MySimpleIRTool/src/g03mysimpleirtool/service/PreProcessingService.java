package g03mysimpleirtool.service;

import static g03mysimpleirtool.G03MySimpleIRTool.APP_CACHE_FILE;
import g03mysimpleirtool.model.Dictionary;
import g03mysimpleirtool.model.TFDocumentModel;
import static g03mysimpleirtool.util.Dialogs.showError;
import g03mysimpleirtool.util.TextFileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * La classe {@code PreProcessingService} rappresenta il servizio di
 * pre-elaborazione dei documenti.
 */
public class PreProcessingService extends Service<Set<TFDocumentModel>> {

    /**
     * Definisce il path del folder.
     */
    private final String path;

    /**
     * Costruttore.
     *
     * @param path Path del folder.
     */
    public PreProcessingService(String path) {
        this.path = path;
    }

    /**
     * Restituisce il path del folder.
     *
     * @return Restituisce il path del folder.
     */
    public String getPath() {
        return path;
    }

    /**
     * Invocato dopo l'avvio del servizio sul thread dell'applicazione JavaFX.
     *
     * @return Restituisce il {@code Task}.
     */
    @Override
    protected Task<Set<TFDocumentModel>> createTask() {
        return new Task<Set<TFDocumentModel>>() {
            /**
             * Invocato quando il task viene eseguito.
             *
             * @return Restituisce il risultato del task, se presente.
             * @throws Exception Sollevata in caso di eccezione non gestita
             * durante l'operazione in background.
             */
            @Override
            protected Set<TFDocumentModel> call() throws Exception {
                final AtomicLong nodes = new AtomicLong(0);
                final long totalNodes = Files.walk(Paths.get(path))
                        .filter(new TextFileFilter())
                        .count();
                updateProgress(nodes.get(), totalNodes);

                //  Qui memorizziamo tutti i modelli (dalla cache e correnti).
                final Map<String, TFDocumentModel> models = new HashMap<>();

                //  Controlla se il file di cache esiste.
                if (Files.exists(Paths.get(APP_CACHE_FILE))) {
                    //  Legge i modelli dalla cache.
                    Map<String, TFDocumentModel> cache;
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(
                            new FileInputStream(APP_CACHE_FILE))) {
                        cache = (Map<String, TFDocumentModel>) objectInputStream.readObject();
                    } catch (ClassNotFoundException ex) {
                        cache = null;
                    }
                    //  Li aggiunge alla mappa con tutti i modelli.
                    models.putAll(cache);
                }

                final Set<TFDocumentModel> current = new HashSet<>();

                //  Per ciascun file di testo nel folder...
                Files.walk(Paths.get(path))
                        .filter(new TextFileFilter())
                        .forEach(path -> {
                            boolean cachedAndValid = false;
                            //  Se il modello per quel file è già in cache...
                            if (models.containsKey(path.toString())) {
                                TFDocumentModel cacheModel = models.get(path.toString());
                                //  Se il modello è aggiornato (gli hash coincidono)...
                                if (cacheModel.isUpToDate()) {
                                    //  Lo aggiunge all'insieme dei modelli
                                    //  per il folder corrente
                                    current.add(cacheModel);
                                    cachedAndValid = true;
                                }
                            }
                            //  In caso contrario...
                            if (!cachedAndValid) {
                                try {
                                    //  Calcola il modello a partire dal documento...
                                    TFDocumentModel computed = TFDocumentModel.fromPath(path.toString());
                                    //  ... lo aggiunge all'insieme dei modelli correnti...
                                    current.add(computed);
                                    //  ... e anche alla mappa di tutti i modelli.
                                    models.put(path.toString(), computed);
                                } catch (IOException ex) {
                                    Platform.runLater(() -> showError("Errore durante l'apertura del documento \""
                                            + path.toAbsolutePath() + "\"."));
                                }
                            }
                            updateProgress(nodes.incrementAndGet(), totalNodes);
                        });

                //  Aggiorna la cache con gli eventuali nuovi modelli.
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                        new FileOutputStream(APP_CACHE_FILE))) {
                    objectOutputStream.writeObject(models);
                }

                //  Costruisce il dizionario a partire dai modelli.
                Dictionary dictionary = new Dictionary(current);

                //  Estende i modelli utilizzando il dizionario.
                Set<TFDocumentModel> extended = current.stream()
                        .map(model -> model.toExtended(dictionary))
                        .collect(Collectors.toSet());

                //  A questo punto i modelli da considerare sono quelli estesi.
                current.clear();
                current.addAll(extended);

                //  Ora abbiamo tutto quello che serve per poter fare le query!
                return current;
            }
        };
    }

}
