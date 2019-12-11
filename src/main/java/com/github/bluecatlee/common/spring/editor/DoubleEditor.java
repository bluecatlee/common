package com.github.bluecatlee.common.spring.editor;

import java.beans.PropertyEditorSupport;

/**
 * 自定义Double编辑器
 */
public class DoubleEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || text.equals("")) {
            text = "0";
        }
        setValue(Double.parseDouble(text));  // 将null和空串转成0
    }

    @Override
    public String getAsText() {
        return getValue().toString();
    }

}
