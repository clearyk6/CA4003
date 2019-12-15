import java.util.*;

public class SemCheckVisitor implements CCALLangVisitor {

    private static String scope = "global";
    private static SymbolTable symbolTable;

    private static boolean declareBeforeUseCheck = true;
    private static boolean noDuplicateVarsCheck = true;
    private static boolean varAssignmentCheck = true;
    private static boolean constAssignmentCheck = true;
    private static boolean arithArgCheck = true;
    private static boolean booleanArgCheck = true;
    private static boolean functionForInvokationCheck = true;
    private static boolean functionArgCheck = true;
    private static boolean varReadWriteCheck = true;
    private static boolean functionCallCheck = true;

    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    public Object visit(ASTProgram node, Object data) {
        symbolTable = (SymbolTable) data;
        node.childrenAccept(this, data);

        noDuplicateVarsCheck = symbolTable.checkForDups();

        System.out.println();
        if (declareBeforeUseCheck) System.out.printf("Pass: Every identifier is declared within scope before its use.\n");
        if (noDuplicateVarsCheck) System.out.printf("Pass: No identifier is declared more than once in the same scope.\n");
        if (varAssignmentCheck) System.out.printf("Pass: LHS of every assignment is a variable of the correct type.\n");
        if (constAssignmentCheck) System.out.printf("Pass: LHS of every assignment is a constant of the correct type.\n");
        if (arithArgCheck) System.out.printf("Pass: args of all arithmetic operator are either integer constants/variables\n");
        if (booleanArgCheck) System.out.printf("Pass: args of all boolean operators are boolean vars/consts\n");
        if (functionForInvokationCheck) System.out.printf("Pass: there is a function for every invoked identifier\n");
        if (functionArgCheck) System.out.printf("Pass: every function call has the correct number of arguments\n");
        if (varReadWriteCheck) System.out.printf("Pass: every variable is both written to and read from\n");
        if (functionCallCheck) System.out.printf("Pass: every function is called\n");

        return DataType.Program;
    }

    public Object visit(ASTVarDecl node, Object data) {
        node.childrenAccept(this, data);
        return DataType.VarDecl;
    }

    public Object visit(ASTConstDecl node, Object data) {
        node.childrenAccept(this, data);
        SimpleNode simpleNode = (SimpleNode) node.jjtGetChild(0);
        DataType child1 = (DataType) node.jjtGetChild(1).jjtAccept(this, data);
        DataType child2 = (DataType) node.jjtGetChild(2).jjtAccept(this, data);

        if (child1 != child2) {
            constAssignmentCheck = false;
            System.out.printf("Error: Const \"%s\" assigned a value of the incorrect type.\n", simpleNode.jjtGetValue());
            System.out.printf("\tExpected type \"%s\" but instead encountered \"%S\".\n", child1, child2);
        }

        return DataType.ConstDecl;
    }

    public Object visit(ASTConstAssign node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        return child1DataType;
    }

    public Object visit(ASTFunction node, Object data) {
        SimpleNode id = (SimpleNode) node.jjtGetChild(1);
        scope = (String) id.value;
        node.childrenAccept(this, data);
        return DataType.Function;
    }

    public Object visit(ASTFunctionReturn node, Object data) {
        node.childrenAccept(this, data);
        scope = "global";
        return DataType.TypeUnknown;
    }

    public Object visit(ASTType node, Object data) {
        String value = (String) node.value;
        if (value.equals("boolean")) return DataType.Bool;
        if (value.equals("integer")) return DataType.Number;

        return DataType.TypeUnknown;
    }

    public Object visit(ASTParamList node, Object data) {
        node.childrenAccept(this, data);
        return DataType.ParamList;
    }

    public Object visit(ASTNempParamList node, Object data) {
        node.childrenAccept(this, data);
        return DataType.ParamList;
    }

    public Object visit(ASTMain node, Object data) {
        scope = "main";
        node.childrenAccept(this, data);

        scope = "global";
        return DataType.Main;
    }

    public Object visit(ASTStatement node, Object data) {
        node.childrenAccept(this, data);
        return DataType.Statement;
    }

    public Object visit(ASTAssign node, Object data) {
        SimpleNode child1 = (SimpleNode) node.jjtGetChild(0);
        SimpleNode child2 = (SimpleNode) node.jjtGetChild(1);
        DataType child1DataType = (DataType) child1.jjtAccept(this, data);
        DataType child2DataType = (DataType) child2.jjtAccept(this, data);

        if (child1DataType != DataType.TypeUnknown && child1DataType != child2DataType) {
            varAssignmentCheck = false;
            System.out.println("Error: Var \"" + child1.value + "\" was assigned a value of the wrong type.");
            System.out.printf("\tExpected type \"%s\" but instead encountered \"%S\".\n", child1DataType, child2DataType);
        }

        node.childrenAccept(this, data);
        return DataType.Assign;
    }

