package ast.stm;

public class Block extends T
{
  public java.util.LinkedList<ast.DecOrStm>  doss;
  public java.util.LinkedList<ast.dec.T>  locals;
  public java.util.LinkedList<ast.stm.T>  stms;

  public Block(java.util.LinkedList<ast.DecOrStm> doss)
  {
    this.doss = doss; //not used now
    this.locals = new java.util.LinkedList<ast.dec.T>();
    this.stms = new java.util.LinkedList<ast.stm.T>();
    for (ast.DecOrStm dos : doss){
    	if ( dos instanceof ast.dec.T ){
    		ast.dec.Dec dec = (ast.dec.Dec)dos;
    		locals.addLast(dec);
    	}
    	else{
    		ast.stm.T stm = (ast.stm.T)dos;
    		stms.addLast(stm);
    	}
    }
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
