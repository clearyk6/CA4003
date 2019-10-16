# JavaCC

JavaCC is a **LL(1) compiler generator**

      javacc_input ::= javacc_options
        "PARSER_BEGIN" "(" <IDENTIFIER> ")"
        java_compilation_unit
        "PARSER_END" "(" <IDENTIFIER> ")"
        (production)*
        <EOF>


Lexical Analyser:

    /*******************************
    ***** SECTION 1 - OPTIONS *****
    *******************************/

    options { JAVA_UNICODE_ESCAPE = true; }

    /*********************************
    ***** SECTION 2 - USER CODE *****
    *********************************/

    PARSER_BEGIN(SLPTokeniser)
      
    public class SLPTokeniser {
        public static void main(String args[]) {
            SLPTokeniser tokeniser;
            if (args.length == 0) {
                System.out.println("Reading from standard input . . .");
                tokeniser = new SLPTokeniser(System.in);
            } else if (args.length == 1) {
                System.out.println("SLP Parser: Reading from file " + args[0] + " . . .");
                try {
                    tokeniser = new SLPTokeniser(new java.io.FileInputStream(args[0]));
                } catch (java.io.FileNotFoundException e) {
                    System.err.println("File " + args[0] + " not found.");
                    return;
                }
            }
            else {
                System.out.println("SLP Tokeniser: Usage is one of:");
                System.out.println(" java SLPTokeniser < inputfile");
                System.out.println("OR");
                System.out.println(" java SLPTokeniser inputfile");
                return;
            }
    /*
    * We’ve now initialised the tokeniser to read from the appropriate place,
    * so just keep reading tokens and printing them until we hit EOF
    */
    
        for (Token t = getNextToken(); t.kind!=EOF; t = getNextToken()) {
            // Print out the actual text for the constants, identifiers etc.
            if (t.kind==NUM)
            {
                System.out.print("Number");
                System.out.print("("+t.image+") ");
            }
            else if (t.kind==ID)
            {
                System.out.print("Identifier");
                System.out.print("("+t.image+") ");
            }
            else {
                System.out.print(t.image+" ");
            }
        }
    }
    }
    
    PARSER_END(SLPTokeniser)

    /*****************************************
    ***** SECTION 3 - TOKEN DEFINITIONS *****
    *****************************************/

    TOKEN_MGR_DECLS :
    {
        static int commentNesting = 0;
    }

    SKIP : /*** Ignoring spaces/tabs/newlines ***/
    {
        " "
        | "\t"
        | "\n"
        | "\r"
        | "\f"
    }

    SKIP : /* COMMENTS */
    {
        "/*" { commentNesting++; } : IN_COMMENT
    }

    <IN_COMMENT> SKIP :
    {
        "/*" { commentNesting++; }
        | "*/" { commentNesting--; 
                if (commentNesting == 0)
                    SwitchTo(DEFAULT);
                }
        | <~[]>
    }

    TOKEN : /* Keywords and punctuation */
    {
          < SEMIC : ";" >
        | < ASSIGN : ":=" >
        | < PRINT : "print" >
        | < LBR : "(" >
        | < RBR : ")" >
        | < COMMA : "," >
        | < PLUS_SIGN : "+" >
        | < MINUS_SIGN : "-" >
        | < MULT_SIGN : "*" >
        | <DIV_SIGN : "/" >
    }

    TOKEN : /* Numbers and identifiers */
    {
          < NUM : (<DIGIT>)+ >
        | < #DIGIT : ["0" - "9"] >
        | < ID : (<LETTER>)+ >
        | < #LETTER : ["a" - "z", "A" - Z"] > 
    }

    TOKEN : /* Anything not recognised so far +/
    {
        < OTHER : ~[] >
    }

This is the end of the Tokenizer thus far, from here we **Specify the Grammar**:

### Refresher on Straight Line Programming Language

- Stm     -> Stm ; Stm
- Stm     -> id := Exp
- Stm     -> print ( ExpList )
- Exp     -> id
- Exp     -> nu
- Exp     -> Exp Binop Exp
- Exp     -> ( Stm, Exp )
- ExpList -> Exp, ExpList
- ExpList -> Exp
- Binop   -> +
- Binop   -> -
- Binop   -> *
- Binop   -> / 


        /*************************************
        ***** SECTION 4 - THE GRAMMAR *******
        **************************************/

        void Prog() : {}
        {
            Stm() <EOF>
        }

        void Stm() : {}
        {
            (SimpleStm() [<SEMIC> Stm()] )
        }

        void SimpleStm() : {}
        {
            (Ident() <ASSIGN> Exp()) | (<PRINT> <LBR> ExpList() <RBR>)
        }

        void Exp() : {}
        {
            (SimpleExp() [BinOp() Exp()]   )
        }

        void SimpleExp() : {}
        {
            IdExp()
        |   NumExp()
        |   (<LBR> Stm() <COMMA> Exp() <RBR>)    
        }

        void Ident() {}
        {
            <ID>
        }

        void IdExp() {}
        {
            <ID>
        }

        void NumExp() {}
        {
            <NUM>
        }

        void ExpList() : {}
        {
            (Exp() [<COMMA> ExpList()] )
        }

        void BinOp() : {}
        {
            <PLUS_SIGN>
        |   <MINUS_SIGN>
        |   <MULT_SIGN>
        |   <DIV_SIGN>    
        }


