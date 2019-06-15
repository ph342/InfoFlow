package syntaxtree;

import visitor.Visitor;

public abstract class Cmd extends AST {

    public abstract <T> T accept(Visitor<T> v);
}
