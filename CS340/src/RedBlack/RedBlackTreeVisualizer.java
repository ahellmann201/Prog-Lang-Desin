package RedBlack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Red-Black Tree Node
class RBNode {
    int data;
    RBNode parent;
    RBNode left;
    RBNode right;
    int color; // 0 for black, 1 for red
    
    public RBNode(int data) {
        this.data = data;
        this.color = 1; // New nodes are always red initially
        this.left = null;
        this.right = null;
        this.parent = null;
    }
}

// Red-Black Tree implementation
class RedBlackTree {
    private RBNode root;
    private RBNode TNULL;
    
    public RedBlackTree() {
        TNULL = new RBNode(0);
        TNULL.color = 0;
        TNULL.left = null;
        TNULL.right = null;
        root = TNULL;
    }
    
    // Search the tree
    private RBNode searchTreeHelper(RBNode node, int key) {
        if (node == TNULL || key == node.data) {
            return node;
        }
        
        if (key < node.data) {
            return searchTreeHelper(node.left, key);
        }
        return searchTreeHelper(node.right, key);
    }
    
    // Balance the tree after deletion
    private void fixDelete(RBNode x) {
        RBNode s;
        while (x != root && x.color == 0) {
            if (x == x.parent.left) {
                s = x.parent.right;
                if (s.color == 1) {
                    s.color = 0;
                    x.parent.color = 1;
                    leftRotate(x.parent);
                    s = x.parent.right;
                }
                
                if (s.left.color == 0 && s.right.color == 0) {
                    s.color = 1;
                    x = x.parent;
                } else {
                    if (s.right.color == 0) {
                        s.left.color = 0;
                        s.color = 1;
                        rightRotate(s);
                        s = x.parent.right;
                    }
                    
                    s.color = x.parent.color;
                    x.parent.color = 0;
                    s.right.color = 0;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                s = x.parent.left;
                if (s.color == 1) {
                    s.color = 0;
                    x.parent.color = 1;
                    rightRotate(x.parent);
                    s = x.parent.left;
                }
                
                if (s.right.color == 0 && s.right.color == 0) {
                    s.color = 1;
                    x = x.parent;
                } else {
                    if (s.left.color == 0) {
                        s.right.color = 0;
                        s.color = 1;
                        leftRotate(s);
                        s = x.parent.left;
                    }
                    
                    s.color = x.parent.color;
                    x.parent.color = 0;
                    s.left.color = 0;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = 0;
    }
    
    // Transplant nodes during deletion
    private void rbTransplant(RBNode u, RBNode v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }
    
    // Delete node from the tree
    public boolean delete(int data) {
        RBNode z = searchTreeHelper(this.root, data);
        if (z == TNULL) {
            return false;
        }
        
        RBNode y = z;
        RBNode x;
        int yOriginalColor = y.color;
        
        if (z.left == TNULL) {
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == TNULL) {
            x = z.left;
            rbTransplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            
            if (y.parent == z) {
                x.parent = y;
            } else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            
            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        
        if (yOriginalColor == 0) {
            fixDelete(x);
        }
        
        return true;
    }
    
    // Balance the tree after insertion
    private void fixInsert(RBNode k) {
        RBNode u;
        while (k.parent.color == 1) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left;
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    leftRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.right;
                
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) {
                        k = k.parent;
                        leftRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) {
                break;
            }
        }
        root.color = 0;
    }
    
    // Rotate left at node x
    private void leftRotate(RBNode x) {
        RBNode y = x.right;
        x.right = y.left;
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }
    
    // Rotate right at node x
    private void rightRotate(RBNode x) {
        RBNode y = x.left;
        x.left = y.right;
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }
    
    // Insert a new node
    public void insert(int key) {
        RBNode node = new RBNode(key);
        node.parent = null;
        node.data = key;
        node.left = TNULL;
        node.right = TNULL;
        node.color = 1;
        
        RBNode y = null;
        RBNode x = this.root;
        
        while (x != TNULL) {
            y = x;
            if (node.data < x.data) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        
        node.parent = y;
        if (y == null) {
            root = node;
        } else if (node.data < y.data) {
            y.left = node;
        } else {
            y.right = node;
        }
        
        if (node.parent == null) {
            node.color = 0;
            return;
        }
        
        if (node.parent.parent == null) {
            return;
        }
        
        fixInsert(node);
    }
    
    // Find the minimum node
    private RBNode minimum(RBNode node) {
        while (node.left != TNULL) {
            node = node.left;
        }
        return node;
    }
    
    public RBNode getRoot() {
        return root;
    }
    
    public RBNode getTNULL() {
        return TNULL;
    }
    
    // Get tree structure for study tool
    public String getTreeStructure() {
        StringBuilder sb = new StringBuilder();
        buildTreeString(root, sb, 0);
        return sb.toString();
    }
    
    private void buildTreeString(RBNode node, StringBuilder sb, int depth) {
        if (node == TNULL) return;
        
        buildTreeString(node.right, sb, depth + 1);
        sb.append("  ".repeat(depth));
        sb.append(node.color == 0 ? "B:" : "R:").append(node.data).append("\n");
        buildTreeString(node.left, sb, depth + 1);
    }
}

// Tree Position in the grid
class TreePosition {
    int level;
    int position;
    
    public TreePosition(int level, int position) {
        this.level = level;
        this.position = position;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TreePosition that = (TreePosition) obj;
        return level == that.level && position == that.position;
    }
    
    @Override
    public String toString() {
        return level + "," + position;
    }
}

// Tree Node for the grid system
class TreeNode {
    String number;
    boolean isRed;
    TreePosition treePosition;
    boolean isEmpty;
    
    public TreeNode(TreePosition treePosition) {
        this.treePosition = treePosition;
        this.number = "";
        this.isRed = true;
        this.isEmpty = true;
    }
    
    public void placeNode(String number, boolean isRed) {
        this.number = number;
        this.isRed = isRed;
        this.isEmpty = false;
    }
    
    public void clear() {
        this.number = "";
        this.isEmpty = true;
    }
}

// Study Tool Panel with Proper Centered Tree Grid System
class StudyToolPanel extends JPanel {
    private JPanel canvas;
    private JTextArea feedbackArea;
    private RedBlackTree tree;
    private TreeNode[][] treeGrid;
    private List<TreeNode> placedNodes;
    private TreeNode selectedNode;
    
    private static final int TREE_HEIGHT = 5; // Levels in the tree
    private static final int NODE_SIZE = 40;
    private static final int HORIZONTAL_SPACING = 80;
    private static final int VERTICAL_SPACING = 80;
    private static final int START_X = 400; // Centered start
    private static final int START_Y = 50;
    
    private JButton addRedButton, addBlackButton;
    private JButton clearButton, checkButton;
    private JTextField numberField;
    private JButton setNumberButton;
    private JButton flipColorButton;
    private JButton clearNodeButton;
    private boolean placementRed = true; // Track placement color
    
    public StudyToolPanel(RedBlackTree tree) {
        this.tree = tree;
        // Calculate maximum nodes per level: 2^level
        int maxCols = (int) Math.pow(2, TREE_HEIGHT - 1);
        this.treeGrid = new TreeNode[TREE_HEIGHT][maxCols];
        this.placedNodes = new ArrayList<>();
        initializeTreeGrid();
        initializeStudyTool();
    }
    
    private void initializeTreeGrid() {
        for (int level = 0; level < TREE_HEIGHT; level++) {
            int nodesInLevel = (int) Math.pow(2, level);
            for (int pos = 0; pos < nodesInLevel; pos++) {
                treeGrid[level][pos] = new TreeNode(new TreePosition(level, pos));
            }
        }
    }
    
    private void initializeStudyTool() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Tree Grid Study Tool - Click to Place Nodes"));
        setPreferredSize(new Dimension(500, 600));
        
        // Create canvas for tree grid
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                drawTreeGrid(g2d);
                drawTreeConnections(g2d);
                drawTreeNodes(g2d);
            }
            
            private void drawTreeGrid(Graphics2D g2d) {
                // Draw tree structure lines (proper binary tree pattern)
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                for (int level = 0; level < TREE_HEIGHT - 1; level++) {
                    int nodesInLevel = (int) Math.pow(2, level);
                    for (int pos = 0; pos < nodesInLevel; pos++) {
                        TreeNode parent = treeGrid[level][pos];
                        if (parent == null) continue;
                        
                        int parentX = getNodeX(level, pos);
                        int parentY = getNodeY(level);
                        
                        // Calculate child positions
                        int leftChildPos = pos * 2;
                        int rightChildPos = pos * 2 + 1;
                        int nextLevel = level + 1;
                        
                        // Left child connection
                        if (nextLevel < TREE_HEIGHT && leftChildPos < Math.pow(2, nextLevel)) {
                            TreeNode leftChild = treeGrid[nextLevel][leftChildPos];
                            if (leftChild != null) {
                                int childX = getNodeX(nextLevel, leftChildPos);
                                int childY = getNodeY(nextLevel);
                                g2d.setColor(new Color(100, 200, 100)); // Green for left
                                g2d.drawLine(parentX, parentY, childX, childY);
                            }
                        }
                        
                        // Right child connection
                        if (nextLevel < TREE_HEIGHT && rightChildPos < Math.pow(2, nextLevel)) {
                            TreeNode rightChild = treeGrid[nextLevel][rightChildPos];
                            if (rightChild != null) {
                                int childX = getNodeX(nextLevel, rightChildPos);
                                int childY = getNodeY(nextLevel);
                                g2d.setColor(new Color(255, 150, 50)); // Orange for right
                                g2d.drawLine(parentX, parentY, childX, childY);
                            }
                        }
                    }
                }
                
                // Draw grid positions (empty circles)
                g2d.setColor(new Color(200, 200, 200, 150));
                g2d.setStroke(new BasicStroke(1));
                for (int level = 0; level < TREE_HEIGHT; level++) {
                    int nodesInLevel = (int) Math.pow(2, level);
                    for (int pos = 0; pos < nodesInLevel; pos++) {
                        int x = getNodeX(level, pos);
                        int y = getNodeY(level);
                        
                        // Draw empty circle
                        g2d.drawOval(x - NODE_SIZE/2, y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
                        
                        // Draw position label (small and subtle)
                        g2d.setColor(Color.GRAY);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                        String label = level + "," + pos;
                        FontMetrics fm = g2d.getFontMetrics();
                        int labelWidth = fm.stringWidth(label);
                        g2d.drawString(label, x - labelWidth/2, y - NODE_SIZE/2 - 5);
                        g2d.setColor(new Color(200, 200, 200, 150));
                    }
                }
            }
            
            private void drawTreeConnections(Graphics2D g2d) {
                // Draw connections between placed nodes following tree structure
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                for (int level = 0; level < TREE_HEIGHT - 1; level++) {
                    int nodesInLevel = (int) Math.pow(2, level);
                    for (int pos = 0; pos < nodesInLevel; pos++) {
                        TreeNode parent = treeGrid[level][pos];
                        if (parent == null || parent.isEmpty) continue;
                        
                        int parentX = getNodeX(level, pos);
                        int parentY = getNodeY(level);
                        
                        // Calculate child positions
                        int leftChildPos = pos * 2;
                        int rightChildPos = pos * 2 + 1;
                        int nextLevel = level + 1;
                        
                        // Left child connection
                        if (nextLevel < TREE_HEIGHT && leftChildPos < Math.pow(2, nextLevel)) {
                            TreeNode leftChild = treeGrid[nextLevel][leftChildPos];
                            if (leftChild != null && !leftChild.isEmpty) {
                                int childX = getNodeX(nextLevel, leftChildPos);
                                int childY = getNodeY(nextLevel);
                                drawArrow(g2d, parentX, parentY, childX, childY, new Color(0, 180, 0));
                            }
                        }
                        
                        // Right child connection
                        if (nextLevel < TREE_HEIGHT && rightChildPos < Math.pow(2, nextLevel)) {
                            TreeNode rightChild = treeGrid[nextLevel][rightChildPos];
                            if (rightChild != null && !rightChild.isEmpty) {
                                int childX = getNodeX(nextLevel, rightChildPos);
                                int childY = getNodeY(nextLevel);
                                drawArrow(g2d, parentX, parentY, childX, childY, new Color(255, 120, 0));
                            }
                        }
                    }
                }
            }
            
            private void drawTreeNodes(Graphics2D g2d) {
                // Draw all placed nodes
                for (int level = 0; level < TREE_HEIGHT; level++) {
                    int nodesInLevel = (int) Math.pow(2, level);
                    for (int pos = 0; pos < nodesInLevel; pos++) {
                        TreeNode node = treeGrid[level][pos];
                        if (node == null || node.isEmpty) continue;
                        
                        int x = getNodeX(level, pos);
                        int y = getNodeY(level);
                        
                        // Draw selection highlight
                        if (node == selectedNode) {
                            g2d.setColor(Color.YELLOW);
                            g2d.fillOval(x - NODE_SIZE/2 - 3, y - NODE_SIZE/2 - 3, NODE_SIZE + 6, NODE_SIZE + 6);
                        }
                        
                        // Draw node circle
                        if (node.isRed) {
                            g2d.setColor(Color.RED);
                        } else {
                            g2d.setColor(Color.BLACK);
                        }
                        g2d.fillOval(x - NODE_SIZE/2, y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
                        
                        // Draw number
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        String displayText = node.number.isEmpty() ? "?" : node.number;
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(displayText);
                        int textHeight = fm.getHeight();
                        g2d.drawString(displayText, x - textWidth/2, y + textHeight/4);
                        
                        // Draw border
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(x - NODE_SIZE/2, y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
                    }
                }
            }
            
            private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, Color color) {
                g2d.setColor(color);
                g2d.drawLine(x1, y1, x2, y2);
                
                // Draw arrow head
                double angle = Math.atan2(y2 - y1, x2 - x1);
                int arrowSize = 8;
                int arrowX = (int) (x2 - arrowSize * Math.cos(angle));
                int arrowY = (int) (y2 - arrowSize * Math.sin(angle));
                
                Polygon arrowHead = new Polygon();
                arrowHead.addPoint(x2, y2);
                arrowHead.addPoint((int)(arrowX - arrowSize * Math.cos(angle - Math.PI/6)), 
                                 (int)(arrowY - arrowSize * Math.sin(angle - Math.PI/6)));
                arrowHead.addPoint((int)(arrowX - arrowSize * Math.cos(angle + Math.PI/6)), 
                                 (int)(arrowY - arrowSize * Math.sin(angle + Math.PI/6)));
                
                g2d.fill(arrowHead);
            }
        };
        canvas.setBackground(Color.WHITE);
        canvas.setPreferredSize(new Dimension(800, 600));
        
        // Add mouse listener for node placement
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TreePosition clickedPos = getPositionAt(e.getX(), e.getY());
                if (clickedPos != null) {
                    handleGridClick(clickedPos, e.getButton() == MouseEvent.BUTTON3); // Right click to clear
                }
            }
        });
        
        JScrollPane canvasScroll = new JScrollPane(canvas);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        
        // Add nodes panel
        JPanel addButtonsPanel = new JPanel(new FlowLayout());
        addButtonsPanel.setBorder(BorderFactory.createTitledBorder("Placement Mode"));
        
        addRedButton = new JButton("Place Red Nodes");
        addBlackButton = new JButton("Place Black Nodes");
        
        addRedButton.setBackground(Color.RED);
        addRedButton.setForeground(Color.WHITE);
        addRedButton.setOpaque(true);
        addRedButton.setBorderPainted(false);
        
        addBlackButton.setBackground(Color.BLACK);
        addBlackButton.setForeground(Color.WHITE);
        addBlackButton.setOpaque(true);
        addBlackButton.setBorderPainted(false);
        
        addButtonsPanel.add(addRedButton);
        addButtonsPanel.add(addBlackButton);
        
        // Node management panel
        JPanel nodeManagementPanel = new JPanel(new FlowLayout());
        nodeManagementPanel.setBorder(BorderFactory.createTitledBorder("Node Management"));
        
        numberField = new JTextField(8);
        setNumberButton = new JButton("Set Number");
        flipColorButton = new JButton("Flip Color");
        clearNodeButton = new JButton("Clear Selected Node");
        
        nodeManagementPanel.add(new JLabel("Number:"));
        nodeManagementPanel.add(numberField);
        nodeManagementPanel.add(setNumberButton);
        nodeManagementPanel.add(flipColorButton);
        nodeManagementPanel.add(clearNodeButton);
        
        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout());
        clearButton = new JButton("Clear All Nodes");
        checkButton = new JButton("Check Answer");
        
