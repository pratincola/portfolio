import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.util.zip.DataFormatException;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.reflect.*;

public class TestBinaryTreeCanonical {

  @Before
  public void setUp() {
  }

  BinaryTreeCanonical.BinaryTreeNode a = new BinaryTreeCanonical.BinaryTreeNode( 1, null, null );
  BinaryTreeCanonical.BinaryTreeNode b = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );
  BinaryTreeCanonical.BinaryTreeNode c = new BinaryTreeCanonical.BinaryTreeNode( 3, null, null );
  BinaryTreeCanonical.BinaryTreeNode d = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );
  BinaryTreeCanonical.BinaryTreeNode e = new BinaryTreeCanonical.BinaryTreeNode( 3, null, null );
  BinaryTreeCanonical.BinaryTreeNode f = new BinaryTreeCanonical.BinaryTreeNode( 1, null, null );

  BinaryTreeCanonical.BinaryTreeNode bDup = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );

  { // static initializer block
    a.left = b;
    a.right = c;
    d.left = b;
    d.right = e;
    f.left = b;
    f.right = c;
  }

  @Test(timeout=1000000)
  public void testFlyweightsAreUnique() {

    BinaryTreeCanonical.BinaryTreeNode t1 = BinaryTreeCanonical.getCanonical(a);
    BinaryTreeCanonical.BinaryTreeNode t2 = BinaryTreeCanonical.getCanonical(d);
    BinaryTreeCanonical.BinaryTreeNode t3 = BinaryTreeCanonical.getCanonical(f);

    assertFalse(t1==t2);
    assertTrue(t1==t3);
  }

  @Test(timeout=1000)
  public void testRightNumberOfFlyweights() {
    List<BinaryTreeCanonical.BinaryTreeNode> Alist = new LinkedList<BinaryTreeCanonical.BinaryTreeNode>();
    Alist.add( a );
    Alist.add( b );
    Alist.add( c );
    Alist.add( d );
    Alist.add( e );
    Alist.add( f );

    List<BinaryTreeCanonical.BinaryTreeNode> Blist = new LinkedList<BinaryTreeCanonical.BinaryTreeNode>();
    for ( BinaryTreeCanonical.BinaryTreeNode n : Alist ) {
      BinaryTreeCanonical.BinaryTreeNode cn = BinaryTreeCanonical.getCanonical( n );
      Blist.add( cn );
    }
    assertEquals(BinaryTreeCanonical.numberOfFlyweightNodes(), 4);
  }

  // -------------------------------------
  @Test(timeout = 1000)
  public void testFlyweightsAreUnique2() {
      BinaryTreeCanonical.BinaryTreeNode zero = new BinaryTreeCanonical.BinaryTreeNode( 0, null, null );
      BinaryTreeCanonical.BinaryTreeNode one = new BinaryTreeCanonical.BinaryTreeNode( 1, null, null );
      BinaryTreeCanonical.BinaryTreeNode two = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );
      BinaryTreeCanonical.BinaryTreeNode two2 = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );

      BinaryTreeCanonical.BinaryTreeNode three = new BinaryTreeCanonical.BinaryTreeNode( 3, null, null );
      BinaryTreeCanonical.BinaryTreeNode five = new BinaryTreeCanonical.BinaryTreeNode( 5, null, null );
      BinaryTreeCanonical.BinaryTreeNode five2 = new BinaryTreeCanonical.BinaryTreeNode( 5, null, null );
      BinaryTreeCanonical.BinaryTreeNode seven = new BinaryTreeCanonical.BinaryTreeNode( 7, null, null );
      BinaryTreeCanonical.BinaryTreeNode ten = new BinaryTreeCanonical.BinaryTreeNode( 10, null, null );
      { // static initializer block
          one.left = zero;
          two.left = one;
          two2.left = one;

          five.left = three;
          five.right = seven;

          five2.left = three;
          five2.right = ten;

      }
      BinaryTreeCanonical.BinaryTreeNode t1 = BinaryTreeCanonical.getCanonical(two);
      BinaryTreeCanonical.BinaryTreeNode t2 = BinaryTreeCanonical.getCanonical(two2);
      BinaryTreeCanonical.BinaryTreeNode t3 = BinaryTreeCanonical.getCanonical(five);
      BinaryTreeCanonical.BinaryTreeNode t4 = BinaryTreeCanonical.getCanonical(five2);
      assertTrue(t1 == t2);
      assertFalse(t3 == t4);
  }
    @Test(timeout = 1000)
    public void testRightNumberOfFlyweights2() {
        BinaryTreeCanonical.BinaryTreeNode zero = new BinaryTreeCanonical.BinaryTreeNode( 0, null, null );
        BinaryTreeCanonical.BinaryTreeNode one = new BinaryTreeCanonical.BinaryTreeNode( 1, null, null );
        BinaryTreeCanonical.BinaryTreeNode two = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );
        BinaryTreeCanonical.BinaryTreeNode two2 = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );

        BinaryTreeCanonical.BinaryTreeNode three = new BinaryTreeCanonical.BinaryTreeNode( 3, null, null );
        BinaryTreeCanonical.BinaryTreeNode five = new BinaryTreeCanonical.BinaryTreeNode( 5, null, null );
        BinaryTreeCanonical.BinaryTreeNode five2 = new BinaryTreeCanonical.BinaryTreeNode( 5, null, null );
        BinaryTreeCanonical.BinaryTreeNode seven = new BinaryTreeCanonical.BinaryTreeNode( 7, null, null );
        BinaryTreeCanonical.BinaryTreeNode ten = new BinaryTreeCanonical.BinaryTreeNode( 10, null, null );
        { // static initializer block
            one.left = zero;
            two.left = one;
            two2.left = one;

            five.left = three;
            five.right = seven;

            five2.left = three;
            five2.right = ten;

        }
        List<BinaryTreeCanonical.BinaryTreeNode> Alist = new LinkedList<BinaryTreeCanonical.BinaryTreeNode>();
        Alist.add(zero);
        Alist.add(one);
        Alist.add(two);
        Alist.add(two2);
        Alist.add(three);
        Alist.add(five);
        Alist.add(five2);
        Alist.add(seven);
        Alist.add(ten);
        List<BinaryTreeCanonical.BinaryTreeNode> Blist = new LinkedList<BinaryTreeCanonical.BinaryTreeNode>();
        for (BinaryTreeCanonical.BinaryTreeNode n : Alist) {
            BinaryTreeCanonical.BinaryTreeNode cn = BinaryTreeCanonical.getCanonical(n);
            Blist.add(cn);
        }
        assertEquals(BinaryTreeCanonical.numberOfFlyweightNodes(), 8);
    }

    // ----------------------------------

    @Test
    public void testFlippedAreNotIdentical() {
        BinaryTreeCanonical.BinaryTreeNode g = new BinaryTreeCanonical.BinaryTreeNode(4, null, null);
        BinaryTreeCanonical.BinaryTreeNode h = new BinaryTreeCanonical.BinaryTreeNode(4, null, null);
        BinaryTreeCanonical.BinaryTreeNode i = new BinaryTreeCanonical.BinaryTreeNode(4, null, null);
        BinaryTreeCanonical.BinaryTreeNode j = new BinaryTreeCanonical.BinaryTreeNode(4, null, null);
        g.left = a;
        g.right = b;
        h.left = b;
        h.right = a;
        i.left = g;
        i.right =h;
        j.left = g;
        j.right = h;
        BinaryTreeCanonical.BinaryTreeNode t1 = BinaryTreeCanonical.getCanonical(g);
        BinaryTreeCanonical.BinaryTreeNode t2 = BinaryTreeCanonical.getCanonical(h);
        BinaryTreeCanonical.BinaryTreeNode t3 = BinaryTreeCanonical.getCanonical(i);
        BinaryTreeCanonical.BinaryTreeNode t4 = BinaryTreeCanonical.getCanonical(j);

        assertFalse(t1 == t2);
        assertTrue(t3 == t4);
    }

    //  ------------------------------------

    @Test(timeout=1000)
    public void testFlyweightUniqueness() {
        List<BinaryTreeCanonical.BinaryTreeNode> Alist = new LinkedList<BinaryTreeCanonical.BinaryTreeNode>();
        Alist.add(a);
        Alist.add(b);
        Alist.add(c);
        Alist.add(d);
        Alist.add(e);
        Alist.add(f);
        Set<BinaryTreeCanonical.BinaryTreeNode> set = new HashSet<BinaryTreeCanonical.BinaryTreeNode>();
        for (BinaryTreeCanonical.BinaryTreeNode n : Alist) {
            BinaryTreeCanonical.BinaryTreeNode cn = BinaryTreeCanonical.getCanonical(n);
            set.add(cn);
        }
        assertEquals(set.size(), 4);
    }

    // -----------------SERVESH ------------

    // This is for the structure thats shown in the assignment document
    BinaryTreeCanonical.BinaryTreeNode zero0 = new BinaryTreeCanonical.BinaryTreeNode( 0, null, null );
    BinaryTreeCanonical.BinaryTreeNode zero1 = new BinaryTreeCanonical.BinaryTreeNode( 0, null, null );
    BinaryTreeCanonical.BinaryTreeNode one0 = new BinaryTreeCanonical.BinaryTreeNode( 1, null, null );
    BinaryTreeCanonical.BinaryTreeNode one1 = new BinaryTreeCanonical.BinaryTreeNode( 1, null, null );
    BinaryTreeCanonical.BinaryTreeNode two0 = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );
    BinaryTreeCanonical.BinaryTreeNode two1 = new BinaryTreeCanonical.BinaryTreeNode( 2, null, null );
    BinaryTreeCanonical.BinaryTreeNode three0 = new BinaryTreeCanonical.BinaryTreeNode( 3, null, null );
    BinaryTreeCanonical.BinaryTreeNode three1 = new BinaryTreeCanonical.BinaryTreeNode( 3, null, null );
    BinaryTreeCanonical.BinaryTreeNode three2 = new BinaryTreeCanonical.BinaryTreeNode( 3, null, null );
    BinaryTreeCanonical.BinaryTreeNode five0 = new BinaryTreeCanonical.BinaryTreeNode( 5, null, null );
    BinaryTreeCanonical.BinaryTreeNode five1 = new BinaryTreeCanonical.BinaryTreeNode( 5, null, null );
    BinaryTreeCanonical.BinaryTreeNode seven0 = new BinaryTreeCanonical.BinaryTreeNode( 7, null, null );
    BinaryTreeCanonical.BinaryTreeNode seven1 = new BinaryTreeCanonical.BinaryTreeNode( 7, null, null );
    BinaryTreeCanonical.BinaryTreeNode nine0 = new BinaryTreeCanonical.BinaryTreeNode( 9, null, null );
    BinaryTreeCanonical.BinaryTreeNode eleven0 = new BinaryTreeCanonical.BinaryTreeNode( 11, null, null );



    { // static initializer block
        // T1
        three0.left = two0;
        two0.left = one0;
        one0.left = zero0;

        // T2
        nine0.left = five0;
        nine0.right = eleven0;
        five0.left = three1;
        five0.right = seven0;

        // T3
        two1.left = one1;
        one1.left = zero1;
        two1.right = five1;
        five1.left = three2;
        five1.right = seven1;

    }



    @Test(timeout=10000000)
    public void testFlyweightsAreUnique3() {
        System.out.println ("Running testFlyweightsAreUnique");
        System.out.println ("Inserting t1");
        BinaryTreeCanonical.BinaryTreeNode t1 = BinaryTreeCanonical.getCanonical(three0);
        System.out.println ("Inserting t2");
        BinaryTreeCanonical.BinaryTreeNode t2 = BinaryTreeCanonical.getCanonical(nine0);
        System.out.println ("Inserting t3");
        BinaryTreeCanonical.BinaryTreeNode t3 = BinaryTreeCanonical.getCanonical(two1);

        assertTrue((t1!=t2) && (t1!=t3) && (t2!=t3));
        assertTrue(t1.left.left==t3.left);
        assertTrue(t1.left.left.key==1);

        assertTrue(t3.right==t2.left);
        assertTrue(t3.right.key==5);
        assertTrue(BinaryTreeCanonical.numberOfFlyweightNodes() == 10);
    }

}
