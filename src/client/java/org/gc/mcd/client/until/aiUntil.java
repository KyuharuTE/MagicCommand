package org.gc.mcd.client.until;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class aiUntil {
    private static final String URL = "https://api.chatanywhere.tech/v1/chat/completions";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    private static final String SYSTEM_CONTENT = "你是一个我的世界 Ai 命令助理，帮助用户写命令，你的职责就是根据用户的关键词生成命令，版本是 Java 1.20.4 你只需要生成命令并告诉我，多余的话请不要讲，如果用户输入和命令无关，请用 /say <内容> 命令告诉他，如果你要生成两条及以上的指令，请使用&&分割它们，分割时不要用空格来增加美观。";

    public static String getAiReturn(String content) throws IOException, InterruptedException, ExecutionException {
        String json = String.format("""
                {
                  "max_tokens": 1200,
                  "model": "gpt-3.5-turbo",
                  "temperature": 0.8,
                  "top_p": 1,
                  "presence_penalty": 1,
                  "messages": [
                    {
                      "role": "system",
                      "content": "%s"
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }""", SYSTEM_CONTENT, content);

        RequestBody body = RequestBody.create(json, MEDIA_TYPE);

        CompletableFuture<String> resultFuture = CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer sk-4jPT2mF7DYh4Z5QlvHtr7cFO6JCtDsmenx7jIWUkm8Gj6Jru")
                    .build();

            Call call = CLIENT.newCall(request);
            Response response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                Logger.getLogger("MagicCommand").warning("Error sending message to server: " + e.getMessage());
            }

            if (response != null && response.body() != null) {
                try {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONObject choices = jsonObject.getJSONArray("choices").getJSONObject(0);
                    return choices.getJSONObject("message").getString("content");
                } catch (IOException e) {
                    Logger.getLogger("MagicCommand").warning("Error reading response body: " + e.getMessage());
                }
            }
            return null;
        });

        return resultFuture.get();
    }
}
