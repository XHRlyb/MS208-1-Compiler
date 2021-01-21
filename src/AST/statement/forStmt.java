package AST.statement;

import AST.ASTVisitor;
import AST.expression.exprNode;
import Util.position;

public class forStmt extends stmtNode{
    public exprNode cond, inc, ini;
    public stmtNode body;

    public forStmt(exprNode ini, exprNode cond, exprNode inc, stmtNode body, position pos) {
        super(pos);
        this.ini = ini;
        this.inc = inc;
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
