package AST.declaration;

import AST.ASTVisitor;
import AST.statement.varDefSigStmt;
import Util.symbol.*;
import Util.position;
import java.util.ArrayList;

public class classDef extends defNode {
    public String nam;
    public ArrayList<varDefSigStmt> varLis = new ArrayList<>(); // ?
    public ArrayList<funDef> funLis = new ArrayList<>();
    public funDef constructor = null;

    public classType clsTyp = null;//@

    public classDef(String nam, position pos) {
        super(pos);
        this.nam = nam;
    }

    @Override
    public void accept(ASTVisitor vis) {
        vis.visit(this);
    }
}
