package com.springbootjpa.springbootjpa.service;

import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.Member;
import com.springbootjpa.springbootjpa.domain.Order;
import com.springbootjpa.springbootjpa.domain.OrderStatus;
import com.springbootjpa.springbootjpa.domain.item.Book;
import com.springbootjpa.springbootjpa.exception.NotEnoughStockException;
import com.springbootjpa.springbootjpa.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public  class OrderServiceTest {

    @PersistenceContext
    public EntityManager entityManager;
    @Autowired
    public OrderService orderService;
    @Autowired
    public OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {

        Member member = createMember("회원1", new Address("대전", "서구", "123-123"));

        Book book = createBook("JPA1", 10000, 10);
        int orderCount = 2; //2건 주문

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order order = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 OREDER", OrderStatus.ORDER, order.getOrderStatus());


    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        Member member = createMember("회원1", new Address("대전", "서구", "123-123"));

        Book book = createBook("JPA1", 10000, 10);
        int orderCount = 11; //2건 주문

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order order = orderRepository.findOne(orderId);

        fail("재고 수량 부족 예외가 발행해야 한다.");
    }


    @Test
    public void 주문취소() throws Exception {

    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity); // 수량?
        entityManager.persist(book);
        return book;
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        entityManager.persist(member);
        return member;
    }

}