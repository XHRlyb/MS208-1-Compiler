package Frontend;

import AST.*;
import AST.statement.*;
import AST.expression.*;
import AST.declaration.*;
import Parser.MxBaseVisitor;
import Parser.MxParser;
import Util.error.syntaxError;
import Util.position;
import org.antlr.v4.runtime.ParserRuleContext;
import java.util.ArrayList;

public class ASTBuilder extends MxBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        programNode ans = new programNode(new position(ctx));
        if (ctx.subprogram() != null) {
            for (ParserRuleContext x : ctx.subprogram()) {
                ASTNode t = visit(x);
                if (t instanceof varDefStmt) {
                    ans.body.addAll(((varDefStmt)t).varLis);
                } else {
                    ans.body.add(t);
                }
            }
        }
        return ans;
    }

    @Override
    public ASTNode visitSubprogram(MxParser.SubprogramContext ctx) {
        if (ctx.funDef() != null) return visit(ctx.funDef());
        else if (ctx.varDef() != null) return visit(ctx.varDef());
        else return visit(ctx.classDef());
    }

    @Override
    public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
        varDefStmt ans = new varDefStmt(new position(ctx));
        typeNode type = (typeNode) visit(ctx.type());
        for (ParserRuleContext x : ctx.varDefSig()) {
            varDefSigStmt t = (varDefSigStmt) visit(x);
            t.typ = type;
            ans.varLis.add(t);
        }
        return ans;
    }

    @Override
    public ASTNode visitVarDefSig(MxParser.VarDefSigContext ctx) {
        return new varDefSigStmt(ctx.Identifier().getText(), ctx.expression() != null ? (exprNode) visit(ctx.expression()) : null, new position(ctx));
    }

    @Override
    public ASTNode visitFunDef(MxParser.FunDefContext ctx) {
        return new funDef(ctx.Identifier().getText(), ctx.returnType() != null ? (typeNode) visit(ctx.returnType()) : null, (blockStmt) visit(ctx.block()), ctx.paraLis() != null ? ((varDefStmt) visit(ctx.paraLis())).varLis : new ArrayList<>(), new position(ctx));
    }

    @Override
    public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
        classDef ans = new classDef(ctx.Identifier().getText(), new position(ctx));
        if (ctx.varDef() != null) {
            for (ParserRuleContext x : ctx.varDef()) {
                varDefStmt t = (varDefStmt) visit(x);
                ans.varLis.addAll(t.varLis);
            }
        }
        if (ctx.funDef() != null) {
            for (ParserRuleContext x : ctx.funDef()) {
                funDef t = (funDef) visit(x);
                if (t.typ == null) ans.constructor = t;
                else ans.funLis.add(t);
            }
        }
        return ans;
    }

    @Override
    public ASTNode visitParaLis(MxParser.ParaLisContext ctx) {
        varDefStmt ans = new varDefStmt(new position(ctx));
        for (ParserRuleContext x : ctx.para()) {
            varDefSigStmt t = (varDefSigStmt) visit(x);
            ans.varLis.add(t);
        }
        return ans;
    }

    @Override
    public ASTNode visitPara(MxParser.ParaContext ctx) {
        varDefSigStmt ans = new varDefSigStmt(ctx.Identifier().getText(), null, new position(ctx));
        ans.typ = (typeNode) visit(ctx.type());
        return ans;
    }

    @Override
    public ASTNode visitType(MxParser.TypeContext ctx) {
        return new typeNode(ctx.basType().getText(), (ctx.getChildCount() - 1) / 2, new position(ctx));
    }

    @Override
    public ASTNode visitBasType(MxParser.BasTypeContext ctx) {
        return new typeNode(ctx.getText(), 0, new position(ctx));
    }

    @Override
    public ASTNode visitReturnType(MxParser.ReturnTypeContext ctx) {
        if (ctx.type() != null) return visit(ctx.type());
        else return new typeNode(ctx.Void().getText(), 0, new position(ctx));
    }

    @Override
    public ASTNode visitBlock(MxParser.BlockContext ctx) {
        blockStmt ans = new blockStmt(new position(ctx));
        if (ctx.statement() != null) {
            for (ParserRuleContext x : ctx.statement()) {
                stmtNode t = (stmtNode) visit(x);
                ans.stmtLis.add(t);
            }
        }
        return ans;
    }

    @Override
    public ASTNode visitPrimExp(MxParser.PrimExpContext ctx) {
        if (ctx.expression() != null) return visit(ctx.expression());
        else if (ctx.This() != null) return new thisExpr(new position(ctx));
        else if (ctx.Identifier() != null) return new varExpr(ctx.Identifier().getText(), new position(ctx));
        else return visit(ctx.literal());
    }

    @Override
    public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
        if (ctx.IntLiteral() != null)
            return new intLiteral(Integer.parseInt(ctx.IntLiteral().getText()), new position(ctx));
        else if (ctx.BoolLiteral() != null)
            return new boolLiteral(Boolean.parseBoolean(ctx.BoolLiteral().getText()), new position(ctx));
        else if (ctx.StringLiteral() != null) return new stringLiteral(ctx.StringLiteral().getText(), new position(ctx));
        else return new nullLiteral(new position(ctx));
    }

    @Override
    public ASTNode visitErrorCreator(MxParser.ErrorCreatorContext ctx) {
        throw new syntaxError("ErrorCreator", new position(ctx));
    }

    @Override
    public ASTNode visitArrayCreator(MxParser.ArrayCreatorContext ctx) {
        ArrayList<exprNode> exprList = new ArrayList<>();
        for (ParserRuleContext x : ctx.expression()) {
            exprList.add((exprNode) visit(x));
        }
        return new newExpr((typeNode) visit(ctx.basType()), (ctx.getChildCount() - ctx.expression().size() - 1) / 2, exprList, new position(ctx));
    }

    @Override
    public ASTNode visitClassCreator(MxParser.ClassCreatorContext ctx) {
        return new newExpr((typeNode) visit(ctx.basType()), 0, null, new position(ctx));
    }

    @Override
    public ASTNode visitBasicCreator(MxParser.BasicCreatorContext ctx) {
        return new newExpr((typeNode) visit(ctx.basType()), 0, null, new position(ctx));
    }

    @Override
    public ASTNode visitBlockStmt(MxParser.BlockStmtContext ctx) {
        return visit(ctx.block());
    }

    @Override
    public ASTNode visitVarDefStmt(MxParser.VarDefStmtContext ctx) {
        return visit(ctx.varDef());
    }

    @Override
    public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
        return new ifStmt((exprNode) visit(ctx.expression()), (stmtNode) visit(ctx.tStmt), ctx.fStmt != null ? (stmtNode) visit(ctx.fStmt) : null, new position(ctx));
    }

    @Override
    public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
        return new whileStmt((exprNode) visit(ctx.expression()), (stmtNode) visit(ctx.statement()), new position(ctx));
    }

    @Override
    public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
        return new forStmt(ctx.ini != null ? (exprNode) visit(ctx.ini) : null, ctx.cond != null ? (exprNode) visit(ctx.cond) : null, ctx.inc != null ? (exprNode) visit(ctx.inc) : null, (stmtNode) visit(ctx.statement()), new position(ctx));
    }

    @Override
    public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
        return new breakStmt(new position(ctx));
    }

    @Override
    public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
        return new continueStmt(new position(ctx));
    }

    @Override
    public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
        return new returnStmt(ctx.expression() != null ? (exprNode) visit(ctx.expression()) : null, new position(ctx));
    }

    @Override
    public ASTNode visitExprStmt(MxParser.ExprStmtContext ctx) {
        return new exprStmt((exprNode) visit(ctx.expression()), new position(ctx));
    }

    @Override
    public ASTNode visitEmptyStmt(MxParser.EmptyStmtContext ctx) {
        return new emptyStmt(new position(ctx));
    }

    @Override
    public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
        return visit(ctx.creator());
    }

    @Override
    public ASTNode visitPrefixExpr(MxParser.PrefixExprContext ctx) {
        return new prefixExpr((exprNode) visit(ctx.expression()), ctx.op.getText(), new position(ctx));
    }

    @Override
    public ASTNode visitSubscriptExpr(MxParser.SubscriptExprContext ctx) {
        return new subscriptExpr((exprNode) visit(ctx.bas), (exprNode) visit(ctx.offs), new position(ctx));
    }

    @Override
    public ASTNode visitMemberExpr(MxParser.MemberExprContext ctx) {
        return new memberExpr((exprNode) visit(ctx.expression()), ctx.Identifier().getText(), new position(ctx));
    }

    @Override
    public ASTNode visitSuffixExpr(MxParser.SuffixExprContext ctx) {
        return new suffixExpr((exprNode) visit(ctx.expression()), ctx.op.getText(), new position(ctx));
    }

    @Override
    public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
        return visit(ctx.primExp());
    }

    @Override
    public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        return new binaryExpr((exprNode) visit(ctx.src1), (exprNode) visit(ctx.src2), ctx.op.getText(), new position(ctx));
    }

    @Override
    public ASTNode visitFunCallExpr(MxParser.FunCallExprContext ctx) {
        exprNode base = (exprNode) visit(ctx.expression());
        if (base instanceof memberExpr) {
            ((memberExpr) base).isFun = true;
            base.isAsg = false;
        }
        return new funCallExpr(base, ctx.expressionLis() != null ? (exprList) visit(ctx.expressionLis()) : new exprList(new position(ctx)), new position(ctx));
    }

    @Override
    public ASTNode visitExpressionLis(MxParser.ExpressionLisContext ctx) {
        exprList ans = new exprList(new position(ctx));
        for (ParserRuleContext x : ctx.expression()) {
            ans.params.add((exprNode) visit(x));
        }
        return ans;
    }
}