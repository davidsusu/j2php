package hu.webarticum.j2php;

import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;

// TODO: per block variable postfix

public class StatementTranslator {

    private final Statement statement;
    
    private final EmbeddingContext embeddingContext;
    
    public StatementTranslator(Statement statement, EmbeddingContext embeddingContext) {
        this.statement = statement;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        if (statement.isEmptyStmt()) {
            outputBuilder.append(";");
        } else if (statement.isBlockStmt()) {
            BlockStmt blockStatement = statement.asBlockStmt();
            
            outputBuilder.append("{\n");
            
            {
                EmbeddingContext innerEmbeddingContext = embeddingContext.moreIndent("    ");
                for (Statement subStatement: blockStatement.getStatements()) {
                    outputBuilder.append(innerEmbeddingContext.indent);
                    new StatementTranslator(subStatement, innerEmbeddingContext).toString(outputBuilder);
                    outputBuilder.append("\n");
                }
            }
            
            outputBuilder.append(embeddingContext.indent + "}");
        } else if (statement.isExpressionStmt()) {
            new ExpressionTranslator(
                statement.asExpressionStmt().getExpression(),
                embeddingContext
            ).toString(outputBuilder);
            outputBuilder.append(";");
        } else if (statement.isIfStmt()) {
            IfStmt ifStatement = statement.asIfStmt();
            
            outputBuilder.append("if (");

            Expression conditionExpression = ifStatement.getCondition();
            new ExpressionTranslator(conditionExpression, embeddingContext).toString(outputBuilder);
            
            outputBuilder.append(") ");
            
            Statement thenStatement = ifStatement.getThenStmt();
            new StatementTranslator(thenStatement, embeddingContext).toString(outputBuilder);
            
            Optional<Statement> elseStatementOptional = ifStatement.getElseStmt();
            if (elseStatementOptional.isPresent()) {
                Statement elseStatement = elseStatementOptional.get();
                
                outputBuilder.append(" else");
                if (!elseStatement.isIfStmt()) {
                    outputBuilder.append(" ");
                }
                new StatementTranslator(elseStatement, embeddingContext).toString(outputBuilder);
            }
            
        } else if (statement.isSwitchStmt()) {
            
            // TODO
            
            outputBuilder.append("switch (@value) {\n" + embeddingContext.indent + "    // TODO\n" + embeddingContext.indent + "}");
        } else if (statement.isWhileStmt()) {
            WhileStmt whileStatement = statement.asWhileStmt();
            
            outputBuilder.append("while (");
            new ExpressionTranslator(whileStatement.getCondition(), embeddingContext).toString(outputBuilder);
            outputBuilder.append(") ");
            
            new StatementTranslator(whileStatement.getBody(), embeddingContext).toString(outputBuilder);
        } else if (statement.isDoStmt()) {
            DoStmt doStatement = statement.asDoStmt();
            
            outputBuilder.append("do ");
            
            new StatementTranslator(doStatement.getBody(), embeddingContext).toString(outputBuilder);
            
            outputBuilder.append(" while (");
            new ExpressionTranslator(doStatement.getCondition(), embeddingContext).toString(outputBuilder);
            outputBuilder.append(");");
        } else if (statement.isForStmt()) {
            ForStmt forStatement = statement.asForStmt();
            
            outputBuilder.append("for (");

            {
                NodeList<Expression> initializationExpressions = forStatement.getInitialization();
                String[] initializationOutputs = new String[initializationExpressions.size()];
                int i = -1;
                for (Expression initializationExpression: initializationExpressions) {
                    initializationOutputs[++i] = new ExpressionTranslator(initializationExpression, embeddingContext).toString();
                }
                
                outputBuilder.append(String.join(", ", initializationOutputs));
            }
            
            outputBuilder.append("; ");
            
            Optional<Expression> compareExpressionOptional = forStatement.getCompare();
            if (compareExpressionOptional.isPresent()) {
                new ExpressionTranslator(compareExpressionOptional.get(), embeddingContext).toString(outputBuilder);
            }

            outputBuilder.append("; ");
            
            {
                NodeList<Expression> updateExpressions = forStatement.getUpdate();
                String[] updateOutputs = new String[updateExpressions.size()];
                int i = -1;
                for (Expression updateExpression: updateExpressions) {
                    updateOutputs[++i] = new ExpressionTranslator(updateExpression, embeddingContext).toString();
                }
                outputBuilder.append(String.join(", ", updateOutputs));
            }
            
            outputBuilder.append(") ");
            
            new StatementTranslator(forStatement.getBody(), embeddingContext).toString(outputBuilder);
        } else if (statement.isForeachStmt()) {
            ForeachStmt foreachStatement = statement.asForeachStmt();
            
            outputBuilder.append("foreach (");
            new ExpressionTranslator(foreachStatement.getIterable(), embeddingContext).toString(outputBuilder);
            outputBuilder.append(" as ");
            new ExpressionTranslator(foreachStatement.getVariable(), embeddingContext).toString(outputBuilder);
            outputBuilder.append(")");
            
            new StatementTranslator(foreachStatement.getBody(), embeddingContext).toString(outputBuilder);
        } else if (statement.isReturnStmt()) {
            ReturnStmt returnStatement = statement.asReturnStmt();
            
            outputBuilder.append("return");
            
            Optional<Expression> returnExpressionOptional = returnStatement.getExpression();
            if (returnExpressionOptional.isPresent()) {
                outputBuilder.append(" ");
                new ExpressionTranslator(returnExpressionOptional.get(), embeddingContext)
                    .toString(outputBuilder)
                ;
            }
            
            outputBuilder.append(";");
        } else {
            outputBuilder.append("// TODO ???");
        }
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return resultBuilder.toString();
    }
    
}
