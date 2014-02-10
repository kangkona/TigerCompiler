package lexer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;



//import java.util.*;
//import util.Todo;
import lexer.Token.Kind;

public class Lexer
{
  public String fname; // the input file name to be compiled
  InputStream fstream; // input stream for the above file
  public static Integer lineNum; //line number
  public int columnNum; //current Token'column number
  public BufferedReader fin; // set another instream to get currentLine
  public String currentLine; // all information in current line
  Hashtable words; //store the key words and existed ID 
  int current;  // current reading character
  public Lexer(String fname, InputStream fstream)
  {
    this.fname = fname;
    this.fstream = fstream;
    Lexer.lineNum = 1;
    this.columnNum = 0;
    try{
      this.fin = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
      this.currentLine = this.fin.readLine();
    }catch(Exception e){
      System.out.println(this.fname + " can't be compiled!");
    }
    this.words = new Hashtable();
    this.current = -2 ; // -2 stand for current token has been accepted
    initWords("boolean",Kind.TOKEN_BOOLEAN);
    initWords("class",Kind.TOKEN_CLASS);
    initWords("else",Kind.TOKEN_ELSE);
    initWords("extends",Kind.TOKEN_EXTENDS);
    initWords("false",Kind.TOKEN_FALSE);
    initWords("if",Kind.TOKEN_IF);
    initWords("int",Kind.TOKEN_INT);
    initWords("length",Kind.TOKEN_LENGTH);
    initWords("main",Kind.TOKEN_MAIN);
    initWords("new",Kind.TOKEN_NEW);
    initWords("out",Kind.TOKEN_OUT);
    initWords("println",Kind.TOKEN_PRINTLN);
    initWords("public",Kind.TOKEN_PUBLIC);
    initWords("return",Kind.TOKEN_RETURN);
    initWords("static",Kind.TOKEN_STATIC);
    initWords("String",Kind.TOKEN_STRING);
    initWords("System",Kind.TOKEN_SYSTEM);
    initWords("this",Kind.TOKEN_THIS);
    initWords("true",Kind.TOKEN_TRUE);
    initWords("void",Kind.TOKEN_VOID);
    initWords("while",Kind.TOKEN_WHILE);
  }
  
  
  public void initWords(String s,Kind kind){
    this.words.put(s,new Token(kind,s));
  }
  
  private void error(String s)
  {
    System.out.print(this.fname +":"+lineNum+":"+this.columnNum+":Lexer error: ");
    System.out.println(s);
    System.out.println(this.currentLine);
    int width = this.columnNum+1;  // the init value of column is 0, so +1 
    String format = "%" + String.valueOf(width) +"s";   //left of '^' filled with blank
    System.out.printf(format, "^");
    System.out.println();
    System.exit(1);
  }
  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  private Token nextTokenInternal() throws Exception
  { 
    if ( -2 == current ){
      current = this.fstream.read();
      this.columnNum++;
    }

    if (-1 == current){
      // The value for "lineNum" is now "null",
      // you should modify this to an appropriate
      // line number for the "EOF" token.    
      return new Token(Kind.TOKEN_EOF, lineNum);
    }
    // skip all kinds of "blanks"  and "annotation"
    while (' ' == current || '\t' == current || '\n' == current || '/' == current) {
      if (' ' == current){
          this.columnNum++;
      }
      if ('\t' == current){
        this.columnNum+=8;
      }

      if ('/'== current ){
            current = this.fstream.read();
            this.columnNum++;
            if('/'== current){
              do{
                current = this.fstream.read();
                this.columnNum++;
              }while('\n'!= current );
             }
            else if ('*' == current ) {
              do{
                current = this.fstream.read();
                this.columnNum++;
                if ( '*' == current ) {
                  current = this.fstream.read();
                  this.columnNum++;
                  if ('/' == current) {
                    break;
                  }  
                }

                if ('\n' == current ) {
                  lineNum++;
                  this.currentLine = this.fin.readLine(); //read current line
                  this.columnNum = 0;
                }
              }while(-1 != current);
            }
            else{
              error("There may miss another '/'.");
            }
           }

      if('\n'== current ){
        lineNum++;
        this.currentLine = this.fin.readLine(); //read current line
        this.columnNum = 0;
           }
      current = this.fstream.read();
    }
    
    if (-1 == current )
      return new Token(Kind.TOKEN_EOF, lineNum);

    switch (current) {
    case '+':
      current = -2;
      return new Token(Kind.TOKEN_ADD, lineNum);
    case '&':{
      current =  this.fstream.read();
      this.columnNum++;
      if ('&'== current ) {
      current = -2;
        return new Token(Kind.TOKEN_AND,lineNum);
      }
      else{
        error("There may miss another '&'.");
      }
    }
    case '=':
      current = -2;
        return new Token(Kind.TOKEN_ASSIGN,lineNum);
    case ',':
      current = -2;
      return new Token(Kind.TOKEN_COMMER,lineNum);
    case '.':
      current = -2;
      return new Token(Kind.TOKEN_DOT,lineNum);
    case '{':
      current = -2;
      return new Token(Kind.TOKEN_LBRACE,lineNum);
    case '[':
      current = -2;
      return new Token(Kind.TOKEN_LBRACK,lineNum);
    case '(':
      current = -2;
      return new Token(Kind.TOKEN_LPAREN,lineNum);
    case '<':
      current = -2;
      return new Token(Kind.TOKEN_LT,lineNum);
    case '!':
      current = -2;
      return new Token(Kind.TOKEN_NOT,lineNum);
    case '}':
      current = -2;
      return new Token(Kind.TOKEN_RBRACE,lineNum);
    case ']':
      current = -2;
      return new Token(Kind.TOKEN_RBRACK,lineNum);
    case ')':
      current = -2;
      return new Token(Kind.TOKEN_RPAREN,lineNum);
    case ';':
      current = -2;
      return new Token(Kind.TOKEN_SEMI,lineNum);
    case '-':
      current = -2;
      return new Token(Kind.TOKEN_SUB,lineNum);
    case '*':
      current = -2;
      return new Token(Kind.TOKEN_TIMES,lineNum);
    }
    if(Character.isDigit(current)){
       StringBuffer sb=new StringBuffer();
      if('0'== current ){
        current =  this.fstream.read();
        this.columnNum++;
        if ( !Character.isDigit(current) ){
          return new Token(Kind.TOKEN_NUM,lineNum,"0");
        }
        else{
          error("Num(except 0) can't start with 0.");
        }  
      }
      else{
        while(Character.isDigit( current )){
          sb = sb.append( (char)current );
          current = this.fstream.read();
          this.columnNum++;
        }
        return new Token(Kind.TOKEN_NUM,lineNum,sb.toString());
      }
    }
    if( Character.isLetter(current) || '_'== current ){
      StringBuffer sb = new StringBuffer();
      do{
        sb.append((char) current );
        current = this.fstream.read();
        this.columnNum++;
      }while( Character.isLetterOrDigit( current ) || '_'== current );
      String word = sb.toString();
      Token t = (Token)words.get(word);
      if(null==t){
        words.put(word, new Token(Kind.TOKEN_ID,word));
        return new Token(Kind.TOKEN_ID,lineNum,word);
      }
      else{
        return new Token(t.kind,lineNum,t.lexeme);
      }
    }

    error((char)current + " is illegal used in MiniJava.");
    return null;
  }

  public Token nextToken()
  {
    Token t = null;

    try {
      t = this.nextTokenInternal();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    if (control.Control.lex)
      System.out.println(t.toString());
    return t;
  }
}
