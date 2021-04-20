package AST.expression;

import AST.ASTVisitor;
import Util.position;
import Util.symbol.*;

public class varExpr extends exprNode {
    public String nam;
    public boolean inCls;

    public varEntity var;

    public varExpr(String nam, position pos) {
        super(pos, true);
        this.nam = nam;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
