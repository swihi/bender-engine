package cz.zajezdy.data.bengine.exception;

/**
 * An InvalidConfiguationException can occur any time, when using the rule
 * engine.
 * This is a runtime exception and it is recommend, that you use the rule engine
 * only in a try-catch-block.
 * 
 * The exception occurs, if the engine encounters problems using your provided
 * configuration. This happens, if the JSON of the configuration is invalid or
 * does not satisfy the specification. But it can also happen later at runtime,
 * when the engine tries to execute javascript expressions or load the document
 * and encounters a script exception doing this.
 * 
 * With a correct configuration file, this exception will never occur.
 * 
 * @author Florian Beese
 */

public class InvalidConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidConfigurationException() {
		super();
	}

	public InvalidConfigurationException(String msg) {
		super(msg);
	}

	public InvalidConfigurationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public InvalidConfigurationException(Throwable throwable) {
		super(throwable);
	}
}
