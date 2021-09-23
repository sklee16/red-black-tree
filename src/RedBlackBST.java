import java.util.*;
import java.io.*;

/**
 * RedBlackBST class
 *
 * @Sue Lee
 * February 23rd, 2019
 */
public class RedBlackBST<Key extends Comparable<Key>, Value> {

	private static final boolean RED   = true;
	private static final boolean BLACK = false;
	Node root;     // root of the BST

	/*************************************************************************
	 *  Node Class and methods - DO NOT MODIFY
	 *************************************************************************/
	public class Node {
		Key key;           // key
		Value val;         // associated data
		Node left, right;  // links to left and right subtrees
		boolean color;     // color of parent link
		int N;             // subtree count

		public Node(Key key, Value val, boolean color, int N) {
			this.key = key;
			this.val = val;
			this.color = color;
			this.N = N;
		}
	}

	// is node x red; false if x is null ?
	private boolean isRed(Node x) {
		if (x == null) return false;
		return (x.color == RED);
	}

	// number of node in subtree rooted at x; 0 if x is null
	private int size(Node x) {
		if (x == null) return 0;
		return x.N;
	}

	// return number of key-value pairs in this symbol table
	public int size() { return size(root); }

	// is this symbol table empty?
	public boolean isEmpty() {
		return root == null;
	}

	public RedBlackBST() {
		this.root = null;
	}

	/*************************************************************************
	 *  Modification Functions
	 *************************************************************************/

	// insert the key-value pair; overwrite the old value with the new value
	// if the key is already present
	public void insert(Key key, Value val) {
		//TODO

		if (root == null) {
			//create a new tree
			root = new RedBlackBST.Node(key, val, BLACK, 0);
			root.key = key;
			root.val = val;
			root.left = null;
			root.right = null;
			root.N = 1;
		} else {
			assert root != null : "Current root is null";

			root = insert(root, key, val);
			if(root.color) {
				root.color = BLACK;
			}
		}
	}

	//recursive method for insert
	private Node insert(Node current, Key key, Value val) {

		if (current == null) {
			return new RedBlackBST.Node(key, val, RED, 1);
		}
		assert current != null : "Current node is not null";

		if (key.compareTo(current.key) == 0) {//key already exists
			current.val = val;

		} else if (key.compareTo(current.key) < 0) {
			//traverse through the tree to find the correct location to insert the node
			current.left = insert(current.left, key, val);

		} else {//inserted key is greater than the current key
			current.right = insert(current.right, key, val);
		}

		current = balance(current);//new node is inserted, check the balance of the tree

		return current;
	}


	// delete the key-value pair with the given key
	public void delete(Key key) {
		//TODO
		if (root == null) {
			root = null;
		} else {
			if (!contains(key)) {//reduce runtime by checking the existence of key
				return;
			} else if ((!isRed(root.left)) && (!isRed(root.right))) {//temporarily color change
				root.color = RED;
			}
			root = delete(root, key);

			if(!isEmpty()) {
				root.color = BLACK;//change the color of the root back to black
			}
		}
	}

	//recursive method for delete
	private Node delete (Node current, Key key) {
		assert current != null : "Current node is null";

		if (key.compareTo(current.key) < 0) {
			if (!isRed(current.left) && !isRed(current.left.left)) {
				current = moveRedLeft(current);
			}
			current.left = delete(current.left, key);

		} else {//rotate before comparing the keys to delete accordingly
			if (isRed(current.left)) {//make the current node to be red for deletion
				current = rotateRight(current);
			}
			if ((current.right != null) && (current.left != null)) {
				if (!isRed(current.right) && !isRed(current.right.left)) {
					current = moveRedRight(current);
				}
			}

			if (key.compareTo(current.key) == 0) {

				//node needing to be deleted is at the bottom of the tree
				if (current.right == null) {
					return null;
				} else {//if the node has children
					Node successor = successor(current.right);

					//copy the values of the successor key and value
					current.key = successor.key;
					current.val = successor.val;

					//remove the duplicate
					current.right = deleteSuccessor(current.right);
				}
			} else if (key.compareTo(current.key) > 0) {
				current.right = delete(current.right, key);
			}
		}
		return balance(current);
	}

