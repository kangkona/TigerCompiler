package ast.stm;

public class While extends T
{
  public ast.exp.T condition;
  public ast.DecOrStm body;

  public While(ast.exp.T condition, ast.DecOrStm body)
  {
    this.condition = condition;
    this.body = body;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
