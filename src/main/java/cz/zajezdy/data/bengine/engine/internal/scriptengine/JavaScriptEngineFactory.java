package cz.zajezdy.data.bengine.engine.internal.scriptengine;

import cz.zajezdy.data.bengine.monitoring.PerformanceMarkerMgr;


public interface JavaScriptEngineFactory {

	public void enableStandardSecurity(boolean enableSecurity);

	public JavaScriptEngine getEngine();

	public void registerPerformanceMarkerMgr(PerformanceMarkerMgr mgr);
}
