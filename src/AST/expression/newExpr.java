package AST.expression;

import AST.ASTVisitor;
import AST.declaration.typeNode;
import Util.position;
import java.util.ArrayList;

public class newExpr extends exprNode {
    public typeNode typNd;
    public ArrayList<exprNode> exprs;

    public newExpr(typeNode typ, int dim, ArrayList<exprNode> exprs, position pos) {
        super(pos);
        this.typNd = typ;
        this.typNd.dim = dim;
        this.exprs = exprs;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
