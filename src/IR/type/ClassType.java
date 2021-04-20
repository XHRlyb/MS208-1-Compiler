package IR.type;
import IR.operand.Null;
import IR.operand.Operand;
import IR.operand.Reg;
import IR.Func;
import java.util.ArrayList;

public class ClassType extends BaseType {
    public String nam;
    public int siz = 0;
    public ArrayList<Reg> vars = new ArrayList<>();
    public ArrayList<Func> funs = new ArrayList<>();
    public Func constructor = null;

    public ClassType(String nam) { this.nam = nam; }
    public void addVar(Reg a) {
        vars.add(a);
        siz += a.typ.size();
    }
    public int offs(int id) {
        int ret = 0;
        for (int i = 0; i < id; i++)
            ret += vars.get(i).typ.size();
        return ret;
    }
    public int getVid(String nam) {
        nam = this.nam + "." + nam;
        for (int i = 0; i < vars.size(); i++)
            if (vars.get(i).nam.equals(nam)) return i;
        return -1;
    }
    public Reg getVreg(String nam) {
        nam = this.nam + "."  + nam;
        for (Reg reg : vars)
            if (reg.nam.equals(nam)) return reg;
        return null;
    }

    @Override
    public int size() { return siz; }
    @Override
    public String toString() { return "%struct."+nam; }
    @Override
    public boolean equals(BaseType o) { 
        return (o instanceof ClassType && ((ClassType)o).nam.equals(nam)); 
    }
    @Override
    public Operand init() { return new Null(); }
}