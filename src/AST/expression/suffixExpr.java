package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class suffixExpr extends exprNode {
    public exprNode src;
    public String op;

    public suffixExpr(exprNode src, String op, position pos) {
        super(pos);
        this.src = src;
        this.op = op;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
