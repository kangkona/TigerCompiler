package codegen.C.mainMethod;

import codegen.C.Visitor;

public class MainMethod extends T
{
 //  public java.util.LinkedList<codegen.C.dec.T> locals;
//  public codegen.C.stm.T stm;
    public codegen.C.stm.Block block;
/*
  public MainMethod(java.util.LinkedList<codegen.C.dec.T> locals,
      codegen.C.stm.T stm)
  {
    this.locals = locals;
    this.stm = stm;
  }
*/
 
    public MainMethod(codegen.C.stm.Block block)
    {
      this.block = block;
    }
    
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }

}
