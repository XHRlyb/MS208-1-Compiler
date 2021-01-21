package AST.expression;

import AST.ASTVisitor;
import Util.position;
import java.util.ArrayList;

public class funCallExpr extends exprNode {
    public exprNode bas;
    public ArrayList<exprNode> params;

    public funCallExpr(exprNode bas, exprList paramlis, position pos) {
        super(pos);
        this.bas = bas;
        this.params = paramlis.params;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
