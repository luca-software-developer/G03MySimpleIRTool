package g03mysimpleirtool.util;

import g03mysimpleirtool.G03MySimpleIRTool;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_NAME;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_STATS_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_STATS_VIEW;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_VIEWER_ICON;
import static g03mysimpleirtool.G03MySimpleIRTool.APP_VIEWER_VIEW;
import g03mysimpleirtool.controller.G03MySimpleIRToolStatsController;
import g03mysimpleirtool.controller.G03MySimpleIRToolViewerController;
import g03mysimpleirtool.model.TFDocumentModel;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.swing.filechooser.FileSystemView;

/**
 * La classe {@code Dialogs} fornisce metodi di utilità per la visualizzazione
 * di finestre di dialogo.
 */
public class Dialogs {

    /**
     * Costruttore.
     */
    private Dialogs() {
    }

    /**
     * Costruisce, configura e mostra lo Stage.
     *
     * @param view Path relativo della view.
     * @param title Titolo della finestra.
     * @param icon Icona della finestra.
     * @throws IOException Sollevata in caso di errore di I/O durante il
     * caricamento della view.
     */
    public static void showStage(String view, String title, String icon) throws IOException {
        showStage(view, title, icon, false, null);
    }

    /**
     * Costruisce, configura e mostra lo Stage.
     *
     * @param view Path relativo della view.
     * @param title Titolo della finestra.
     * @param icon Icona della finestra.
     * @param resizable Indica se la finestra è ridimensionabile.
     * @throws IOException Sollevata in caso di errore di I/O durante il
     * caricamento della view.
     */
    public static void showStage(String view, String title, String icon, boolean resizable) throws IOException {
        showStage(view, title, icon, resizable, null);
    }

    /**
     * Costruisce, configura e mostra lo Stage.
     *
     * @param view Path relativo della view.
     * @param title Titolo della finestra.
     * @param icon Icona della finestra.
     * @param resizable Indica se la finestra è ridimensionabile.
     * @param controller Controller MVC da utilizzare per la view.
     * @throws IOException Sollevata in caso di errore di I/O durante il
     * caricamento della view.
     */
    public static void showStage(String view, String title, String icon, boolean resizable, Initializable controller) throws IOException {
        final FXMLLoader loader = new FXMLLoader(G03MySimpleIRTool.class.getResource(view));
        final Stage stage = new Stage();
        if (controller != null) {
            loader.setController(controller);
        }
        stage.setScene(new Scene(loader.load()));
        stage.getIcons().add(new Image(G03MySimpleIRTool.class.getResourceAsStream(icon)));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setResizable(resizable);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Visualizza un {@code FileChooser} per la selezione di un file.
     *
     * @param ownerWindow Oggetto {@code Window} genitore della finestra di
     * dialogo visualizzata.
     * @return Restituisce il file selezionato o {@code null} se non è stato
     * selezionato alcun file.
     */
    public static File chooseFile(Window ownerWindow) {
        return chooseFile(ownerWindow, null);
    }

    /**
     * Visualizza un {@code FileChooser} per la selezione di un file.
     *
     * @param ownerWindow Oggetto {@code Window} genitore della finestra di
     * dialogo visualizzata.
     * @param extensionFilter Filtro per le estensioni dei file selezionabili.
     * @return Restituisce il file selezionato o {@code null} se non è stato
     * selezionato alcun file.
     */
    public static File chooseFile(Window ownerWindow, ExtensionFilter extensionFilter) {
        final FileChooser fileChooser = new FileChooser();
        final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        fileChooser.setTitle("Apri file");
        if (extensionFilter != null) {
            fileChooser.getExtensionFilters().add(extensionFilter);
        }
        fileChooser.setInitialDirectory(fileSystemView.getHomeDirectory());
        return fileChooser.showOpenDialog(ownerWindow);
    }

    /**
     * Visualizza un {@code DirectoryChooser} per la selezione di una directory.
     *
     * @param ownerWindow Oggetto {@code Window} genitore della finestra di
     * dialogo visualizzata.
     * @return Restituisce la directory selezionata o {@code null} se non è
     * stata selezionata alcuna directory.
     */
    public static File chooseDirectory(Window ownerWindow) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        directoryChooser.setTitle("Apri cartella");
        directoryChooser.setInitialDirectory(fileSystemView.getHomeDirectory());
        return directoryChooser.showDialog(ownerWindow);
    }

