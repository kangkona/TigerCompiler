package codegen.C.stm;

import codegen.C.Visitor;

public class Block extends T
{
  public java.util.LinkedList<codegen.C.dec.T> locals;
  public java.util.LinkedList<T> stms;

  public Block(java.util.LinkedList<T> stms)
  {
    this.stms = stms;
  }
  
  public Block(java.util.LinkedList<codegen.C.dec.T> locals,java.util.LinkedList<T> stms)
  {
	this.locals = locals;
    this.stms = stms;
  }
  
  

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
