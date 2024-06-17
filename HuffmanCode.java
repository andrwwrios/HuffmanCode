// andrew rios 
// ta : Shreya Nambi
// 05/31/2024
// P3: Huffman

/*
 * This class assists a HuffmanCompressor by creating Huffman code from a given list of frequencies
 * and writes out Huffman code to a .code file
 * This class also helps with decrompression by reading inside a .code file and 
 * writes out to a .new file
 */

 import java.util.*;
 import java.io.*;
 
 public class HuffmanCode {
     private Queue<HuffmanNode> priorityQueue;
 
     /*
      * initializes a new HuffmanCode object with the given array of frequencies 
      * where the element at a given index of the array represents the count of the character
      * with ASCII value index
      */
     public HuffmanCode(int[] frequencies) {
         priorityQueue = createPriorityQueue(frequencies);
         combineNodes();
     }
 
     /*
      * initializes a new HuffmanCode object by reading in previously 
      * constructed code from a .code file that contains pairs of lines where the first line
      * is ASCII code of a character and the second line is the huffman encoding for that character
      */
     // takes in a Scanner that is used to read input from a file
     public HuffmanCode(Scanner input) {
         priorityQueue = new PriorityQueue<>();
         HuffmanNode overallRoot = createHuffmanTree(input);
         priorityQueue.add(overallRoot);
     }
 
     /*
      * develops and returns a Priority queue of Huffman nodes based on the given
      * array of frequencies
      * nodes with a lower frequency have a higher priority
      * nodes with no frequencies are not considered
      */
     private Queue<HuffmanNode> createPriorityQueue(int[] frequencies) {
         Queue<HuffmanNode> priorityQueue = new PriorityQueue<>();
         for(int i = 0; i<frequencies.length; i++) {
             if(frequencies[i] > 0) {
                 priorityQueue.add(new HuffmanNode(frequencies[i], (char)i));
             }
         }
         return priorityQueue;
     }
 
     // combines the nodes within the priority queue into a binary tree until one node is left
     // which represents the overall root of the tree that will be used to create the Huffman Code
     private void combineNodes() {
         while(priorityQueue.size() != 1) {
             HuffmanNode left = priorityQueue.remove();
             HuffmanNode right = priorityQueue.remove();
             HuffmanNode newNode = new HuffmanNode(left.freqData+right.freqData, ' ', left, right);
             priorityQueue.add(newNode);
         }
     }
 
     /*
      * with a given Scanner to read input from a file, creates and returns a Huffman Binary Tree
      * that contains elements from a file where the .code file contains pairs of lines where the
      * first line is the ASCII code of a character and the second line is the huffman encoding 
      * for that character
      */
      // takes in a Scanner to read contents from the file
     private HuffmanNode createHuffmanTree(Scanner input) {
         HuffmanNode overallRoot = new HuffmanNode(0, ' ');
         while(input.hasNextLine()) {
             char character = (char)Integer.parseInt(input.nextLine());
             String code = input.nextLine();
             addToTree(overallRoot, character, code);
         }
         return overallRoot;
     }
 
     /*
      * With the given Huffman node, character, and String, modifies the Huffman tree to contain
      * the given character as a leaf node
      */
     // takes in a String code that determines where in the tree the character needs to go
     // takes in a HuffmanNode to keep references the same when nodes are added
     // takes in a char that is the new element added to a new node
     private void addToTree(HuffmanNode curr, char character, String code) {
         for(int i = 0; i<code.length(); i++) {
             if(code.charAt(i) == '0') {
                 if(i == code.length() - 1) {
                     curr.left = new HuffmanNode(1, character);
                 } else if(curr.left == null) {
                     curr.left = new HuffmanNode(0, ' ');
                 } 
                 curr = curr.left;
             } else {
                 if(i == code.length() - 1) {
                     curr.right = new HuffmanNode(1, character);
                 } else if(curr.right == null) {
                     curr.right = new HuffmanNode(0, ' ');
                 }
                 curr = curr.right;
             }
         }
     }
 
     // saves the current Huffman code to the given output printstream
     // contents are saved in the format described below
     /*
      * inputs the huffman code in pairs of lines where the first line is ASCII code of a character
      * and the second line is the huffman encoding for that character
      */
      // takes in a printatream to output the huffman code to
     public void save(PrintStream output) {
         save(output, priorityQueue.peek(), "");
     }
 
     // private helper method that saves the current Huffman code and keeps references unchanged
     // takes in a PrintStream which is where the Huffman code gets added to
     // takes in a Huffman Node to keep references
     // takes in a String code to help format the output correctly into a file
     private void save(PrintStream output, HuffmanNode curr, String code) {
         if(curr.left == null && curr.right == null) {
             output.println((int)curr.charData);
             output.println(code);
         } else {
             save(output, curr.left, code + 0);
             save(output, curr.right, code + 1);
         }
     }
 
     /*
      * reads individual bits from the input stream and writes the corresponding characters from
      * the huffman code to the given output
      * takes in a BitInputStream to get bits from which determine what node data to grab
      * takes in a PrintStream to output the node data to
      */
     // once a leaf node is hit, the character data of that node is wrote to the output
     public void translate(BitInputStream input, PrintStream output) {
         HuffmanNode curr = priorityQueue.peek();
         while(input.hasNextBit()) {
             int bit = input.nextBit();
             if(bit == 0) {
                 curr = curr.left;
             } else {
                 curr = curr.right;
             }
             if(curr.left == null && curr.right == null) {
                 output.write(curr.charData);
                 curr = priorityQueue.peek();
             }
         }
     }
 
     /*
      * this class represents a node of a huffman tree
      * chars and ints are kept as data where the int represents the frequency of the corresponding
      * character
      */
     // implements the comparable interface
     private static class HuffmanNode implements Comparable<HuffmanNode> {
         public final int freqData;
         public final char charData;
         public HuffmanNode left;
         public HuffmanNode right;
 
         // Constructs a leaf node with the given data.
         public HuffmanNode(int freqData, char charData) {
             this(freqData, charData, null, null);
         }
 
         // Constructs a leaf or branch node with the given data and links.
         public HuffmanNode(int freqData, char charData, HuffmanNode left, HuffmanNode right) {
             this.freqData = freqData;
             this.charData = charData;
             this.left = left;
             this.right = right;
         }
 
         // compares the current huffman node object with another huffman node object based
         // on their frequency
         // nodes with the lower frequency have a higher priority
         // returns an int where a positive number gives the other object priority, a negative
         // number gives this object a priority, and 0 means the objects are equal
         @Override
         public int compareTo(HuffmanCode.HuffmanNode o) {
             return this.freqData - o.freqData;
         }
     }
 }