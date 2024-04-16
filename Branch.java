import java.io.Serializable;


public class Branch implements Serializable{
	
	private String name;
	private Node head;
	
	/**
	 * Constructor for Branch
	 * 
	 * @param name   The name for current Branch
	 * @param node   The current node that Branch points to.
	 */
	public Branch(String name, Node node) {
		this.name = name;
		this.head = node;
	}
	
	/**
	 * Getter method for getting the name of the current branch.
	 * 
	 * @return The name of the current branch
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Setter method for setting the current branch point to current commit
	 * 
	 * @param head   The head of current branch
	 */
	public void setPointNode(Node head) {
		this.head = head;
	}
	
	/**
	 * Getter method for getting the pointer of the current branch
	 * 
	 * @return The pointer of current branch.
	 */
	public Node getNode() {
		return head;
	}
}