package AST.statement;

import AST.ASTVisitor;
import Util.position;
import java.util.ArrayList;

public class blockStmt extends stmtNode{
    public ArrayList<stmtNode> stmtLis = new ArrayList<>();

    public blockStmt(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
