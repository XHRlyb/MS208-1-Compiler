package ASM.operand;

public class Addr extends Imm {
    public String nam;

    public Addr(String nam, int val) {
        super(val);
        this.nam = nam;
    }

    @Override
    public String toString() {
        return "%" + (val > 0 ? "hi" : "lo") + "(" + nam + ")";
    }
}