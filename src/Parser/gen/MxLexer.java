// Generated from D:/mine/¿Î³Ì/±àÒëÆ÷/mine/src/Parser\Mx.g4 by ANTLR 4.9
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Int=1, Bool=2, String=3, Null=4, Void=5, True=6, False=7, If=8, Else=9, 
		For=10, While=11, Break=12, Continue=13, Return=14, New=15, Class=16, 
		This=17, Plu=18, Sub=19, Mul=20, Div=21, Mod=22, Gt=23, Lt=24, Ge=25, 
		Le=26, Eq=27, Neq=28, AndAnd=29, OrOr=30, Not=31, RShift=32, LShift=33, 
		And=34, Or=35, Xor=36, Conty=37, Assign=38, PluPlu=39, SubSub=40, Dot=41, 
		LParen=42, RParen=43, LBracket=44, RBracket=45, LBrace=46, RBrace=47, 
		Question=48, Colon=49, Semi=50, Comma=51, StringLiteral=52, Identifier=53, 
		Integer=54, Whitespace=55, Newline=56, LineComment=57;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Int", "Bool", "String", "Null", "Void", "True", "False", "If", "Else", 
			"For", "While", "Break", "Continue", "Return", "New", "Class", "This", 
			"Plu", "Sub", "Mul", "Div", "Mod", "Gt", "Lt", "Ge", "Le", "Eq", "Neq", 
			"AndAnd", "OrOr", "Not", "RShift", "LShift", "And", "Or", "Xor", "Conty", 
			"Assign", "PluPlu", "SubSub", "Dot", "LParen", "RParen", "LBracket", 
			"RBracket", "LBrace", "RBrace", "Question", "Colon", "Semi", "Comma", 
			"StringLiteral", "SChar", "Identifier", "Integer", "Whitespace", "Newline", 
			"LineComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'int'", "'bool'", "'string'", "'null'", "'void'", "'true'", "'false'", 
			"'if'", "'else'", "'for'", "'while'", "'break'", "'continue'", "'return'", 
			"'new'", "'class'", "'this'", "'+'", "'-'", "'*'", "'/'", "'%'", "'>'", 
			"'<'", "'>='", "'<='", "'=='", "'!='", "'&&'", "'||'", "'!'", "'>>'", 
			"'<<'", "'&'", "'|'", "'^'", "'~'", "'='", "'++'", "'--'", "'.'", "'('", 
			"')'", "'['", "']'", "'{'", "'}'", "'?'", "':'", "';'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Int", "Bool", "String", "Null", "Void", "True", "False", "If", 
			"Else", "For", "While", "Break", "Continue", "Return", "New", "Class", 
			"This", "Plu", "Sub", "Mul", "Div", "Mod", "Gt", "Lt", "Ge", "Le", "Eq", 
			"Neq", "AndAnd", "OrOr", "Not", "RShift", "LShift", "And", "Or", "Xor", 
			"Conty", "Assign", "PluPlu", "SubSub", "Dot", "LParen", "RParen", "LBracket", 
			"RBracket", "LBrace", "RBrace", "Question", "Colon", "Semi", "Comma", 
			"StringLiteral", "Identifier", "Integer", "Whitespace", "Newline", "LineComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MxLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Mx.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2;\u015f\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\3\2\3\2\3\2"+
		"\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33"+
		"\3\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3!"+
		"\3!\3!\3\"\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3(\3)\3)\3)\3"+
		"*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63"+
		"\3\63\3\64\3\64\3\65\3\65\7\65\u0124\n\65\f\65\16\65\u0127\13\65\3\65"+
		"\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\5\66\u0132\n\66\3\67\3\67\7\67"+
		"\u0136\n\67\f\67\16\67\u0139\13\67\38\38\78\u013d\n8\f8\168\u0140\138"+
		"\38\58\u0143\n8\39\69\u0146\n9\r9\169\u0147\39\39\3:\3:\5:\u014e\n:\3"+
		":\5:\u0151\n:\3:\3:\3;\3;\3;\3;\7;\u0159\n;\f;\16;\u015c\13;\3;\3;\2\2"+
		"<\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37"+
		"= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\2m\67"+
		"o8q9s:u;\3\2\t\6\2\f\f\17\17$$^^\4\2C\\c|\6\2\62;C\\aac|\3\2\63;\3\2\62"+
		";\4\2\13\13\"\"\4\2\f\f\17\17\2\u0168\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3"+
		"\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2"+
		"\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2"+
		"[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3"+
		"\2\2\2\2i\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2"+
		"\2\3w\3\2\2\2\5{\3\2\2\2\7\u0080\3\2\2\2\t\u0087\3\2\2\2\13\u008c\3\2"+
		"\2\2\r\u0091\3\2\2\2\17\u0096\3\2\2\2\21\u009c\3\2\2\2\23\u009f\3\2\2"+
		"\2\25\u00a4\3\2\2\2\27\u00a8\3\2\2\2\31\u00ae\3\2\2\2\33\u00b4\3\2\2\2"+
		"\35\u00bd\3\2\2\2\37\u00c4\3\2\2\2!\u00c8\3\2\2\2#\u00ce\3\2\2\2%\u00d3"+
		"\3\2\2\2\'\u00d5\3\2\2\2)\u00d7\3\2\2\2+\u00d9\3\2\2\2-\u00db\3\2\2\2"+
		"/\u00dd\3\2\2\2\61\u00df\3\2\2\2\63\u00e1\3\2\2\2\65\u00e4\3\2\2\2\67"+
		"\u00e7\3\2\2\29\u00ea\3\2\2\2;\u00ed\3\2\2\2=\u00f0\3\2\2\2?\u00f3\3\2"+
		"\2\2A\u00f5\3\2\2\2C\u00f8\3\2\2\2E\u00fb\3\2\2\2G\u00fd\3\2\2\2I\u00ff"+
		"\3\2\2\2K\u0101\3\2\2\2M\u0103\3\2\2\2O\u0105\3\2\2\2Q\u0108\3\2\2\2S"+
		"\u010b\3\2\2\2U\u010d\3\2\2\2W\u010f\3\2\2\2Y\u0111\3\2\2\2[\u0113\3\2"+
		"\2\2]\u0115\3\2\2\2_\u0117\3\2\2\2a\u0119\3\2\2\2c\u011b\3\2\2\2e\u011d"+
		"\3\2\2\2g\u011f\3\2\2\2i\u0121\3\2\2\2k\u0131\3\2\2\2m\u0133\3\2\2\2o"+
		"\u0142\3\2\2\2q\u0145\3\2\2\2s\u0150\3\2\2\2u\u0154\3\2\2\2wx\7k\2\2x"+
		"y\7p\2\2yz\7v\2\2z\4\3\2\2\2{|\7d\2\2|}\7q\2\2}~\7q\2\2~\177\7n\2\2\177"+
		"\6\3\2\2\2\u0080\u0081\7u\2\2\u0081\u0082\7v\2\2\u0082\u0083\7t\2\2\u0083"+
		"\u0084\7k\2\2\u0084\u0085\7p\2\2\u0085\u0086\7i\2\2\u0086\b\3\2\2\2\u0087"+
		"\u0088\7p\2\2\u0088\u0089\7w\2\2\u0089\u008a\7n\2\2\u008a\u008b\7n\2\2"+
		"\u008b\n\3\2\2\2\u008c\u008d\7x\2\2\u008d\u008e\7q\2\2\u008e\u008f\7k"+
		"\2\2\u008f\u0090\7f\2\2\u0090\f\3\2\2\2\u0091\u0092\7v\2\2\u0092\u0093"+
		"\7t\2\2\u0093\u0094\7w\2\2\u0094\u0095\7g\2\2\u0095\16\3\2\2\2\u0096\u0097"+
		"\7h\2\2\u0097\u0098\7c\2\2\u0098\u0099\7n\2\2\u0099\u009a\7u\2\2\u009a"+
		"\u009b\7g\2\2\u009b\20\3\2\2\2\u009c\u009d\7k\2\2\u009d\u009e\7h\2\2\u009e"+
		"\22\3\2\2\2\u009f\u00a0\7g\2\2\u00a0\u00a1\7n\2\2\u00a1\u00a2\7u\2\2\u00a2"+
		"\u00a3\7g\2\2\u00a3\24\3\2\2\2\u00a4\u00a5\7h\2\2\u00a5\u00a6\7q\2\2\u00a6"+
		"\u00a7\7t\2\2\u00a7\26\3\2\2\2\u00a8\u00a9\7y\2\2\u00a9\u00aa\7j\2\2\u00aa"+
		"\u00ab\7k\2\2\u00ab\u00ac\7n\2\2\u00ac\u00ad\7g\2\2\u00ad\30\3\2\2\2\u00ae"+
		"\u00af\7d\2\2\u00af\u00b0\7t\2\2\u00b0\u00b1\7g\2\2\u00b1\u00b2\7c\2\2"+
		"\u00b2\u00b3\7m\2\2\u00b3\32\3\2\2\2\u00b4\u00b5\7e\2\2\u00b5\u00b6\7"+
		"q\2\2\u00b6\u00b7\7p\2\2\u00b7\u00b8\7v\2\2\u00b8\u00b9\7k\2\2\u00b9\u00ba"+
		"\7p\2\2\u00ba\u00bb\7w\2\2\u00bb\u00bc\7g\2\2\u00bc\34\3\2\2\2\u00bd\u00be"+
		"\7t\2\2\u00be\u00bf\7g\2\2\u00bf\u00c0\7v\2\2\u00c0\u00c1\7w\2\2\u00c1"+
		"\u00c2\7t\2\2\u00c2\u00c3\7p\2\2\u00c3\36\3\2\2\2\u00c4\u00c5\7p\2\2\u00c5"+
		"\u00c6\7g\2\2\u00c6\u00c7\7y\2\2\u00c7 \3\2\2\2\u00c8\u00c9\7e\2\2\u00c9"+
		"\u00ca\7n\2\2\u00ca\u00cb\7c\2\2\u00cb\u00cc\7u\2\2\u00cc\u00cd\7u\2\2"+
		"\u00cd\"\3\2\2\2\u00ce\u00cf\7v\2\2\u00cf\u00d0\7j\2\2\u00d0\u00d1\7k"+
		"\2\2\u00d1\u00d2\7u\2\2\u00d2$\3\2\2\2\u00d3\u00d4\7-\2\2\u00d4&\3\2\2"+
		"\2\u00d5\u00d6\7/\2\2\u00d6(\3\2\2\2\u00d7\u00d8\7,\2\2\u00d8*\3\2\2\2"+
		"\u00d9\u00da\7\61\2\2\u00da,\3\2\2\2\u00db\u00dc\7\'\2\2\u00dc.\3\2\2"+
		"\2\u00dd\u00de\7@\2\2\u00de\60\3\2\2\2\u00df\u00e0\7>\2\2\u00e0\62\3\2"+
		"\2\2\u00e1\u00e2\7@\2\2\u00e2\u00e3\7?\2\2\u00e3\64\3\2\2\2\u00e4\u00e5"+
		"\7>\2\2\u00e5\u00e6\7?\2\2\u00e6\66\3\2\2\2\u00e7\u00e8\7?\2\2\u00e8\u00e9"+
		"\7?\2\2\u00e98\3\2\2\2\u00ea\u00eb\7#\2\2\u00eb\u00ec\7?\2\2\u00ec:\3"+
		"\2\2\2\u00ed\u00ee\7(\2\2\u00ee\u00ef\7(\2\2\u00ef<\3\2\2\2\u00f0\u00f1"+
		"\7~\2\2\u00f1\u00f2\7~\2\2\u00f2>\3\2\2\2\u00f3\u00f4\7#\2\2\u00f4@\3"+
		"\2\2\2\u00f5\u00f6\7@\2\2\u00f6\u00f7\7@\2\2\u00f7B\3\2\2\2\u00f8\u00f9"+
		"\7>\2\2\u00f9\u00fa\7>\2\2\u00faD\3\2\2\2\u00fb\u00fc\7(\2\2\u00fcF\3"+
		"\2\2\2\u00fd\u00fe\7~\2\2\u00feH\3\2\2\2\u00ff\u0100\7`\2\2\u0100J\3\2"+
		"\2\2\u0101\u0102\7\u0080\2\2\u0102L\3\2\2\2\u0103\u0104\7?\2\2\u0104N"+
		"\3\2\2\2\u0105\u0106\7-\2\2\u0106\u0107\7-\2\2\u0107P\3\2\2\2\u0108\u0109"+
		"\7/\2\2\u0109\u010a\7/\2\2\u010aR\3\2\2\2\u010b\u010c\7\60\2\2\u010cT"+
		"\3\2\2\2\u010d\u010e\7*\2\2\u010eV\3\2\2\2\u010f\u0110\7+\2\2\u0110X\3"+
		"\2\2\2\u0111\u0112\7]\2\2\u0112Z\3\2\2\2\u0113\u0114\7_\2\2\u0114\\\3"+
		"\2\2\2\u0115\u0116\7}\2\2\u0116^\3\2\2\2\u0117\u0118\7\177\2\2\u0118`"+
		"\3\2\2\2\u0119\u011a\7A\2\2\u011ab\3\2\2\2\u011b\u011c\7<\2\2\u011cd\3"+
		"\2\2\2\u011d\u011e\7=\2\2\u011ef\3\2\2\2\u011f\u0120\7.\2\2\u0120h\3\2"+
		"\2\2\u0121\u0125\7$\2\2\u0122\u0124\5k\66\2\u0123\u0122\3\2\2\2\u0124"+
		"\u0127\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0128\3\2"+
		"\2\2\u0127\u0125\3\2\2\2\u0128\u0129\7$\2\2\u0129j\3\2\2\2\u012a\u0132"+
		"\n\2\2\2\u012b\u012c\7^\2\2\u012c\u0132\7p\2\2\u012d\u012e\7^\2\2\u012e"+
		"\u0132\7^\2\2\u012f\u0130\7^\2\2\u0130\u0132\7$\2\2\u0131\u012a\3\2\2"+
		"\2\u0131\u012b\3\2\2\2\u0131\u012d\3\2\2\2\u0131\u012f\3\2\2\2\u0132l"+
		"\3\2\2\2\u0133\u0137\t\3\2\2\u0134\u0136\t\4\2\2\u0135\u0134\3\2\2\2\u0136"+
		"\u0139\3\2\2\2\u0137\u0135\3\2\2\2\u0137\u0138\3\2\2\2\u0138n\3\2\2\2"+
		"\u0139\u0137\3\2\2\2\u013a\u013e\t\5\2\2\u013b\u013d\t\6\2\2\u013c\u013b"+
		"\3\2\2\2\u013d\u0140\3\2\2\2\u013e\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f"+
		"\u0143\3\2\2\2\u0140\u013e\3\2\2\2\u0141\u0143\7\62\2\2\u0142\u013a\3"+
		"\2\2\2\u0142\u0141\3\2\2\2\u0143p\3\2\2\2\u0144\u0146\t\7\2\2\u0145\u0144"+
		"\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0145\3\2\2\2\u0147\u0148\3\2\2\2\u0148"+
		"\u0149\3\2\2\2\u0149\u014a\b9\2\2\u014ar\3\2\2\2\u014b\u014d\7\17\2\2"+
		"\u014c\u014e\7\f\2\2\u014d\u014c\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u0151"+
		"\3\2\2\2\u014f\u0151\7\f\2\2\u0150\u014b\3\2\2\2\u0150\u014f\3\2\2\2\u0151"+
		"\u0152\3\2\2\2\u0152\u0153\b:\2\2\u0153t\3\2\2\2\u0154\u0155\7\61\2\2"+
		"\u0155\u0156\7\61\2\2\u0156\u015a\3\2\2\2\u0157\u0159\n\b\2\2\u0158\u0157"+
		"\3\2\2\2\u0159\u015c\3\2\2\2\u015a\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b"+
		"\u015d\3\2\2\2\u015c\u015a\3\2\2\2\u015d\u015e\b;\2\2\u015ev\3\2\2\2\f"+
		"\2\u0125\u0131\u0137\u013e\u0142\u0147\u014d\u0150\u015a\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}