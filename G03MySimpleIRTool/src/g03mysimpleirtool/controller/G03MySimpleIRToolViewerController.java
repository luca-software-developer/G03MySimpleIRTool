package g03mysimpleirtool.controller;

import static g03mysimpleirtool.util.Dialogs.showError;
import g03mysimpleirtool.util.TextProcessing;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * La classe {@code G03MySimpleIRToolViewerController} rappresenta il controller
 * MVC del visualizzatore di documenti integrato dell'applicazione. Fornisce i
 * metodi handler per la gestione degli eventi di interfaccia grafica.
 */
public class G03MySimpleIRToolViewerController implements Initializable {

    @FXML
    private TextArea txtDocument;

    /**
     * Definisce il {@code Path} del documento.
     */
    private final Path path;

    /**
     * Costruttore
     *
     * @param path {@code Path} del documento.
     */
    public G03MySimpleIRToolViewerController(Path path) {
        this.path = path;
    }

    /**
     * Restituisce il {@code Path} del documento.
     *
     * @return Restituisce il {@code Path} del documento.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Inizializza il controller.
     *
     * @param url URL utilizzata per risolvere i percorsi relativi dell'oggetto
     * radice, o null se la posizione non è nota.
     * @param rb Risorse utilizzate per localizzare l'oggetto radice, o null se
     * l'oggetto radice non è stato localizzato.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            txtDocument.setText(String.join("\n",
                    TextProcessing.getLineStream(path.toString())
                            .collect(Collectors.toList())));
        } catch (IOException ex) {
            showError("Errore durante l'apertura del file.");
            Platform.runLater(() -> ((Stage) txtDocument.getScene()
                    .getWindow()).close());
        }
    }

}
