package g03mysimpleirtool.service;

import static g03mysimpleirtool.G03MySimpleIRTool.APP_HELP_URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * La classe {@code HelpFetchService} rappresenta il servizio che effettua il
 * fetch del codice HTML della guida in linea.
 */
public class HelpFetchService extends Service<String> {

    /**
     * Invocato dopo l'avvio del servizio sul thread dell'applicazione JavaFX.
     *
     * @return Restituisce il {@code Task}.
     */
    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            /**
             * Invocato quando il task viene eseguito. Il codice HTML della
             * pagina può essere scaricato da un webserver oppure letto da un
             * file HTML locale in caso di problemi a contattare il server.
             *
             * Per l'implementazione di questo metodo si è scelto di utilizzare
             * la classe {@code Socket}, è possibile realizzare il trasferimento
             * del contenuto del file HTML hostato dal server web anche
             * utilizzando il metodo {@code openStream()} della classe
             * {@code URL}.
             *
             * @return Restituisce il risultato del task, se presente.
             * @throws Exception Sollevata in caso di eccezione non gestita
             * durante l'operazione in background.
             */
            @Override
            protected String call() throws Exception {
                //  I numeri di porta sono rappresentati su 16 bit. La porta
                //  standard per il protocollo HTTP (HyperText Transfer Protocol)
                //  è la porta 80.
                final short httpPort = 80;
                final URL httpURL = new URL(APP_HELP_URL);
                final String httpRequest = "GET /index.html HTTP/1.1\r\nHost: "
                        + httpURL.getHost() + "\r\n\r\n";
                final StringBuilder httpPayload = new StringBuilder();

                //  Apre una connessione TCP con il webserver sulla porta 80.
                try (Socket socket = new Socket(httpURL.getHost(), httpPort);
                        InputStream in = socket.getInputStream();
                        OutputStream out = socket.getOutputStream()) {

                    //  La richiesta HTTP viene convertita in un array di byte
                    //  e inviata sullo stream (di rete) di output.
                    out.write(httpRequest.getBytes());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                        String line;
                        while ((line = reader.readLine()) != null && !line.isEmpty()) {
                            //  Salta header HTTP.
                        }
                        reader.readLine();  //  Salta riga vuota.

                        //  Legge il payload del pacchetto.
                        while ((line = reader.readLine()) != null) {
                            httpPayload
                                    .append(line)
                                    .append('\n');
                        }
                    }
                }

                return httpPayload.toString();
            }
        };
    }

}
