package Network;

import java.io.*;
import java.util.*;

import Core.CTFException;

// / <summary>
// / Loads the protocol file. Can be used to encode and decode packets.
// / </summary>
public class Protocol
{
	// Store the protocol information.
	List<PacketDefinition>	ProtocolData;
	Map<String, Integer>	Constants;

	// / <summary>
	// / Load a protocol file.
	// / </summary>
	// / <param name="protocolFile">The protocol file.</param>
	public Protocol(String protocolFile) throws CTFException, NumberFormatException, IOException
	{
		// Load the data file.
		BufferedReader input = new BufferedReader(new FileReader(protocolFile));

		// Read the protocol data.
		ProtocolData = new ArrayList<PacketDefinition>();
		Constants = new HashMap<String, Integer>();
		String line;
		String currentPacketTitle = null;
		List<PacketData> currentPacketData = null;
		while ((line = input.readLine()) != null)
		{
			// Remove comments.
			line = line.replaceAll("#[^\n]*$", "");

			// Ignore empty lines.
			if (line.length() == 0 || line.startsWith("#"))
			{
				continue;
			}

			// Constants start with 'CONST'
			else if (line.startsWith("CONST"))
			{
				line = line.trim().replaceAll("CONST", "").trim();
				String[] parts = line.split(":");
				if (parts.length != 2)
					throw new CTFException("Invalid protocol format on line: " + line);
				parts[0] = parts[0].trim();
				parts[1] = parts[1].trim();
				Constants.put(parts[0], Integer.parseInt(parts[1]));
			}

			// If the first character is a tab or a space, add to the previous
			// packet.
			else if (line.startsWith("\t") || line.startsWith(" "))
			{
				// Remove excess spaces.
				line = line.trim();

				// If nothing else is left, just ignore the line.
				if (line.length() == 0)
					continue;

				// Split into two parts.
				String[] parts = line.split(":");
				if (parts.length != 2)
					throw new CTFException("Invalid protocol format on line: " + line);
				parts[0] = parts[0].trim();
				parts[1] = parts[1].trim();

				// Add the data (just datatype).
				if (parts[1].equalsIgnoreCase("byte"))
					currentPacketData.add(new PacketData(parts[0], PacketDatatype.Byte));
				else if (parts[1].equalsIgnoreCase("int"))
					currentPacketData.add(new PacketData(parts[0], PacketDatatype.Int));
				else if (parts[1].equalsIgnoreCase("char"))
					currentPacketData.add(new PacketData(parts[0], PacketDatatype.Char));
				else if (parts[1].equalsIgnoreCase("string"))
					currentPacketData.add(new PacketData(parts[0], PacketDatatype.String));

				// Add the data (constant values).
				else
					throw new CTFException("Invalid protocol format (unknown datatype) on line: " + line);
			}

			// Otherwise, start a new packet.
			else
			{
				// Clear spaces.
				line = line.trim();
				if (line.length() == 0)
					continue;

				// If we already have one, add it to the list.
				if (currentPacketTitle != null && currentPacketData != null)
					ProtocolData.add(new PacketDefinition(currentPacketTitle, currentPacketData));

				// Start collecting data for the new packet.
				currentPacketTitle = line;
				currentPacketData = new ArrayList<PacketData>();
			}
		}

		// Add the last packet type.
		if (currentPacketTitle != null && currentPacketData != null)
			ProtocolData.add(new PacketDefinition(currentPacketTitle, currentPacketData));
	}

	// / <summary>
	// / Encode data as a packet.
	// / </summary>
	// / <param name="packet">The packet to send.</param>
	// / <returns>An encoded byte array of requested data.</returns>
	public String Encode(Packet packet)
	{
		return packet.Encode();
	}

	// / <summary>
	// / Decode data into a packet.
	// / </summary>
	// / <param name="data">The encoded data.</param>
	// / <returns>A packet.</returns>
	public Packet Decode(String data) throws CTFException
	{
		return new Packet(this, data);
	}
}

// Possible packet datatypes.
enum PacketDatatype
{
	Byte, Int, Char, String,
};

// Packet definitions.
class PacketData
{
	String			Title;
	PacketDatatype	Datatype;

	public PacketData(String title, PacketDatatype datatype)
	{
		Title = title;
		Datatype = datatype;
	}
}

class PacketDefinition
{
	String				Title;
	List<PacketData>	Data;

	public PacketDefinition(String title, List<PacketData> data)
	{
		Title = title;
		Data = data;
	}
};
