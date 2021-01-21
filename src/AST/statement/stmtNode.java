package AST.statement;

import AST.ASTNode;
import Util.position;

abstract public class stmtNode extends ASTNode {
    public stmtNode(position pos) {
        super(pos);
    }
}
