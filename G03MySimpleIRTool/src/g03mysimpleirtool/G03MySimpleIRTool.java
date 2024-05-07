package g03mysimpleirtool;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * La classe {@code G03MySimpleIRTool} rappresenta l'applicazione JavaFX.
 */
public class G03MySimpleIRTool extends Application {

    /**
     * Definisce il nome dell'applicazione visualizzato nella barra del titolo e
     * nelle finestre di dialogo.
     */
    public static final String APP_NAME = G03MySimpleIRTool.class.getSimpleName();

    /**
     * Definisce il path relativo della view principale dell'applicazione.
     */
    public static final String APP_VIEW = "view/G03MySimpleIRToolView.fxml";

    /**
     * Definisce il path relativo dell'icona dell'applicazione visualizzata
     * nella barra del titolo e nelle finestre di dialogo.
     */
    public static final String APP_ICON = "icon/icon.png";

    /**
     * Definisce il path relativo della view di preferenze dell'applicazione.
     */
    public static final String APP_PREFS_VIEW = "view/G03MySimpleIRToolPreferencesView.fxml";

    /**
     * Definisce il path relativo dell'icona della view di preferenze
     * dell'applicazione.
     */
    public static final String APP_PREFS_ICON = "icon/settings.png";

    /**
     * Definisce il path relativo della view di informazioni sull'applicazione.
     */
    public static final String APP_ABOUT_VIEW = "view/G03MySimpleIRToolAboutView.fxml";

    /**
     * Definisce il path relativo dell'icona della view di informazioni
     * sull'applicazione.
     */
    public static final String APP_ABOUT_ICON = "icon/about.png";

    /**
     * Definisce il path relativo della view di aiuto dell'applicazione.
     */
    public static final String APP_HELP_VIEW = "view/G03MySimpleIRToolHelpView.fxml";

    /**
     * Definisce il path relativo dell'icona della view di aiuto
     * dell'applicazione.
     */
    public static final String APP_HELP_ICON = "icon/help.png";

    /**
     * Definisce il path relativo del file HTML utilizzato view di aiuto
     * dell'applicazione.
     */
    public static final String APP_HELP_HTML = "asset/help.html";

    /**
     * Definisce il path relativo del fragment da visualizzare per ciascun
     * {@code TreeItem} nella vista della directory corrente.
     */
    public static final String APP_TREEITEM_FRAGMENT = "fragment/TreeItemFragment.fxml";

    /**
     * Definisce il path relativo del fragment da visualizzare per ciascun
     * risultato.
     */
    public static final String APP_RESULT_FRAGMENT = "fragment/ResultFragment.fxml";

    /**
     * Definisce il path relativo dell'icona che rappresenta una cartella.
     */
    public static final String APP_FOLDER_ICON = "icon/folder.png";

    /**
     * Definisce il path relativo dell'icona che rappresenta un documento.
     */
    public static final String APP_DOCUMENT_ICON = "icon/document.png";

    /**
     * Definisce il path relativo della view del visualizzatore di documenti
     * integrato dell'applicazione.
     */
    public static final String APP_VIEWER_VIEW = "view/G03MySimpleIRToolViewerView.fxml";

    /**
     * Definisce il path relativo dell'icona del visualizzatore di documenti
     * integrato dell'applicazione.
     */
    public static final String APP_VIEWER_ICON = "icon/view.png";

    /**
     * Definisce il path relativo della view del visualizzatore di statistiche
     * dell'applicazione.
     */
    public static final String APP_STATS_VIEW = "view/G03MySimpleIRToolStatsView.fxml";

    /**
     * Definisce il path relativo dell'icona del visualizzatore di statistiche
     * dell'applicazione.
     */
    public static final String APP_STATS_ICON = "icon/stats.png";

    /**
     * Definisce il path relativo del file di cache dei modelli.
     */
    public static final String APP_CACHE_FILE = "irtcache.bin";

    /**
     * Punto di ingresso principale dell'applicazione JavaFX.
     *
     * @param stage Stage primario per l'applicazione JavaFX.
     * @throws IOException Sollevata in caso di errore di I/O durante il
     * caricamento della view.
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(new Scene(FXMLLoader.load(G03MySimpleIRTool.class.getResource(APP_VIEW))));
        stage.getIcons().add(new Image(G03MySimpleIRTool.class.getResourceAsStream(APP_ICON)));
        stage.setTitle(APP_NAME);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Metodo main dell'applicazione Java. Nelle applicazioni JavaFX può anche
     * essere omesso. Scegliamo di dichiarare gli argomenti da riga di comando
     * come un varargs di String.
     *
     * @param args Argomenti da riga di comando.
     */
    public static void main(String... args) {
        launch(args);
    }

}
