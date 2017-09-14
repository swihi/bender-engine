package cz.zajezdy.data.bengine.engine.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.internal.runtime.ScriptRuntime;

@SuppressWarnings("restriction")
public class JsMapWrapper extends AbstractJSObject {

	Map<String, Object> wrappedMap = null;

	public JsMapWrapper(Map<String, Object> mapToWrap) {
		wrappedMap = mapToWrap;
	}

	public Map<String, Object> getMap() {
		return wrappedMap;
	}

	@Override
	public Object call(Object thiz, Object... args) {
		return super.call(thiz, args);
	}

	@Override
	public Object newObject(Object... args) {
		return super.newObject(args);
	}

	@Override
	public Object eval(String s) {
		return super.eval(s);
	}

	@Override
	public Object getMember(String name) {
		if (!wrappedMap.containsKey(name)) {
			return ScriptRuntime.UNDEFINED;
		}
		return wrappedMap.get(name);
	}

	@Override
	public Object getSlot(int index) {
		return null;
	}

	@Override
	public boolean hasMember(String name) {
		return super.hasMember(name);
	}

	@Override
	public boolean hasSlot(int slot) {
		return super.hasSlot(slot);
	}

	@Override
	public void removeMember(String name) {
		wrappedMap.remove(name);
	}

	@Override
	public void setMember(String name, Object value) {
		wrappedMap.put(name, value);
	}

	@Override
	public void setSlot(int index, Object value) {
		super.setSlot(index, value);
	}

	@Override
	public Set<String> keySet() {
		return wrappedMap.keySet();
	}

	@Override
	public Collection<Object> values() {
		return wrappedMap.values();
	}

	@Override
	public boolean isInstance(Object instance) {
		return super.isInstance(instance);
	}

	@Override
	public boolean isInstanceOf(Object clazz) {
		return super.isInstanceOf(clazz);
	}

	@Override
	public String getClassName() {
		return super.getClassName();
	}

	@Override
	public boolean isFunction() {
		return false;
	}

	@Override
	public boolean isStrictFunction() {
		return false;
	}

	@Override
	public double toNumber() {
		return super.toNumber();
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public String toString() {
		String s = "Object(Map) : {";
		boolean isFirst = true;
		for (Map.Entry<String, Object> entry : wrappedMap.entrySet()) {
			if (!isFirst) {
				s += ", ";
				isFirst = false;
			}
			s += entry.getKey() + ": " + entry.getValue().toString();
		}
		s += "}";
		return s;
	}
}
