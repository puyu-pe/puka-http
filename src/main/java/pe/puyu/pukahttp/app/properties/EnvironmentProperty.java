package pe.puyu.pukahttp.app.properties;

import pe.puyu.pukahttp.util.AppUtil;

public class EnvironmentProperty {

	private final String key, value;

	private EnvironmentProperty(String key, String value){
		this.key = key;
		this.value = value;
	}

	public static EnvironmentProperty get() {
		var suffix = AppUtil.isProductionEnvironment() ? "prod" : "beta";
		return new EnvironmentProperty("app.env", suffix);
	}

	public String key(){
		return this.key;
	}

	public String value(){
		return this.value;
	}
}
