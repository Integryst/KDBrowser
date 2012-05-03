package com.integryst.kdbrowser.extjs;

public class GridColumn {
    private String id;
    private String name;
    private String mapping;
    private String fieldType;
    private String renderer;
    private int width;
    private boolean hidden;
    private boolean editable;
    private boolean dateField;
    
    public GridColumn(String id, String name, String mapping, String fieldType, String renderer, int width, boolean hidden, boolean editable, boolean dateField) {
        this.id = id;
        this.name = name;
        this.mapping = mapping;
        this.fieldType = fieldType;
        this.renderer = renderer;
        this.width = width;
        this.hidden = hidden;
        this.editable = editable;
        this.dateField = dateField;
        //{name: 'due', type: 'date', dateFormat:'m/d/Y'}

    }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setMapping(String mapping) { this.mapping = mapping; }
    public String getMapping() { return mapping; }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getFieldType() { return fieldType; }

    public void setRenderer(String renderer) { this.renderer = renderer; }
    public String getRenderer() { return renderer; }

    public void setWidth(int width) { this.width = width; }
    public int getWidth() { return width; }

    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public boolean isHidden() { return hidden; }

    public void setEditable(boolean editable) { this.editable = editable; }
    public boolean isEditable() { return editable; }

    public void setDateField(boolean dateField) {
        this.dateField = dateField;
    }

    public boolean isDateField() {
        return dateField;
    }
}
