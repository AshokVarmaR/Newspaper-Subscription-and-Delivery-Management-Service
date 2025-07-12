package com.news.controllers;

@Controller
public class HomeController{

  @GetMapping({"/", "/home"})
  public String home(){
    return "home";
  }
}
