import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableVisitor;
import rmn.grammar.JavaLexer;
import rmn.grammar.JavaParser;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		CharStream input = null;
		if (args.length > 0) {
			try {
				input = CharStreams.fromFileName(args[0]);
			} catch (IOException e) {
				return;
			}
		} else {
			try {
				input = CharStreams.fromFileName("./MAIN/testFiles/factorial.java");
			} catch (IOException e) {
				return;
			}
		}

		JavaLexer lexer = new JavaLexer(input);
		JavaParser parser = new JavaParser(new BufferedTokenStream(lexer));
		ParseTree tree = parser.program();
		Trees.inspect(tree, parser);

		SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
		SymbolTable visitedST = (SymbolTable) symbolTableVisitor.visit(tree);
		System.out.println("Printing the Symbol Table:");
		visitedST.printTable();
		visitedST.resetTable();
		}

}
