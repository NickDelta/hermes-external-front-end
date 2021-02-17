package org.hua.hermes.frontend.component;

import com.vaadin.flow.component.textfield.TextArea;

public class TrimmedTextArea extends TextArea
{
    public TrimmedTextArea()
    {
    }

    public TrimmedTextArea(String label)
    {
        super(label);
    }

    public TrimmedTextArea(String label, String placeholder)
    {
        super(label, placeholder);
    }

    public TrimmedTextArea(String label, String initialValue, String placeholder)
    {
        super(label, initialValue, placeholder);
    }

    public TrimmedTextArea(ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener)
    {
        super(listener);
    }

    public TrimmedTextArea(String label, ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener)
    {
        super(label, listener);
    }

    public TrimmedTextArea(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<TextArea, String>> listener)
    {
        super(label, initialValue, listener);
    }

    @Override
    public String getValue()
    {
        return super.getValue().trim();
    }
}
