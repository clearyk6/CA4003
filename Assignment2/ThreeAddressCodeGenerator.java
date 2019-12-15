import java.util.*;

public class ThreeAddressCodeGenerator implements CCALLangVisitor {
    private static int labelCount = 1;
    private static int tmpCount = 1;

    private static boolean labelOnLine = false;

    private void printLabel(String label) {
        System.out.printf("\n%s", label + ":\n");
        labelOnLine = true;
        labelCount++;
    }

    private void printInstruction(String inst) {
        System.out.printf("          %s\n", inst);
    }

    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    public Object visit(ASTProgram node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTVarDecl node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTConstDecl node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String val = (String) node.jjtGetChild(2).jjtAccept(this, data);

        printInstruction(id + " = " + val);

        return null;
    }

    public Object visit(ASTConstAssign node, Object data) {
        return node.value + " " + ((String) node.jjtGetChild(0).jjtAccept(this, data));
    }

    public Object visit(ASTFunction node, Object data) {
        SimpleNode id = (SimpleNode) node.jjtGetChild(1);
        printLabel((String) id.value);
        node.childrenAccept(this, data);
        return null;
    }

    // TODO check this
    public Object visit(ASTFunctionReturn node, Object data) {
        String in = "return " + ((String) node.jjtGetChild(0).jjtAccept(this, data));
        printInstruction(in);
        return (Object) in;
    }

    public Object visit(ASTType node, Object data) {
        return node.value;
    }

    public Object visit(ASTParamList node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTNempParamList node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTMain node, Object data) {
        printLabel("main");
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTStatement node, Object data) {
        String stm = (String) node.value;
        String beginLabel;
        String condition;
        switch (stm) {
            case "while":
                beginLabel = "L" + labelCount;
                printLabel(beginLabel);
                condition = (String) node.jjtGetChild(0).jjtAccept(this, data);
                printInstruction("ifFalse " + condition + " goto " + beginLabel);

                node.jjtGetChild(1).jjtAccept(this, data);

                return null;

            case "if": // TODO
                beginLabel = "L" + labelCount;
                String elseLabel = "L" + (labelCount + 1);
                printLabel(beginLabel);
                condition = (String) node.jjtGetChild(0).jjtAccept(this, data);
                printInstruction("ifFalse " + condition + " goto " + elseLabel);

                node.jjtGetChild(1).jjtAccept(this, data);
                printLabel(elseLabel);

                return null;

            default:
                return null;
        }
    }

    public Object visit(ASTAssign node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String in = child1 + " " + node.value + " " + child2;
        printInstruction(in);

        return (Object) in;
    }

    private Object visitOpHelper(SimpleNode node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String tmp = "t" + tmpCount;
        printInstruction(tmp + " = " + child1 + " " + node.value + " " + child2);
        return tmp;
    }

    public Object visit(ASTPlus node, Object data) {
        return visitOpHelper(node, data);
    }

    public Object visit(ASTMinus node, Object data) {
        return visitOpHelper(node, data);
    }

    public Object visit(ASTNumber node, Object data) {
        return node.value;
    }

    public Object visit(ASTBool node, Object data) {
        return node.value;
    }

    public Object visit(ASTOr node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTAnd node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTEqual node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTNotEqual node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTLessThan node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTLessEqual node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTGreaterThan node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTGreaterEqual node, Object data) {

        return visitOpHelper(node, data);
    }

    public Object visit(ASTArgList node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            printInstruction("param " + ((String) node.jjtGetChild(i).jjtAccept(this, data)));
        }
        return (Object) 1;
    }

    public Object visit(ASTId node, Object data) {
        return node.value;
    }
}