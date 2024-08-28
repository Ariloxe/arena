package fr.ariloxe.arena.tasks;

import fr.ariloxe.arena.ArenaAPI;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ariloxe
 */
public class GlobalExecutor {

    private final List<ExecutorTask> executorTasks = new ArrayList<>();

    public void addTask(ExecutorTask executorTask) {
        executorTasks.add(executorTask);
    }

    public void removeTask(ExecutorTask executorTask) {
        executorTasks.remove(executorTask);
    }

    public GlobalExecutor(){
        Bukkit.getScheduler().runTaskTimer(ArenaAPI.getInstance(), ()->{
            for(ExecutorTask executorTask : executorTasks)
                executorTask.setCurrentDuration(executorTask.getCurrentDuration() + 1);


            for(ExecutorTask executorTask : new ArrayList<>(executorTasks)){
                if(executorTask.getCurrentDuration() >= executorTask.getDuration()){
                    if(executorTask.isRepeatable())
                        executorTask.setCurrentDuration(0);
                    else
                        executorTasks.remove(executorTask);

                    executorTask.getRunnable().run();
                }
            }
        }, 0, 20);
    }
}
