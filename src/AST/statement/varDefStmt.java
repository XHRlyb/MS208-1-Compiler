package AST.statement;

import AST.ASTVisitor;
import Util.position;
import java.util.ArrayList;

public class varDefStmt extends stmtNode {
    public ArrayList<varDefSigStmt> varLis = new ArrayList<>();

    public varDefStmt(position pos) { super(pos); }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