        actionPanel.add(clearButton);
        actionPanel.add(checkButton);
        
        // Feedback area
        feedbackArea = new JTextArea(6, 40);
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        feedbackScroll.setBorder(BorderFactory.createTitledBorder("Instructions & Feedback"));
        
        // Layout
        controlPanel.add(addButtonsPanel);
        controlPanel.add(nodeManagementPanel);
        controlPanel.add(actionPanel);
        
        add(controlPanel, BorderLayout.NORTH);
        add(canvasScroll, BorderLayout.CENTER);
        add(feedbackScroll, BorderLayout.SOUTH);
        
        // Event listeners
        addRedButton.addActionListener(e -> setPlacementMode(true));
        addBlackButton.addActionListener(e -> setPlacementMode(false));
        setNumberButton.addActionListener(e -> setNumberForSelectedNode());
        flipColorButton.addActionListener(e -> flipSelectedNodeColor());
        clearNodeButton.addActionListener(e -> clearSelectedNode());
        clearButton.addActionListener(e -> clearAllNodes());
        checkButton.addActionListener(e -> checkAnswer());
        
        numberField.addActionListener(e -> setNumberForSelectedNode());
        
        // Initial instructions
        feedbackArea.setText("TREE GRID STUDY TOOL INSTRUCTIONS:\n" +
            "1. Click 'Place Red Nodes' or 'Place Black Nodes' to set placement mode\n" +
            "2. Click on any empty circle in the tree grid to place a node\n" +
            "3. Click on a placed node to select it (yellow highlight)\n" +
            "4. Enter number and click 'Set Number' to assign value\n" +
            "5. Use 'Flip Color' to change node color\n" +
            "6. Right-click a node or use 'Clear Selected Node' to remove\n" +
            "7. Green lines = left children, Orange lines = right children\n" +
            "8. The tree structure is fixed - nodes automatically connect\n\n" +
            "Ready to start building!");
    }
    
    private int getNodeX(int level, int position) {
        int nodesInLevel = (int) Math.pow(2, level);
        double levelWidth = nodesInLevel * HORIZONTAL_SPACING;
        double startX = START_X - levelWidth / 2 + HORIZONTAL_SPACING / 2;
        return (int) (startX + position * HORIZONTAL_SPACING);
    }
    
    private int getNodeY(int level) {
        return START_Y + level * VERTICAL_SPACING;
    }
    
    private TreePosition getPositionAt(int x, int y) {
        for (int level = 0; level < TREE_HEIGHT; level++) {
            int nodesInLevel = (int) Math.pow(2, level);
            for (int pos = 0; pos < nodesInLevel; pos++) {
                int nodeX = getNodeX(level, pos);
                int nodeY = getNodeY(level);
                
                double distance = Math.sqrt(Math.pow(x - nodeX, 2) + Math.pow(y - nodeY, 2));
                if (distance <= NODE_SIZE / 2) {
                    return new TreePosition(level, pos);
                }
            }
        }
        return null;
    }
    
    private void setPlacementMode(boolean isRed) {
        placementRed = isRed;
        if (isRed) {
            addRedButton.setBackground(new Color(255, 100, 100)); // Bright red
            addBlackButton.setBackground(Color.GRAY);
            feedbackArea.append("Placement mode: RED - Click empty circles to place red nodes\n");
        } else {
            addRedButton.setBackground(Color.GRAY);
            addBlackButton.setBackground(new Color(80, 80, 80)); // Dark gray
            feedbackArea.append("Placement mode: BLACK - Click empty circles to place black nodes\n");
        }
    }
    
    private void handleGridClick(TreePosition pos, boolean isRightClick) {
        TreeNode node = treeGrid[pos.level][pos.position];
        
        if (isRightClick) {
            // Right click - clear node
            if (!node.isEmpty) {
                node.clear();
                placedNodes.remove(node);
                if (selectedNode == node) {
                    selectedNode = null;
                    numberField.setText("");
                }
                feedbackArea.append("Cleared node at position " + pos + "\n");
                repaintCanvas();
            }
        } else {
            // Left click - select or place node
            if (node.isEmpty) {
                // Place new node
                String number = JOptionPane.showInputDialog(this, "Enter number for new node:");
                if (number != null && !number.trim().isEmpty()) {
                    node.placeNode(number.trim(), placementRed);
                    if (!placedNodes.contains(node)) {
                        placedNodes.add(node);
                    }
                    selectNode(node);
                    feedbackArea.append("Placed " + (placementRed ? "RED" : "BLACK") + " node '" + number + "' at " + pos + "\n");
                    repaintCanvas();
                }
            } else {
                // Select existing node
                selectNode(node);
                feedbackArea.append("Selected node '" + node.number + "' at " + pos + "\n");
            }
        }
    }
    
    private void selectNode(TreeNode node) {
        selectedNode = node;
        if (node != null) {
            numberField.setText(node.number);
        } else {
            numberField.setText("");
        }
        repaintCanvas();
    }
    
    private void setNumberForSelectedNode() {
        if (selectedNode == null) {
            feedbackArea.setText("Please select a node first by clicking on it.\n");
            return;
        }
        
        String numberText = numberField.getText().trim();
        if (numberText.isEmpty()) {
            feedbackArea.setText("Please enter a number in the text field.\n");
            return;
        }
        
        try {
            Integer.parseInt(numberText);
            selectedNode.number = numberText;
            feedbackArea.append("Set number '" + numberText + "' for selected node\n");
            repaintCanvas();
        } catch (NumberFormatException ex) {
            feedbackArea.setText("Please enter a valid number (e.g., 5, 10, 25).\n");
        }
    }
    
    private void flipSelectedNodeColor() {
        if (selectedNode == null) {
            feedbackArea.setText("Please select a node first.\n");
            return;
        }
        
        selectedNode.isRed = !selectedNode.isRed;
        feedbackArea.append("Flipped color to: " + (selectedNode.isRed ? "RED" : "BLACK") + "\n");
        repaintCanvas();
    }
    
    private void clearSelectedNode() {
        if (selectedNode == null) {
            feedbackArea.setText("Please select a node first.\n");
            return;
        }
        
        TreePosition pos = selectedNode.treePosition;
        selectedNode.clear();
        placedNodes.remove(selectedNode);
        selectedNode = null;
        numberField.setText("");
        feedbackArea.append("Cleared node at position " + pos + "\n");
        repaintCanvas();
    }
    
    private void clearAllNodes() {
        for (TreeNode node : placedNodes) {
            node.clear();
        }
        placedNodes.clear();
        selectedNode = null;
        numberField.setText("");
        feedbackArea.setText("All nodes cleared. Ready to start fresh!\n");
        repaintCanvas();
    }
    
    private void checkAnswer() {
        StringBuilder result = new StringBuilder();
        result.append("=== REAL TREE STRUCTURE ===\n");
        result.append(tree.getTreeStructure());
        result.append("\n=== YOUR TREE ===\n");
        
        if (placedNodes.isEmpty()) {
            result.append("No nodes placed\n");
        } else {
            result.append("Placed nodes:\n");
            for (TreeNode node : placedNodes) {
                result.append("  Position ").append(node.treePosition)
                      .append(": ").append(node.isRed ? "RED:" : "BLACK:")
                      .append(node.number).append("\n");
            }
            
            // Find root (position 0,0)
            TreeNode root = treeGrid[0][0];
            if (!root.isEmpty) {
                result.append("\nRoot: ").append(root.number).append(" (").append(root.isRed ? "RED" : "BLACK").append(")\n");
            }
        }
        
        feedbackArea.setText(result.toString());
    }
    
    public void repaintCanvas() {
        canvas.revalidate();
        canvas.repaint();
    }
}

