package pe.puyu.pukahttp.services.trayicon;

public class TrayIconServiceProvider {

	private static TrayIconService trayIconService;

	public static TrayIconService get(){
		if(trayIconService == null){
			trayIconService = new TrayIconService();
		}
		return trayIconService;
	}
}
