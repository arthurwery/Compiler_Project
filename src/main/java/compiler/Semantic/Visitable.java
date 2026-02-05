package compiler.Semantic;

public interface Visitable {
    public void accept(Visitor visitor) throws SemanticException;
}
