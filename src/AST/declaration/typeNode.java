package AST.declaration;

import AST.ASTNode;
import AST.ASTVisitor;
import Util.position;

public class typeNode extends ASTNode {
    public String typ;
    public int dim, vid;

    public typeNode(String typ, int dim, position pos) {
        super(pos);
        this.typ = typ;
        this.dim = dim;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
