package cz.zajezdy.data.bengine.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

//@formatter:off
@SuiteClasses({
	ConverterTest.class,
	RuleEngineTest.class,
	ComputerConfigTest.class,
	CacheTest.class,
	SecurityTest.class,
	MultioutputTest.class,
	PerformanceMonitoringTest.class	  //TODO implement assertions for this test
})
//@formatter:on

public class JsreTestsSuite {

}
