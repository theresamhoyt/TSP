package student;
/* 
 * This Student class is meant to contain your algorithm.
 * You should implement the static method:
 *   solveTSP - Finds a tour that visits all the vertices and then returns to the starting
 *   vertex.  It does not have to be optimal, but shorter is better.
 *   It should return the length of the tour as the return value,
 *   and it should mark the edges that are used by your tour and the vertices
 *   that are visited.
 *
 * The input is a Graph object, which has:
 *   an ArrayList of all vertices - use graph.vertexIterator()
 *   an ArrayList of all edges - use graph.edgeIterator()
 *   each vertex has an ArrayList of its edges - use vertex.edgeIterator()
 *   
 *   see the documentation for: Graph, Vertex, and Edge for more details
 *   
 * You can animate your algorithm by periodically calling TSPtester.show(g).
 * 
 * If in your algorithm you want to save a copy of the graph, for instance 
 * the best solution found so far, you can clone it with.
 * 		static Graph bestSolution;   // a global class variable
 * 		...
 * 		bestSolution = g.clone();    // presumably called from a searching method 
 * 
 * To copy the best solution back into the original graph use:
 * 		g.copyMarks(bestSolution);
 */
import java.util.*;

import graph.*;
import tsp.TSPtester;

public class TSP
{
	private static Graph graph;
	private static Vertex root;
	private static int bound = 1;

	public static int solveTSP(Graph g) 
	{	
		// sort each vertex's edges shortest first
		g.sortVertexEdgeLists(new Graph.CompareEdgesSmallestFirst());
		
		int result = 0;
		while((result = solveTSPimp(g)) == 0){
			bound++;
		}
		return result;
	}
	private static boolean complete(VertexAndPath vp){
		if(vp.vertexCount == graph.numVertices() && graph.findEdge(vp.vertex, root) != null){
			return true;
		}
		return false;
	}
	private static int markPathAndComputeLength(VertexAndPath vp){
		graph.clearMarks();
		Edge lastEdge = graph.findEdge(vp.vertex, root);
		lastEdge.setMark(1);
		
		int length = lastEdge.getWeight();
		VertexAndPath curr = vp;
		while(curr.parent != null){
			Edge currEdge = graph.findEdge(curr.vertex, curr.parent.vertex);
			length = length + currEdge.getWeight();
			currEdge.setMark(1);
			
			Vertex one = currEdge.getV1();
			Vertex two = currEdge.getV2();
			one.setMark(1);
			two.setMark(1);
			curr = curr.parent;
		}
		return length;
	}

	
	public static int solveTSPimp(Graph g){
		graph = g;
				
		// just start at first vertex
		Vertex v = g.vertexIterator().next();
		root = v;
		g.clear();  // clear all marks and values
		
		ArrayDeque<VertexAndPath> vertexQ = new ArrayDeque<VertexAndPath>();	
		VertexAndPath vp = new VertexAndPath();
		
		vp.vertex = v;
		vertexQ.add(vp);

		do {  
			
			vp = vertexQ.remove();
			
			if(complete(vp)){
				return markPathAndComputeLength(vp);
			
			}
			Iterator<Edge> vItr = vp.vertex.edgeIterator();
			int currCount = 0;
			while(vItr.hasNext() && currCount < bound){
				Edge e = vItr.next();
				if(!vp.isVisited(e.getOppositeVertexOf(vp.vertex))){
					VertexAndPath newVP = new VertexAndPath();
					newVP.vertex = e.getOppositeVertexOf(vp.vertex);
					newVP.parent = vp;
					newVP.vertexCount = vp.vertexCount + 1;
					vertexQ.add(newVP);
				}
				currCount++;
			}
		} while(!vertexQ.isEmpty());
		
		return 0;	

	}

}
class VertexAndPath{
	public Vertex vertex;
	public VertexAndPath parent;
	public int vertexCount = 1;

	
	public boolean isVisited(Vertex v){

		if(v.getId() == vertex.getId()){
			return true;
		}
		if(parent == null){
			return false;
		}
		VertexAndPath currVertex = parent;
		while(currVertex != null){
			if(currVertex.vertex.getId() == v.getId()){
				return true;
			}
			currVertex = currVertex.parent;
		}
		return false;
	}
}


