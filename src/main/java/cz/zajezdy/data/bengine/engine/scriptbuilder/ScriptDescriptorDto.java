package cz.zajezdy.data.bengine.engine.scriptbuilder;

import java.util.LinkedList;

public class ScriptDescriptorDto {
    private LinkedList<RuleDescriptorDto> rules;
    private String script = "";

    public ScriptDescriptorDto() {
        rules = new LinkedList<>();
    }

    public ScriptDescriptorDto(LinkedList<RuleDescriptorDto> rules, String script) {
        this.rules = rules;
        this.script = script;
    }

    public LinkedList<RuleDescriptorDto> getRules() {
        return rules;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
