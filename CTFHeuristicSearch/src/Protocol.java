import java.io.*;
import java.util.*;

/// <summary>
/// Loads the protocol file.  Can be used to encode and decode packets.
/// </summary>
public class Protocol
{
    // Store the protocol information.
    List<PacketDefinition> ProtocolData;
    Map<String, Integer> Constants;

    /// <summary>
    /// Load a protocol file.
    /// </summary>
    /// <param name="protocolFile">The protocol file.</param>
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

            // If the first character is a tab or a space, add to the previous packet.
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

    /// <summary>
    /// Encode data as a packet.
    /// </summary>
    /// <param name="packet">The packet to send.</param>
    /// <returns>An encoded byte array of requested data.</returns>
    public String Encode(Packet packet)
    {
        return packet.Encode();
    }

    /// <summary>
    /// Decode data into a packet.
    /// </summary>
    /// <param name="data">The encoded data.</param>
    /// <returns>A packet.</returns>
    public Packet Decode(String data) throws CTFException
    {
        return new Packet(this, data);
    }
}

/// <summary>
/// An internal C# representation of a packet.
/// </summary>
class Packet {
    // Packet specification data.
    Protocol Protocol;
    PacketDefinition Definition;
    Map<String, Object> Data = new HashMap<String, Object>();
    
    /// <summary>
    /// Create a new packet of the given type (performs some minor sanity checks).
    /// Timestamp will be set to the current system time.
    /// </summary>
    /// <param name="protocol">The protocol this packet belongs to.</param>
    /// <param name="title">The type of packet.</param>
    /// <param name="data">The packet data.</param>
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

    /// <summary>
    /// Create a new packet directly from a data stream.
    /// </summary>
    /// <param name="protocol">The protocol to use to create the packet.</param>
    /// <param name="data">The data that the packet should / does contain.</param>
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
            if (definition.Title.equalsIgnoreCase((String)Data.get("Type")))
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

    /// <summary>
    /// Encode the data into a byte array.
    /// </summary>
    /// <returns>The encoded data.</returns>
    public String Encode()
    {
        // Use a list to dynamically build the array.
        String data = "";

        // Add the protocol name, version, and the current timestamp.
        data += "CTFS,";
        
        // Start adding the data.
        for(String key : Data.keySet())
            data += key + "=" + Data.get(key) + ",";

        // Return the data.
        return data.substring(0, data.length() - 1) + "\n";
    }
};

// Possible packet datatypes.
enum PacketDatatype
{
    Byte,
    Int,
    Char,
    String,
};

// Packet definitions.
class PacketData
{
    String Title;
    PacketDatatype Datatype;
    public PacketData(String title, PacketDatatype datatype)
    {
        Title = title;
        Datatype = datatype;
    }
}
class PacketDefinition
{
    String Title;
    List<PacketData> Data;
    public PacketDefinition(String title, List<PacketData> data)
    {
        Title = title;
        Data = data;
    }
};
