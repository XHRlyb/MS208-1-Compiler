package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class varExpr extends exprNode {
    public String nam;

    public varExpr(String nam, position pos) {
        super(pos, true);
        this.nam = nam;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
