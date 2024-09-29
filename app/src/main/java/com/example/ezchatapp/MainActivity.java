package com.example.ezchatapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;
    private EditText userInputEditText;
    private Button sendButton;
    private static final String CHATGPT_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "sk-PVrAgUvDdb2-r8Eamf4H5uhYWTm4nrIrSx_aTSnkHvT3BlbkFJ1LE1PDNnNDtOk9Ssvt0hqybYHAIHlPDPCHkkjG_fMA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        userInputEditText = findViewById(R.id.user_input);
        sendButton = findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = userInputEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(userMessage)) {
                    sendMessage(userMessage);
                }
            }
        });
    }

    private void sendMessage(String userMessage) {
        // Add user message to the RecyclerView
        messageList.add(new ChatMessage(userMessage, "user"));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
        userInputEditText.setText("");


        getBotResponse(userMessage);
    }

    private void getBotResponse(String userMessage) {
        OkHttpClient client = new OkHttpClient();


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("inputs", userMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonBody.toString()
        );

        Request request = new Request.Builder()
                .url(CHATGPT_API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonResponse = new JSONArray(responseData);
                        String botResponse = jsonResponse.getJSONObject(0).getString("generated_text");


                        runOnUiThread(() -> {
                            messageList.add(new ChatMessage(botResponse, "bot"));
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
