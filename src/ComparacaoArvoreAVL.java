import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComparacaoArvoreAVL {
    public static void main(String[] args) {
        // Criar uma lista de 100.000 números a partir do arquivo
        List<Integer> numbers = readNumbersFromFile("numbers.txt");

        // Criar árvore AVL
        AVLTree avlTree = new AVLTree();

        // Medir o tempo para inserir na árvore AVL
        long avlInsertStartTime = System.currentTimeMillis();
        for (int num : numbers) {
            avlTree.insert(num);
        }
        long avlInsertEndTime = System.currentTimeMillis();

        // Medir o tempo para imprimir a árvore AVL
        long avlPrintStartTime = System.currentTimeMillis();
        avlTree.print();
        long avlPrintEndTime = System.currentTimeMillis();

        // Imprimir tempos
        System.out.println("Tempo de inserção árvore AVL: " + (avlInsertEndTime - avlInsertStartTime) + " ms");
        System.out.println("Tempo da árvore AVL " + (avlPrintEndTime - avlPrintStartTime) + " ms");

        // Realizar a ordenação da lista usando Bubble Sort
        int[] arrayToSort = numbers.stream().mapToInt(Integer::intValue).toArray();
        long bubbleSortStartTime = System.currentTimeMillis();
        bubbleSort(arrayToSort);
        long bubbleSortEndTime = System.currentTimeMillis();

        // Imprimir tempo para ordenação usando Bubble Sort
        System.out.println("Tempo Bubble Sort : " + (bubbleSortEndTime - bubbleSortStartTime) + " ms");

        // Escrever a lista ordenada em um arquivo
        writeNumbersToFile("output_sorted.txt", arrayToSort);
    }

    // Função para ler números a partir de um arquivo
    private static List<Integer> readNumbersFromFile(String fileName) {
        List<Integer> numbers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Remover espaços e converter para inteiros
                Arrays.stream(line.split(","))
                      .map(String::trim)
                      .filter(s -> !s.isEmpty())
                      .map(Integer::parseInt)
                      .forEach(numbers::add);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return numbers;
    }

    // Função para escrever números em um arquivo
    private static void writeNumbersToFile(String fileName, int[] numbers) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < numbers.length; i++) {
                writer.write(String.valueOf(numbers[i]));
                if (i < numbers.length - 1) {
                    writer.write(", ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Implementação básica de uma árvore AVL
    static class AVLTree {
        private Node root;

        private class Node {
            int key;
            Node left, right;
            int height;

            Node(int key) {
                this.key = key;
                this.height = 1;
            }
        }

        // Função para calcular a altura de um nó
        private int height(Node node) {
            return (node != null) ? node.height : 0;
        }

        // Função para obter o fator de balanceamento de um nó
        private int getBalance(Node node) {
            return (node != null) ? height(node.left) - height(node.right) : 0;
        }

        // Função para rotacionar à direita em torno de um nó
        private Node rightRotate(Node y) {
            Node x = y.left;
            Node T2 = x.right;

            // Realizar a rotação
            x.right = y;
            y.left = T2;

            // Atualizar alturas
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            x.height = Math.max(height(x.left), height(x.right)) + 1;

            // Retornar nova raiz
            return x;
        }

        // Função para rotacionar à esquerda em torno de um nó
        private Node leftRotate(Node x) {
            Node y = x.right;
            Node T2 = y.left;

            // Realizar a rotação
            y.left = x;
            x.right = T2;

            // Atualizar alturas
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            y.height = Math.max(height(y.left), height(y.right)) + 1;

            // Retornar nova raiz
            return y;
        }

        // Função para inserir um nó na árvore AVL
        public void insert(int key) {
            root = insert(root, key);
        }

        private Node insert(Node node, int key) {
            // Passo 1: Realizar a inserção normal de BST
            if (node == null) {
                return new Node(key);
            }

            if (key < node.key) {
                node.left = insert(node.left, key);
            } else if (key > node.key) {
                node.right = insert(node.right, key);
            } else {
                // Chaves iguais não são permitidas em árvores BST
                return node;
            }

            // Passo 2: Atualizar a altura do nó atual
            node.height = 1 + Math.max(height(node.left), height(node.right));

            // Passo 3: Obter o fator de balanceamento deste nó para verificar se ele se tornou desequilibrado
            int balance = getBalance(node);

            // Se o nó se tornar desequilibrado, existem quatro casos

            // Caso da rotação à direita
            if (balance > 1 && key < node.left.key) {
                return rightRotate(node);
            }

            // Caso da rotação à esquerda
            if (balance < -1 && key > node.right.key) {
                return leftRotate(node);
            }

            // Caso da rotação à esquerda-direita
            if (balance > 1 && key > node.left.key) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }

            // Caso da rotação à direita-esquerda
            if (balance < -1 && key < node.right.key) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            // Nó não desequilibrado, retornar o próprio nó
            return node;
        }

        // Função para imprimir a árvore AVL em ordem
        public void print() {
            print(root);
        }

        private void print(Node node) {
            if (node != null) {
                print(node.left);
                System.out.print(node.key + " ");
                print(node.right);
            }
        }
    }

    // Função de ordenação de bolha (Bubble Sort)
    private static void bubbleSort(int[] array) {
        int n = array.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Trocar array[j] e array[j+1]
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }

            // Se nenhum swap ocorreu, a matriz está ordenada
            if (!swapped) {
                break;
            }
        }
    }
}
