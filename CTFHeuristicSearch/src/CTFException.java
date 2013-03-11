/**
 * Simple extension of Extension to handle Capture The Flag exceptions.
 * 
 * @author JP Verkamp
 */
public class CTFException extends Exception {
	private static final long serialVersionUID = 6588551318480584392L;

	/**
	 * Create a new exception.
	 * 
	 * @param message The exception message.
	 */
	public CTFException(String message)
	{
		super(message);
	}
}
