package com.idenchev.io.output.template;

import com.idenchev.io.output.NotifierSingleton;
import com.idenchev.malware.InfectedUser;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class PastebinSiteFileReplacementStrategy implements FileReplacementStrategy {
    private String apiKey;

    PastebinSiteFileReplacementStrategy(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getReplacedFileTemplateString(InfectedUser user) {
        try {
            URL url = new URL("https://api.paste.ee/v1/pastes");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);

            Map<String, String> arguments = new HashMap<>();
            arguments.put("description", "A list of infected files");
            arguments.put("key", apiKey);
            arguments.put("sections[0][name]", "Malware Scan");
            arguments.put("sections[0][contents]", String.join(System.lineSeparator(), user.getFiles()).trim());
            arguments.put("sections[0][syntax]", "text");
            arguments.put("encrypted", "true");

            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
                String responseJSON = reader.readLine();
                NotifierSingleton.INSTANCE.addPasteSiteNormal(user);
                return new Gson().fromJson(responseJSON, JSONResponse.class).link;
            }
        } catch (IOException e) {
            NotifierSingleton.INSTANCE.addPasteSiteError(user);
        }
        return "Cannot fetch infected files. List might be too large. Refer to scan file.";
    }

    static class JSONResponse {
        String id;
        String link;
    }
}