	//Finding the successor of the current position for deletion
	private Node successor (Node current) {
		assert current != null : "Current node is null";

		if (current.left == null) {
			return current;
		} else {
			return successor(current.left);
		}
	}

	//Removing the successor that has been duplicated in delete method
	private Node deleteSuccessor(Node current) {
		if (current.left == null)
			return null;

		if (!isRed(current.left) && !isRed(current.left.left))
			current = moveRedLeft(current);

		current.left = deleteSuccessor(current.left);
		return balance(current);
	}

	/*************************************************************************
	 *  Search Functions
	 *************************************************************************/

	// value associated with the given key; null if no such key
	public Value search(Key key) { 
		//TODO

		if (root == null) {//no tree exists
			return null;
		} else {
			Node current = root;
			current = search(current, key);
			if (current == null) {
				return null;
			} else {
				assert current != null: "Current node is null";
				return current.val;
			}
		}
	}

	//recursive method for search
	private Node search (Node current, Key key){
		if(current == null) {
			return null;
		}
		assert current != null : "Current node is null";

		if (key.compareTo(current.key) == 0) {
			return current;
		} else if (key.compareTo(current.key) < 0) {
			if (current.left == null) {
				return null;
			} else {
				assert current.left != null: "The left node is null";
				current = current.left;
				current = search(current, key);
				return current;
			}
		} else {
			if (current.right == null) {
				return null;
			} else {
				assert current.right != null: "The right node is null";
				current = current.right;
				current = search(current, key);
				return current;
			}
		}
	}

	// is there a key-value pair with the given key?
	public boolean contains(Key key) {
		return (search(key) != null);
	}



	/*************************************************************************
	 *  Utility functions
	 *************************************************************************/

	// height of tree (1-node tree has height 0)
	public int height() { return height(root); }
	private int height(Node x) {
		if (x == null) return -1;
		return 1 + Math.max(height(x.left), height(x.right));
	}

	/*************************************************************************
	 *  Rank Methods
	 *************************************************************************/



	// the key of rank k
	public Key getValByRank(int k) {
		//TODO
		if (k > size(root)) {//rank indicated has more nodes than the tree size
			return null;
		} else {
			Node current;
			current = getValByRank(root, k);
			if(current == null) {
				return null;
			} else {
				return current.key;
			}
		}
	}

	//recursive method for getValByRank
	private Node getValByRank(Node current, int k) {
		if (current == null) {
			return null;
		}
		if (size(current.left) == k) {
			return current;
		} else if (size(current.left) > k) {
			if (current.left != null) {
				return getValByRank(current.left, k);
			} else {
				return null;
			}
		} else {
			if(current.right != null) {
				return getValByRank(current.right, k - 1 - size(current.left));
			} else {
				return null;
			}
		}

	}

	// number of keys less than key
	public int rank(Key key) {
		//TODO

		int keyCount = 0;

		if (height() == 0) {
			return keyCount;
		} else {
			keyCount = rank(root, key);
			return keyCount;
		}
	}

	//recursive method for rank
	private int rank (Node current, Key key) {
		if (current == null) {
			return 0;
		}
		if (key.compareTo(current.key) == 0) {
			return size(current.left);
		} else if (key.compareTo(current.key) < 0) {
			current = current.left;
			return rank(current, key);
		} else {
			return rank(current.right, key) + size(current.left) + 1;
		}
	}


	/***********************************************************************
	 *  Range count and range search.
	 ***********************************************************************/

	public List<Key> getElements(int a, int b){
		//TODO
		List<Key> elementsInRange = new ArrayList<Key> ();

		if (a > b || a < 0 || b < 0 || b >= size(root)) {
			return elementsInRange;
		}
		else {
			Key lower = getValByRank(a);
			Key upper = getValByRank(b);
			getElementsKey(root, elementsInRange, lower, upper);
			return elementsInRange;
		}
	}

	//recursive method for getElements
	private void getElementsKey(Node current, List<Key>elementsInRange, Key lower, Key upper) {
		if (current == null) {
			return;
		} else {
			if (lower.compareTo(current.key) < 0) {
				getElementsKey(current.left, elementsInRange, lower, upper);
			}
			if (lower.compareTo(current.key) <= 0 && upper.compareTo(current.key) >= 0) {
				elementsInRange.add(current.key);
			}
			if (upper.compareTo(current.key) > 0) {
				getElementsKey(current.right, elementsInRange, lower, upper);
			}
		}
	}



