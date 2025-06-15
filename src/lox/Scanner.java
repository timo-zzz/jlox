package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

class Scanner {
    private final String sourceCode;
    private final List<Token> tokens = new ArrayList<>();
    private int start; // First character in a lexeme
    private int current; // Current character
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // Start at beginning of next lexeme
            start = current;
            scanToken();
        }

        // Add EOF token at the end
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // Single Character Tokens
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;

            // Operators
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // Comments go until the end of the line.
                    while (peek() != '\n' && !isAtEnd())
                        advance(); // Ignore comments
                } else {
                    addToken(SLASH);
                }
                break;

            // Whitespace and Newline
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace and go back to beginning of loop; intended fall-through
                break;
            case '\n':
                line++;
                break;

            // Literals
            case '"':
                string();
                break;

            default:
                if (isDigit(c)) { // Check for number literals
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    // Throw an error for random characters
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String text = sourceCode.substring(start, current);
        TokenType type = keywords.get(text); // Check for any of the keywords in the hashmap

        if (type == null)
            type = IDENTIFIER;

        addToken(type);
    }

    private void number() {
        // Advance through the number
        while (isDigit(peek()))
            advance();

        // Look for a fractional parts
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            // Advance to end of the fractional part
            while (isDigit(peek()))
                advance();
        }

        // Add the token, add convert the lexeme to a double with parseDouble.
        addToken(NUMBER, Double.parseDouble(sourceCode.substring(start, current)));
    }

    private void string() {
        // Advance through the string
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') // Allow multiline strings
                line++;
            advance();
        }

        // Catch unterminated strings
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the quotes.
        String value = sourceCode.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (sourceCode.charAt(current) != expected)
            return false;

        current++; // Only advance if it matches
        return true;
    }

    private char peek() {
        if (isAtEnd())
            return '\0'; // Return null character

        return sourceCode.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= sourceCode.length())
            return '\0';
        return sourceCode.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= sourceCode.length();
    }

    private char advance() {
        return sourceCode.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = sourceCode.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }
}
