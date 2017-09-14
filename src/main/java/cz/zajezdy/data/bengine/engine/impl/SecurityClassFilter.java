package cz.zajezdy.data.bengine.engine.impl;

import jdk.nashorn.api.scripting.ClassFilter;


@SuppressWarnings("restriction")
public class SecurityClassFilter implements ClassFilter {

	@Override
	public boolean exposeToScripts(String arg0) {
		return false;
	}

}
