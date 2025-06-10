package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Lox {
    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    // Input Handling/Code Execution Methods
    private static void runFile(String path) throws IOException {
        // Read bytes from the provided path
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError)
            System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // This for loop is equivalent to "while (true){...}".
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            // Pressing Ctrl + D in a buffered reader stream sends an EOF. This if statement breaks if it detects an EOF.
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        /*
         To put it simply, tokens are what keywords in programming languages are called.
         (ex. "if", "static", "for", "print", etc.) Though, these are technically lexeme.
         Read more in 4.2 of Crafting Interpreters!
         */
        List<Token> tokens = scanner.scanTokens();

        // Temporarily just printing the tokens. Functionality will come later!
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    // Error Handling Methods
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}
