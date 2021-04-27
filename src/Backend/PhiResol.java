package Backend;

import IR.*;
import IR.inst.*;
import IR.operand.*;
import java.util.Map;
import java.util.Iterator;

public class PhiResol {
    public IR ir;

    public PhiResol(IR ir) { this.ir = ir; }

    public void resol(Func fun) {
        for (int t = 0; t < fun.blks.size(); t++) {
            Block b = fun.blks.get(t);
            boolean cond = false;
            for (int i = 0; i < b.insts.size(); i++) {
                Inst ins = b.insts.get(i);
                if (ins instanceof Phi) { cond = true; break; }
            }
            if (!cond) continue;
            for (int i = 0; i < b.pre.size(); i++) {
                Block x = b.pre.get(i);
                if (x.nex.size() <= 1) continue;
                Block tmp = new Block(0);
                tmp.nam = "block." + fun.blks.size();
                fun.blks.add(tmp);
                tmp.addIns(new Jump(tmp, b));
                tmp.termed = true;
                tmp.nex.add(b);
                b.pre.set(i, tmp);
                b.replacePre(x, tmp);
                for (int j = 0; j < x.nex.size(); j++)
                    if (x.nex.get(j) == b)
                        x.nex.set(j, tmp);
                x.replaceNex(b, tmp);
            }
            for (int i = 0; i < b.insts.size(); i++) {
                Inst ins = b.insts.get(i);
                if (ins instanceof Phi) {
                    for (int j = 0; j < ((Phi)ins).blks.size(); j++)
                        ((Phi)ins).blks.get(j).pCpy.put(ins.reg, ((Phi)ins).vals.get(j));
                    b.insts.remove(i);
                    i--;
                }
            }
        }
        for (int t = 0; t < fun.blks.size(); t++) {
            Block b = fun.blks.get(t);
            while (!b.pCpy.isEmpty()) {
                boolean cond = true;
                while (cond) {
                    cond = false;
                    Iterator<Map.Entry<Reg, Operand>> it = b.pCpy.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Reg, Operand> x = it.next();
                        if (!(x.getValue() instanceof Reg) || 
                            !b.pCpy.containsKey(x.getValue())) {
                                b.addInsB(new Assign(b, x.getKey(), x.getValue()));
                                it.remove();
                                cond = true;
                        }
                    }
                }
                Iterator<Map.Entry<Reg, Operand>> it = b.pCpy.entrySet().iterator();
                if (it.hasNext()) {
                    Map.Entry<Reg, Operand> x = it.next();
                    Reg tmp = new Reg(x.getKey().typ, "tmp");
                    b.addInsB(new Assign(b, tmp, x.getValue()));
                    b.pCpy.forEach((key, value) -> {
                        if (value == x.getValue())
                            b.pCpy.replace(key, tmp);
                    });
                }
            }
        }
    }
    public void work() {
        ir.funs.forEach((s, x) -> resol(x));
    }
}