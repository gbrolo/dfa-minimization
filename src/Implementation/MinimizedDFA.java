package Implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gabriel Brolo on 04/08/2017.
 */
public class MinimizedDFA {
    private DFA dfa;
    private List<List<List<State>>> partitionList;
    private HashMap<List<List<State>>, HashMap<String, List<List<State>>>> minimizedDFATable;
    private HashMap<List<List<State>>, Integer> partitionIDs;

    private List<Integer> finalStates;
    private List<Integer> initialStates;

    private boolean equivalent;

    public MinimizedDFA (DFA dfa) {
        this.dfa = dfa;
        partitionList = new LinkedList<>();
        minimizedDFATable = new HashMap<>();
        partitionIDs = new HashMap<>();
        finalStates = new LinkedList<>();
        initialStates = new LinkedList<>();
        setInitialPartitionList();
        this.partitionList = setPartitionList(this.partitionList);
        setMinimizedDFATable();
        //setFinalAndInitialStates();
    }

    // arreglar para que reciba estados finales del NFA para que funcione
    public void setFinalAndInitialStates() {
        System.out.println(partitionList.toString());
        for (int i = 0; i < partitionList.size(); i++) {
            List<List<State>> currPartition = partitionList.get(i);
            int currPartitionID = partitionIDs.get(currPartition);
            for (int j = 0; j < currPartition.size(); j++) {
                List<State> currStateList = currPartition.get(j);
                for (int m = 0; m < currStateList.size(); m++) {
                    State currState = currStateList.get(m);
                    if (this.dfa.getFinalStates().contains(currState.getStateId())) {
                        finalStates.add(currPartitionID);
                    } else if (this.dfa.getInitialStates().contains(currState.getStateId())) {
                        initialStates.add(currPartitionID);
                    }
                }
            }
        }
    }

    private void setMinimizedDFATable() {
        HashMap<List<State>, HashMap<String, List<State>>> dfaTable = dfa.getDfaTable(); // DFA's transition table
        HashMap<String, List<List<State>>> tmpCol = new HashMap<>();

        for (int i = 0; i < this.partitionList.size(); i++) {
            List<List<State>> currentSet = this.partitionList.get(i);
            List<State> firstStateSet = currentSet.get(0);

            for (int j = 0; j < this.dfa.getSymbolList().size(); j++) {
                String currSymbol = Character.toString(this.dfa.getSymbolList().get(j));
                List<State> currStateList = dfaTable.get(firstStateSet).get(currSymbol);
                for (int k = 0; k < this.partitionList.size(); k++) {
                    List<List<State>> currentInternalSet = this.partitionList.get(k);
                    if (currentInternalSet.contains(currStateList)) {
                        tmpCol.put(currSymbol, currentInternalSet);
                    }
                }
            }
            partitionIDs.put(currentSet, i);
            minimizedDFATable.put(currentSet, tmpCol);
            tmpCol = new HashMap<>();

        }
        System.out.println(minimizedDFATable.toString());
        System.out.println(partitionIDs.toString());
    }

    private void setInitialPartitionList() {
        List<List<State>> dfaStates = this.dfa.getDfaStates();
        List<Integer> finalStates = this.dfa.getFinalStates();
        HashMap<List<State>, Integer> dfaStatesWithNumbering = this.dfa.getDfaStatesWithNumbering();
        List<List<State>> SMinusF = new LinkedList<>(); // S\F
        List<List<State>> F = new LinkedList<>(); //F

        for (int i = 0; i < dfaStates.size(); i++) { // iterate over DFA state list
            List<State> currentStateList = dfaStates.get(i);
            int stateListID = dfaStatesWithNumbering.get(currentStateList);

            if (finalStates.contains(stateListID)) {
                F.add(currentStateList);
            } else {
                SMinusF.add(currentStateList);
            }
        }
        this.partitionList.add(SMinusF);
        this.partitionList.add(F);
    }

