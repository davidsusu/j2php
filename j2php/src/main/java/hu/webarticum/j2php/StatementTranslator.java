package hu.webarticum.j2php;

import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.Type;

import hu.webarticum.j2php.util.HierarchicalStringBuilder;

// TODO: per block variable postfix
//    (necessary? new names are safe! however: shared variables (eg closure))

public class StatementTranslator {

    private final Statement statement;
    
    private final EmbeddingContext embeddingContext;
    
    public StatementTranslator(Statement statement, EmbeddingContext embeddingContext) {
        this.statement = statement;
        this.embeddingContext = embeddingContext;
    }

    public void toString(HierarchicalStringBuilder outputBuilder) {
        //statement.isAssertStmt();
        //statement.isBlockStmt();
        //statement.isBreakStmt();
        //statement.isContinueStmt();
        //statement.isDoStmt();
        //statement.isEmptyStmt();
        statement.isExplicitConstructorInvocationStmt();
        //statement.isExpressionStmt();
        //statement.isForeachStmt();
        //statement.isForStmt();
        //statement.isIfStmt();
        //statement.isLabeledStmt();
        statement.isLocalClassDeclarationStmt();
        //statement.isReturnStmt();
        //statement.isSwitchEntryStmt();
        //statement.isSwitchStmt();
        //statement.isSynchronizedStmt();
        //statement.isThrowStmt();
        //statement.isTryStmt();
        //statement.isWhileStmt();

        if (statement.isAssertStmt()) {
            // nothing to do
        } else if (statement.isEmptyStmt()) {
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
        } else if (statement.isSynchronizedStmt()) {
            SynchronizedStmt synchronizedStatement = statement.asSynchronizedStmt();
            
            outputBuilder.append("/* synchronized */ ");
            new StatementTranslator(synchronizedStatement.getBody(), embeddingContext).toString(outputBuilder);
        } else if (statement.isLabeledStmt()) {
            
            // TODO
            // XXX: break, continue etc.
            
            LabeledStmt labeledStatement = statement.asLabeledStmt();
            
            outputBuilder.append("/* synchronized */ ");
            new StatementTranslator(labeledStatement.getStatement(), embeddingContext).toString(outputBuilder);
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
            
        } else if (statement.isBreakStmt()) {
            BreakStmt breakStatement = statement.asBreakStmt();
            outputBuilder.append("break");
            // TODO: breakStatement.getLabel();
            outputBuilder.append(";");
        } else if (statement.isContinueStmt()) {
            ContinueStmt continueStatement = statement.asContinueStmt();
            outputBuilder.append("continue");
            // TODO: continueStatement.getLabel();
            outputBuilder.append(";");
        } else if (statement.isSwitchStmt()) {
            SwitchStmt switchStatement = statement.asSwitchStmt();
            
            outputBuilder.append("switch (");
            
            new ExpressionTranslator(switchStatement.getSelector(), embeddingContext).toString(outputBuilder);
            
            outputBuilder.append(") {\n");
            
            EmbeddingContext subEmbeddingContext = embeddingContext.moreIndent("    ");
            EmbeddingContext subSubEmbeddingContext = subEmbeddingContext.moreIndent("    ");
            
            for (SwitchEntryStmt switchEntryStatement: switchStatement.getEntries()) {
                Optional<Expression> labelOptional = switchEntryStatement.getLabel();
                if (labelOptional.isPresent()) {
                    Expression labelExpression = labelOptional.get();
                    outputBuilder.append(subEmbeddingContext.indent + "case ");
                    new ExpressionTranslator(labelExpression, embeddingContext).toString(outputBuilder);
                    outputBuilder.append(":\n");
                } else {
                    outputBuilder.append(subEmbeddingContext.indent + "default:\n");
                }
                
                for (Statement switchEntrySubStatement: switchEntryStatement.getStatements()) {
                    outputBuilder.append(subSubEmbeddingContext.indent);
                    new StatementTranslator(switchEntrySubStatement, subSubEmbeddingContext).toString(outputBuilder);
                    outputBuilder.append("\n");
                }
            }
            
            outputBuilder.append(embeddingContext.indent + "}");
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
        } else if (statement.isTryStmt()) {
            TryStmt tryStatement = statement.asTryStmt();
            
            outputBuilder.append("try ");
            new StatementTranslator(tryStatement.getTryBlock(), embeddingContext).toString(outputBuilder);
            
            for (CatchClause catchClause: tryStatement.getCatchClauses()) {
                outputBuilder.append(" catch (");
                Parameter parameter = catchClause.getParameter();
                
                ExpressionTranslator.printType(parameter.getType(), outputBuilder);
                outputBuilder.append(" ");
                
                String parameterName = parameter.getNameAsString();
                outputBuilder.append(parameterName);
                
                outputBuilder.append(") ");
                new StatementTranslator(catchClause.getBody(), embeddingContext).toString(outputBuilder);
            }
            
            Optional<BlockStmt> finallyOptional = tryStatement.getFinallyBlock();
            if (finallyOptional.isPresent()) {
                BlockStmt finallyBlock = finallyOptional.get();
                outputBuilder.append(" finally ");
                new StatementTranslator(finallyBlock, embeddingContext).toString(outputBuilder);
            }
            
        } else if (statement.isThrowStmt()) {
            ThrowStmt throwStatement = statement.asThrowStmt();
            outputBuilder.append("throw ");
            new ExpressionTranslator(throwStatement.getExpression(), embeddingContext).toString(outputBuilder);
            outputBuilder.append(";");
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
            // TODO
            outputBuilder.append("/** STMT (" + statement.getClass().getSimpleName() + ") **/");
        }
    }
    
    @Override
    public String toString() {
        HierarchicalStringBuilder resultBuilder = new HierarchicalStringBuilder();
        toString(resultBuilder);
        return resultBuilder.toString();
    }
    
}
