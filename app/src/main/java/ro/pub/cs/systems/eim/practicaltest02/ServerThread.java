package ro.pub.cs.systems.eim.practicaltest02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private HashMap<String, WeatherForecastInformation> data;

    public ServerThread(int port) {
        data = new HashMap<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized HashMap<String, WeatherForecastInformation> getData() {
        return data;
    }


    public synchronized void setData(String city, WeatherForecastInformation info) {
        data.put(city, info);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Socket socket = serverSocket.accept();
                new CommunicationThread(this, socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
