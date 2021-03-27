package AST;

import Util.position;
import Util.symbol.*;

abstract public class ASTNode {
    public position pos;
    public Scope scp;
    public RegId rid;

    public ASTNode(position pos) { this.pos = pos; }

    abstract public void accept(ASTVisitor vis);
}