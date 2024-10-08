package luke.task;

import java.util.ArrayList;
import java.util.List;

import luke.Storage;
import luke.env.Constants;

/**
 * The {@code TaskList} class stores a ArrayList of Tasks (todo, deadline, event).
 * It also has methods that can be used to modify or inspect the state of the Task ArrayList.
 *
 * @see TaskList
 * @see luke.Ui
 */
public class TaskList {
    private ArrayList<Task> taskList = new ArrayList<>();

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public Task getTask(int index) {
        return taskList.get(index);
    }

    public boolean addTask(Task task) {
        if (checkDuplicate(task)) {
            return false;
        }
        taskList.add(task);
        return true;
    }

    /**
     * Removes a task from the task list.
     * @param index index of the task in the task list.
     * @return the deleted task
     */
    public Task removeTask(int index) {
        return taskList.remove(index);
    }

    /**
     * Prints a message to inform the user that the task they had called does not exist.
     * @param taskNumber The index number of the task the user had tried to call.
     */
    public static String taskNotFound(int taskNumber) {
        return "task " + taskNumber + " doesn't exist...try another number!\n";
    }

    /**
     * Returns information on the size of the task list.
     * @return A string message with information on the size of the task list.
     */
    public String listSizeUpdateMessage() {
        int listSize = taskList.size();
        if (listSize == 1) {
            return "your list has " + listSize + " item now.\n";
        } else {
            return "your list has " + listSize + " items now.\n";
        }
    }

    /**
     * Adds a task from user input or save data to the task list.
     * @param command Task type (todo, deadline, event).
     * @param taskDetails Details of the task.
     * @param isMarked True if the task to be added is marked as done.
     * @param isLoadingFromDisk True if the task to be added originated from save data.
     * @throws NoDescriptionException Thrown when args is a blank string.
     * @throws UnknownCommandException Thrown when any command apart from the task types are passed to the function.
     */
    public String addToList(String command, String taskDetails, boolean isMarked, boolean isLoadingFromDisk)
            throws NoDescriptionException, UnknownCommandException {
        if (!(Constants.TASK_TYPES.contains(command))) {
            throw new UnknownCommandException();
        }
        if (taskDetails.isEmpty()) {
            throw new NoDescriptionException();
        }
        switch (command) {
        case "todo" -> {
            return addTodoToList(taskDetails, isMarked, isLoadingFromDisk);
        }
        case "deadline" -> {
            return addDeadlineToList(taskDetails, isMarked, isLoadingFromDisk);
        }
        case "event" -> {
            return addEventToList(taskDetails, isMarked, isLoadingFromDisk);
        }
        default -> {
            throw new UnknownCommandException();
        }
        }
    }

    private String addTodoToList(String taskDetails, boolean isMarked, boolean isLoadingFromDisk) {
        Todo todo = new Todo(taskDetails, isMarked);
        boolean notDuplicate = addTask(todo);
        if (!isLoadingFromDisk) {
            if (notDuplicate) {
                Storage.saveData(taskList);
                return "i've thrown this to-do into your task list:\n"
                        + Constants.INDENT + todo.taskDescription() + "\n"
                        + listSizeUpdateMessage();
            } else {
                return Constants.DUPLICATE_TASK_MESSAGE;
            }
        }
        return "";
    }

    private String addDeadlineToList(String taskDetails, boolean isMarked, boolean isLoadingFromDisk) {
        String[] taskAndDeadline;
        if (isLoadingFromDisk) {
            taskAndDeadline = taskDetails.split(" by ");
        } else {
            taskAndDeadline = taskDetails.split(" /by ");
        }
        String taskName = taskAndDeadline[0];
        String deadline = taskAndDeadline[1];
        Deadline dl = new Deadline(taskName, deadline, isMarked);
        boolean notDuplicate = addTask(dl);
        if (!isLoadingFromDisk && notDuplicate) {
            Storage.saveData(taskList);
            return "the new deadline's been added to your task list:\n"
                    + Constants.INDENT + dl.taskDescription() + "\n"
                    + listSizeUpdateMessage();
        }
        return Constants.DUPLICATE_TASK_MESSAGE;
    }

    private String addEventToList(String taskDetails, boolean isMarked, boolean isLoadingFromDisk) {
        String[] taskAndTimings;
        if (isLoadingFromDisk) {
            taskAndTimings = taskDetails.split(" from | to ");
        } else {
            taskAndTimings = taskDetails.split(" /from | /to ");
        }
        String taskName = taskAndTimings[0];
        String from = taskAndTimings[1];
        String to = taskAndTimings[2];
        Event event = new Event(taskName, from, to, isMarked);
        boolean notDuplicate = addTask(event);
        if (!isLoadingFromDisk && notDuplicate) {
            Storage.saveData(taskList);
            return "aaaaand this event is now in your task list:\n"
                    + Constants.INDENT + event.taskDescription() + "\n"
                    + listSizeUpdateMessage();
        }
        return Constants.DUPLICATE_TASK_MESSAGE;
    }

    /**
     * Filters the task list to include only tasks that contain the specified search term.
     * The search matches substrings within task descriptions.
     * @param searchTerm the term the user is searching for.
     * @return a list of tasks that contain the specified search term within their descriptions.
     */
    public List<Task> findTasks(String searchTerm) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : taskList) {
            if (task.name.contains(searchTerm)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Prints the current list of tasks to the standard output.
     * Each task is displayed with an index number followed by its description.
     */
    public String printList() {
        String list = "";
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            String listing = (i + 1) + ". " + task.taskDescription();
            list = list + listing + "\n";
        }
        return list;
    }

    /**
     * Checks if there is a duplicate task in the task list.
     * Two tasks are considered duplicates if they have the same name and are of the same type.
     *
     * @param newTask The task that will be checked for duplication
     * @return true if the task list contains a duplicate task
     */
    public boolean checkDuplicate(Task newTask) {
        for (Task task : taskList) {
            if (newTask.equals(task)) {
                return true;
            }
        }
        return false;
    }
}
