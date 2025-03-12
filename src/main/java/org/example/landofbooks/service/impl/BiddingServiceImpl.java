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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
