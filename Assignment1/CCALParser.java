/* CCALParser.java */
/* Generated By:JavaCC: Do not edit this line. CCALParser.java */
public class CCALParser implements CCALParserConstants {

    public static void main(String[] args) {

        // Initialise parser to read
        ccalParser parser;

        if (args.length == 0) {
            System.out.println("Reading from standard input...");
            parser = new ccalParser(System.in);
        } else if (args.length == 1) {
            System.out.println("Reading from file " + args[0] + "...");
            try {
                parser = new ccalParser(new java.io.FileInputStream(args[0]));
            } catch (java.io.FileNotFoundException e) {
                System.err.println("File " + args[0] + " not found.");
                return;
            }
        } else {
            System.out.println("ccal Parser: Usage is one of:");
            System.out.println("    java ccalParser < inputfile");
            System.out.println("OR");
            System.out.println("    java ccalParser inputfile");
            return;
        }

    // Parse the file
    try {
        parser.program();
        System.out.println("CCAL program parsed successfully.");
    } catch (ParseException e) {
        System.out.println(e.getMessage());
        System.out.println("Encountered errors during parse.");
        }
    }

/***********************************
***** SECTION 4 - THE GRAMMAR *****
***********************************/
  static final public 
void program() throws ParseException {
    declList();
    functionList();
    main();
    jj_consume_token(0);
  }

  static final public void declList() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case VARIABLE:
    case CONSTANT:{
      decl();
      jj_consume_token(SEMIC);
      declList();
      break;
      }
    default:
      jj_la1[0] = jj_gen;
      ;
    }
  }

  static final public void decl() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case VARIABLE:{
      varDecl();
      break;
      }
    case CONSTANT:{
      constDecl();
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void varDecl() throws ParseException {
    jj_consume_token(VARIABLE);
    jj_consume_token(ID);
    jj_consume_token(COLON);
    type();
  }

  static final public void type() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case INTEGER:{
      jj_consume_token(INTEGER);
      break;
      }
    case BOOLEAN:{
      jj_consume_token(BOOLEAN);
      break;
      }
    case VOID:{
      jj_consume_token(VOID);
      break;
      }
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void constDecl() throws ParseException {
    jj_consume_token(CONSTANT);
    jj_consume_token(ID);
    jj_consume_token(COLON);
    type();
    jj_consume_token(ASSIGN);
    expression();
  }

/* Removal of left recursion in expression - deleted fragment nonterminal completely - add in left association */
  static final public void expression() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case LPAREN:{
      jj_consume_token(LPAREN);
      expression();
      jj_consume_token(RPAREN);
      break;
      }
    case INTEGER:
    case TRUE:
    case FALSE:
    case MINUS:
    case ID:{
      expressionSimple();
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void expressionSimple() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ID:{
      jj_consume_token(ID);
      expressionWithId();
      break;
      }
    case INTEGER:
    case TRUE:
    case FALSE:
    case MINUS:{
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case MINUS:{
        jj_consume_token(MINUS);
        jj_consume_token(ID);
        break;
        }
      case INTEGER:{
        jj_consume_token(INTEGER);
        break;
        }
      case TRUE:{
        jj_consume_token(TRUE);
        break;
        }
      case FALSE:{
        jj_consume_token(FALSE);
        break;
        }
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case PLUS:
      case MINUS:{
        binaryArithOp();
        expression();
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      break;
      }
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void expressionWithId() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case LPAREN:{
      jj_consume_token(LPAREN);
      argList();
      jj_consume_token(RPAREN);
      break;
      }
    default:
      jj_la1[8] = jj_gen;
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case PLUS:
      case MINUS:{
        binaryArithOp();
        expression();
        break;
        }
      default:
        jj_la1[7] = jj_gen;
        ;
      }
    }
  }

  static final public void binaryArithOp() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case PLUS:{
      jj_consume_token(PLUS);
      break;
      }
    case MINUS:{
      jj_consume_token(MINUS);
      break;
      }
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void argList() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ID:{
      nempArgList();
      break;
      }
    default:
      jj_la1[10] = jj_gen;
      ;
    }
  }

