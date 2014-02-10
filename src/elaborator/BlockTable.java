package elaborator;


public class BlockTable
{
  private java.util.Hashtable<String, ast.type.T> table;
  private java.util.Hashtable<String,Integer> usedTable;

  public BlockTable()
  {
    this.table = new java.util.Hashtable<String, ast.type.T>();
    this.usedTable = new java.util.Hashtable<String, Integer>();
  }

  // Duplication is not allowed
  public void put(java.util.LinkedList<ast.dec.T> locals)
  { 
     if ( null != locals){
		 for (ast.dec.T dec : locals) {
			 ast.dec.Dec decc = (ast.dec.Dec) dec;
			 if (this.table.get(decc.id) != null) {
				 System.out.println("Error:at line " +decc.lineNum+" duplicated variable: " + decc.id);
				 ElaboratorVisitor.errorCount++;
			 }
			 else{
				 this.table.put(decc.id, decc.type);
				 this.usedTable.put(decc.id, 0);
			 }
		 }
     }
  }

  // return null for non-existing keys
  public ast.type.T get(String id)
  {
	 if (this.usedTable.containsKey(id))
		 this.usedTable.put(id, this.usedTable.get(id)+1);
    return this.table.get(id);
  }
  
  public void dump()
  {
	   System.out.println(this.toString());
  }
  public void empty()
  {
	  this.table.clear();
  }
  
  
  public void unUsed()
  {
	  java.util.Set<String> variables = this.table.keySet();
	  for ( String variable : variables){
		  if (this.usedTable.get(variable)==0)
			  System.out.println("Warning: at line "+this.table.get(variable)
			  .lineNum+":variable "+variable + " declared but never used");
	  }
  }


  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
