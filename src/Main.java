import AST.programNode;
import Codegen.toASM;
import Frontend.ASTBuilder;
import Frontend.SemanticChecker;
import Frontend.SymbolCollector;
import Frontend.TypeCollector;
import Parser.MxLexer;
import Parser.MxParser;
import Util.ErrorListener;
import Util.symbol.Scope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        //String nam = "test.mx";
        //InputStream inp = new FileInputStream(nam);

        InputStream inp = System.in;

        try {
            programNode ASTRoot;

            MxLexer lexer = new MxLexer(CharStreams.fromStream(inp));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new ErrorListener());

            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new ErrorListener());

            ParseTree parseTreeRoot = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ASTRoot = (programNode) astBuilder.visit(parseTreeRoot);

            Scope global = new Scope(null, "");
            new SymbolCollector(global).visit(ASTRoot);
            new TypeCollector(global).visit(ASTRoot);
            global.varMap.clear();
            global.varVidMap.clear();
            new SemanticChecker(global).visit(ASTRoot);
            new toASM(global).visit(ASTRoot);

        } catch (Error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}