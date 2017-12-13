package hu.webarticum.j2php;

import java.util.Optional;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

public class ExpressionTranslator {
    
    private final Expression expression;
    
    private final EmbeddingContext embeddingContext;
    
    public ExpressionTranslator(Expression expression, EmbeddingContext embeddingContext) {
        this.expression = expression;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        expression.isAnnotationExpr();
        //expression.isArrayAccessExpr();
        //expression.isArrayCreationExpr();
        //expression.isArrayInitializerExpr();
        //expression.isAssignExpr();
        expression.isBinaryExpr();
        //expression.isBooleanLiteralExpr();
        //expression.isCastExpr();
        //expression.isCharLiteralExpr();
        expression.isClassExpr();
        //expression.isConditionalExpr();
        //expression.isDoubleLiteralExpr();
        //expression.isEnclosedExpr();
        expression.isFieldAccessExpr();
        expression.isInstanceOfExpr();
        //expression.isIntegerLiteralExpr();
        expression.isLambdaExpr();
        //expression.isLiteralExpr();
        expression.isLiteralStringValueExpr();
        //expression.isLongLiteralExpr();
        expression.isMarkerAnnotationExpr();
        //expression.isMethodCallExpr();
        expression.isMethodReferenceExpr();
        //expression.isNameExpr();
        expression.isNormalAnnotationExpr();
        //expression.isNullLiteralExpr();
        expression.isObjectCreationExpr();
        expression.isSingleMemberAnnotationExpr();
        //expression.isStringLiteralExpr();
        expression.isSuperExpr();
        expression.isThisExpr();
        expression.isTypeExpr();
        //expression.isUnaryExpr();
        //expression.isVariableDeclarationExpr();
        
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
            NodeList<VariableDeclarator> variables = variableDeclarationExpression.getVariables();
            for (VariableDeclarator variableDeclarator: variables) {
                Optional<Expression> variableInitializerOptional = variableDeclarator.getInitializer();
                String variableName = variableDeclarator.getNameAsString(); // XXX
                if (variableInitializerOptional.isPresent()) {
                    Expression variableInitializerExpression = variableInitializerOptional.get();
                    if (!firstInitializer) {
                        outputBuilder.append(", ");
                    }
                    outputBuilder.append("$" + variableName + " = ");
                    new ExpressionTranslator(variableInitializerExpression, embeddingContext).toString(outputBuilder);
                    firstInitializer = false;
                    
                // XXX
                } else if (variables.size() == 1) {
                    outputBuilder.append("$" + variableName);
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
            
            outputBuilder.append("$");
            
            // XXX incredibly ugly hack
            try {
                TypeSolver typeSolver = new CombinedTypeSolver();
                JavaParserFacade facade = JavaParserFacade.get(typeSolver);
                facade.getSymbolSolver().solveSymbol(nameExpression.getNameAsString(), nameExpression).getCorrespondingDeclaration();
            } catch (Exception e) {
                outputBuilder.append("this->");
            }
            
            String variableName = nameExpression.getNameAsString(); // XXX
            outputBuilder.append(variableName);
        } else if (expression.isArrayCreationExpr()) {
            ArrayCreationExpr arrayCreationExpression = expression.asArrayCreationExpr();
            Optional<ArrayInitializerExpr> initializerOptional = arrayCreationExpression.getInitializer();
            
            if (initializerOptional.isPresent()) {
                new ExpressionTranslator(initializerOptional.get(), embeddingContext).toString(outputBuilder);
            } else {
                int creationLevels = 0;
                boolean isComplete = true;
                
                for (ArrayCreationLevel level: arrayCreationExpression.getLevels()) {
                    Optional<Expression> dimensionOptional = level.getDimension();
                    
                    if (dimensionOptional.isPresent()) {
                        outputBuilder.append("\\array_fill(0, ");
                        Expression dimensionExpression = dimensionOptional.get();
                        new ExpressionTranslator(dimensionExpression, embeddingContext).toString(outputBuilder);
                        outputBuilder.append(", ");
                        creationLevels++;
                    } else {
                        isComplete = false;
                        break;
                    }
                }
                
                
                if (isComplete) {
                    Type arrayType = arrayCreationExpression.getElementType();
                    String valueLiteral;
                    if (!arrayType.isPrimitiveType()) {
                        valueLiteral = "null";
                    } else {
                        PrimitiveType.Primitive primitive = arrayType.asPrimitiveType().getType();
                        switch (primitive) {
                            case BOOLEAN:
                                valueLiteral = "false";
                                break;
                            case CHAR:
                                valueLiteral = "\"\\0\"";
                                break;
                            default:
                                valueLiteral = "0";
                        }
                        
                    }
                    outputBuilder.append(valueLiteral);
                } else {
                    outputBuilder.append("[]");
                }
                for (int i = 0; i < creationLevels; i++) {
                    outputBuilder.append(")");
                }
            }
        } else if (expression.isArrayInitializerExpr()) {
            ArrayInitializerExpr arrayInitializerExpression = expression.asArrayInitializerExpr();
            outputBuilder.append("[\n");
            for (Expression value: arrayInitializerExpression.getValues()) {
                EmbeddingContext valueEmbeddingContext = embeddingContext.moreIndent("    ");
                outputBuilder.append(valueEmbeddingContext.indent);
                new ExpressionTranslator(value, valueEmbeddingContext).toString(outputBuilder);
                outputBuilder.append(",\n");
            }
            outputBuilder.append(embeddingContext.indent + "]");
        } else if (expression.isArrayAccessExpr()) {
            ArrayAccessExpr arrayAccessExpression = expression.asArrayAccessExpr();
            Expression nameExpression = arrayAccessExpression.getName();
            boolean isProtected = (
                nameExpression.isNameExpr() ||
                nameExpression.isEnclosedExpr() ||
                nameExpression.isArrayAccessExpr() ||
                nameExpression.isFieldAccessExpr()
            );
            if (!isProtected) {
                outputBuilder.append("(");
            }
            new ExpressionTranslator(nameExpression, embeddingContext).toString(outputBuilder);
            if (!isProtected) {
                outputBuilder.append(")");
            }
            outputBuilder.append("[");
            new ExpressionTranslator(arrayAccessExpression.getIndex(), embeddingContext).toString(outputBuilder);
            outputBuilder.append("]");
        } else if (expression.isCastExpr()) {
            CastExpr castExpression = expression.asCastExpr();
            Expression subExpression = castExpression.getExpression();
            new ExpressionTranslator(subExpression, embeddingContext).toString(outputBuilder);
        } else if (expression.isMethodCallExpr()) {
            MethodCallExpr methodCallExpression = expression.asMethodCallExpr();
            Optional<Expression> scopeOptional = methodCallExpression.getScope();
            if (scopeOptional.isPresent()) {
                new ExpressionTranslator(scopeOptional.get(), embeddingContext).toString(outputBuilder);
            } else {
                
                // XXX
                outputBuilder.append("$UNKNOWN");
                
            }
            outputBuilder.append("->"); // XXX static etc...
            outputBuilder.append(methodCallExpression.getNameAsString());
            outputBuilder.append("(");
            {
                boolean firstArgument = true;
                for (Expression argumentExpression: methodCallExpression.getArguments()) {
                    if (!firstArgument) {
                        outputBuilder.append(", ");
                    }
                    new ExpressionTranslator(argumentExpression, embeddingContext).toString(outputBuilder);
                    firstArgument = false;
                }
            }
            outputBuilder.append(")");
        } else {
            // TODO
            outputBuilder.append("/** EXPR (" + expression.getClass().getSimpleName() + ") **/");
        }
        
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return resultBuilder.toString();
    }
    
}
