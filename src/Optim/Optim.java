package Optim;

import IR.*;
import Backend.*;

public class Optim {
    public IR ir;

    public Optim(IR ir) { this.ir = ir; }

    public void work() {
        for (int i = 0; i < 10; i++)  {
            new CleanUp(ir).work();
            new SCCP(ir).work();
            new CleanUp(ir).work();
            new CSE(ir).work();
            new CleanUp(ir).work();
            new ADCE(ir).work();
            new CleanUp(ir).work();
            new Inline(ir).work();
            new CleanUp(ir).work();
            new LICM(ir).work();
            new CleanUp(ir).work();
            new ImmInst(ir).work();
            new CleanUp(ir).work();
            new gVarAcc(ir).work();//*/
        }
        //new IRPrinter(System.out, ir).outp();
        new CleanUp(ir).work();
    }
}