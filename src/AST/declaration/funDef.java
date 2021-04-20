package AST.declaration;

import AST.ASTVisitor;
import AST.statement.*;
import Util.position;
import Util.symbol.*;
import java.util.ArrayList;

public class funDef extends defNode {
    public String nam;
    public typeNode typ;
    public blockStmt block;
    public ArrayList<varDefSigStmt> params;

    public funEntity fun = null;
    public Type retTyp = null;

    public funDef(String nam, typeNode typ, blockStmt block, ArrayList<varDefSigStmt> params, position pos) {
        super(pos);
        this.nam = nam;
        this.typ = typ;
        this.block = block;
        this.params = params;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
