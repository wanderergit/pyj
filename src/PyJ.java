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
    int pos;
    char currentChar;

    public Lexer(String text) {
        this.text = text;
        this.pos = -1;
        advance();
    }

    public void advance() {
        this.pos += 1;
        currentChar = this.pos < this.text.length() ? this.text.charAt(pos) : '\0';
    }

    public LinkedList<Token> makeTokens() throws IllegalCharException {
        LinkedList<Token> tokens = new LinkedList<>();

        while(this.currentChar != '\0'){
            if(this.currentChar == ' ' || this.currentChar == '\t')
                advance();
            else if(Character.isDigit(this.currentChar)) {
                tokens.add(makeNumber());
                advance();
            }
            else if(this.currentChar == '+'){
                tokens.add(new Token(Constants.TT_PLUS));
                advance();
            } else if(this.currentChar == '-'){
                tokens.add(new Token(Constants.TT_MINUS));
                advance();
            } else if(this.currentChar == '*'){
                tokens.add(new Token(Constants.TT_MUL));
                advance();
            } else if(this.currentChar == '/'){
                tokens.add(new Token(Constants.TT_DIV));
                advance();
            } else if(this.currentChar == '('){
                tokens.add(new Token(Constants.TT_LPAREN));
                advance();
            } else if(this.currentChar == ')'){
                tokens.add(new Token(Constants.TT_RPAREN));
                advance();
            } else {
                // return error
                char ch = this.currentChar;
                advance();
                throw new IllegalCharException("Illegal Character " + "'" + ch + "'");
            }
        }

        return tokens;
    }

    public Token makeNumber(){
        StringBuffer numStr = new StringBuffer("");
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
            return new Token<Integer>(Constants.TT_INT, Integer.parseInt(numStr.toString()));
        } else {
            return new Token<Float>(Constants.TT_FLOAT, Float.parseFloat(numStr.toString()));
        }
    }
}

/*
    NODES
 */

class NumberNode {
    Token token;
    public NumberNode(Token t){
        this.token = t;
    }

    @Override
    public String toString() {
        return "NumberNode{" +
                "token=" + token +
                '}';
    }
}

class BinOpNode {
    NumberNode leftNode;
    NumberNode rightNode;
    Token opToken;

    public BinOpNode(NumberNode leftNode, Token opToken, NumberNode rightNode){
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.opToken = opToken;
    }

    @Override
    public String toString() {
        return "BinOpNode{" +
                "leftNode=" + leftNode +
                ", rightNode=" + rightNode +
                ", opToken=" + opToken +
                '}';
    }
}

/*
    PARSER
 */



public class PyJ {
    public static LinkedList<Token> run(String text) throws Exception{
        Lexer lexer = new Lexer(text);
        LinkedList<Token> tokens = lexer.makeTokens();
        return tokens;
    }
}
