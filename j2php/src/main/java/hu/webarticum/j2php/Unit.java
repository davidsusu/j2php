package hu.webarticum.j2php;

import com.github.javaparser.ast.CompilationUnit;

public class Unit {
    
    private Source source;
    
    private String name = null;
    
    private CompilationUnit parserCompilationUnit = null;
    
    public Unit(Source source) {
        this.source = source;
    }
    
    public String getName() {
        // XXX
        return null;
    }
    
}
