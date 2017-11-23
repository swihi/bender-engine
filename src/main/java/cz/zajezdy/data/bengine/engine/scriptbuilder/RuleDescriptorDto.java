package cz.zajezdy.data.bengine.engine.scriptbuilder;

public class RuleDescriptorDto {
    private String scriptName;
    private int conditionStartAt;
    private int codeStartAt;

    public RuleDescriptorDto(String scriptName, int conditionStartAt, int codeStartAt) {
        this.scriptName = scriptName;
        this.conditionStartAt = conditionStartAt;
        this.codeStartAt = codeStartAt;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public int getConditionStartAt() {
        return conditionStartAt;
    }

    public void setConditionStartAt(int conditionStartAt) {
        this.conditionStartAt = conditionStartAt;
    }

    public int getCodeStartAt() {
        return codeStartAt;
    }

    public void setCodeStartAt(int codeStartAt) {
        this.codeStartAt = codeStartAt;
    }
}
