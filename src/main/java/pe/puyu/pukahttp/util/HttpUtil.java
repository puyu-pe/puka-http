package pe.puyu.pukahttp.util;

import pe.puyu.pukahttp.services.api.FxGsonMapper;
import pe.puyu.pukahttp.services.api.ResponseApi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpUtil {

	public static <DataType> ResponseApi<DataType> get(String endpoint) throws Exception {
		HttpClient client = buildClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(endpoint))
			.GET()
			.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		FxGsonMapper mapper = new FxGsonMapper();
		return mapper.fromJsonString(response.body(), ResponseApi.class);
	}

	public static String getString(String endpoint) throws Exception {
		HttpClient client = buildClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(endpoint))
			.GET()
			.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	//data debe ser un string , si es un objeto json se debe convertir a string antes
	public static <DataType> ResponseApi<DataType> put(String endpoint, String data) throws Exception {
		HttpClient client = buildClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(endpoint))
			.PUT(HttpRequest.BodyPublishers.ofString(data))
			.header("Content-Type", "application/json")
			.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		FxGsonMapper mapper = new FxGsonMapper();
		return mapper.fromJsonString(response.body(), ResponseApi.class);
	}

	public static <DataType> ResponseApi<DataType> delete(String endpoint) throws Exception {
		HttpClient client = buildClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(endpoint))
			.DELETE()
			.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		FxGsonMapper mapper = new FxGsonMapper();
		return mapper.fromJsonString(response.body(), ResponseApi.class);
	}

	private static HttpClient buildClient() {
		final int MAX_TIMEOUT_CONNECTION_SECONDS = 3;
		return HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(MAX_TIMEOUT_CONNECTION_SECONDS))
			.build();
	}
}
