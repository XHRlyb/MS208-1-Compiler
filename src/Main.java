import java.io.File;
import java.io.PrintStream;
import Backend.*;
import IR.*;
import ASM.ASM;
import AST.programNode;
import Optim.*;
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

import java.io.FileInputStream;
import java.io.InputStream;
public class Main {
    public static void main(String[] args) throws Exception {
        //String nam = "test.mx";
        //InputStream inp = new FileInputStream(nam);
        InputStream inp = System.in;

        File file = new File("output.s");
        PrintStream stream = new PrintStream(file);
        System.setOut(stream);

        boolean onlySemantic = false, codegen = false;
        for (String arg : args) {
            switch (arg) {
                case "-semantic":
                    onlySemantic = true;
                    break;
                case "-codegen":
                    codegen = true;
                    break;
                case "-test":
                    String name = "test1.mx";
                    inp = new FileInputStream(name);
                    break;
            }
        }

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

            Scope global = new Scope(null);//, "", new RegVidAlloc());
            new SymbolCollector(global).visit(ASTRoot);
            new TypeCollector(global).visit(ASTRoot);
            global.varMap.clear();
            new SemanticChecker(global).visit(ASTRoot);
            if (!codegen) return;
            IR ir = new IR();
            new IRBuilder(ir).visit(ASTRoot);
            new IRBuilder(ir).work();
            new Optim(ir).work();
            new PhiResol(ir).work();
            //new IRPrinter(System.out, ir).outp();
            ASM asm = new ASM();
            new ASMBuilder(ir, asm).work();
            new RegAlc(asm).work();
            new ASMPrinter(System.out, asm).outp(); //*/
        } catch (Error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}