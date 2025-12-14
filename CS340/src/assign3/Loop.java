package assign3;

import java.util.List;
import java.util.ArrayList;

/****************************************************************
*    CLASS:    Loop                                            *
*    DESCRIPTION:    Represents a recorded loop of commands    *
****************************************************************/
public class Loop {
    private String name;
    private List<String> commands;
    
    public Loop(String name, List<String> commands) {
        this.name = name;
        this.commands = new ArrayList<>(commands); // Create a copy to avoid external modification
    }
    
    /****************************************************
    *    METHOD:    getName                            *
    *    DESCRIPTION:    Returns the name of the loop   *
    *    PARAMETERS:    none                            *
    *    RETURN VALUE:    String - the name of the loop *
    ****************************************************/
    public String getName() {
        return name;
    }
    
    /************************************************************
    *    METHOD:    getCommands                                *
    *    DESCRIPTION:    Returns the commands in the loop    *
    *    PARAMETERS:    none                                    *
    *    RETURN VALUE:    List<String> - the list of commands   *
    ************************************************************/
    public List<String> getCommands() {
        return new ArrayList<>(commands); // Return a copy to avoid external modification
    }
    
    /************************************************************
    *    METHOD:    addCommand                                *
    *    DESCRIPTION:    Adds a command to the loop            *
    *    PARAMETERS:    String command - the command to add    *
    *    RETURN VALUE:    none                                *
    ************************************************************/
    public void addCommand(String command) {
        commands.add(command);
    }
    
    /************************************************************
    *    METHOD:    getCommandCount                            *
    *    DESCRIPTION:    Returns the number of commands        *
    *    PARAMETERS:    none                                    *
    *    RETURN VALUE:    int - number of commands            *
    ************************************************************/
    public int getCommandCount() {
        return commands.size();
    }
    
    /************************************************************
    *    METHOD:    toString                                    *
    *    DESCRIPTION:    Returns string representation        *
    *    PARAMETERS:    none                                    *
    *    RETURN VALUE:    String - loop description            *
    ************************************************************/
    @Override
    public String toString() {
        return "Loop: " + name + " (" + commands.size() + " commands)";
    }
}