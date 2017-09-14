package cz.zajezdy.data.bengine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;

import cz.zajezdy.data.bengine.engine.impl.JsMapWrapper;

public class WrapperTest {

	@Test
	public void testMapUndefined() throws ScriptException, NoSuchMethodException {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		final Compilable compilable = (Compilable) engine;
		final Invocable invocable = (Invocable) engine;

		final String okReturn = "OK: did not contain testKey2";

		final String statement = "function testMap(map) { if (typeof map.testKey2 === 'undefined') { return '" + okReturn
				+ "'; } return 'ERROR: should not happen: ' + map.testKey; };";
		final CompiledScript compiled = compilable.compile(statement);

		compiled.eval();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("testKey", "testVal");

		JsMapWrapper wrapper = new JsMapWrapper(map);

		String ret = (String) invocable.invokeFunction("testMap", wrapper);

		assertEquals(okReturn, ret);
	}

	@Test
	public void testAddToMap() throws ScriptException, NoSuchMethodException {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		final Compilable compilable = (Compilable) engine;
		final Invocable invocable = (Invocable) engine;

		final String statement = "function addToMap(map) { map.testKey2 = 'testVal2'; return map; };";
		final CompiledScript compiled = compilable.compile(statement);

		compiled.eval();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("testKey", "testVal");

		JsMapWrapper wrapper = new JsMapWrapper(map);

		JsMapWrapper ret = (JsMapWrapper) invocable.invokeFunction("addToMap", wrapper);
		Map<String, Object> map2 = ret.getMap();
		assertEquals("testVal2", map2.get("testKey2"));
	}

	@Test
	public void testDeleteFromMap() throws ScriptException, NoSuchMethodException {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		final Compilable compilable = (Compilable) engine;
		final Invocable invocable = (Invocable) engine;

		final String statement = "function deleteFromMap(map) { delete map.testKey2; return map; };";
		final CompiledScript compiled = compilable.compile(statement);

		compiled.eval();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("testKey", "testVal");
		map.put("testKey2", "testVal2");

		JsMapWrapper wrapper = new JsMapWrapper(map);

		assertTrue(map.containsKey("testKey2"));
		invocable.invokeFunction("deleteFromMap", wrapper);
		assertFalse(map.containsKey("testKey2"));
	}
}
