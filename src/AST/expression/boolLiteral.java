package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class boolLiteral extends exprNode{
    public boolean val;

    public boolLiteral(boolean val, position pos) {
        super(pos);
        this.val = val;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
