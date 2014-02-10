package ast.exp;

public class True extends T
{
  public True()
  {
	  
  }
  
  public True(int lineNum)
  {
	  this.lineNum = lineNum;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
