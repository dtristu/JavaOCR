package org.dtristu.javaocr.usersecurity.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.usersecurity.dto.DeleteAccountDTO;
import org.dtristu.javaocr.usersecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminWebController {
    @Autowired
    UserService userService;
    protected static final Logger logger = LogManager.getLogger(AdminWebController.class);
    @GetMapping("/web/admin")
    public String getUsers( Model model, Pageable pageable) {
        Page<DeleteAccountDTO> usersPage = userService.getAccountsDTO(pageable);  // 5 users per page
        model.addAttribute("usersPage", usersPage);
        return "adminPage";
    }

    @PostMapping("/web/admin/users/delete")
    public String deleteUser(@RequestParam("id") String id) {
        userService.deleteUserById(id);
        return "redirect:/admin/users";
    }
}
