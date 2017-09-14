package cz.zajezdy.data.bengine.test.configuration.model;

import cz.zajezdy.data.bengine.configuration.Document;


public class TestDocument implements Document {

	private Boolean test;
	private Boolean expensive;
	private String testText;

	public Boolean getTest() {
		return test;
	}

	public void setTest(Boolean test) {
		this.test = test;
	}

	public Boolean getExpensive() {
		return expensive;
	}

	public void setExpensive(Boolean expensive) {
		this.expensive = expensive;
	}

	public String getTestText() {
		return testText;
	}

	public void setTestText(String testText) {
		this.testText = testText;
	}

	@Override
	public String toString() {
		return "TestDocument [test=" + test + ", expensive=" + expensive + ", testText=" + testText + "]";
	}

}
