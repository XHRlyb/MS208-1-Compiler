package AST.expression;

import AST.ASTVisitor;
import Util.position;

public class nullLiteral extends exprNode {
    public nullLiteral(position pos) { super(pos); }

    @Override
    public void accept(ASTVisitor vis) { vis.visit(this); }
}
