package com.springbootjpa.springbootjpa.service;

import com.springbootjpa.springbootjpa.domain.*;
import com.springbootjpa.springbootjpa.repository.ItemRepository;
import com.springbootjpa.springbootjpa.repository.MemberRepository;
import com.springbootjpa.springbootjpa.repository.OrderRepository;
import com.springbootjpa.springbootjpa.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회할 경우 
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    
    /**
     * 상품 주문
     * 
     * @param memberId Long memberId
     * @param itemId Long itemId
     * @param count int count
     * @return Long orderId
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);
        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); // 배송지 정보는 사용자의 주소로 설정
        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(),count);
        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        // 주문 저장
        orderRepository.save(order);
        
        return order.getId();
    }


    /**
     * 상품 주문 취소
     * 
     * @param orderId Long orderId
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    // 검색
    public List<Order> findAll(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }
}
