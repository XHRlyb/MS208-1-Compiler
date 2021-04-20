package AST.statement;

import AST.ASTVisitor;
import AST.declaration.typeNode;
import AST.expression.exprNode;
import Util.position;
import Util.symbol.*;

public class varDefSigStmt extends stmtNode {
    public String nam;
    public typeNode typ;
    public exprNode expr;

    public varEntity var;

    public varDefSigStmt(String nam, exprNode expr, position pos) {
        super(pos);
        this.nam = nam;
        this.expr = expr;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
