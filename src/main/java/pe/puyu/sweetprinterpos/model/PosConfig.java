package pe.puyu.sweetprinterpos.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PosConfig {
	public PosConfig() {
		ip = new SimpleStringProperty();
		port = new SimpleIntegerProperty();
		password = new SimpleStringProperty();
	}

	public String getIp() {
		return ip.get();
	}

	public StringProperty ipProperty() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip.set(ip);
	}

	public int getPort() {
		return port.get();
	}

	public IntegerProperty portProperty() {
		return port;
	}

	public void setPort(int port) {
		this.port.set(port);
	}

	public String getPassword() {
		return password.get();
	}

	public StringProperty passwordProperty() {
		return password;
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	private final StringProperty ip;
	private final IntegerProperty port;
	private final StringProperty password;
}
