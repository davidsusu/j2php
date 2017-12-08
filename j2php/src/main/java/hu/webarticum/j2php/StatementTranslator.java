package hu.webarticum.j2php;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

// TODO: per block variable postfix

public class StatementTranslator {

    private final Statement statement;
    
    private final EmbeddingContext embeddingContext;
    
    public StatementTranslator(Statement statement, EmbeddingContext embeddingContext) {
        this.statement = statement;
        this.embeddingContext = embeddingContext;
    }

    public void toString(StringBuilder outputBuilder) {
        if (statement.isBlockStmt()) {
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

            Expression expression = ifStatement.getCondition();
            new ExpressionTranslator(expression, embeddingContext).toString(outputBuilder);
            
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
            outputBuilder.append("// TODO switch");
        } else if (statement.isWhileStmt()) {
            outputBuilder.append("// TODO while");
        } else if (statement.isDoStmt()) {
            outputBuilder.append("// TODO do-while");
        } else if (statement.isForStmt()) {
            outputBuilder.append("// TODO for");
        } else if (statement.isForeachStmt()) {
            outputBuilder.append("// TODO foreach");
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
        return toString();
    }
    
}
