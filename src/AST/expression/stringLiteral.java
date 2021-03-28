package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class stringLiteral extends exprNode {
    public String val;
    public int id;

    public stringLiteral(String val, position pos) {
        super(pos);
        this.val = val;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
