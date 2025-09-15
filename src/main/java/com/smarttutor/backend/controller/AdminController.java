package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.User;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.UserRepository;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;

    public AdminController(UserRepository userRepo, BookingRepository bookingRepo, PaymentRepository paymentRepo) {
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
        this.paymentRepo = paymentRepo;
    }

    // ✅ Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // ✅ Delete a user
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return "User deleted successfully!";
    }

    // ✅ Get all bookings
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    // ✅ Get all payments
    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }
}
