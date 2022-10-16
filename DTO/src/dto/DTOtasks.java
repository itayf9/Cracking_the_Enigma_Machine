package dto;

import agent.AgentTask;
import dto.DTOstatus;
import problem.Problem;

import java.util.List;

public class DTOtasks extends DTOstatus {

    private final List<AgentTask> taskList;

    public DTOtasks(boolean isSucceed, Problem details, List<AgentTask> taskList) {
        super(isSucceed, details);
        this.taskList = taskList;
    }

    public List<AgentTask> getTaskList() {
        return taskList;
    }
}