// Panel for drawing the tree
class TreePanel extends JPanel {
    private RedBlackTree tree;
    private static final int NODE_RADIUS = 20;
    private static final int HORIZONTAL_GAP = 50;
    private static final int VERTICAL_GAP = 60;
    
    public TreePanel(RedBlackTree tree) {
        this.tree = tree;
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (tree.getRoot() != tree.getTNULL()) {
            drawTree(g2d, tree.getRoot(), getWidth() / 2, 50, getWidth() / 4);
        } else {
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(Color.BLACK);
            g2d.drawString("Tree is empty", getWidth() / 2 - 50, getHeight() / 2);
        }
    }
    
    private void drawTree(Graphics2D g, RBNode node, int x, int y, int xOffset) {
        if (node == tree.getTNULL()) {
            return;
        }
        
        // Draw left subtree
        if (node.left != tree.getTNULL()) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x - xOffset, y + VERTICAL_GAP);
            drawTree(g, node.left, x - xOffset, y + VERTICAL_GAP, xOffset / 2);
        }
        
        // Draw right subtree
        if (node.right != tree.getTNULL()) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x + xOffset, y + VERTICAL_GAP);
            drawTree(g, node.right, x + xOffset, y + VERTICAL_GAP, xOffset / 2);
        }
        
        // Draw node
        if (node.color == 0) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.RED);
        }
        g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String text = String.valueOf(node.data);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g.drawString(text, x - textWidth / 2, y + textHeight / 4);
        
        // Draw node border
        g.setColor(Color.BLACK);
        g.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
    }
}

