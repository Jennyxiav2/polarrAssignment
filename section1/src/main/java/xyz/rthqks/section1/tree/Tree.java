package xyz.rthqks.section1.tree;


import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    public Node<T> root;

    public Tree(T rootData) {
        root = new Node<>();
        root.data = rootData;
        root.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Tree{" +
                "root=" + root +
                '}';
    }

    public static class Node<T> {
        public T data;
        public transient Node parent;
        public List<Node<T>> children;

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    ", children=" + children +
                    '}';
        }
    }
}