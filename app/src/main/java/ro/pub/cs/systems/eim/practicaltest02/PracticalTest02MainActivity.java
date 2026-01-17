package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button connectButton;

    private EditText addressEditText;
    private EditText clientPortEditText;
    private EditText cityEditText;
    private Spinner informationSpinner;
    private Button getWeatherButton;
    private TextView weatherTextView;

    private ServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        // server UI
        serverPortEditText = findViewById(R.id.port);
        connectButton = findViewById(R.id.connect);

        // client UI
        addressEditText = findViewById(R.id.address);
        clientPortEditText = findViewById(R.id.port2);
        cityEditText = findViewById(R.id.city);
        informationSpinner = findViewById(R.id.spinner);
        getWeatherButton = findViewById(R.id.get_weather);
        weatherTextView = findViewById(R.id.weather);

        // START SERVER
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String portString = serverPortEditText.getText().toString();
                if (portString.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Server port required", Toast.LENGTH_SHORT).show();
                    return;
                }

                int port = Integer.parseInt(portString);
                serverThread = new ServerThread(port);
                serverThread.start();

                Toast.makeText(getApplicationContext(),
                        "Server started on port " + port,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // CLIENT REQUEST
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = addressEditText.getText().toString();
                String portString = clientPortEditText.getText().toString();
                String city = cityEditText.getText().toString();
                String infoType = informationSpinner.getSelectedItem().toString();

                if (address.isEmpty() || portString.isEmpty()
                        || city.isEmpty() || infoType.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "All client fields required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (serverThread == null || !serverThread.isAlive()) {
                    Toast.makeText(getApplicationContext(),
                            "Server not running", Toast.LENGTH_SHORT).show();
                    return;
                }

                int port = Integer.parseInt(portString);
                weatherTextView.setText("");

                ClientThread clientThread =
                        new ClientThread(address, port, city, infoType, weatherTextView);
                clientThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
