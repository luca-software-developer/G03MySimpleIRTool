package g03mysimpleirtool.controller;

import g03mysimpleirtool.G03MySimpleIRTool;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_HELP_HTML;
import g03mysimpleirtool.service.HelpFetchService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;

/**
 * La classe {@code G03MySimpleIRToolHelpController} rappresenta il controller
 * MVC della view di aiuto dell'applicazione. Fornisce i metodi handler per la
 * gestione degli eventi di interfaccia grafica.
 */
public class G03MySimpleIRToolHelpController implements Initializable {

    @FXML
    private WebView webView;

    @FXML
    private Button btnHome;

    @FXML
    private ProgressIndicator piLoading;

    @FXML
    private TextField txtCerca;

    @FXML
    private Button btnCerca;

    @FXML
    private Button btnStampa;

    /**
     * Indica se è in corso il download della pagina.
     */
    private BooleanProperty fetchingProperty;

    /**
     * Indica se è in corso il caricamento della pagina.
     */
    private ReadOnlyBooleanProperty loadingProperty;

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
        fetchingProperty = new SimpleBooleanProperty(false);
        loadingProperty = webView.getEngine().getLoadWorker().runningProperty();
        btnHome.disableProperty().bind(fetchingProperty.or(loadingProperty));
        btnHome.visibleProperty().bind(fetchingProperty.or(loadingProperty).not());
        piLoading.visibleProperty().bind(fetchingProperty.or(loadingProperty));
        txtCerca.disableProperty().bind(fetchingProperty.or(loadingProperty));
        btnCerca.disableProperty().bind(Bindings.createBooleanBinding(
                () -> fetchingProperty.get()
                || txtCerca.getText().trim().isEmpty()
                || webView.getEngine().getLoadWorker().isRunning(),
                fetchingProperty, txtCerca.textProperty(), loadingProperty));
        btnStampa.disableProperty().bind(fetchingProperty.or(loadingProperty));
        webView.getEngine().setJavaScriptEnabled(true);
        showHelpHTML();
    }

    /**
     * Visualizza la pagina HTML di help attraverso la WebView.
     */
    private void showHelpHTML() {
        //  Effettua il fetch della pagina di help dal webserver.
        final HelpFetchService helpFetchService = new HelpFetchService();
        fetchingProperty.set(true);
        helpFetchService.setOnSucceeded(event -> {
            //  Visualizza la pagina HTML di help attraverso la WebView.
            fetchingProperty.set(false);
            webView.getEngine()
                    .loadContent(helpFetchService.getValue(), "text/html");
        });
        helpFetchService.setOnFailed(event -> {
            //  Fallback su file HTML locale in caso di server non raggiungibile.
            fetchingProperty.set(false);
            webView.getEngine()
                    .load(G03MySimpleIRTool.class.getResource(APP_HELP_HTML)
                            .toExternalForm());
        });
        helpFetchService.start();
    }

    /**
     * Gestisce l'evento di click sul {@code Button} [Home].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void home(ActionEvent actionEvent) {
        webView.getEngine().executeScript("scrollToTop();");
        txtCerca.clear();
        txtCerca.requestFocus();
    }

    /**
     * Gestisce l'evento di click sul {@code Button} [Cerca].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void cerca(ActionEvent actionEvent) {
        final String text = txtCerca.getText().trim();
        webView.getEngine().executeScript("search('" + text + "');");
    }

    /**
     * Gestisce l'evento di click sul {@code Button} [Stampa].
     *
     * @param actionEvent Evento JavaFX.
     */
    @FXML
    private void stampa(ActionEvent actionEvent) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null
                && printerJob.showPrintDialog(webView.getScene().getWindow())) {
            webView.getEngine().print(printerJob);
            printerJob.endJob();
        }
    }

}
