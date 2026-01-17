package ro.pub.cs.systems.eim.practicaltest02;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String city;
    private String informationType;
    private TextView resultTextView;

    public ClientThread(String address, int port,
                        String city, String informationType,
                        TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.city = city;
        this.informationType = informationType;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter =
                    new PrintWriter(socket.getOutputStream(), true);

            // send request
            printWriter.println(city);
            printWriter.println(informationType);

            // read response
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String response = line;
                resultTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextView.append(response + "\n");
                    }
                });
            }

            socket.close();

        } catch (Exception e) {
            resultTextView.post(() ->
                    resultTextView.setText("Client error"));
        }
    }
}
