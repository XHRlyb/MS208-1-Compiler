package Backend;

import IR.*;
import IR.type.*;
import IR.inst.*;
import IR.operand.*;
import java.io.PrintStream;

public class IRPrinter {
    public PrintStream outp;
    public IR ir;
    public String builtin = """
        declare i8* @malloc(i32)
        declare void @print(i8*)
        declare void @println(i8*)
        declare void @printInt(i32)
        declare void @printlnInt(i32)
        declare i8* @getString()
        declare i32 @getInt()
        declare i8* @toString(i32)
        declare i32 @str_length(i8*)
        declare i8* @str_substring(i8*, i32, i32)
        declare i32 @str_parseInt(i8*)
        declare i32 @str_ord(i8*, i32)
        declare i8* @str_add(i8*, i8*)
        declare i1 @str_lt(i8*, i8*)
        declare i1 @str_gt(i8*, i8*)
        declare i1 @str_le(i8*, i8*)
        declare i1 @str_ge(i8*, i8*)
        declare i1 @str_eq(i8*, i8*)
        declare i1 @str_ne(i8*, i8*)
        """;
    
    public IRPrinter(PrintStream outp, IR ir) {
        this.outp = outp; this.ir = ir;
    }

    public void outpBlk(Block blk) {
        outp.println(blk.nam + ":");
        blk.insts.forEach(x -> outp.println("  " + x.toString()));
    }  
    public void outpCls(ClassType cls) {
        outp.print(cls.toString() + " = type {");
        for (int i = 0; i < cls.vars.size(); i++) {
            outp.print(cls.vars.get(i).typ.toString());
            if (i != cls.vars.size() - 1) outp.print(", ");
        }
        outp.println("}");
    }
    public void outpFun(Func fun) {
        outp.print("define " + fun.rettyp.toString() + " " + fun.toString() + "(");
        for (int i = 0; i < fun.params.size(); i++) {
            outp.print(fun.params.get(i).typ.toString() + " " + fun.params.get(i).toString());
            if (i != fun.params.size() - 1) outp.print(", ");
        }
        outp.println("){");
        fun.blks.forEach(this::outpBlk);
        outp.println("}");
    }
    public void outpGVar(Reg x) {
        outp.println(x.toString() + " = global " + ((Pointer)x.typ).typ.toString() + " zeroinitializer");
    }
    public void outpCStr(ConstString x) {
        outp.print("@" + x.nam + " = private unnamed_addr constant [");
        outp.println((x.rVal.length() + 1) + "x i8]c\"" + x.convert() + "\\00\", align 1");
    }
    public void outp() {
        outp.print(builtin);
        ir.mxCls.forEach((s, x) -> outpCls(x));
        ir.gVars.forEach((s, x) -> outpGVar(x));
        ir.cStrs.forEach((s, x) -> outpCStr(x));
        ir.funs.forEach((s, x) -> outpFun(x));
    }
}