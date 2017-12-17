package hu.webarticum.j2php.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HierarchicalStringBuilder implements Appendable {
    
    private final List<Entry> entries = new ArrayList<Entry>();
    
    private StringBuilder currentStringBuilder = new StringBuilder();

    public void insertSubContent(Object subContent) {
        Entry newEntry = new Entry(currentStringBuilder, subContent);
        entries.add(newEntry);
        currentStringBuilder = new StringBuilder();
    }

    public Appendable append(boolean b) {
        currentStringBuilder.append(b);
        return this;
    }

    public Appendable append(int i) {
        currentStringBuilder.append(i);
        return this;
    }

    public Appendable append(long l) {
        currentStringBuilder.append(l);
        return this;
    }

    public Appendable append(float f) {
        currentStringBuilder.append(f);
        return this;
    }

    public Appendable append(double d) {
        currentStringBuilder.append(d);
        return this;
    }

    @Override
    public Appendable append(char character) {
        currentStringBuilder.append(character);
        return this;
    }

    public Appendable append(char[] characters) {
        currentStringBuilder.append(characters);
        return this;
    }

    public Appendable append(String string) {
        currentStringBuilder.append(string);
        return this;
    }

    @Override
    public Appendable append(CharSequence charSequence) {
        currentStringBuilder.append(charSequence);
        return this;
    }

    public Appendable append(Object object) {
        currentStringBuilder.append(object);
        return this;
    }

    @Override
    public Appendable append(CharSequence charSequence, int start, int end) {
        currentStringBuilder.append(charSequence, start, end);
        return this;
    }

    public void appendTo(Appendable appendable) throws IOException {
        for (Entry entry: entries) {
            appendable.append(entry.stringBuilder.toString());
            if (entry.subContent instanceof HierarchicalStringBuilder) {
                ((HierarchicalStringBuilder)entry.subContent).appendTo(appendable);
            } else {
                appendable.append(String.valueOf(entry.subContent));
            }
        }
        appendable.append(currentStringBuilder.toString());
    }

    public void appendTo(StringBuilder stringBuilder) {
        for (Entry entry: entries) {
            stringBuilder.append(entry.stringBuilder.toString());
            if (entry.subContent instanceof HierarchicalStringBuilder) {
                ((HierarchicalStringBuilder)entry.subContent).appendTo(stringBuilder);
            } else {
                stringBuilder.append(entry.subContent);
            }
        }
        stringBuilder.append(currentStringBuilder.toString());
    }

    public void appendTo(HierarchicalStringBuilder hierarchicalStringBuilder) {
        for (Entry entry: entries) {
            hierarchicalStringBuilder.append(entry.stringBuilder.toString());
            if (entry.subContent instanceof HierarchicalStringBuilder) {
                ((HierarchicalStringBuilder)entry.subContent).appendTo(hierarchicalStringBuilder);
            } else {
                hierarchicalStringBuilder.append(entry.subContent);
            }
        }
        hierarchicalStringBuilder.append(currentStringBuilder.toString());
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        appendTo(resultBuilder);
        return resultBuilder.toString();
    }
    
    private class Entry {
        
        final StringBuilder stringBuilder;
        
        final Object subContent;
        
        Entry(StringBuilder stringBuilder, Object subContent) {
            this.stringBuilder = stringBuilder;
            this.subContent = subContent;
        }
        
    }

}
