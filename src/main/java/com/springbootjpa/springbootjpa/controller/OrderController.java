package com.springbootjpa.springbootjpa.controller;

import com.springbootjpa.springbootjpa.domain.Item;
import com.springbootjpa.springbootjpa.domain.Member;
import com.springbootjpa.springbootjpa.domain.Order;
import com.springbootjpa.springbootjpa.repository.OrderSearch;
import com.springbootjpa.springbootjpa.service.ItemService;
import com.springbootjpa.springbootjpa.service.MemberService;
import com.springbootjpa.springbootjpa.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    /**
     * 상품 주문 페이지
     *
     * @param model Model
     * @return 상품 주문 페이지
     */
    @GetMapping("/order")
    public String saveForm(Model model) {
        List<Member> members = memberService.findAll();
        List<Item> items = itemService.findAll();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    /**
     * 상품 주문
     * @param memberId Long memberId
     * @param itemId Long itemId
     * @param count int count
     * @return
     */
    @PostMapping("/order")
    public String saveForm(@RequestParam("memberId") Long memberId,
                           @RequestParam("itemId") Long itemId,
                           @RequestParam("count") int count) {

        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    /**
     * 주문 목록 조회
     * 
     * @param orderSearch memberName, orderStatus
     * @param model Model
     * @return
     */
    @GetMapping("/orders")
    public String findAll(@ModelAttribute("orderSearch") OrderSearch orderSearch , Model model) {
        List<Order> orders = orderService.findAll(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    /**
     * 주문 취소
     * 
     * @param orderId Long orderId
     * @return
     */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}