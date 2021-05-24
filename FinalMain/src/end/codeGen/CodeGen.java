package end.codeGen;

import java.io.IOException;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;

import end.analysis.SymbolTable;
import end.analysis.SymbolTableVisitor;
import rmn.grammar.JavaLexer;
import rmn.grammar.JavaParser;

public class CodeGen {

	public static void main(String[] args) {

		CharStream input = null;
		String fileName = "";
		if (args.length > 0) {
			try {
				input = CharStreams.fromFileName(args[0]);
				fileName = args[0].substring(0, args[0].lastIndexOf('.'));
			} catch (IOException e) {
				System.err.println("THE GIVEN FILE PATH IS WRONG!");
				return;
			}
		} else {

			try {
				String file = "./FinalMain/samples/OhGodPleaseNo.java";
				input = CharStreams.fromFileName(file);
				fileName = file.substring(0, file.lastIndexOf('.'));

			} catch (IOException e) {
				System.err.println("THE GIVEN FILE PATH IS WRONG!!");
				return;
			}
		}

		JavaLexer lexer = new JavaLexer(input);
		JavaParser parser = new JavaParser(new BufferedTokenStream(
				lexer));
		ParseTree tree = parser.program();
		Trees.inspect(tree, parser);

		SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
		SymbolTable visitedST = (SymbolTable) symbolTableVisitor.visit(tree);
		if (symbolTableVisitor.getErrorFlag()) {
			System.err
					.println("THE PROGRAM CONTAINS ERRORS!");
		} else {
			visitedST.printTable();
			visitedST.resetTable();

			CodeGenVisitor cgv = new CodeGenVisitor(visitedST);
			cgv.visit(tree);
			cgv.writeToFile(fileName);
			}
		}
}
