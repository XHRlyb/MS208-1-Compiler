package AST.statement;

import AST.expression.exprNode;
import AST.ASTVisitor;
import Util.position;

public class exprStmt extends stmtNode {
    public exprNode expr;

    public exprStmt(exprNode expr, position pos) {
        super(pos);
        this.expr = expr;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
