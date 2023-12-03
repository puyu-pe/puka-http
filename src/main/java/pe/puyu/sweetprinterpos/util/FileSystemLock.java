package pe.puyu.sweetprinterpos.util;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileSystemLock {
	private File file;
	private FileChannel channel;
	private FileLock lock;

	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("FileSystemLock"));

	public FileSystemLock(String fileLockDir) {
		try {
			file = new File(fileLockDir);
			//noinspection resource
			channel = new RandomAccessFile((file), "rw").getChannel();
			lock = channel.tryLock();
			Runtime.getRuntime().addShutdownHook(new Thread(this::unLock));
		} catch (Exception e) {
			logger.error("Exception create lock file: {}", e.getMessage(), e);
		}
	}

	public boolean hasLock() {
		return lock == null;
	}

	public void unLock() {
		try {
			if (!hasLock()) {
				lock.release();
				channel.close();
				var ignored = file.delete();
				logger.info("unlock file: {}", file.getName());
				lock = null;
			}
		} catch (Exception e) {
			logger.error("Exception unlock file: {}", e.getMessage());
		}
	}
}
