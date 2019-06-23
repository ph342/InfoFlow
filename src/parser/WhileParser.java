/* Generated By:JavaCC: Do not edit this line. WhileParser.java */
  package parser;

  import syntaxtree.*;
  import java.util.List;
  import java.util.LinkedList;
  public class WhileParser implements WhileParserConstants {
    private Token tagToken;
    private <T extends AST> T tag(T ast, Token t) {
      ast.tag(t.beginLine, t.beginColumn);
      return ast;
    }
    private <T extends AST> T tag(T ast, AST sub) {
        ast.tag(sub.getTags());
        return ast;
    }

  final public Program nt_Program() throws ParseException {
  Cmd s;
  List<Cmd> ss = new LinkedList<Cmd>();
  ProcDecl pd;
  List<ProcDecl> pds = new LinkedList<ProcDecl>();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACE:
      case IF:
      case WHILE:
      case ID:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      s = nt_Cmd();
                 ss.add(s);
    }
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PROC:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
      pd = nt_ProcDecl();
                                                     pds.add(pd);
    }
    jj_consume_token(0);
    {if (true) return new Program(ss, pds);}
    throw new Error("Missing return statement in function");
  }

  final public ProcDecl nt_ProcDecl() throws ParseException {
  Token t;
  List<Var> infs;
  List<Var> outfs;
  Cmd s;
  List<Cmd> ss = new LinkedList<Cmd>();
  Exp re;
    jj_consume_token(PROC);
    t = jj_consume_token(ID);
    jj_consume_token(LPAREN);
    infs = nt_InFormals();
    outfs = nt_OutFormals();
    jj_consume_token(RPAREN);
    jj_consume_token(LBRACE);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACE:
      case IF:
      case WHILE:
      case ID:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_3;
      }
      s = nt_Cmd();
                   ss.add(s);
    }
    jj_consume_token(RBRACE);
    {if (true) return tag(new ProcDecl(t.image, infs, outfs, ss), t);}
    throw new Error("Missing return statement in function");
  }

  final public List<Var> nt_InFormals() throws ParseException {
  List<Var> vs;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IN:
      jj_consume_token(IN);
      vs = nt_VarList();
      jj_consume_token(SEMICOLON);
                                      {if (true) return vs;}
      break;
    default:
      jj_la1[3] = jj_gen;
     {if (true) return new LinkedList<Var>();}
    }
    throw new Error("Missing return statement in function");
  }

  final public List<Var> nt_OutActuals() throws ParseException {
  List<Var> vs;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ID:
      vs = nt_VarList();
                     {if (true) return vs;}
      break;
    default:
      jj_la1[4] = jj_gen;
     {if (true) return new LinkedList<Var>();}
    }
    throw new Error("Missing return statement in function");
  }

  final public List<Var> nt_OutFormals() throws ParseException {
  List<Var> vs;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OUT:
      jj_consume_token(OUT);
      vs = nt_VarList();
                           {if (true) return vs;}
      break;
    default:
      jj_la1[5] = jj_gen;
     {if (true) return new LinkedList<Var>();}
    }
    throw new Error("Missing return statement in function");
  }

  final public List<Var> nt_VarList() throws ParseException {
  Var v;
  List<Var> fs = new LinkedList<Var>();
  Token t;
    t = jj_consume_token(ID);
      v = new Var(t.image); fs.add(tag(v, t));
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_4;
      }
      v = nt_VarRest();
                      fs.add(v);
    }
      {if (true) return fs;}
    throw new Error("Missing return statement in function");
  }

  final public Var nt_VarRest() throws ParseException {
  Token t;
    jj_consume_token(COMMA);
    t = jj_consume_token(ID);
    {if (true) return tag(new Var(t.image), t);}
    throw new Error("Missing return statement in function");
  }

  final private CmdBlock Block() throws ParseException {
  Cmd s;
  List<Cmd> ss = new LinkedList<Cmd>();
    jj_consume_token(LBRACE);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACE:
      case IF:
      case WHILE:
      case ID:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_5;
      }
      s = nt_Cmd();
                          ss.add(s);
    }
    jj_consume_token(RBRACE);
    {if (true) return new CmdBlock(ss);}
    throw new Error("Missing return statement in function");
  }

  final public Cmd nt_Cmd() throws ParseException {
  CmdBlock b;
  Cmd s, s1, s2;
  List<Exp> ais = new LinkedList<Exp>();
  List<Var> aos = new LinkedList<Var>();
  Exp e, e1, e2;
  Token t, t1;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LBRACE:
      b = Block();
      {if (true) return b;}
      break;
    case ID:
      t = jj_consume_token(ID);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ASSIGN:
        t1 = jj_consume_token(ASSIGN);
        e = nt_Exp();
        jj_consume_token(SEMICOLON);
                Var v = new Var(t.image); tag(v, t); {if (true) return tag(new CmdAssign(v, e), t1);}
        break;
      case LPAREN:
        jj_consume_token(LPAREN);
        ais = nt_ExpList();
        aos = nt_OutActuals();
        jj_consume_token(RPAREN);
        jj_consume_token(SEMICOLON);
                {if (true) return tag(new CmdCall(t.image, ais, aos), t);}
        break;
      default:
        jj_la1[8] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    case IF:
      t1 = jj_consume_token(IF);
      jj_consume_token(LPAREN);
      e = nt_Exp();
      jj_consume_token(RPAREN);
      s1 = nt_Cmd();
      jj_consume_token(ELSE);
      s2 = nt_Cmd();
      {if (true) return tag(new CmdIf(e, s1, s2), t1);}
      break;
    case WHILE:
      t1 = jj_consume_token(WHILE);
      jj_consume_token(LPAREN);
      e = nt_Exp();
      jj_consume_token(RPAREN);
      s = nt_Cmd();
      {if (true) return tag(new CmdWhile(e, s), t1);}
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public List<Exp> nt_ExpList() throws ParseException {
  List<Exp> es;
    if (jj_2_1(2147483647)) {
      es = inActuals();
                     {if (true) return es;}
    } else {
      {if (true) return new LinkedList<Exp>();}
    }
    throw new Error("Missing return statement in function");
  }

  final public Exp nt_Exp() throws ParseException {
  Exp e1, e2;
  ExpOp.Op op;
    e1 = nt_PrimaryExp();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
    case OR:
    case LESSTHAN:
    case GREATERTHAN:
    case EQUALS:
    case DIV:
    case PLUS:
    case MINUS:
    case TIMES:
      op = nt_Op();
      e2 = nt_PrimaryExp();
          {if (true) return tag(new ExpOp(e1, op, e2), tagToken);}
      break;
    default:
      jj_la1[10] = jj_gen;

          {if (true) return e1;}
    }
    throw new Error("Missing return statement in function");
  }

  final public ExpOp.Op nt_Op() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
      tagToken = jj_consume_token(AND);
                      {if (true) return ExpOp.Op.AND;}
      break;
    case OR:
      tagToken = jj_consume_token(OR);
                     {if (true) return ExpOp.Op.OR;}
      break;
    case LESSTHAN:
      tagToken = jj_consume_token(LESSTHAN);
                           {if (true) return ExpOp.Op.LESSTHAN;}
      break;
    case GREATERTHAN:
      tagToken = jj_consume_token(GREATERTHAN);
                              {if (true) return ExpOp.Op.GREATERTHAN;}
      break;
    case EQUALS:
      tagToken = jj_consume_token(EQUALS);
                         {if (true) return ExpOp.Op.EQUALS;}
      break;
    case DIV:
      tagToken = jj_consume_token(DIV);
                      {if (true) return ExpOp.Op.DIV;}
      break;
    case PLUS:
      tagToken = jj_consume_token(PLUS);
                       {if (true) return ExpOp.Op.PLUS;}
      break;
    case MINUS:
      tagToken = jj_consume_token(MINUS);
                        {if (true) return ExpOp.Op.MINUS;}
      break;
    case TIMES:
      tagToken = jj_consume_token(TIMES);
                        {if (true) return ExpOp.Op.TIMES;}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Exp nt_PrimaryExp() throws ParseException {
  Token t, t1;
  int n;
  Exp e;
  List<Exp> es;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case POSINT:
    case NEGINT:
      n = nt_IntLit();
      {if (true) return tag(new ExpInteger(n), tagToken);}
      break;
    case TRUE:
      t = jj_consume_token(TRUE);
      {if (true) return tag(new ExpTrue(), t);}
      break;
    case FALSE:
      t = jj_consume_token(FALSE);
      {if (true) return tag(new ExpFalse(), t);}
      break;
    case ID:
      t = jj_consume_token(ID);
      {if (true) return tag(new ExpVar(tag(new Var(t.image), t)), t);}
      break;
    case NOT:
      t = jj_consume_token(NOT);
      e = nt_PrimaryExp();
      {if (true) return tag(new ExpNot(e), t);}
      break;
    case LPAREN:
      t = jj_consume_token(LPAREN);
      e = nt_Exp();
      jj_consume_token(RPAREN);
      {if (true) return e;}
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private List<Exp> inActuals() throws ParseException {
  Exp e;
  List<Exp> es = new LinkedList<Exp>();
    e = nt_Exp();
                 es.add(e);
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_6;
      }
      e = nt_ExpRest();
                                                 es.add(e);
    }
    jj_consume_token(SEMICOLON);
      {if (true) return es;}
    throw new Error("Missing return statement in function");
  }

  final public Exp nt_ExpRest() throws ParseException {
  Exp e;
    jj_consume_token(COMMA);
    e = nt_Exp();
    {if (true) return e;}
    throw new Error("Missing return statement in function");
  }

  final public int nt_IntLit() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case POSINT:
      tagToken = jj_consume_token(POSINT);
      break;
    case NEGINT:
      tagToken = jj_consume_token(NEGINT);
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                                                {if (true) return Integer.parseInt(tagToken.image);}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3R_30() {
    if (jj_scan_token(TIMES)) return true;
    return false;
  }

  private boolean jj_3R_29() {
    if (jj_scan_token(MINUS)) return true;
    return false;
  }

  private boolean jj_3R_28() {
    if (jj_scan_token(PLUS)) return true;
    return false;
  }

  private boolean jj_3R_21() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(8)) {
    jj_scanpos = xsp;
    if (jj_scan_token(9)) return true;
    }
    return false;
  }

  private boolean jj_3R_27() {
    if (jj_scan_token(DIV)) return true;
    return false;
  }

  private boolean jj_3R_26() {
    if (jj_scan_token(EQUALS)) return true;
    return false;
  }

  private boolean jj_3R_25() {
    if (jj_scan_token(GREATERTHAN)) return true;
    return false;
  }

  private boolean jj_3R_24() {
    if (jj_scan_token(LESSTHAN)) return true;
    return false;
  }

  private boolean jj_3R_23() {
    if (jj_scan_token(OR)) return true;
    return false;
  }

  private boolean jj_3R_20() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) {
    jj_scanpos = xsp;
    if (jj_3R_25()) {
    jj_scanpos = xsp;
    if (jj_3R_26()) {
    jj_scanpos = xsp;
    if (jj_3R_27()) {
    jj_scanpos = xsp;
    if (jj_3R_28()) {
    jj_scanpos = xsp;
    if (jj_3R_29()) {
    jj_scanpos = xsp;
    if (jj_3R_30()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_22() {
    if (jj_scan_token(AND)) return true;
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_12() {
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_3R_20()) return true;
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_3R_8()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_9()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(SEMICOLON)) return true;
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_3R_10()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_3R_12()) return true;
    }
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3R_19() {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_8()) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3R_18() {
    if (jj_scan_token(NOT)) return true;
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3R_17() {
    if (jj_scan_token(ID)) return true;
    return false;
  }

  private boolean jj_3R_16() {
    if (jj_scan_token(FALSE)) return true;
    return false;
  }

  private boolean jj_3R_15() {
    if (jj_scan_token(TRUE)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_3R_13()) return true;
    return false;
  }

  private boolean jj_3R_10() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_14()) {
    jj_scanpos = xsp;
    if (jj_3R_15()) {
    jj_scanpos = xsp;
    if (jj_3R_16()) {
    jj_scanpos = xsp;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) {
    jj_scanpos = xsp;
    if (jj_3R_19()) return true;
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_3R_21()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public WhileParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[15];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x80010000,0x1000,0x80010000,0x0,0x0,0x0,0x20000000,0x80010000,0x80400,0x80010000,0x1ff00000,0x1ff00000,0xe700,0x20000000,0x300,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x11,0x0,0x11,0x4,0x10,0x8,0x0,0x11,0x0,0x11,0x0,0x0,0x10,0x0,0x0,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public WhileParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public WhileParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new WhileParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public WhileParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new WhileParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public WhileParser(WhileParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(WhileParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[37];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 15; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 37; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

  }
