package hu.webarticum.j2php;

import java.util.Optional;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class ExpressionTranslator {
    
    private final Expression expression;
    
    private final EmbeddingContext embeddingContext;
    
    public ExpressionTranslator(Expression expression, EmbeddingContext embeddingContext) {
        this.expression = expression;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        if (expression.isLiteralExpr()) {
            new LiteralTranslator(expression.asLiteralExpr(), embeddingContext).toString(outputBuilder);
        } else if (expression.isEnclosedExpr()) {
            EnclosedExpr enclosedExpression = expression.asEnclosedExpr();
            Expression subExpression = enclosedExpression.getInner();
            
            outputBuilder.append("(");
            new ExpressionTranslator(subExpression, embeddingContext).toString(outputBuilder);
            outputBuilder.append(")");
        } else if (expression.isVariableDeclarationExpr()) {
            VariableDeclarationExpr variableDeclarationExpression = expression.asVariableDeclarationExpr();
            
            boolean firstInitializer = true;
            for (VariableDeclarator variableDeclarator: variableDeclarationExpression.getVariables()) {
                Optional<Expression> variableInitializerOptional = variableDeclarator.getInitializer();
                if (variableInitializerOptional.isPresent()) {
                    Expression variableInitializerExpression = variableInitializerOptional.get();
                    if (!firstInitializer) {
                        outputBuilder.append(", ");
                    }
                    String variableName = variableDeclarator.getNameAsString(); // XXX
                    outputBuilder.append("$" + variableName + " = ");
                    new ExpressionTranslator(variableInitializerExpression, embeddingContext).toString(outputBuilder);
                    firstInitializer = false;
                }
            }
        } else if (expression.isAssignExpr()) {
            AssignExpr assignExpression = expression.asAssignExpr();
            
            Expression targetExpression = assignExpression.getTarget();
            new ExpressionTranslator(targetExpression, embeddingContext).toString(outputBuilder);
            
            // XXX
            outputBuilder.append(" " + assignExpression.getOperator().asString() + " ");
            
            Expression valueExpression = assignExpression.getValue();
            new ExpressionTranslator(valueExpression, embeddingContext).toString(outputBuilder);
        } else if (expression.isUnaryExpr()) {
            UnaryExpr unaryExpression = expression.asUnaryExpr();
            String operatorString = unaryExpression.getOperator().asString(); // XXX
            Expression subExpression = unaryExpression.getExpression();
            
            if (unaryExpression.isPrefix()) {
                outputBuilder.append(operatorString);
            }
            new ExpressionTranslator(subExpression, embeddingContext).toString(outputBuilder);
            if (unaryExpression.isPostfix()) {
                outputBuilder.append(operatorString);
            }
        } else if (expression.isBinaryExpr()) {
            BinaryExpr binaryExpression = expression.asBinaryExpr();
            String operatorString = binaryExpression.getOperator().asString(); // XXX
            Expression leftExpression = binaryExpression.getLeft();
            Expression rightExpression = binaryExpression.getRight();
            
            new ExpressionTranslator(leftExpression, embeddingContext).toString(outputBuilder);
            outputBuilder.append(" " + operatorString + " ");
            new ExpressionTranslator(rightExpression, embeddingContext).toString(outputBuilder);
        } else if (expression.isConditionalExpr()) {
            ConditionalExpr conditionalExpression = expression.asConditionalExpr();
            Expression conditionExpression = conditionalExpression.getCondition();
            Expression thenExpression = conditionalExpression.getThenExpr();
            Expression elseExpression = conditionalExpression.getElseExpr();
            outputBuilder.append(conditionExpression + " ? " + thenExpression + " : " + elseExpression);
        } else if (expression.isNameExpr()) {
            NameExpr nameExpression = expression.asNameExpr();
            outputBuilder.append(nameExpression.getAncestorOfType(Object.class).get() + " -> ");
            String variableName = nameExpression.getNameAsString(); // XXX
            outputBuilder.append("$" + variableName);
        } else {
            // TODO
            outputBuilder.append("/** EXPR (" + expression.getClass().getSimpleName() + ") **/");
        }
        
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return toString();
    }
    
}
