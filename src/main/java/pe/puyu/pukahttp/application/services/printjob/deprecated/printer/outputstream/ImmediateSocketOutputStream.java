package pe.puyu.pukahttp.application.services.printjob.deprecated.printer.outputstream;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.interfaces.Cancelable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class ImmediateSocketOutputStream extends OutputStream implements Cancelable {
	private final Socket socket;
	private final InetSocketAddress address;

	public ImmediateSocketOutputStream(String host, int port) {
		address = new InetSocketAddress(host, port);
		socket = new Socket();
	}

	private CompletableFuture<Void> connectAsync() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.socket.connect(address, 15000);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void close() throws IOException {
		this.socket.getOutputStream().close();
		this.socket.close();
	}

	@Override
	public void write(int i) throws IOException {
		try {
			this.connectAsync().get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		socket.getOutputStream().write(i);
	}

	@Override
	public void write( byte @NotNull [] bytes) throws IOException {
		try {
			this.connectAsync().get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		socket.getOutputStream().write(bytes);
	}

	@Override
	public void cancel() {
		try {
			socket.close();
		} catch (IOException ignored) {
		}
	}

}
