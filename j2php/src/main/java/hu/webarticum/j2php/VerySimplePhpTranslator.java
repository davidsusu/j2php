package hu.webarticum.j2php;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class VerySimplePhpTranslator {
    
    private CompilationUnit compilationUnit;
    
    public VerySimplePhpTranslator(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }
    
    @Override
    public String toString() {
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
        
        String className;
        List<String> staticInitializerNames = new ArrayList<String>();
        
        {
            ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration)compilationUnit.getTypes().get(0);
            
            className = clazz.getNameAsString();

            resultBuilder.append("class " + className + "");

            {
            	NodeList<ClassOrInterfaceType> extendedTypes = clazz.getExtendedTypes();
            	for (ClassOrInterfaceType extendedType: extendedTypes) {
            		System.out.println(extendedType.getNameAsString() + ": " + extendedType.isUnknownType());
            	}
            }

            {
            	NodeList<ClassOrInterfaceType> implementedTypes = clazz.getImplementedTypes();
            	for (ClassOrInterfaceType implementedType: implementedTypes) {
            		System.out.println(implementedType.getNameAsString() + ": " + implementedType.isUnknownType());
            	}
            }
            
            resultBuilder.append(" {\n\n");
            
            {
                List<FieldDeclaration> fields = clazz.getFields();
                for (FieldDeclaration field: fields) {
                    String visibilityName = "public";
                    EnumSet<Modifier> modifiers = field.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE)) {
                        visibilityName = "private";
                    } else if (modifiers.contains(Modifier.PROTECTED)) {
                        visibilityName = "protected";
                    }
                    boolean isStatic = modifiers.contains(Modifier.STATIC);
                    NodeList<VariableDeclarator> variables = field.getVariables();
                    int variableCount = variables.size();
                    String[] variableNames = new String[variables.size()];
                    for (int i = 0; i < variableCount; i++) {
                        variableNames[i] = variables.get(i).getNameAsString();
                    }
                    resultBuilder.append("    " + visibilityName + (isStatic ? " static" : "") + " $" + String.join(", $", variableNames) + ";\n\n");
                }
                if (!fields.isEmpty()) {
                    resultBuilder.append("\n");
                }
            }

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
            
            {
                List<MethodDeclaration> methods = clazz.getMethods();
                for (MethodDeclaration method: methods) {
                    String visibilityName = "public";
                    EnumSet<Modifier> modifiers = method.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE)) {
                        visibilityName = "private";
                    } else if (modifiers.contains(Modifier.PROTECTED)) {
                        visibilityName = "protected";
                    }
                    boolean isAbstract = modifiers.contains(Modifier.ABSTRACT);
                    boolean isStatic = modifiers.contains(Modifier.STATIC);
                    String methodName = method.getNameAsString();
                    resultBuilder.append("    " + visibilityName + (isAbstract ? " abstract" : "") + (isStatic ? " static" : "") + " " + methodName + "(");
                    {
                        boolean isFirstParameter = true;
                        for (Parameter parameter: method.getParameters()) {
                            if (!isFirstParameter) {
                                resultBuilder.append(", ");
                            }
                            String parameterName = parameter.getNameAsString();
                            resultBuilder.append("$" + parameterName);
                            isFirstParameter = false;
                        }
                    }
                    resultBuilder.append(")");
                    if (method.isAbstract()) {
                        resultBuilder.append(";\n\n");
                    } else {
                        resultBuilder.append(" {\n");
                        resultBuilder.append("        // TODO\n");
                        resultBuilder.append("    }\n\n");
                    }
                }
                if (!methods.isEmpty()) {
                    resultBuilder.append("\n");
                }
            }
            
            resultBuilder.append("}\n");
        }

        for (String staticInitializedName: staticInitializerNames) {
            resultBuilder.append("\n" + className + "::" + staticInitializedName + "();\n");
        }
        
        return resultBuilder.toString();
    }
    
}
