package g03mysimpleirtool.controller;

import static g03mysimpleirtool.G03MySimpleIRTool.APP_STATS_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_STATS_VIEW;
import g03mysimpleirtool.model.TFDocumentModel;
import static g03mysimpleirtool.util.Dialogs.showDocumentViewer;
import static g03mysimpleirtool.util.Dialogs.showStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * La classe {@code G03MySimpleIRToolResultController} rappresenta il controller
 * MVC del frammento di interfaccia che costituisce il singolo risultato.
 * Fornisce i metodi handler per la gestione degli eventi di interfaccia
 * grafica.
 */
public class G03MySimpleIRToolResultController implements Initializable {

    @FXML
    private Label lblName;

    /**
     * Definisce il modello del documento.
     */
    private final TFDocumentModel model;

    /**
     * Costruttore.
     *
     * @param model Modello del documento.
     */
    public G03MySimpleIRToolResultController(TFDocumentModel model) {
        this.model = model;
    }

    /**
     * Restituisce il modello del documento.
     *
     * @return Restituisce il modello del documento.
     */
    public TFDocumentModel getModel() {
        return model;
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
        lblName.setText(model.getPath().getFileName().toString());
    }

    /**
     * Gestisce l'evento di click sul {@code Button} [Visualizza].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void visualizza(ActionEvent actionEvent) {
        showDocumentViewer(model.getPath());
    }

    /**
     * Gestisce l'evento di click sul {@code Button} [Statistiche].
     *
     * @param actionEvent Evento JavaFX.
     * @throws IOException Sollevata in caso di errore di I/O.
     */
    @FXML
    private void statistiche(ActionEvent actionEvent) throws IOException {
        showStage(APP_STATS_VIEW,
                "Statistiche di " + model.getPath().getFileName().toString(),
                APP_STATS_ICON, true, new G03MySimpleIRToolStatsController(model));
    }

}
