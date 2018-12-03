package general;

import java.util.Objects;
/** Pair is a mutable data structure used to link two things together.
*/
public class Pair<T1, T2> 
{
	// Abstraction Function:
	// A pair p represents a way to store two objects together
    
    // Representation invariant for every Pair p:
	//p.left and p.right is not null
	private T1 left;
	private T2 right;
	static final boolean checkRepOn = false;
	
	/** @param left The left object.
	 	@param right The right object.
	 	@modifies left and right
    	@effects Constructs a new Pair with left objects and right objects
	*/
	public Pair(T1 left, T2 right)
	{
		this.left = left;
		this.right = right;
	}
	
	/**
     * Checks that the representation invariant holds.
     **/
    // Throws a RuntimeException if the rep invariant is violated.
    private void checkRep() throws RuntimeException 
    {
    	if (!checkRepOn) 
    	{
    		return;
    	}
    	
    	if (this.left == null)
    	{
    		throw new RuntimeException("Pair's left is null.");
    	}
    	
    	if (this.right == null)
    	{
    		throw new RuntimeException("Pair's right is null.");
    	}
    }
	
	/** Returns the object left is holding. It is possible for the user to edit left using this.
	   	@return The Object left is holding
	*/ 
	public T1 getLeft() 
	{
		return left;
	}
	
	/** Returns the object right is holding. It is possible for the user to edit right using this.
	 	@return The Object right is holding
	*/ 
	public T2 getRight()
	{
		return right;
	}
	
	/** @param new_left What left will be
	 	@modifies left
	 	@effects left = new_left
	*/ 
	public void setLeft(T1 new_left)
	{
		checkRep();
		left = new_left;
		checkRep();
	}
	
	/** @param new_right What right will be
 		@modifies right
 		@effects right = new_right
	*/ 
	public void setRight(T2 new_right)
	{
		checkRep();
		right = new_right;
		checkRep();
	}
	
	/** Standard equality operation.
		@param obj The object to be compared for equality.
		@return true if and only if 'obj' is an instance of a Pair
		and 'this' and 'obj' represent the same pair. 
	*/
	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof Pair<?, ?>) {
	    	Pair<?, ?> other_pair = (Pair<?, ?>) obj;
	
	        if(left.equals(other_pair.getLeft()) && right.equals(other_pair.getRight()))
	        {
	        	return true;
	        }
	        else
	        {
	        	return false;
	        }
	    } else {
	        return false;
	    }
	}
	
	/** Standard hashCode function.
	    @return an int that all objects equal to this will also
	    return.
	*/
	@Override
	public int hashCode() 
	{
	    return Objects.hash(left,right);
	}
}
