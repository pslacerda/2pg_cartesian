package conflicts;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import conflicts.sets.ObjectiveSet;
import conflicts.sets.SetOfObjectiveSets;

public class DeltaMOSSExactAlgorithm {

	Population pop;			// set of solutions
	double[][] obj_vector;	// array of the individual's objective vectors
	int os_dim;				// number of objectives

	
	public DeltaMOSSExactAlgorithm(Population pop, int os_dim, Relation[] rels) {
		this.pop = pop;
		this.os_dim = os_dim;
		LinkedList<Individual> ll = pop.getPopulation();
		this.obj_vector = new double[ll.size()][os_dim];
		Iterator<Individual> iter = ll.iterator();
		int i=0;
		while (iter.hasNext()) {
			this.obj_vector[i] = (iter.next()).getObjectiveVector();
			i++;
		}
	}
	
	/**
	 * Performs the exact algorithm for the delta-MOSS problem, returning
	 * an objective subset with minimum number of objectives which 
	 * adhere to the given delta error.
	 */
	public ObjectiveSet performExactAlgorithmGivenDelta(double delta) {
		return performExactAlgorithm(1, delta);
	}
	
	/**
	 * Performs the exact algorithm for the k-EMOSS problem, returning
	 * an objective subset with size k and minimal delta error.
	 */
	public ObjectiveSet performExactAlgorithmGivenK(int k) {
		return performExactAlgorithm(2, k);
	}
	
	/**
	 * Performs the exact algorithm for the MOSS problem, returning an objective
	 * subset with minimum size, non-conflicting w.r.t. the whole set of objectives.
	 * This algorithm corresponds to an error of delta=0. 
	 */
	public ObjectiveSet performExactAlgorithm() {
		return performExactAlgorithm(1, 0);
	}
	
	/**
	 * Performs the exact algorithm both for
	 * a) the case of given delta value (type = 1, value = delta) and
	 * b) the case of given k (type = 2, value = k).
	 * 
	 * In case a) this method returns a smallest objective subset the
	 * delta failure of which is less or equal delta.
	 * In case b) this method returns an objective subset of size k the
	 * delta value of which is minimal.
	 */
	public ObjectiveSet performExactAlgorithm(int type, double value) {
		SetOfObjectiveSets minimalObjectiveSets = getAllMinimalSets(type, value);
		if (type == 1) {
			return minimalObjectiveSets.getSmallestObjectiveSetWithFailureLessOrEqualTo(value);
		} else if (type == 2) {
			return minimalObjectiveSets.getBestObjectiveSetOfSizeLessOrEqual((int)value);
		} else return null;
	}
	
	/**
	 * Performs the exact algorithm of bz2006d, i.e., constructing all
	 * delta-minimal sets with
	 *      a) an error of at most delta or
	 *      b) at most k objectives
	 * 
	 * @return all minimal objective sets, according to type and value:
	 * 		a) (type = 1, value = delta): all sets with error at most delta
	 * 		b) (type = 2, value = k): all sets with size of at most k
	 */
	public SetOfObjectiveSets getAllMinimalSets(int type, double value) {
		SetOfObjectiveSets minimalSets = new SetOfObjectiveSets();
		SetOfObjectiveSets currentSet;

		if (this.pop.mu < 2) {
			return null;
		}
		for (int i=0; i<this.pop.mu-1;i++) {
			for (int j=i+1;j<this.pop.mu;j++) {
				/* Consider now the pair (i,j) of individuals: */
				currentSet = this.computeSetOfSetsFor(i,j, type, value);
				if (currentSet != null) {
					union_ExactAlgo(minimalSets, currentSet, type, value);
				}
			}
		}
		return minimalSets;
	}
	
	
	/**
     * @param p
     * @param q
     * @return the set of all possible minimal sets of objectives the size of which is 1 and 2,
     * together with their delta value for the pair of individuals p and q. 
     * 
     * Redundant sets (a set is redundant if there is at least one set of less or equal size
     * with less or equal delta value) are omitted to reduce the computation time.
     * 
     * If type = 1 then all objective sets are additionally omitted the delta values of which
     * are greater than value.
     * If type = 2 then all objective sets are additionally omitted the sizes of which are
     * larger than value.
     */
    private SetOfObjectiveSets computeSetOfSetsFor(int p, int q, int type, double value) {
    	SetOfObjectiveSets soos = new SetOfObjectiveSets();
    	int[] objectives;
    	for (int kone=0; kone<os_dim; kone++) { // kone = first objective
    		for (int ktwo=kone; ktwo<os_dim; ktwo++) { //ktwo = second objective
    			if (kone==ktwo) {
    				objectives = new int[1];
    				objectives[0] = kone;
    			}else {
    				objectives = new int[2];
    				objectives[0] = kone;
    				objectives[1] = ktwo;
    			}
    			double delta = computeDeltaFor(p, q, objectives);
    			ObjectiveSet os = new ObjectiveSet(objectives, os_dim, delta);
    			if (type==1) {
    				if (os.getDelta() <= value) {
    					soos.add(os);
    				}
    			} else if (type==2) {
    				if (os.size() <= value) {
    					soos.add(os);
    				}
    			}
    		}
    	}
    	return soos;
    }
    
