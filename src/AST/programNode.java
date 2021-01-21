package AST;

import Util.position;
import java.util.ArrayList;

public class programNode extends ASTNode {
    public ArrayList<ASTNode> body = new ArrayList<>();

    public programNode(position pos) { super(pos); }

    @Override
    public void accept(ASTVisitor vis) { vis.visit(this); }
}