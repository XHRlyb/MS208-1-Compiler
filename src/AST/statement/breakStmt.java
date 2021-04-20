package AST.statement;

import AST.ASTVisitor;
import Util.position;

public class breakStmt extends stmtNode {
    public stmtNode loop;

    public breakStmt(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
