package IR.operand;

import IR.type.*;

public class ConstString extends Operand {
    public String nam;
    public String val;
    public String rVal;

    public ConstString(String nam, String val) {
        super(new StringType());
        this.nam = nam;
        this.val = val;
        this.rVal = val.replace("\\\\", "\\")
            .replace("\\n", "\n").replace("\\r", "\r")
            .replace("\\t", "\t").replace("\\\"", "\"");
    }

    public String convert() {
        return val.replace("\\\\", "\\5C")
        .replace("\\n", "\\0A").replace("\\r", "\\0D")
        .replace("\\t", "\\09").replace("\\\"", "\\22");
    }

    @Override
    public String toString() {
        return "getelementptr inbounds ([ " + 
            (rVal.length() + 1) + " x i8 ], [ " + 
            (rVal.length() + 1) + " x i8 ]* @" + 
            nam + ", i32 0, i32 0)";
    }
    @Override
    public boolean equals(Operand o) { return this == o; }
}