package hu.webarticum.j2php;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class UnitTranslator {

    private final Unit unit;
    
    private final FormattingSettings formattingSettings;

    private final TypeMap typeMap;

    public UnitTranslator(Unit unit) {
        this(unit, new FormattingSettings());
    }

    public UnitTranslator(Unit unit, FormattingSettings formattingSettings) {
        this(unit, formattingSettings, null);
    }

    public UnitTranslator(Unit unit, TypeMap typeMap) {
        this(unit, new FormattingSettings(), typeMap);
    }
    
    public UnitTranslator(Unit unit, FormattingSettings formattingSettings, TypeMap typeMap) {
        this.unit = unit;
        this.formattingSettings = formattingSettings;
        this.typeMap = typeMap;
    }
    
    @Override
    public String toString() {
        CompilationUnit compilationUnit = unit.getCompilationUnit();
        
        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append("<?php\n\n");
        
        {
            Optional<PackageDeclaration> packageOptional = compilationUnit.getPackageDeclaration();
            if (packageOptional.isPresent()) {
                PackageDeclaration packageDeclaration = packageOptional.get();
                String packageName = packageDeclaration.getNameAsString();
                String nsName = packageName.replaceAll("\\.", "\\\\");
                resultBuilder.append("namespace " + nsName + ";\n\n");
            }
        }
        
        {
            List<ImportDeclaration> imports = compilationUnit.getImports();
            for (ImportDeclaration importDeclaration: imports) {
                String importName = importDeclaration.getNameAsString();
                String useName = importName.replaceAll("\\.", "\\\\");
                resultBuilder.append("use " + useName + ";\n");
            }
            if (!imports.isEmpty()) {
                resultBuilder.append("\n");
            }
        }
        
        EmbeddingContext embeddingContext = new EmbeddingContext(formattingSettings, "");
        ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration)compilationUnit.getType(0);
        ClassTranslator classTranslator = new ClassTranslator(classDeclaration, embeddingContext);
        classTranslator.toString(resultBuilder);
        
        return resultBuilder.toString();
    }
    
}
