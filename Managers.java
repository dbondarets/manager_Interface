package utils;

import service.HistoryManager;
import service.TaskManager;
import service.mem.InMemoryHistoryManager;
import service.mem.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
