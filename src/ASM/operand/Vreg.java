package ASM.operand;

public class Vreg extends asmReg {
    public String nam;
    
    public Vreg(String nam) { this.nam = nam; }

    @Override
    public String toString() { return col.toString(); }
}
