package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    Task addTask(Task task);
    Task addEpic(Epic task);

    Task addSubtask(Subtask task);

    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();

    Task getTask(long id);

    Task getEpic(long id);
    Task getSubtask(long id);
    void updateTask(Task task);
    void updateEpic(Epic task);
    void updateSubtask(Subtask task);
    Task removeTask(long id);

    Task removeEpic(long id);
    Task removeSubtask(long id);
    List<Subtask> getAllEpicSubtasks(long epicId);

    List<Task> getHistory();
}

