package IR;
import IR.inst.*;
import IR.type.*;
import IR.operand.*;
import java.util.HashSet;
import java.util.ArrayList;

public class Func {
    public String nam;
    public Block begBlk = new Block(0);
    public ArrayList<Block> blks = new ArrayList<>();
    public ArrayList<Reg> params = new ArrayList<>();
    public HashSet<Reg> vars = new HashSet<>();
    public BaseType rettyp = new VoidType();
    public ArrayList<Inst> retInsts = new ArrayList<>();
    public boolean inCls = false;
    public Operand clsPtr = null;

    public Func(String nam) { this.nam = nam; }

    public String toString() { return "@" + nam; }
}