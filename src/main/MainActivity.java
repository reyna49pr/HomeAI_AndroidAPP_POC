package com.example.homeaipoc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "http://<your_backend_ip>:5000/control"; // Replace with your Flask backend URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTurnOn = findViewById(R.id.btnTurnOn);
        Button btnTurnOff = findViewById(R.id.btnTurnOff);
        Button btnChangeColor = findViewById(R.id.btnChangeColor);

        btnTurnOn.setOnClickListener(v -> sendCommand("light", "turn_on"));
        btnTurnOff.setOnClickListener(v -> sendCommand("light", "turn_off"));
        btnChangeColor.setOnClickListener(v -> sendCommand("light", "change_color"));
    }

    private void sendCommand(String device, String action) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String payload = String.format("{\"device\": \"%s\", \"action\": \"%s\"}", device, action);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(payload);
                writer.flush();

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        Toast.makeText(this, "Command sent successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send command.", Toast.LENGTH_SHORT).show();
                    }
                });

                writer.close();
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
