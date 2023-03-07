package com.example.activiti_demo.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hebulin
 */
public interface ShowActivitiImageService {
    void getActivitiImg1(String processInstanceId, HttpServletResponse response);
    void getActivitiImg2(String processInstanceId, HttpServletResponse response);
}
