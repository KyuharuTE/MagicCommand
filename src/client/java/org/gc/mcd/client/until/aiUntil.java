package org.gc.mcd.client.until;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class aiUntil {
    private static final String URL = "https://api.chatanywhere.tech/v1/chat/completions";
    private static final String SYSTEM_CONTENT = "你是一个我的世界 Ai 命令助理，帮助用户写命令，你的职责就是根据用户的关键词生成命令，版本是 Java 1.20.4 你只需要生成命令并告诉我，多余的话请不要讲，如果用户输入和命令无关，请用 /say <内容> 命令告诉他，如果你要生成两条及以上的指令，请使用&&分割它们，分割时不要用空格来增加美观。";

    public static String getAiReturn(String content) throws IOException, InterruptedException, ExecutionException {

        if (!new File("./config/mcd.txt").isFile()) {
            return "/say 失败";
        }

        String fileContent = new String(java.nio.file.Files.readAllBytes(new File("./config/mcd.txt").toPath()), StandardCharsets.UTF_8);

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

        CompletableFuture<String> resultFuture = CompletableFuture.supplyAsync(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + fileContent);
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }

                        // Parsing the JSON response without using a JSON library
                        String jsonResponse = response.toString();
                        int startIndex = jsonResponse.indexOf("\"content\":\"") + 11;
                        int endIndex = jsonResponse.indexOf("\"", startIndex);
                        return jsonResponse.substring(startIndex, endIndex);
                    }
                } else {
                    Logger.getLogger("MagicCommand")
                            .warning("Error sending message to server: HTTP error code: " + responseCode);
                    return "/say 失败 ResponseCode: " + responseCode;
                }
            } catch (IOException e) {
                Logger.getLogger("MagicCommand").warning("Error sending message to server: " + e.getMessage());
                return "/say 失败";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });

        return resultFuture.get();
    }
}
