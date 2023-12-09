package service.mem;

import model.Epic;
import model.Subtask;
import model.Task;
import service.HistoryManager;
import service.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.Status.*;

public class InMemoryTaskManager implements TaskManager {
    private long nextId;
    private final Map<Long, Task> tasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Epic> epics;

    private final HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.nextId = 1L;
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public Task addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task addEpic(Epic task) {
        task.setId(nextId++);
        epics.put(task.getId(), task);
        return task;
    }

    @Override
    public Task addSubtask(Subtask task) {
        Epic epic = epics.get(task.getEpicId());
        if (epic == null) {
            System.out.printf("epic with id = %d not exist", task.getEpicId());
            return null;
        }
        Long id = nextId++;
        task.setId(id);
        subtasks.put(id, task);
        epic.add(id);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(long id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task getEpic(long id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Task getSubtask(long id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (task.getId() == null || tasks.get(task.getId()) == null) {
            System.out.println("Task not found");
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic task) {
        Epic epic = epics.get(task.getId());
        if (epic == null) {
            System.out.println("Epic not found");
            return;
        }
        epic.setDescription(task.getDescription());
        epic.setName(task.getName());
    }

    @Override
    public void updateSubtask(Subtask task) {
        Subtask currentTask =  subtasks.get(task.getId());
        if (currentTask == null) {
            System.out.println("Subtask not found");
            return;
        }
        Epic epic = epics.get(task.getEpicId());
        if (epic == null) {
            System.out.println("Incorrect epic id");
            return;
        }
        subtasks.put(task.getId(), task);
        if (task.getStatus() != currentTask.getStatus()) {
            updateEpicStatusIfNeed(epic);
        }
    }

    private void updateEpicStatusIfNeed(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        for (Long id : epic.getSubtaskIds()) {
            switch (subtasks.get(id).getStatus()) {
                // если хотя бы один в статусе IN_PROGRESS, то задача эпику ставим такой же
                case IN_PROGRESS:
                    epic.setStatus(IN_PROGRESS);
                    break;
                case NEW:
                    countNew++;
                    break;
                case DONE:
                    countDone++;
                    break;
            }
            if (countNew == epic.getSubtaskIds().size()) {
                epic.setStatus(NEW);
            } else if (countDone == epic.getSubtaskIds().size()) {
                epic.setStatus(DONE);
            }
        }
    }

    @Override
    public Task removeTask(long id) {
        return tasks.remove(id);
    }

    @Override
    public Task removeEpic(long id) {
        Epic epic = epics.get(id);
        if (epic  == null) {
            System.out.printf("epic with id = %d not found", id);
            return null;
        }
        for (Long subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        return epics.remove(id);
    }

    @Override
    public Task removeSubtask(long id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            System.out.printf("subtask with id = %d not found", id);
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.remove(id);
        //могли удалить задачу, которая влияет на статус, поэтому вызываем метод, чтобы сменить статус, если потребуется
        updateEpicStatusIfNeed(epic);
        return subtasks.remove(id);
    }

    @Override
    public List<Subtask> getAllEpicSubtasks(long epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.printf("epic with id == %d not found", epicId);
            return null;
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Long id : epic.getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


}
