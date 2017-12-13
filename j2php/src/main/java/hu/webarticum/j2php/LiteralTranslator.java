package hu.webarticum.j2php;

import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class LiteralTranslator {
    
    private final LiteralExpr literalExpression;
    
    @SuppressWarnings("unused")
    private final EmbeddingContext embeddingContext;
    
    public LiteralTranslator(LiteralExpr literalExpression, EmbeddingContext embeddingContext) {
        this.literalExpression = literalExpression;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        if (literalExpression.isStringLiteralExpr()) {
            StringLiteralExpr stringLiteral = literalExpression.asStringLiteralExpr();
            outputBuilder.append("'" + stringLiteral.asString().replaceAll("\\\\|'", "\\\\$0").replaceAll("\\r\\n?|\\n", "' + \"\\\\n\" + '") + "'");
        } else if (literalExpression.isLiteralStringValueExpr()) {
            LiteralStringValueExpr literalStringValueExpression = literalExpression.asLiteralStringValueExpr();
            outputBuilder.append(literalStringValueExpression.getValue());
        } else {
            outputBuilder.append("null");
        }
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return resultBuilder.toString();
    }
    
}
