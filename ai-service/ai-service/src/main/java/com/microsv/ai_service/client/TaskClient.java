package com.microsv.ai_service.client;

import com.microsv.ai_service.config.FeignConfig;
import com.microsv.ai_service.dto.response.TaskResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "task-service")
public interface TaskClient {

    //lấy tất cả task thuộc user
    @GetMapping(value = "/internal/tasks")
    List<TaskResponse> getUserTasks(@RequestHeader("userId") Long userId);
}
