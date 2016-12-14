import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexAnalysis {
	private String[] keyWords = {
		"begin", "end", "if", "then", "else", "const", "var", "do", "while", "call", "read", "write", "odd"
	};

	private List<Token> allToken;
	private char ch = ' ';
	private int searchPtr = 0;
	private char[] buffer;
	private int line = 1;
	private String strToken;

	public LexAnalysis(File file) {
		init(); 
		BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(file));
            String temp1 = "", temp2 = "";
            while((temp1 = bf.readLine()) != null) {
                temp2 = temp2 + temp1 + String.valueOf('\n');
            }
            buffer = temp2.toCharArray();
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doAnalysis();
	}

	public List<Token> getAllToken() {
        return allToken;
    }
    
	private void doAnalysis() {
		while (searchPtr < buffer.length) {
            allToken.add(analysis());
        }
	}

	private Token analysis() {
		strToken = "";
        while (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\0') {
            if (ch == '\n') {
                line++;
            }
            getChar();
        }
        if (ch == '\n') { //到达文件末尾
            return new Token(SymType.EOF, line, "-1");
        }
        if (isLetter()) { //首位为字母，可能为保留字或者变量名
        	while (isLetter() || isDigit()) {
                strToken += ch;
                getChar();
            }
            retract();
            for (int i = 0; i < keyWords.length; i++) {
                if (strToken.equals(keyWords[i])) {
                    return new Token(SymType.values()[i], line, "-");
                }
            }
            return new Token(SymType.SYM, line, strToken);
        } else if (isDigit()) { //首位为数字，即为整数
        	while (isDigit()) {
                strToken += ch;
                getChar();
            }
            retract();
            return new Token(SymType.CONST, line, strToken);
        } else if (ch == '=') { //等号
            return new Token(SymType.EQU, line, "-");
        } else if (ch == '+') { //加号
            return new Token(SymType.ADD, line, "-");
        } else if (ch == '-') { //减号
            return new Token(SymType.SUB, line, "-");
        } else if (ch == '*') { //乘号
            return new Token(SymType.MUL, line, "-");
        } else if (ch == '/') { //除号
            return new Token(SymType.DIV, line, "-");
        } else if (ch == '<') { //小于或不等于或小于等于
            getChar();
            if (ch == '=') {
                return new Token(SymType.LESE, line, "-");
            } else if (ch == '>') {
                return new Token(SymType.NEQE, line, "-");
            } else {
                retract();
                return new Token(SymType.LES, line, "-");
            }
        } else if (ch == '>') { //大于或大于等于
            getChar();
            if (ch == '=') {
                return new Token(SymType.LARE, line, "-");
            } else {
                retract();
                return new Token(SymType.LAR, line, "-");
            }
        } else if (ch == ',') { //逗号
            return new Token(SymType.COMMA, line, "-");
        } else if (ch == ';') { //分号
            return new Token(SymType.SEMIC, line, "-");
        } else if (ch == '.') { //点
            return new Token(SymType.POI, line, "-");
        } else if (ch == '(') { //左括号
            return new Token(SymType.LBR, line, "-");
        } else if (ch == ')') { //右括号
            return new Token(SymType.RBR, line, "-");
        } else if (ch == ':') { //赋值号
            getChar();
            if (ch == '=') {
                return new Token(SymType.CEQU, line, "-");
            } else {
            	retract();
            	return new Token(SymType.COL, line, "-");
            }
        }
        return new Token(SymType.EOF, line, "-");
	}

	private void init() {
		allToken = new ArrayList<Token>();
	}

	private char getChar() {
		if (searchPtr < buffer.length) {
			ch = buffer[searchPtr];
			searchPtr++;
		}
		return ch;
	}

    private void retract() {
        searchPtr--;
        ch = ' ';
    }

	private boolean isLetter() {
        if(Character.isLetter(ch)) {
            return true;
        }
        return false;
    }
    
    private boolean isDigit() {
        if(Character.isDigit(ch)) {
            return true;
        }
        return false;
    }
}
