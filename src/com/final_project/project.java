package com.final_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class project {

    private static final String graphInputFile = "graph_input.txt"; // graph data file path
    private static final String directDistanceFile = "direct_distance.txt"; // direct distance data file path

    private static Map<String, Integer> readDirectDistanceFile() throws FileNotFoundException {
        Map<String, Integer> directDistance = new HashMap<String, Integer>(); // Instantiate a directDistance hashMap
        Scanner fileInput = new Scanner(new File(directDistanceFile)); // read the direct distance file data
        while (fileInput.hasNext()) {
            String[] words = fileInput.nextLine().split("\\s+"); // split the words on each line by space
            directDistance.put(words[0], Integer.valueOf(words[1])); // set the node letter as the key and the integer as the value in the hashMap
        }
        fileInput.close(); // close the file
        return directDistance; // return the hashMap data

    }

    private static String[][] readGraphData() throws FileNotFoundException {
        String[][] arr = new String[26][]; // Instantiate a 2d array
        Scanner matrix = new Scanner(new File(graphInputFile)); // read the graph file data

        // Loop through the graph data and save it in the 2d array
        for (int i = 0; i < arr.length; i++) {
            if (matrix.hasNextLine()) {
                arr[i] = matrix.nextLine().split("\\s+");
            }
        }
        matrix.close(); // close the file
        return arr; // return the 2d array data
    }

    private static Map<String, Integer> setNodesPosition(String[][] graphData) {
        Map<String, Integer> position = new HashMap<String, Integer>(); // Instantiate a position hashMap
        // Loop through all the graph string keys and save the position of each key in the position hashMap data structure
        for (int i = 0; i < graphData[0].length; i++) {
            if (graphData[0][i].matches("[A-Z]")) {
                position.put(graphData[0][i], i);
            }
        }
        return position; // return the position data
    }

    private static String validateUserInput(Map<String, Integer> nodesPosition) {
        boolean error = false; // boolean variable
//        List<String> illegalValues = Arrays.asList("U", "V", "W", "X", "Y");
        String userInput; // variable that will hold user input
        do { // while the error variable is equal to true, keep prompting the user for input
            Scanner scanner = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter your starting node (A to Z):"); // prompt user to enter a node
            userInput = scanner.nextLine().toUpperCase().trim();  // Read user input
            if (userInput.length() != 1) { // check if user input length is not equal to 1
                error = true; // set error variable to true
                System.out.println("Only a single letter between A and Z are accepted. Try again."); // display error
            } else if (!userInput.matches("[A-Z]")) { // check if user input is not an alphabet
                error = true; // set error variable to true
                System.out.println("Only a single letter between A and Z are accepted. Try again."); // display error
            }else if (!nodesPosition.containsKey(userInput)){ // check if userInput exists in the nodes position hashmap
                error = true; // set error variable to true
                System.out.println("The node "+userInput+" is not found. Try again"); // display error
            } else {
                error = false; // set error variable to false
                scanner.close();
            }
        } while (error);

        return userInput; // return user input
    }

    private static void getNodesIndex(ArrayList<Integer> nodesIndex, String[][] graphData, int currentNodePosition) {
        for (int i = 0; i < graphData[currentNodePosition].length; i++) { // Loop through all values of the currentNode in the graph 2d array
            if (graphData[currentNodePosition][i].matches("[+-]?[0-9][0-9]*")) { // get integers only
                if (Integer.parseInt(graphData[currentNodePosition][i]) != 0) { // if the node is not 0
                    nodesIndex.add(i); // add the nodes position to nodeIndex ArrayList
                }
            }
        }

    }

    private static void getNodesKey(ArrayList<String> keys, ArrayList<Integer> nodesIndex, Map<String, Integer> nodesPosition) {
        for (int value : nodesIndex) { // Loop through all the nodes index values in the ArrayList
            // get the key of each node index
            for (Map.Entry<String, Integer> entry : nodesPosition.entrySet()) {
                if (entry.getValue() == value) { // if node index value equals the value in the nodesPosition hashMap
                    keys.add(entry.getKey()); // get the key and add it to the keys ArrayList
                }
            }
        }
    }

    private static String getMinDirectDistanceValue(Map<String, Integer> directDistances, ArrayList<String> keys, ArrayList<String> visitedNodes) {
        int minValue = directDistances.get(keys.get(0)); // set minValue to the first value in the directDistances hashMap
        String minKey = keys.get(0); // set minKey to the first element in the keys ArrayList

        for (String key : keys) { // Loop through all the keys
            if (!visitedNodes.contains(key)) { // if the current key is not found in the visitedNodes ArrayList
                if (minValue > directDistances.get(key)) { // if minValue is greater than the value of the key in the directDistances hashMap
                    minKey = key; // set minKey to the current key
                    minValue = directDistances.get(key); // set minValue to the current key value
                }
            }
        }
        return minKey; // return the minKey
    }

    private static String getMinEdgeAndDirectDistanceValue(Map<String, Integer> directDistances, String[][] graphData, ArrayList<String> keys, ArrayList<String> visitedNodes, String currentNode, Map<String, Integer> nodesPosition) {
        // set minValue to the sum of the first value in the 2d array and directDistances hashMap
        int minValue = Integer.parseInt(graphData[nodesPosition.get(currentNode)][nodesPosition.get(keys.get(0))]) + directDistances.get(keys.get(0));
        String minKey = keys.get(0); // set minKey to the first element in the keys ArrayList
        for (String key : keys) { // Loop through all the keys
            int edge = Integer.parseInt(graphData[nodesPosition.get(currentNode)][nodesPosition.get(key)]); // get the currentNode and the current key edge value from the graph
            if (!visitedNodes.contains(key)) { // if the current key does not exist in the visitedNodes ArrayList
                if (minValue > edge + directDistances.get(key)) { // if minValue greater than the sum of the edge and the directDistances key value
                    minKey = key; // set minKey to the current key
                    minValue = edge + directDistances.get(key); // set minValue to the sum of the edge and the directDistances key value
                }
            }
        }
        return minKey; // return minKey
    }

    private static void getNodesPath(String currentNode, ArrayList<String> visitedNodes, Map<String, Integer> nodesPosition, String[][] graphData, Map<String, Integer> directDistances, String type) {

        visitedNodes.add(currentNode); // add first node into the visitedNodes ArrayList

        while (!currentNode.equals("Z")) { // Loop until the currentNode equals Z
            int currentNodePosition = nodesPosition.get(currentNode); // get currentNode position

            ArrayList<Integer> nodesIndex = new ArrayList<>(); // Instantiate a nodIndex ArrayList
            getNodesIndex(nodesIndex, graphData, currentNodePosition); // get selected nodes index

            ArrayList<String> keys = new ArrayList<>(); // Instantiate a keys ArrayList
            getNodesKey(keys, nodesIndex, nodesPosition); // convert nodes index to keys

            if (type.equals("Algorithm1")) { // if the type parameter is equal to Algorithm 1
                currentNode = getMinDirectDistanceValue(directDistances, keys, visitedNodes); // get the node using Algorithm 1
            } else {
                currentNode = getMinEdgeAndDirectDistanceValue(directDistances, graphData, keys, visitedNodes, currentNode, nodesPosition); // get the node using Algorithm 2
            }

            visitedNodes.add(currentNode); // add currentNode to the visitedNode ArrayList
        }

    }

    private static void getShortestPath(ArrayList<String> visitedNodes, ArrayList<String> shortestPath) {
        for (String node : visitedNodes) { // Loop through all the visited nodes
            if (!shortestPath.contains(node)) { // if current node does not exists in the shortestPath ArrayList
                shortestPath.add(node); // add the current node to the shortestPath ArrayList
            } else {
                shortestPath.remove(shortestPath.indexOf(node) + 1); // this means that the node after the found node in the shortestPath is a dead end. remove it.
            }
        }
    }

    private static void displayDirectDistanceResults(ArrayList<String> shortestPath, Map<String, Integer> nodesPosition, ArrayList<String> visitedNodes, String[][] graphData) {
        int sum = 0; // instantiate an integer sum variable
        for (int i = 0; i < shortestPath.size() - 1; i++) { // Loop through all the nodes in the shortestPath ArrayList
            sum += Integer.parseInt(graphData[nodesPosition.get(shortestPath.get(i))][nodesPosition.get(shortestPath.get(i + 1))]); // add up the nodes values and the sum value
        }

        // Save all visitedNodes in the resultAllNodes StringBuilder
        StringBuilder resultAllNodes = new StringBuilder();
        for (String node : visitedNodes) {
            resultAllNodes.append(node).append(" -> ");
        }

        // Save the shortestPath nodes in the resultShortestPath StringBuilder
        StringBuilder resultShortestPath = new StringBuilder();
        for (String node : shortestPath) {
            resultShortestPath.append(node).append(" -> ");
        }

        // display the results
        System.out.println("Sequent of all nodes: " + resultAllNodes);
        System.out.println("Shortest Path: " + resultShortestPath);
        System.out.println("Shortest Pakth Length: " + sum + "\n");
    }

    public static void main(String[] args) throws FileNotFoundException {

        Map<String, Integer> directDistances = readDirectDistanceFile(); // read direct distances
        String[][] graphData = readGraphData(); // read graph data
        Map<String, Integer> nodesPosition = setNodesPosition(graphData); // store nodes position in a map

        String currentNode = validateUserInput(nodesPosition); // validate and get user input

        String userInput = currentNode; // save current node in a userInput variable. It will be used to reset the current node after algorithm 1

        ArrayList<String> visitedNodes = new ArrayList<>(); // instantiate a visitedNods ArrayList that will hold all the visited nodes
        ArrayList<String> shortestPath = new ArrayList<>(); // instantiate a shortestPath ArrayList that will hold the shortestPath to node Z

        // Algorithm 1
        getNodesPath(currentNode, visitedNodes, nodesPosition, graphData, directDistances, "Algorithm1"); // Get the nodes path to Z
        getShortestPath(visitedNodes, shortestPath); // get the shortest path to Z
        System.out.println("Algorithm 1: ");
        displayDirectDistanceResults(shortestPath, nodesPosition, visitedNodes, graphData); // Display Algorithm 1 results

        visitedNodes.clear(); // clear visitedNodes ArrayList
        shortestPath.clear(); // clear shortestPath ArrayList
        currentNode = userInput; // reset current node to the user's selected node

        // Algorithm 2
        getNodesPath(currentNode, visitedNodes, nodesPosition, graphData, directDistances, "Algorithm2"); // Get the nodes path to Z
        getShortestPath(visitedNodes, shortestPath); // get the shortest path to Z
        System.out.println("Algorithm 2: ");
        displayDirectDistanceResults(shortestPath, nodesPosition, visitedNodes, graphData); // Display Algorithm 2 results
    }


}
