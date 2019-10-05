# CA4003 Compiler Construction

## David Sinclair

office: L253

Phone: 5510

Email: David.Sinclair@computing.dcu.ie

### [Course web page](http://www.computing.dcu.ie/~davids/CA4003/CA4003.html)

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

- **Epsilon**: denotes the empty string with e

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

  - all the states that can be reached from state S **without consuming any symbols** (using _epsilon_ (ε) edges only)

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

---

# 3. Parsing

### Regular expressions can't count. :(( !
  
  - they cannot balance parentheses..

  - therefore, we need to **add mututal recurscion**
    
    - this gets rid of alternation (except at top level)

    - this gets rid of Kleene Closure

    - .. **this becomes a Context Free Grammar**

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

  - "What if answer and y are integers and x is a float??"

- ### **In an interpreter**

  - _Evaluates the source program_ stored in the Abstract Syntax Tree

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