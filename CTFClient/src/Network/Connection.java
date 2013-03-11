package Network;

import java.io.*;
import java.net.*;
import java.util.*;

import Core.CTFException;

/**
 * Controls the connection to the server.
 * 
 * @author JP Verkamp
 */
public class Connection implements Runnable
{
	// Set this to display debug information.
	static boolean				DEBUG		= true;

	// Store the protocol and connection information.
	Protocol					Protocol;
	Socket						Server;
	String						IP;
	int							Port;

	// Keep a list of listeners.
	List<ConnectionListener>	Listeners	= new ArrayList<ConnectionListener>();

	// The input and output streams.
	BufferedReader				Input;
	BufferedWriter				Output;

	/**
	 * Create a new connection to the given server.
	 * 
	 * @param ip
	 *            The server's IP address.
	 * @param port
	 *            The port on the server to connect to.
	 * 
	 * @throws IOException
	 *             If there is a problem with system IO.
	 * @throws UnknownHostException
	 *             If the host cannot be found.
	 */
	public Connection(Protocol protocol, String ip, int port) throws UnknownHostException, IOException
	{
		// Store the protocol.
		Protocol = protocol;

		// Create a new socket.
		Server = new Socket(ip, port);

		// Store references to the input and output streams.
		Input = new BufferedReader(new InputStreamReader(Server.getInputStream()));
		Output = new BufferedWriter(new OutputStreamWriter(Server.getOutputStream()));

		// Start the listener thread.
		(new Thread(this)).start();
	}

	/**
	 * Add a new connection listener to the connection.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void AddConnectionListener(ConnectionListener listener)
	{
		Listeners.add(listener);
	}

	/**
	 * Run the listening thread.
	 */
	public void run()
	{
		// The buffer that will read from the network.
		String line;

		// Loop forever! (Or at least until the connection closes).
		while (true)
		{
			// Catch errors with network streams.
			try
			{
				// Only read when a whole packet is available.
				line = Input.readLine();
				if (line != null && line.length() >= 4)
				{
					// Debug messages.
					if (DEBUG)
						System.out.println("Received: " + line);

					// Decode the new line into a packet.
					Packet packet = new Packet(Protocol, line);

					// Tell each associated listener that a new packet has been
					// received.
					for (ConnectionListener listener : Listeners)
						listener.PacketReceived(packet);
				}
			}

			// Problems with the network stream.
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			}

			// Problem with the protocol.
			catch (CTFException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * Send a new packet to the server.
	 * 
	 * @param packet
	 *            The packet to send.
	 * 
	 * @throws IOException
	 *             If there are any problems sending the packet.
	 */
	public void SendPacket(Packet packet) throws IOException
	{
		// Debug message.
		if (DEBUG)
			System.out.print("    Sent: " + packet.Encode());

		// Write the array to the server and ensure that it gets sent.
		Output.write(packet.Encode());
		Output.flush();
	}

}