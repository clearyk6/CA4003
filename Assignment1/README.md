# CA4003 Assignment 1

Kevin Cleary, 16373026

## Implementing a Lexical/Syntax analyser for CCAL in JavaCC

###Resources Used:

### Section 1: Options

I included JAVA_UNICODE_ESCAPE = true

I also included the line IGNORE_CASE = true to address the case

### Section 2: User Code

I based this section largely on the example in the JavaCC notes on the Straight Line Programming language, changing SLP in each case to CCAL.   

The 

### Section 3: Token Definitions

Using the CCAL language specification, I first defined all the keyword and symbol tokens. These were all straightforward. For the number/identifier regex, I incorporated <#DIGIT> and <#LETTER> tokens in order to recognise standalone digits and characters. For the <INT> token I added in the option to recognise a standalone 0, as long as it is not the first in a string of more than one digits. For the <#LETTER> token, I have included both lower and uppercase letters in the regex. If I were to only recognise a single case, the IGNORE_CASE option mentioned above would still allow for non case-sensitivity.

I then tackled the SKIP tokens, but made sure to include them above the TOKEN specifications as to ensure whitespace and comments are ignored before they are recognised. I based the multi-line comment and <IN_COMMENT> state on the SLP language in the JavaCC example in CA4003 notes. I also added in the regex for single line comments terminating after a newline.

### Section 4: Grammar

I approached the grammar by first rewriting the grammar in the CCAL description to Backus Naur form. I then used the CA4003 example to translate the grammar in backus naur form into it's equivalent JavaCC syntax. I wrote the grammar exactly as it was specified to start - and using IntelliJ's JavaCC plugin the obvious instance of Left Recursion was flagged between the expression() and fragment() nonterminals. I found the best way to remove this left recursion was to remove the fragment() nonterminal completely as it only existed within the scope of an expression(). I then had to seperate expression cases into those with < ID > tokens as their first and those without. This gave rise to a further expressionWithId() nonterminal.

I then ran JavaCC though the .jj file and this flagged the lines where Lookahead of 1 was not possible. For each 'list' nonterminal with a not empty (nemp) case, I added in a second nonterminal to add left factoring into the grammar and acheive LL(1).

The condition nonterminal faced conflict between FIRST(condition()) and FIRST(expression()), both having < LEFTPAREN > in their FIRST set. To address this conflict, I decided to only let an simple expression in it's FIRST set. I seperated the expression() nonterminal into simple and not simple (containing < LPAREN > as their FIRST symbol). I also used two condition cases, with ConditionA being the last item in each condition option. ConditionA would either compare another condition with || or && or else return an empty array.

