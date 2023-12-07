package pe.puyu.pukahttp.util;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hildan.fxgson.FxGson;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class JsonUtil {
  private static final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("util"));

  public static <T> void saveJson(String jsonFileDir, T beanSource) {
    Gson gson = FxGson.create();
    try (FileWriter writer = new FileWriter(jsonFileDir)) {
      String jsonSource = gson.toJson(beanSource);
      writer.write(jsonSource);
    } catch (Exception e) {
      logger.error("Exception on save json: {}", e.getMessage(), e);
    }
  }

  public static <T> Optional<T> convertFromJson(String jsonFileDir, Class<T> objectClass) {
    Gson gson = FxGson.create();
    try {
      String jsonFromFile = new String(Files.readAllBytes(Paths.get(jsonFileDir)));
      return Optional.ofNullable(gson.fromJson(jsonFromFile, objectClass));
    } catch (Exception e) {
      logger.error("Exception on convert json to object: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  public static <T> JsonObject toJSONObject(T beanSource) {
    Gson gson = FxGson.create();
    return JsonParser.parseString(gson.toJson(beanSource)).getAsJsonObject();
  }

  public static JsonObject getJsonFrom(URL jsonURL) throws Exception {
    InputStream is = jsonURL.openStream();
    BufferedReader buf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    String line = buf.readLine();
    StringBuilder sb = new StringBuilder();
    while(line != null){
        sb.append(line).append("\n");
        line = buf.readLine();
    }
    String json = sb.toString();
    return JsonParser.parseString(json).getAsJsonObject();
  }
}
