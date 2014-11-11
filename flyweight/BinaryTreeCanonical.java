import java.util.Hashtable;

public class BinaryTreeCanonical {

    static Hashtable<Integer, BinaryTreeNode> node = new Hashtable();
    static Hashtable<Integer, BinaryTreeNode> tempNode = new Hashtable();

    static BinaryTreeNode getCanonical(BinaryTreeNode n) {
        BinaryTreeNode leftNode = null;
        BinaryTreeNode rightNode = null;

        // Create hash vals for the new BinaryTreeNode...
        if(n.left == null && n.right == null){
            n.cachedHash = (Integer)n.hashCode();
            if(!tempNode.containsKey(n.cachedHash)) {
                tempNode.put(n.cachedHash, n );
            }
            System.out.println(" Key: " + n.key + " leaf: " + n.toString() + " hash " + n.hashCode() + " cachedHash " + n
                    .cachedHash);
            return n;
        }
        else{
            if(n.left != null){
                System.out.println(" Calling Left Node.. ");
                leftNode = getCanonical(n.left);
//               if(n.cachedHash == null) n.cachedHash = (Integer)0;
//               n.cachedHash += leftNode.cachedHash;
                System.out.println(" Key: " + n.key );
                if(tempNode.containsKey(leftNode.cachedHash)){
                    BinaryTreeNode temp = tempNode.get(leftNode.cachedHash);
                    n.left = temp;
                    System.out.println(" pointing my left node to already existing node");
                }
            }
            if(n.right != null){
                System.out.println(" Calling right Node.. ");
                rightNode = getCanonical(n.right);
//               if(n.cachedHash == null) n.cachedHash = (Integer)0;
//               n.cachedHash += rightNode.cachedHash;
                System.out.println(" Key: " + n.key );

                if(tempNode.containsKey(rightNode.cachedHash)){
                    BinaryTreeNode temp = tempNode.get(rightNode.cachedHash);
                    n.right = temp;
                    System.out.println(" pointing my right node to already existing node");
                }
            }
            if(n.cachedHash == null) n.cachedHash = (Integer)0;
            n.cachedHash += (Integer)n.hashCode();

//          if(tempNode.containsKey(n.cachedHash)){
//                BinaryTreeNode temp = tempNode.get(n.cachedHash);
//            }
            // Only add if its not already there...
            if(!tempNode.containsKey(n.cachedHash)) {
                tempNode.put(n.cachedHash, n);
            }
            System.out.println(" Key: " + n.key + " All done creating the hashtable: " + n.toString() + " cachedHash " +
                    "" + n.cachedHash);
            System.out.println("TempNode size: " + tempNode.size() + " values: " + tempNode.values().toString());
            return n;
        }

    }

    static int numberOfFlyweightNodes() {
        return tempNode.size();
    }

    static class BinaryTreeNode {
        int key;
        BinaryTreeNode left, right;
        Integer cachedHash; // can speed up hash function computation hugely

        public BinaryTreeNode(int k, BinaryTreeNode l, BinaryTreeNode r) {
            this.key = k;
            this.left = l;
            this.right = r;
            this.cachedHash = null;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BinaryTreeNode)) return false;

            BinaryTreeNode that = (BinaryTreeNode) o;

            if (key != that.key) return false;
            if (cachedHash != null ? !cachedHash.equals(that.cachedHash) : that.cachedHash != null) return false;
            if (left != null ? !left.equals(that.left) : that.left != null) return false;
            if (right != null ? !right.equals(that.right) : that.right != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = key;
            result = 31 * result;
            result = 31 * result + (left != null ? left.cachedHash : 0);
            result = 31 * result + (right != null ? right.cachedHash : 0);
//          result = 31 * result + (cachedHash != null ? cachedHash.hashCode() : 0);
            return result;
        }


    }




}

