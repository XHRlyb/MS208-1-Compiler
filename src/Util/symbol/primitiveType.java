package Util.symbol;

public class primitiveType extends Type {
    public String nam;

    public primitiveType(String nam) { this.nam = nam; }

    @Override
    public boolean isNull() { return nam.equals("null"); }
    @Override
    public boolean isVoid() { return nam.equals("void"); }
    @Override
    public boolean isBool() { return nam.equals("bool"); }
    @Override
    public boolean isInt() { return nam.equals("int"); }
    @Override
    public boolean isString() { return nam.equals("string"); }

    @Override
    public boolean sameType(Type o) {
        return     ( isNull() &&
                     (o instanceof arrayType || o instanceof classType) )
                || ( (o instanceof primitiveType) &&
                     (nam.equals(((primitiveType)o).nam)) );
    }
}