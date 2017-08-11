package Implementation;

import java.util.*;

/**
 * Created by Gabriel Brolo on 04/08/2017.
 */
public class MinimizedDFA {
    private DFA dfa;
    private List<List<State>> dfaStates;
    private List<Integer> dfaFinalStates;
    private List<Integer> dfaInitialStates;
    private List<Transition> dfaTransitionList;
    private List<Character> dfaSymbolList;
    private HashMap<List<State>, Integer> dfaStatesWithNumbering;
    private List<List<Integer>> PiSet;
    private HashMap<List<Integer>, Integer> minimizedDFAStates;
    private HashMap<Integer, HashMap<String, Integer>> minimizedDFATable;
    private HashMap<List<List<State>>, Integer> partitionIDs;

    private List<Integer> finalStates;
    private List<Integer> initialStates;

    private boolean equivalent;

    public MinimizedDFA (DFA dfa) {
        this.dfa = dfa;

        dfaStates = this.dfa.getDfaStates();
        dfaInitialStates = this.dfa.getInitialStates();
        dfaFinalStates = this.dfa.getFinalStates();
        dfaStatesWithNumbering = this.dfa.getDfaStatesWithNumbering();
        dfaTransitionList = this.dfa.getTransitionsList();
        dfaSymbolList = this.dfa.getSymbolList();

        PiSet = new LinkedList<>();
        minimizedDFATable = new HashMap<>();
        minimizedDFAStates = new HashMap<>();
        partitionIDs = new HashMap<>();
        finalStates = new LinkedList<>();
        initialStates = new LinkedList<>();
        minimize();
        //this.PiSet = setPartitionList(this.PiSet);
        //setFinalAndInitialStates();
    }

    private void minimize() {

        List<Integer> SMinusF = new LinkedList<>(); // S\F
        List<Integer> F = new LinkedList<>(); //F

        for (List<State> currentState : dfaStates) {
            int stateID = dfaStatesWithNumbering.get(currentState);
            if (dfaFinalStates.contains(stateID)) { F.add(stateID); } else { SMinusF.add(stateID); }
        }

        // add initial partitions to PiSet
        this.PiSet.add(SMinusF);
        this.PiSet.add(F);
        System.out.println("PiSet: " + PiSet.toString());

        List<List<Integer>> newPi = newPartition(PiSet);

        while (!newPi.equals(PiSet)) {
            PiSet = newPi;
            newPi = newPartition(PiSet);
        }

        List<List<Integer>> finalPi = PiSet;
        PiSet = finalPi;
        System.out.println("PiSet after: " + PiSet.toString());

        int i = 0;
        for (List<Integer> state : PiSet) {
            minimizedDFAStates.put(state, i);
            i++;
        }
        System.out.println("minimizedDFAStates: " + minimizedDFAStates.toString());

        // set initial and final states
        for (List<Integer> set : PiSet) {
            for (int state : set) {
                if (dfaFinalStates.contains(state) &&
                        (!finalStates.contains(minimizedDFAStates.get(set)))) { finalStates.add(minimizedDFAStates.get(set)); }
                if (dfaInitialStates.contains(state)) { initialStates.add(minimizedDFAStates.get(set)); }
            }
        }

        for (List<Integer> state : PiSet) {
            int id = minimizedDFAStates.get(state);
            int representative = state.get(0);
            HashMap<String, Integer> tmpCol = new HashMap<>();
            for (char a : dfaSymbolList) {
                String A = Character.toString(a);
                for (Transition tr : dfaTransitionList) {
                    if ((tr.getInitialState().getStateId() == representative)
                            && (tr.getTransitionSymbol().equals(A))) {
                        int finalState = tr.getFinalState().getStateId();
                        for (List<Integer> S : PiSet) {
                            if (S.contains(finalState)) {
                                int FS = minimizedDFAStates.get(S);
                                tmpCol.put(A, FS);
                            }
                        }
                    }
                }
            }
            minimizedDFATable.put(id, tmpCol);
        }

        System.out.println("DFA table: " + minimizedDFATable);

    }

    private List<List<Integer>> newPartition(List<List<Integer>> partition) {
        List<List<Integer>> partitionNew = new LinkedList<>();
        for (List<Integer> S : partition) {
            for (int p : S) {
                List<Integer> tmpP = new LinkedList<>();
                for (int q : S) {
                    if (p != q) {
                        int equivCount = 0;
                        tmpP.add(p);
                        for (char a : dfaSymbolList) {
                            String A = Character.toString(a);
                            int pA = -1;
                            int qA = -1;

                            for (Transition tr : dfaTransitionList) {
                                if ((tr.getInitialState().getStateId() == p)
                                        && (tr.getTransitionSymbol().equals(A))) { pA = tr.getFinalState().getStateId(); }
                                if ((tr.getInitialState().getStateId() == q)
                                        && (tr.getTransitionSymbol().equals(A))) { qA = tr.getFinalState().getStateId(); }
                            }

                            // check if pA and qA belong to the same set
                            for (List<Integer> SA : partition) {
                                if ((SA.contains(pA)) && (SA.contains(qA))) {
                                    equivCount++;
                                } else if ((pA == -1) && (qA == -1)) { equivCount++; }
                            }
                        }
                        if (equivCount >= dfaSymbolList.size()) { tmpP.add(q); }
                    }
                }
                Set<Integer> set = new HashSet<>();
                set.addAll(tmpP);
                tmpP.clear();
                tmpP.addAll(set);
                if (!partitionNew.contains(tmpP) && (tmpP.size() > 0)) { partitionNew.add(tmpP); }
            }
            int count = 0;
            for (int tmp1 : S) {
                for (List<Integer> tmp : partitionNew) {
                    if (tmp.contains(tmp1)) { count++; }
                }
                if (count == 0) {
                    List<Integer> list = new LinkedList<>();
                    list.add(tmp1);
                    partitionNew.add(list);
                }
            }
        }

        return partitionNew;
    }
    
    public List<List<Integer>> getPiSet() { return this.PiSet; }
    public HashMap<Integer, HashMap<String, Integer>> getMinimizedDFATable () { return this.minimizedDFATable; }
    public HashMap<List<Integer>, Integer> getPartitionIDs () { return this.minimizedDFAStates; }
    public List<Integer> getFinalStates () { return this.finalStates; }
    public List<Integer> getInitialStates () { return this.initialStates; }
}
