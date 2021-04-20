package AST.statement;

import AST.ASTVisitor;
import Util.position;

public class continueStmt extends stmtNode{
    public stmtNode loop;
    public continueStmt(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
