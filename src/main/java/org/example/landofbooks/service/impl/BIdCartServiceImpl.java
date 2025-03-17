package org.example.landofbooks.service.impl;

import org.example.landofbooks.repo.BidCartRepo;
import org.example.landofbooks.service.BidCartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BIdCartServiceImpl implements BidCartService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BidCartRepo bidCartRepo;



}