// Main GUI class
public class RedBlackTreeVisualizer extends JFrame {
    private RedBlackTree tree;
    private TreePanel treePanel;
    private StudyToolPanel studyPanel;
    private JTextField inputField;
    private JTextField minField;
    private JTextField maxField;
    private Random random;
    private JLabel randomNumberLabel;
    
    public RedBlackTreeVisualizer() {
        tree = new RedBlackTree();
        random = new Random();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Red-Black Tree Visualizer with Tree Grid Study Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main control panel
        JPanel mainControlPanel = new JPanel();
        mainControlPanel.setLayout(new FlowLayout());
        
        // Manual input section
        JLabel instructionLabel = new JLabel("Enter number:");
        inputField = new JTextField(8);
        
        JButton insertButton = new JButton("Insert");
        JButton deleteButton = new JButton("Delete");
        
        // Random number section
        JLabel minLabel = new JLabel("Min:");
        minField = new JTextField(4);
        minField.setText("1");
        
        JLabel maxLabel = new JLabel("Max:");
        maxField = new JTextField(4);
        maxField.setText("100");
        
        JButton randomInsertButton = new JButton("Generate & Show Random");
        JButton insertShownRandomButton = new JButton("Insert Shown Random");
        randomNumberLabel = new JLabel("No random number generated");
        randomNumberLabel.setForeground(Color.BLUE);
        randomNumberLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton clearButton = new JButton("Clear Tree");
        
        // Add components to tree control panel
        mainControlPanel.add(instructionLabel);
        mainControlPanel.add(inputField);
        mainControlPanel.add(insertButton);
        mainControlPanel.add(deleteButton);
        
        mainControlPanel.add(Box.createHorizontalStrut(20));
        
        mainControlPanel.add(minLabel);
        mainControlPanel.add(minField);
        mainControlPanel.add(maxLabel);
        mainControlPanel.add(maxField);
        mainControlPanel.add(randomInsertButton);
        mainControlPanel.add(insertShownRandomButton);
        mainControlPanel.add(randomNumberLabel);
        
        mainControlPanel.add(Box.createHorizontalStrut(20));
        
        mainControlPanel.add(clearButton);
        
        // Create tree visualization panel
        treePanel = new TreePanel(tree);
        JScrollPane treeScrollPane = new JScrollPane(treePanel);
        
        // Create study tool panel
        studyPanel = new StudyToolPanel(tree);
        JScrollPane studyScrollPane = new JScrollPane(studyPanel);
        
        // Use JSplitPane to divide the window horizontally
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, studyScrollPane);
        mainSplitPane.setResizeWeight(0.4);
        
