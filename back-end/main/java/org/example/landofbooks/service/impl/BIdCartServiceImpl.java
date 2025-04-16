package org.example.landofbooks.service.impl;

import org.example.landofbooks.dto.BidCartDTO;
import org.example.landofbooks.entity.BidCart;
import org.example.landofbooks.repo.BidCartRepo;
import org.example.landofbooks.service.BidCartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BIdCartServiceImpl implements BidCartService {

    @Autowired
    private BidCartRepo bidCartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<BidCartDTO> getCartByUser(UUID userId) {
        List<BidCart> cartItems = bidCartRepository.findByUser_uid(userId);
        return cartItems.stream()
                .map(item -> modelMapper.map(item, BidCartDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public int getBidCartItemCount(UUID userId) {
        return bidCartRepository.countByUser_Uid(userId);
    }

    @Override
    public void deleteCartItem(UUID bcid) {
        bidCartRepository.deleteById(bcid);
    }
    @Override
    public void clearCart(UUID userId) {
        bidCartRepository.deleteByUser_uid(userId);
    }


}
