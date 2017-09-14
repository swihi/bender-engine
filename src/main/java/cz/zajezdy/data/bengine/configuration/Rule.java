package cz.zajezdy.data.bengine.configuration;

import java.util.List;

public interface Rule {
	public String getExpression();
	public String getDescription();
	public List<String> getScriptActions();
	public List<String> getExecutionActions();
	public Integer getPriority();
}
