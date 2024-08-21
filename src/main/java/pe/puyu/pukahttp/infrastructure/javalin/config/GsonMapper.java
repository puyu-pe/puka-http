package pe.puyu.pukahttp.infrastructure.javalin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;

public class GsonMapper implements JsonMapper {
    private final Gson gson;

    public GsonMapper(){
        this.gson  = new GsonBuilder().create();
    }

    @Override
    public @NotNull String toJsonString(@NotNull Object obj, @NotNull Type type) {
        return gson.toJson(obj, type);
    }

    @Override
    public <T> @NotNull T fromJsonString(@NotNull String json, @NotNull Type targetType) {
        return gson.fromJson(json, targetType);
    }
}
