package pe.puyu.pukahttp.app.properties;

import pe.puyu.pukahttp.util.AppUtil;

public record EnvironmentProperty(String key, String value) {
	public static EnvironmentProperty get() {
		var suffix = AppUtil.isProductionEnvironment() ? "prod" : "beta";
		return new EnvironmentProperty("app.env", suffix);
	}
}
