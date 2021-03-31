package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class varExpr extends exprNode {
    public String nam;
    public boolean incls;

    public varExpr(String nam, position pos) {
        super(pos, true);
        this.nam = nam;
        incls = false;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
