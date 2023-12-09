package app;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.HistoryManager;
import service.TaskManager;
import utils.Managers;

import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        HistoryManager historyManager = Managers.getHistoryManager();
        TaskManager taskManager = Managers.getDefault(historyManager);

        //test created tasks
        Task firstTask = new Task("task_1", "task_1_description");
        Task secondTask = new Task("task_2", "task_2_description");

        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);

        assertNotNull(firstTask.getId());
        assertNotNull(secondTask.getId());


        //test created epic with 2 subtasks
        Epic firstEpic = new Epic("epic_1", "epic_1_description");
        taskManager.addEpic(firstEpic);
        assertNotNull(firstEpic.getId());


        Subtask firstSubtask = new Subtask("subtask_1", "subtask_1_description", firstEpic.getId());
        Subtask secondSubtask = new Subtask("subtask_1", "subtask_1_description", firstEpic.getId());

        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(secondSubtask);

        assertNotNull(firstSubtask.getId());
        assertNotNull(secondSubtask.getId());

        assertEquals(2, firstEpic.getSubtaskIds().size());
        assertEquals(Status.NEW, firstEpic.getStatus());

        //test updated subtask and epic status
        Subtask updatedFirstSubtask = new Subtask("subtask_1", "subtask_1_description", firstEpic.getId());
        updatedFirstSubtask.setId(firstSubtask.getId());
        updatedFirstSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(updatedFirstSubtask);

        assertEquals(Status.IN_PROGRESS, firstEpic.getStatus());

        //test created epic with 1 subtask
        Epic secondEpic = new Epic("epic_2", "epic_2_description");
        taskManager.addEpic(secondEpic);
        Subtask thirdSubtask = new Subtask("subtask_3", "subtask_3_description", secondEpic.getId());
        taskManager.addSubtask(thirdSubtask);

        assertEquals(1, secondEpic.getSubtaskIds().size());

        //test updated subtask and epic status
        Subtask updatedThirdSubtask = new Subtask("subtask_3", "subtask_3_description", secondEpic.getId());
        updatedThirdSubtask.setId(thirdSubtask.getId());
        updatedThirdSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(updatedThirdSubtask);

        assertEquals(Status.DONE, secondEpic.getStatus());

        //test getAll
        assertEquals(2, taskManager.getAllTasks().size());
        assertEquals(2, taskManager.getAllEpics().size());
        assertEquals(3, taskManager.getAllSubtasks().size());

        //test remove task
        taskManager.removeTask(firstTask.getId());
        assertEquals(1, taskManager.getAllTasks().size());

        //test remove subtask
        taskManager.removeSubtask(firstSubtask.getId());
        assertEquals(1, taskManager.getAllEpicSubtasks(firstSubtask.getEpicId()).size());
        assertEquals(Status.NEW, taskManager.getEpic(firstEpic.getId()).getStatus());


        taskManager.getTask(2L);
        taskManager.getEpic(3L);
        taskManager.getEpic(6L);
        taskManager.getSubtask(5L);
        taskManager.getSubtask(7L);


        List<Task> history = taskManager.getHistory();
        assertEquals(6, history.size());


        System.out.println("test completed");
    }

    private static void assertNotNull(Long id) {
        if (id == null) {
            throw new RuntimeException("value must be not null");
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new RuntimeException("expected = " + expected + " but actual = " + actual);
        }
    }
}