package org.example.landofbooks.service.impl;

import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.dto.BookDTO;
import org.example.landofbooks.entity.Bidding;
import org.example.landofbooks.entity.Book;
import org.example.landofbooks.entity.Category;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.BiddingRepo;
import org.example.landofbooks.repo.CategoryRepo;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.BiddingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    // Place a new bid
    @Override
    public boolean placeBid(BiddingDTO biddingDTO) {
        try {
            // Convert the BookDTO to Book entity
            Bidding bidding = modelMapper.map(biddingDTO, Bidding.class);

            // If book image is provided as Base64, process and save it
            if (biddingDTO.getImage() != null) {
                String imagePath = saveImage(biddingDTO.getImage()); // Save the image and get the path
                bidding.setImage(imagePath); // Set the image path in the book entity
            }
            Category category = categoryRepo.findById(biddingDTO.getCategoryId()).orElse(null);
            User user = userRepo.findById(biddingDTO.getUserId()).orElse(null);
            bidding.setCategory(category);
            bidding.setUser(user);
            // Save the book in the repository
            biddingRepo.save(bidding);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String saveImage(String base64Image) {
        try {
            // Remove any extra characters (e.g., spaces) that may have been added during transfer
            String sanitizedBase64 = base64Image.replaceAll("[^A-Za-z0-9+/=]", "");

            // Decode the image
            byte[] decodedImage = Base64.getDecoder().decode(sanitizedBase64);

            // Generate a unique file name using UUID
            String fileName = UUID.randomUUID().toString() + ".jpg"; // You can change the extension if needed

            // Define the directory where the image will be saved (adjust path as needed)
            Path path = Paths.get("uploads","images","bid", fileName);

            // Ensure the directory exists
            Files.createDirectories(path.getParent());

            // Save the image to the file system
            Files.write(path, decodedImage);

            // Return the image path (relative or absolute based on your requirement)
            return path.toString();
        } catch (IllegalArgumentException e) {
            System.err.println("Error decoding base64 string: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
        return null; // Return null if there was an error
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
