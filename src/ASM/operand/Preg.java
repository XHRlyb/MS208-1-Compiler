package ASM.operand;

public class Preg extends asmReg {
    public String nam;
    
    public Preg(String nam) { this.nam = nam; }
    
    @Override
    public String toString() { return nam; }
}
