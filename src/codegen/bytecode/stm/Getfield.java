package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Getfield extends T
{
  public String field_spec;
  public String descriptor;
  public Getfield()
  {
  }
  
  public Getfield(String field_spec,String descriptor)
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
