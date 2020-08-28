package org.xiong.xmock.jacoco;

import static java.lang.String.format;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import org.jacoco.core.tools.ExecDumpClient;
import org.jacoco.core.tools.ExecFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jacoco.exec file client端
 */
public class ExecDump {

	static Logger log = LoggerFactory.getLogger(ExecDump.class);

	/**
	 * Path to the output file for execution data.
	 */
	//@Parameter(property = "jacoco.destFile", defaultValue = "${project.build.directory}/jacoco.exec")
	private File destFile = new File("target/jacoco.exec");

	/**
	 * If set to true and the execution data file already exists, coverage data
	 * is appended to the existing file. If set to false, an existing execution
	 * data file will be replaced.
	 */
	//@Parameter(property = "jacoco.append", defaultValue = "true")
	private boolean append = true;

	/**
	 * Sets whether execution data should be downloaded from the remote host.
	 */
	//@Parameter(property = "jacoco.dump", defaultValue = "true")
	private boolean dump = true;

	/**
	 * Sets whether a reset command should be sent after the execution data has
	 * been dumped.
	 */
	//@Parameter(property = "jacoco.reset", defaultValue = "false")
	private boolean reset = false;

	/**
	 * IP address or hostname to connect to.
	 */
	//@Parameter(property = "jacoco.address")
	private String address = "localhost";

	/**
	 * Port number to connect to. If multiple JaCoCo agents should run on the
	 * same machine, different ports have to be specified for the agents.
	 */
	//@Parameter(property = "jacoco.port", defaultValue = "6300")
	private int port = 6300;

	/**
	 * Number of retries which the goal will attempt to establish a connection.
	 * This can be used to wait until the target JVM is successfully launched.
	 */
	//@Parameter(property = "jacoco.retryCount", defaultValue = "10")
	private int retryCount = 10;

	public void execute() throws Exception {
		final ExecDumpClient client = new ExecDumpClient() {
			@Override
			protected void onConnecting(final InetAddress address,
					final int port) {
				log.info(format("Connecting to %s:%s", address,
						Integer.valueOf(port)));
			}

			@Override
			protected void onConnectionFailure(final IOException exception) {
				log.info(exception.getMessage());
			}
		};
		client.setDump(dump);
		client.setReset(reset);
		client.setRetryCount(retryCount);

		try {
			final ExecFileLoader loader = client.dump(address, port);
			if (dump) {
				log.info(format("Dumping execution data to %s",
						destFile.getAbsolutePath()));
				loader.save(destFile, append);
			}
		} catch (final Exception e) {
			throw new Exception("Unable to dump coverage data", e);
		}
	}

}