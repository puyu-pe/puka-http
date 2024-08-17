package pe.puyu.pukahttp.util;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import pe.puyu.pukahttp.Constants;

import java.io.File;
import java.nio.file.Path;

public class AppUtil {

	public static String getUserDataDir() {
		AppDirs appDirs = AppDirsFactory.getInstance();
		//nota: appVersion = null por que no es necesario la version
		String userDataDir = appDirs.getUserDataDir(Constants.APP_NAME, null, "puyu");
		File file = new File(userDataDir);
		if (!file.exists()) {
			var ignored = file.mkdirs();
		}
		return userDataDir;
	}

	public static String makeNamespaceLogs(String namespace) {
		return Constants.PACKAGE_BASE_PATH + "." + namespace.toLowerCase();
	}

}
