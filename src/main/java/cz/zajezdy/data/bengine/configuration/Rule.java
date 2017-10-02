package cz.zajezdy.data.bengine.configuration;

import cz.zajezdy.data.bengine.action.Action;

import java.util.List;

public interface Rule {
	String getExpression();
	String getDescription();
	List<String> getScriptActions();

	/**
	 * Execute actions from the list of registered Actions.
	 * Take a look at {@link Action the Action inferace} for usage
	 * information.
	 *
	 * @return List of action executions
	 */
	List<String> getExecutionActions();
	Integer getPriority();
}
