// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser.helpers;

public class Property {
    private int id;
    private String name;
    private boolean hideColumn;
    private boolean editable;
    private int columnWidth;
    private String columnName;
    private String renderer;
    private boolean DateField;

    public Property(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHideColumn(boolean hideColumn) {
        this.hideColumn = hideColumn;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public boolean isHideColumn() {
        return hideColumn;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setDateField(boolean DateField) {
        this.DateField = DateField;
    }

    public boolean isDateField() {
        return DateField;
    }
}
