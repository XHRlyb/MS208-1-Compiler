package AST.statement;

import AST.ASTVisitor;
import Util.position;

public class emptyStmt extends stmtNode{
    public emptyStmt(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
