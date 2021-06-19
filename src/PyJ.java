import java.util.LinkedList;

/*
    CONSTANTS
 */

class Constants {
    static String TT_INT = "INT";
    static String TT_FLOAT = "FLOAT";
    static String TT_PLUS = "PLUS";
    static String TT_MINUS = "MINUS";
    static String TT_MUL = "MUL";
    static String TT_DIV = "DIV";
    static String TT_LPAREN = "LPAREN";
    static String TT_RPAREN = "RPAREN";
}

/*
    EXCEPTIONS
 */

class IllegalCharException extends Exception {
    IllegalCharException(String message){
        super(message);
    }
}


/*
    POSITION
 */

class Position {
    int idx, ln, col;

    Position(int idx, int ln, int col){
        this.idx = idx;
        this.ln = ln;
        this.col = col;
    }

    public void advance(char currentChar){
        this.idx += 1;
        this.col += 1;

        if(currentChar == '\n'){
            this.ln += 1;
            this.col = 0;
        }
    }

    public Position copy(){
        return new Position(this.idx, this.ln, this.col);
    }

    @Override
    public String toString() {
        return "index:" + idx +
                ", line:" + ln +
                ", col:" + col;
    }
}


/*
    TOKENS
 */

class Token<T> {
    String type;
    T value;

    public Token(String type) {
        this.type = type;
    }

    public Token(String type, T value) {
        this.type = type;
        this.value = value;
    }

    public String getToken() {
        if(value != null) return type + ":" + value;
        return type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}

/*
    LEXER
 */

class Lexer {
    String text;
    Position pos;
    char currentChar;

    public Lexer(String text) {
        this.text = text;
        this.pos = new Position(-1, 0, -1);
        advance();
    }

    public void advance() {
        this.pos.advance(currentChar);
        currentChar = this.pos.idx < this.text.length() ? this.text.charAt(this.pos.idx) : '\0';
    }

    public LinkedList<Token<?>> makeTokens() throws IllegalCharException {
        LinkedList<Token<?>> tokens = new LinkedList<>();

        while(this.currentChar != '\0'){
            if(this.currentChar == ' ' || this.currentChar == '\t')
                advance();
            else if(Character.isDigit(this.currentChar)) {
                tokens.add(makeNumber());
                advance();
            }
            else if(this.currentChar == '+'){
                tokens.add(new Token<>(Constants.TT_PLUS));
                advance();
            } else if(this.currentChar == '-'){
                tokens.add(new Token<>(Constants.TT_MINUS));
                advance();
            } else if(this.currentChar == '*'){
                tokens.add(new Token<>(Constants.TT_MUL));
                advance();
            } else if(this.currentChar == '/'){
                tokens.add(new Token<>(Constants.TT_DIV));
                advance();
            } else if(this.currentChar == '('){
                tokens.add(new Token<>(Constants.TT_LPAREN));
                advance();
            } else if(this.currentChar == ')'){
                tokens.add(new Token<>(Constants.TT_RPAREN));
                advance();
            } else {
                // return error
                char ch = this.currentChar;
                advance();
                throw new IllegalCharException("Illegal Character " + "'" + ch + "' at position " + pos.toString());
            }
        }

        return tokens;
    }

    public Token<?> makeNumber(){
        StringBuilder numStr = new StringBuilder("");
        int dotCount = 0;

        while(this.currentChar != '\0' && (Character.isDigit(this.currentChar) || this.currentChar == '.')){
            if(this.currentChar == '.'){
                if(dotCount == 1) break;
                dotCount += 1;
                numStr.append(".");
            } else {
                numStr.append(this.currentChar);
            }
            advance();
        }

        if(dotCount == 0){
            return new Token<>(Constants.TT_INT, Integer.parseInt(numStr.toString()));
        } else {
            return new Token<>(Constants.TT_FLOAT, Float.parseFloat(numStr.toString()));
        }
    }
}

/**
 * NODES
 */

class Node {}

class NumberNode extends Node{
    Token<?> token;
    public NumberNode(Token<?> t){
        this.token = t;
    }

    @Override
    public String toString() {
        return "" + token;
    }
}

class BinOpNode<T, U> extends Node{
    T leftNode;
    U rightNode;
    Token<?> opToken;

    public BinOpNode(T leftNode, Token<?> opToken, U rightNode){
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.opToken = opToken;
    }

    @Override
    public String toString() {
        return "(" + leftNode +
                ", " + opToken +
                ", " + rightNode +
                ')';
    }
}

/**
 * PARSER
 */

class Parser {
    LinkedList<Token<?>> tokens;
    int tokIdx;
    Token<?> currToken;

    Parser(LinkedList<Token<?>> tokens){
        this.tokens = tokens;
        this.tokIdx = 0;
        currToken = tokens.get(0);
    }

    public Token<?> advance(){
        this.tokIdx += 1;
        if(tokIdx < tokens.size()){
            currToken = tokens.get(tokIdx);
        }
        return currToken;
    }

    public Node factor(){
        Token<?> tok = this.currToken;
        if(tok.type.equals(Constants.TT_INT) || tok.type.equals(Constants.TT_FLOAT)){
            advance();
            return new NumberNode(tok);
        }
        return null;
    }

    public Node term(){
        Node left = factor();

        while(this.currToken.type.equals(Constants.TT_MUL) || this.currToken.type.equals(Constants.TT_DIV)){
           Token<?> opToken = currToken;
           advance();
           Node right = factor();
           left = new BinOpNode<>(left, opToken, right);
        }
        return left;
    }

    public Node expression(){
        Node left = term();

        while(this.currToken.type.equals(Constants.TT_PLUS) || this.currToken.type.equals(Constants.TT_MINUS)){
            Token<?> opToken = currToken;
            advance();
            Node right = term();
            left = new BinOpNode<>(left, opToken, right);
        }

        return left;
    }

    public Node parse(){
        return expression();
    }

}

public class PyJ {
    public static Node run(String text) throws Exception{
        // Generate tokens
        Lexer lexer = new Lexer(text);
        LinkedList<Token<?>> tokens = lexer.makeTokens();

        // Generate Abstract Syntax Tree
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
}
