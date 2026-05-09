package com.example.medical_online_store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/reviews") // URL eka: http://localhost:8080/api/reviews
public class ReviewController {
        @Autowired
        private ReviewManager manager;

        // GET: All reviews
        @GetMapping
        public List<Review> getAll() {
            return manager.getAll(); // Direct Java List eka return kalama Spring auto JSON hadanawa
        }

        // POST: Add new review
        @PostMapping
        public String add(@RequestBody Review review) {
            manager.add(review);
            return "Added Successfully";
        }

        // PUT: Update review
        @PutMapping("/{id}")
        public String update(@PathVariable int id, @RequestBody Review review) {
            manager.update(id, review);
            return "Updated Successfully";
        }

        // DELETE: Remove review
        @DeleteMapping("/{id}")
        public String delete(@PathVariable int id) {
            manager.delete(id);
            return "Deleted Successfully";
        }
    }

