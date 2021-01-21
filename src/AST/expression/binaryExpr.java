package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class binaryExpr extends exprNode{
    public exprNode src1, src2;
    public String op;

    public binaryExpr(exprNode src1, exprNode src2, String op, position pos) {
        super(pos);
        this.src1 = src1;
        this.src2 = src2;
        this.op = op;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
