package end.codeGen;

public interface ICodes {

	final static int ILOAD = 1;	// Push integer value stored in local variable n.
	final static int IDIV = 2; // Pop value v1, Pop value v2, Push v2 / v1.
	final static int IMUL = 3; // Pop value v1, Pop value v2, Push v2 * v1.
	final static int IADD = 4; // Pop value v1, Pop value v2, Push v2 + v1.
	final static int ISUB = 5; // Pop value v1, Pop value v2, Push v2 - v1.
	final static int ISTORE = 6; // Pop value v and store it in local variable n.
	final static int ICONST = 7; // Push integer value v.
	final static int ILT = 8; // Pop value v1, Pop value v2, Push 1 if v2 < v1 else Push 0.
	final static int GOTO = 9; // Jump to instruction i.
	final static int IEQ = 10; // Pop value v1, Pop value v2, Push 1 if v1 = v2 else Push 0.
	final static int IAND = 11; // Pop value v1, Pop value v2, Push 0 if v1 * v2 = 0 else Push 1.
	final static int IOR = 12;	// Pop value v1, Pop value v2, Push 0 if v1 + v2 = 0 else Push 1.
	final static int INOT = 13; // Pop value v, Push 1 if v = 0 else Push 0.
	final static int IF_FALSE = 14; // Pop value v, if v = 0 jump to instruction i, else continue with next instruction.
	final static int IRETURN = 15; // Pop activation and continue.
	final static int STOP = 16; // Execution completed.
	final static int PRINT = 17; // Pop value and print.
	final static int INVOKEVIRTUAL = 18; //	Push current activation and switch to the method having qualified name m.
}