/* Add left factoring into nempArgList to achieve LL(1) */
  static final public void nempArgList() throws ParseException {
    jj_consume_token(ID);
    nempArgListA();
  }

  static final public void nempArgListA() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case COMMA:{
      jj_consume_token(COMMA);
      nempArgList();
      break;
      }
    default:
      jj_la1[11] = jj_gen;
      ;
    }
  }

  static final public void functionList() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case INTEGER:
    case BOOLEAN:
    case VOID:{
      function();
      functionList();
      break;
      }
    default:
      jj_la1[12] = jj_gen;
      ;
    }
  }

  static final public void function() throws ParseException {
    type();
    jj_consume_token(ID);
    jj_consume_token(LPAREN);
    parameterList();
    jj_consume_token(RPAREN);
    jj_consume_token(LBRACE);
    declList();
    statementBlock();
    jj_consume_token(RETURN);
    jj_consume_token(LPAREN);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case INTEGER:
    case TRUE:
    case FALSE:
    case LPAREN:
    case MINUS:
    case ID:{
      expression();
      break;
      }
    default:
      jj_la1[13] = jj_gen;
      ;
    }
    jj_consume_token(RPAREN);
    jj_consume_token(SEMIC);
    jj_consume_token(RBRACE);
  }

  static final public void parameterList() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ID:{
      nempParameterList();
      break;
      }
    default:
      jj_la1[14] = jj_gen;
      ;
    }
  }

  static final public void nempParameterList() throws ParseException {
    jj_consume_token(ID);
    jj_consume_token(COLON);
    type();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case COMMA:{
        ;
        break;
        }
      default:
        jj_la1[15] = jj_gen;
        break label_1;
      }
      jj_consume_token(COMMA);
      jj_consume_token(ID);
      jj_consume_token(COLON);
      type();
    }
  }

  static final public void statementBlock() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case IF:
    case WHILE:
    case SKIP_T:
    case LBRACE:
    case ID:{
      statement();
      statementBlock();
      break;
      }
    default:
      jj_la1[16] = jj_gen;
      ;
    }
  }

/* left-factoring implemented for statement to achieve LL(1) */
  static final public void statement() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ID:{
      jj_consume_token(ID);
      statementWithId();
      break;
      }
    case LBRACE:{
      jj_consume_token(LBRACE);
      statementBlock();
      jj_consume_token(RBRACE);
      break;
      }
    case IF:{
      jj_consume_token(IF);
      condition();
      jj_consume_token(LBRACE);
      statementBlock();
      jj_consume_token(RBRACE);
      jj_consume_token(ELSE);
      jj_consume_token(LBRACE);
      statementBlock();
      jj_consume_token(RBRACE);
      break;
      }
    case WHILE:{
      jj_consume_token(WHILE);
      condition();
      jj_consume_token(LBRACE);
      statementBlock();
      jj_consume_token(RBRACE);
      break;
      }
    case SKIP_T:{
      jj_consume_token(SKIP_T);
      jj_consume_token(SEMIC);
      break;
      }
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void statementWithId() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ASSIGN:{
      jj_consume_token(ASSIGN);
      expression();
      jj_consume_token(SEMIC);
      break;
      }
    case LPAREN:{
      jj_consume_token(LPAREN);
      argList();
      jj_consume_token(RPAREN);
      jj_consume_token(SEMIC);
      break;
      }
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/* Removed choice conflict between FIRST(condition()) and FIRST(expression()) containing "(" */
/* Removed left recursion occuring by seperating into Condition() and ConditionA() */
  static final public void condition() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case NOT:{
      jj_consume_token(NOT);
      condition();
      conditionA();
      break;
      }
    case LPAREN:{
      jj_consume_token(LPAREN);
      condition();
      jj_consume_token(RPAREN);
      conditionA();
      break;
      }
    case INTEGER:
    case TRUE:
    case FALSE:
    case MINUS:
    case ID:{
      expressionSimple();
      compOp();
      expression();
      conditionA();
      break;
      }
    default:
      jj_la1[19] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void conditionA() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case OR:
    case AND:{
      orAnd();
      condition();
      conditionA();
      break;
      }
    default:
      jj_la1[20] = jj_gen;

    }
  }

  static final public void orAnd() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case OR:{
      jj_consume_token(OR);
      break;
      }
    case AND:{
      jj_consume_token(AND);
      break;
      }
    default:
      jj_la1[21] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void compOp() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case EQUAL:{
      jj_consume_token(EQUAL);
      break;
      }
    case NOT_EQUAL:{
      jj_consume_token(NOT_EQUAL);
      break;
      }
    case LESS_THAN:{
      jj_consume_token(LESS_THAN);
      break;
      }
    case LESS_THAN_EQUAL_TO:{
      jj_consume_token(LESS_THAN_EQUAL_TO);
      break;
      }
    case GREATER_THAN:{
      jj_consume_token(GREATER_THAN);
      break;
      }
    case GREATER_THAN_EQUAL_TO:{
      jj_consume_token(GREATER_THAN_EQUAL_TO);
      break;
      }
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void main() throws ParseException {
    jj_consume_token(MAIN);
    jj_consume_token(LBRACE);
    declList();
    statementBlock();
    jj_consume_token(RBRACE);
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public CCALParserTokenManager token_source;
  static JavaCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[23];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x3000,0x3000,0x38000,0x80608000,0x608000,0x0,0x608000,0x0,0x80000000,0x0,0x0,0x2000000,0x38000,0x80608000,0x0,0x2000000,0x21880000,0x21880000,0x90000000,0x80608000,0x0,0x0,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x8004,0x4,0x6,0x8004,0x6,0x0,0x6,0x8000,0x0,0x0,0x8004,0x8000,0x0,0x8000,0x8000,0x0,0x800c,0x30,0x30,0xfc0,};
   }

  /** Constructor with InputStream. */
  public CCALParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public CCALParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new CCALParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public CCALParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new CCALParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public CCALParser(CCALParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(CCALParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk_f() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[48];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 23; i++) {
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
    for (int i = 0; i < 48; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

}
