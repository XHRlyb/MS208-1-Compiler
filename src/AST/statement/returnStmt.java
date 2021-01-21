package AST.statement;

import AST.ASTVisitor;
import AST.expression.exprNode;
import Util.position;

public class returnStmt extends stmtNode {
    public exprNode retVal;

    public returnStmt(exprNode retVal, position pos) {
        super(pos);
        this.retVal = retVal;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
