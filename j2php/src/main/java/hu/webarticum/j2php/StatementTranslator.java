package hu.webarticum.j2php;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class StatementTranslator {

    private Statement statement;
    
    private EmbeddingContext embeddingContext;
    
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
        } else {
            outputBuilder.append("// TODO");
        }
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        toString(resultBuilder);
        return toString();
    }
    
}
