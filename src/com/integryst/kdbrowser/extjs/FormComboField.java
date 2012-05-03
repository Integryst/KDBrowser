// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser.extjs;

public class FormComboField {
    private String label; // shown to user
    private String value; // passed to server
    private boolean isDefault=false;

    public FormComboField() {
    }

    public FormComboField(String label, String value, boolean isDefault) {
        this.label = label;
        this.value = value;
        this.isDefault = isDefault;
    }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }

    public void setValue(String value) { this.value = value; }
    public String getValue() { return value; }

    public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }
    public boolean getIsDefault() { return isDefault; }
}