    private List<List<List<State>>> setPartitionList (List<List<List<State>>> partitions) {
        boolean flag = false;
        List<List<List<State>>> tmpPartitions = partitions;

        while (!flag) {
            List<List<List<State>>> subPartitions = new LinkedList<>();
            for (int k = 0; k < tmpPartitions.size(); k++) {
                List<List<State>> partition = tmpPartitions.get(k);
                List<State> representant = partition.get(0);

                for (int i = 0; i < partition.size(); i++) {
                    List<State> state = partition.get(i);
                    if (state != representant) {
                        boolean b = verifyEquivalence(representant, state, tmpPartitions);

                        if (b) {
                            if (subPartitions.size() == 0) {
                                List<List<State>> newPartition = new LinkedList<>();
                                newPartition.add(representant);
                                newPartition.add(state);
                                if (!subPartitions.contains(newPartition)) {
                                    subPartitions.add(newPartition);
                                }
                            } else {
                                for (int t = 0; t < subPartitions.size(); t++) {
                                    List<List<State>> subPartition = subPartitions.get(t);
                                    if (subPartition.contains(representant)) {
                                        subPartition.add(state);
                                    } else {
                                        List<List<State>> newPartition = new LinkedList<>();
                                        newPartition.add(representant);
                                        newPartition.add(state);
                                        if (!subPartitions.contains(newPartition)) {
                                            subPartitions.add(newPartition);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (subPartitions.size() == 0) {
                                List<List<State>> newPartition = new LinkedList<>();
                                newPartition.add(representant);
                                subPartitions.add(newPartition);
                                List<List<State>> newPartition2 = new LinkedList<>();
                                newPartition2.add(state);
                                subPartitions.add(newPartition2);
                            } else {
                                for (int r = 0; r < subPartitions.size(); r++) {
                                    List<List<State>> partitionNew = subPartitions.get(r);

                                    if (!partitionNew.contains(representant)) {
                                        List<State> newRepresentant = partitionNew.get(0);
                                        boolean newb = verifyEquivalence(newRepresentant, state, tmpPartitions);

                                        if (newb) {
                                            partitionNew.add(state);
                                        } else if (r == (subPartitions.size()-1)) {
                                            List<List<State>> newPartition = new LinkedList<>();
                                            newPartition.add(state);
                                            if (!subPartitions.contains(newPartition)) {
                                                subPartitions.add(newPartition);
                                            }
                                        }

                                    }

                                }
                            }
                        }

                    }

                }

            }

            if (tmpPartitions.size() == subPartitions.size()) {
                flag = true;
            } else if(subPartitions.size() < tmpPartitions.size()) {
                flag = true;
            } else {
                tmpPartitions = subPartitions;
            }

        }

        return tmpPartitions;
    }

    private boolean verifyEquivalence(List<State> representant, List<State> state, List<List<List<State>>> partitions) {
        int symbolSize = this.dfa.getSymbolList().size();
        int symbolCont = 0;

        for (int i = 0; i < symbolSize; i++) {
            List<State> representantWithSymbol = this.dfa.getDfaTable().get(representant).get(
                    Character.toString(this.dfa.getSymbolList().get(i))
            );
            List<State> stateWithSymbol = this.dfa.getDfaTable().get(state).get(
                    Character.toString(this.dfa.getSymbolList().get(i))
            );

            if (representantWithSymbol.size()==0 && stateWithSymbol.size()==0) {
                symbolCont++;
            }

            for (int j = 0; j < partitions.size(); j++) {
                List<List<State>> partition = partitions.get(j);
                if (partition.contains(representantWithSymbol) &&
                        partition.contains(stateWithSymbol)) {
                    symbolCont++;
                    break;
                }
            }
        }
        boolean equivalent = false;
        if (symbolCont == symbolSize) {
            equivalent = true;
        }

        return equivalent;
    }

    public List<List<List<State>>> getPartitionList () { return this.partitionList; }
    public HashMap<List<List<State>>, HashMap<String, List<List<State>>>> getMinimizedDFATable () { return this.minimizedDFATable; }
    public HashMap<List<List<State>>, Integer> getPartitionIDs () { return this.partitionIDs; }
    public List<Integer> getFinalStates () { return this.finalStates; }
    public List<Integer> getInitialStates () { return this.initialStates; }
}
