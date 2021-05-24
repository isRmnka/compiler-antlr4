package symbolTable;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import rmn.grammar.JavaBaseVisitor;
import rmn.grammar.JavaParser.*;

@SuppressWarnings("rawtypes")
public class SymbolTableVisitor extends JavaBaseVisitor {
	SymbolTable symbolTable;
	isClass currentClass;
	isMethod currentMethod;
	boolean errorFlag;

	public SymbolTableVisitor() {
		super();
		this.symbolTable = new SymbolTable();
		this.currentClass = null;
		this.currentMethod = null;
		this.errorFlag = false;
	}

	@Override
	public Object visitProgram(ProgramContext ctx) {
		int i = 0;
		int n = ctx.getChildCount();
		visitMainClass((MainClassContext) ctx.getChild(i++));
		for (; i < n; i++)
			visitClassDeclaration((ClassDeclarationContext) ctx.getChild(i));
		return symbolTable;
	}

	@Override
	public Object visitMainClass(MainClassContext ctx) {
		int i = 0;
		i++; // skip 'CLASS'
		String cName = ctx.getChild(i++).getText();

		currentClass = new isClass(cName, cName);
		symbolTable.put(cName, currentClass);
		symbolTable.enterScope();
		symbolTable.setCurrentClass(currentClass);
		i++;// skip '{'
		visitMainMethod((MainMethodContext) ctx.getChild(i++));
		i++;// skip '}'
		symbolTable.exitScope();
		return null;
	}

	@Override
	public Object visitClassDeclaration(ClassDeclarationContext ctx) {
		int i = 0;
		int n = ctx.getChildCount();

		i++;// skip 'CLASS'
		String cName = ctx.getChild(i++).getText();
		currentClass = new isClass(cName, cName);
		if (symbolTable.lookup(cName) != null) {
			errorFlag = true;
			System.err.println("[Duplicated]: Class name \"" + cName
					+ "\" already defined");
		} else {
			symbolTable.put(cName, currentClass);
			symbolTable.enterScope();
			symbolTable.setCurrentClass(currentClass);
		}
		i++; // skip '{'
		for (; i < n - 1; i++) {
			ParseTree child = ctx.getChild(i);
			if (child instanceof FieldDeclarationContext)
				visitFieldDeclaration((FieldDeclarationContext) child);
			else
				visitMethodDeclaration((MethodDeclarationContext) child);
		}
		i++;// skip '}'
		symbolTable.exitScope();
		return null;
	}

	@Override
	public Object visitMainMethod(MainMethodContext ctx) {
		int i = 0;
		currentMethod = new isMethod("main", null);
		if (symbolTable.lookup(currentClass.getId() + ".main") != null) {
			errorFlag = true;
			System.err.println("main method already defined!");
		} else {
			symbolTable.put(currentClass.getId() + ".main", currentMethod);
			currentClass.putMethod("main", currentMethod);
			i += 11; // skip 'public' 'static' 'void' 'main' '(' 'String' '['
						// ']'
						// identifier ')' '{'
			symbolTable.enterScope();
			while (ctx.getChild(i) instanceof StatementContext) {
				visitStatement((StatementContext) ctx.getChild(i++));
			}

			i++;// skip '}'
			symbolTable.exitScope();
		}
		return null;
	}

	@Override
	public Object visitStatement(StatementContext ctx) {
		ParseTree child = ctx.getChild(0);
		visit(child);
		return null;
	}

	@Override
	public Object visitFieldDeclaration(FieldDeclarationContext ctx) {
		int i = 0;
		String type = (String) visitType((TypeContext) ctx.getChild(i++));
		String name = ctx.getChild(i++).getText();
		i++; // skip SC
		isVariable var = new isVariable(name, type);
		if (symbolTable.lookup(name) != null) {
			errorFlag = true;
			System.err.println("[Duplicated] Field Variable \"" + name
					+ "\" already defined");
		} else {
			symbolTable.put(name, var);
			currentClass.putField(name, var);
		}
		return null;
	}