## Specifying the Grammar

Creating the Parser from the Tokeniser(Scanner)

## An Interpreter for the Straight Line Programming Language

- Section one remains the same
- Section two we rename _tokenizer_ to _interpreter_
- Section two adds in a new _try_ statement after everything carried over from tokenizer
            
        try {
            interpreter.Prog();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println("SLP Interpreter: Encountered errors during parse.");
        }

- Section 4 is different, includes _table_ 

        /***********************************
        ***** SECTION 4 - THE GRAMMAR *****
        ***********************************/

        void Prog() :
        {Table t;}
        {
            t=Stm(null) <EOF>
        }

        Table Stm(Table t) :
        {}
        {
            (t=SimpleStm(t) [<SEMIC> t=STM(t)] ) {return t;}
        }

        Table SimpleStm(Table t) :
        {String id; IntAndTable it; IntListAndTable ilt;}
        {
            (id=Ident() <ASSIGN> it=Exp(t))
            {
                if (t = null)
                    return new Table(id,it.i,t);
                else
                    return t.update(t,id,it.i);
            }
        | (<PRINT> <LBR> ilt=ExpList(t) <RBR>)
            {
                ilt.il.print();
                return ilt.t;
            }
        }

        IntAndTable Exp(Table t) :
        {IntAndTable arg1, arg2; int oper;}
        {
            (arg1=SimpleExp(t)
                [oper=BinOp() arg2=Exp(arg1.t)
                {   switch(oper) {
                        case 1: return new IntAndTable(arg1.i+arg2.i,arg2.t);
                        case 2: return new IntAndTable(arg1.i-arg2.i,arg2.t);
                        case 3: return new IntAndTable(arg1.i*arg2.i,arg2.t);
                        case 4: return new IntAndTable(arg1.i/arg2.i,arg2.t);
                    }
                }
                ]
            )
            {return arg1;}
        }

        IntAndTable SimpleExp(Table t) :
        {IntAndTable it;}
        {
            it=IdExp(t) {return it;}
        |   it=NumEx(t) {return it;}
        |   (<LBR> t=Stm(t) <COMMA> it=Exp(t) <RBR>) {return it;}
        }

        String Ident() :
        {Token tok;}
        {
            tok=<ID> {return tok.image;}
        }

        IntAndTable IdExp(Table t) :
        {Token tok;}
        {
            tok=<ID> {return new IntAndTable(t.lookup(t,tok.image),t);}
        }

        IntListAndTable ExpList(Table t) :
        {IntAndTable it;IntListAndTable ilt;}
        {
            (it=Exp(t)
                [<COMMA> ilt=ExpList(it.t)
                {return new IntListAndTable(new IntList(it.i,itl.il),ilt.t)}
                ]
            )
            {return new IntListAndTable(new IntList(it.i,null),it.t);}
        }

        int BinOp() : {}
        {
            <PLUS_SIGN>     {return 1;}
        |   <MINUS_SIGN>    {return 2;}
        |   <MULT_SIGN>     {return 3;}
        |   <DIV_SIGN>      {return 4;}
        }

