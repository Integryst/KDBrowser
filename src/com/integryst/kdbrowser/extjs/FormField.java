package com.integryst.kdbrowser.extjs;

import java.util.ArrayList;

public class FormField {
    private String name;
    private String label;
    private String value;
    private String fieldType;
    private ArrayList comboItems;
    private int minvalue=0;
    private int maxvalue=0;
    private int maxlength=0;
    private int minlength=0;
    private int size=20;
    private boolean isPassword = false;
    private boolean isEditable = true;
    private boolean allowBlank = false;
    
    // type constants
    public static final String TYPE_TEXT="Ext.form.TextField";
    public static final String TYPE_CHECKBOX="Ext.form.Checkbox";
    public static final String TYPE_NUMBER="Ext.form.NumberField";
    public static final String TYPE_HIDDEN="Ext.form.Hidden";
    public static final String TYPE_COMBO="Ext.form.ComboBox";
    
    public FormField() {
    }
    
    public FormField(String name, String label, String value, String fieldType, int minvalue, int maxvalue) {
        setName(name);
        setLabel(label);
        setValue(value);
        setFieldType(fieldType);
        setMinvalue(minvalue);
        setMaxvalue(maxvalue);
    }
    
    public FormField(String name, String label, String value, String fieldType) {
        setName(name);
        setLabel(label);
        setValue(value);
        setFieldType(fieldType);
    }
    
    public FormField(String name, String label, boolean value, String fieldType) {
        setName(name);
        setLabel(label);
        setValue("" + value);
        setFieldType(fieldType);
    }
    
    public FormField(String name, String label, int value, String fieldType) {
        setName(name);
        setLabel(label);
        setValue("" + value);
        setFieldType(fieldType);
    }
    
    public FormField(String name, String label, long value, String fieldType) {
        setName(name);
        setLabel(label);
        setValue("" + value);
        setFieldType(fieldType);
    }

    public void setName(String name) { this.name = name; }
    public String getName() { 
        if (name==null) 
            return "";
        else 
            return name; 
    }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { 
        if (label==null) 
            return "";
        else 
            return label; 
    }

    public void setValue(String value) { this.value = value; }
    public String getValue() { 
        if (value==null) 
            return "";
        else 
            return value.replace("\\","\\\\").replace("'","\\'"); 
    }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getFieldType() { 
        if (fieldType==null) 
            return "";
        else 
            return fieldType; 
    }

    public void setMinvalue(int minvalue) { this.minvalue = minvalue; }
    public int getMinvalue() { return minvalue; }

    public void setMaxvalue(int maxvalue) { this.maxvalue = maxvalue; }
    public int getMaxvalue() { return maxvalue; }

    public void setMaxlength(int maxlength) { this.maxlength = maxlength; }
    public int getMaxlength() { return maxlength; }

    public void setMinlength(int minlength) { this.minlength = minlength; }
    public int getMinlength() { return minlength; }

    public void setSize(int size) { this.size = size; }
    public int getSize() { return size; }

    public void setIsPassword(boolean isPassword) { this.isPassword = isPassword; }
    public boolean getIsPassword() { return isPassword; }

    public void setIsEditable(boolean isEditable) { this.isEditable = isEditable; }
    public boolean getIsEditable() { return isEditable; }

    public void setAllowBlank(boolean allowBlank) { this.allowBlank = allowBlank; }
    public boolean getAllowBlank() { return allowBlank; }
    
    public void addComboField(String label, String value, boolean isDefault) {
        FormComboField newField = new FormComboField(label, value, isDefault);
        addComboField(newField);
    }
    
    public void addComboField(FormComboField newField) {
        if (comboItems == null)
            comboItems = new ArrayList();
        
        comboItems.add(newField);
    }

    public ArrayList<FormComboField> getComboFields() {
        return comboItems;
    }
}
