package cz.zajezdy.data.bengine.engine.internal.scriptengine;

import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptException;


public interface JavaScriptEngine {

	public Object eval(String scriptToEval) throws ScriptException;

	public void put(String varName, Map<String, Object> varValue);

	public CompiledScript getCompiledScript(String script) throws ScriptException;

	public Object invokeFunction(String functionName, Object... arguments) throws NoSuchMethodException, ScriptException;
}
