package hu.webarticum.j2php;

import java.lang.reflect.Method;

import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class LiteralTranslator {
    
    private final LiteralExpr literalExpression;
    
    private final EmbeddingContext embeddingContext;
    
    public LiteralTranslator(LiteralExpr literalExpression, EmbeddingContext embeddingContext) {
        this.literalExpression = literalExpression;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        if (literalExpression.isStringLiteralExpr()) {
            StringLiteralExpr stringLiteral = literalExpression.asStringLiteralExpr();
            outputBuilder.append("'" + stringLiteral.asString().replaceAll("\\\\|'", "\\\\$0").replaceAll("\\r\\n?|\\n", "' + \"\\\\n\" + '") + "'");
        } else {
            
            // XXX: Java and PHP literals are mostly compatible
            
            Class<? extends LiteralExpr> literalClass = literalExpression.getClass();
            
            Method method = null;
            try {
                method = literalClass.getMethod("getValue");
                if (method.getReturnType() != String.class) {
                    method = null;
                }
            } catch (NoSuchMethodException e) {
            } catch (SecurityException e) {
            }
            
            String literalString = null;
            if (method != null) {
                try {
                    literalString = (String)method.invoke(literalExpression);
                } catch (Exception e) {
                }
            }
            
            if (literalString == null) {
                outputBuilder.append("null");
            } else {
                outputBuilder.append(literalString);
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return resultBuilder.toString();
    }
    
}
