package elaborator;


public class ClassBinding
{
  public String extendss; // null for non-existing extends
  public java.util.Hashtable<String, ast.type.T> fields;
  public java.util.Hashtable<String, MethodType> methods;
  private java.util.Hashtable<String,Integer> usedTable;//record the used times of fields

  public ClassBinding(String extendss)
  {
    this.extendss = extendss;
    this.fields = new java.util.Hashtable<String, ast.type.T>();
    this.methods = new java.util.Hashtable<String, MethodType>();
    this.usedTable = new java.util.Hashtable<String, Integer>();
  }

  public ClassBinding(String extendss,
      java.util.Hashtable<String, ast.type.T> fields,
      java.util.Hashtable<String, MethodType> methods)
  {
    this.extendss = extendss;
    this.fields = fields;
    this.methods = methods;
  }

  public void put(String xid, ast.type.T type)
  {
    if (this.fields.get(xid) != null) {
      System.out.println("Error: at line " + type.lineNum +" duplicated class field: " + xid);
      ElaboratorVisitor.errorCount++;      
    }
    else{
    	this.fields.put(xid, type);
    	this.usedTable.put(xid, 0);
    }
  }

  public void put(String mid, MethodType mt)
  {
    if (this.methods.get(mid) != null) {
      System.out.println("Error: at line " + mt.retType.lineNum + " duplicated class method: " + mid);
      ElaboratorVisitor.errorCount++;  
    }
    else
    	this.methods.put(mid, mt);
  }
  
  public ast.type.T get(String id){
	  if (this.usedTable.containsKey(id))
			 this.usedTable.put(id, this.usedTable.get(id)+1);
	  return this.fields.get(id);
  }
  
  public void unUsed()
  {
	  java.util.Set<String> variables = this.fields.keySet();
	  for ( String variable : variables){
		  if (this.usedTable.get(variable)==0)
			  System.out.println("Warning: at line "+this.fields.get(variable)
			  .lineNum+":variable "+variable + " declared but never used");
	  }
  }


  @Override
  public String toString()
  {
    System.out.print("extends: ");
    if (this.extendss != null)
      System.out.println(this.extendss);
    else
      System.out.println("<>");
    System.out.println("\nfields:\n  ");
    System.out.println(fields.toString());
    System.out.println("\nmethods:\n  ");
    System.out.println(methods.toString());

    return "";
  }

}