for reference to language used, see [Appendix A](#Appendix-A:-the-Straight-Line-Programming-Language)

## Building a Syntax Tree for the Straight Line Programming Language

    PARSER_BEGIN(SLPTreeBuilder)
    
    public class SLPTreeBuilder {
        public static void main(String args[]) {
        SLPTreeBuilder treebuilder;
    
    if (args.length == 0) {
        System.out.println("SLP Tree Builder: Reading from standard input . . .");
        treebuilder = new SLPTreeBuilder(System.in);
    } else if (args.length == 1) {
        System.out.println("SLP Tree Builder: Reading from file " + args[0] + " . . .");
        try {
            treebuilder = new SLPTreeBuilder(new java.io.FileInputStream(args[0]));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("SLP Tree Builder: File " + args[0] + " not found.");
            return;
            }
    } else {
        System.out.println("SLP Tree Builder: Usage is one of:");
        System.out.println(" java SLPTreeBuilder < inputfile");
        System.out.println("OR");
        System.out.println(" java SLPTreeBuilder inputfile");
        return;
    }

    try {
        Stm s = treebuilder.Prog();
        s.interp();
    } catch (ParseException e) {
        System.out.println(e.getMessage());
        System.out.println("SLP Tree Builder: Encountered errors during parse.");
    }
    }
    }
    PARSER_END(SLPTreeBuilder)
    
    /*****************************************
    ***** SECTION 3 - TOKEN DEFINITIONS *****
    *****************************************/

    /*same as before*/
    
    TOKEN_MGR_DECLS :
    {
        static int commentNesting = 0;
    }

    SKIP : /*** Ignoring spaces/tabs/newlines ***/
    {
        " "
        | "\t"
        | "\n"
        | "\r"
        | "\f"
    }

    SKIP : /* COMMENTS */
    {
        "/*" { commentNesting++; } : IN_COMMENT
    }

    <IN_COMMENT> SKIP :
    {
        "/*" { commentNesting++; }
        | "*/" { commentNesting--;
                if (commentNesting == 0) {
                    SwitchTo(DEFAULT);
                }
        | <~[]>
    }

    TOKEN : /* Keywords and punctuation */
    {
          < SEMIC : ";" >
        | < ASSIGN : ":=" >
        | < PRINT : "print" >
        | < LBR : "(" >
        | < RBR : ")" >
        | < COMMA : "," >
        | < PLUS_SIGN : "+" >
        | < MINUS_SIGN : "-" >
        | < MULT_SIGN : "*" >
        | < DIV_SIGN : "/" >
    }

    TOKEN : /* Numbers and identifiers */
    {
          < NUM : (<DIGIT>)+ >
        | < #DIGIT : ["0" - "9"] >
        | < ID : (<LETTER>)+ >
        | < #LETTER : ["a" - "z", "A" - "Z"] >
    }

    TOKEN : /* Anything not recognised so far */
    {
        < OTHER : ~[] >
    }

    /***********************************
    ***** SECTION 4 - THE GRAMMAR *****
    ***********************************/
    
    Stm Prog() :
    { Stm s; }
    {
        s=Stm() <EOF>
        { return s; }
    }

    Stm Stm() :
    { Stm s1,s2; }
    {
        (s1=SimpleStm() [<SEMIC> s2=Stm() {return new CompoundStm(s1,s2);} ] )
        { return s1; }
    }

    Stm SimpleStm() :
    { String s; Exp e; ExpList el; }
    {
        (s=Ident() <ASSIGN> e=Exp()) { return new AssignStm(s,e); }
    |   (<PRINT> <LBR> el=ExpList() <RBR>) { return new PrintStm(el); }
    }

    Exp Exp() :
    { Exp e1,e2; int o; }
    {
        (e1=SimpleExp() [o=BinOp() e2=Exp() { return new OpExp(e1,o,e2); } ] )
        { return e1; }
    }

    Exp SimpleExp() :
    { Stm s; Exp e; }
    {
        e=IdExp() { return e; }
    |   e=NumExp() { return e; }
    |   (<LBR> s=Stm() <COMMA> e=Exp() <RBR>) { return new EseqExp(s,e); }
    }

    String Ident() :
    { Token t; }
    {
        t=<ID> { return t.image; }
    }

    IdExp IdExp() :
    { Token t; }
    {
        t=<ID> { return new IdExp(t.image); }
    }

    NumExp NumExp() :
    { Token t; }
    {
        t=<NUM> { return new NumExp(Integer.parseInt(t.image)); }
    }

    ExpList ExpList() :
    { Exp e; ExpList el; }
    {
        (e=Exp() [<COMMA> el=ExpList() { return new PairExpList(e,el); } ] )
        { return new LastExpList(e); }
    }

    int BinOp() : {}
    {
        <PLUS_SIGN> { return 1; }
    |   <MINUS_SIGN> { return 2; }
    |   <MULT_SIGN> { return 3; }
    |   <DIV_SIGN> { return 4; }
    }

## Appendix A: the Straight Line Programming Language

(from the _tiger book_)

    Stm     → Stm ; Stm             (CompoundStm)
    Stm     → id := Exp             (AssignStm)
    Stm     → print ( ExpList )     (PrintStm)
    Exp     → id                    (IdExp)
    Exp     → num                   (NumExp)
    Exp     → Exp Binop Exp         (OpExp)
    Exp     → ( Stm , Exp )         (EseqExp)
    ExpList → Exp , ExpList         (PairExpList)
    ExpList → Exp                   (LastExpList)
    Binop   → +                     (Plus)
    Binop   → -                     (Minus)
    Binop   → ×                     (Times)
    Binop   → /                     (Div)