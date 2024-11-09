package br.com.brunoluz.todolist.task;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.brunoluz.todolist.utils.utils;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @GetMapping(path = "/list")
  public ResponseEntity<Object> listTasks(HttpServletRequest request) {
    UUID userId = (UUID) request.getAttribute("user_id");
    var tasks = taskRepository.findByIdUser(userId);

    if (tasks.size() == 0) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok().body(tasks);
  }

  @PostMapping(path = "/create")
  public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    try {
      UUID userId = (UUID) request.getAttribute("user_id");
      taskModel.setIdUser(userId);

      var currentDate = LocalDateTime.now();
      if (currentDate.isAfter(taskModel.getStartAt())) {
        throw new BadRequestException("A data de inicio deve ser superior a data atual");
      }

      LocalDateTime startAt = taskModel.getStartAt();
      LocalDateTime endAt = taskModel.getEndAt();
      if (endAt.isBefore((startAt))) {
        throw new BadRequestException("A data fim deve ser superior a data inicio");
      }

      var task = this.taskRepository.save(taskModel);
      return ResponseEntity.ok().body(task);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e);
    }
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> update(@RequestBody TaskModel taskModel, @PathVariable UUID id,
      HttpServletRequest request) {

    UUID userId = (UUID) request.getAttribute("user_id");

    var task = this.taskRepository.findById(id).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    if (!task.getIdUser().equals(userId)) {
      return ResponseEntity.status(403).build();
    }

    utils.copyNonNullProperties(taskModel, task);

    var taskUpdated = this.taskRepository.save(task);

    return ResponseEntity.ok().body(taskUpdated);
  }
}
