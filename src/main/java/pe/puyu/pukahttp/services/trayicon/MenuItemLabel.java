package pe.puyu.pukahttp.services.trayicon;

public enum MenuItemLabel {
	ABOUT("Acerca de..."),
	ENABLE_NOTIFICATIONS("Activar notificaciones"),
	SHOW_INIT_WINDOW("Abrir panel de acciones"),
	CLOSE("Salir");
	private final String value;

	MenuItemLabel(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static MenuItemLabel fromValue(String value) {
		for (MenuItemLabel type : MenuItemLabel.values()) {
			if (type.value.equals(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException(String.format("Bad option, unknown: %s", value));
	}
}
