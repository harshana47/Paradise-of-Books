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

    // Place a new bid
    @Override
    public boolean placeBid(BiddingDTO biddingDTO, MultipartFile image) {
        try {
            // Convert the BookDTO to Book entity
            Bidding bidding = modelMapper.map(biddingDTO, Bidding.class);

            // If book image is provided as Base64, process and save it
            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image); // Save the image and get the relative path
                if (imagePath != null) {
                    bidding.setImage(imagePath); // Set the image path in the book entity
                } else {
                    // If image saving fails, return false
                    return false;
                }
            }
            Category category = categoryRepo.findById(biddingDTO.getCategoryId()).orElse(null);
            User user = userRepo.findById(biddingDTO.getUserId()).orElse(null);
            if (category == null || user == null) {
                return false;
            }
            bidding.setCategory(category);
            bidding.setUser(user);

            biddingRepo.save(bidding);
            return true;
        } catch (Exception e) {
            System.out.println("Error while bidding: " + e.getMessage());
            return false;
        }
    }
    public String saveImage(MultipartFile image) {
        try {
            // Generate a unique file name using UUID
            String fileName = UUID.randomUUID().toString();

            // Get file extension based on the original file content type
            String fileExtension = getFileExtension(image);
            if (fileExtension == null) {
                // Return null if the file type is not supported
                return null;
            }

            // Get the absolute path of the project directory
            String projectRootPath = System.getProperty("user.dir");

            // Define the absolute path where the image will be saved (inside the project)
            Path path = Paths.get(projectRootPath, "uploads", "images", fileName + fileExtension);

            // Ensure the directory exists
            Files.createDirectories(path.getParent());  // Create parent directories if they don't exist

            // Save the image to the file system
            image.transferTo(path.toFile());  // Save the file

            // Return the relative path (you can store this in the database)
            return path.toString();  // Return the file path relative to your project root
        } catch (IOException e) {
            // Log the error (consider using a logging framework)
            System.err.println("Error saving image: " + e.getMessage());
        }
        return null;  // Return null if there was an error
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
                    return null;  // Unsupported file type
            }
        }
        return null;  // Return null if contentType is null
    }

    @Override
    public List<BiddingDTO> getBidByStatus(String status) {
        List<Bidding> biddings = biddingRepo.findByStatus(status);  // Fetch from DB
        return biddings.stream()
                .map(bidding -> modelMapper.map(bidding, BiddingDTO.class))  // Convert Entity to DTO
                .collect(Collectors.toList());
    }

    // Update Book Status (e.g., Change to ACTIVE)
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

        return bids.stream().map(bid -> new BiddingDTO(
                bid.getBidId(),
                bid.getTitle(),
                bid.getAuthor(),
                bid.getImage(),
                bid.getBidDate() != null ? bid.getBidDate().atStartOfDay() : null,
                bid.getBidAmount()
        )).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteStor(UUID bidId) {
        // Delete related bid storage records first
        bidStorageRepo.deleteByBidding_BidId(bidId);

        // Then delete the bid from Bidding table
        biddingRepo.deleteById(bidId);
    }

    @Transactional
    @Override
    public boolean endBid(UUID bidId) {
        try {
            // Get the highest bid for the given bidId
            Optional<BidStorage> highestBid = bidStorageRepo.findTopByBidding_BidIdOrderByMaxPriceDesc(bidId);

            if (highestBid.isPresent()) {
                BidStorage maxBid = highestBid.get();
                Bidding bidding = maxBid.getBidding(); // Fetch the bidding entity
                User highestBidder = maxBid.getUser(); // Get the highest bidder

                // Ensure the bidding entity is persisted before associating it with the bid
                if (!biddingRepo.existsById(bidId)) {
                    throw new EntityNotFoundException("Bidding not found for ID: " + bidId);
                }

                // Create a new BidCart entity (bcid is auto-generated)
                BidCart cartItem = new BidCart();
                cartItem.setBidding(bidding); // Bidding entity is now persisted
                cartItem.setUser(highestBidder);
                cartItem.setTitle(bidding.getTitle());
                cartItem.setMaxPrice(maxBid.getMaxPrice());

                // Save the bid to the cart
                bidCartRepo.save(cartItem);

                // Mark the bid as "closed"
                bidding.setStatus("closed");
                biddingRepo.save(bidding);

                // Delete all bid storage records for the given bidding
                bidStorageRepo.deleteByBidding_BidId(bidId);

                // Optionally delete the bidding record
                // biddingRepo.deleteById(bidId); // You can skip this if you want to retain the bidding entity

                return true; // Success, bid moved to the cart and closed
            } else {
                // If no highest bid is found for the given bidId, return false
                return false;
            }
        } catch (Exception e) {
            // Log the error and return false
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<BiddingDTO> getActiveBids() {
        List<Bidding> activeBids = biddingRepo.findByStatus("active");

        // Convert entities to DTOs
        return activeBids.stream()
                .map(bidding -> mapToDTO(bidding))
                .collect(Collectors.toList());
    }

    @Override
    public String placeBids(BiddingDTO biddingDTO) {
        // Get the bidding and user entities from the database using their IDs
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

        // Check if there's already a bid for this bidding
        Optional<BidStorage> existingBidStorage = bidStorageRepo.findByBiddingAndUser(bidding, user);

        // If there's an existing bid, compare the maxPrice
        if (existingBidStorage.isPresent()) {
            BidStorage existingBid = existingBidStorage.get();
            if (biddingDTO.getBidAmount() <= existingBid.getMaxPrice()) {
                return "Your bid must be higher than the existing maximum bid!";
            } else {
                // Update the existing bid's price
                existingBid.setMaxPrice(biddingDTO.getBidAmount());
                bidStorageRepo.save(existingBid);
                return "Bid updated successfully!";
            }
        }

        // Create a new BidStorage entry if no existing bid is found
        BidStorage newBid = new BidStorage();
        newBid.setMaxPrice(biddingDTO.getBidAmount());
        newBid.setBidding(bidding);  // Set the associated Bidding
        newBid.setUser(user);  // Set the associated User

        // Save the new bid
        bidStorageRepo.save(newBid);
        return "Bid placed successfully!";
    }

    @Override
    public Double getMaxBid(UUID biddingId) {
        if (biddingId == null) {
            return null; // Avoid NullPointerException
        }

        Optional<BidStorage> highestBid = bidStorageRepo.findHighestBidByBidding(biddingId);

        return highestBid.map(BidStorage::getMaxPrice).orElse(0.0); // Return max price or 0 if no bids exist
    }

    private BiddingDTO mapToDTO(Bidding bidding) {
        return new BiddingDTO(
                bidding.getBidId(),
                bidding.getTitle(),
                bidding.getAuthor(),
                bidding.getImage(),
                bidding.getBidDate(),
                bidding.getBidAmount(),
                bidding.getStatus()
        );
    }


    // Fetch all bids for a specific book
//    @Override
//    public List<BiddingDTO> getBidsForBook(Long bookId) {
//        try {
//            // Fetch all bids for the book using the repository
//            List<Bidding> bids = biddingRepo.findByBookId(bookId);
//
//            // Convert the list of Bidding entities to BiddingDTOs using ModelMapper
//            return bids.stream()
//                    .map(bid -> modelMapper.map(bid, BiddingDTO.class))
//                    .toList();
//        } catch (Exception e) {
//            return null; // Return null if any exception occurs
//        }
//    }
//
//    // Fetch the details of a specific bid by bidId
//    @Override
//    public BiddingDTO getBidDetails(Long bidId) {
//        try {
//            // Find the bid by bidId from the repository
//            Optional<Bidding> bidding = biddingRepo.findById(bidId);
//
//            // Return the BiddingDTO if present, otherwise return null
//            return bidding.map(bid -> modelMapper.map(bid, BiddingDTO.class)).orElse(null);
//        } catch (Exception e) {
//            return null; // Return null if any exception occurs
//        }
//    }
//
//    // Update an existing bid
//    @Override
//    public boolean updateBid(Long bidId, BiddingDTO biddingDTO) {
//        try {
//            // Check if the bid exists in the database
//            Optional<Bidding> existingBid = biddingRepo.findById(bidId);
//
//            if (existingBid.isPresent()) {
//                // Map the updated details from BiddingDTO to Bidding entity
//                Bidding bidding = existingBid.get();
//                bidding.setAmount(biddingDTO.getAmount());
//                bidding.setStatus(biddingDTO.getStatus());
//                bidding.setBidderName(biddingDTO.getBidderName());
//
//                // Save the updated bidding entity back to the database
//                biddingRepo.save(bidding);
//
//                return true;
//            } else {
//                return false; // If the bid does not exist, return false
//            }
//        } catch (Exception e) {
//            return false; // Return false if any exception occurs
//        }
//    }
//
//    // Delete a specific bid
//    @Override
//    public boolean deleteBid(Long bidId) {
//        try {
//            // Check if the bid exists in the database
//            Optional<Bidding> existingBid = biddingRepo.findById(bidId);
//
//            if (existingBid.isPresent()) {
//                // Delete the bid from the repository
//                biddingRepo.delete(existingBid.get());
//                return true;
//            } else {
//                return false; // If the bid does not exist, return false
//            }
//        } catch (Exception e) {
//            return false; // Return false if any exception occurs
//        }
//    }
}
