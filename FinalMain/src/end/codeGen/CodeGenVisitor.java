package end.codeGen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import end.analysis.isClass;
import end.analysis.isMethod;
import end.analysis.Record;
import end.analysis.SymbolTable;
import end.analysis.isVariable;
import rmn.grammar.JavaBaseVisitor;
import rmn.grammar.JavaParser.AddExpressionContext;
import rmn.grammar.JavaParser.BoolLitExpressionContext;
import rmn.grammar.JavaParser.ClassDeclarationContext;
import rmn.grammar.JavaParser.DivExpressionContext;
import rmn.grammar.JavaParser.FieldDeclarationContext;
import rmn.grammar.JavaParser.IdentifierContext;
import rmn.grammar.JavaParser.IdentifierExpressionContext;
import rmn.grammar.JavaParser.IfElseStatementContext;
import rmn.grammar.JavaParser.IntegerLitExpressionContext;
import rmn.grammar.JavaParser.LessThanExpressionContext;
import rmn.grammar.JavaParser.MainClassContext;
import rmn.grammar.JavaParser.MainMethodContext;
import rmn.grammar.JavaParser.MethodBodyContext;
import rmn.grammar.JavaParser.MethodCallExpressionContext;
import rmn.grammar.JavaParser.MethodCallParamsContext;
import rmn.grammar.JavaParser.MethodDeclarationContext;
import rmn.grammar.JavaParser.MulExpressionContext;
import rmn.grammar.JavaParser.NotExpressionContext;
import rmn.grammar.JavaParser.ObjectInstantiationExpressionContext;
import rmn.grammar.JavaParser.PrintStatementContext;
import rmn.grammar.JavaParser.ReturnStatementContext;
import rmn.grammar.JavaParser.StatementContext;
import rmn.grammar.JavaParser.SubExpressionContext;
import rmn.grammar.JavaParser.ThisExpressionContext;
import rmn.grammar.JavaParser.VariableAssignmentStatementContext;
import rmn.grammar.JavaParser.WhileStatementContext;

@SuppressWarnings("rawtypes")
public class CodeGenVisitor extends JavaBaseVisitor implements ICodes {

	private SymbolTable symtab; // From previous iteration
	private isMethod currentMethod; // See visitMethodDecl()
	private String currentClass; // See visitClassDecl()
	private ClassFile classFile; // To be saved on disk
	private int next; // instruction counter

	// -----CONSTRUCTOR-------
	public CodeGenVisitor(SymbolTable visitedST) {
		this.setSymtab(visitedST);
		this.setClassfile(new ClassFile());
		setIC(0);
	}

	private String errorMessage(ParseTree ctx) {
		return "[err - @ " + ((ParserRuleContext) ctx).getStart().getLine()
				+ ":"
				+ ((ParserRuleContext) ctx).getStop().getCharPositionInLine()
				+ "] ";

	}

	// ---- METHODS-----------
	private void addInstruction(int code, Object arg) {
		Instruction inst = new Instruction(code, arg);
		Method method = classFile.getMethods().get(
				currentClass + "." + currentMethod.getId());
		method.setInstList(inst);

	}

	public void writeToFile(String fileName) {
		FileOutputStream fileOut;
		ObjectOutputStream objectOut;
		try {
			File file = new File(fileName+".pepe");
			file.getParentFile().mkdirs();
			fileOut = new FileOutputStream(file,false);
			objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(classFile);
			objectOut.close();
		} catch (Exception e) {
			System.err.println("Error in creating \'"+fileName+".pepe\' ");
			e.printStackTrace();
		}
		
		System.out.println("\n \t \'"+fileName+".pepe\' GENERATED SUCCESSFULY.");

	}

	// ----GETTERS/SETTERS------
	//public ClassFile getClassfile() {return classFile;}

	public void setClassfile(ClassFile classfile) {
		this.classFile = classfile;
	}

	//public String getCurrentClass() {return currentClass;}

