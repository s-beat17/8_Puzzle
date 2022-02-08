import java.util.HashMap;
import java.util.Stack;
import java.util.PriorityQueue;
import java.math.BigInteger;

public class Node implements Comparable<Node> {

    // направления перемещения состояния сохраняются
    // для проверки осуществимости предоставленного перемещения
    String dir[] = { "up", "down", "right", "left" };
    public int dimension;
    // родительское состояние сохраняется для отслеживания решения,
    // когда целевое состояние найдено
    public Node parent;
    public int[][] matrix;
    public int level;
    // A* heuristic
    public int astar;

    // конструктор для начального состояния головоломки,
    // где size-это размеры головоломки
    public Node(int size) {
        this.dimension = size;
        matrix = new int[size][size];
        parent = null;
        Node p = null;
        level = 0;
        astar = heuristic() + level;
    }

    // конструктор для создания состояния из методов
    public Node(int[][] m, Node p) {
        matrix = m;
        parent = p;
        level = p.level + 1;
        astar = heuristic() + level;
    }

    // Метод исследования DFS, DLS для создания преемников
    public void explore(HashMap<BigInteger, Integer> visited, Stack<Node> openList, int limit) {

        // если заданный предельный параметр равен -1,
        // то исследуйте с помощью DFS, если нет,
        // то исследуйте с помощью DLS
        if (limit != -1)
            if (this.level >= limit)
                return;

        // цикл для проверки возможных перемещений текущего состояния
        // и последующего добавления их в открытый список
        for (int i = 0; i < 4; i++) {
            int[][] temp = copy();
            // boolean флаг для проверки того, что сгенерированное возможное состояние
            // уже было сгенерированно
            boolean already = false;
            if (move(temp, dir[i])) {
                if (visited.containsKey(hash(temp))) {
                    already = true;
                    continue;
                }
                if (!already) {
                    openList.push(new Node(temp, this));
                    visited.put(hash(temp), 0);
                }
            }
        }
    }

    // A* метод для создания преемников
    public void exploreAstar(HashMap<BigInteger, Integer> visited, PriorityQueue<Node> openList) {
        for (int i = 0; i < 4; i++) {
            Node temp = new Node(this.copy(), this);
            boolean worse = false;
            if (move(temp.matrix, dir[i])) {
                if (visited.containsKey(hash(temp.matrix)))
                    if (visited.get(hash(temp.matrix)) < temp.astar) {
                        worse = true;
                        continue;
                    }
                if (!worse) {
                    visited.put(hash(temp.matrix), temp.astar);
                    openList.offer(temp);
                }
            }
        }
    }

    // вычисляет эвристику Манхэттена для данной головоломки
    public int heuristic() {
        int c = 1;
        int h = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (c == matrix.length * matrix.length)
                    continue;
                int x = find(c)[0];
                int y = find(c)[1];
                h += Math.abs(x - i) + Math.abs(y - j);
                c++;
            }
        }
        return h;
    }

    // метод print выводит текущее состояние головоломки
    public void print() {
        System.out.println("\n************");
        for (int i = 0; i < matrix.length; i++) {
            System.out.println();
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(" " + matrix[i][j] + " " + "\t");
            }
        }
        System.out.println("\n************");
    }

    // находит координаты заданного числа
    public int[] find(int n) {
        int[] coord = new int[2];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == n) {
                    coord[0] = i;
                    coord[1] = j;
                }
            }
        }
        return coord;
    }

    // этот метод перемещает пустой слот в указанном направлении
    public boolean move(int[][] a, String dir) {
        int zCoord[] = find(0);
        int x = zCoord[0];
        int y = zCoord[1];

        switch (dir) {

            case "up":
                if (x - 1 >= 0) {
                    a[x][y] = a[x - 1][y];
                    a[x - 1][y] = 0;
                    return true;
                }
                break;
            case "down":
                if (x + 1 <= a.length - 1) {
                    a[x][y] = a[x + 1][y];
                    a[x + 1][y] = 0;
                    return true;
                }
                break;
            case "right":
                if (y + 1 <= a.length - 1) {
                    a[x][y] = a[x][y + 1];
                    a[x][y + 1] = 0;
                    return true;
                }
                break;
            case "left":
                if (y - 1 >= 0) {
                    a[x][y] = a[x][y - 1];
                    a[x][y - 1] = 0;
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    // проверяет, решена ли головоломка или нет
    public boolean isComplete() {
        int c = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i == matrix.length - 1 && j == matrix.length - 1)
                    break;
                if (matrix[i][j] != c)
                    return false;
                c++;
            }
        }
        return true;
    }

    // возвращает копию матрицы данной головоломки
    public int[][] copy() {
        int[][] copy = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix.length; j++) {
                copy[i][j] = matrix[i][j];
            }
        return copy;
    }

    // возвращает совершенно уникальную хэш - функцию
    // для текущего состояния головоломки
    public BigInteger hash(int[][] matrix) {
        String text = "";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                text += Integer.toString(matrix[i][j]);
            }
        }
        BigInteger hash = new BigInteger(text);
        return hash;
    }

    // проверяет разрешимость головоломки
    public boolean isSolvable() {
        int linMatrix[] = new int[matrix.length * matrix.length];
        int count = 0;
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix.length; j++) {
                linMatrix[count] = matrix[i][j];
                count++;
            }
        int parity = 0;
        int gridWidth = (int) Math.sqrt(linMatrix.length);
        int row = 0;
        int blankRow = 0;

        for (int i = 0; i < linMatrix.length; i++) {
            if (i % gridWidth == 0) {
                row++;
            }
            if (linMatrix[i] == 0) {
                blankRow = row;
                continue;
            }
            for (int j = i + 1; j < linMatrix.length; j++) {
                if (linMatrix[i] > linMatrix[j] && linMatrix[j] != 0) {
                    parity++;
                }
            }
        }

        if (gridWidth % 2 == 0) {
            if (blankRow % 2 == 0) {
                return parity % 2 == 0;
            } else {
                return parity % 2 != 0;
            }
        } else {
            return parity % 2 == 0;
        }
    }

    @Override
    public int compareTo(Node a) {
        if (this.astar > a.astar)
            return 1;
        else if (a.astar == this.astar)
            return 0;
        else
            return -1;
    }

}
