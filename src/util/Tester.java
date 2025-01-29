package util;

import controller.TaskManager;
import models.Epic;
import models.StatusTask;
import models.Subtask;
import models.Task;

public class Tester {

    public void startTest(){
        System.out.println("Воспроизводим тест...");
        TaskManager manager = new TaskManager();

        System.out.println("Создание и добавление простых задач...");
        Task taskTest1 = new Task("test1", "other", StatusTask.NEW);        //
        Task taskTest2 = new Task("test2", "other another", StatusTask.NEW);
        manager.addTask(taskTest1);
        manager.addTask(taskTest2);

        System.out.println("Создание и добавление EPIC задач...");
        Epic epicTest1 = new Epic("EPIC", "epic task");
        Epic epicTest2 = new Epic("EPIC", "epic task check");
        manager.addEpic(epicTest1);
        manager.addEpic(epicTest2);

        System.out.println("Создание и добавление подзадач эпиков...");
        Subtask subtaskTest1 = new Subtask("sub1", "other another", StatusTask.NEW, epicTest1.getId());
        Subtask subtaskTest2 = new Subtask("sub2", "other another", StatusTask.NEW, epicTest1.getId());
        Subtask subtaskTest3 = new Subtask("sub3", "other another", StatusTask.NEW, epicTest2.getId());
        manager.addSubtask(subtaskTest1);
        manager.addSubtask(subtaskTest2);
        manager.addSubtask(subtaskTest3);

        System.out.println("\nВывод всех списков:");
        System.out.println("список задач: " + manager.getListAllTask());
        System.out.println("список задач EPIC: " + manager.getListAllEpic());
        System.out.println("список задач subEPIC: " + manager.getListAllSubtask());

        System.out.println("\nИзменение статусов задач...");
        taskTest1.setStatus(StatusTask.IN_PROGRESS);
        taskTest2.setStatus(StatusTask.DONE);
        epicTest1.setStatus(StatusTask.DONE);
        epicTest2.setStatus(StatusTask.IN_PROGRESS);
        subtaskTest1.setStatus(StatusTask.IN_PROGRESS);
        subtaskTest3.setStatus(StatusTask.DONE);

        System.out.println("Обновление задач по ID...");
        manager.updateTask(taskTest1);
        manager.updateTask(taskTest2);
        manager.updateEpic(epicTest1);
        manager.updateEpic(epicTest2);
        manager.updateSubtask(subtaskTest1);
        manager.updateSubtask(subtaskTest3);

        System.out.println("\nВывод всех списков после обновления:");
        System.out.println("список задач: " + manager.getListAllTask());
        System.out.println("список задач EPIC: " + manager.getListAllEpic());
        System.out.println("список задач subEPIC: " + manager.getListAllSubtask());

        System.out.println("\nПоиск по id:");
        System.out.println("task: " + manager.getTaskById(taskTest1.getId()));
        System.out.println("EPIC: " + manager.getEpicById(epicTest1.getId()));
        System.out.println("subtask: " + manager.getSubtaskById(subtaskTest2.getId()));

        System.out.println("\nПроверка списка конкретного EPIC");
        System.out.println("список первого эпика" + manager.getAllSubtaskOfEpic(epicTest1));
        System.out.println("список второго эпика" + manager.getAllSubtaskOfEpic(epicTest2));

        System.out.println("\nПроверка удаления по ID");
        System.out.println("Информация о задаче: " + manager.getSubtaskById(subtaskTest3.getId()));
        System.out.println("Удаление подзадачи по ID...");
        manager.removeSubtaskById(subtaskTest3.getId());
        System.out.println("Информация о задаче, должен быть null: " + manager.getSubtaskById(subtaskTest3.getId()));
        System.out.println("Проверка статусов Epic, должен быть NEW: " + manager.getEpicById(subtaskTest3.getEpicId()));

        System.out.println("\nПроверка на удаление всех списков");
        System.out.println("Очистка всех списков...");
        manager.removeAllTask();
        manager.removeAllSubtask();
        manager.removeAllEpic();
        System.out.println("список задач: " + manager.getListAllTask());
        System.out.println("список задач EPIC: " + manager.getListAllEpic());
        System.out.println("список задач subEPIC: " + manager.getListAllSubtask());
    }
}

