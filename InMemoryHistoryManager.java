package service.mem;

import model.Task;
import service.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> tasks = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (tasks.size() == 10) {
            tasks.remove(0);
        }
        tasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasks;
    }
}
