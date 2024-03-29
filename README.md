# CA4003 Compiler Construction

## David Sinclair

office: L253

Phone: 5510

Email: David.Sinclair@computing.dcu.ie

### [Course web page](https://www.computing.dcu.ie/~davids/teaching.shtml)

    java -cp C:\JavaCC\javacc-6.0\bin\lib\javacc.jar javacc

---

# Module Overview

## Topics to be covered

- 1 [Structure of a compiler](#1.-Structure-of-a-Compiler)
- 2 [Lexical analysis](#2.-Lexical-Analysis) - _analyse tokens to form words_
- 3 [Parsing](#3.-Parsing) - _do words in order make sense? LInguistic check_
- 4 [Abstract Syntax](#4.-Syntax-Analysis) - _Abstract Syntax Tree; data structure_
- 5 [Semantic Analysis](#5.-Semantic-Analysis) - _does data in AST make sense?_
- 6 [Intermediate Code Generation](#6.-Intermediate-Code-Generation) - _"halfway state between source & target lang."_
- 7 [Register Allocation & Code Optimisation](#7.-Code-Optimisation)
- 7.1 Run-time Environments
- 8 [Code Generation](#8.-Code-Generation)

### Techniques used to **compile a _high-level_ computer program into an executable program**, and can also be used to **process and analyse any _structured data_**

## Texts

- _Modern Compiler Implementation in Java_, Andrew W. Appel; Cambridge University Press (1998)
- _Compilers: Principles, Techniques and Tools, 2nd Edition_, Alfred V. Aho, Monica S. Lam, Ravi Sethi and Jeffrey D. Ullman (2007)

---
# Module Breakdown

## 30% continuous assessment

- Assignment 1: Front end (15%)
  - Lexical syntax analysis

- Assignment 2: Back end (15%)
  - Semantic analysis
  - Generating intermediate code

## 70% end-of-semester examination

- 10 questions: **answer all**

---

# 1. Structure of a Compiler

## Compiler

### A program that translates a **_high-level_ program in one language into an _executable_ program in another language**

## Interpreter

### A program that reads an _executable_ program and produces the **results of running that program**

- Typically involves **executing** the source program

#### _Same front-end issues often arise in interpreters & compilers_

## Transpiling

### translating one **high level language to another high level language**

---
# The Compilation Process

## Consist of a number of _phases_

 (no. of phases varies from compiler to compiler depending on their _complexity_)

## Basic set of phases:

- ### Front end

  - Lexical Analysis
  - Syntax Analysis
  - Semantic Analysis

- ### Back end

  - Intermediate Code Generation
  - Code Optimisation
  - Code Generation

  ---

# 2. Lexical Analysis

## Goal of L.A is to _convert a stream of characters from the source program into stream of **tokens**_ that represent recognised **keywords, identifiers, numbers** and **punctuation**

- ### Some tokens require a **lexeme**; an additional quantity that _indicates the type and/or value of the token_ 

       answer = x * 2 - y 
      becomes..  id(answer), =, id(x), *, num(2), -, id(y)

      TOKEN(lexeme)

- ### A **scanner** is the piece of code which performs L.A

- ### Scanners can be automatically built using **Regular Expression** and **Finite Automata**

    ---

## 2.1 Lexical Analyisis in action

      bool compare(char *s, char *t) /* compare 2 strings */
      {
          if (strcmp(s, t)
            return (true);
          else
            return (false);
      }
    ### would yield the following..
      BOOL ID(compare) LBRACK CHAR STAR ID(s) COMMA CHAR STAR ID(t) RBRACK IF LBRACK ID(strcmp) LBRACK ID(s) COMMA ID(t) RBRACK RETURN LBRACK TRUE RBRACK SEMI ELSE RETURN LBRACK FALSE RBRACK SEMI RBRACE EOF

    ---

## 2.2 Scanners/ Specifying Patterns

### Scanner has to recognise the various _parts of a language_

- ### **white space**

      <ws> ::= <ws> ''
           |   <ws> 't'
           |   ' '
           |   '\t'

      
            (""|"\n"|"\t") ========> Whitespace

- ### **keywords** and **operators**

  - expressed as _literal patterns_
    - if
    - while
    - do

           do ========> DO

- ### **comments**

  - opening and closing delimiters
    - /* ... */

          "//"[a - zA -Z0 - 9]*"\n" |  ========> Comment 

- ### **identifiers**

  - varies between languages
  - typically _starts with a character_ and contains characters, numerics and limited symbols

        [a - zA - Z][a - zA - Z0 - 9]* =========> ID

- ### **numbers**

  - **integers**: 0 or digit from 1-9 followed by digits from 0-9
  - **decimals**: integer . digits from 0-9
  - **reals**: (int or dec) E (+ or -) digits from 0-9

        [0 - 9]+ ========> NUM
        ([0 - 9]+"."[0 - 9]*)|([0 - 9]*"."[0 - 9]+) ========> REAL

### Specify these patterns using **Regular Expressions**

  ---

## 2.3 Regular Expressions

### Represents a _set of strings_

- **Symbol**: For each symbol _a_ in the language, the **regular expression _a_** denotes the string a

- **Alternation**: M **|** N == a string in _M_ **or** in _N_
  - _M_ and _N_ are 2 regular expressions

- **Concatenation**: MN == a string _ab_ where _a_ is in _M_ **and** _b_ is in _N_.

- **epsilon**: denotes the empty string with ε

- **Repitition**: the **_Kleene Closure_** of _M_ ( **M*** ) is the set of _zero or more concatenations_ of _M_.

Kleene Closures bind tigheter than concatenation, which bind tigheter than alternation.

      (a | b)* aa (a | b)*

        --> "a sring of a's and b's that contains at least one pair of consecutive a's"

        --> "a string of a's and b's with no consecutive a's"

- ## Shorthands for RegEx
  
  - [axby] == (a|x|b|y)
  - [a - e] == [abcde]
  - _M_? == (_M_|e)
  - _M_+ == (_MM_*)
  - . == any single character _except newline_
  - " _a_ +*" == a quotation; string **stands for itself**

  ---

## 2.4 Scanners: Disambiguation Rules

### _Is **do99** an **identifier** or a **keyword(do)** followed by a **number(99)**??_

Most lexical-analyser generators follow **2 rules** to disambiguate situations like above.

- **Longest match**: the **longest initial substring** that can match _any_ regular expression is taken as the next token.

- **Rule priority**: If longest initial substring is matched by _multiple regular expressions_, the **first regular expression that matches** determines the token type.

### ~do99 is an **identifier**; Kleene closure do99* binds tighter than concatenation "do" + "99"

  ---

## 2.5 Finite Automata

### Good for **recognising regular expressions** (_which are then in turn good for **specifying lexical tokens**_)

### Consists of a set of **nodes** and **edges**

- **Nodes** represent states
  
  - One node represents the _start node_, some of the nodes are _final nodes_ 

- **Edges** go from one node to another node and are labelled with a **symbol**

- ### **Deterministic Finite Automation**

  - _**no pairs of edges**_ leading away from a node are _labelled with the same symbol_

  - This is the type of FA that is **required** for a scanner 

- ### **Nondeterministic Finite Automation**

  - _**two or more pairs of edges**_ leading away from a node are _labelled with the same symbol_
  - In general we **need an NFA** to recognise a regular expression
    - NFA always correctly "guesses" which edge to take whan faced with nondeterministic choice
    - Executes all nondeterministic paths concurrently and if **any** path terminates successfully then so does the NFA.
  - Cannot execute a nondeterministic finite automation.

### A finite automation can be **encoded** by either

- **Transition Matrix**: 2-dimensional array; indexed by _input character and state number_, that contains the next state

      ~needs image of p. 7 in the notes to notes as a reference~

      int edges[][] = {/* ws, ..., 0, 1, 2, ... d, e, f, ... o, ... */
        /* state 0 */    { 0, ..., 0, 0, 0, ... 0, 0, 0, ... 0, ... }
      }

- **Action Array**: an array; indexed by _final state number_, contains the resulting action
  - if the final state is 2, then return ID; if the final state is 3, then return DO etc etc

The Scanner builds these Transition Matrices and Action Arrays - must be v v fast.

        RegExp --> NFA
                    |
                   DFA
                  /   \
    Transition Matrix  Action Array

  ---

# Subset Construction Algorithm

It's easier to convert RegEx to NFA, but we can _only build DFA_

## Subset Construction Algorithm converts NFA to a DFA using **2 concepts**

- ### **_epsilon_ - closure**(S)

  - all the states that can be reached from state S **without consuming any symbols** (using _ε_ (epsilon) edges only)

- ### **DFAedge**(d, c)

  - all the states that can be reached from the states in _d_ by consumng **only edges labelled with _c_ and _ε_.**

  We use this algorithm to get **sets of states** at each step. If _at least one state in set states[i]_ is final, then i is a **final set**.

  - keep a **trans array** to recognise the tokens

  - record the _type_ of token in the final DFA state reognises 

  - use **rule priority**; if more tan one final state in _states[i]_, then first occuring is taken as the final state.

  ---

## Lexical Analyser Generators

### Built for automatic conversion from regular expression to NFA to DFA

### **JavaCC** can generate _lexical analysers_ and _parsers_

- consists of two sections
  - **lexical specification** section to _define tokens and white spaces_
  - **Production** section ...

### **JavaCC**

      PARSER_BEGIN(MyParser)
        class MyParser
      PARSER_END(MyParser)

      /* For the regex on the right generates the token on the left */
      TOKEN : {
        < IF: "if" >
      | <#DIGIT: ["0"-"9"] >
      | <#CHAR: ["a"-"z"] | ["A"-"Z"] >
      | <ID: (<CHAR>)(<CHAR> | <DIGIT>)* >
      | <NUM: (<DIGIT>)+ >
      | <REAL: ( (<DIGIT>)+ "." (<DIGIT>)* )
               ( (<DIGIT>)* "." (<DIGIT>)* ) >
      }

      /* regex here wil be skipped.. */
      SKIP : {
          <"//" (<CHAR> | <DIGIT>)* ("\n" | "\r" | "\r\n") >
        | " "
        | "\t"
        | "\n"
      }

      void Start () :
      { }
      { ( <IF> | <ID> | <NUM> | <REAL> )* }

- the name following PARSER_BEGIN and PARSER_END **must be the same**.

- given a name of _MyParser_, the following files are generated:
  - **MyParser.java**; generated parser
  - **MyParserTokenManager.java**; generated token manager (scanner)
  - **MyParserConstants.java**; useful constants

- Java program is placed in between the PARSER_BEGIN and PARSER_END. **Must** contain a class declaration with the _same name as the generated parser_

### **Token Manager**

Token manager produced by JavaCC provides one public method

      Token getNextToken() throws ParseException

- returns the next available token in the input stream and moves the pointer one step in the input stream

- **important**: two important attributes of any token
  - kind: the kind of token as specified in token manager
  - image: the _lexeme_ associated with that token

---

# 3. Parsing

### Regular expressions can't count. :(( !
  
  - they cannot balance parentheses..

  - therefore, we need to **add mututal recurscion**
    
    - this gets rid of alternation (except at top level)

          expr = a b(c | d)e
          becomes
          aux = c
          aux = d
          expr = a b aux e

    - this gets rid of Kleene Closure

          expr = (a b c)*
          becomes
          expr = (a b c) expr
          expr = ε

    - .. **this becomes a Context Free Grammar**

    ---

## Context Free Grammers

  A CFG _G_ is a 4-tuple (_Vt_, _Vt_, _S_, _P_)

  - _Vt_ is the set of **terminal** symbols in the grammar. (the set of tokens returned by the scanner.)

    - a,b,c,...∈ _Vt_

  - _Vn_ are the **nontermnals**, a set of syntactic variables that denote sets of (sub)strings occurring in the language. These impose a structure on the grammar

    - A,B,C,...∈ _Vn_

  - _S_ is a distinguished nonterminal denoting the **entire set of strings in _L(G)_**. A.K.A **goal symbol**

  - _P_ is a finite **set of productions** specifying how terminals and nonterminals can be combined to form strings in the language. Each production _must have a single non-terminal on its left hand side_.

  The set _V = Vt_ U _Vn_ is the **vocabulary** of G.

  ### **Vocabulary**

  - U, V, W,... ∈ _V_

  - α, β, γ,... ∈ _V*_

  - u, v, w,... ∈ _Vt*_

  - **single-step derivation**: (if A -> γ), then αAβ **=>** αγβ is a s.s derivation using A -> γ

    - **=>*** denotes derivation of 0+ steps; **=>+** denotes 1+ step derivation 

  - **sentence of _G_**

        L(G) = {w ∈ V*t|S =>+ w}, w ∈ L(G)

        note
        L(G) = {β ∈ V*|S =>* β} ∩ V*t

  ### **Notation - Backus-Naur form**

        1|<goal> ::= <expr>                       (non-terminals represented with <angle brackets>)
        2|<expr> ::= <expr><op><expr>             (terminals with typewriter font or underline)
        3|        |   num
        4|        |   id                          (productions as shown to the left)
        5|<op>   ::=  +
        6|<op>   ::=  -
        7|<op>   ::=  *
        8|<op>   ::=  /


  ### **Derivations**

  A sequence of production applications, AKA _parse_

  _Parsing_ is the **process of discovering a derivation for a statement**

  - At each step, we _choose a nonterminal to replace_. This can lead to different derivations...

    - **leftmost derivation**: the leftmost non-terminal is replaced at each step.

    - **rightmost derivation**: the rightmost non-terminal is replaced at each step.

  Can lead to discrepancies.... take sentence **x + 2*y** using the above grammar..

      Leftmost Derivation                             Rightmost Derivation
      <goal> => <expr>                                <goal> => <expr>
             => <expr><op><expr>                             => <expr><op><expr>   
             => <expr><op><expr><op><expr>                   => <expr><op><id,y>
             => <id,x><op><expr><op><expr>                   => <expr>*<id,y> 
             => <id,x>+<expr><op><expr>                      => <expr><op><expr>*<id,y>
             => <id,x>+<num,2><op><expr>                     => <expr><op><num,2>*<id,y>
             => <id,x>+<num,2>*<expr>                        => <expr>+<num,2>*<id,y> 
             => <id,x>+<num,2>*<id,y>                        => <id,x>+<num,2>*<id,y>

      ..<goal> =>* id + num*id in both cases, BUT structure induced is different

      x + (2 * y) in parse tree             WHEREAS     (x + 2) * y in parse tree for R.most ...WRONG

  ..need to add _precedence_ to add additional structure to the Grammar. Precedence lets the computer know _which route to take_

      1|<goal>    ::= <expr>
      2|<expr>    ::= <expr> + <term>            
      3|           |  <expr> - <term>
      4|           |  <term>                      (terms must be derived from expressions)       
      5|<term>    ::= <term> * <factor> 
      6|<term>    ::= <term> / <factor>
      7|<term>    ::= <factor>                    (factors must be derived from terms)
      8|<factor>  ::= num
      9|           |  id  

  By adding this extra structure, we **imply an evaluation order** and get the correct Tree.


  ## Ambiguity

  Grammar is **ambiguous** if a _sentence admits two or more derivations._

  Two types of ambiguity; Context _free_ ambiguity and Context _sensitive_ ambiguity

  - Context Free: 

    - need to **rearrange the grammar** in order to eliminate this ambiguity

            <stmt> ::= if <expr> then <stmt>                  
                    |  if <expr> then <stmt> else <stmt>                 
                    |  (other stmts)..   

              - The sentece "if E1 then if E2 then S1 else S2" would have two derivations..                     
            
            <stmt>      ::= <matched>
                         |  <unmatched>
            <matched>   ::= if <expr> then <matched> else <matched>
                         |  (other stmts)..
            <unmatched> ::= if <expr> then <stmt>
                         |  if <expr> then <matched> else <unmatched>

              - this matches each "else" with the closest unmatched "then"


  - Context Sensitive: 
  
    - arises from **overloadng**

           a = f(17)          ...(in Algol-like languages), f could be a function OR a subscripted variable..

    - disambiguating requires context.. need values of declarations, this is really an issue of type

    - rather then complicate parsing, this type of ambiguity is handled separately..

---

## 3.1 Top Down Parsing

Starts with the **root** of the parse tree labelled with the _goal symbol_ of the grammar. Repeats the following until **fringe of the parse tree matches the input string**:

- At node label _A_ select a production _A_ -> _α_, and construct the appropriate child for each symbol of α.
- When a terminal is added to the fringe that _doesn't match the input string, **backtrack**_.
- Find the next node to be expanded (Must have a label in Vn)

To avoid backtracking the parse should be guided with the input string.._choose the right rule @ the right time_

**Top down parsers cannot handle left recursion** in a grammar

- transform the grammar to remove left recursion
     
      A ::= Aα
         |  β

         becomes
      
      A  ::= βA'
      A' ::= αA'
         |   ε   

         where A' is a new nonterminal. This is right-recursive

**Lookahead** needed to _avoid backtracking_; if parser doesn't make right choices the expansion may terminate!

We need arbitrary lookahead to parse a CFG, but a large no of subclasses of CFGs can be parsed with limited lookahead such as:

- **LL(1):** Left to right scan, Left-most derivation, 1-token lookahead
- **LR(2):** Left to right scan, Right-most derivation, 1-token lookahead 

      1 <goal> ::= <expr>
      2 <expr> ::= <term> <expr′>
      3 <expr′> ::= + <expr>
      4         | - <expr>
      5         | ε
      6 <term> ::= <factor> <term′>
      7 <term′> ::= * <term>
      8         | / <term>
      9         | ε
      10 <factor> ::= num
      11         | id

**Table Driven Parsing**: generate a **parse table**, then parsing code _remains the same across any language_.

- Remove left recursion and left a
- FIRST: (α); the **set of terminal symbols** that _begin strings derived from α._ 

(XYZ -> d, in copy)

  - {a element of Vt| α =>* aβ} 
  - **if** α =>* ε, then ε element of FIRST(α)

        note:  =>* means in zero or more steps

- compute FIRST(X) for all grammar symbols X by doing the following:
  - if X terminal, then FIRST(X) = {X}
  - if X is a nonterminal and X -> Y1 Y2 Y3
  - ...
  - ...


- FOLLOW(X): the set of terminals that can _immediately follow X_. 
  - t element of FOLLOW(X) if tere is **any derivation containing _Xt_**. includes any derivation containing XYZt where Y and Z are nullable.

- compute FOLLOW(A) for all nonterminals A by doing the following until nothing else can be dded to set:
  - Place $ in FOLLOW(S) where S is the start symbol and $ is the end of input marker
  - if there is a prod rule _A -> αBβ_, then everything in FIRST(β), except ε, is in FOLLOW(B).
  - if there is a prod rule _A -> αB_, or a prod rule _A -> αBβ_ where FIRST(β) contains ε everything in FOLLOW(A) in FOLLOW(B)

**note**: if symbol Y can tranform to ε, then Y is a **nullable symbol**.

- LOOKAHEAD(A -> α): set of terminals which can appear next in the input if we want to recognise the rule A -> α
- to build LOOKAHEAD(A -> α):

  - Put FIRST(α) (less {ε}) in LOOKAHEAD(A -> α).
  - if ε is element of FIRST(α) then put FOLLOW(A) in LOOKAHEAD(A -> α)

### LL(1) Grammars

A grammar G is **LL(1)** if for each set of productions A -> α1|α2|...|αn

- LOOKAHEAD(A -> α), LOOKAHEAD(A -> α2),..., LOOKAHEAD(A -> αn) are all _pairwise disjoint_.
- if more than one entry then G is not LL(1).

Facts about LL(1)s:

- No left-recursive grammar is LL(1)
- No ambiguous grammar is LL(1)
- Some languages have no LL(1) grammar
- An ε-free grammar where _each alternate expansion for A begins with a **disinct** terminal_ is a **simple LL(1) grammar**

### Building the AST

Insert code at the right points...

      tos = 0
      Stack[tos] = EOF
      Stack[++tos] = root node
      Stack[++tos] = Start Symbol
      token = next token()
      repeat
        X = Stack[tos]
        if X is a terminal or EOF then
          if X == token then
            pop X
            token = next token()
              //pop and fill in node
          else error()
        else /* X is a non-terminal */
          if M[X,token] == X → Y1Y2...Yk then
            pop X
              // pop node for X
              // build node for each child and
              // make it a child of node for X
            push {nk , Yk , nk−1, Yk−1, ..., n1, Y1}
          else error() 
      until X == EOF

**Error Recovery**: for each non-terminal, construct a set of terminals on which the parser can _synchronize_
- When an error occurs looking for A, scan until an element of SYNCH(A) is found
- building SYNCH(A):
  - a element of FOLLOW(A) => a element SYNCH(A)
  - place keywords that start that start statements in SYNCH(A)
  - add symbols in FIRST(A) to SYNCH(A)
- (if we can't match a terminal on top of the stack..)
  - pop the terminal
  - print a message saying terminal was inserted
  - continue the parse

To fix conflict between picking two rules..
- if something goes to something and somthing goes to nothing, pick the **someting** to something rule.
- if both go to something then you gotta choose between the two.

---

## 3.2 Bottom Up Parsing

LL(k) parsers: left to right parse, leftmost derivation using _k_ token lookahead, Must predict which production rule to use having seen the first k tokens of the right hand side.

More powerful to use **LR(_k_)** parsing; left to right parse, _rightmost derivation_, k token lookahead. Many a programming language requires an LR(1) grammar and parser.

- LR(1) is the norm since k > 1 generates lots of internal states. SableCC, Yacc and Bison all support LR grammars.

### LR Parse Engine

Has a **stack** and an**input**. First _k_ tokens of the input are the _lookahead_.

LR engine shifts tokens from the input to the stack, and when the tokens on top of the stack match the righthand-side of a production rule, pop the tokens off the stack and push the lefthand-side of the production rule onto the stack.

- if the stack contains ABC and there is a production rule X -> ABC, then pop C, B and A and push X onto th stack.
- Stack is initially empty and when the parser shifts the end-of-file marker, $, and the input has a valid derivation; parser enters the accepting state. Otherwise parser ends in an error state.

**Valid actions of an LR Parse Engine**

- **shift(n)** Advance input by 1 token and push state n onto the stack.
- **reduce(k)** Pop of the number of symbols on the right hand side of rule k.
  - Let X be the lefthand side of rule k. In the state now on top of the stack, look up X in the state table to get "goto n". Push state n onto the stack.
- **accept** Stop parsing and report success
- **error** Stop parsing and report failure.

**State Table**: a two dimensional table - States x Symbols.

- Each entry contains an action. Implemented as a DFA

  - DFA not powerful enough to parse a context-free grammar, but can manage the stack in the LR parse engine.
  - the edges of the DFA are the symbols and the states represent the stack.

### LR(0) Parsing

> insert initial notes

Initially have an empty stack, input will have a
complete **sentence S** followed by the **end-of-input marker, $**. 

We denote this as:

    S′ → .S$

where the **dot indicates the _current parser position_**.

**LR(0) item:** A production rule combined with a parser position, dot ('.')

In this state, an input that can be parsed not only begins with S
but also with any any possible right-hand side of an S-production.

**State**: Set of LR(0) items, visually represented as a list of items in a box with State number label.

**Shift actions**: If we shift an x is state 1, only the second production rule is affected. We can ignore the other rules.

The effect of shifting an x symbol will be to _move the dot past the x_. This will give us a **new state**.


Build **State Diagram** first
- be mechanical and meticulous and this won't fail

Then build **Parsing Table**

Conflicts...

- **Shift reduce**
- **Reduce reduce**

### SLR Parsers

"Simple LR", but more powerful than LR(0)

Like LR(0) parsers, but generation of reduce actions depends on the FOLLOW() set.

- have fewer reduce actions than LR(0)

### LR(1) Parsers

More powerful than LR(0) and SLR language.

- **Most programming languages** parsed by an LR(1) Parser.

Algorithm similar to LR(0), exceot:

- concept of an _item_ in LR(1) is expanded to a **lookahead symbol**
- LR(1) item is (A -> α.Beta,x), where x = lookahead symbol.
  - sequence α is on top of stack
  - input starts with a string that can be derived from Betax
- LR(1) state a set of LR(1) items that are constructed using versions of Closure and Goto that incorporate the lookahead symbol.

To build Parsing Table once the Sate Diagram has been generaterd...

- if dot is at the end of a production rule, then **reduce** action placed in Parsing table in the corresponding column to lookahead symbol.
- If the dot is to the left of a _terminal_ or _non-terminal_ symbol, the corresponding column for that state contains a _shift_ or _goto action respectively_ (same as with an LR(0) table)


LR(1) can have maaany states - COBOL-85 has  > 2,000,000!

- Therefore a _lookahead LR(1) parser_ (**LALR(1)**) can be used to merge states that differ by their lookahead sets - reducing no. of states
- Brings COBOL-85's states down to < 2,000
- Check which states can be merged from the LR(1) diagram, if available.
- Create parse table based on the merge states.
- LALR(1) parsing tables are at risk of reduce-reduce conflicts where LR(1) parsers may not..

### If Then Else statements

Tend to give rise to shift-reduce errors..

- **shift** gives one interpretation - _if a then {if b then s1 else s2}_ (more common)
- **reduce** gives a different interpretation - _if a then {if b then s1} else s2_

.. better way to deal is to introduce non-terminals to identify Matched and Unmatched statements (this arises from [**ambiguity**](##Ambiguity))

Some Compiler generators (Yacc, Bison) have **precedence directives** for _assisting the resolvement of shift-reduce conflict_.

- can make items _right associative_, _left associative_ or _non-associative_
- definitions of precedence in Yacc:
       
      precedence nonassoc EQ, NEQ;
      precedence left PLUS, MINUS;
      precedence left TIMES, DIV;
      precedence right EXP;

      /*Order of appearance is order of precedence*/

### Recursion: left vs right

Right Recursion:
- Needed for _termination in predictive parsers_.
- Requires more stack space.
- Gives rise to right associative operators.

Left Recursion:
- Works fine in bottom-up parsers.
- _Limits the required stack space._
- Gives rise to left associative operators.
- Causes a top-down parser to crash... see LL(0) left recursion handling [here](##3.1-Top-Down-Parsing)

Rule of thumb:
- **Right recursion for top-down parsers.**
- **Left recursion for bottom-up parsers.**

---

# 4. Syntax Analysis

## Goal of S.A is to _combine the tokens generated by the L.A_ into a valid **"sentence"**

- ### A **Grammar** is a set of rules that _specifies how the tokens can be combined_

- ### Use of **Finite State Automata**

- ### Produces a **Parse Tree** and a compressed version of the Parse Tree, the **Abstract Syntax Tree**, which doesn't contain any redundant information

---

# 5. Semantic Analysis

- ### **In a compiler**

  - _Checks the source program_ for **semantic errors**, gathers **type information** for the Intermediate Code Generation phase

  - Does something useful given the parsed sentence

    - e.g expression z = x + 2 & y is _Syntactically correct_ but is not semantiacally understandable.

  - "What if answer and y are integers and x is a float??"

- ### **In an interpreter**

  - _Evaluates the source program_ stored in the Abstract Syntax Tree

---

## Semantic Actions

Semantic actions of a parser perform useful operations:
- Build an abstract parse tree
- type check
- evaluation/begin translation, depending on interpreter/compiler

In some compiler constructors eg. JavaCC, semantic actions are _attached to production rules_.

In JJTree, syntax tree is _automatically generated_.

Each symbol, terminal or non-terminal has it's own type of **semantic value** (semantic type)

A rule W -> XYZ **must** return a semantic type associated with the symbol W, but _may_ use values associated with all RHS symbols

**Parse Tree** is a data structure used to separate parsing from semantics.

Possible to write a compiler so that everything is done by semantic action of the parsing phase, **but**

- constrains the compiler to analyse the input _exactly in the order in which it is parsed_
- it becomes v v difficult to maintain.

**Concrete parse tree**: Exactly **one** terminal node for each token in the input, one internal node for each production rules used in Parse.

- great as educational tool, but contain a lot of redundant info, useless info for semantic analysis. (punctuation and operator tokens)

**Abstract Syntax Tree**: a _clean interface_ between parsing and semantic analysis/later phases of compilation. Removes all redundant info. Captures the _phase structure_ of the input.

In a **single pass compiler** where lexical analysis, parsing and semantic analysis are _all done simultaneously_, then the current position of the lexical analyser is a reasonable approximation of the source of the error

In a **multi-pass compiler** using an AST, the current position of the lexical analyser is EOF, and so is not useful if we find an error during semantic analysis.

- we need to extend the data structures used in the AST to include a **pos** field that indicates the position, in the original source file, of the characters from which the abstract syntax structures were derived. Need to include this in Scanner too to include line and column numbers.

  ---

Style of programming affects **modularity** of the code

- **Object Oriented** style of programming involves use of an _eval_ method
  - each interpretation is a method in the class that defined the **kind**.
  - adding a kind is just adding a new class with a method of each interpretation
  - adding a new interpretation means editing each class that represents a kind.
  - useful for _GUIs_ where the number/type of objects tend to change & the operations on them do not.

- **Syntax seperate from interpretation** is a different style, seperates the processing of ASTs from creating/definition of the ASTs.
  - method written for each interpretation with clauses for each kind.
  - interpretations are gathered together
  - Adding a new interpretation means adding a new method
  - Adding a new kind means editing _all_ the methods that represent interpretations
  - Better for _compilers_, makes more sense to fix the syntax and then provide different interpretations.

**Visitor Pattern** - used to avoid using the _instanceof_ operator and having each interpretation method looking like a _morass of clauses_..

- a **visitor** implements an interpretation. Contains a method for ech kind (i.e each _class of syntax tree node_)
- each syntax tree class has an _accept_ method that is called by the Visitor
- _accept_ method acts as a hook for _all_ interpretations.
- _accept_ method's only task is to call the visitor, parameterised by the _accept_ method's own type. Ensures the correct visitor method is invoked

  ---

### Symbol Tables

A.K.A **environment**

Maps identifiers to their types and locations.

As types, variables and functions are declared, they are added to this symbol table.

When an identifier is used, it is looked up in the symbol table.

Identifiers in modern languages tend to have **scopes** in which they are visible

**Environments** are a _set of bindings_

- _a → x_ denotes that _a_ is bound to _x_.
- σ0 = {g → string, a → int} denotes environment σ0 where _g_ is a string variable and _a_ is an integer variable.

A **hash table** with _external chaining_ and an **undo stack** can:
- give precedence where two or more environments contain different bindings for the same symbol.
- make sure we find correct binding
- allow us to efficiently discard environments

**Hash Table**
- hash function converts the symbole into an index in a table/array. SHou.d be capable of being evaluated quickly. Resulting indices should be randomly distributed across the table so it is unlikely two diff symbols will result in the same hash value.
- Each enty in hash table is a linked list of bindings..
  - when a binding is added, it is inserted at the _front_ of the linked list associated with the symbol's hash value.
  - when looking up a symbol in hash table, the linked list associated with the symbol's hash value is searched from th front, finding the most recent binding that involves that symbol.
  - Move a symbol successfully searched for to the front of the list to improve lookup time and make use of locality.

**Undo Stack**
- Whenever binding is added to Symbol Table, it is also pushed onto the undo stack
- when a new scope is created (eg by the '{' symbol in Java), a special marker is placed onto the undo stack
- When a scope is being destroyed (by '}'), entries on the undo stack are popped off and removed from the symbol table _until the special marker is popped off the undo stack_.

Symbol table bindings associate names with attributes.

- **variables**: type, procedure level, frame, offset
- **types**: type descriptor, data size/alignment
- **constants**: type, value
- **procedures**: formals (names/types), result type, block information, frame size

  ---

### Type Checking

One of the major semantic analysis operations

Type expressions are _textual representations_ for types:

- basic types: boolean, char, int, real
- type names
- constructed types...constructors applied to type expressions
  - arrays
  - products
  - records
  - pointers
  - functions

Type checking needs to determine **type equivalence**, two approaches:


- **Name equivalence**: each type name is a distict type
- **Structural equivalence**: two types equivalent if they have the same structure (after substituting type expressions for type names)

Most languages have _type overloading_
- can be resolved at compile-time by _type inference_, usually done bottom up. If f can be either int -> int or float -> float.. then f(42) has only one valid type, int!

**Polymorphic Functions**
- Ad-hoc polymorphism: on a case by case basis
- Parametric polymorphism: can take a type as an argument
  - often combined with type inference

---

# 6. Intermediate Code Generation

### **Intermediate Code**: a kind of abstract machine code which _does not rely on a particular target machine_ by _specifying the registers or memory locations_ to be used for each operation. Intermediate Code does not specify registers or memory locations

- ### Separates compilation into:

  - _mostly language-dependant_ Front-End
  - _mostly machine-dependant_ Back-End

---

# 7. Code Optimisation

### An optional phase used to _improve the intermediate code_ to make it **run faster** and/or **use less memory**

---

# 8. Code Generation

### This phase _translates intermediate code_ into **object code**, allocating memory locations for data and selecting registers.

- ### This can include a **linking phase** when the language allows the source code to be _written in separate files_

---