	/*************************************************************************
	 *  red-black tree helper functions
	 *************************************************************************/

	// make a left-leaning link lean to the right
	private Node rotateRight(Node h) {
		// assert (h != null) && isRed(h.left);
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = x.right.color;
		x.right.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// make a right-leaning link lean to the left
	private Node rotateLeft(Node h) {
		// assert (h != null) && isRed(h.right);
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = x.left.color;
		x.left.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// flip the colors of a node and its two children
	private void flipColors(Node h) {
		// h must have opposite color of its two children
		// assert (h != null) && (h.left != null) && (h.right != null);
		// assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
		//     || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
		h.color = !h.color;
		h.left.color = !h.left.color;
		h.right.color = !h.right.color;
	}

	// Assuming that h is red and both h.left and h.left.left
	// are black, make h.left or one of its children red.
	private Node moveRedLeft(Node h) {
		// assert (h != null);
		// assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

		flipColors(h);
		if (isRed(h.right.left)) {
			h.right = rotateRight(h.right);
			h = rotateLeft(h);
		}
		return h;
	}

	// Assuming that h is red and both h.right and h.right.left
	// are black, make h.right or one of its children red.
	private Node moveRedRight(Node h) {
		// assert (h != null);
		// assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
		flipColors(h);
		if (isRed(h.left.left)) {
			h = rotateRight(h);
		}
		return h;
	}

	// restore red-black tree invariant
	private Node balance(Node h) {
		// assert (h != null);

		if (isRed(h.right))                      h = rotateLeft(h);
		if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
		if (isRed(h.left) && isRed(h.right))     flipColors(h);

		h.N = size(h.left) + size(h.right) + 1;
		return h;
	}

    
    
    
    
    /*************************************************************************
     *  The manin funciton
        Use this for testing
     *************************************************************************/
    public static void main(String[] args) {
        
        Scanner readerTest = null;

        try {
            //Change File name to test other test files.
            readerTest = new Scanner(new File(args[0]));
        } catch (IOException e) {
            System.out.println("Reading Oops");
        }
        
        RedBlackBST<Integer, Integer> test = new RedBlackBST<>();
        
        while(readerTest.hasNextLine()){
           String[] input  =readerTest.nextLine().split(" ");
           
           for(String x: input){
               System.out.print(x+" ");
           }
            
           System.out.println();
           switch (input[0]){
               case "insert":
                   Integer key = Integer.parseInt(input[1]);
                   Integer val = Integer.parseInt(input[2]);                 
                   test.insert(key,val);
				   break;
                   
               case "delete":
                    Integer key1 = Integer.parseInt(input[1]);
                    test.delete(key1);
				   break;
                   
               case "search":
                    Integer key2 = Integer.parseInt(input[1]);
                    Integer ans2 = test.search(key2);                    
                    System.out.println(ans2);
                    System.out.println();
                    break;   
                   
               case "getval":
                    Integer key3 = Integer.parseInt(input[1]);
                    Integer ans21 = test.getValByRank(key3);
                    System.out.println(ans21);
                    System.out.println();
                    break;
                   
               case "rank":
                    Integer key4 = Integer.parseInt(input[1]);
                    Object ans22 = test.rank(key4);
                    System.out.println(ans22);
                    System.out.println();
                    break;   
                   
               case "getelement":
                    Integer low = Integer.parseInt(input[1]);
                    Integer high = Integer.parseInt(input[2]);
                    List<Integer> testList = test.getElements(low,high);
                   
                    for(Integer list : testList){
                        System.out.println(list);    
                    }
                   
                    break;
               
               default:
                    System.out.println("Error, Invalid test instruction! "+input[0]);    
           }
        }
        
    }    
    
    
    /*************************************************************************
     *  Prints the tree
     *************************************************************************/
    public static void printTree(RedBlackBST.Node node){
        
	    if (node == null){
		    return;
	    }
	   
	    printTree(node.left);
	    System.out.print(((node.color == true)? "Color: Red; ":"Color: Black; ") + "Key: " + node.key + " Value: " + node.val + "\n");
        printTree(node.right);
    }
}