package hu.webarticum.j2php;

import java.io.IOException;
import java.io.InputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;

public class Unit {
    
    private Source source;
    
    private String name = null;
    
    private CompilationUnit compilationUnit = null;
    
    // TODO: translation map (method mapping etc.)
    
    public Unit(Source source) {
        this.source = source;
    }

    public String getName() {
        if (name == null) {
            CompilationUnit compilationUnit = getCompilationUnit();
            String packageName = compilationUnit.getPackageDeclaration().get().getNameAsString(); // XXX
            for (TypeDeclaration<?> type: compilationUnit.getTypes()) {
                name = packageName + "." + type.getNameAsString(); // XXX
                break;
            }
        }
        return name;
    }

    public CompilationUnit getCompilationUnit() {
        if (compilationUnit == null) {
            InputStream inputStream;
            try {
                inputStream = source.getInputStream();
            } catch (IOException e) {
                return null; // XXX
            }
            compilationUnit = JavaParser.parse(inputStream);
        }
        return compilationUnit;
    }
    
}

class X {}
