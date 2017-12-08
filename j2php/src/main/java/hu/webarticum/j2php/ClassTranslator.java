package hu.webarticum.j2php;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ClassTranslator {

    private final ClassOrInterfaceDeclaration classDeclaration;
    
    private final EmbeddingContext embeddingContext;
    
    public ClassTranslator(ClassOrInterfaceDeclaration classDeclaration, EmbeddingContext embeddingContext) {
        this.classDeclaration = classDeclaration;
        this.embeddingContext = embeddingContext;
    }
    
    public void toString(StringBuilder outputBuilder) {
        if (classDeclaration.isInterface()) {
            outputBuilder.append("interface ");
        } else {
            if (classDeclaration.isAbstract()) {
                outputBuilder.append("abstract ");
            } else if (classDeclaration.isFinal()) {
                outputBuilder.append("final ");
            }
            outputBuilder.append("class ");
        }
        
        outputBuilder.append(classDeclaration.getNameAsString());
        
        {
            NodeList<ClassOrInterfaceType> extendedTypes = classDeclaration.getExtendedTypes();
            if (!extendedTypes.isEmpty()) {
                String[] extendedTypeNames = new String[extendedTypes.size()];
                int i = -1;
                for (ClassOrInterfaceType extendedType: extendedTypes) {
                    extendedTypeNames[++i] = extendedType.getNameAsString();
                }
                outputBuilder.append(" extends " + String.join(", ", extendedTypeNames));
            }
        }
        
        {
            NodeList<ClassOrInterfaceType> implementedTypes = classDeclaration.getImplementedTypes();
            if (!implementedTypes.isEmpty()) {
                String[] extendedTypeNames = new String[implementedTypes.size()];
                int i = -1;
                for (ClassOrInterfaceType extendedType: implementedTypes) {
                    extendedTypeNames[++i] = extendedType.getNameAsString();
                }
                outputBuilder.append(" implements " + String.join(", ", extendedTypeNames));
            }
        }

        outputBuilder.append(" {\n");
        
        {
            EmbeddingContext innerEmbeddingContext = embeddingContext.moreIndent("    ");
            outputBuilder.append(innerEmbeddingContext.indent + "\n");
            {
                
                // TODO: grouping by name
                for (MethodDeclaration methodDeclaration: classDeclaration.getMethods()) {
                    outputBuilder.append(innerEmbeddingContext.indent);
                    new MethodTranslator(methodDeclaration, innerEmbeddingContext).toString(outputBuilder);
                    outputBuilder.append("\n" + innerEmbeddingContext.indent + "\n");
                }
            }
        }

        outputBuilder.append(embeddingContext.indent + "}");
        
        
        
        
        
        
        
        /*
        
        {
            List<String> instanceInitializerNames = new ArrayList<String>();
            {
                for (BodyDeclaration<?> member: clazz.getMembers()) {
                    if (member.isInitializerDeclaration()) {
                        InitializerDeclaration initializer = (InitializerDeclaration)member;
                        if (initializer.isStatic()) {
                            String postFix = staticInitializerNames.isEmpty() ? "" : "" + (staticInitializerNames.size() + 1);
                            String associatedName = "_staticInit" + postFix;
                            staticInitializerNames.add(associatedName);
                            resultBuilder.append("    public static " + associatedName + "() {\n        // TODO\n    }\n\n");
                        } else {
                            String postFix = instanceInitializerNames.isEmpty() ? "" : "" + (instanceInitializerNames.size() + 1);
                            String associatedName = "_init" + postFix;
                            instanceInitializerNames.add(associatedName);
                            resultBuilder.append("    private " + associatedName + "() {\n        // TODO\n    }\n\n");
                        }
                    }
                }
            }
            
            {
                List<ConstructorDeclaration> constructors = clazz.getConstructors();
                for (ConstructorDeclaration constructor: constructors) {
                    int parameterCount = constructor.getParameters().size();
                    resultBuilder.append("    // TODO: constructor with " + parameterCount + " parameter(s)\n\n");
                }
                if (!constructors.isEmpty()) {
                    resultBuilder.append("\n");
                }
            }
        }
            
        for (String staticInitializedName: staticInitializerNames) {
            resultBuilder.append("\n" + className + "::" + staticInitializedName + "();\n");
        }
        */
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return toString();
    }
    
}
