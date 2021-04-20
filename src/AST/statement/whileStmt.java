package AST.statement;

import IR.Block;
import AST.ASTVisitor;
import AST.expression.exprNode;
import Util.position;

public class whileStmt extends stmtNode {
    public exprNode cond;
    public stmtNode body;

    public Block destBlk, condBlk;

    public whileStmt(exprNode cond, stmtNode body, position pos) {
        super(pos);
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
