import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class Gitlet implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String STAGINGPATH = ".gitlet/stage";
	private final String COMMITPATH = ".gitlet/commit";
	private Node head;
	private int id;
	private ArrayList<File> files;
	private ArrayList<File> trackedFiles;
	private ArrayList<File> untrackedFiles;
	private HashMap<String, Integer> idMatcher;
	private HashMap<Integer, Node> globalLog;
	private HashMap<Integer, String> idMessagePair;
	private Branch currentBranch;
	private ArrayList<String> branchNames;
	private ArrayList<Branch> branches;
	private HashMap<String, Branch> nameMatchingBranch;
	private HashMap<File, String> trackedFilesMap;
	private HashMap<File, String> untrackedFilesMap;

	/**
	 * Gitlet's constructor
	 */
	public Gitlet() {
		head = null;
		id = 0;
		files = new ArrayList<File>();
		idMatcher = new HashMap<String, Integer>();
		globalLog = new HashMap<Integer, Node>();
		idMessagePair = new HashMap<Integer, String>();
		trackedFiles = new ArrayList<File>();
		untrackedFiles = new ArrayList<File>();
		currentBranch = new Branch("master", null);
		branchNames = new ArrayList<String>();
		branches = new ArrayList<Branch>();
		untrackedFilesMap = new HashMap<File, String>();
		nameMatchingBranch = new HashMap<String, Branch>();
		trackedFilesMap = new HashMap<File, String>();
		nameMatchingBranch.put(currentBranch.getName(), currentBranch);

		branchNames.add(currentBranch.getName());
		branches.add(currentBranch);

	}

	/**
	 * initialize the Gitlet version control system
	 */
	public void init() {
		File gitlet = new File(".gitlet");

		if (gitlet.exists() && gitlet.isDirectory()) {
			System.err.println("A gitlet version control system "
					+ "already exists in the current directory.");
		} else {
			gitlet.mkdir();
			File stagingArea = new File(STAGINGPATH);
			stagingArea.mkdir();
			File commitFolder = new File(COMMITPATH);
			commitFolder.mkdir();
			commit("initial commit");
		}
	}

	/**
	 * add a file or a file in some directly
	 * 
	 * @param name file or file location
	 */
	public void add(String name) {

		File dir = new File(name);
		if (!dir.exists()) {
			System.err.println("File does not exist.");
			return;
		}
		String path = dir.getParent();

		if (path != null) {
			File src = new File(name);
			File dst = new File(STAGINGPATH + "/" + name);

			if (dst.getParentFile() != null) {
				dst.getParentFile().mkdirs();
			}

			try {
				Files.copy(src.toPath(), dst.toPath());
				files.add(dst);
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			File src = new File(name);
			File dst = new File(STAGINGPATH + "/" + name);

			try {
				Files.copy(src.toPath(), dst.toPath());
				files.add(dst);
			} catch (IOException e) {

			}
		}
	}

	/**
	 * Committing a message.
	 * 
	 * @param message Pass in an message for committing
	 */
	public void commit(String message) {
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		Node curNode = null;
		File dst = null;
		String commitFileName = COMMITPATH + "/" + Integer.toString(id);

		if (head == null) {
			curNode = new Node(id, time, message, null, null, null);
		} else {
			File newCommit = new File(commitFileName);
			newCommit.mkdir();

			for (File f : files) {
				dst = new File(commitFileName + "/" + f.getPath().substring(14));
				if (dst.getParent().length() != (COMMITPATH.length() + Integer.toString(id).length() + 1)) {
					dst.getParentFile().mkdirs();
				}

				try {
					Files.copy(f.toPath(), dst.toPath());
					idMatcher.put(f.getPath().substring(14), id);
					trackedFiles.add(dst);
					trackedFilesMap.put(dst, f.getPath().substring(14));
				} catch (IOException e) {

				}
			}
		}

		curNode = new Node(id, time, message, trackedFiles, head, idMatcher);
		globalLog.put(id, curNode);
		this.idMessagePair.put(id, curNode.getMessage());
		files.clear();
		untrackedFiles.clear();
		untrackedFilesMap.clear();
		cleanStageArea();
		head = curNode;
		currentBranch.setPointNode(head);
		id++;
	}

	/**
	 * Clean the files in staging area (Except directories)
	 */
	public void cleanStageArea() {
		File files[] = new File(STAGINGPATH).listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteDir(f);
				} else {
					f.delete();
				}
			}
		}
	}

	/**
	 * Delete the content and folder for the passing directory
	 * 
	 * @param file Directory that wants to be deleted
	 */
	public void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

	/**
	 * It removes a name from the current list
	 * 
	 * @param name: taking in a file name
	 */
	public void remove(String name) {
		String filePath = STAGINGPATH + "/" + name;
		File file = new File(filePath);

		if (files.contains(file)) {
			int index = files.indexOf(file);
			File[] contents = new File(STAGINGPATH).listFiles();
			deleteDir(contents[index]);
			files.remove(file);
		} else {
			if (head.getPrevNode() == null) {
				System.err.println("No reason to remove the file.");
			} else {
				if (idMatcher.get(name) != null) {
					file = new File(COMMITPATH + "/" + idMatcher.get(name) + "/" + name);
				} else {
					System.err.println("No reason to remove the file.");
					return;
				}

				if (trackedFiles.contains(file)) {
					trackedFiles.remove(file);
					untrackedFiles.add(file);
					untrackedFilesMap.put(file, name);
				} else {
					System.err.println("No reason to remove the file.");
				}
			}
		}
	}

	/**
	 * Starting at the current head commit, display information
	 * about each commit backwards along the commit tree until
	 * the initial commit.
	 * 
	 * @param head The head of current commit.
	 */
	public void log(Node head) {
		if (head == null) {
			return;
		}

		Node cur = head;

		while (cur != null) {
			System.out.println("===");
			System.out.println("Commit " + cur.getId());
			System.out.println(cur.getTime());
			System.out.println(cur.getMessage());
			cur = cur.getPrevNode();
			if (cur != null) {
				System.out.println();
			}
		}
	}

	/**
	 * Print out all the previous commit information.
	 * 
	 */
	public void globalLog() {
		int i = id - 1;

		while (i >= 0) {
			System.out.println("===");
			System.out.println("Commit " + i);
			System.out.println(globalLog.get(i).getTime());
			System.out.println(globalLog.get(i).getMessage());
			i--;
			if (i >= 0) {
				System.out.println();
			}
		}
	}

	/**
	 * Find id or ids commit with corresponding message
	 * 
	 * @param message commit message
	 */
	public void find(String message) {
		int i = id - 1;
		if (this.idMessagePair.containsValue(message)) {
			while (i >= 0) {
				if (idMessagePair.get(i).equals(message)) {
					System.out.println(i);
				}
				i--;
			}
		} else {
			System.err.println("Found no commit withsc61bl");
		}
	}

	/**
	 * Displays what branches currently exist, and marks the
	 * current branch with a *. Also displays what files have
	 * been staged or marked for untracking. An example of
	 * the exact format it should follow is as follows.
	 */
	public void status() {
		System.out.println("=== Branches ===");
		System.out.println("*" + currentBranch.getName());
		for (String name : branchNames) {
			if (name != currentBranch.getName()) {
				System.out.println(name);
			}
		}
		System.out.println();
		System.out.println("=== Staged Files ===");
		for (File file : files) {
			System.out.println(file.toString().substring(14));
		}
		System.out.println();
		System.out.println("=== Files Marked for Untracking ===");
		for (File file : untrackedFiles) {
			System.out.println(untrackedFilesMap.get(file));
		}
	}

	/**
	 * Takes the version of the file as it exists in the head commit,
	 * the front of the current branch, and puts it in the working
	 * directory, overwriting the version of the file that's already
	 * there if there is one.
	 * OR
	 * Takes all files in the commit at the head of the given branch,
	 * and puts them in the working directory, overwriting the versions
	 * of the files that are already there if they exist. Also, at the
	 * end of this command, the given branch will now be considered
	 * the current branch.
	 * 
	 * @param name The name of a branch or a file
	 */
	public void checkoutOneArg(String name) {
		if (currentBranch.getName().equals(name)) {
			System.out.println("No need to checkout the current branch.");
			return;
		}

		if (branchNames.contains(name)) {
			this.currentBranch = this.nameMatchingBranch.get(name);

			head = this.currentBranch.getNode();
			for (File from : head.getFiles()) {
				File to = new File(trackedFilesMap.get(from));
				try {
					Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
			return;
		}

		if (head.getIdMatcher().get(name) != null) {
			File from = new File(COMMITPATH + "/" + head.getIdMatcher().get(name) + "/" + name);
			File to = new File(name);

			try {
				Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		} else {
			System.err.println("File does not exist in the most recent commit, or no such branch exists.");
		}
	}

	/**
	 * Takes the version of the file as it exists in the commit with
	 * the given id, and puts it in the working directory, overwriting
	 * the version of the file that's already there if there is one.
	 * 
	 * @param commitId The id corresponding on each commit
	 * @param name     The name of a file
	 */
	public void checkoutTwoArgs(String commitId, String name) {

	}

	/**
	 * Creates a new branch with the given name, and points at the current head
	 * node.
	 * 
	 * @param name The name of current branch
	 */
	public void createBranch(String name) {
		if (branches.contains(name)) {
			System.err.println("A branch with that name already exists.");
		} else {
			Branch newBr = new Branch(name, head);
			branches.add(newBr);
			branchNames.add(name);
			nameMatchingBranch.put(name, newBr);
		}
	}

	/**
	 * Deletes the branch with the given name.
	 * 
	 * @param name The name of the branch that want to be removed.
	 */
	public void rmBranch(String name) {
		if (branchNames.contains(name)) {
			if (currentBranch.getName().equals(name)) {
				System.err.println("Cannot remove the current branch.");
			} else {
				Branch branch = nameMatchingBranch.get(name);
				branch.setPointNode(null);
				branchNames.remove(name);
			}
		} else {
			System.err.println("A branch with that name does not exist.");
		}
	}

	/**
	 * Check out all the files tracked by the given commit.
	 * Also moves the current branch's head to that commit
	 * node.
	 * 
	 * @param id The commit id.
	 */
	public void reset(int id) {
		if (!globalLog.containsKey(id)) {
			System.err.println("No commit with that id exists.");
		} else {
			Node curNode = globalLog.get(id);
			for (File from : curNode.getFiles()) {
				File to = new File(trackedFilesMap.get(from));
				try {
					Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
			currentBranch.setPointNode(curNode);
		}
	}

	/**
	 * merge the given branch name with current branch.
	 * 
	 * @param name
	 */
	// public void merge(Branch name) {
	//
	// }

	/**
	 * 
	 * @param name
	 */
	// public void rebase(Branch name) {
	//
	// }

	// private static class ListFile {
	// private Object itemName;
	// private ListFile next;
	//
	// private ListFile(Object itemName, ListFile next) {
	// this.itemName = itemName;
	// this.next = next;
	// }
	// }

	public static void main(String[] args) {
		String gitFile = "gitFile.ser";
		File gitSer = new File(gitFile);
		Gitlet git = null;

		if (gitSer.exists()) {
			try {
				FileInputStream fileIn = new FileInputStream(gitSer);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				git = (Gitlet) objectIn.readObject();
				objectIn.close();
				fileIn.close();
			} catch (IOException e0) {

			} catch (ClassNotFoundException e1) {

			}
		} else {
			git = new Gitlet();
		}

		if (args.length == 0) {
			System.out.println("Please enter a command.");
		} else if (args[0].equals("init") && args.length == 1) {
			git.init();
		} else if (args[0].equals("add") && args.length == 2) {
			git.add(args[1]);
		} else if (args[0].equals("commit")) {
			if (args.length != 2) {
				System.err.println("Please enter a commit message.");
				System.exit(1);
			}
			if (args[1] == null || args[1].length() == 0) {
				System.err.println("Please enter a commit message.");
				System.exit(1);
			}
			git.commit(args[1]);
		} else if (args[0].equals("log") && args.length == 1) {
			git.log(git.head);
		} else if (args[0].equals("global-log") && args.length == 1) {
			git.globalLog();
		} else if (args[0].equals("find") && args.length == 2) {
			git.find(args[1]);
		} else if (args[0].equals("rm") && (args.length == 2)) {
			git.remove(args[1]);
		} else if (args[0].equals("status") && (args.length == 1)) {
			git.status();
		} else if (args[0].equals("checkout") && (args.length == 2 || args.length == 3)) {
			if (args.length == 2) {
				git.checkoutOneArg(args[1]);
			} else {
				git.checkoutTwoArgs(args[1], args[2]);
			}
		} else if (args[0].equals("branch") && (args.length == 2)) {
			git.createBranch(args[1]);
		} else if (args[0].equals("rm-branch") && (args.length == 2)) {
			git.rmBranch(args[1]);
		} else if (args[0].equals("reset") && (args.length == 2)) {
			git.reset(Integer.parseInt(args[1]));
		} else {
			System.out.println("No command with that name exists.");
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(gitSer);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(git);
			objectOut.close();
			fileOut.close();
		} catch (IOException e) {

		}
	}
}