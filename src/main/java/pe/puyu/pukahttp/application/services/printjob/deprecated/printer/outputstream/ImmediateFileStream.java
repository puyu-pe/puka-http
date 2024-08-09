package pe.puyu.pukahttp.application.services.printjob.deprecated.printer.outputstream;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.interfaces.Cancelable;

import java.io.*;
import java.util.concurrent.CompletableFuture;

public class ImmediateFileStream extends OutputStream implements Cancelable {
	private final String resourcePath;
	private FileOutputStream outputStream;

	public ImmediateFileStream(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	private CompletableFuture<Void> connectAsync() {
		return CompletableFuture.runAsync(() -> {
			try {
				File smbFile = new File(this.resourcePath);
				this.outputStream = new FileOutputStream(smbFile);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void close() throws IOException {
		this.outputStream.close();
	}

	@Override
	public void write(int i) throws IOException {
		try {
			connectAsync().get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.outputStream.write(i);
	}

	@Override
	public void write(byte @NotNull [] bytes) throws IOException {
		try {
			connectAsync().get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.outputStream.write(bytes);
	}

	@Override
	public void cancel() {
		try {
			this.outputStream.close();
		} catch (IOException ignored) {
		}
	}
}
