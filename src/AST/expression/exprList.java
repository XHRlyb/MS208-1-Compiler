package AST.expression;

import AST.ASTNode;
import AST.ASTVisitor;
import Util.position;
import java.util.ArrayList;

public class exprList extends ASTNode {
    public ArrayList<exprNode> params = new ArrayList<>();

    public exprList(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
