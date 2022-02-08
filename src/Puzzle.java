import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.PriorityQueue;
import java.math.BigInteger;

public class Puzzle {
    // Стек для хранения шагов к решению, где head  - это начальное состояние
    // а tail - это целевое состояние
    static public Stack<Node> solPath = new Stack<Node>();
    // HashMap для хранения ранее посещенных/исследованных состояний (использование hashmap для
    // O(1) доступ для проверки наличия дубликатов при создании новых состояний)
    static public HashMap<BigInteger, Integer> visited = new HashMap<BigInteger, Integer>();
    // startTime для записи времени начала каждого алгоритма
    static double startTime;
    // endTime для записи времени окончания каждого алгоритма
    static double endTime;

    public static void main(String[] args) throws IOException {
        // Размеры головоломки
        int dimension = 3;

        preWrittenInitialState(dimension);

    }

    public static void preWrittenInitialState(int dimension) throws IOException {
        // Заданное целое число в параметре инициализированного узла GTNode-это размеры
        // головоломки (она не обязательно должна быть 3х3, она может быть любого размера)
        Node initial = new Node(dimension);

        // Начальное состояние
        int matrix[][] = { { 3, 4, 0 }, { 1, 7, 6 }, { 5, 2, 8 } };

        // Присвоение матрицы объекту GTNode
        initial.matrix = matrix;

        // Проверка на разрешимость
        if(!initial.isSolvable())
            System.out.println("Эта головоломка неразрешима, пожалуйста, измените входные данные.");

        // Выберите только один алгоритм
        Astar(initial);
//		DFS(initial);
//		DLS(initial, 5000);

    }

    // A* алгоритм поиска
    public static void Astar(Node initial) throws IOException {
        startTime = System.currentTimeMillis();
        // Инициировать открытый список в качестве приоритетной очереди,
        // сортировать по А* и предлагать его начальное состояние
        PriorityQueue<Node> openList = new PriorityQueue<Node>();
        openList.offer(initial);
        Node state;
        while (!openList.isEmpty()) {
            // опросите head PQ (имеющего самый низкий балл A*), чтобы исследовать, а также
            // добавил его в список посещенных, чтобы избежать дубликатов
            state = openList.poll();
            visited.put(state.hash(state.matrix), state.astar);
            // проверка на полноту
            if (state.isComplete()) {
                // вызываем метод path, который наполняет состояниями для решения
                path(state);
                endTime = System.currentTimeMillis();
                printInfo((endTime - startTime) / 1000.00);
                return;
            }
            // генерировать возможные состояния из текущего состояния
            state.exploreAstar(visited, openList);
        }
    }

    // Depth-First Search
    public static void DFS(Node initial) throws IOException {
        startTime = System.currentTimeMillis();
        // инициировать открытый список в виде стека
        // и поместить в него начальное состояние
        Stack<Node> openList = new Stack<Node>();
        openList.push(initial);
        Node state;
        while (!openList.isEmpty()) {
            // pop следующее состояние для изучения, а также добавил его в список посещенных,
            // чтобы избежать дубликаты
            state = openList.pop();
            visited.put(state.hash(state.matrix), 0);
            // проверка на полноту
            if (state.isComplete()) {
                // вызов метода path, который заполняет стек решений состояниями для решения
                path(state);
                endTime = System.currentTimeMillis();
                printInfo((endTime - startTime) / 1000.00);
                return;
            }
            // генерировать возможные состояния из текущего состояния
            state.explore(visited, openList, -1);
        }
    }

    // Depth-Limited Search algorithm
    public static void DLS(Node initial, int limit) throws IOException {
        startTime = System.currentTimeMillis();
        // инициировать открытый список в виде стека и поместить в него начальное состояние
        Stack<Node> openList = new Stack<Node>();
        openList.push(initial);
        Node state;
        while (!openList.isEmpty()) {
            // pop следующее состояние для изучения, а также добавил его в список посещенных,
            // чтобы избежать дубликатов
            state = openList.pop();
            visited.put(state.hash(state.matrix), 0);
            if (state.isComplete()) {
                // вызов метода path, который заполняет стек решений состояниями
                // для решения
                path(state);
                endTime = System.currentTimeMillis();
                printInfo((endTime - startTime) / 1000.00);
                return;
            }
            // генерируйте возможные состояния из текущего состояния (и помните о пределе)
            state.explore(visited, openList, limit);
        }
        System.out.println("Решение не найдено в пределах указанного предела.");
    }

    // Добавляет путь решения в стек
    public static void path(Node c) {
        while (c.parent != null) {
            solPath.push(c);
            c = c.parent;
        }
        solPath.push(c);
    }

    // Выводим шаги для решения и результаты алгоритма
    public static void printInfo(double time) throws IOException {
        // Размер стека - 1 - это необходимые ходы для поиска решения
        int moves = solPath.size() - 1;

        // Печать ходов поиска решения
        while (!solPath.empty()) {
            Node p = (Node) solPath.pop();
            p.print();
//            System.out.println("Press ENTER key to continue...");
//            System.in.read();
        }

        System.out.println("\n");
        System.out.println("Решение за " + moves + " ходов.");
        System.out.println("Затраченное время " + time + " секунд.");
    }
}
