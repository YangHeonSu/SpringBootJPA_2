package com.springbootjpa.springbootjpa.controller;

import com.springbootjpa.springbootjpa.domain.BookDTO;
import com.springbootjpa.springbootjpa.domain.Item;
import com.springbootjpa.springbootjpa.domain.item.Book;
import com.springbootjpa.springbootjpa.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    /**
     * 상품 목록 조회
     * 
     * @param model
     * @return
     */
    @GetMapping("/items")
    public String findAll(Model model) {
        List<Item> items = itemService.findAll();
        model.addAttribute("items", items);

        return "item/itemList";
    }

    /**
     * 상품 등록 페이지
     * 
     * @param model
     * @return
     */
    @GetMapping("/items/new")
    public String saveForm(Model model) {
        model.addAttribute("form", new BookDTO());

        return "item/itemForm";
    }

    /**
     * 상품 등록
     * validation 생략
     * setter 사용 하지말고 dto -> entity 변환해야함 
     * 이번 예제에서는 생략
     * 
     * @param bookDTO
     * @return
     */
    @PostMapping("/items/new")
    public String save(BookDTO bookDTO) {
        Book book = new Book();
        book.setName(bookDTO.getName());
        book.setPrice(bookDTO.getPrice());
        book.setStockQuantity(bookDTO.getStockQuantity());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());

        itemService.save(book);

        return "redirect:/";
    }

    /**
     * 상품 수정 페이지
     *
     * @param itemId Long id 수정할 상품 Pk 값
     * @param model
     * @return
     */
    @GetMapping("/items/{itemId}/edit")
    public String updateForm(@PathVariable Long itemId, Model model) {
        Book book = (Book) itemService.findOne(itemId);  // 예제에서는 상품을 책만 하기때문에 Book으로 강제 변환
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setName(book.getName());
        bookDTO.setPrice(book.getPrice());
        bookDTO.setStockQuantity(book.getStockQuantity());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setIsbn(book.getIsbn());

        model.addAttribute("form" , bookDTO);

        return "item/updateItemForm";
    }

    /**
     * 상품 수정 페이지
     *
     * @param itemId Long id 수정할 상품 Pk 값
     * @param model
     * @return
     */
    @PostMapping("/items/{itemId}/edit")
    public String update(@PathVariable Long itemId,@ModelAttribute("form") BookDTO bookDTO) {

        itemService.update(bookDTO);
        return "redirect:/";
    }
}
