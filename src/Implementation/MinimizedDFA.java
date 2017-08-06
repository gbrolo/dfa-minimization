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

    private List<List<List<State>>> setPartitionList (List<List<List<State>>> partitionList) {
        HashMap<List<State>, HashMap<String, List<State>>> dfaTable = dfa.getDfaTable(); // DFA's transition table
        equivalent = false;
        List<List<List<State>>> tmpPartitionListGlobal = new LinkedList<>();

        for (int i = 0; i < partitionList.size(); i++) {
            List<List<State>> currentSet = partitionList.get(i); // the current Set
            List<State> equivalenceSet = currentSet.get(0); // first subset of set to establish equivalences

            List<List<List<State>>> tmpPartitionList = new LinkedList<>();
            List<List<State>> eqPartition = new LinkedList<>();
            eqPartition.add(equivalenceSet);
            tmpPartitionList.add(eqPartition);

            // iterate over symbolList
            for (int k = 1; k < currentSet.size(); k++) {
                List<State> setToCompare = currentSet.get(k);
                int symbolCont = 0;

                for (int j = 0; j < this.dfa.getSymbolList().size(); j++){
                    String currSymbol = Character.toString(this.dfa.getSymbolList().get(j));

                    List<State> eqSetWithSymbol = dfaTable.get(equivalenceSet).get(currSymbol);
                    List<State> setToCompareWithSymbol = dfaTable.get(setToCompare).get(currSymbol);

                    for (int m = 0; m < partitionList.size(); m++) {
                        if ((partitionList.get(m).contains(eqSetWithSymbol)) &&
                                partitionList.get(m).contains((setToCompareWithSymbol))) {
                            //equivalent = true;
                            symbolCont++;
                            m = partitionList.size();
                        }
                    }
                }

                if (symbolCont == this.dfa.getSymbolList().size()) {
                    equivalent = true;
                }

                if (equivalent) {
                    for (int p = 0; p < tmpPartitionList.size(); p++) {
                        if (tmpPartitionList.get(p).contains(equivalenceSet)) {
                            tmpPartitionList.get(p).add(setToCompare);
                        }
                    }
                } else {
                    if (tmpPartitionList.size() == 1) {
                        List<List<State>> newPartition = new LinkedList<>();
                        newPartition.add(setToCompare);
                        tmpPartitionList.add(newPartition);
                    } else {
                        tmpPartitionList = setPartitionList(tmpPartitionList);
                    }
                }
                tmpPartitionListGlobal.addAll(tmpPartitionList);
            }
        }

        if (partitionList.size() != tmpPartitionListGlobal.size()) {
            setPartitionList(tmpPartitionListGlobal);
        }

        //this.partitionList = partitionList;
        //System.out.println(this.partitionList.toString());

        return partitionList;
    }

    public List<List<List<State>>> getPartitionList () { return this.partitionList; }
    public HashMap<List<List<State>>, HashMap<String, List<List<State>>>> getMinimizedDFATable () { return this.minimizedDFATable; }
    public HashMap<List<List<State>>, Integer> getPartitionIDs () { return this.partitionIDs; }
    public List<Integer> getFinalStates () { return this.finalStates; }
    public List<Integer> getInitialStates () { return this.initialStates; }
}
