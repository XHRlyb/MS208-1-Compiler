package AST.expression;

import IR.*;
import IR.operand.*;
import AST.ASTNode;
import Util.position;
import Util.symbol.Type;

abstract public class exprNode extends ASTNode {
    public Type typ;
    public boolean isAsg = false;

    public Block tBlk = null, fBlk = null;
    public Operand oprnd;

    public exprNode(position pos) { super(pos); }
    public exprNode(position pos, boolean isAsg) {
        super(pos);
        this.isAsg = isAsg;
    }
}
