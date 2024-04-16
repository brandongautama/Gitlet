# Gitlet

Replica of Git version control system

## Usage

### init

```
java Gitlet init
```

Description: Creates a new gitlet version control system in the current directory. This system will automatically start with one commit: a commit that contains no files and has the commit message initial commit.

### add

```
java Gitlet add [file name]
```

Description: Adds a copy of the file as it currently exists to the staging area. Sometimes, for this reason, adding a file is also just called staging the file. The staging area should be somewhere in .gitlet. If the file had been marked for untracking (more on this in the description of the rm command), then add just unmarks the file, and does not also add it to the staging area.

### commit

```
java Gitlet commit [message]
```

Description: Saves a backup of certain files so they can be restored at a later time. The collection of versions of files in a commit is sometimes called the commit's snapshot of files, and the commit is said to be tracking those versions of files. By default, each commit's snapshot of files will be exactly the same as its parent commit's snapshot of files; it will keep versions of files exactly as they are, and not update them. A commit will only update the version of a file it is tracking if that file had been staged at the time of commit, in which case the commit will now include the version of the file that was staged instead of the version it got from its parent. In addition, a commit will start tracking any files that were staged but weren't tracked by its parent.

### rm

```
java Gitlet rm [file name]
```

Description: Mark the file for untracking; this means it will not be included in the upcoming commit, even if it was tracked by that commit's parent. If the file had been staged, instead just unstage it, and don't also mark it for untracking.

### log

```
java Gitlet log
```

Description: Starting at the current head commit, display information about each commit backwards along the commit tree until the initial commit. This set of commit nodes is called the commit’s history. For every node in this history, the information it should display is the commit id, the time the commit was made, and the commit message.

### global-log

```
java Gitlet global-log
```

Description: Like log, except displays information about all commits ever made. The order of the commits does not matter

### find

```
java Gitlet find [commit message]
```

Description: Prints out the id of the commit that has the given commit message. If there are multiple such commits, it prints the ids out on separate lines.

### status

```
java Gitlet status
```

Description: Displays what branches currently exist, and marks the current branch with a \*. Also displays what files have been staged or marked for untracking.

### checkout

Checkout is a kind of general command that can do a few different things depending on what its arguments are. There are 3 possible use cases. In each section below, you'll see 3 bullet points. Each corresponds to the respective usage of checkout.

```
java Gitlet checkout [file name]
java Gitlet checkout [commit id] [file name]
java Gitlet checkout [branch name]
```

Descriptions:

- Takes the version of the file as it exists in the head commit, the front of the current branch, and puts it in the working directory, overwriting the version of the file that's already there if there is one.
- Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory, overwriting the version of the file that's already there if there is one.
- Takes all files in the commit at the head of the given branch, and puts them in the working directory, ovewriting the versions of the files that are already there if they exist. Also, at the end of this command, the given branch will now be considered the current branch.

### branch

```
java Gitlet branch [branch name]
```

Description: Creates a new branch with the given name, and points it at the current head node. A branch is nothing more than a name for a pointer to a commit node into the commit tree. Before you ever call branch, your code should be running with a default branch called "master". Note: Does NOT immediately switch to the newly created branch.

### rm-branch

```
java Gitlet rm-branch [branch name]
```

Description: Deletes the branch with the given name. This only means to delete the pointer associated with the branch; it does not mean to delete all commits that were created under the branch, or anything like that.

### reset

```
java Gitlet reset [commit id]
```

Description: Checks out all the files tracked by the given commit. Also moves the current branch's head to that commit node. See the intro for an example of what happens to the head pointer after using reset.

### merge

```
java Gitlet merge [branch name]
```

Description: Merges files from the given branch into the current branch.

### rebase

```
java Gitlet rebase [branch name]
```

Description: Conceptually, what rebase does is find the split point of the current branch and the given branch, then snaps off the current branch at this point, then reattaches the current branch to the head of the given branch.
