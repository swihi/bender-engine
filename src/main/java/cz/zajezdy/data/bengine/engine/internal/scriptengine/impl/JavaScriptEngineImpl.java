package cz.zajezdy.data.bengine.engine.internal.scriptengine.impl;

import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cz.zajezdy.data.bengine.engine.internal.scriptengine.JavaScriptEngine;


public class JavaScriptEngineImpl implements JavaScriptEngine {

	ScriptEngine engine = null;

	public JavaScriptEngineImpl(ScriptEngine engine) {
		this.engine = engine;
	}

	@Override
	public Object eval(String scriptToEval) throws ScriptException {
		return engine.eval(scriptToEval);
	}

	@Override
	public void put(String varName, Map<String, Object> varValue) {
		engine.put(varName, varValue);
	}

	@Override
	public CompiledScript getCompiledScript(String script) throws ScriptException {
		final Compilable compilable = (Compilable) engine;
		CompiledScript compiledScript = compilable.compile(script);
		compiledScript.eval();
		return compiledScript;
	}

	@Override
	public Object invokeFunction(String functionName, Object... arguments) throws NoSuchMethodException, ScriptException {
		final Invocable invocable = (Invocable) engine;
		return invocable.invokeFunction("executeScript", arguments);
	}
}
