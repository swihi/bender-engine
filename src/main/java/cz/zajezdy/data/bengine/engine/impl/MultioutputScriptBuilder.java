package cz.zajezdy.data.bengine.engine.impl;

import cz.zajezdy.data.bengine.action.Action;
import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Rule;

import java.util.HashMap;
import java.util.List;

public class MultioutputScriptBuilder extends AbstractScriptBuilder {
    private static String CLONE_FUNCTION =
            "var clone = function(obj, depth) {\n" +
            "  if (typeof(depth) === 'undefined') depth = 0;" +     // depth is here to prevent circular references issue
            "  if (obj === null || typeof(obj) !== 'object' || depth > 5) return obj;\n" +
            "\n" +
            "  if (obj instanceof Date)\n" +
            "    var temp = new Date(obj);\n" +
            "  else\n" +
            "    var temp = obj.constructor();\n" +
            "\n" +
            "  for (var key in obj) {\n" +
            "    if (Object.prototype.hasOwnProperty.call(obj, key)) {\n" +
            "      temp[key] = clone(obj[key], depth++);\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  return temp;\n" +
            "}\n\n";

    private static String ADD_TO_OUTPUT_FUNCTION =
            "var addToOutput = function() { output[output.length] = clone(document); }";

    public static String getScript(Configuration configuration, HashMap<String, Action> registeredActions) {
        StringBuilder script = new StringBuilder();

        script.append(CLONE_FUNCTION);
        script.append(ADD_TO_OUTPUT_FUNCTION);

        script.append("" +
                "var executeScript = function(inputJson, registeredActions) { \n" +
                "var input; try { input = JSON.parse(inputJson); } catch (e) { return inputJson; }\n" +
                "var output = [];");

        script.append("var document = ").append(configuration.getDocument()).append(";\n");

        @SuppressWarnings("unchecked")
        List<String> preExecution = configuration.getPreExecution();
        script.append(getPreExecutionScriptPart(preExecution));

        @SuppressWarnings("unchecked")
        List<? extends Rule> rules = configuration.getRules();
        script.append(getRulesScriptPart(rules, registeredActions));

        @SuppressWarnings("unchecked")
        List<String> postExecution = configuration.getPostExecution();
        script.append(getPostExecutionScriptPart(postExecution));

        script.append("if (output.length === 0) addToOutput();");
        script.append("return JSON.stringify(output);\n}\n");
        return script.toString();
    }
}
