package com.threeluoxuan.controller;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utils.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Resource
    private IdentityService identityService;

    /**
     * 跳转至登录页面
     * @return
     */
    @RequestMapping(value = "/login")
    public ModelAndView login(){
        ModelAndView view = new ModelAndView("login");
        return view;
    }

    /**
     * 跳转至登出
     * @param session
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        session.removeAttribute("pending_task");

        return "redirect:/login";
    }

    /**
     * 用于拦截器拦截的路由等待5秒并跳转至登录界面
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/wait")
    public String wait_redirect(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("level", "danger");
        redirectAttributes.addFlashAttribute("message", "请先登录");
        return "redirect:login";
    }

    /**
     * 验证用户名和密码
     * @param username 用户的id
     * @param password 用户的密码
     * @param httpSession session
     * @param redirectAttributes 显示闪回消息
     * @return 登录成功则重定向到显示任务，否则重定向到登录界面
     */
    @PostMapping(value = "/validate")
    private String validate(@RequestParam("username") String username, @RequestParam("password") String password
            , HttpSession httpSession, RedirectAttributes redirectAttributes){
        if (identityService.checkPassword(username, password)){
            User user = identityService.createUserQuery().userId(username).singleResult();
            httpSession.setAttribute(UserUtil.USER, user);

            return "redirect:/process-list";
        }
        else {
            redirectAttributes.addFlashAttribute("message", "账号或密码有误，请确认后重新输入");
            redirectAttributes.addFlashAttribute("level", "warning");
            return "redirect:/login";
        }
    }

}