	public void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;
	}

	public void setCurrentMethod(isMethod currentMethod) {
		this.currentMethod = currentMethod;
	}

	//public SymbolTable getSymtab() {return symtab;}

	public void setSymtab(SymbolTable symtab) {
		this.symtab = symtab;
	}

	//public int getIC() {return next;}

	public void setIC(int iC) {
		next = iC;
	}

	@Override
	public Object visitMainClass(MainClassContext ctx) {
		setCurrentClass(ctx.getChild(1).getText());
		symtab.enterScope();
		visitMainMethod((MainMethodContext) ctx.getChild(3));
		symtab.exitScope();
		addInstruction(STOP, null);
		next++;
		return null;
	}

	@Override
	public Object visitMainMethod(MainMethodContext ctx) {
		setCurrentMethod((isMethod) symtab.lookup(currentClass + ".main"));
		Method method = new Method();
		classFile.addMethod(currentClass + "." + currentMethod.getId(), method);
		classFile.setMainMethod(method);
		symtab.enterScope();
		for (int i = 0; i < ctx.getChildCount(); i++) {
			if (ctx.getChild(i) instanceof StatementContext) {
				visitStatement((StatementContext) ctx.getChild(i));
			}
		}
		symtab.exitScope();
		return null;
	}

	@Override
	public Object visitClassDeclaration(ClassDeclarationContext ctx) {
		int i = 1;
		int n = ctx.getChildCount();
		currentClass = ctx.getChild(i++).getText(); // Class name
		i++;
		symtab.enterScope();
		for (; i < n - 1; i++) {
			ParseTree child = ctx.getChild(i);
			if (child instanceof FieldDeclarationContext)
				System.err.println(errorMessage(child)
						+ " In Tiny Java We should NOT have Field-Declaration");
			else
				visitMethodDeclaration((MethodDeclarationContext) child);
		}
		symtab.exitScope();
		return null;
	}

	@Override
	public Object visitMethodDeclaration(MethodDeclarationContext ctx) {
		int i = 0;
		if (ctx.getChild(0) instanceof TerminalNodeImpl
				&& ctx.getChild(0).getText().equals("public")) {
			i++; // skip 'public'
		}
		i++; // skip type
		String mName = ctx.getChild(i++).getText(); // Method name
		setCurrentMethod((isMethod) symtab.lookup(currentClass + "."
				+ mName));
		currentMethod.setList(currentMethod.getParamList());
		currentMethod.setList(currentMethod.getVarList());
		/* classFile */
		Method method = new Method();
		method.setParamList(currentMethod.getParamList());
		method.setList(currentMethod.getList());
		for (int k = 0; k < method.getList().size(); k++) {
			// Initialized the varList with 0 as default value with size of the
			// number of params and vars
			method.getVarValues().add(k, 0);
		}
		classFile.addMethod(currentClass + "." + currentMethod.getId(), method);

		next = 0;
		for (int j = currentMethod.getParamList().size() - 1; j >= 0; j--) {
			// store assign data in related parameter
			addInstruction(ISTORE, j);
			next++;
		}
		symtab.enterScope(); // Enter method scope
		while (!(ctx.getChild(i) instanceof MethodBodyContext)) {
			i++;
		}
		visitMethodBody((MethodBodyContext) ctx.getChild(i));
		symtab.exitScope(); // Exit method scope
		return null;
	}

	@Override
	public Object visitMethodCallExpression(MethodCallExpressionContext ctx) {
		int i = 0;
		String className = (String) visit(ctx.getChild(i++));
		isClass classRec = (isClass) symtab.lookup(className);
		isMethod mRec = null;
		int n = ctx.getChildCount();
		i++; // skip '.' after (class/method) instantiation
		for (; i < n; i++) {
			String mName = ctx.getChild(i).getText();
			mRec = (isMethod) classRec.getMethodList().get(mName);
			i += 1; // after method name we have methodCallParams in shape
					// '('(expression(',' expression)*)? ')'
			visitMethodCallParams((MethodCallParamsContext) ctx.getChild(i));
			addInstruction(INVOKEVIRTUAL, className + "." + mName);
			next++;
			className = mRec.getType();
		}
		return null;
	}

	@Override
	public Object visitVariableAssignmentStatement(
			VariableAssignmentStatementContext ctx) {
		String LHS = ctx.getChild(0).getChild(0).getText();
		isVariable lookup = (isVariable) symtab.lookup(LHS);
		visit(ctx.getChild(2));
		addInstruction(ISTORE, currentMethod.getList().indexOf(lookup));
		next++;
		return null;
	}

	@Override
	public Object visitIdentifier(IdentifierContext ctx) {
		String varName = ctx.getText();
		Record varRec = symtab.lookup(varName);
		if (varRec == null)
			return null;
		return null;
	}
	
	@Override
	public Object visitThisExpression(ThisExpressionContext ctx) {
		return symtab.getCurrentClass().getId();
	}

	@Override
	public Object visitIdentifierExpression(IdentifierExpressionContext ctx) {
		String varName = ctx.getText();
		Record varRec = symtab.lookup(varName);
		if (varRec == null)
			return null;
		addInstruction(ILOAD, currentMethod.getList().indexOf(varRec));
		next++;
		return null;
	}

	@Override
	public Object visitIntegerLitExpression(IntegerLitExpressionContext ctx) {
		int value = Integer.parseInt(ctx.getText());
		addInstruction(ICONST, value);
		next++;
		return null;
	}

	@Override
	public Object visitBoolLitExpression(BoolLitExpressionContext ctx) {
		String value = ctx.getText();
		if (value.equals("true")) {
			addInstruction(ICONST, 1);
		}
		if (value.equals("false")) {
			addInstruction(ICONST, 0);
		}
		next++;
		return null;
	}

	@Override
	public Object visitReturnStatement(ReturnStatementContext ctx) {
		visit(ctx.getChild(1));
		addInstruction(IRETURN, null);
		next++;
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatementContext ctx) {
		int go_to = next;
		visit(ctx.getChild(2));
		int if_fale = next;
		addInstruction(IF_FALSE, null);
		next++;
		visit(ctx.getChild(4)); // Generate While-body
		addInstruction(GOTO, go_to);
		next++;
		Method method = classFile.getMethods().get(
				currentClass + "." + currentMethod.getId());
		method.getInstList().get(if_fale).setArgument(next);
		return null;
	}

	@Override
	public Object visitPrintStatement(PrintStatementContext ctx) {
		visit(ctx.getChild(2));
		addInstruction(PRINT, null);
		next++;
		return null;
	}
	
	

	@Override
	public Object visitIfElseStatement(IfElseStatementContext ctx) {
		visit(ctx.getChild(2)); // Generate condition
		int ifLabel = next;
		addInstruction(IF_FALSE, null);
		next++;
		visit(ctx.getChild(4)); // Generate if-body
		int gotoLabel = next;
		addInstruction(GOTO, null);
		next++;
		Method method = classFile.getMethods().get(
				currentClass + "." + currentMethod.getId());
		method.getInstList().get(ifLabel).setArgument(next);
		if (ctx.getChildCount() > 4) {
			visit(ctx.getChild(6)); // Generate else-body
			method.getInstList().get(gotoLabel).setArgument(next);
		}
		return null;
	}

	@Override
	public Object visitLessThanExpression(LessThanExpressionContext ctx) {
		int n = ctx.getChildCount();
		visit(ctx.getChild(0));
		if (n > 3) {
			visit(ctx.getChild(3));
		} else {
			visit(ctx.getChild(2));
		}
		addInstruction(ILT, null);
		next++;
		return null;
	}

	@Override
	public Object visitMulExpression(MulExpressionContext ctx) {
		visit(ctx.getChild(0));
		visit(ctx.getChild(2));
		addInstruction(IMUL, null);
		next++;
		return null;
	}

	@Override
	public Object visitAddExpression(AddExpressionContext ctx) {
		visit(ctx.getChild(0));
		visit(ctx.getChild(2));
		addInstruction(IADD, null);
		next++;
		return null;
	}

	@Override
	public Object visitDivExpression(DivExpressionContext ctx) {
		visit(ctx.getChild(0));
		visit(ctx.getChild(2));
		addInstruction(IDIV, null);
		next++;
		return null;
	}

	@Override
	public Object visitSubExpression(SubExpressionContext ctx) {
		visit(ctx.getChild(0));
		visit(ctx.getChild(2));
		addInstruction(ISUB, null);
		next++;
		return null;
	}

	@Override
	public Object visitNotExpression(NotExpressionContext ctx) {
		visit(ctx.getChild(1));
		addInstruction(INOT, null);
		next++;
		return null;
	}

	@Override
	public Object visitObjectInstantiationExpression(
			ObjectInstantiationExpressionContext ctx) {
		return ctx.getChild(1).getText();
	}

}
