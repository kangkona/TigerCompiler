package ast.exp;

public class This extends T
{
  public This()
  {
  }
  public This(int lineNum)
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
