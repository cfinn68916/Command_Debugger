// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.CMDZ;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A composition that runs a set of commands in parallel, ending when any one of the commands ends
 * and interrupting all the others.
 *
 * <p>The rules for command compositions apply: command instances that are passed to it cannot be
 * added to any other composition or scheduled individually, and the composition requires all
 * subsystems its components require.
 *
 * <p>This class is provided by the NewCommands VendorDep
 */
public class ParallelRaceGroup extends Command {
  // LinkedHashSet guarantees we iterate over commands in the order they were added
  private final Set<Command> m_commands = new LinkedHashSet<>();
  private boolean m_runWhenDisabled = true;
  private boolean m_finished = true;
  private InterruptionBehavior m_interruptBehavior = InterruptionBehavior.kCancelIncoming;

  @Override
  public String repr() {
    StringBuilder ret= new StringBuilder("[");
    for (Command command : m_commands) {
      ret.append(command.repr()).append(",");
    }
    ret.append("]");
    return "{\"type\":\"Race\", \"subcommands\":"+ ret +"}";
  }
  @Override
  public String status() {
    StringBuilder ret= new StringBuilder("[");
    for (Command command : m_commands) {
      ret.append(command.status()).append(",");
    }
    ret.append("]");

    return "{\"substatus\":"+ret+"}";
  }

  /**
   * Creates a new ParallelCommandRace. The given commands will be executed simultaneously, and will
   * "race to the finish" - the first command to finish ends the entire command, with all other
   * commands being interrupted.
   *
   * @param commands the commands to include in this composition.
   */
  @SuppressWarnings("this-escape")
  public ParallelRaceGroup(Command... commands) {
    addCommands(commands);
  }

  /**
   * Adds the given commands to the group.
   *
   * @param commands Commands to add to the group.
   */
  @SuppressWarnings("PMD.UseArraysAsList")
  public final void addCommands(Command... commands) {
    if (!m_finished) {
      throw new IllegalStateException(
          "Commands cannot be added to a composition while it's running!");
    }

    CommandScheduler.getInstance().registerComposedCommands(commands);

    for (Command command : commands) {
      if (!Collections.disjoint(command.getRequirements(), getRequirements())) {
        throw new IllegalArgumentException(
            "Multiple commands in a parallel composition cannot require the same subsystems");
      }
      m_commands.add(command);
      addRequirements(command.getRequirements());
      m_runWhenDisabled &= command.runsWhenDisabled();
      if (command.getInterruptionBehavior() == InterruptionBehavior.kCancelSelf) {
        m_interruptBehavior = InterruptionBehavior.kCancelSelf;
      }
    }
  }

  @Override
  public final void initialize() {
    m_finished = false;
    for (Command command : m_commands) {
      command.initialize();
    }
  }

  @Override
  public final void execute() {
    for (Command command : m_commands) {
      command.execute();
      if (command.isFinished()) {
        m_finished = true;
      }
    }
  }

  @Override
  public final void end(boolean interrupted) {
    for (Command command : m_commands) {
      command.end(!command.isFinished());
    }
  }

  @Override
  public final boolean isFinished() {
    return m_finished;
  }

  @Override
  public boolean runsWhenDisabled() {
    return m_runWhenDisabled;
  }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return m_interruptBehavior;
  }
}
