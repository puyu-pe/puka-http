package pe.puyu.sweetprinterpos.util;

import pe.puyu.sweetprinterpos.services.api.FxGsonMapper;
import pe.puyu.sweetprinterpos.services.api.ResponseApi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpUtil {

	public static <DataType> ResponseApi<DataType> get(String endpoint) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(endpoint))
			.GET()
			.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		FxGsonMapper mapper = new FxGsonMapper();
		return mapper.fromJsonString(response.body(), ResponseApi.class);
	}

	//data debe ser un string , si es un objeto json se debe convertir a string antes
	public static <DataType> ResponseApi<DataType> put(String endpoint, String data) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(endpoint))
			.PUT(HttpRequest.BodyPublishers.ofString(data))
			.header("Content-Type", "application/json")
			.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		FxGsonMapper mapper = new FxGsonMapper();
		return mapper.fromJsonString(response.body(), ResponseApi.class);
	}
}
