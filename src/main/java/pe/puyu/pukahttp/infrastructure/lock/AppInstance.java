package pe.puyu.pukahttp.infrastructure.lock;

import pe.puyu.pukahttp.infrastructure.loggin.AppLog;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Path;

public class AppInstance {
    private static FileLock lock;
    private static final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");

    public static void requestLock() {
        if (!isMac) { // mac have native lock
            try {
                String tempDir = System.getProperty("java.io.tmpdir");
                File file = new File(Path.of(tempDir, "pukahttp.lock").toString());
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                lock = randomAccessFile.getChannel().tryLock();
                if (lock == null) {
                    randomAccessFile.close();
                }
                Runtime.getRuntime().addShutdownHook(new Thread(AppInstance::unlock));
            } catch (Exception e) {
                AppLog log = new AppLog(AppInstance.class);
                log.getLogger().error(e.getMessage());
                log.getLogger().trace("", e);
            }
        }
    }

    public static boolean gotLock() {
        if (isMac)
            return true;
        return lock != null;
    }

    public static void unlock() {
        if (isMac)
            return;
        if (gotLock()) {
            try {
                lock.release();
                lock = null;
            } catch (Exception e) {
                AppLog log = new AppLog(AppInstance.class);
                log.getLogger().error(e.getMessage());
                log.getLogger().trace("", e);
            }
        }
    }

}
