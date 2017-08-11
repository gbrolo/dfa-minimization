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
    private int stateIDCount;
    private List<String> symbolList;
    private List<Integer> finalStates;
    private List<Integer> initialState;
    private HashMap<Integer, List<Integer>> followposTable;
    private HashMap<Integer, String> stateSymbol;
    private HashMap<Integer, List<Integer>> stateMap; // DFA states
    private HashMap<List<Integer>, Integer> stateMapBck; // DFA states backwards notation
    private HashMap<Integer, HashMap<String, Integer>> transitionTable;

    public REtoDFA(String postfixRegexp) {
        this.postfixRegexp = postfixRegexp;
        this.nodeList = new LinkedList<>();
        this.nodeStack = new Stack<>();
        this.symbolList = new LinkedList<>();
        this.finalStates = new LinkedList<>();
        this.initialState = new LinkedList<>();
        this.followposTable = new HashMap<>();
        this.stateSymbol = new HashMap<>();
        this.stateMap = new HashMap<>();
        this.stateMapBck = new HashMap<>();
        this.transitionTable = new HashMap<>();
        stateIDCount = 0;
        // extend the regexp
        extendRE();
        // find all nullable, firstpos and lastpos and fill the nodeList
        fillList();
        // compute followpos
        findFollowPos();
        // build the minimized DFA
        buildDFA();
    }

    private void extendRE() {
        this.postfixRegexp = this.postfixRegexp + "#.";
    }

    private void buildDFA() {
        // compute stateMap
        for (int i = 0; i < followposTable.size(); i++) {
            if (!followposTable.isEmpty()) {
                if (followposTable.get(i) != null) {
                    if (!stateMap.containsValue(followposTable.get(i))){
                        stateMap.put(stateIDCount, followposTable.get(i));
                        stateMapBck.put(followposTable.get(i), stateIDCount);
                        stateIDCount++;
                    }
                }
            }
        }

        // traverse stateMap to check if all pos are in there
        for (int j = 1; j < posCount; j++) {
            boolean flag = false;
            for (int i = 0; i < stateMap.size(); i++) {
                if ((stateMap.get(i) != null) && stateMap.get(i).contains(j)) {
                    flag = true;
                }
            }

            if (!flag) {
                List<Integer> state = new LinkedList<>();
                state.add(j);
                stateMap.put(stateIDCount, state);
                stateMapBck.put(state, stateIDCount);
                stateIDCount++;
                break;
            }

        }

        // traverse stateMap to check for finalState & initialState
        for (int i = 0; i < stateMap.size(); i++) {
            if (stateMap.get(i).contains(last)) {
                finalStates.add(stateMapBck.get(stateMap.get(i)));
            }

            if (stateMap.get(i).contains(1)) {
                initialState.add(stateMapBck.get(stateMap.get(i)));
            }
        }

        for (int i = 0; i < stateMap.size(); i++) {
            List<Integer> currentState = stateMap.get(i);

            HashMap<String, Integer> tmpCol = new HashMap<>();
            for (int j = 0; j < currentState.size(); j++) {
                int currentPos = currentState.get(j);
                String currPosSymbol = stateSymbol.get(currentPos);


                // traverse the symbollist
                for (int k = 0; k < symbolList.size(); k++) {
                    if (currPosSymbol.equals(symbolList.get(k))) { // add followpos of pos(i)
                        List<Integer> posFollowPos = followposTable.get(currentPos);
                        int posFollowPosStateID = stateMapBck.get(posFollowPos);
                        tmpCol.put(symbolList.get(k), posFollowPosStateID);
                    }
                }

            }

//            if ((currentState.size() == 1) && (currentState.contains(last))) {
//                for (int k = 0; k < symbolList.size(); k++) {
//                    int index = stateMapBck.get(currentState);
//                    tmpCol.put(symbolList.get(k), index);
//                }
//            }

            transitionTable.put(stateMapBck.get(currentState), tmpCol);

        }

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

                stateSymbol.put(pos, currSymbol); // assign each position with its symbol, where symbol belongs to alphabet

                if (!symbolList.contains(currSymbol) && (!currSymbol.equals("#"))) {
                    symbolList.add(currSymbol); // add symbol to symbolList
                }

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

    public List<String> getSymbolList() { return this.symbolList; }
    public List<Integer> getFinalStates() { return this.finalStates; }
    public List<Integer> getInitialState() { return this.initialState; }
    public HashMap<Integer, String> getStateSymbol() { return this.stateSymbol; }
    public HashMap<Integer, List<Integer>> getStateMap() { return this.stateMap; }
    public HashMap<Integer, HashMap<String, Integer>> getTransitionTable() { return this.transitionTable; }

}
