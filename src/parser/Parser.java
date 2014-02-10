package parser;

//import java.util.ArrayList;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser
{
  Lexer lexer;
  Token current;
  public Parser(String fname, java.io.InputStream fstream)
  {
    lexer = new Lexer(fname, fstream);
    current = lexer.nextToken();
  }

  // /////////////////////////////////////////////
  // utility methods to connect the lexer
  // and the parser.

  private void advance()
  {
    current = lexer.nextToken();
  }

  private void eatToken(Kind kind)
  {
    if (kind == current.kind)
      advance();
    else {
      System.out.print(lexer.fname+":"+Lexer.lineNum+":"+lexer.columnNum+":");
      System.out.print(" Expects: " + kind.toString());
      System.out.println(" But got: " + current.kind.toString());
      System.out.println(lexer.currentLine);
      int width;
      if (lexer.columnNum == 0) {
        width = lexer.columnNum+1;
      }
      else
        width = lexer.columnNum;
      
      String format = "%" + String.valueOf(width) +"s";
      System.out.printf(format, "^");
      System.out.println();
      System.exit(1);
    }
  }

  private void error()
  {
    //System.out.println(Lexer.lineNum+" Syntax error: compilation aborting...\n");
	System.out.print(lexer.fname+":"+Lexer.lineNum+":"+lexer.columnNum+":");
	System.out.println(" Syntax error:");
	System.out.println(lexer.currentLine);
    int width = lexer.columnNum + 1;
    String format = "%" + String.valueOf(width) +"s";
    System.out.printf(format, "^");
    System.out.println();
	System.exit(1);
    return;
  }

  // ////////////////////////////////////////////////////////////
  // below are method for parsing.

  // A bunch of parsing methods to parse expressions. The messy
  // parts are to deal with precedence and associativity.

  // ExpList -> Exp ExpRest*
  // ->
  // ExpRest -> , Exp
  private java.util.LinkedList<ast.exp.T> parseExpList()
  { 
    java.util.LinkedList<ast.exp.T> args =  new java.util.LinkedList<ast.exp.T>();
    if (current.kind == Kind.TOKEN_RPAREN)
      return null;
    args.addLast(parseExp());
    while (current.kind == Kind.TOKEN_COMMER) {
      advance();
      args.addLast(parseExp());
    }
    return args;
  }

  // AtomExp -> (exp)
  // -> INTEGER_LITERAL
  // -> true
  // -> false
  // -> this
  // -> id
  // -> new int [exp]
  // -> new id ()
  private ast.exp.T parseAtomExp()
  {
    switch (current.kind) {
    case TOKEN_LPAREN:
      advance();
      ast.exp.T exp = parseExp();
      eatToken(Kind.TOKEN_RPAREN);
      return new ast.exp.Paren(exp);
    case TOKEN_NUM:
      int num = Integer.parseInt(current.lexeme);
      advance();
      return new ast.exp.Num(num,current.lineNum);
    case TOKEN_TRUE:
      advance();
      return new ast.exp.True(current.lineNum);
    case TOKEN_FALSE:
    	advance();
    	return new ast.exp.False(current.lineNum);
    case TOKEN_THIS:
      advance();
      return new ast.exp.This(current.lineNum);
    case TOKEN_ID:
      //ast.exp.Id id = new ast.exp.Id(current.lexeme,null,false);
      String id = current.lexeme; 
      advance();
      return new ast.exp.Id(id,current.lineNum);
    case TOKEN_NEW: {
      advance();
      switch (current.kind) {
      case TOKEN_INT:
        advance();
        eatToken(Kind.TOKEN_LBRACK);
        exp = parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        return new ast.exp.NewIntArray(exp);
      case TOKEN_ID:
        id = current.lexeme;
        advance();
        eatToken(Kind.TOKEN_LPAREN); // safe
        eatToken(Kind.TOKEN_RPAREN);
        return new ast.exp.NewObject(id,current.lineNum);
      default:
        error();
        return null;
      }
    }
    default:
      error();
      return null;
    }
  }

  // NotExp -> AtomExp
  // -> AtomExp .id (expList)
  // -> AtomExp [exp]
  // -> AtomExp .length
  private ast.exp.T parseNotExp()
  { ast.exp.T exp = parseAtomExp();
    while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {
      if (current.kind == Kind.TOKEN_DOT) {
        advance();
        if (current.kind == Kind.TOKEN_LENGTH) {
          advance();
          exp = new ast.exp.Length(exp);
        }
        else if (current.kind == Kind.TOKEN_ID){
          //eatToken(Kind.TOKEN_ID);
          String id = current.lexeme;
          advance();
          eatToken(Kind.TOKEN_LPAREN);  
          java.util.LinkedList<ast.exp.T> args = parseExpList();
          eatToken(Kind.TOKEN_RPAREN);
          exp = new ast.exp.Call(exp,id,args);
       }
    } 
    else {
        advance();
        ast.exp.T index = parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        exp = new ast.exp.ArraySelect(exp,index);
     }
  }
    return exp;
  }

  // TimesExp -> ! TimesExp
  // -> NotExp
  private ast.exp.T parseTimesExp()
  { 
    //ast.exp.T exp = new ast.exp.T();
    if (current.kind == Kind.TOKEN_NOT) {
      advance();
      return new ast.exp.Not(parseTimesExp());
    }
    return parseNotExp();
  }

  // AddSubExp -> TimesExp * TimesExp
  // -> TimesExp
  private ast.exp.T parseAddSubExp()
  {
    ast.exp.T left = parseTimesExp();
    while (current.kind == Kind.TOKEN_TIMES) {
      advance();
      ast.exp.T right = parseTimesExp();
      left = new ast.exp.Times(left,right);
    }
    return left;
  }

  // LtExp -> AddSubExp + AddSubExp
  // -> AddSubExp - AddSubExp
  // -> AddSubExp
  private ast.exp.T parseLtExp()
  {
    ast.exp.T left = parseAddSubExp();
    while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
      if (current.kind == Kind.TOKEN_ADD){
        advance();
        ast.exp.T right = parseAddSubExp();
        left = new ast.exp.Add(left,right);
      }
      else{
        advance();
        ast.exp.T right = parseAddSubExp();
        left = new ast.exp.Sub(left,right);
      }
   }
    return left;
  }

  // AndExp -> LtExp < LtExp
  // -> LtExp
  private ast.exp.T parseAndExp()
  {
    ast.exp.T left = parseLtExp();
    while (current.kind == Kind.TOKEN_LT) {
      advance();
      ast.exp.T right = parseLtExp();
      left = new ast.exp.Lt(left,right);
    }
    return left;
  }

  // Exp -> AndExp && AndExp
  // -> AndExp
  private ast.exp.T parseExp()
  {
    ast.exp.T left = parseAndExp();
    while (current.kind == Kind.TOKEN_AND) {
      advance();
      ast.exp.T right = parseAndExp();
      left = new ast.exp.And(left,right);
    }
    return left;
  }

  // Statement -> { Statement* }
  // -> if ( Exp ) Statement else Statement
  // -> while ( Exp ) Statement
  // -> System.out.println ( Exp ) ;
  // -> id = Exp ;
  // -> id [ Exp ]= Exp ;
   // VarDecl -> Type id ;  first_set = {int,boolean,id}
  // VarDecls -> VarDecl VarDecls
  private ast.DecOrStm parseStatementOrVarDecl()
  {
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a statement.
  switch (current.kind) {
  case TOKEN_LBRACE:
    	advance();
    	java.util.LinkedList<ast.DecOrStm> doss 
                              = parseStatementOrVarDecls();
    	eatToken(Kind.TOKEN_RBRACE);
    	return new ast.stm.Block(doss);
	case TOKEN_IF:
		advance();
		eatToken(Kind.TOKEN_LPAREN);
		ast.exp.T condition = parseExp();
		eatToken(Kind.TOKEN_RPAREN);
		ast.DecOrStm thenn = parseStatementOrVarDecl();
		eatToken(Kind.TOKEN_ELSE);
		ast.DecOrStm elsen = parseStatementOrVarDecl();
		return new ast.stm.If(condition,thenn,elsen);
	case TOKEN_WHILE:
		advance();
		eatToken(Kind.TOKEN_LPAREN);
		condition = parseExp();
		eatToken(Kind.TOKEN_RPAREN);
		ast.DecOrStm body = parseStatementOrVarDecl();
		return new ast.stm.While(condition,body);
	case TOKEN_SYSTEM:
		advance();
		eatToken(Kind.TOKEN_DOT);
		eatToken(Kind.TOKEN_OUT);
		eatToken(Kind.TOKEN_DOT);
		eatToken(Kind.TOKEN_PRINTLN);
		eatToken(Kind.TOKEN_LPAREN);
		ast.exp.T exp = parseExp();
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_SEMI);
		return new ast.stm.Print(exp);
  case TOKEN_INT:      // int and boolean operated in same way 
  case TOKEN_BOOLEAN:
    ast.type.T type = parseType();
    String id = current.lexeme;
    eatToken(Kind.TOKEN_ID);
    eatToken(Kind.TOKEN_SEMI);
    return new ast.dec.Dec(type,id);
	case TOKEN_ID:{
    id = current.lexeme;
		advance();
	  switch(current.kind){
      case TOKEN_ID:
        type = new ast.type.Class(id);
        id = current.lexeme;
        advance();
        eatToken(Kind.TOKEN_SEMI);
        return new ast.dec.Dec(type,id);
	    case TOKEN_ASSIGN:
	    	advance();
	      exp = parseExp();  
	    	eatToken(Kind.TOKEN_SEMI);
	    	return new ast.stm.Assign(id, exp);
	    case TOKEN_LBRACK:
	    	advance();
	    	ast.exp.T index = parseExp();
	    	eatToken(Kind.TOKEN_RBRACK);
	    	eatToken(Kind.TOKEN_ASSIGN);
	    	exp   = parseExp();
	    	eatToken(Kind.TOKEN_SEMI);
	    	return new ast.stm.AssignArray(id,index,exp);
	    default:
	    	error();
	    	return null;
	    }
	}
	default:
		error();
		return null;
	}
    //new util.Todo();
  }

  // Statements -> Statement Statements
  // ->
  private java.util.LinkedList<ast.DecOrStm> parseStatementOrVarDecls()
  {
    java.util.LinkedList<ast.DecOrStm> doss = 
                       new java.util.LinkedList<ast.DecOrStm>();
    while (current.kind == Kind.TOKEN_LBRACE || current.kind == Kind.TOKEN_IF
        || current.kind == Kind.TOKEN_WHILE  || current.kind == Kind.TOKEN_INT
        || current.kind == Kind.TOKEN_SYSTEM || current.kind == Kind.TOKEN_ID
        || current.kind == Kind.TOKEN_BOOLEAN ) {
      doss.addLast(parseStatementOrVarDecl());
    }
    return doss;
  }

  // Type -> int []
  // -> boolean
  // -> int
  // -> id
  private ast.type.T parseType()
  {
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a type.
    switch(current.kind){
    case TOKEN_INT:{
    	advance();
    	if(current.kind == Kind.TOKEN_LBRACK){
    		advance();
    		eatToken(Kind.TOKEN_RBRACK);
        return new ast.type.IntArray(current.lineNum);
    		}
    	return new ast.type.Int(current.lineNum);
    }
    case TOKEN_BOOLEAN:
    	advance();
    	return new ast.type.Boolean(current.lineNum);
    case TOKEN_ID:
      String id = current.lexeme;
    	advance();
    	return new ast.type.Class(id,current.lineNum);
    default:
    	error();
    	return null;
    }
  }
  
  // Because in a class ,there are only VarDecls and Methods,no Statements,so 
  //we parse vardecl alone here.
  // VarDecl -> Type id ;
  private ast.dec.T parseVarDecl()
  {
    // to parse the "Type" nonterminal in this method, instead of writing
    // a fresh one.
    ast.type.T type = parseType();
    //System.out.println(current.lexeme);
    String id = new String();
    if ( Kind.TOKEN_ID == current.kind ){
      id = current.lexeme;
    }
    eatToken(Kind.TOKEN_ID);
    eatToken(Kind.TOKEN_SEMI);
    return new ast.dec.Dec(type,id);
  }
  // VarDecls -> VarDecl VarDecls
  // ->
  private java.util.LinkedList<ast.dec.T> parseVarDecls()
  {
    java.util.LinkedList<ast.dec.T> decls = new java.util.LinkedList<ast.dec.T>();
    while (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) {
         
    	decls.addLast(parseVarDecl());
    }
    return decls;
  }

  // FormalList -> Type id FormalRest*
  // ->
  // FormalRest -> , Type id
  private java.util.LinkedList<ast.dec.T> parseFormalList()
  {
    java.util.LinkedList<ast.dec.T> formals = new java.util.LinkedList<ast.dec.T>();
    if (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) {
      ast.type.T type = parseType();
      String id = current.lexeme;
      eatToken(Kind.TOKEN_ID);
      formals.addLast(new ast.dec.Dec(type,id));
      while (current.kind == Kind.TOKEN_COMMER) {
        advance();
        type = parseType();
        id   = current.lexeme;
        eatToken(Kind.TOKEN_ID);
        formals.addLast(new ast.dec.Dec(type,id));
      }
      return formals;
    }
    return null;
  }

  // Method -> public Type id ( FormalList )
  // { VarDecl* Statement* return Exp ;}
  private ast.method.T parseMethod()
  {
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a method.
    eatToken(Kind.TOKEN_PUBLIC);
    ast.type.T retType = parseType();
    String id = current.lexeme;
    eatToken(Kind.TOKEN_ID);
    eatToken(Kind.TOKEN_LPAREN);
    java.util.LinkedList<ast.dec.T> formals = 
    		                       parseFormalList();
    eatToken(Kind.TOKEN_RPAREN);
    eatToken(Kind.TOKEN_LBRACE);
    java.util.LinkedList<ast.DecOrStm> doss =
    		              parseStatementOrVarDecls();
    eatToken(Kind.TOKEN_RETURN);
    ast.exp.T retExp = parseExp();
    eatToken(Kind.TOKEN_SEMI);
    eatToken(Kind.TOKEN_RBRACE);
    java.util.LinkedList<ast.dec.T> locals =
    		   new java.util.LinkedList<ast.dec.T>();
    java.util.LinkedList<ast.stm.T>  stms  =
    		   new java.util.LinkedList<ast.stm.T>();
    for ( ast.DecOrStm dos : doss ){
    	if ( dos instanceof ast.dec.T ){
    		ast.dec.Dec dec = (ast.dec.Dec)dos;
    		locals.addLast(dec);
    	}
    	else if ( dos instanceof ast.stm.T){
    		ast.stm.T stm = (ast.stm.T)dos;
    		stms.addLast(stm);
    	}
    }
    return new ast.method.Method(retType,id,formals,locals,stms,retExp);
  }

  // MethodDecls -> MethodDecl MethodDecls
  // ->
  private java.util.LinkedList<ast.method.T> parseMethodDecls()
  {
    java.util.LinkedList<ast.method.T> methods = 
                new java.util.LinkedList<ast.method.T>();
    while (current.kind == Kind.TOKEN_PUBLIC) {
      methods.addLast(parseMethod());
    }
    return methods;
  }

  // ClassDecl -> class id { VarDecl* MethodDecl* }
  // -> class id extends id { VarDecl* MethodDecl* }
  private ast.classs.T parseClassDecl()
  {
    eatToken(Kind.TOKEN_CLASS);
    String id = current.lexeme;
    eatToken(Kind.TOKEN_ID);
    String extendss ; 
    if (current.kind == Kind.TOKEN_EXTENDS) {
      eatToken(Kind.TOKEN_EXTENDS);
      extendss = current.lexeme;
      eatToken(Kind.TOKEN_ID);
    }
    else
    	extendss = null;
    eatToken(Kind.TOKEN_LBRACE);
    java.util.LinkedList<ast.dec.T> decls = parseVarDecls();
    java.util.LinkedList<ast.method.T> methods = parseMethodDecls();
    eatToken(Kind.TOKEN_RBRACE);
    return new ast.classs.Class(id,extendss,decls,methods);
  }

  // ClassDecls -> ClassDecl ClassDecls
  // ->
  private java.util.LinkedList<ast.classs.T> parseClassDecls()
  {
    java.util.LinkedList<ast.classs.T> classes =
                    new java.util.LinkedList<ast.classs.T>();
    while (current.kind == Kind.TOKEN_CLASS) {
      classes.addLast(parseClassDecl());
    }
    return classes;
  }

  // MainClass -> class id
  // {
  // public static void main ( String [] id )
  // {
  // Statement
  // }
  // }
  private ast.mainClass.T parseMainClass()
  {
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a main class as described by the
    // grammar above.
    //new util.Todo();
	  eatToken(Kind.TOKEN_CLASS);
    String id = current.lexeme;
	  eatToken(Kind.TOKEN_ID);
	  eatToken(Kind.TOKEN_LBRACE);
	  eatToken(Kind.TOKEN_PUBLIC);
	  eatToken(Kind.TOKEN_STATIC);
	  eatToken(Kind.TOKEN_VOID);
	  eatToken(Kind.TOKEN_MAIN);
	  eatToken(Kind.TOKEN_LPAREN);
	  eatToken(Kind.TOKEN_STRING);
	  eatToken(Kind.TOKEN_LBRACK);
	  eatToken(Kind.TOKEN_RBRACK);
    String arg = current.lexeme;
	  eatToken(Kind.TOKEN_ID);
	  eatToken(Kind.TOKEN_RPAREN);
	  eatToken(Kind.TOKEN_LBRACE);
	  java.util.LinkedList<ast.DecOrStm> doss = parseStatementOrVarDecls();
	  ast.stm.Block block = new ast.stm.Block(doss);
	  eatToken(Kind.TOKEN_RBRACE);
	  eatToken(Kind.TOKEN_RBRACE);
	  return new ast.mainClass.MainClass(id,arg,block);
  }

  // Program -> MainClass ClassDecl*
  private ast.program.T parseProgram()
  {
    ast.mainClass.T mainClass = parseMainClass();
    java.util.LinkedList<ast.classs.T> classes = parseClassDecls();
    eatToken(Kind.TOKEN_EOF);
    return new ast.program.Program(mainClass,classes);
  }

  public ast.program.T parse()
  {
    ast.program.T program = parseProgram();
    return program;
  }
}
