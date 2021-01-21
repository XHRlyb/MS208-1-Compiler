package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class thisExpr extends exprNode {
    public thisExpr(position pos) { super(pos); }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
