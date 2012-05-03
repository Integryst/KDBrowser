package com.integryst.kdbrowser.extjs;

public class FormButton {
    private String label;
    private String icon;
    private String js;

    public FormButton() {
    }

    public FormButton(String label, String icon, String js) {
        this.label = label;
        this.icon = icon;
        this.js = js;
    }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }

    public void setIcon(String icon) { this.icon = icon; }
    public String getIcon() { return icon; }

    public void setJs(String js) { this.js = js; }
    public String getJs() { return js; }
}
