package cz.zajezdy.data.bengine.exception;

/**
 * An InputValidationException occurs, when setting the input of the API.
 * When calling RuleEngine.setInput() the engine validates the input against the
 * specification in the configuration. If an input value does not fullfil the
 * requirements an InputValidationException is thrown.
 * 
 * The exception contains further information of what went wrong.
 * 
 * @author Florian Beese
 *
 */
public class InputValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	public InputValidationException(String msg) {
		super(msg);
	}

}
