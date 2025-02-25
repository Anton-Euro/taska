package com.geml.taska;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Start server in port 8000.
 */
@SpringBootApplication
public class TaskaApplication {

  /**
   * Start server in port 8000.
   */
  public static void main(String[] args) {
    SpringApplication.run(TaskaApplication.class, args);
  }

}
