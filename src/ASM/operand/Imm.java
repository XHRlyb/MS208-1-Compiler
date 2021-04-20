package ASM.operand;

public class Imm extends asmOperand {
    public int val;
    public boolean inPara = false;

    public Imm(int val) {
        this.val = val;
    }

    public Imm(int val, boolean inPara) {
        this.val = val;
        this.inPara = inPara;
    }

    @Override
    public String toString() { return Integer.toString(val); }
}
