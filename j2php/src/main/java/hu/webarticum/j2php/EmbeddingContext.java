package hu.webarticum.j2php;

public class EmbeddingContext {
    
    public final FormattingSettings formattingsSettings;
    
    public final String indent;
    
    public EmbeddingContext(FormattingSettings formattingsSettings, String indent) {
        this.formattingsSettings = formattingsSettings;
        this.indent = indent;
    }
    
    public EmbeddingContext moreIndent(String plusIndent) {
        return new EmbeddingContext(formattingsSettings, indent + plusIndent);
    }
    
}
