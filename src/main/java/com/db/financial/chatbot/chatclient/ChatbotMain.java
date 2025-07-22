package com.db.financial.chatbot.chatclient;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ChatbotMain {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "mistralai/mistral-7b-instruct:free";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nPrompt : ");
            String prompt = scanner.nextLine();
            if ("exit".equalsIgnoreCase(prompt)) break;

            JSONObject payload = new JSONObject()
                    .put("model", MODEL)
                    .put("stream", true)
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("content", prompt)));

            OkHttpClient client = new OkHttpClient.Builder().build();

            RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "http://localhost")
                    .addHeader("X-Title", "MyApp")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    System.out.println(" API error: " + response);
                    continue;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                String line;
                System.out.println("Response:");
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String json = line.substring(6);
                        if ("[DONE]".equals(json)) break;

                        JSONObject data = new JSONObject(json);
                        String delta = data
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("delta")
                                .optString("content", "");
                        System.out.print(delta);
                    }
                }
                System.out.println("\n✅ Done.\n");

            } catch (Exception e) {
                System.err.println(" Error: " + e.getMessage());
            }
        }

        System.out.println("👋 Exiting.");
    }
}