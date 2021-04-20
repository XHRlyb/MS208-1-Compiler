package Backend;

import IR.operand.*;
import ASM.*;
import java.io.PrintStream;

public class ASMPrinter {
    public PrintStream outp;
    public ASM asm;

    public ASMPrinter(PrintStream outp, ASM asm) {
        this.outp = outp;
        this.asm = asm;
    }

    public void outpBlk(asmBlock blk) {
        outp.println(blk.nam + ":");
        blk.insts.forEach(ins -> outp.println("\t" + ins.toString()));
    }
    public void outpFun(asmFunc fun) {
        outp.println("\t.globl\t" + fun.toString());
        outp.println("\t.type\t" + fun.toString() + ", @function");
        outp.println(fun.nam + ":");
        fun.blks.forEach(this::outpBlk);
        outp.println("\t.size\t" + fun.toString() + ", .-" + fun.toString());
    }
    public void outpGVar(Reg x) {
        outp.println("\t.globl\t" + x.nam);
        outp.println("\t.type\t" + x.nam + ", @object");
        outp.println(x.nam + ":");
        outp.println("\t.zero\t4");
        outp.println("\t.size\t" + x.nam + ", 4");
    }
    public void outpCStr(ConstString x) {
        outp.println(x.nam + ":");
        outp.println("\t.string\t\"" + x.val + "\"");
    }
    public void outp() {
        outp.println("\t.text");
        asm.funs.forEach((s, x) -> outpFun(x));
        outp.println("\t.section\t.bss");
        asm.gVars.forEach((s, x) -> outpGVar(x));
        outp.println("\t.section\t.rodata");
        asm.cStrs.forEach((s, x) -> outpCStr(x));
    }
}