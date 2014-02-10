package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Putfield extends T
{
  public String field_spec;
  public String descriptor;
  public Putfield()
  {
  }
	  
  public Putfield(String field_spec,String descriptor)
   {
	 this.field_spec = field_spec;
	 this.descriptor = descriptor;
   }
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
