package cz.zajezdy.data.bengine.engine.scriptbuilder;

import com.google.common.base.Strings;
import cz.zajezdy.data.bengine.action.Action;
import cz.zajezdy.data.bengine.action.ParameterizedAction;
import cz.zajezdy.data.bengine.action.SimpleAction;
import cz.zajezdy.data.bengine.configuration.Rule;
import cz.zajezdy.data.bengine.exception.InvalidConfigurationException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractScriptBuilder implements ScriptBuilder {
    protected static String getPreExecutionScriptPart(final List<String> preExecution) {
        if (preExecution == null || preExecution.size() == 0) return "";
        final StringBuilder script = new StringBuilder();

        for (String action : preExecution) {
            script.append(appendSemicolonIfMissing(action)).append("\n");
        }

        return script.toString();
    }

    protected static String getPostExecutionScriptPart(final List<String> postExecution) {
        if (postExecution == null || postExecution.size() == 0) return "";
        final StringBuilder script = new StringBuilder();

        for (String action : postExecution) {
            script.append(appendSemicolonIfMissing(action)).append("\n");
        }

        return script.toString();
    }

    protected static String getRulesScriptPart(List<? extends Rule> rules, HashMap<String, Action> registeredActions) {
        if (rules == null || rules.size() == 0) return "";
        StringBuilder script = new StringBuilder();

        for (Rule rule : rules) {
           script.append(getRuleScript(rule, registeredActions));
        }

        return script.toString();
    }

    protected static String getRuleScript(Rule r, HashMap<String, Action> registeredActions) {
        if (r == null) return "";
        StringBuilder script = new StringBuilder();

        String expression = r.getExpression();
        if (Strings.isNullOrEmpty(expression)) expression = "true";

        script.append("if (").append(expression).append(") { \n");
        List<String> scriptActions = r.getScriptActions();
        if (scriptActions != null) {
            for (String action : scriptActions) {
                if (StringUtils.isNotBlank(action)) {
                    script.append("  ").append(appendSemicolonIfMissing(action)).append("\n");
                }
            }
        }

        List<String> executionActions = r.getExecutionActions();
        if (executionActions != null) {
            for (String action : executionActions) {
                String callExecAction = getActionScriptCall(r, action, registeredActions) + ";\n";
                script.append(callExecAction);
            }
        }
        script.append("}\n");

        return script.toString();
    }

    protected static String getActionScriptCall(Rule r, String action, HashMap<String, Action> registeredActions) {
        if (action == null) {
            throw new InvalidConfigurationException(
                    "Encountered an empty execution action at rule '" + r.getExpression() + "'. Put a comma behind the last action?");
        }
        String actionKey = action;
        String param = null;
        int pos = actionKey.indexOf('(');
        if (pos != -1) {
            actionKey = actionKey.substring(0, pos);
            int pos1 = action.indexOf("'", pos);
            if (pos1 != -1) {
                pos1++;
                int pos2 = action.indexOf("'", pos1);
                param = action.substring(pos1, pos2);
            }
        }

        if (!registeredActions.containsKey(actionKey)) {
            throw new InvalidConfigurationException("Encountered an unregistered execution action '" + actionKey + "' at rule '" + r.getExpression() + "'");
        }

        String accessScript = "registeredActions.get('" + actionKey + "')";

        Action execAction = registeredActions.get(actionKey);
        if (execAction instanceof ParameterizedAction) {
            if (param == null) {
                throw new InvalidConfigurationException("Encountered an the execution action '" + actionKey + "' at rule '" + r.getExpression()
                        + "', which is of type ParameterizedAction, but no parameter was passed.");
            }
            // TODO: Just strings work right now, fix this?!
            return accessScript + ".execute('" + param + "')";
        }
        else if (execAction instanceof SimpleAction) {
            return accessScript + ".execute()";
        }
        else {
            throw new InvalidConfigurationException("Encountered an unknown type of action...");
        }
    }

    protected static String appendSemicolonIfMissing(String action) {
        String scriptAction = action.trim();
        if (!(scriptAction.endsWith(";") || scriptAction.endsWith("}"))) {
            scriptAction += ";";
        }
        return scriptAction;
    }
}
