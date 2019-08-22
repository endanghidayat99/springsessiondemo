package com.endang.springsessiondemo.controllers;

import com.endang.springsessiondemo.models.ProductCart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
public class SessionController {

    @GetMapping("/")
    public String hello(HttpSession session){
        session.setAttribute("data",123);
        session.setAttribute("email","endang@gmail.com");
        Random r = new Random();
        int index = r.nextInt((100-10)+1)+10;
        session.setAttribute("username","endang"+index);
        return "index";
    }
    @GetMapping("/show")
    public String show(HttpSession session, Model model){
        model.addAttribute("data",session.getAttribute("data"));
        model.addAttribute("email",session.getAttribute("email"));
        model.addAttribute("username",session.getAttribute("username"));

        return "viewSession";
    }

    @GetMapping("/cart")
    public String pesan(HttpSession session,Model model){
        ProductCart productCart= new ProductCart();
        if (session.getAttribute("cart_session")!=null){
            Map<Integer,ProductCart> listCart = (Map<Integer, ProductCart>) session.getAttribute("cart_session");
            productCart.setId(listCart.size()+1);
        }else{
            productCart.setId(1);
        }
        model.addAttribute("productCart",productCart);
        model.addAttribute("ishidden",true);
        return "addCart";
    }

    @PostMapping("/addCart")
    public String doAddCart(@Valid @ModelAttribute ProductCart productCart, HttpSession session, Model model){
        Map<Integer,ProductCart> productCartList;
        if (session.getAttribute("cart_session")!=null){
            productCartList = (Map<Integer, ProductCart>) session.getAttribute("cart_session");
            productCartList.put(productCart.getId(),productCart);
        }else{
            productCartList = new HashMap<>();
            productCartList.put(productCart.getId(),productCart);
        }
        session.setAttribute("cart_session",productCartList);
        ProductCart product= new ProductCart();
        product.setId(productCartList.size()+1);
        model.addAttribute("productCart",product);
        model.addAttribute("ishidden",false);
        return "addCart";
    }

    @GetMapping("/showCart")
    public String viewCart(HttpSession session,Model model){
        if (session.getAttribute("cart_session")!=null){
            Map<Integer,ProductCart> listProductCart = (Map<Integer, ProductCart>) session.getAttribute("cart_session");
            model.addAttribute("carts",listProductCart);
        }else{
            model.addAttribute("carts",new HashMap<>());
        }

        return "showCart";
    }

    @RequestMapping(value="/action",params="delete",method= RequestMethod.POST)
    public String deleteUserbyId(Model model,final HttpServletRequest req,HttpSession session){
        final Integer rowId = Integer.valueOf(req.getParameter("delete"));
        Map<Integer,ProductCart> map = (Map<Integer, ProductCart>) session.getAttribute("cart_session");
        map.remove(rowId);
        session.setAttribute("cart_session",map);
        model.addAttribute("carts",map);
        return "showCart";
    }

    @RequestMapping(value="/action",params="edit",method= RequestMethod.POST)
    public String editUserById(HttpServletRequest req, Model model,HttpSession session) {
        final Integer id = Integer.valueOf(req.getParameter("edit"));
        Map<Integer,ProductCart> map = (Map<Integer, ProductCart>) session.getAttribute("cart_session");
        model.addAttribute("productCart",(ProductCart)map.get(id));
        model.addAttribute("ishidden",true);
        return "addCart";
    }

}
