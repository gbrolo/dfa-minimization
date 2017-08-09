package Implementation.DirectDFA;

import java.util.*;

/**
 * Created by Gabriel Brolo on 08/08/2017.
 */
public class REtoDFA {
    private String postfixRegexp;
    private List<Node> nodeList;
    private Stack<Node> nodeStack;
    private int posCount;
    private int last;
    private HashMap<Integer, List<Integer>> followposTable;

    public REtoDFA(String postfixRegexp) {
        this.postfixRegexp = postfixRegexp;
        this.nodeList = new LinkedList<>();
        this.nodeStack = new Stack<>();
        this.followposTable = new HashMap<>();
        // extend the regexp
        extendRE();
        // find all nullable, firstpos and lastpos and fill the nodeList
        fillList();
        // compute followpos
        findFollowPos();
        System.out.println(followposTable.toString());
    }

    private void extendRE() {
        this.postfixRegexp = this.postfixRegexp + "#.";
    }

    private void findFollowPos() {
        // initialize followpos table with empty values and as many keys as posCount
        for (int i = 0; i < posCount; i++) {
            this.followposTable.put(i, null);
        }

        // find out last position
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).isLast()) {
                last = nodeList.get(i).getPosition();
            }
        }

        boolean stop = false;

        // iterate over nodeList
        for (int i = 0; i < nodeList.size(); i++) {
            Node currentNode = nodeList.get(i);
            String symbol = currentNode.getSymbol();

            // check for kleene behaviour
            if (symbol.equals("*")) {
                List<Integer> lastpos = currentNode.getLastPos();

                // iterate over kleenes lastpos
                for (int l = 0; l < lastpos.size(); l++) {
                    List<Integer> tmpFollowPos = new LinkedList<>();
                    int currentPos = lastpos.get(l);
                    if (followposTable.get(currentPos) != null) {
                        tmpFollowPos = currentNode.getFirstPos();
                        tmpFollowPos.addAll(followposTable.get(currentPos));
                        followposTable.put(currentPos, tmpFollowPos);

                        if (currentNode.getFirstPos().contains(last)) {
                            stop = true;
                        }

                    } else {
                        tmpFollowPos = currentNode.getFirstPos();
                        followposTable.put(currentPos, tmpFollowPos);

                        if (currentNode.getFirstPos().contains(last)) {
                            stop = true;
                        }

                    }
                }
            }

            // check for catnode behaviour
            if (symbol.equals(".")) {
                List<Integer> lastpos = currentNode.getLeftChild().getLastPos();

                // iterate over left child's lastpos
                for (int l = 0; l < lastpos.size(); l++) {
                    List<Integer> tmpFollowPos = new LinkedList<>();
                    int currentPos = lastpos.get(l);

                    if (followposTable.get(currentPos) != null) {
                        tmpFollowPos = currentNode.getRightChild().getFirstPos();
                        tmpFollowPos.addAll(followposTable.get(currentPos));
                        followposTable.put(currentPos, tmpFollowPos);

                        if (currentNode.getRightChild().getFirstPos().contains(last)) {
                            stop = true;
                        }

                    } else {
                        tmpFollowPos = currentNode.getRightChild().getFirstPos();
                        followposTable.put(currentPos, tmpFollowPos);

                        if (currentNode.getRightChild().getFirstPos().contains(last)) {
                            stop = true;
                        }
                    }
                }
            }

            if (stop) { i = nodeList.size(); } // stop computing followpos if reached final pos

        }

        // remove duplicates
        for (int i = 0; i < followposTable.size(); i++) {
            Set<Integer> noDups = new HashSet<>();
            if (followposTable.get(i) != null) {
                noDups.addAll(followposTable.get(i));
                followposTable.put(i, new LinkedList<Integer>(noDups));
            }
        }

    }

    private void fillList() {
        // traverse the RE
        int pos = 1;
        for (int i = 0; i < this.postfixRegexp.length(); i++) {
            String currSymbol = Character.toString(this.postfixRegexp.charAt(i));
            // if symbol is in alphabet or is #
            if ((!currSymbol.equals("*")) && (!currSymbol.equals(".")) && (!currSymbol.equals("|"))) {

                Node tmpNode = new Node(currSymbol);
                tmpNode.setIsPosition(true); // Symbol has position
                tmpNode.setPosition(pos); // set Position
                List<Integer> flPos = new LinkedList<>();
                flPos.add(pos);
                tmpNode.setFirstPos(flPos); // add firstPos
                tmpNode.setLastPos(flPos); // add LastPos
                tmpNode.setNullable(false); // nullable is false for pos

                if (currSymbol.equals("#")) { tmpNode.setLast(true); }
                pos++; // add 1 to pos
                // add to list and stack
                nodeList.add(tmpNode);
                nodeStack.push(tmpNode);
                posCount = pos;
            } else if (currSymbol.equals("*")) { // if symbol is kleene star
                if (!nodeStack.isEmpty()) {
                    Node childNode = nodeStack.pop(); // child node is previous node
                    Node tmpNode = new Node(currSymbol, childNode);
                    tmpNode.setNullable(true); // kleene is always nullable
                    // firstpos and lastpos for kleene node are the same as child
                    tmpNode.setFirstPos(childNode.getFirstPos());
                    tmpNode.setLastPos(childNode.getLastPos());
                    // add to list and stack
                    nodeList.add(tmpNode);
                    nodeStack.push(tmpNode);
                }
            } else if (currSymbol.equals(".")) { // if symbol is concat
                if (!nodeStack.isEmpty()) {
                    Node rightChild = nodeStack.pop();
                    Node leftChild = nodeStack.pop();
                    Node tmpNode = new Node(currSymbol, leftChild, rightChild);

                    // set nullable
                    boolean tmpNullable = (rightChild.getNullable()) && (leftChild.getNullable());
                    tmpNode.setNullable(tmpNullable);

                    // set firstpos
                    List<Integer> tmpFirstPos = new LinkedList<>();
                    if (leftChild.getNullable()) {
                        tmpFirstPos.addAll(leftChild.getFirstPos());
                        tmpFirstPos.addAll(rightChild.getFirstPos());
                    } else {
                        tmpFirstPos.addAll(leftChild.getFirstPos());
                    }
                    tmpNode.setFirstPos(tmpFirstPos);

                    // set lastpos
                    List<Integer> tmpLastPos = new LinkedList<>();
                    if (rightChild.getNullable()) {
                        tmpLastPos.addAll(leftChild.getLastPos());
                        tmpLastPos.addAll(rightChild.getLastPos());
                    } else {
                        tmpLastPos.addAll(rightChild.getLastPos());
                    }
                    tmpNode.setLastPos(tmpLastPos);

                    nodeList.add(tmpNode);
                    nodeStack.push(tmpNode);
                }
            } else if (currSymbol.equals("|")) {
                if (!nodeStack.isEmpty()) {
                    Node rightChild = nodeStack.pop();
                    Node leftChild = nodeStack.pop();
                    Node tmpNode = new Node(currSymbol, leftChild, rightChild);

                    // set nullable
                    boolean tmpNullable = (rightChild.getNullable()) || (leftChild.getNullable());
                    tmpNode.setNullable(tmpNullable);

                    // set firstpos
                    List<Integer> tmpFirstPos = new LinkedList<>();
                    tmpFirstPos.addAll(leftChild.getFirstPos());
                    tmpFirstPos.addAll(rightChild.getFirstPos());
                    tmpNode.setFirstPos(tmpFirstPos);

                    // set lastpos
                    List<Integer> tmpLastPos = new LinkedList<>();
                    tmpLastPos.addAll(leftChild.getLastPos());
                    tmpLastPos.addAll(rightChild.getLastPos());
                    tmpNode.setLastPos(tmpLastPos);

                    nodeList.add(tmpNode);
                    nodeStack.push(tmpNode);
                }
            }
        }
    }

}
