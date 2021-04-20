package ASM;

import ASM.operand.*;
import java.util.ArrayList;

public class asmFunc {
    public String nam;
    public asmBlock begBlk = null, endBlk = null;
    public ArrayList<asmBlock> blks = new ArrayList<>();
    public ArrayList<asmReg> params = new ArrayList<>();
    public ArrayList<Vreg> callSaveVregs = new ArrayList<>();
    public Vreg raSaveVreg = null;

    public asmFunc(String nam) { this.nam = nam; }

    public String toString() { return nam; }
}