package Optim;

import IR.*;

public class Optim {
    public IR ir;

    public Optim(IR ir) { this.ir = ir; }

    public void work() {
//        for (int i = 0; i < 10; i++)  {
            new CleanUp(ir).work();
            new SCCP(ir).work();
//        }
        new CleanUp(ir).work();
    }
}