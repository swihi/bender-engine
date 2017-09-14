package cz.zajezdy.data.bengine.test.actions;

import cz.zajezdy.data.bengine.action.ParameterizedAction;
import cz.zajezdy.data.bengine.test.RuleEngineTest;


public class LogAction implements ParameterizedAction<String> {

	@Override
	public void execute(String param) {
		// System.out.println("Executing LogAction with param '" + param + "'");
		RuleEngineTest.logActionExecuted = true;
	}

}
