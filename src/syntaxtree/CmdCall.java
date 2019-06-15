package syntaxtree;

import java.util.List;
import visitor.Visitor;

public class CmdCall extends Cmd {

    public final String id;
    public final List<Exp> ais;
    public final List<Var> aos;

    public CmdCall(String id, List<Exp> ais, List<Var> aos) {
        this.id = id;
        this.ais = ais;
        this.aos = aos;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
