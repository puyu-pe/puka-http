package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class EthernetOutputStream extends OutputStream {
    private final InetSocketAddress address;
    private final Socket socket;
    private final int timeout;

    public EthernetOutputStream(@NotNull String host) {
        this(host, 9100);
    }

    public EthernetOutputStream(@NotNull String host, int port) {
        this(host, port, 15000);
    }

    public EthernetOutputStream(@NotNull String host, int port, int timeout) {
        address = new InetSocketAddress(host, port);
        socket = new Socket();
        this.timeout = timeout;
    }

    @Override
    public void write(int i) throws IOException {
        connect();
        if (!this.socket.isClosed()) {
            this.socket.getOutputStream().write(i);
        }
    }

    @Override
    public void write(byte @NotNull [] b) throws IOException {
        connect();
        if (!this.socket.isClosed()) {
            this.socket.getOutputStream().write(b);
        }
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }

    private void connect() throws IOException {
        if (!this.socket.isConnected() || !this.socket.isBound()) {
            this.socket.connect(address, timeout);
        }
    }
}