    /**
     * Apre il visualizzatore di documenti integrato per mostrare il contenuto
     * del documento al {@code Path} specificato.
     *
     * @param path {@code Path} del documento.
     */
    public static void showDocumentViewer(Path path) {
        try {
            showStage(APP_VIEWER_VIEW, path.getFileName().toString(),
                    APP_VIEWER_ICON, true, new G03MySimpleIRToolViewerController(path));
        } catch (IOException ex) {
            showError("Errore durante l'apertura del visualizzatore.");
        }
    }

    /**
     * Apre il visualizzatore di statistiche integrato per mostrare le
     * statistiche del documento al {@code Path} specificato.
     *
     * @param path {@code Path} del documento.
     */
    public static void showStatsViewer(Path path) {
        try {
            showStage(APP_STATS_VIEW,
                    "Statistiche di " + path.getFileName().toString(),
                    APP_STATS_ICON, true, new G03MySimpleIRToolStatsController(
                            TFDocumentModel.fromPath(path.toString())));
        } catch (IOException ex) {
            showError("Errore durante l'apertura del visualizzatore.");
        }
    }

    /**
     * Mostra il documento nel folder in cui è contenuto utilizzando il file
     * explorer di default disponibile sulla piattaforma.
     *
     * @param path {@code Path} del documento.
     */
    public static void revealInFolder(Path path) {
        try {
            Desktop.getDesktop().browse(path.getParent().toUri());
        } catch (IOException ex) {
            showError("Errore durante l'apertura della cartella.");
        }
    }

    /**
     * Visualizza una {@code Alert} di scelta.
     *
     * @param headerText Testo da visualizzare nell'area di intestazione della
     * finestra di dialogo.
     * @param contentText Testo da visualizzare nell'area del contenuto della
     * finestra di dialogo.
     * @return Restituisce {@code true} se viene premuto il pulsante di tipo
     * {@code ButtonType.YES}, {@code false} altrimenti.
     */
    public static boolean askYesNo(String headerText, String contentText) {
        final Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        final Stage stage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
        final Image icon = new Image(G03MySimpleIRTool.class.getResourceAsStream(APP_ICON));
        stage.getIcons().add(icon);
        confirmationAlert.setTitle(APP_NAME);
        confirmationAlert.setHeaderText(headerText);
        confirmationAlert.setContentText(contentText);
        confirmationAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    /**
     * Visualizza una {@code Alert} di informazioni.
     *
     * @param contentText Testo da visualizzare nell'area del contenuto della
     * finestra di dialogo.
     */
    public static void showInformation(String contentText) {
        final Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
        final Stage stage = (Stage) informationAlert.getDialogPane().getScene().getWindow();
        final Image icon = new Image(G03MySimpleIRTool.class.getResourceAsStream(APP_ICON));
        stage.getIcons().add(icon);
        informationAlert.setTitle(APP_NAME);
        informationAlert.setContentText(contentText);
        informationAlert.showAndWait();
    }

    /**
     * Visualizza una {@code Alert} di errore.
     *
     * @param contentText Testo da visualizzare nell'area del contenuto della
     * finestra di dialogo.
     */
    public static void showError(String contentText) {
        final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        final Stage stage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
        final Image icon = new Image(G03MySimpleIRTool.class.getResourceAsStream(APP_ICON));
        stage.getIcons().add(icon);
        errorAlert.setTitle(APP_NAME);
        errorAlert.setContentText(contentText);
        errorAlert.showAndWait();
    }

}
