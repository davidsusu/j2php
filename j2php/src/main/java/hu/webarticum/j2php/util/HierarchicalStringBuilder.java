package hu.webarticum.j2php.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HierarchicalStringBuilder implements Appendable {
    
    private final List<Entry> entries = new ArrayList<Entry>();
    
    private StringBuilder currentStringBuilder = new StringBuilder();

    public HierarchicalStringBuilder insertSub() {
        Entry newEntry = new Entry();
        entries.add(newEntry);
        return newEntry.subBuilder;
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
            appendable.append(entry.inlineBuilder.toString());
            entry.subBuilder.appendTo(appendable);
        }
        appendable.append(currentStringBuilder.toString());
    }

    public void appendTo(StringBuilder stringBuilder) {
        for (Entry entry: entries) {
            stringBuilder.append(entry.inlineBuilder.toString());
            entry.subBuilder.appendTo(stringBuilder);
        }
        stringBuilder.append(currentStringBuilder.toString());
    }

    public void appendTo(HierarchicalStringBuilder hierarchicalStringBuilder) {
        for (Entry entry: entries) {
            hierarchicalStringBuilder.append(entry.inlineBuilder.toString());
            entry.subBuilder.appendTo(hierarchicalStringBuilder);
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
        
        StringBuilder inlineBuilder = new StringBuilder();
        
        HierarchicalStringBuilder subBuilder = new HierarchicalStringBuilder();
        
    }

}
