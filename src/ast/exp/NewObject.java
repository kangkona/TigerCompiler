package ast.exp;

public class NewObject extends T
{
  public String id;

  public NewObject(String id)
  {
    this.id = id;
  }
  
  public NewObject(String id,int lineNum)
  {
    this.id = id;
    this.lineNum = lineNum;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
