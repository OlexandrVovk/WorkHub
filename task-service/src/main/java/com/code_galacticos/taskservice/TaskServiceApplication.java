package com.code_galacticos.taskservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }

}