	@Override
	public Object visitLocalDeclaration(LocalDeclarationContext ctx) {
		int i = 0;
		String type = (String) visitType((TypeContext) ctx.getChild(i++));
		String name = ctx.getChild(i++).getText();
		i++; // skip SC
		isVariable var = new isVariable(name, type);
		if (symbolTable.lookup(name) != null) {
			errorFlag = true;
			System.err.println("[Duplicated] Field Variable \"" + name
					+ "\" already defined");
		} else {
			symbolTable.put(name, var);
			currentMethod.putVar(var);
		}
		return null;
	}

	@Override
	public Object visitMethodDeclaration(MethodDeclarationContext ctx) {
		int i = 0;
		if (ctx.getChild(0) instanceof TerminalNodeImpl
				&& ctx.getChild(0).getText().equals("public")) {
			i++; // skip 'public'

		}
		ParseTree methodReturnType = ctx.getChild(i++);
		String returnType;
		if (methodReturnType instanceof TerminalNodeImpl) {
			returnType = null;
		} else {
			returnType = (String) visitType((TypeContext) methodReturnType);
		}
		String mName = ctx.getChild(i++).getText();
		if (currentClass.getId().equals(mName)) {
			errorFlag = true;
			System.err
					.println("The method name is the same as class name! we do not have constructors in MiniJava");
		}
		i++;// skip '('
		currentMethod = new isMethod(mName, returnType);
		if (symbolTable.lookup(currentClass.getId() + "." + mName) != null) {
			errorFlag = true;
			System.err.println("[Duplicated] Method name \"" + mName
					+ "\" already defined");
		} else {
			symbolTable.put(currentClass.getId() + "." + mName, currentMethod);
			currentClass.putMethod(mName, currentMethod);
			symbolTable.enterScope();
			symbolTable.setCurrentClass(currentClass);
			if (ctx.getChild(i) instanceof ParameterListContext) {
				visitParameterList((ParameterListContext) ctx.getChild(i++));
			}
			i += 2;// skip ')' '{'
			visitMethodBody((MethodBodyContext) ctx.getChild(i++));
			i++; // skip '}'
			symbolTable.exitScope();
		}
		return null;
	}

	@Override
	public Object visitType(TypeContext ctx) {

		return ctx.getText();
	}

	@Override
	public Object visitParameterList(ParameterListContext ctx) {
		int n = ctx.getChildCount();
		for (int i = 0; i < n; i += 2)
			// skipping ','s
			visitParameter((ParameterContext) ctx.getChild(i));
		return null;
	}

	@Override
	public Object visitParameter(ParameterContext ctx) {
		int i = 0;
		String type = (String) visitType((TypeContext) ctx.getChild(i++));
		String name = ctx.getChild(i++).getText();
		isVariable var = new isVariable(name, type);
		if (symbolTable.lookup(name) != null) {
			errorFlag = true;
			System.err.println("[Duplicated] parameter name \"" + name
					+ "\'\" already defined");
		} else {
			symbolTable.put(name, var);
			currentMethod.putParam(var);// Store Parameter in method
		}
		return null;
	}

	@Override
	public Object visitMethodBody(MethodBodyContext ctx) {
		int i = 0;
		int n = ctx.getChildCount();
		for (; i < n; i++) {
			ParseTree child = ctx.getChild(i);
			// just visit whatever it is :)
			visit(child);
		}
		return null;
	}

	@Override
	public Object visitNestedStatement(NestedStatementContext ctx) {
		int i = 0;
		int n = ctx.getChildCount();
		i++;
		for (; i < n - 1; i++) {
			if (ctx.getChild(i) instanceof StatementContext)
				visitStatement((StatementContext) ctx.getChild(i));
		}
		return null;
	}

	@Override
	public Object visitIdentifier(IdentifierContext ctx) {
		return ctx.getText();
	}
	
	@Override
	public Object visitIdentifierExpression(IdentifierExpressionContext ctx) {
		if(ctx.getChildCount()>1) return ctx.getChild(1).getText();
		return ctx.getChild(0).getText();
		
	}

}
