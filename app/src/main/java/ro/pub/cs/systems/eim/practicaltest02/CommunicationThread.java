package ro.pub.cs.systems.eim.practicaltest02;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    private static final String API_KEY = "e03c3b32cfb5a6f7069f2ef29237d87e";

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);

            String city = reader.readLine();
            String informationType = reader.readLine();

            if (city == null || informationType == null) {
                socket.close();
                return;
            }

            // 🔹 CACHE
            WeatherForecastInformation info =
                    serverThread.getData().get(city);


            if (info == null) {

                // 🌐 INTERNET
                String urlString =
                        "https://api.openweathermap.org/data/2.5/weather?q="
                                + city + "&appid=" + API_KEY + "&units=metric";

                URL url = new URL(urlString);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader httpReader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = httpReader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject json = new JSONObject(result.toString());

                JSONObject main = json.getJSONObject("main");
                JSONObject wind = json.getJSONObject("wind");
                String condition =
                        json.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("main");

                info = new WeatherForecastInformation(
                        String.valueOf(main.getDouble("temp")),
                        String.valueOf(wind.getDouble("speed")),
                        condition,
                        String.valueOf(main.getInt("pressure")),
                        String.valueOf(main.getInt("humidity"))
                );

                serverThread.setData(city, info);
            }

            // 🔹 FILTRARE
            String response;
            switch (informationType) {
                case "temperature":
                    response = info.getTemperature();
                    break;
                case "wind_speed":
                    response = info.getWindSpeed();
                    break;
                case "condition":
                    response = info.getCondition();
                    break;
                case "pressure":
                    response = info.getPressure();
                    break;
                case "humidity":
                    response = info.getHumidity();
                    break;
                case "all":
                    response = info.toString();
                    break;
                default:
                    response = "ERROR";
            }

            writer.println(response);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
