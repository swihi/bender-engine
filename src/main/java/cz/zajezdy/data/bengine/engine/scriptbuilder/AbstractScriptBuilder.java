package cz.zajezdy.data.bengine.engine.scriptbuilder;

import com.google.common.base.Strings;
import cz.zajezdy.data.bengine.action.Action;
import cz.zajezdy.data.bengine.action.ParameterizedAction;
import cz.zajezdy.data.bengine.action.SimpleAction;
import cz.zajezdy.data.bengine.configuration.Rule;
import cz.zajezdy.data.bengine.exception.InvalidConfigurationException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    protected static ScriptDescriptorDto getRulesScriptPart(List<? extends Rule> rules, HashMap<String, Action> registeredActions) {
        if (rules == null || rules.size() == 0) return new ScriptDescriptorDto();
        StringBuilder script = new StringBuilder();
        int rowPointer = 2;
        LinkedList<RuleDescriptorDto> ruleDescriptors = new LinkedList<>();

        for (Rule rule : rules) {
            final String ruleScript = getRuleScript(rule, registeredActions);
            if (StringUtils.isEmpty(ruleScript)) continue;

            ruleDescriptors.add(
                    new RuleDescriptorDto(rule.getDescription(), rowPointer, rowPointer + countLines(rule.getExpression()) + 1));
            rowPointer += countLines(ruleScript) - 1;
            script.append(ruleScript);
        }

        return new ScriptDescriptorDto(ruleDescriptors, script.toString());
    }

    protected static String getRuleScript(Rule r, HashMap<String, Action> registeredActions) {
        if (r == null) return "";
        StringBuilder script = new StringBuilder();

        String expression = r.getExpression();
        if (Strings.isNullOrEmpty(expression)) expression = "true";

        script.append("if (\n").append(expression).append("\n) { \n");
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

    protected static int countLines(String input) {
        Matcher m = Pattern.compile("\r\n|\r|\n").matcher(input);
        int lines = 1;
        while (m.find()) { lines ++; }
        return lines;
    }

    /**
     * Go through all rule descriptors and move (means to 'add') all row numbers of rowsCount
     * @param rules A list of rule descriptors to be moved
     * @param rowsCount For how many rows should be each rule descriptior moved
     */
    protected static LinkedList<RuleDescriptorDto> moveRows(LinkedList<RuleDescriptorDto> rules, int rowsCount) {
        for (RuleDescriptorDto rule: rules) {
            rule.setConditionStartAt(rule.getConditionStartAt() + rowsCount - 1);
            rule.setCodeStartAt(rule.getCodeStartAt() + rowsCount - 1);
        }
        return rules;
    }
}
