package AST.statement;

import AST.ASTVisitor;
import AST.expression.exprNode;
import Util.position;

public class ifStmt extends stmtNode {
    public exprNode cond;
    public stmtNode tStmt, fStmt;

    public ifStmt(exprNode cond, stmtNode tStmt, stmtNode fStmt, position pos) {
        super(pos);
        this.cond = cond;
        this.tStmt  = tStmt;
        this.fStmt = fStmt;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
