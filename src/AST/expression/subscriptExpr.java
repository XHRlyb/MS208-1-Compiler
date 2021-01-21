package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class subscriptExpr extends exprNode {
    public exprNode bas, offs;

    public subscriptExpr(exprNode bas, exprNode offs, position pos) {
        super(pos, true);
        this.bas = bas;
        this.offs = offs;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
