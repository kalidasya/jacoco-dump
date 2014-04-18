package org.kalidasya.jacoco;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

public class DumpJacoco {

	private static final String DESTFILE = "jacoco.exec";

	private static final String ADDRESS = "localhost";

	private static final int PORT = 6300;

	/**
	 * Starts the execution data request.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		String ips;
		// com.google.common.base.Preconditions
		if (args.length > 0) {
			ips = args[0];
		} else {
			ips = ADDRESS;
		}

		boolean flush = (args.length > 1) ? Boolean.parseBoolean(args[0])
				: true;

		for (String host : ips.split(",")) {
			String[] target = host.split(":");
			String domain = target[0];
			int port = (target.length > 1) ? Integer.valueOf(target[1]) : PORT;

			final FileOutputStream localFile = new FileOutputStream(DESTFILE);
			final ExecutionDataWriter localWriter = new ExecutionDataWriter(
					localFile);

			// Open a socket to the coverage agent:
			final Socket socket = new Socket(InetAddress.getByName(domain),
					port);
			final RemoteControlWriter writer = new RemoteControlWriter(
					socket.getOutputStream());
			final RemoteControlReader reader = new RemoteControlReader(
					socket.getInputStream());
			reader.setSessionInfoVisitor(localWriter);
			reader.setExecutionDataVisitor(localWriter);

			// Send a dump command and read the response:
			writer.visitDumpCommand(true, flush);
			reader.read();

			socket.close();
			localFile.close();
		}

	}

	private DumpJacoco() {
	}

}
