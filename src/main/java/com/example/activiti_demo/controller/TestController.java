package com.example.activiti_demo.controller;

import com.example.activiti_demo.service.ShowActivitiImageService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author hebulin
 * @version 1.0
 * @date
 */
@RestController
@RequestMapping("testcontroller")
@CrossOrigin
@Slf4j
public class TestController {

    /**
     * 运行时服务
     */
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 任务服务
     */
    @Autowired
    private TaskService taskService;

    /**
     * 历史服务
     */
    @Autowired
    private HistoryService historyService;

    /**
     * 资源管理服务
     */
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ShowActivitiImageService showActivitiImageService;


    /**
     * 页面测试 没有用
     *
     * @param a
     * @param b
     * @return
     */
    @RequestMapping("/testReturn")
    public String testReturn(@Param("a") String a, @Param("b") String b) {
        System.out.println(a + "--" + b);
        return a + "--" + b;
    }

    /**
     * 手动部署
     *
     * @return
     */
    public void createDef() {
    }

    @RequestMapping("/queryAllAct")
    public String queryAllAct() {
        StringBuilder string = new StringBuilder();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list();
        for (int i = 0; i < list.size(); i++) {
            ProcessDefinition processDefinition = list.get(i);
            string.append("流程定义id：" + processDefinition.getKey() + "；\n");
            string.append("流程名称：" + processDefinition.getName() + "；\n");
            string.append("流程描述：" + processDefinition.getDescription() + "；\n\n");
        }
        return StringUtils.isEmpty(string.toString()) ? "未查到任何流程模型" : string.toString();
    }

    /**
     * 启动流程
     *
     * @param proDefId
     * @return
     */
    @RequestMapping("/startAct")
    public String startAct(@Param("proDefId") String proDefId) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(proDefId);
        System.out.println(processInstance.getId());
        return "新的审批流程实例启动，流程实例id：" + processInstance.getId();
    }

    /**
     * 查询代办任务
     *
     * @param user
     * @return
     */
    @RequestMapping("taskAgents")
    public String taskAgents(@Param("user") String user) {
//        String user = "zhangsan";
        StringBuilder string = new StringBuilder();
        List<Task> list = taskService.createTaskQuery().taskAssignee(user).list();
        for (Task task : list) {
            System.out.println("任务id：" + task.getId());
            System.out.println("当前审批用户：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());

            String processDefinitionId = task.getProcessDefinitionId();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();

            string.append("流程定义id：" + processDefinition.getKey() + "；\n");
            string.append("流程名称：" + processDefinition.getName() + "；\n");

            string.append("流程实例id：" + task.getProcessInstanceId() + "；\n");

            string.append("任务id：" + task.getId() + "；\n");
            string.append("任务名称：" + task.getName() + "；\n");
            string.append("当前审批用户：" + task.getAssignee() + "；\n\n");
        }
        return StringUtils.isEmpty(string.toString()) ? "未查到待办任务" : string.toString();
    }

    /**
     * 审批流程
     *
     * @param actId
     * @param taskId
     * @param userId
     * @return
     */
    @RequestMapping("/approvalProcess")
    public String approvalProcess(@Param("actId") String actId,
                                  @Param("taskId") String taskId,
                                  @Param("userId") String userId) {
        System.out.println("审批流程：" + taskId);
        // 任务id
//        String actId = "test-activiti0220-id-1001";
//        // 审批人
//        String user = userId;
//        //任务id，有多个流程时传递，单个不需要传，act_ru_task表id
//        String id = taskId;
        //根据流程key,与用户名称查询审批
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(actId)
                .taskAssignee(userId)
                .taskId(taskId)
//                .processInstanceId("")
                .singleResult();
        if (task != null) {
            //审批流程
            taskService.complete(task.getId());
            System.out.println("审批成功!");
            return "审批成功!";
        }
        return "未找到该流程的实例";

    }

    /**
     * 查询已办任务
     *
     * @param user
     * @return
     */
    @RequestMapping("/hasToDoTasks")
    public String hasToDoTasks(@Param("user") String user) {
//        String user = "zhangsan";
        StringBuilder string = new StringBuilder();
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee(user).list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            Date startTime = historicTaskInstance.getStartTime();
            Date endTime = historicTaskInstance.getEndTime();
            ZoneId zoneId = ZoneId.systemDefault();
            System.out.println("开始时间：" + startTime + ",结束时间：" + endTime);
            System.out.println("流程名称：" + historicTaskInstance.getName());

            string.append("流程实例id：" + historicTaskInstance.getProcessInstanceId() + "；\n");
            string.append("开始时间："
                    + (startTime == null ? null : startTime.toInstant().atZone(zoneId).toLocalDateTime())
                    + "；\n");
            string.append("结束时间："
                    + (endTime == null ? null : endTime.toInstant().atZone(zoneId).toLocalDateTime())
                    + "；\n");
            string.append("任务名称：" + historicTaskInstance.getName() + "；\n\n");
        }
        return StringUtils.isEmpty(string.toString()) ? "未查到已办任务" : string.toString();
    }

    /**
     * 查看流程图
     *
     * @param response
     * @param processInstanceId
     */
    @RequestMapping(value = "/image")
    public void image(HttpServletResponse response,
                      @RequestParam String processInstanceId) {
        showActivitiImageService.getActivitiImg1(processInstanceId, response);
    }
}