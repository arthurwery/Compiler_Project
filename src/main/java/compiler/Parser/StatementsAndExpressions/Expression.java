package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.Visitor;

public abstract class Expression extends Statement {
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

}
