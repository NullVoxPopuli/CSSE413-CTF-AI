package Network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Core.CTFException;

// / <summary>
// / An internal C# representation of a packet.
// / </summary>
public class Packet
{
	// Packet specification data.
	Protocol					Protocol;
	PacketDefinition			Definition;
	public Map<String, Object>	Data	= new HashMap<String, Object>();

	// / <summary>
	// / Create a new packet of the given type (performs some minor sanity
	// checks).
	// / Timestamp will be set to the current system time.
	// / </summary>
	// / <param name="protocol">The protocol this packet belongs to.</param>
	// / <param name="title">The type of packet.</param>
	// / <param name="data">The packet data.</param>
	public Packet(Protocol protocol, String title, Object[] data) throws CTFException
	{
		// Setup the protocol.
		Protocol = protocol;

		// Get the correct packet definition.
		for (PacketDefinition definition : protocol.ProtocolData)
			if (definition.Title.equalsIgnoreCase(title))
				Definition = definition;
		if (Definition == null)
			throw new CTFException("Unknown packet type: " + title);

		// Set the timestamp.
		Data.put("Type", title);
		Data.put("Version", Protocol.Constants.get("VERSION"));
		Data.put("Timestamp", new Date().getTime());

		// Not enough or too much data.
		if (Definition.Data.size() != data.length)
			throw new CTFException("Inproper amount of data : packet.");

		// Store the data : the dictionary.
		for (int i = 0; i < Definition.Data.size(); i++)
			Data.put(Definition.Data.get(i).Title, data[i]);
	}

	// / <summary>
	// / Create a new packet directly from a data stream.
	// / </summary>
	// / <param name="protocol">The protocol to use to create the
	// packet.</param>
	// / <param name="data">The data that the packet should / does
	// contain.</param>
	public Packet(Protocol protocol, String data) throws CTFException
	{
		// Setup the protocol.
		Protocol = protocol;

		// Check that the (required) first section exists.
		if (!data.startsWith("CTFS"))
			throw new CTFException("Invalid packet, see protocol format.");
		data = data.substring(5);

		// Process the data.
		for (String pair : data.split(","))
		{
			String[] parts = pair.split("=");
			if (parts.length != 2)
				throw new CTFException("Invalid packet.  Not a valid pair: '" + pair + "'");
			Data.put(parts[0].trim(), parts[1].trim());
		}

		// Get the correct protocol data.
		Protocol = protocol;
		Definition = null;
		for (PacketDefinition definition : protocol.ProtocolData)
			if (definition.Title.equalsIgnoreCase((String) Data.get("Type")))
				Definition = definition;
		if (Definition == null)
			throw new CTFException("Unknown packet type: " + Data.get("Types"));

		// Convert objects to the correct types.
		for (PacketData blog : Definition.Data)
			if (Data.containsKey(blog.Title))
				if (blog.Datatype == PacketDatatype.Byte)
					Data.put(blog.Title, Byte.parseByte((String) Data.get(blog.Title)));
				else if (blog.Datatype == PacketDatatype.Char)
					Data.put(blog.Title, (Character) Data.get(blog.Title));
				else if (blog.Datatype == PacketDatatype.Int)
					Data.put(blog.Title, Integer.parseInt((String) Data.get(blog.Title)));
	}

	// / <summary>
	// / Encode the data into a byte array.
	// / </summary>
	// / <returns>The encoded data.</returns>
	public String Encode()
	{
		// Use a list to dynamically build the array.
		String data = "";

		// Add the protocol name, version, and the current timestamp.
		data += "CTFS,";

		// Start adding the data.
		for (String key : Data.keySet())
			data += key + "=" + Data.get(key) + ",";

		// Return the data.
		return data.substring(0, data.length() - 1) + "\n";
	}
};