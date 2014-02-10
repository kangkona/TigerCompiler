package ast.exp;

public class False extends T
{
  public False()
  {
	  
  }
  
  public False(int lineNum)
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
