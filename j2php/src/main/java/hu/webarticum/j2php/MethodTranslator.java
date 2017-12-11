package hu.webarticum.j2php;

import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;

public class MethodTranslator {

    private MethodDeclaration methodDeclaration;
    
    private EmbeddingContext embeddingContext;
    
    public MethodTranslator(MethodDeclaration methodDeclaration, EmbeddingContext embeddingContext) {
        this.methodDeclaration = methodDeclaration;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        ClassOrInterfaceDeclaration containerClassDeclaration = (ClassOrInterfaceDeclaration)methodDeclaration.getParentNode().get();
        
        if (methodDeclaration.isPrivate()) {
            outputBuilder.append("private ");
        } else if (methodDeclaration.isProtected()) {
            outputBuilder.append("protected ");
        } else {
            outputBuilder.append("public ");
        }
        
        if (methodDeclaration.isAbstract() && !containerClassDeclaration.isInterface()) {
            outputBuilder.append("abstract ");
        }
        
        outputBuilder.append("function " + methodDeclaration.getNameAsString() + "(");
        
        {
            NodeList<Parameter> parameters = methodDeclaration.getParameters();
            if (!parameters.isEmpty()) {
                String[] parameterFragments = new String[parameters.size()];
                int i = -1;
                for (Parameter parameter: parameters) {
                    parameterFragments[++i] = "$" + parameter.getNameAsString();
                }
                outputBuilder.append(String.join(", ", parameterFragments));
            }
        }
        
        outputBuilder.append(")");
        
        Optional<BlockStmt> bodyOptional = methodDeclaration.getBody();
        
        if (!bodyOptional.isPresent()) {
            outputBuilder.append(";");
        } else {
            outputBuilder.append(" ");
            BlockStmt body = bodyOptional.get();
            new StatementTranslator(body, embeddingContext).toString(outputBuilder);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return resultBuilder.toString();
    }
    
}
