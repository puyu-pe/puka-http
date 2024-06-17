package pe.puyu.pukahttp.services.trayicon;

import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FileSystemLock;

public class TrayIconServiceProvider {

	private static FileSystemLock lock;
	private static TrayIconService trayIconService;
	private final static String NAME_LOCK_FILE = "lockTrayIconService";

	private TrayIconServiceProvider(){}

	public static TrayIconService instance(){
		if(trayIconService == null){
			trayIconService = new TrayIconService();
		}
		return trayIconService;
	}

	public static boolean isLock() {
		var otherLock = new FileSystemLock(AppUtil.makeLockFile(NAME_LOCK_FILE), false);
		var isLock = otherLock.hasLock();
		if (!isLock) {
			otherLock.unLock();
		}
		return isLock;
	}

	public static void lock() {
		lock = new FileSystemLock(AppUtil.makeLockFile(NAME_LOCK_FILE));
	}
	public static void unLock() {
		if (lock != null) {
			lock.unLock();
		}
	}
}