    private double computeDeltaFor(int p, int q, int[] objectives) {
    	double delta = 0;
    	// when considering only one objective, the case p||q is not possible!
    	if (objectives.length == 1) {
    		/* delta = max\limits_{j} (p_j - q_j) if p_i <= q_i */
    		if (obj_vector[p][objectives[0]] <= obj_vector[q][objectives[0]]) {
    			for (int j=0; j<os_dim; j++) {
    				delta = max(delta, obj_vector[p][j]- obj_vector[q][j]);
    			}
    		}
    		/* delta = max\limits_{j} (q_j - p_j) if q_i <= p_i */
    		if (obj_vector[q][objectives[0]] <= obj_vector[p][objectives[0]]) {
    			for (int j=0; j<os_dim; j++) {
    				delta = max(delta, obj_vector[q][j]- obj_vector[p][j]);
    			}
    		}
    	}
    	// consider now the case of two objectives in objectives[]:
    	if (objectives.length == 2) {
    		/* when p || q, the delta value is always 0, 
    		 * i.e., we only have to consider the cases p~q and p\preceq q*/
    		if (obj_vector[p][objectives[0]] <= obj_vector[q][objectives[0]] &&
    						obj_vector[p][objectives[1]] <= obj_vector[q][objectives[1]]) {
    			for (int j=0; j<os_dim; j++) {
    				delta = max(delta, obj_vector[p][j]- obj_vector[q][j]);
    			}
    		}
    		if (obj_vector[q][objectives[0]] <= obj_vector[p][objectives[0]] &&
							obj_vector[q][objectives[1]] <= obj_vector[p][objectives[1]]) {
    			for (int j=0; j<os_dim; j++) {
    				delta = max(delta, obj_vector[q][j]- obj_vector[p][j]);
    			}
    		}
    	}
    	return delta;
    }
	
	
	/**
	 * This method is used during the exact algorithm for computing a minimal non-redundant
	 * set of objectives, delta-nonconflicting with the whole set of all objectives.
	 * 
	 * The SetOfObjectiveSets unionSet is updated during this method with the information from currentSet.
	 * 
	 * post: The new list of possible sets in unionSet.elements is the list we get if we union each
	 *       set A of unionSet.elements with each set B in currentSet the delta value of which is
	 *       the maximum of the delta values of A and B.
	 *       After this computation all redundant sets are deleted from unionSet.elements, where
	 *       a set S is redundant and we omit S iff there is a subset S* in unionSet.elements
	 *       the delta value of which is smaller or equal to S's delta value.   
	 * 
	 * @param currentSet
	 */
	public static void union_ExactAlgo(SetOfObjectiveSets unionSet, SetOfObjectiveSets currentSet, int type, double value) {
		Vector<ObjectiveSet> newList = new Vector<ObjectiveSet>();
		int sizeunionset = unionSet.getElements().size();
		Vector<ObjectiveSet> elementsInUnionSet = unionSet.getElements();
		Vector<ObjectiveSet> elementsInCurrentSet = currentSet.getElements();
		if (unionSet.isEmpty()) {
			unionSet.setElements(elementsInCurrentSet);
		} else {
			// TODO Could be optimized by deciding which set of sets is iter1 and which is iter2
			for (int i=0; i<sizeunionset; i++) {
				int sizecurrentset = elementsInCurrentSet.size();
				ObjectiveSet set1 = (ObjectiveSet)(elementsInUnionSet.get(i));
				for (int j=0; j<sizecurrentset; j++) {
					ObjectiveSet set2 = (ObjectiveSet)(elementsInCurrentSet.get(j));
					ObjectiveSet unionset = union(set1,set2);
					/* Add union to S only if the union matches the constraints:
					 *  a) union.delta <= given delta or
					 *  b) union.size <= given k 
					 */
					if (type == 1) {
						if (unionset.getDelta() <= value) {
							addIfNotSupSet(unionset, newList);
						}
					} else if (type == 2) {
						if (unionset.size() <= value) {
							addIfNotSupSet(unionset, newList);
						}
					} else {
						addIfNotSupSet(unionset, newList);
					}
					
				}
			}
			unionSet.setElements(newList);
		}
	}
	

	/**
	 * if there is no subset of is with less or equal delta value in list:
	 * 		--> add is into list
	 *
	 * else if there is a subset of is in list the delta value of which is greater than
	 * 		is's delta value:
	 * 		--> add is into list
	 *
	 *
	 * always:
	 * --> remove all sets from list which are supersets of is and have a larger delta value
	 *  
	 * @param is IntSet
	 * @param list a vector which contains only IntSets 
	 */
	private static void addIfNotSupSet(ObjectiveSet os, Vector<ObjectiveSet> list) {
		/* insert = true iff is has to be inserted into list: */
		boolean insert = true;
		/* a list of elements, we have to delete in list: */
		Vector<ObjectiveSet> delete = new Vector<ObjectiveSet>();
		for (ObjectiveSet currentIs : list) {
			if (currentIs.isSuperSetOf(os)) {
				delete.add(currentIs);
				insert = true;
			} else {
				if (os.isSuperSetOf(currentIs)) {
					insert = false;
				}
			}
		}
		/* delete elements in list */
		list.removeAll(delete);
		if (insert) {
			list.add(os);
		}
	}
	
	private static ObjectiveSet union(ObjectiveSet s1, ObjectiveSet s2) {
		double maxdelta = max(s1.getDelta(), s2.getDelta());
		ObjectiveSet union = new ObjectiveSet(s1.getElements(), maxdelta); 
		union.addAll(s2);
		return union;
	}
	
	public static double max(double a, double b){		
		if (a > b) {
			return a;
		} else {
			return b;
		}
	}

}
