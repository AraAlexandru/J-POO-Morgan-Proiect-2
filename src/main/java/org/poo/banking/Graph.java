package org.poo.banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Collections;

public class Graph<T> {

    public class Edge {
        private final T source;
        private final T destination;
        private final double cost;

        /**
         * Clasa Edge reprezinta o muchie intre doua noduri din graf.
         */
        public Edge(final T source, final T destination, final double cost) {
            this.source = source;
            this.destination = destination;
            this.cost = cost;
        }

        /**
         * Returneaza nodul sursa al muchiei.
         * @return Nodul sursa.
         */
        public T getSource() {
            return source;
        }

        /**
         * Returneaza nodul destinatie al muchiei.
         * @return Nodul destinatie.
         */
        public T getDestination() {
            return destination;
        }

        /**
         * Returneaza costul muchiei.
         * @return Costul muchiei sub forma de double.
         */
        public double getCost() {
            return cost;
        }
    }


    private final Map<T, List<Edge>> adjLists = new HashMap<>();

    /**
     * Adauga o muchie intre doua noduri in graful orientat.
     * @param source Nodul sursa.
     * @param destination Nodul destinatie.
     * @param cost Costul muchiei.
     */
    public void addEdge(final T source, final T destination, final double cost) {
        adjLists.putIfAbsent(source, new ArrayList<>());
        adjLists.get(source).add(new Edge(source, destination, cost));
    }

    /**
     * Gaseste un drum de la un nod de start la un nod de
     * sfarsit folosind parcurgerea in latime (BFS).
     * @param start Nodul de start.
     * @param end Nodul de sfarsit.
     * @return O lista de muchii reprezentand drumul gasit sau null daca drumul nu exista.
     */
    public ArrayList<Edge> getPath(final T start, final T end) {
        Queue<T> queue = new LinkedList<>();
        Map<T, Edge> parent = new HashMap<>();
        Set<T> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            T current = queue.poll();

            if (!adjLists.containsKey((current))) {
                continue;
            }

            for (Edge edge : adjLists.get(current)) {
                T neighbor = edge.getDestination();

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, edge);
                    queue.add(neighbor);

                    if (neighbor.equals(end)) {
                        return reconstructPath(start, end, parent);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Reconstruieste drumul de la nodul de start la nodul de sfarsit folosind vectorul de parinti.
     * @param start Nodul de start.
     * @param end Nodul de sfarsit.
     * @param parent O harta care stocheaza parintii fiecarui nod.
     * @return O lista de muchii reprezentand drumul reconstruit.
     */
    private ArrayList<Edge> reconstructPath(final T start,
                                             final T end, final Map<T, Edge> parent) {
        ArrayList<Edge> path = new ArrayList<>();
        T current = end;

        while (!current.equals(start)) {
            Edge edge = parent.get(current);
            path.add(edge);
            current = edge.getSource();
        }

        Collections.reverse(path);
        return path;
    }
}
