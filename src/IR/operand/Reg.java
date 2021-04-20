package IR.operand;

import IR.inst.*;
import IR.type.*;
import java.util.ArrayList;
import java.util.Stack;

public class Reg extends Operand {
    public String nam;
    public boolean glb = false;
    public boolean consptr = false;
    public ArrayList<Inst> assign = new ArrayList<>();
    public Stack<Reg> renams = new Stack<>();
    public int rnmid;
    public boolean used = false;

    public Reg(BaseType typ, String nam) {
        super(typ); this.nam = nam;
    }

    @Override
    public String toString() { return (glb?"@":"%")+nam; }
    @Override
    public boolean equals(Operand o) { return this == o; }
}
