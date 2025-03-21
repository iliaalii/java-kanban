import util.Tester;

public class Main {

    public static void main(String[] args) {
        //Запуск тестовой проверки
        Tester tester = new Tester();
//      запущен новый тест с использованием тестового файла
//      старый запускается с помощью "tester.startTestInMemoryManager()"
        tester.startTestFileBackedManager();
    }
}