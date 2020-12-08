import java.util.*;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.*;
import java.awt.event.*;

class MyComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {

        return x.data - y.data;
    }
}

public class TreeViewer extends JPanel {

    static private int CANVAS_HEIGHT = 700;
    static private int CANVAS_WIDTH = 1300;
    static Vector<String> liststring = new Vector<>();
    static HashMap<Character, String> codes = new HashMap<Character, String>();
    static String encoded_text = new String();
    static HuffmanNode root = null;
    private int rootY = 10;
    private int NODE_SIZE = 23;
    private int ROW_HEIGHT = 55;
    mxGraph graph = new mxGraph();
    Object parent = graph.getDefaultParent();

    public static void create_root(String text) {

        HashMap<Character, Integer> d = new HashMap<Character, Integer>();
        HashMap<Character, Integer> sorted_d = new HashMap<Character, Integer>();

        for (int i = 0; i < text.length(); i++) {

            if (!d.containsKey(text.charAt(i))) {
                d.put(text.charAt(i), char_occurence(text, text.charAt(i)));
            }
        }
        sorted_d = sortByValue(d);
        PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>(d.size(), new MyComparator());
        for (Map.Entry<Character, Integer> aux : sorted_d.entrySet()) {

            HuffmanNode hn = new HuffmanNode((int) aux.getValue(), (char) aux.getKey(), null, null);
            q.add(hn);
        }

        // create a root node

        while (q.size() > 1) {
            HuffmanNode x = q.peek();
            q.poll();
            HuffmanNode y = q.peek();
            q.poll();
            HuffmanNode f = new HuffmanNode(x.data + y.data, ' ', x, y);
            root = f;
            q.add(f);
        }
        printCode(root, "");
        encode(text, codes);

        for (Map.Entry<Character, String> aux : codes.entrySet()) {
            liststring.add(aux.getKey() + ":" + aux.getValue());
        }
    }

    public Object drawTree(HuffmanNode root, int depth, int index) {
        if (root == null) {
            return null;
        }
        /*
         * leftChildIndex = parentIndex * 2 - 1 rightChildIndex = parentIndex *2
         *
         * x = index * canvasWidth / (2^depth + 1)
         *
         * y = depth * canvasHeight / treeDepth
         */
        int myX = (int) ((CANVAS_WIDTH * (index)) / (Math.pow(2, depth - 1) + 1));

        Object rootVertex = graph.insertVertex(parent, null, root.data + " " + root.c, myX, depth * ROW_HEIGHT + rootY,
                NODE_SIZE, NODE_SIZE);

        // recurse for right child

        Object rightChildVertex = drawTree(root.right, depth + 1, index * 2);

        if (rightChildVertex != null) {// edge
            graph.insertEdge(parent, null, "1", rootVertex, rightChildVertex,
                    "startArrow=none;endArrow=none;strokeWidth=1;strokeColor=green");
        }

        Object leftChildVertex = drawTree(root.left, depth + 1, index * 2 - 1);

        // recurse for right child

        if (leftChildVertex != null) { // edge
            graph.insertEdge(parent, null, "0", rootVertex, leftChildVertex,
                    "startArrow=none;endArrow=none;strokeWidth=1;strokeColor=green");
        }

        return rootVertex;

    }

    public void update(HuffmanNode root) {

        graph.getModel().beginUpdate();

        try {

            Object[] cells = graph.getChildCells(parent, true, false);
            graph.removeCells(cells, true);
            drawTree(root, 1, 1);

        } finally {
            graph.getModel().endUpdate();
        }
    }

    public TreeViewer(HuffmanNode root) {
        this.update(root);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        this.add(graphComponent);
    }

    public static void printCode(HuffmanNode root, String s) {

        if (root.left == null && root.right == null) {
            codes.put(root.c, s);
            return;
        }

        printCode(root.left, s + "0");
        printCode(root.right, s + "1");
    }

    public static void encode(String text, HashMap<Character, String> codes) {

        for (int i = 0; i < text.length(); i++) {
            encoded_text += codes.get(text.charAt(i));
        }
    }

    public static int char_occurence(String s, char c) {
        int count = 0;
        StringBuilder sb = new StringBuilder(s);
        while (sb.indexOf(c + "") != -1) {
            sb.deleteCharAt(sb.indexOf(c + ""));
            count++;
        }
        return count;
    }

    public static HashMap<Character, Integer> sortByValue(HashMap<Character, Integer> hm) {
        // Create a list from elements of HashMap
        LinkedList<Map.Entry<Character, Integer>> list = new LinkedList<Map.Entry<Character, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Character, Integer>>() {
            public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Character, Integer> temp = new LinkedHashMap<Character, Integer>();
        for (Map.Entry<Character, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static void main(String[] args) {

        JFrame frame1 = new JFrame();
        JTextField field = new JTextField();
        JButton button = new JButton();
        frame1.getContentPane().setLayout(null);
        field.setBounds(50, 20, 450, 20);
        button.setBounds(250, 75, 50, 30);
        frame1.setTitle("HuffmanTree Visualiser");
        field.setBounds(50, 20, 450, 20);
        button.setBounds(215, 75, 120, 30);
        button.setText("Construct tree");
        frame1.add(button);
        frame1.add(field);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setBounds(700, 500, 550, 150);
        frame1.setVisible(true);
        JFrame frame2 = new JFrame();
        String text = null;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = field.getText();
                if (text.length() != 0) {
                    frame1.setVisible(false);
                    create_root(text);
                    TreeViewer myTreeViewer = new TreeViewer(root);
                    JPanel panel = myTreeViewer;
                    JScrollPane scrollPane = new JScrollPane();
                    JLabel label = new JLabel(encoded_text);
                    JList list = new JList(liststring);
                    scrollPane.setViewportView(list);
                    frame2.setLayout(new BorderLayout());
                    frame2.add(panel, BorderLayout.NORTH);
                    frame2.add(label);
                    frame2.add(scrollPane, BorderLayout.SOUTH);
                    frame2.setTitle("HuffmanTree Visualiser");
                    frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame2.setSize(CANVAS_WIDTH + 50, CANVAS_HEIGHT);
                    frame2.setVisible(true);
                } else {
                    System.out.println("error");
                }

            }
        });

    }
}