/*package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Inline {
    public IR ir;
    public HashMap<Func, ArrayList<Func>> edge, reEdgeF;
    public HashMap<Func, ArrayList<Call>> reEdge;

    public Inline(IR ir) { this.ir = ir; }

    public void work() {
        getEdge();
        inlineCheck();
        for (Func fun : canInline) inlineFunc(fun);
        for (Map.entry<String, Func> entry : ir.funs.entrySet()) {
            Func x = entry.getValue();
            if (edge.get(x).contains(x))
                for (int c = 0; c < 10; c++)
                    for (int i = 0; i < reEdge.get(x).size(); i++)
                        inline(reEdge.get(x).get(i), reEdgeF.get(x).get(i));
        }
    }
}*/