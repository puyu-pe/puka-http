package pe.puyu.pukahttp.validations;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class PosConfigValidator {

	public static List<String> validateIp(String ip){
		List<String> errors = new LinkedList<>();
		ip = ip.trim();
		if (ip.isEmpty()) {
			errors.add("La IP no debe ser un campo vacio");
		}
		var ipPattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$", Pattern.MULTILINE);
		var ipMatcher = ipPattern.matcher(ip);
		boolean isValidIp = ipMatcher.matches();
		if (!isValidIp) {
			errors.add("La IP no tiene un valor valido correcto");
		}
		return errors;
	}

	public static List<String> validatePort(String port){
		List<String> errors = new LinkedList<>();
		port = port.trim();
		if (port.isEmpty()) {
			errors.add("El puerto no puede ser un campo vacio");
		}
		var portPattern = Pattern.compile( "^([0-9]|[1-9][0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$", Pattern.MULTILINE);
		var portMatcher = portPattern.matcher(port);
		boolean isValidPort = portMatcher.matches();
		if (!isValidPort) {
			errors.add("El puerto no tiene un valor valido correcto");
		}
		return errors;
	}

	public static List<String> validatePassword(String password){
		List<String> errors = new LinkedList<>();
		password = password.trim();
		if (password.isEmpty()) {
			errors.add("La contraseña no puede ser un campo vacio");
		}
		if (password.length() > 12){
			errors.add("La contraseña no puede tener mas de 12 caracteres");
		}
		return errors;
	}
}
