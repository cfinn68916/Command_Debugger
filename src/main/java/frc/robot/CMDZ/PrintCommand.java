// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.CMDZ;

/**
 * A command that prints a string when initialized.
 *
 * <p>This class is provided by the NewCommands VendorDep
 */
public class PrintCommand extends InstantCommand {
  /**
   * Creates a new a PrintCommand.
   *
   * @param message the message to print
   */
  String message;
  public PrintCommand(String message) {
    super(() -> System.out.println(message));    this.message=message;


  }

  @Override
  public String repr() {
    return "{\"type\":\"Print\",\"data\":\"+"+message+"\"}";
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }
}
