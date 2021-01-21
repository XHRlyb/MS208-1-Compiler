package Util.symbol;

public class arrayType extends Type {
    public Type typ;
    public int dim;

    public arrayType(Type typ, int dim) {
        this.typ = typ;
        this.dim = dim;
    }

    @Override
    public boolean sameType(Type o) {
        return o.isNull() || (dim == ((arrayType)o).dim &&
                        typ.sameType(((arrayType)o).typ) );
    }
}