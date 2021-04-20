package IR;

import IR.type.*;
import IR.operand.*;
import java.util.HashMap;
import Util.symbol.*;

public class IR {
    public HashMap<String, Func> funs = new HashMap<>();
    public HashMap<String, Reg> gVars = new HashMap<>();
    public HashMap<String, ClassType> mxCls = new HashMap<>();
    public HashMap<String, ConstString> cStrs = new HashMap<>();
    public Func mlcFun;
    public Block gVarDef;

    public IR() {
        funs.put("gVarDef", new Func("gVarDef"));
        gVarDef = funs.get("gVarDef").begBlk;
        mlcFun = new Func("my_malloc");
        mlcFun.rettyp = new Pointer(new IntType(8));
    }

    public BaseType getTyp(Type typ) {
        if (typ instanceof arrayType) {
            BaseType t = getTyp(((arrayType)typ).typ);
            for (int i = 0; i < ((arrayType)typ).dim; i++)
                t = new Pointer(t);
            return t;
        }
        if (typ instanceof classType) {
            return new Pointer(((classType)typ).clsTyp);
        }
        if (typ instanceof primitiveType) {
            if (typ.isString()) return new StringType();
            if (typ.isNull()) return new VoidType();
            if (typ.isInt()) return new IntType(32);
            if (typ.isBool()) return new BoolType();
            if (typ.isVoid()) return new VoidType();
        }
        return new VoidType();
    }
}