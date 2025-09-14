package com.example.chatgptapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import okhttp3.*;
import org.json.*;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText userInput;
    private Button sendButton;
    private TextView responseText;

    private final String API_KEY = "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"; // Thay b?ng API key c?a b?n
    private final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        responseText = findViewById(R.id.responseText);

        sendButton.setOnClickListener(v -> {
            String prompt = userInput.getText().toString();
            if (!prompt.isEmpty()) {
                sendRequest(prompt);
            }
        });
    }

    private void sendRequest(String prompt) {
        OkHttpClient client = new OkHttpClient();

        JSONObject message = new JSONObject();
        try {
            message.put("role", "user");
            message.put("content", prompt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray messages = new JSONArray();
        messages.put(message);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseText.setText("L?i k?t n?i: " + e.getMessage()));
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        String reply = choices.getJSONObject(0)
                                              .getJSONObject("message")
                                              .getString("content");

                        runOnUiThread(() -> responseText.setText(reply));
                    } catch (JSONException e) {
                        runOnUiThread(() -> responseText.setText("L?i x? lý JSON"));
                    }
                } else {
                    runOnUiThread(() -> responseText.setText("L?i API: " + response.code()));
                }
            }
        });
    }
}