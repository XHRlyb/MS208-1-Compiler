package Util;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class position {
    private int row, col;

    public position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public position(Token tok) {
        this.row = tok.getLine();
        this.col = tok.getCharPositionInLine();
    }
    public position(TerminalNode ter) { this(ter.getSymbol()); }
    public position(ParserRuleContext ctx) { this(ctx.getStart()); }

    public int row() { return row; }
    public int col() { return col; }

    public String toString() { return row + "," + col; }
}