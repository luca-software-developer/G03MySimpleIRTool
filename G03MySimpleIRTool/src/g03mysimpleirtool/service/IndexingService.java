package g03mysimpleirtool.service;

import g03mysimpleirtool.util.TextFileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * La classe {@code IndexingService} rappresenta il servizio di indicizzazione
 * del folder.
 */
public class IndexingService extends Service<TreeItem<Path>> {

    /**
     * Definisce il path del folder.
     */
    private final Path path;

    /**
     * Costruttore.
     *
     * @param path {@code Path} del folder.
     */
    public IndexingService(Path path) {
        this.path = path;
    }

    /**
     * Restituisce il path del folder.
     *
     * @return Restituisce il path del folder.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Invocato dopo l'avvio del servizio sul thread dell'applicazione JavaFX.
     *
     * @return Restituisce il {@code Task}.
     */
    @Override
    protected Task<TreeItem<Path>> createTask() {
        return new Task<TreeItem<Path>>() {

            /**
             * Definisce il numero attuale di nodi visitati.
             */
            final AtomicLong visitedNodes = new AtomicLong(0);

            /**
             * Invocato quando il task viene eseguito.
             *
             * @return Restituisce il risultato del task, se presente.
             * @throws Exception Sollevata in caso di eccezione non gestita
             * durante l'operazione in background.
             */
            @Override
            protected TreeItem<Path> call() throws Exception {
                final long totalNodes = Files.walk(path).count();
                final TreeItem<Path> root = new TreeItem<>(path);
                root.setExpanded(true);
                updateProgress(visitedNodes.get(), totalNodes);
                visitTree(root, new TextFileFilter(), totalNodes);
                return root;
            }

            /**
             * Effettua la visita depth-first del sottoalbero del file system
             * radicato sul folder rappresentato dal {@code TreeItem} passato
             * come parametro.
             *
             * @param current {@code TreeItem} corrispondente al nodo corrente.
             * @param fileFilter Filtro da applicare ai file.
             * @param totalNodes Totale dei nodi da visitare.
             * @throws IOException Sollevata in caso di errore di I/O.
             */
            private void visitTree(TreeItem<Path> current, Predicate<Path> fileFilter, long totalNodes) throws IOException, InterruptedException {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(current.getValue())) {
                    for (Path path : stream) {
                        final TreeItem<Path> node = new TreeItem<>(path);
                        current.setExpanded(true);
                        if (Files.isDirectory(path)) {
                            current.getChildren().add(node);
                            visitTree(node, fileFilter, totalNodes);
                        } else if (fileFilter.test(path)) {
                            current.getChildren().add(node);
                        }
                        updateProgress(visitedNodes.incrementAndGet(), totalNodes);
                    }
                }
            }
        };
    }

}
