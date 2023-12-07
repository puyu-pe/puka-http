package pe.puyu.pukahttp.services.api;

import com.google.gson.Gson;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import org.hildan.fxgson.FxGson;

import java.lang.reflect.Type;

public class FxGsonMapper implements JsonMapper {
	Gson gson = FxGson.create();
	@Override
	public @NotNull String toJsonString(@NotNull Object obj, @NotNull Type type) {
		return gson.toJson(obj, type);
	}

	@Override
	public <T> @NotNull T fromJsonString(@NotNull String json, @NotNull Type targetType) {
		return gson.fromJson(json, targetType);
	}
}