    public Object visit(ASTNumber node, Object data) {
        return DataType.Number;
    }

    public Object visit(ASTBool node, Object data) {
        return DataType.Bool;
    }

    private DataType getArithOpType(SimpleNode node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if ((child1DataType == DataType.Number) && (child2DataType == DataType.Number)) return DataType.Number;

        arithArgCheck = false;
        System.out.println("Error: Non numeric types used in an arithmetic operation " + node);
        System.out.printf("\tExpected two integer arguments but encountered \"%s\" and \"%s\" instead.\n", child1DataType, child2DataType);
        return DataType.TypeUnknown;
    }

    public Object visit(ASTPlus node, Object data) {
        DataType plusOp = getArithOpType(node, data);
        return plusOp;
    }

    public Object visit(ASTMinus node, Object data) {
        DataType minusOp = getArithOpType(node, data);
        return minusOp;
    }

    private DataType getEquivalenceOpType(SimpleNode node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if (child1DataType == child2DataType) return child1DataType;

        arithArgCheck = false;
        System.out.println("Error: incompatible types in comparison op.");
        System.out.printf("\tEncountered \"%s\" and \"%s\".", child1DataType, child2DataType);
        return DataType.TypeUnknown;
    }

    private DataType getCompOpType(SimpleNode node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if ((child1DataType == DataType.Number) && (child2DataType == DataType.Number)) return DataType.Bool;

        arithArgCheck = false;
        System.out.println("Error: Non numeric types used in compound operation " + node);
        System.out.printf("\tWas expecting two integer arguments but encountered \"%s\" and \"%s\" instead.", child1DataType, child2DataType);
        return DataType.TypeUnknown;
    }

    public Object visit(ASTEqual node, Object data) {
        DataType equalOp = getEquivalenceOpType(node, data);
        return equalOp;
    }

    public Object visit(ASTNotEqual node, Object data) {
        DataType notOp = getEquivalenceOpType(node, data);
        return notOp;
    }

    public Object visit(ASTLessThan node, Object data) {
        DataType lessThanOp = getCompOpType(node, data);
        return lessThanOp;
    }

    public Object visit(ASTLessEqual node, Object data) {
        DataType lessEqualOp = getCompOpType(node, data);
        return lessEqualOp;
    }

    public Object visit(ASTGreaterThan node, Object data) {
        DataType greaterThanOp = getCompOpType(node, data);
        return greaterThanOp;
    }

    public Object visit(ASTGreaterEqual node, Object data) {
        DataType greaterEqual = getCompOpType(node, data);
        return greaterEqual;
    }

    private DataType getLogicalOpType(SimpleNode node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if ((child1DataType == DataType.Bool) && (child2DataType == DataType.Bool)) return DataType.Bool;

        booleanArgCheck = false;
        System.out.println("Error: Non boolean types used in logical comparison " + node);
        System.out.printf("\tExpected two boolean arguments but encountered \"%s\" and \"%s\" instead.", child1DataType, child2DataType);
        return DataType.TypeUnknown;
    }

    public Object visit(ASTOr node, Object data) {
        DataType orOp = getLogicalOpType(node, data);
        return orOp;
    }

    public Object visit(ASTAnd node, Object data) {
        DataType andOp = getLogicalOpType(node, data);
        return andOp;
    }

    public Object visit(ASTArgList node, Object data) {
        node.childrenAccept(this, data);
        return DataType.ArgList;
    }

    public Object visit(ASTId node, Object data) {
        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        String type = parent.toString();

        if (type != "varDecl" && type != "ConstDecl" && type != "Func") {
            String value = (String) node.jjtGetValue();
            boolean isInScope = symbolTable.inScope(value, scope);
            if (isInScope) {
                String dataType = symbolTable.typeLookup(value, scope);
                switch (dataType) {
                    case "integer": return DataType.Number;
                    case "boolean": return DataType.Bool;
                    default: return DataType.TypeUnknown;
                }
            } else {
                boolean inGlobal = symbolTable.inScope(value, "global");
                if (inGlobal) {
                    String dataType = symbolTable.typeLookup(value, "global");
                    switch (dataType) {
                        case "integer": return DataType.Number;
                        case "boolean": return DataType.Bool;
                        default: return DataType.TypeUnknown;
                    }
                }
            }
        }
        
        return DataType.TypeUnknown;
    }
}