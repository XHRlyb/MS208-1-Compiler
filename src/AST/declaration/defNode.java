package AST.declaration;

import AST.*;
import Util.position;

public abstract class defNode extends ASTNode {
    public String abs_addr;
    public defNode(position pos) {
        super(pos);
    }
}