        // Add components to main frame
        add(mainControlPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Event listeners
        insertButton.addActionListener(e -> insertNumber());
        deleteButton.addActionListener(e -> deleteNumber());
        randomInsertButton.addActionListener(e -> generateAndShowRandom());
        insertShownRandomButton.addActionListener(e -> insertShownRandom());
        clearButton.addActionListener(e -> clearTree());
        inputField.addActionListener(e -> insertNumber());
        
        pack();
        setSize(1600, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void insertNumber() {
        try {
            int number = Integer.parseInt(inputField.getText());
            tree.insert(number);
            inputField.setText("");
            treePanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteNumber() {
        try {
            int number = Integer.parseInt(inputField.getText());
            boolean deleted = tree.delete(number);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Number " + number + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Number " + number + " not found in the tree.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
            inputField.setText("");
            treePanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateAndShowRandom() {
        try {
            int min = Integer.parseInt(minField.getText());
            int max = Integer.parseInt(maxField.getText());
            
            if (min >= max) {
                JOptionPane.showMessageDialog(this, "Minimum value must be less than maximum value.", "Invalid Range", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int randomNumber = random.nextInt(max - min + 1) + min;
            randomNumberLabel.setText("Random: " + randomNumber + " - Try to insert this yourself!");
            randomNumberLabel.setForeground(Color.RED);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for min and max values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void insertShownRandom() {
        String currentText = randomNumberLabel.getText();
        if (currentText.startsWith("Random: ")) {
            try {
                // Extract the number from "Random: X - Try to insert this yourself!"
                String numberStr = currentText.substring(8, currentText.indexOf(" -"));
                int number = Integer.parseInt(numberStr.trim());
                tree.insert(number);
                treePanel.repaint();
                randomNumberLabel.setText("Inserted: " + number);
                randomNumberLabel.setForeground(Color.GREEN);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No valid random number to insert.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please generate a random number first.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void clearTree() {
        tree = new RedBlackTree();
        treePanel = new TreePanel(tree);
        studyPanel = new StudyToolPanel(tree);
        randomNumberLabel.setText("No random number generated");
        randomNumberLabel.setForeground(Color.BLUE);
        
        // Update the split pane
        JSplitPane splitPane = (JSplitPane) getContentPane().getComponent(1);
        splitPane.setLeftComponent(new JScrollPane(treePanel));
        splitPane.setRightComponent(new JScrollPane(studyPanel));
        
        revalidate();
        repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RedBlackTreeVisualizer());
    }
}