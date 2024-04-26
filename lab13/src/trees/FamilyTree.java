package trees;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;


public class FamilyTree
{
    
    private static class TreeNode
    {
        private String                    name;
        private TreeNode                parent;
        private ArrayList<TreeNode>        children;
        
        
        TreeNode(String name)
        {
            this.name = name;
            children = new ArrayList<>();
        }
        
        
        String getName()
        {
            return name;
        }
        
        
        void addChild(TreeNode childNode)
        {
            children.add(childNode); //adding the child node
            childNode.parent = this; //set its parent
        }

        
        
        // Searches subtree at this node for a node
        // with the given name. Returns the node, or null if not found.
        TreeNode getNodeWithName(String targetName)
        {
            if (this.name.equals(targetName)) //set it to the name we want
                return this;
                    
            for (TreeNode child: children) //check the name in children
            {
                TreeNode resultNode = child.getNodeWithName(targetName);
                if (resultNode != null) //return the name if found
                    return resultNode;
            }
            
            return null;
        }

        
        
        // Returns a list of ancestors of this TreeNode, starting with this node’s parent and
        // ending with the root. Order is from recent to ancient.
        ArrayList<TreeNode> collectAncestorsToList()
        {
            ArrayList<TreeNode> ancestors = new ArrayList<>(); //creating a new node for ancestors
            TreeNode current = this.parent; //set it to the correct parent
            while (current != null)
            {
                ancestors.add(current); //add new ancestors accordingly to the parent
                current = current.parent;
            }
            return ancestors;
        }

        
        
        public String toString()
        {
            return toStringWithIndent("");
        }
        
        
        private String toStringWithIndent(String indent)
        {
            String s = indent + name + "\n";
            indent += "  ";
            for (TreeNode childNode: children)
                s += childNode.toStringWithIndent(indent);
            return s;
        }
    }

	private TreeNode			root;
	
	
	//
	// Displays a file browser so that user can select the family tree file.
	//
	public FamilyTree() throws IOException, TreeException
	{
		// User chooses input file. This block doesn't need any work.
		FileNameExtensionFilter filter = 
			new FileNameExtensionFilter("Family tree text files", "txt");
		File dirf = new File("data");
		if (!dirf.exists())
			dirf = new File(".");
		JFileChooser chooser = new JFileChooser(dirf);
		chooser.setFileFilter(filter);
		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			System.exit(1);
		File treeFile = chooser.getSelectedFile();

		// Parse the input file. Create a FileReader that reads treeFile. Create a BufferedReader
		// that reads from the FileReader.
		FileReader fr = new FileReader(treeFile);
		BufferedReader br = new BufferedReader(fr);

		String line;
		while ((line = br.readLine()) != null)
			addLine(line);
		br.close();
		fr.close();
	}
	
	
	//
	// Line format is "parent:child1,child2 ..."
	// Throws TreeException if line is illegal.
	//
	private void addLine(String line) throws TreeException
	{
	    int colonIndex = line.indexOf(":");
	    if (colonIndex < 0)
	        throw new TreeException("Line format is incorrect, colon missing: " + line);
	    String parentName = line.substring(0, colonIndex);
	    String childrenString = line.substring(colonIndex + 1);
	    String[] childrenArray = childrenString.split(",");
	    
	    TreeNode parentNode;
	    if (root == null)
	        parentNode = root = new TreeNode(parentName);
	    else
	    {
	        parentNode = root.getNodeWithName(parentName);
	        if (parentNode == null)
	            throw new TreeException("Parent node not found for");
	    }
	    
	    for (String childName : childrenArray)
	    {
	        TreeNode childNode = new TreeNode(childName.trim());
	        parentNode.addChild(childNode);
	    }
	}

	
	
	// Returns the "deepest" node that is an ancestor of the node named name1, and also is an
	// ancestor of the node named name2.
	//
	// "Depth" of a node is the "distance" between that node and the root. The depth of the root is 0. The
	// depth of the root's immediate children is 1, and so on.
	//
	TreeNode getMostRecentCommonAncestor(String name1, String name2) throws TreeException
	{
	    TreeNode node1 = root.getNodeWithName(name1);
	    if (node1 == null)
	        throw new TreeException("Node not found");
	    TreeNode node2 = root.getNodeWithName(name2);
	    if (node2 == null)
	        throw new TreeException("Node not found");
	    
	    ArrayList<TreeNode> ancestorsOf1 = node1.collectAncestorsToList();
	    ArrayList<TreeNode> ancestorsOf2 = node2.collectAncestorsToList();
	    
	    for (TreeNode n1: ancestorsOf1)
	        if (ancestorsOf2.contains(n1))
	            return n1;
	    
	    return null;
	}

	
	
	public String toString()
	{
		return "Family Tree:\n\n" + root;
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			FamilyTree tree = new FamilyTree();
			System.out.println("Tree:\n" + tree + "\n**************\n");
			TreeNode ancestor = tree.getMostRecentCommonAncestor("Bilbo", "Frodo");
			System.out.println("Bilbo and Frodo most recent ancestor is " + ancestor.getName());
		}
		catch (IOException x)
		{
			System.out.println("IO trouble: " + x.getMessage());
		}
		catch (TreeException x)
		{
			System.out.println("Input file trouble: " + x.getMessage());
		}
	}
}
