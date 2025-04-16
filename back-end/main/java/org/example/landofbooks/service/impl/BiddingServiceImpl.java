package org.example.landofbooks.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.landofbooks.dto.BidStorageDTO;
import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.entity.*;
import org.example.landofbooks.repo.*;
import org.example.landofbooks.service.BiddingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BiddingServiceImpl implements BiddingService {

    @Autowired
    private BiddingRepo biddingRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BidStorageRepo bidStorageRepo;

    @Autowired
    private BidCartRepo bidCartRepo;

    @Override
    public boolean placeBid(BiddingDTO biddingDTO, MultipartFile image) {
        try {
            Bidding bidding = modelMapper.map(biddingDTO, Bidding.class);

            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image);
                if (imagePath != null) {
                    bidding.setImage(imagePath);
                } else {
                    return false;
                }
            }
            modelMapper.typeMap(BiddingDTO.class, Bidding.class).addMappings(mapper -> {
                mapper.skip(Bidding::setCategory);
                mapper.skip(Bidding::setUser);
            });

            bidding.setCategory(categoryRepo.findById(biddingDTO.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found")));
            bidding.setUser(userRepo.findById(biddingDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));

            biddingRepo.save(bidding);
            return true;
        } catch (Exception e) {
            System.out.println("Error while bidding: " + e.getMessage());
            return false;
        }
    }
    public String saveImage(MultipartFile image) {
        try {
            String fileName = UUID.randomUUID().toString();

            String fileExtension = getFileExtension(image);
            if (fileExtension == null) {
                return null;
            }

            String projectRootPath = System.getProperty("user.dir");

            Path path = Paths.get(projectRootPath, "back-end", "main", "resources", "uploads", "images", fileName + fileExtension);

            Files.createDirectories(path.getParent());
            image.transferTo(path.toFile());

            return path.toString();
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
        return null;
    }

    private String getFileExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType != null) {
            switch (contentType) {
                case "image/jpeg":
                    return ".jpg";
                case "image/png":
                    return ".png";
                case "image/gif":
                    return ".gif";
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public List<BiddingDTO> getBidByStatus(String status) {
        List<Bidding> biddings = biddingRepo.findByStatus(status);
        return biddings.stream()
                .map(bidding -> modelMapper.map(bidding, BiddingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateBidStatus(UUID id, String activeStatus) {
        Bidding bidding = biddingRepo.findById(id).orElseThrow(() -> new RuntimeException("Bid not found"));
        System.out.println("Old Status: " + bidding.getStatus());
        System.out.println("New Status: " + activeStatus);

        bidding.setStatus(activeStatus);
        biddingRepo.save(bidding);

        System.out.println("Updated Status: " + bidding.getStatus());
    }

    // Delete Book by UUID
    @Override
    public void deleteBid(UUID id) {
        if (!biddingRepo.existsById(id)) {
            throw new RuntimeException("Bid not found");
        }
        biddingRepo.deleteById(id);
    }

    @Override
    public List<BiddingDTO> getOngoingBidsByUser(UUID userId) {
        List<Bidding> bids = biddingRepo.findByUserUidAndStatus(userId, "active");

        return bids.stream()
                .map(bid -> modelMapper.map(bid, BiddingDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteStor(UUID bidId) {
        bidStorageRepo.deleteByBidding_BidId(bidId);

        biddingRepo.deleteById(bidId);
    }

    @Transactional
    @Override
    public boolean endBid(UUID bidId) {
        try {
            Optional<BidStorage> highestBid = bidStorageRepo.findTopByBidding_BidIdOrderByMaxPriceDesc(bidId);

            if (highestBid.isPresent()) {
                BidStorage maxBid = highestBid.get();
                Bidding bidding = maxBid.getBidding();
                User highestBidder = maxBid.getUser();

                if (!biddingRepo.existsById(bidId)) {
                    throw new EntityNotFoundException("Bidding not found for ID: " + bidId);
                }

                BidCart cartItem = new BidCart();
                cartItem.setBidding(bidding);
                cartItem.setUser(highestBidder);
                cartItem.setTitle(bidding.getTitle());
                cartItem.setMaxPrice(maxBid.getMaxPrice());
                cartItem.setImage(bidding.getImage());

                bidCartRepo.save(cartItem);

                bidding.setStatus("end");
                biddingRepo.save(bidding);

                bidStorageRepo.deleteByBidding_BidId(bidId);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<BiddingDTO> getActiveBids() {
        List<Bidding> activeBids = biddingRepo.findByStatus("active");

        return activeBids.stream()
                .map(bidding -> modelMapper.map(bidding, BiddingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String placeBids(BiddingDTO biddingDTO) {
        if (biddingDTO.getBidId() == null) {
            return "Bid ID cannot be null!";
        }
        if (biddingDTO.getUserId() == null) {
            return "User ID cannot be null!";
        }

        Optional<Bidding> biddingOptional = biddingRepo.findById(biddingDTO.getBidId());
        Optional<User> userOptional = userRepo.findById(biddingDTO.getUserId());

        if (!biddingOptional.isPresent()) {
            return "Bidding not found!";
        }
        if (!userOptional.isPresent()) {
            return "User not found!";
        }

        Bidding bidding = biddingOptional.get();
        User user = userOptional.get();

        Optional<BidStorage> existingBidStorage = bidStorageRepo.findByBiddingAndUser(bidding, user);

        if (existingBidStorage.isPresent()) {
            BidStorage existingBid = existingBidStorage.get();
            if (biddingDTO.getBidAmount() <= existingBid.getMaxPrice()) {
                return "Your bid must be higher than the existing maximum bid!";
            } else {
                existingBid.setMaxPrice(biddingDTO.getBidAmount());
                bidStorageRepo.save(existingBid);
                return "Bid updated successfully!";
            }
        }

        double initialBidAmount = bidding.getBidAmount();
        if (biddingDTO.getBidAmount() <= initialBidAmount) {
            return "Your bid must be higher than the starting bid amount!";
        }

        BidStorage newBid = new BidStorage();
        newBid.setMaxPrice(biddingDTO.getBidAmount());
        newBid.setBidding(bidding);
        newBid.setUser(user);

        bidStorageRepo.save(newBid);
        return "Bid placed successfully!";
    }

    @Override
    public Double getMaxBid(UUID biddingId) {
        Optional<BidStorage> highestBid = bidStorageRepo.findFirstByBidding_BidIdOrderByMaxPriceDesc(biddingId);

        if (highestBid.isPresent()) {
            System.out.println("Max bid found: " + highestBid.get().getMaxPrice());
            return highestBid.get().getMaxPrice();
        }

        System.out.println("No bids found, returning bidAmount from Bidding table.");
        return biddingRepo.findById(biddingId).map(Bidding::getBidAmount).orElse(0.0);
    }

    @Override
    public Object getPendingBidsByUser(UUID userId) {
        List<Bidding> bids = biddingRepo.findByUserUidAndStatus(userId, "closed");

        return bids.stream()
                .map(bid -> modelMapper.map(bid, BiddingDTO.class))
                .collect(Collectors.toList());
    }

//    private BiddingDTO mapToDTO(Bidding bidding) {
//        return new BiddingDTO(
//                bidding.getBidId(),
//                bidding.getTitle(),
//                bidding.getAuthor(),
//                bidding.getDescription(),
//                bidding.getImage(),
//                bidding.getBidDate(),
//                bidding.getBidAmount(),
//                bidding.getStatus()
//        );
//    }
}
