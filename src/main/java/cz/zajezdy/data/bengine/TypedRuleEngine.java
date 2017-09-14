package cz.zajezdy.data.bengine;

import cz.zajezdy.data.bengine.configuration.Document;


/**
 * The TypedRuleEngine extends the RuleEngine by applying an easy
 * deserialization of the document to a java class instace. The type is
 * specified via the generic param TDoc.
 * 
 * @author Florian Beese
 *
 * @param <TDoc> A java representation of the configurations document. Needed
 * for deserialization.
 */

public interface TypedRuleEngine<TDoc extends Document> extends RuleEngine {

	/**
	 * Returns the deserialized document of the engine.
	 * 
	 * @see RuleEngine#getJsonDocument()
	 * @return TDoc the deserialized json document of the engine.
	 */
	public TDoc getDocument();

}