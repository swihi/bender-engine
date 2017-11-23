package cz.zajezdy.data.bengine.engine.scriptbuilder;

import cz.zajezdy.data.bengine.action.Action;
import cz.zajezdy.data.bengine.configuration.Configuration;

import java.util.HashMap;

public interface ScriptBuilder {

    /**
     * Create JS script which can be executed via {@link cz.zajezdy.data.bengine.engine.internal.scriptengine.JavaScriptEngine JavascriptEngine interface}
     *
     * There are 2 implementations of this interface:
     * BasicScriptBuilder - for rules processing one input document another ONE output document
     * MultioutputScriptBuilder - for rules which can generate multiple output documents based on one input document
     *
     * @param configuration Contains definition of PreExecution action, PostExecution actions and Rules
     * @param registeredActions A list of registered actions (methods implemented in Java code) which can be referenced (called) from rules
     * @return
     */
    static ScriptDescriptorDto getScript(Configuration configuration, HashMap<String, Action> registeredActions) {
        return new ScriptDescriptorDto();
    }

}
