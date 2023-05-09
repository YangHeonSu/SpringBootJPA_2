package com.springbootjpa.springbootjpa.service;

import com.springbootjpa.springbootjpa.domain.BookDTO;
import com.springbootjpa.springbootjpa.domain.Item;
import com.springbootjpa.springbootjpa.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 상품 등록
     *
     * @param item Item item
     */
    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void update(BookDTO bookDTO) {
        Item findItem = itemRepository.findOne(bookDTO.getId()); // Entity 변경할 경우에는 merge 보다는 변경감지 사용 추천
        findItem.update(bookDTO);
    }

    /**
     * 전체 상품 조회
     *
     * @return List<Item> items
     */
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     * 상품 조회
     *
     * @param id Long id
     * @return Item item
     */
    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }
}
