import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Node implements Serializable {
	
	private int id;
	private String time;
	private String message;
	private ArrayList<File> files;
	private Node prev;
	private HashMap<String, Integer> idMatcher;
	
	/**
	 * constructor for current node
	 * 
	 * @param id           commit ID
	 * @param time         The time when the node commits
	 * @param message	   The commit message
	 * @param files        Current commit file containing
	 * @param previous     points to previous commit
	 * @param matcher      file match corresponding folders in commit database
	 */
	public Node(int id, String time, String message, ArrayList<File> file, Node previous, HashMap<String, Integer> matcher) {
		this.id = id;
		this.time = time;
		this.message = message;
		this.files = file;
		this.prev = previous;	
		this.idMatcher = matcher;
	}
	
	/**
	 * Getter method for getting the commit containing files
	 * 
	 * @return containing tracked files
	 */
	public ArrayList<File> getFiles() {
		return files;
	}
	
	/**
	 * Getter method for getting the current commit message
	 * 
	 * @return  current commit message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Getter method for getting the current commit id
	 * 
	 * @return current commit id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Getter method for getting the current commit time
	 * 
	 * @return current commit time
	 */
	public String getTime() {
		return time;
	}
	
	/**
	 * Getter method for getting the previous commit node
	 * 
	 * @return previous commit node
	 */
	public Node getPrevNode() {
		return prev;
	}
	
	/**
	 * Getter method for getting the idMatcher map
	 * 
	 * @return  A map contains file and commit id pair
	 */
	public HashMap<String, Integer> getIdMatcher() {
		return this.idMatcher;
	}
}