package Util.symbol;

import Util.error.semanticError;
import Util.position;
import AST.declaration.typeNode;

import java.util.HashMap;

public class Scope {
    public HashMap<String, varEntity> varMap = new HashMap<>();
    public HashMap<String, funEntity> funMap = new HashMap<>();
    public HashMap<String, Type> typMap = new HashMap<>();
    public Scope fa;

    public Scope(Scope fa) { this.fa = fa; }

    public void defVar(String nam, varEntity var, position pos) {
        if (contType(nam, true))
            throw new semanticError("duplicated with type name " + nam, pos);
        if (varMap.containsKey(nam))
            throw new semanticError("varible " + nam + " redefine", pos);
        varMap.put(nam, var);
    }
    public void defFun(String nam, funEntity fun, position pos) {
        if (contType(nam, true))
            throw new semanticError("duplicated with type name " + nam, pos);
        if (funMap.containsKey(nam))
            throw new semanticError("function " + nam + " redefine", pos);
        funMap.put(nam, fun);
    }
    public void defTyp(String nam, Type t, position pos) {
        if (typMap.containsKey(nam))
            throw new semanticError("class " + nam + " redefine", pos);
        typMap.put(nam, t);
    }

    public boolean contType(String nam, boolean up) {
        if (typMap.containsKey(nam)) return true;
        else if (fa != null && up) return fa.contType(nam, true);
        else return false;
    }


    public varEntity getVar(String nam, position pos, boolean up) {
        if (varMap.containsKey(nam)) return varMap.get(nam);
        else if (fa != null && up) return fa.getVar(nam, pos, true);
        else throw new semanticError("undefined variable " + nam, pos);
    }
    public funEntity getFun(String nam, position pos, boolean up) {
        if (funMap.containsKey(nam)) return funMap.get(nam);
        else if (fa != null && up) return fa.getFun(nam, pos, true);
        else throw new semanticError("undefined function " + nam, pos);
    }
    public Type getTyp(String nam, position pos, boolean up) {
        if (typMap.containsKey(nam)) return typMap.get(nam);
        else if (fa != null && up) return fa.getTyp(nam, pos, true);
        else throw new semanticError("undefined type " + nam, pos);
    }
    public Type getTyp(typeNode typ) {
        if (typ.dim == 0) return typMap.get(typ.typ);
        else return new arrayType(typMap.get(typ.typ), typ.dim);
    }
}