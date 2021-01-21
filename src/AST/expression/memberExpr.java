package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class memberExpr extends exprNode {
    public exprNode bas;
    public String nam;
    public boolean isFun = false;

    public memberExpr(exprNode bas, String nam, position pos) {
        super(pos, true);
        this.bas = bas;
        this.nam = nam;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
