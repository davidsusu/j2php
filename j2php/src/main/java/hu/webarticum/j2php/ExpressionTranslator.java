package hu.webarticum.j2php;

import com.github.javaparser.ast.expr.Expression;

public class ExpressionTranslator {
    
    private final Expression expression;
    
    private final EmbeddingContext embeddingContext;
    
    public ExpressionTranslator(Expression expression, EmbeddingContext embeddingContext) {
        this.expression = expression;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        
        // TODO
        outputBuilder.append("/** EXPR **/");
        
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return toString();
    }
    
}
